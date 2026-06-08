package com.ecocode.ui;

import com.ecocode.models.*;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.util.List;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Formats analysis reports for console display.
 * Uses ANSI colors only -- all border/structure chars are plain ASCII
 * so they render correctly on any Windows terminal codepage.
 */
public class ReportFormatter {

    static {
        AnsiConsole.systemInstall();
    }

    // -------------------------------------------------------------------------
    // Main report entry point
    // -------------------------------------------------------------------------

    public String formatCarbonReport(CarbonReport report) {
        StringBuilder sb = new StringBuilder();

        // Header
        sb.append(divider('=', GREEN));
        sb.append(ansi().bold().fg(GREEN)
            .a("        EcoCode Analyzer -- Carbon Emission Report\n").reset());
        sb.append(divider('=', GREEN));
        sb.append("\n");

        // Metadata
        sb.append(metricRow("File",          report.getFileName()));
        sb.append(metricRow("Path",          report.getFilePath()));
        sb.append(metricRow("Analyzed",      report.getAnalysisTimestamp().toString()));
        sb.append(metricRow("Lines of Code", String.valueOf(report.getTotalLinesOfCode())));
        sb.append("\n");

        // Carbon footprint
        sb.append(sectionHeader("CARBON FOOTPRINT"));
        sb.append(formatCarbonScore(report.getTotalCarbonEmissions()));
        sb.append(metricRow("Estimated CPU Time", String.format("%.2f ms", report.getTotalEstimatedTime())));
        sb.append(metricRow("Energy Consumption", String.format("%.6f Wh", report.getTotalEnergyConsumption())));
        sb.append(metricRow("Rating",            report.getEmissionRating()));
        sb.append("\n");

        // Environmental equivalents
        EnvironmentalMetrics m = report.getEnvironmentalMetrics();
        sb.append(sectionHeader("ENVIRONMENTAL EQUIVALENTS"));
        sb.append(envRow("Driving",            m.getEquivalentDrivingDescription(),          YELLOW));
        sb.append(envRow("Trees needed",        m.getEquivalentTreesDescription(),            GREEN));
        sb.append(envRow("Smartphone charges",  String.format("%.1f charges",
                          m.getEquivalentSmartphoneCharges()),                               CYAN));
        sb.append(ansi().fg(WHITE).a("     ").a(m.getImpactDescription()).reset().a("\n"));
        sb.append("\n");

        // Function table
        if (!report.getFunctionAnalyses().isEmpty()) {
            sb.append(sectionHeader("FUNCTION-LEVEL ANALYSIS"));
            sb.append(formatFunctionTable(report.getFunctionAnalyses()));

            if (report.getWorstFunction() != null
                    && !report.getWorstFunction().equals("None")
                    && !report.getWorstFunction().startsWith("None")) {
                sb.append("\n");
                sb.append(ansi().bold().fg(RED)
                    .a("  [!] Hotspot: ").reset()
                    .fg(RED).a(report.getWorstFunction())
                    .fg(WHITE).a(" -- has the highest carbon emissions").reset()
                    .a("\n"));
            }
            sb.append("\n");
        }

        // Suggestions
        sb.append(sectionHeader("OPTIMIZATION SUGGESTIONS"));
        if (!report.getSuggestions().isEmpty()) {
            sb.append(formatSuggestions(report.getSuggestions()));
        } else {
            sb.append(ansi().fg(GREEN).a("  [+] No suggestions -- code is already well optimized!\n").reset());
        }

        sb.append("\n");
        sb.append(divider('-', WHITE));
        sb.append(ansi().fg(244)
            .a("  EcoCode Analyzer v1.0.0  |  Making Code Greener\n")
            .reset());

        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Structure helpers (ASCII-only)
    // -------------------------------------------------------------------------

    private String divider(char ch, Ansi.Color color) {
        return ansi().fg(color).a("  " + String.valueOf(ch).repeat(58) + "\n").reset().toString();
    }

    private String sectionHeader(String title) {
        return ansi().bold().fg(YELLOW)
            .a("\n  -- " + title + " --\n\n")
            .reset().toString();
    }

    private String metricRow(String label, String value) {
        return ansi()
            .fg(CYAN).a(String.format("  %-20s", label + ":")).reset()
            .fg(WHITE).a(value).reset()
            .a("\n").toString();
    }

    private String envRow(String label, String value, Ansi.Color color) {
        return ansi()
            .fg(color).a(String.format("  %-22s", label + ":")).reset()
            .fg(WHITE).a(value).reset()
            .a("\n").toString();
    }

    // -------------------------------------------------------------------------
    // Carbon score with ASCII gauge bar
    // -------------------------------------------------------------------------

    private String formatCarbonScore(double carbonGrams) {
        Ansi.Color color;
        String levelLabel;
        int filled;

        if (carbonGrams < 1.0) {
            color = GREEN;              levelLabel = "Excellent"; filled = 1;
        } else if (carbonGrams < 10.0) {
            color = CYAN;               levelLabel = "Good";      filled = 3;
        } else if (carbonGrams < 50.0) {
            color = YELLOW;             levelLabel = "Moderate";  filled = 5;
        } else if (carbonGrams < 100.0) {
            color = Ansi.Color.MAGENTA; levelLabel = "High";      filled = 7;
        } else {
            color = RED;               levelLabel = "Critical";  filled = 10;
        }

        // Pure ASCII gauge: # for filled, . for empty
        String gauge = "#".repeat(filled) + ".".repeat(10 - filled);

        return ansi()
            .fg(CYAN).a(String.format("  %-20s", "Carbon Emissions:")).reset()
            .bold().fg(color).a(String.format("%.4f gCO2", carbonGrams)).reset()
            .a("\n")
            .fg(CYAN).a(String.format("  %-20s", "Footprint Level:")).reset()
            .fg(color).a("[" + gauge + "]  ").bold().a(levelLabel).reset()
            .a("\n").toString();
    }

    // -------------------------------------------------------------------------
    // Function table
    // -------------------------------------------------------------------------

    private String formatFunctionTable(List<FunctionAnalysis> analyses) {
        StringBuilder sb = new StringBuilder();

        sb.append(ansi().bold().fg(WHITE)
            .a(String.format("  %-26s %-14s %-14s %-12s\n",
                "Function", "Complexity", "Carbon(gCO2)", "Time(ms)"))
            .reset());
        sb.append(ansi().fg(244).a("  " + "-".repeat(68) + "\n").reset());

        boolean alt = false;
        for (FunctionAnalysis a : analyses) {
            Complexity c = a.getComplexityResult().getTimeComplexity();
            Ansi.Color color = complexityColor(c);
            String row = String.format("  %-26s %-14s %-14.4f %-12.2f",
                truncate(a.getFunctionName(), 25),
                c.getNotation(),
                a.getCarbonEmissions(),
                a.getEstimatedExecutionTime());
            sb.append(alt
                ? ansi().fg(color).a(row).reset().a("\n")
                : ansi().bold().fg(color).a(row).reset().a("\n"));
            alt = !alt;
        }
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Suggestion cards (ASCII borders)
    // -------------------------------------------------------------------------

    private String formatSuggestions(List<OptimizationSuggestion> suggestions) {
        StringBuilder sb = new StringBuilder();
        int count = 1;

        for (OptimizationSuggestion s : suggestions) {
            Ansi.Color color = priorityColor(s.getPriority());
            String icon  = priorityIcon(s.getPriority());
            String label = s.getPriority().getLabel().toUpperCase() + " PRIORITY";

            // Top border
            sb.append(ansi().fg(color)
                .a("  +" + "-".repeat(58) + "+\n")
                .reset());

            // Title row
            sb.append(ansi().fg(color).a("  | ").reset()
                .bold().fg(color).a(icon + " Suggestion #" + count + " -- " + label)
                .reset().a("\n"));

            sb.append(ansi().fg(color).a("  +" + "-".repeat(58) + "+\n").reset());

            // Content rows
            sb.append(cardRow(color, "Function", s.getFunctionName()));
            sb.append(cardRow(color, "Type",     formatType(s.getType())));
            sb.append(ansi().fg(color).a("  | ").reset()
                .fg(CYAN).a("  " + s.getDescription()).reset().a("\n"));

            if (s.getCurrentComplexity() != null && s.getSuggestedComplexity() != null) {
                sb.append(ansi().fg(color).a("  | ").reset()
                    .fg(RED).a("  [X] Current:   ").bold().a(s.getCurrentComplexity().getNotation()).reset().a("\n"));
                sb.append(ansi().fg(color).a("  | ").reset()
                    .fg(GREEN).a("  [+] Suggested: ").bold().a(s.getSuggestedComplexity().getNotation()).reset().a("\n"));
            }

            if (s.getEstimatedCarbonSavings() > 0) {
                sb.append(ansi().fg(color).a("  | ").reset()
                    .bold().fg(GREEN)
                    .a(String.format("  [$] Saves: %.4f gCO2  (%.1f%% reduction)",
                        s.getEstimatedCarbonSavings(), s.getSavingsPercentage()))
                    .reset().a("\n"));
            }

            // Bottom border
            sb.append(ansi().fg(color)
                .a("  +" + "-".repeat(58) + "+\n\n")
                .reset());

            count++;
        }
        return sb.toString();
    }

    private String cardRow(Ansi.Color borderColor, String label, String value) {
        return ansi().fg(borderColor).a("  | ").reset()
            .fg(CYAN).a(String.format("  %-12s", label + ":")).reset()
            .fg(WHITE).a(value).reset()
            .a("\n").toString();
    }

    private String formatType(OptimizationSuggestion.SuggestionType type) {
        if (type == null) return "General";
        return switch (type) {
            case ALGORITHM_REPLACEMENT -> "Algorithm Replacement";
            case DATA_STRUCTURE_CHANGE -> "Data Structure Change";
            case CACHING_OPPORTUNITY   -> "Caching / Memoization";
            case PARALLEL_PROCESSING   -> "Parallelization";
            case LOOP_OPTIMIZATION     -> "Loop Optimization";
            default                    -> type.toString();
        };
    }

    // -------------------------------------------------------------------------
    // Color / icon helpers
    // -------------------------------------------------------------------------

    private Ansi.Color complexityColor(Complexity c) {
        return switch (c) {
            case O_1, O_LOG_N             -> GREEN;
            case O_N, O_N_LOG_N           -> CYAN;
            case O_N_SQUARED              -> YELLOW;
            case O_N_CUBED                -> Ansi.Color.MAGENTA;
            case O_2_POW_N, O_N_FACTORIAL -> RED;
            default                       -> WHITE;
        };
    }

    private Ansi.Color priorityColor(OptimizationSuggestion.Priority p) {
        return switch (p) {
            case LOW      -> CYAN;
            case MEDIUM   -> YELLOW;
            case HIGH     -> Ansi.Color.MAGENTA;
            case CRITICAL -> RED;
        };
    }

    private String priorityIcon(OptimizationSuggestion.Priority p) {
        return switch (p) {
            case LOW      -> "[i]";
            case MEDIUM   -> "[~]";
            case HIGH     -> "[!]";
            case CRITICAL -> "[X]";
        };
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 2) + "..";
    }
}
