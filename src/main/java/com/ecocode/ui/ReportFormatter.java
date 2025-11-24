package com.ecocode.ui;

import com.ecocode.models.*;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.util.List;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Formats analysis reports for console display with colors and formatting
 */
public class ReportFormatter {
    
    static {
        AnsiConsole.systemInstall(); // Enable ANSI colors on Windows
    }
    
    /**
     * Format a complete carbon report for display
     */
    public String formatCarbonReport(CarbonReport report) {
        StringBuilder sb = new StringBuilder();
        
        // Header
        sb.append(formatHeader("EcoCode Analyzer - Carbon Emission Report"));
        sb.append("\n");
        
        // File Information
        sb.append(ansi().bold().fg(CYAN).a("File: ").reset().a(report.getFileName()).a("\n"));
        sb.append(ansi().fg(WHITE).a("Path: ").a(report.getFilePath()).a("\n"));
        sb.append(ansi().fg(WHITE).a("Analysis Date: ").a(report.getAnalysisTimestamp()).a("\n"));
        sb.append(ansi().fg(WHITE).a("Total Lines of Code: ").a(report.getTotalLinesOfCode()).a("\n"));
        sb.append("\n");
        
        // Overall Carbon Emissions
        sb.append(formatSectionHeader("Overall Carbon Footprint"));
        sb.append(formatCarbonScore(report.getTotalCarbonEmissions()));
        sb.append(ansi().fg(WHITE).a("Total Estimated Time: ")
            .a(String.format("%.2f ms", report.getTotalEstimatedTime())).a("\n"));
        sb.append(ansi().fg(WHITE).a("Total Energy Consumption: ")
            .a(String.format("%.6f Wh", report.getTotalEnergyConsumption())).a("\n"));
        sb.append(ansi().bold().a("Rating: ").a(report.getEmissionRating()).reset().a("\n"));
        sb.append("\n");
        
        // Environmental Equivalents
        EnvironmentalMetrics metrics = report.getEnvironmentalMetrics();
        sb.append(formatSectionHeader("Environmental Impact Equivalents"));
        sb.append(ansi().fg(YELLOW).a("🚗 Driving: ").reset()
            .a(metrics.getEquivalentDrivingDescription()).a("\n"));
        sb.append(ansi().fg(GREEN).a("🌳 Trees needed: ").reset()
            .a(metrics.getEquivalentTreesDescription()).a("\n"));
        sb.append(ansi().fg(BLUE).a("📱 Smartphone charges: ").reset()
            .a(String.format("%.1f charges", metrics.getEquivalentSmartphoneCharges())).a("\n"));
        sb.append(ansi().fg(WHITE).a("   ").a(metrics.getImpactDescription()).a("\n"));
        sb.append("\n");
        
        // Function Analysis
        if (!report.getFunctionAnalyses().isEmpty()) {
            sb.append(formatSectionHeader("Function-Level Analysis"));
            sb.append(formatFunctionTable(report.getFunctionAnalyses()));
            sb.append("\n");
            
            // Highlight worst function
            if (report.getWorstFunction() != null && !report.getWorstFunction().equals("None")) {
                sb.append(ansi().bold().fg(RED).a("⚠️  Hotspot: ").reset()
                    .a(report.getWorstFunction())
                    .a(" has the highest carbon emissions\n"));
                sb.append("\n");
            }
        }
        
        // Optimization Suggestions
        if (!report.getSuggestions().isEmpty()) {
            sb.append(formatSectionHeader("Optimization Suggestions"));
            sb.append(formatSuggestions(report.getSuggestions()));
        } else {
            sb.append(formatSectionHeader("Optimization Suggestions"));
            sb.append(ansi().fg(GREEN).a("✓ No optimization suggestions - code is already efficient!\n"));
        }
        
        sb.append("\n");
        sb.append(formatFooter());
        
        return sb.toString();
    }
    
    /**
     * Format header
     */
    private String formatHeader(String title) {
        int width = 80;
        String border = "═".repeat(width);
        int padding = (width - title.length()) / 2;
        String paddedTitle = " ".repeat(padding) + title;
        
        return ansi().bold().fg(GREEN)
            .a("╔").a(border).a("╗\n")
            .a("║").a(paddedTitle).a(" ".repeat(width - padding - title.length())).a("║\n")
            .a("╚").a(border).a("╝\n")
            .reset().toString();
    }
    
    /**
     * Format section header
     */
    private String formatSectionHeader(String title) {
        return ansi().bold().fg(YELLOW)
            .a("\n┌─ ").a(title).a(" ").a("─".repeat(Math.max(0, 70 - title.length()))).a("\n")
            .reset().toString();
    }
    
