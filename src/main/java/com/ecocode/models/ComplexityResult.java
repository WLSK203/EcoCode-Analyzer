package com.ecocode.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the result of complexity analysis for a code block or function
 */
public class ComplexityResult {
    private String functionName;
    private Complexity timeComplexity;
    private Complexity spaceComplexity;
    private double confidenceScore; // 0.0 to 1.0
    private String detectionMethod;
    private List<String> details;
    private int startLine;
    private int endLine;

    public ComplexityResult() {
        this.details = new ArrayList<>();
        this.timeComplexity = Complexity.UNKNOWN;
        this.spaceComplexity = Complexity.O_1;
        this.confidenceScore = 0.0;
    }

    public ComplexityResult(String functionName, Complexity timeComplexity, Complexity spaceComplexity) {
        this();
        this.functionName = functionName;
        this.timeComplexity = timeComplexity;
        this.spaceComplexity = spaceComplexity;
    }

    // Getters and Setters
    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Complexity getTimeComplexity() {
        return timeComplexity;
    }

    public void setTimeComplexity(Complexity timeComplexity) {
        this.timeComplexity = timeComplexity;
    }

    public Complexity getSpaceComplexity() {
        return spaceComplexity;
    }

    public void setSpaceComplexity(Complexity spaceComplexity) {
        this.spaceComplexity = spaceComplexity;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = Math.max(0.0, Math.min(1.0, confidenceScore));
    }

    public String getDetectionMethod() {
        return detectionMethod;
    }

    public void setDetectionMethod(String detectionMethod) {
        this.detectionMethod = detectionMethod;
    }

    public List<String> getDetails() {
        return details;
    }

    public void addDetail(String detail) {
        this.details.add(detail);
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    @Override
    public String toString() {
        return String.format("ComplexityResult{function='%s', time=%s, space=%s, confidence=%.2f}",
                functionName, timeComplexity.getNotation(), spaceComplexity.getNotation(), confidenceScore);
    }
}
