package com.ecocode;

import com.ecocode.core.CodeAnalyzer;
import com.ecocode.models.CarbonReport;
import com.ecocode.ui.ReportFormatter;
import org.fusesource.jansi.AnsiConsole;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Main entry point for EcoCode Analyzer
 *
 * Usage:
 *   java -jar ecocode-analyzer.jar <file-path>
 *   java -jar ecocode-analyzer.jar --interactive
 */
public class Main {

    private static final String[] SPINNER_FRAMES = {"|", "/", "-", "\\"};
    private static volatile boolean spinnerRunning = false;

    public static void main(String[] args) {
        // Force UTF-8 on stdout/stderr
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        } catch (Exception ignored) {}

        AnsiConsole.systemInstall();

        try {
            if (args.length == 0 || "--interactive".equals(args[0]) || "-i".equals(args[0])) {
                runInteractiveMode();
            } else if ("--help".equals(args[0]) || "-h".equals(args[0])) {
                printHelp();
            } else if ("--version".equals(args[0]) || "-v".equals(args[0])) {
                printVersion();
            } else {
                analyzeFile(args[0]);
            }
        } catch (Exception e) {
            System.out.println(ansi().bold().fg(RED).a("  [ERROR] ").reset().fg(RED).a(e.getMessage()).reset());
        } finally {
            AnsiConsole.systemUninstall();
        }
    }

    // -------------------------------------------------------------------------
    // Interactive mode
    // -------------------------------------------------------------------------

    private static void runInteractiveMode() {
        Scanner scanner = new Scanner(System.in);
        CodeAnalyzer analyzer = new CodeAnalyzer();
        ReportFormatter formatter = new ReportFormatter();

        printBanner();

        while (true) {
            printMenu();
            System.out.print(ansi().bold().fg(CYAN).a("  > ").reset());
            String choice = scanner.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1" -> {
                    System.out.print(ansi().fg(CYAN).a("  File path to analyze: ").reset());
                    String filePath = scanner.nextLine().trim();
                    System.out.println();
                    try {
                        CarbonReport report = analyzeWithSpinner(filePath, analyzer);
                        String reportText = formatter.formatCarbonReport(report);
                        System.out.println(reportText);
                        promptSaveReport(scanner, reportText, new File(filePath).getName());
                    } catch (Exception e) {
                        printError(e.getMessage());
                    }
                }
                case "2" -> {
                    System.out.print(ansi().fg(CYAN).a("  Project directory path: ").reset());
                    String projectPath = scanner.nextLine().trim();
                    System.out.println();
                    try {
                        startSpinner("Scanning project");
                        CodeAnalyzer.ProjectReport projectReport = analyzer.analyzeProject(projectPath);
                        stopSpinner();
                        StringBuilder allReports = new StringBuilder();
                        printProjectSummary(projectReport, formatter, allReports);
                        promptSaveReport(scanner, allReports.toString(), "project");
                    } catch (Exception e) {
                        stopSpinner();
                        printError(e.getMessage());
                    }
                }
                case "3" -> runDemoMode(analyzer, scanner);
                case "4" -> printAbout();
                case "5" -> printHelp();
                case "6", "q", "quit", "exit" -> {
                    printGoodbye();
                    return;
                }
                default -> printError("Invalid choice. Please enter 1-6.");
            }

            System.out.println();
            System.out.print(ansi().fg(WHITE).a("  Press ").bold().a("Enter").reset().fg(WHITE).a(" to continue...").reset());
            scanner.nextLine();
            System.out.println();
        }
    }

    // -------------------------------------------------------------------------
    // Demo Mode -- Compare All Sample Algorithms
    // -------------------------------------------------------------------------

    private static void runDemoMode(CodeAnalyzer analyzer, Scanner scanner) {
        System.out.println();
        printDivider('=', GREEN);
        System.out.println(ansi().bold().fg(GREEN)
            .a("  DEMO -- Compare All Sample Algorithms").reset());
        printDivider('=', GREEN);
        System.out.println();

        File sampleDir = findSampleCodeDir();
        if (sampleDir == null) {
            printError("sample-code directory not found. Make sure you are running from the project root.");
            return;
        }

        File[] javaFiles = sampleDir.listFiles(f -> f.isFile() && f.getName().endsWith(".java"));
        if (javaFiles == null || javaFiles.length == 0) {
            printError("No .java files found in sample-code directory: " + sampleDir.getAbsolutePath());
            return;
        }

        // Analyze each sample file
        List<AlgoResult> results = new ArrayList<>();
        System.out.println(ansi().fg(WHITE).a("  Analyzing sample algorithms...").reset());
        System.out.println();

        for (File f : javaFiles) {
            try {
                startSpinner("Analyzing " + f.getName());
                CarbonReport report = analyzer.analyzeFile(f.getAbsolutePath());
                stopSpinner();

                // Dominant complexity = worst (highest) across all functions in the file
                String complexity = report.getFunctionAnalyses().isEmpty() ? "O(?)"
                    : report.getFunctionAnalyses().stream()
                        .map(fa -> fa.getComplexityResult().getTimeComplexity().getNotation())
                        .max(Comparator.comparingInt(Main::complexityOrder))
                        .orElse("O(?)");

                results.add(new AlgoResult(
                    stripExtension(f.getName()),
                    complexity,
                    report.getTotalCarbonEmissions(),
                    complexityRating(complexity)
                ));
            } catch (Exception e) {
                stopSpinner();
                System.err.println("  Warning: Could not analyze " + f.getName() + ": " + e.getMessage());
            }
        }

        if (results.isEmpty()) {
            printError("No algorithms could be analyzed.");
            return;
        }

        // Sort by carbon emissions (ascending)
        results.sort(Comparator.comparingDouble(r -> r.carbonGrams));

        // Print comparison table
        System.out.println();
        printDivider('=', CYAN);
        System.out.println(ansi().bold().fg(CYAN)
            .a("  ALGORITHM COMPARISON -- Carbon Footprint Ranking").reset());
        printDivider('=', CYAN);
        System.out.println();

        System.out.println(ansi().bold().fg(WHITE)
            .a(String.format("  %-5s %-22s %-16s %-14s %-20s",
                "Rank", "Algorithm", "Complexity", "Carbon(gCO2)", "Rating")).reset());
        System.out.println(ansi().fg(244).a("  " + "-".repeat(78)).reset());

        int rank = 1;
        for (AlgoResult r : results) {
            org.fusesource.jansi.Ansi.Color col = r.carbonGrams < 0.001 ? GREEN
                : r.carbonGrams < 1.0 ? CYAN
                : r.carbonGrams < 10.0 ? YELLOW
                : RED;

            System.out.println(ansi().fg(col)
                .a(String.format("  %-5s %-22s %-16s %-14.4f %-20s",
                    rank + ".",
                    truncate(r.name, 21),
                    truncate(r.complexity, 15),
                    r.carbonGrams,
                    r.rating))
                .reset());
            rank++;
        }

        System.out.println();
        printDivider('-', YELLOW);
        System.out.println(ansi().bold().fg(YELLOW).a("  -- VERDICT --").reset());
        printDivider('-', YELLOW);
        System.out.println();

        AlgoResult best  = results.get(0);
        AlgoResult worst = results.get(results.size() - 1);
        double savings = worst.carbonGrams - best.carbonGrams;

        System.out.println(ansi().fg(GREEN).a("  [+] Best algorithm:  ").reset()
            .bold().fg(GREEN).a(best.name).reset()
            .fg(WHITE).a("  (" + best.complexity + ")  -- least carbon").reset());
        System.out.println(ansi().fg(RED).a("  [!] Worst algorithm: ").reset()
            .bold().fg(RED).a(worst.name).reset()
            .fg(WHITE).a("  (" + worst.complexity + ")  -- most carbon").reset());
        System.out.println();
    }

    /** Locate the sample-code directory via multiple strategies */
    private static File findSampleCodeDir() {
        // Strategy 1: relative to user.dir (works when run from project root)
        String userDir = System.getProperty("user.dir");
        File candidate1 = new File(userDir
            + File.separator + "src"
            + File.separator + "main"
            + File.separator + "resources"
            + File.separator + "sample-code");
        if (candidate1.exists() && candidate1.isDirectory()) return candidate1;

        // Strategy 2: relative to the JAR's parent directory
        try {
            File jarFile = new File(Main.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI());
            File candidate2 = new File(jarFile.getParentFile().getParentFile()
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "resources"
                + File.separator + "sample-code");
            if (candidate2.exists() && candidate2.isDirectory()) return candidate2;
        } catch (Exception ignored) {}

        return null;
    }

    /** Assign a sort order to complexity strings for ranking */
    private static int complexityOrder(String notation) {
        return switch (notation) {
            case "O(1)"       -> 0;
            case "O(log n)"   -> 1;
            case "O(n)"       -> 2;
            case "O(n log n)" -> 3;
            case "O(n^2)"     -> 4;
            case "O(n^3)"     -> 5;
            case "O(2^n)"     -> 6;
            case "O(n!)"      -> 7;
            default           -> 8;
        };
    }

    /** Map complexity notation to a human-readable performance rating */
    private static String complexityRating(String notation) {
        return switch (notation) {
            case "O(1)"       -> "Optimal   [5/5]";
            case "O(log n)"   -> "Excellent [5/5]";
            case "O(n)"       -> "Good      [4/5]";
            case "O(n log n)" -> "Good      [4/5]";
            case "O(n^2)"     -> "Poor      [2/5]";
            case "O(n^3)"     -> "Bad       [1/5]";
            case "O(2^n)"     -> "Critical  [0/5]";
            case "O(n!)"      -> "Critical  [0/5]";
            default           -> "Unknown";
        };
    }

    /** Simple holder for demo comparison */
    private static class AlgoResult {
        final String name;
        final String complexity;
        final double carbonGrams;
        final String rating;
        AlgoResult(String name, String complexity, double carbonGrams, String rating) {
            this.name = name; this.complexity = complexity;
            this.carbonGrams = carbonGrams; this.rating = rating;
        }
    }

    // -------------------------------------------------------------------------
    // Save report to file
    // -------------------------------------------------------------------------

    private static void promptSaveReport(Scanner scanner, String reportText, String baseName) {
        System.out.println();
        System.out.print(ansi().fg(CYAN).a("  Save report to file? (y/n): ").reset());
        String ans = scanner.nextLine().trim().toLowerCase();
        if (ans.equals("y") || ans.equals("yes")) {
            String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            String safeBase = baseName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String fileName = "ecocode-report-" + safeBase + "-" + timestamp + ".txt";
            try (FileWriter fw = new FileWriter(fileName, StandardCharsets.UTF_8)) {
                // Strip ANSI escape codes for plain-text file
                fw.write(stripAnsi(reportText));
                System.out.println(ansi().fg(GREEN).a("  [+] Report saved to: ").bold().a(fileName).reset());
            } catch (IOException e) {
                printError("Could not save report: " + e.getMessage());
            }
        }
    }

    /** Remove ANSI escape sequences from a string */
    private static String stripAnsi(String text) {
        // Matches ESC[ ... m and other common ANSI sequences
        return text.replaceAll("\u001B\\[[;\\d]*[A-Za-z]", "");
    }

    // -------------------------------------------------------------------------
    // Progress spinner
    // -------------------------------------------------------------------------

    private static CarbonReport analyzeWithSpinner(String filePath, CodeAnalyzer analyzer) throws Exception {
        startSpinner("Analyzing code");
        try {
            CarbonReport report = analyzer.analyzeFile(filePath);
            stopSpinner();
            System.out.println(ansi().fg(GREEN).a("  [OK] Analysis complete").reset());
            System.out.println();
            return report;
        } catch (Exception e) {
            stopSpinner();
            throw e;
        }
    }

    private static void startSpinner(String label) {
        spinnerRunning = true;
        Thread t = new Thread(() -> {
            int i = 0;
            while (spinnerRunning) {
                String frame = SPINNER_FRAMES[i % SPINNER_FRAMES.length];
                System.out.print(ansi().cursorToColumn(0)
                    .fg(CYAN).a("  [" + frame + "] ").reset()
                    .a(label).a("...").eraseLine());
                System.out.flush();
                i++;
                try { Thread.sleep(120); } catch (InterruptedException e) { break; }
            }
            System.out.print(ansi().cursorToColumn(0).eraseLine());
            System.out.flush();
        });
        t.setDaemon(true);
        t.start();
    }

    private static void stopSpinner() {
        spinnerRunning = false;
        try { Thread.sleep(150); } catch (InterruptedException ignored) {}
    }

    // -------------------------------------------------------------------------
    // Single-file CLI mode
    // -------------------------------------------------------------------------

    private static void analyzeFile(String filePath) {
        printBannerCompact();
        System.out.println(ansi().fg(WHITE).a("  Analyzing: ").bold().a(filePath).reset());
        System.out.println();
        try {
            CodeAnalyzer analyzer = new CodeAnalyzer();
            CarbonReport report = analyzeWithSpinner(filePath, analyzer);
            ReportFormatter formatter = new ReportFormatter();
            System.out.println(formatter.formatCarbonReport(report));
        } catch (Exception e) {
            printError("Error analyzing file: " + e.getMessage());
            System.exit(1);
        }
    }

    // -------------------------------------------------------------------------
    // Project summary
    // -------------------------------------------------------------------------

    private static void printProjectSummary(CodeAnalyzer.ProjectReport pr, ReportFormatter formatter,
                                             StringBuilder allReports) {
        int files = pr.getFileReports().size();
        printDivider('=', GREEN);
        System.out.println(ansi().bold().fg(GREEN).a("  PROJECT ANALYSIS SUMMARY").reset());
        printDivider('=', GREEN);
        System.out.println();
        System.out.println(ansi().fg(CYAN).a("  Path:           ").reset().a(pr.getProjectPath()));
        System.out.println(ansi().fg(CYAN).a("  Files Analyzed: ").reset().bold().a(String.valueOf(files)).reset());
        System.out.println(ansi().fg(CYAN).a("  Total LOC:      ").reset().bold().a(String.valueOf(pr.getTotalLinesOfCode())).reset());
        System.out.println(ansi().bold().fg(GREEN)
            .a(String.format("  Total Carbon:   %.4f gCO2", pr.getTotalCarbonEmissions())).reset());
        System.out.println();
        System.out.println(ansi().fg(YELLOW).a("  -- Individual File Reports --").reset());
        System.out.println();

        allReports.append("PROJECT ANALYSIS SUMMARY\n");
        allReports.append("Path:           ").append(pr.getProjectPath()).append("\n");
        allReports.append("Files Analyzed: ").append(files).append("\n");
        allReports.append("Total LOC:      ").append(pr.getTotalLinesOfCode()).append("\n");
        allReports.append(String.format("Total Carbon:   %.4f gCO2%n", pr.getTotalCarbonEmissions()));
        allReports.append("\n");

        for (CarbonReport fileReport : pr.getFileReports()) {
            String rText = formatter.formatCarbonReport(fileReport);
            System.out.println(rText);
            printDivider('-', WHITE);
            allReports.append(rText).append("\n");
        }
    }

    // -------------------------------------------------------------------------
    // Banner
    // -------------------------------------------------------------------------

    private static void printBanner() {
        System.out.println();
        printDivider('=', GREEN);
        System.out.println(ansi().bold().fg(GREEN)
            .a("                 EcoCode Analyzer  v1.0.0                ").reset());
        System.out.println(ansi().fg(WHITE)
            .a("          Carbon Footprint Analysis for Source Code       ").reset());
        System.out.println(ansi().fg(WHITE)
            .a("          Making Software Environmentally Conscious        ").reset());
        printDivider('=', GREEN);
        System.out.println();
        System.out.println(ansi().fg(244).a("  Created by ").reset()
            .bold().fg(CYAN).a("Alok Sharma").reset()
            .fg(244).a("  --  linkedin.com/in/alok-sharma-b17550321").reset());
        System.out.println();
    }

    private static void printBannerCompact() {
        System.out.println();
        printDivider('=', GREEN);
        System.out.println(ansi().bold().fg(GREEN)
            .a("       EcoCode Analyzer v1.0.0  --  Carbon Analysis       ").reset());
        printDivider('=', GREEN);
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // Menu
    // -------------------------------------------------------------------------

    private static void printMenu() {
        printDivider('-', YELLOW);
        System.out.println(ansi().bold().fg(YELLOW).a("  MAIN MENU").reset());
        printDivider('-', YELLOW);
        printMenuItem("1", "Analyze Single File",        "scan one source file");
        printMenuItem("2", "Analyze Project Directory",  "scan all files in a folder");
        printMenuItem("3", "Demo -- Compare Algorithms", "side-by-side sample algorithm comparison");
        printMenuItem("4", "About",                      "project info & methodology");
        printMenuItem("5", "Help",                       "usage & supported languages");
        printMenuItem("6", "Exit",                       "quit EcoCode Analyzer");
        printDivider('-', YELLOW);
    }

    private static void printMenuItem(String key, String label, String hint) {
        System.out.println(
            ansi().bold().fg(CYAN).a("  [" + key + "]  ").reset()
                .fg(WHITE).a(String.format("%-28s", label)).reset()
                .fg(244).a(hint).reset()
        );
    }

    // -------------------------------------------------------------------------
    // Help
    // -------------------------------------------------------------------------

    private static void printHelp() {
        System.out.println();
        printDivider('=', CYAN);
        System.out.println(ansi().bold().fg(CYAN).a("  HELP & USAGE").reset());
        printDivider('=', CYAN);
        System.out.println();

        printSection("COMMAND LINE");
        System.out.println(ansi().fg(WHITE).a("  java -jar ecocode-analyzer.jar <file-path>").reset());
        System.out.println(ansi().fg(WHITE).a("  java -jar ecocode-analyzer.jar --interactive").reset());
        System.out.println();

        printSection("OPTIONS");
        printHelpRow("--interactive, -i", "Launch interactive mode with menu");
        printHelpRow("--help, -h       ", "Show this help message");
        printHelpRow("--version, -v    ", "Show version information");
        System.out.println();

        printSection("SUPPORTED LANGUAGES");
        printCheckRow("Java (.java)                 -- Full analysis");
        printCheckRow("Python (.py)                 -- Basic analysis");
        printCheckRow("JavaScript/TS (.js/.ts)      -- Basic analysis");
        printCheckRow("C / C++ (.c/.cpp)            -- Basic analysis");
        System.out.println();

        printSection("WHAT IT ANALYZES");
        printBulletRow("Algorithmic complexity  (Big-O notation)");
        printBulletRow("Estimated CPU time & energy consumption");
        printBulletRow("Carbon emissions in gCO2");
        printBulletRow("Optimization suggestions with savings %");
        printBulletRow("Real-world environmental equivalents");
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // Version & About
    // -------------------------------------------------------------------------

    private static void printVersion() {
        System.out.println();
        System.out.println(ansi().bold().fg(GREEN).a("  EcoCode Analyzer  v1.0.0").reset());
        System.out.println(ansi().fg(WHITE).a("  Carbon Footprint Analysis for Source Code").reset());
        System.out.println();
    }

    private static void printAbout() {
        System.out.println();
        printDivider('=', GREEN);
        System.out.println(ansi().bold().fg(GREEN).a("  ABOUT").reset());
        printDivider('=', GREEN);
        System.out.println();

        printSection("PROJECT");
        System.out.println(ansi().fg(WHITE)
            .a("  EcoCode Analyzer detects algorithmic complexity in source\n")
            .a("  code, estimates CPU usage, and converts it to carbon\n")
            .a("  emissions -- empowering developers to write greener code.").reset());
        System.out.println();

        printSection("THE PROBLEM");
        printBulletRow("Software consumes massive electricity via data centers");
        printBulletRow("Inefficient algorithms drive unnecessary carbon emissions");
        printBulletRow("Developers lack real-time environmental feedback");
        System.out.println();

        printSection("THE SOLUTION");
        printCheckRow("Automated complexity detection  (O(n), O(n^2), ...)");
        printCheckRow("Carbon emission calculation using IEEE research model");
        printCheckRow("Actionable optimization suggestions");
        printCheckRow("Real-world impact equivalents (cars, trees, phones)");
        System.out.println();

        printSection("METHODOLOGY");
        System.out.println(ansi().fg(CYAN).a("  Carbon (gCO2) = CPU Time x Power x Carbon Intensity").reset());
        System.out.println();
        printBulletRow("CPU Power:        65W  (average desktop processor)");
        printBulletRow("Carbon Intensity: 475 gCO2/kWh  (global grid average)");
        printBulletRow("Based on IEEE research on software energy consumption");
        System.out.println();

        printSection("IMPACT");
        printCheckRow("Addresses UN SDG 13: Climate Action");
        printCheckRow("Promotes sustainable software engineering practices");
        printCheckRow("Educates developers about environmental costs of code");
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // Goodbye
    // -------------------------------------------------------------------------

    private static void printGoodbye() {
        System.out.println();
        printDivider('=', GREEN);
        System.out.println(ansi().bold().fg(GREEN)
            .a("       Thanks for using EcoCode Analyzer!                 ").reset());
        System.out.println(ansi().fg(WHITE)
            .a("       Making software greener, one line at a time.       ").reset());
        printDivider('=', GREEN);
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // Shared helpers
    // -------------------------------------------------------------------------

    private static void printDivider(char ch, org.fusesource.jansi.Ansi.Color color) {
        System.out.println(ansi().fg(color).a("  " + String.valueOf(ch).repeat(58)).reset());
    }

    private static void printSection(String title) {
        System.out.println(ansi().bold().fg(YELLOW).a("  -- " + title + " --").reset());
        System.out.println();
    }

    private static void printHelpRow(String key, String val) {
        System.out.println(ansi().bold().fg(CYAN).a("  " + key).reset().fg(WHITE).a("   " + val).reset());
    }

    private static void printCheckRow(String text) {
        System.out.println(ansi().fg(GREEN).a("  [+] ").reset().fg(WHITE).a(text).reset());
    }

    private static void printBulletRow(String text) {
        System.out.println(ansi().fg(CYAN).a("   -  ").reset().fg(WHITE).a(text).reset());
    }

    private static void printError(String message) {
        System.out.println(ansi().bold().fg(RED).a("  [!] ").reset().fg(RED).a(message).reset());
    }

    private static String stripExtension(String name) {
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 2) + "..";
    }
}
