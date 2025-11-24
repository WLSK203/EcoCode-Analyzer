package com.ecocode.models;

/**
 * Represents a suggestion for optimizing code to reduce carbon emissions
 */
public class OptimizationSuggestion {
    private String functionName;
    private int lineNumber;
    private SuggestionType type;
    private String description;
    private String currentPattern;
    private String suggestedPattern;
    private Complexity currentComplexity;
    private Complexity suggestedComplexity;
    private double estimatedCarbonSavings; // grams CO2
    private double savingsPercentage;
    private Priority priority;

    public enum SuggestionType {
        ALGORITHM_REPLACEMENT,
        DATA_STRUCTURE_CHANGE,
        LOOP_OPTIMIZATION,
        CACHING_OPPORTUNITY,
        REDUNDANT_COMPUTATION,
        PARALLEL_PROCESSING,
        EARLY_TERMINATION,
        OTHER
    }

    public enum Priority {
        LOW("Low", 1),
        MEDIUM("Medium", 2),
        HIGH("High", 3),
        CRITICAL("Critical", 4);

        private final String label;
        private final int level;

        Priority(String label, int level) {
            this.label = label;
            this.level = level;
        }

        public String getLabel() {
            return label;
        }

        public int getLevel() {
            return level;
        }
    }

    public OptimizationSuggestion() {
        this.priority = Priority.MEDIUM;
    }

    public OptimizationSuggestion(String functionName, SuggestionType type, String description) {
        this();
        this.functionName = functionName;
        this.type = type;
        this.description = description;
    }

    /**
     * Automatically determine priority based on carbon savings
     */
    public void calculatePriority() {
        if (savingsPercentage > 75) {
            priority = Priority.CRITICAL;
        } else if (savingsPercentage > 50) {
            priority = Priority.HIGH;
        } else if (savingsPercentage > 25) {
            priority = Priority.MEDIUM;
        } else {
            priority = Priority.LOW;
        }
    }

    // Getters and Setters
    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public SuggestionType getType() {
        return type;
    }

    public void setType(SuggestionType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrentPattern() {
        return currentPattern;
    }

    public void setCurrentPattern(String currentPattern) {
        this.currentPattern = currentPattern;
    }

    public String getSuggestedPattern() {
        return suggestedPattern;
    }

    public void setSuggestedPattern(String suggestedPattern) {
        this.suggestedPattern = suggestedPattern;
    }

    public Complexity getCurrentComplexity() {
        return currentComplexity;
    }

    public void setCurrentComplexity(Complexity currentComplexity) {
        this.currentComplexity = currentComplexity;
    }

    public Complexity getSuggestedComplexity() {
        return suggestedComplexity;
    }

    public void setSuggestedComplexity(Complexity suggestedComplexity) {
        this.suggestedComplexity = suggestedComplexity;
    }

    public double getEstimatedCarbonSavings() {
        return estimatedCarbonSavings;
    }

    public void setEstimatedCarbonSavings(double estimatedCarbonSavings) {
        this.estimatedCarbonSavings = estimatedCarbonSavings;
    }

    public double getSavingsPercentage() {
        return savingsPercentage;
    }

    public void setSavingsPercentage(double savingsPercentage) {
        this.savingsPercentage = savingsPercentage;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return String.format("OptimizationSuggestion{function='%s', type=%s, priority=%s, savings=%.2fg (%.1f%%)}",
                functionName, type, priority.getLabel(), estimatedCarbonSavings, savingsPercentage);
    }
}