    /**
     * Format carbon score with color coding
     */
    private String formatCarbonScore(double carbonGrams) {
        Ansi.Color color;
        String emoji;
        
        if (carbonGrams < 1.0) {
            color = GREEN;
            emoji = "🌱";
        } else if (carbonGrams < 10.0) {
            color = CYAN;
            emoji = "♻️";
        } else if (carbonGrams < 50.0) {
            color = YELLOW;
            emoji = "🌍";
        } else if (carbonGrams < 100.0) {
            color = Ansi.Color.MAGENTA;
            emoji = "⚠️";
        } else {
            color = RED;
            emoji = "🚨";
        }
        
        return ansi().bold().fg(color)
            .a(emoji).a(" Total Carbon Emissions: ")
            .a(String.format("%.4f gCO₂", carbonGrams))
            .reset().a("\n").toString();
    }
    
    /**
     * Format function analysis table
     */
    private String formatFunctionTable(List<FunctionAnalysis> analyses) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(String.format("%-25s %-15s %-15s %-15s\n", 
            "Function", "Time Complexity", "Carbon (gCO₂)", "Time (ms)"));
        sb.append("─".repeat(80)).append("\n");
        
        for (FunctionAnalysis analysis : analyses) {
            Complexity complexity = analysis.getComplexityResult().getTimeComplexity();
            
            // Color code by complexity severity
            Ansi.Color color = getComplexityColor(complexity);
            
            sb.append(ansi().fg(color)
                .a(String.format("%-25s %-15s %-15.4f %-15.2f",
                    truncate(analysis.getFunctionName(), 24),
                    complexity.getNotation(),
                    analysis.getCarbonEmissions(),
                    analysis.getEstimatedExecutionTime()))
                .reset().a("\n"));
        }
        
        return sb.toString();
    }
    
    /**
     * Format optimization suggestions
     */
    private String formatSuggestions(List<OptimizationSuggestion> suggestions) {
        StringBuilder sb = new StringBuilder();
        
        int count = 1;
        for (OptimizationSuggestion suggestion : suggestions) {
            Ansi.Color color = getPriorityColor(suggestion.getPriority());
            String icon = getPriorityIcon(suggestion.getPriority());
            
            sb.append(ansi().bold().fg(color)
                .a(icon).a(" Suggestion #").a(count++).a(" - ")
                .a(suggestion.getPriority().getLabel()).a(" Priority")
                .reset().a("\n"));
            
            sb.append(ansi().fg(WHITE)
                .a("  Function: ").a(suggestion.getFunctionName()).a("\n"));
            sb.append(ansi().fg(WHITE)
                .a("  Type: ").a(suggestion.getType()).a("\n"));
            sb.append(ansi().fg(CYAN)
                .a("  Description: ").a(suggestion.getDescription()).a("\n"));
            
            if (suggestion.getCurrentComplexity() != null && suggestion.getSuggestedComplexity() != null) {
                sb.append(ansi().fg(YELLOW)
                    .a("  ❌ Current: ").a(suggestion.getCurrentComplexity().getNotation()).a("\n"));
                sb.append(ansi().fg(GREEN)
                    .a("  ✅ Suggested: ").a(suggestion.getSuggestedComplexity().getNotation()).a("\n"));
            }
            
            if (suggestion.getEstimatedCarbonSavings() > 0) {
                sb.append(ansi().bold().fg(GREEN)
                    .a("  💰 Potential Savings: ")
                    .a(String.format("%.4f gCO₂ (%.1f%% reduction)", 
                        suggestion.getEstimatedCarbonSavings(),
                        suggestion.getSavingsPercentage()))
                    .reset().a("\n"));
            }
            
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Format footer
     */
    private String formatFooter() {
        return ansi().fg(WHITE)
            .a("─".repeat(80)).a("\n")
            .a("Generated by EcoCode Analyzer | Making Software Greener 🌱\n")
            .reset().toString();
    }
    
    /**
     * Get color based on complexity severity
     */
    private Ansi.Color getComplexityColor(Complexity complexity) {
        return switch (complexity) {
            case O_1, O_LOG_N -> GREEN;
            case O_N, O_N_LOG_N -> CYAN;
            case O_N_SQUARED -> YELLOW;
            case O_N_CUBED -> Ansi.Color.MAGENTA;
            case O_2_POW_N, O_N_FACTORIAL -> RED;
            default -> WHITE;
        };
    }
    
    /**
     * Get color based on priority
     */
    private Ansi.Color getPriorityColor(OptimizationSuggestion.Priority priority) {
        return switch (priority) {
            case LOW -> CYAN;
            case MEDIUM -> YELLOW;
            case HIGH -> Ansi.Color.MAGENTA;
            case CRITICAL -> RED;
        };
    }
    
    /**
     * Get icon based on priority
     */
    private String getPriorityIcon(OptimizationSuggestion.Priority priority) {
        return switch (priority) {
            case LOW -> "ℹ️";
            case MEDIUM -> "⚡";
            case HIGH -> "⚠️";
            case CRITICAL -> "🚨";
        };
    }
    
    /**
     * Truncate string to max length
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
}
