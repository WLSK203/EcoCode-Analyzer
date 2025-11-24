package com.ecocode;

import com.ecocode.core.CodeAnalyzer;
import com.ecocode.models.CarbonReport;
import com.ecocode.ui.ReportFormatter;
import org.fusesource.jansi.AnsiConsole;

import java.util.Scanner;

/**
 * Main entry point for EcoCode Analyzer
 * 
 * Usage:
 *   java -jar ecocode-analyzer.jar <file-path>
 *   java -jar ecocode-analyzer.jar --interactive
 */
public class Main {
    
    public static void main(String[] args) {
        AnsiConsole.systemInstall(); // Enable colors on Windows
        
        try {
            if (args.length == 0 || "--interactive".equals(args[0]) || "-i".equals(args[0])) {
                // Interactive mode
                runInteractiveMode();
            } else if ("--help".equals(args[0]) || "-h".equals(args[0])) {
                printHelp();
            } else if ("--version".equals(args[0]) || "-v".equals(args[0])) {
                printVersion();
            } else {
                // Analyze file from command line
                String filePath = args[0];
                analyzeFile(filePath);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            AnsiConsole.systemUninstall();
        }
    }
    
    /**
     * Run interactive mode with menu
     */
    private static void runInteractiveMode() {
        Scanner scanner = new Scanner(System.in);
        CodeAnalyzer analyzer = new CodeAnalyzer();
        ReportFormatter formatter = new ReportFormatter();
        
        printBanner();
        
        while (true) {
            printMenu();
            System.out.print("\nYour choice: ");
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    System.out.print("Enter file path to analyze: ");
                    String filePath = scanner.nextLine().trim();
                    try {
                        System.out.println("\n🔍 Analyzing code...\n");
                        CarbonReport report = analyzer.analyzeFile(filePath);
                        String formattedReport = formatter.formatCarbonReport(report);
                        System.out.println(formattedReport);
                    } catch (Exception e) {
                        System.err.println("❌ Error: " + e.getMessage());
                    }
                    break;
                    
                case "2":
                    System.out.print("Enter project directory path: ");
                    String projectPath = scanner.nextLine().trim();
                    try {
                        System.out.println("\n🔍 Analyzing project...\n");
                        CodeAnalyzer.ProjectReport projectReport = analyzer.analyzeProject(projectPath);
                        printProjectSummary(projectReport, formatter);
                    } catch (Exception e) {
                        System.err.println("❌ Error: " + e.getMessage());
                    }
                    break;
                    
                case "3":
                    printAbout();
                    break;
                    
                case "4":
                    printHelp();
                    break;
                    
                case "5":
                case "q":
                case "quit":
                case "exit":
                    System.out.println("\n👋 Thank you for using EcoCode Analyzer!");
                    System.out.println("Making software greener, one line at a time. 🌱\n");
                    return;
                    
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
            
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
    
    /**
     * Analyze a single file from command line
     */
    private static void analyzeFile(String filePath) {
        try {
            System.out.println("\n🔍 EcoCode Analyzer - Carbon Footprint Analysis\n");
            System.out.println("Analyzing: " + filePath + "\n");
            
            CodeAnalyzer analyzer = new CodeAnalyzer();
            CarbonReport report = analyzer.analyzeFile(filePath);
            
            ReportFormatter formatter = new ReportFormatter();
            String formattedReport = formatter.formatCarbonReport(report);
            System.out.println(formattedReport);
            
        } catch (Exception e) {
            System.err.println("❌ Error analyzing file: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Print project summary
     */
    private static void printProjectSummary(CodeAnalyzer.ProjectReport projectReport, ReportFormatter formatter) {
        System.out.println("═".repeat(80));
        System.out.println("  PROJECT ANALYSIS SUMMARY");
        System.out.println("═".repeat(80));
        System.out.println("Project Path: " + projectReport.getProjectPath());
        System.out.println("Files Analyzed: " + projectReport.getFileReports().size());
        System.out.println("Total Lines of Code: " + projectReport.getTotalLinesOfCode());
        System.out.println(String.format("Total Carbon Emissions: %.4f gCO₂", projectReport.getTotalCarbonEmissions()));
        System.out.println("═".repeat(80));
        System.out.println();
        
        // Show individual file reports
        System.out.println("Individual File Reports:");
        System.out.println("─".repeat(80));
        
        for (CarbonReport fileReport : projectReport.getFileReports()) {
            System.out.println("\n" + formatter.formatCarbonReport(fileReport));
            System.out.println("─".repeat(80));
        }
    }
    
    /**
     * Print application banner
     */
    private static void printBanner() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                               ║");
        System.out.println("║                        🌱 ECOCODE ANALYZER 🌱                                 ║");
        System.out.println("║                                                                               ║");
        System.out.println("║              Carbon Footprint Analysis for Source Code                        ║");
        System.out.println("║              Making Software Environmentally Conscious                        ║");
        System.out.println("║                                                                               ║");
        System.out.println("║                           Version 1.0.0                                       ║");
        System.out.println("║                                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    /**
     * Print menu
     */
    private static void printMenu() {
        System.out.println("\n┌─ MAIN MENU ─────────────────────────────────────────────────────────────┐");
        System.out.println("│                                                                           │");
        System.out.println("│  1. Analyze Single File                                                  │");
        System.out.println("│  2. Analyze Project Directory                                            │");
        System.out.println("│  3. About                                                                 │");
        System.out.println("│  4. Help                                                                  │");
        System.out.println("│  5. Exit                                                                  │");
        System.out.println("│                                                                           │");
        System.out.println("└───────────────────────────────────────────────────────────────────────────┘");
    }
    
    /**
     * Print help information
     */
    private static void printHelp() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                              HELP & USAGE                                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("COMMAND LINE USAGE:");
        System.out.println("  java -jar ecocode-analyzer.jar <file-path>");
        System.out.println("  java -jar ecocode-analyzer.jar --interactive");
        System.out.println();
        System.out.println("OPTIONS:");
        System.out.println("  --interactive, -i    Launch interactive mode with menu");
        System.out.println("  --help, -h           Show this help message");
        System.out.println("  --version, -v        Show version information");
        System.out.println();
        System.out.println("EXAMPLES:");
        System.out.println("  java -jar ecocode-analyzer.jar MyCode.java");
        System.out.println("  java -jar ecocode-analyzer.jar --interactive");
        System.out.println();
        System.out.println("SUPPORTED LANGUAGES:");
        System.out.println("  ✓ Java (.java)");
        System.out.println("  ✓ Python (.py) - Basic analysis");
        System.out.println("  ✓ JavaScript (.js) - Basic analysis");
        System.out.println("  ✓ C/C++ (.c, .cpp) - Basic analysis");
        System.out.println();
        System.out.println("WHAT IT DOES:");
        System.out.println("  • Analyzes code complexity (Big O notation)");
        System.out.println("  • Calculates estimated CPU time and energy consumption");
        System.out.println("  • Computes carbon emissions in gCO₂");
        System.out.println("  • Provides optimization suggestions");
        System.out.println("  • Shows environmental impact equivalents");
        System.out.println();
    }
    
    /**
     * Print version information
     */
    private static void printVersion() {
        System.out.println("\nEcoCode Analyzer v1.0.0");
        System.out.println("Carbon Footprint Analysis for Source Code");
        System.out.println("\nDeveloped for VITyarthi Project");
        System.out.println("Making software environmentally conscious 🌱");
        System.out.println();
    }
    
    /**
     * Print about information
     */
    private static void printAbout() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                 ABOUT                                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("PROJECT: EcoCode Analyzer");
        System.out.println("VERSION: 1.0.0");
        System.out.println();
        System.out.println("DESCRIPTION:");
        System.out.println("  EcoCode Analyzer is an innovative tool that analyzes source code to");
        System.out.println("  calculate its environmental impact. It detects algorithmic complexity,");
        System.out.println("  estimates CPU usage, and converts it to carbon emissions, empowering");
        System.out.println("  developers to write more efficient, environmentally-conscious code.");
        System.out.println();
        System.out.println("THE PROBLEM:");
        System.out.println("  • Software consumes massive electricity through data centers");
        System.out.println("  • Inefficient algorithms lead to higher carbon emissions");
        System.out.println("  • Developers lack awareness of environmental costs");
        System.out.println();
        System.out.println("THE SOLUTION:");
        System.out.println("  • Automated complexity detection (O(n), O(n²), etc.)");
        System.out.println("  • Carbon emission calculation using scientific formulas");
        System.out.println("  • Actionable optimization suggestions");
        System.out.println("  • Real-world environmental equivalents");
        System.out.println();
        System.out.println("METHODOLOGY:");
        System.out.println("  Carbon Emission (gCO₂) = CPU Time × Power × Carbon Intensity");
        System.out.println("  • CPU Power: 65W (average desktop processor)");
        System.out.println("  • Carbon Intensity: 475 gCO₂/kWh (global average)");
        System.out.println("  • Based on IEEE research on software energy consumption");
        System.out.println();
        System.out.println("IMPACT:");
        System.out.println("  🌍 Addresses UN Sustainable Development Goals (SDG 13: Climate Action)");
        System.out.println("  📊 Provides tangible metrics for code efficiency");
        System.out.println("  💡 Educates developers about environmental impact");
        System.out.println("  ♻️ Promotes sustainable software development practices");
        System.out.println();
    }
}
