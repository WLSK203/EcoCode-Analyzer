package com.ecocode.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Comprehensive report of carbon emission analysis for a code file or project
 */
public class CarbonReport {
    private String fileName;
    private String filePath;
    private LocalDateTime analysisTimestamp;
    private List<FunctionAnalysis> functionAnalyses;
    private double totalCarbonEmissions; // in grams CO2
    private double totalEstimatedTime; // in milliseconds
    private double totalEnergyConsumption; // in watt-hours
    private int totalLinesOfCode;
    private String worstFunction; // Function with highest emissions
    private List<OptimizationSuggestion> suggestions;
    private EnvironmentalMetrics environmentalMetrics;

    public CarbonReport() {
        this.analysisTimestamp = LocalDateTime.now();
        this.functionAnalyses = new ArrayList<>();
        this.suggestions = new ArrayList<>();
        this.environmentalMetrics = new EnvironmentalMetrics();
    }

    public CarbonReport(String fileName, String filePath) {
        this();
        this.fileName = fileName;
        this.filePath = filePath;
    }

    /**
     * Add a function analysis result to the report
     */
    public void addFunctionAnalysis(FunctionAnalysis analysis) {
        this.functionAnalyses.add(analysis);
        this.totalCarbonEmissions += analysis.getCarbonEmissions();
        this.totalEstimatedTime += analysis.getEstimatedExecutionTime();
        this.totalEnergyConsumption += analysis.getEnergyConsumption();
    }

    /**
     * Calculate and update environmental equivalents
     */
    public void calculateEnvironmentalMetrics() {
        environmentalMetrics.calculateEquivalents(totalCarbonEmissions);
    }

    /**
     * Find the function with highest carbon emissions.
     * Only marks a hotspot if the worst function is genuinely problematic
     * (i.e., meaningfully worse than its peers, not just tied at near-zero).
     */
    public void identifyWorstFunction() {
        if (functionAnalyses.isEmpty()) {
            worstFunction = "None";
            return;
        }

        // Only flag a hotspot if there's a function with genuinely high complexity
        FunctionAnalysis worst = functionAnalyses.stream()
                .max((a, b) -> Double.compare(a.getCarbonEmissions(), b.getCarbonEmissions()))
                .orElse(null);

        // Only flag a hotspot if the worst function has genuinely visible emissions
        // (5e-5 = 0.0001 when rounded to 4 decimal places -- the display precision)
        if (worst == null || worst.getCarbonEmissions() < 5e-5) {
            worstFunction = "None";
            return;
        }

        // Only show hotspot if the worst function is meaningfully worse than the average
        double avgEmissions = functionAnalyses.stream()
                .mapToDouble(FunctionAnalysis::getCarbonEmissions).average().orElse(0);

        if (functionAnalyses.size() == 1) {
            // Single function -- always name it
            worstFunction = worst.getFunctionName();
        } else if (worst.getCarbonEmissions() > avgEmissions * 2) {
            // Worst is at least 2x the average -- genuinely bad hotspot
            worstFunction = worst.getFunctionName();
        } else {
            worstFunction = "None (all functions have similar efficiency)";
        }
    }

    // Getters and Setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getAnalysisTimestamp() {
        return analysisTimestamp;
    }

    public List<FunctionAnalysis> getFunctionAnalyses() {
        return functionAnalyses;
    }

    public double getTotalCarbonEmissions() {
        return totalCarbonEmissions;
    }

    public void setTotalCarbonEmissions(double totalCarbonEmissions) {
        this.totalCarbonEmissions = totalCarbonEmissions;
    }

    public double getTotalEstimatedTime() {
        return totalEstimatedTime;
    }

    public double getTotalEnergyConsumption() {
        return totalEnergyConsumption;
    }

    public int getTotalLinesOfCode() {
        return totalLinesOfCode;
    }

    public void setTotalLinesOfCode(int totalLinesOfCode) {
        this.totalLinesOfCode = totalLinesOfCode;
    }

    public String getWorstFunction() {
        return worstFunction;
    }

    public List<OptimizationSuggestion> getSuggestions() {
        return suggestions;
    }

    public void addSuggestion(OptimizationSuggestion suggestion) {
        this.suggestions.add(suggestion);
    }

    public EnvironmentalMetrics getEnvironmentalMetrics() {
        return environmentalMetrics;
    }

    /**
     * Get a summary rating based on total emissions
     */
    public String getEmissionRating() {
        if (totalCarbonEmissions < 1.0)  return "Excellent  [5/5]";
        if (totalCarbonEmissions < 5.0)  return "Good       [4/5]";
        if (totalCarbonEmissions < 10.0) return "Average    [3/5]";
        if (totalCarbonEmissions < 50.0) return "Poor       [2/5]";
        return                                  "Very Poor  [1/5]";
    }

    @Override
    public String toString() {
        return String.format("CarbonReport{file='%s', totalCO2=%.2fg, functions=%d, rating=%s}",
                fileName, totalCarbonEmissions, functionAnalyses.size(), getEmissionRating());
    }
}
