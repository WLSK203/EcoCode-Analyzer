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
     * Find the function with highest carbon emissions
     */
    public void identifyWorstFunction() {
        if (functionAnalyses.isEmpty()) {
            worstFunction = "None";
            return;
        }

        FunctionAnalysis worst = functionAnalyses.stream()
                .max((a, b) -> Double.compare(a.getCarbonEmissions(), b.getCarbonEmissions()))
                .orElse(null);

        worstFunction = worst != null ? worst.getFunctionName() : "None";
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
        if (totalCarbonEmissions < 1.0) return "Excellent ⭐⭐⭐⭐⭐";
        if (totalCarbonEmissions < 5.0) return "Good ⭐⭐⭐⭐";
        if (totalCarbonEmissions < 10.0) return "Average ⭐⭐⭐";
        if (totalCarbonEmissions < 50.0) return "Poor ⭐⭐";
        return "Very Poor ⭐";
    }

    @Override
    public String toString() {
        return String.format("CarbonReport{file='%s', totalCO2=%.2fg, functions=%d, rating=%s}",
                fileName, totalCarbonEmissions, functionAnalyses.size(), getEmissionRating());
    }
}
