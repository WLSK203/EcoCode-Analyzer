package com.ecocode.models;

/**
 * Analysis result for a single function/method
 */
public class FunctionAnalysis {
    private String functionName;
    private ComplexityResult complexityResult;
    private double carbonEmissions; // grams CO2
    private double estimatedExecutionTime; // milliseconds
    private double energyConsumption; // watt-hours
    private int inputSize; // Default input size used for estimation
    private int linesOfCode;

    public FunctionAnalysis() {
        this.inputSize = 1000; // Default input size for estimation
    }

    public FunctionAnalysis(String functionName, ComplexityResult complexityResult) {
        this();
        this.functionName = functionName;
        this.complexityResult = complexityResult;
    }

    // Getters and Setters
    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public ComplexityResult getComplexityResult() {
        return complexityResult;
    }

    public void setComplexityResult(ComplexityResult complexityResult) {
        this.complexityResult = complexityResult;
    }

    public double getCarbonEmissions() {
        return carbonEmissions;
    }

    public void setCarbonEmissions(double carbonEmissions) {
        this.carbonEmissions = carbonEmissions;
    }

    public double getEstimatedExecutionTime() {
        return estimatedExecutionTime;
    }

    public void setEstimatedExecutionTime(double estimatedExecutionTime) {
        this.estimatedExecutionTime = estimatedExecutionTime;
    }

    public double getEnergyConsumption() {
        return energyConsumption;
    }

    public void setEnergyConsumption(double energyConsumption) {
        this.energyConsumption = energyConsumption;
    }

    public int getInputSize() {
        return inputSize;
    }

    public void setInputSize(int inputSize) {
        this.inputSize = inputSize;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(int linesOfCode) {
        this.linesOfCode = linesOfCode;
    }

    @Override
    public String toString() {
        return String.format("FunctionAnalysis{name='%s', complexity=%s, CO2=%.4fg}",
                functionName, 
                complexityResult != null ? complexityResult.getTimeComplexity().getNotation() : "Unknown",
                carbonEmissions);
    }
}
