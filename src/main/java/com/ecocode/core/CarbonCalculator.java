package com.ecocode.core;

import com.ecocode.models.Complexity;
import com.ecocode.models.ComplexityResult;
import com.ecocode.models.FunctionAnalysis;

/**
 * Calculates carbon emissions based on code complexity and execution estimates
 * 
 * Formula: Carbon Emission (gCO₂) = CPU Time × Power Consumption × Carbon Intensity
 * 
 * Where:
 * - CPU Time: Estimated execution time based on complexity and input size
 * - Power Consumption: Average CPU power (default: 65W for typical processor)
 * - Carbon Intensity: gCO₂/kWh for electricity grid (default: 475g/kWh global average)
 */
public class CarbonCalculator {
    
    // Configuration constants
    private static final double CPU_POWER_WATTS = 65.0; // Average desktop CPU power
    private static final double CARBON_INTENSITY_G_PER_KWH = 475.0; // Global average
    private static final double CPU_CLOCK_SPEED_GHZ = 3.5; // Average CPU speed
    private static final double INSTRUCTIONS_PER_CYCLE = 2.0; // IPC (Instructions Per Cycle)
    private static final int DEFAULT_INPUT_SIZE = 1000; // Default n for complexity estimation
    private static final double EXECUTION_SCALE_FACTOR = 1.0; // How many times the code runs
    
    // Instruction count multiplier per complexity class
    private static final double BASE_INSTRUCTIONS = 100.0; // Base instructions for O(1)
    
    /**
     * Calculate carbon emissions for a function analysis
     */
    public FunctionAnalysis calculateFunctionEmissions(FunctionAnalysis analysis) {
        if (analysis == null || analysis.getComplexityResult() == null) {
            return analysis;
        }
        
        Complexity complexity = analysis.getComplexityResult().getTimeComplexity();
        int inputSize = analysis.getInputSize();
        
        // Step 1: Estimate operations based on complexity
        double operations = complexity.estimateOperations(inputSize);
        
        // Step 2: Convert operations to instructions
        double totalInstructions = operations * BASE_INSTRUCTIONS;
        
        // Step 3: Calculate CPU cycles needed
        // cycles = instructions / IPC
        double cpuCycles = totalInstructions / INSTRUCTIONS_PER_CYCLE;
        
        // Step 4: Calculate execution time in seconds
        // time = cycles / (clock_speed_Hz)
        // clock_speed_Hz = GHz * 10^9
        double executionTimeSeconds = cpuCycles / (CPU_CLOCK_SPEED_GHZ * 1_000_000_000);
        double executionTimeMs = executionTimeSeconds * 1000;
        
        // Step 5: Calculate energy consumption in watt-hours
        // energy (Wh) = power (W) × time (hours)
        double energyWh = CPU_POWER_WATTS * (executionTimeSeconds / 3600.0);
        
        // Step 6: Calculate carbon emissions in grams
        // carbon (g) = energy (Wh) × carbon_intensity (g/kWh) / 1000
        double carbonGrams = energyWh * CARBON_INTENSITY_G_PER_KWH / 1000.0;
        
        // Step 7: Apply execution scale factor (how many times this runs)
        carbonGrams *= EXECUTION_SCALE_FACTOR;
        
        // Update the analysis object
        analysis.setEstimatedExecutionTime(executionTimeMs);
        analysis.setEnergyConsumption(energyWh);
        analysis.setCarbonEmissions(carbonGrams);
        
        return analysis;
    }
    
    /**
     * Calculate emissions for a specific complexity and input size
     */
    public double calculateEmissions(Complexity complexity, int inputSize) {
        FunctionAnalysis tempAnalysis = new FunctionAnalysis();
        ComplexityResult tempResult = new ComplexityResult();
        tempResult.setTimeComplexity(complexity);
        tempAnalysis.setComplexityResult(tempResult);
        tempAnalysis.setInputSize(inputSize);
        
        calculateFunctionEmissions(tempAnalysis);
        return tempAnalysis.getCarbonEmissions();
    }
    
    /**
     * Estimate CPU execution time in milliseconds
     */
    public double estimateExecutionTime(Complexity complexity, int inputSize) {
        double operations = complexity.estimateOperations(inputSize);
        double totalInstructions = operations * BASE_INSTRUCTIONS;
        double cpuCycles = totalInstructions / INSTRUCTIONS_PER_CYCLE;
        double executionTimeSeconds = cpuCycles / (CPU_CLOCK_SPEED_GHZ * 1_000_000_000);
        return executionTimeSeconds * 1000; // Convert to milliseconds
    }
    
    /**
     * Calculate potential savings from optimization
     */
    public double calculateSavings(Complexity currentComplexity, Complexity optimizedComplexity, int inputSize) {
        double currentEmissions = calculateEmissions(currentComplexity, inputSize);
        double optimizedEmissions = calculateEmissions(optimizedComplexity, inputSize);
        return currentEmissions - optimizedEmissions;
    }
    
    /**
     * Calculate savings percentage
     */
    public double calculateSavingsPercentage(Complexity currentComplexity, Complexity optimizedComplexity, int inputSize) {
        double currentEmissions = calculateEmissions(currentComplexity, inputSize);
        double savings = calculateSavings(currentComplexity, optimizedComplexity, inputSize);
        
        if (currentEmissions == 0) return 0;
        return (savings / currentEmissions) * 100.0;
    }
    
    /**
     * Get configuration info for reporting
     */
    public String getConfigurationSummary() {
        return String.format(
            "Carbon Calculator Configuration:\n" +
            "  CPU Power: %.1fW\n" +
            "  Carbon Intensity: %.1f gCO₂/kWh\n" +
            "  CPU Clock Speed: %.1f GHz\n" +
            "  Instructions Per Cycle: %.1f\n" +
            "  Default Input Size: %d",
            CPU_POWER_WATTS, CARBON_INTENSITY_G_PER_KWH, CPU_CLOCK_SPEED_GHZ,
            INSTRUCTIONS_PER_CYCLE, DEFAULT_INPUT_SIZE
        );
    }
}
