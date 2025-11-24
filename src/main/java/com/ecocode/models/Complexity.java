package com.ecocode.models;

/**
 * Enum representing different time complexity classes
 * Used for categorizing and comparing algorithm efficiency
 */
public enum Complexity {
    O_1("O(1)", "Constant", 1.0),
    O_LOG_N("O(log n)", "Logarithmic", 2.0),
    O_N("O(n)", "Linear", 3.0),
    O_N_LOG_N("O(n log n)", "Linearithmic", 4.0),
    O_N_SQUARED("O(n²)", "Quadratic", 5.0),
    O_N_CUBED("O(n³)", "Cubic", 6.0),
    O_2_POW_N("O(2^n)", "Exponential", 7.0),
    O_N_FACTORIAL("O(n!)", "Factorial", 8.0),
    UNKNOWN("Unknown", "Unknown", 0.0);

    private final String notation;
    private final String name;
    private final double severity; // Higher = worse performance

    Complexity(String notation, String name, double severity) {
        this.notation = notation;
        this.name = name;
        this.severity = severity;
    }

    public String getNotation() {
        return notation;
    }

    public String getName() {
        return name;
    }

    public double getSeverity() {
        return severity;
    }

    /**
     * Calculate estimated operations for given input size
     * @param n Input size
     * @return Estimated number of operations
     */
    public double estimateOperations(int n) {
        return switch (this) {
            case O_1 -> 1;
            case O_LOG_N -> Math.log(n) / Math.log(2);
            case O_N -> n;
            case O_N_LOG_N -> n * (Math.log(n) / Math.log(2));
            case O_N_SQUARED -> n * n;
            case O_N_CUBED -> n * n * n;
            case O_2_POW_N -> Math.pow(2, Math.min(n, 30)); // Cap to prevent overflow
            case O_N_FACTORIAL -> factorial(Math.min(n, 20)); // Cap to prevent overflow
            default -> 0;
        };
    }

    private double factorial(int n) {
        if (n <= 1) return 1;
        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    @Override
    public String toString() {
        return notation + " (" + name + ")";
    }
}
