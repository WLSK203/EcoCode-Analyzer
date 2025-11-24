package com.ecocode.core;

import com.ecocode.models.*;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Generates optimization suggestions to reduce carbon emissions
 */
public class OptimizationEngine {
    
    private final CarbonCalculator carbonCalculator;
    
    public OptimizationEngine(CarbonCalculator carbonCalculator) {
        this.carbonCalculator = carbonCalculator;
    }
    
    /**
     * Generate optimization suggestions for a function
     */
    public List<OptimizationSuggestion> generateSuggestions(FunctionAnalysis analysis, MethodDeclaration method) {
        List<OptimizationSuggestion> suggestions = new ArrayList<>();
        
        Complexity currentComplexity = analysis.getComplexityResult().getTimeComplexity();
        
        // Don't suggest optimizations for already optimal code
        if (currentComplexity == Complexity.O_1 || currentComplexity == Complexity.O_LOG_N) {
            return suggestions;
        }
        
        // Check for nested loops
        suggestions.addAll(checkNestedLoops(analysis, method));
        
        // Check for inefficient patterns
        suggestions.addAll(checkInefficientPatterns(analysis, method));
        
        // Check for sorting algorithms
        suggestions.addAll(checkSortingAlgorithms(analysis, method));
        
        // Check for search algorithms
        suggestions.addAll(checkSearchAlgorithms(analysis, method));
        
        // Check for redundant computations
        suggestions.addAll(checkRedundantComputations(analysis, method));
        
        return suggestions;
    }
    
    /**
     * Check for nested loop optimizations
     */
    private List<OptimizationSuggestion> checkNestedLoops(FunctionAnalysis analysis, MethodDeclaration method) {
        List<OptimizationSuggestion> suggestions = new ArrayList<>();
        
        Complexity complexity = analysis.getComplexityResult().getTimeComplexity();
        
        if (complexity == Complexity.O_N_SQUARED) {
            OptimizationSuggestion suggestion = new OptimizationSuggestion();
            suggestion.setFunctionName(analysis.getFunctionName());
            suggestion.setType(OptimizationSuggestion.SuggestionType.DATA_STRUCTURE_CHANGE);
            suggestion.setDescription("Nested loop detected. Consider using HashMap/HashSet for O(1) lookups.");
            suggestion.setCurrentComplexity(Complexity.O_N_SQUARED);
            suggestion.setSuggestedComplexity(Complexity.O_N);
            suggestion.setCurrentPattern("Nested loops with O(n²) complexity");
            suggestion.setSuggestedPattern("Use HashMap for O(1) lookup, reducing to O(n)");
            
            // Calculate savings
            double savings = carbonCalculator.calculateSavings(
                Complexity.O_N_SQUARED, Complexity.O_N, analysis.getInputSize()
            );
            double savingsPercent = carbonCalculator.calculateSavingsPercentage(
                Complexity.O_N_SQUARED, Complexity.O_N, analysis.getInputSize()
            );
            
            suggestion.setEstimatedCarbonSavings(savings);
            suggestion.setSavingsPercentage(savingsPercent);
            suggestion.calculatePriority();
            
            suggestions.add(suggestion);
        }
        
        if (complexity == Complexity.O_N_CUBED) {
            OptimizationSuggestion suggestion = new OptimizationSuggestion();
            suggestion.setFunctionName(analysis.getFunctionName());
            suggestion.setType(OptimizationSuggestion.SuggestionType.ALGORITHM_REPLACEMENT);
            suggestion.setDescription("Triple nested loop detected. Consider dynamic programming or memoization.");
            suggestion.setCurrentComplexity(Complexity.O_N_CUBED);
            suggestion.setSuggestedComplexity(Complexity.O_N_SQUARED);
            suggestion.setPriority(OptimizationSuggestion.Priority.CRITICAL);
            
            double savings = carbonCalculator.calculateSavings(
                Complexity.O_N_CUBED, Complexity.O_N_SQUARED, analysis.getInputSize()
            );
            double savingsPercent = carbonCalculator.calculateSavingsPercentage(
                Complexity.O_N_CUBED, Complexity.O_N_SQUARED, analysis.getInputSize()
            );
            
            suggestion.setEstimatedCarbonSavings(savings);
            suggestion.setSavingsPercentage(savingsPercent);
            
            suggestions.add(suggestion);
        }
        
        return suggestions;
    }
    
    /**
     * Check for inefficient patterns
     */
    private List<OptimizationSuggestion> checkInefficientPatterns(FunctionAnalysis analysis, MethodDeclaration method) {
        List<OptimizationSuggestion> suggestions = new ArrayList<>();
        
        String methodBody = method.toString().toLowerCase();
        
        // Check for ArrayList contains() in loop (O(n²))
        if (methodBody.contains("arraylist") && methodBody.contains(".contains(")) {
            AtomicBoolean inLoop = new AtomicBoolean(false);
            
            method.accept(new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(ForStmt n, Void arg) {
                    inLoop.set(true);
                    super.visit(n, arg);
                    inLoop.set(false);
                }
                
                @Override
                public void visit(WhileStmt n, Void arg) {
                    inLoop.set(true);
                    super.visit(n, arg);
                    inLoop.set(false);
                }
            }, null);
            
            if (inLoop.get()) {
                OptimizationSuggestion suggestion = new OptimizationSuggestion();
                suggestion.setFunctionName(analysis.getFunctionName());
                suggestion.setType(OptimizationSuggestion.SuggestionType.DATA_STRUCTURE_CHANGE);
                suggestion.setDescription("Using ArrayList.contains() in a loop. Replace with HashSet for O(1) lookup.");
                suggestion.setCurrentPattern("ArrayList.contains() in loop");
                suggestion.setSuggestedPattern("HashSet.contains() for O(1) lookup");
                suggestion.setPriority(OptimizationSuggestion.Priority.HIGH);
                
                suggestions.add(suggestion);
            }
        }
        
        return suggestions;
    }
    
    /**
     * Check sorting algorithm efficiency
     */
    private List<OptimizationSuggestion> checkSortingAlgorithms(FunctionAnalysis analysis, MethodDeclaration method) {
        List<OptimizationSuggestion> suggestions = new ArrayList<>();
        
        String methodName = method.getNameAsString().toLowerCase();
        Complexity complexity = analysis.getComplexityResult().getTimeComplexity();
        
        if ((methodName.contains("bubblesort") || methodName.contains("selectionsort")) && 
            complexity == Complexity.O_N_SQUARED) {
            
            OptimizationSuggestion suggestion = new OptimizationSuggestion();
            suggestion.setFunctionName(analysis.getFunctionName());
            suggestion.setType(OptimizationSuggestion.SuggestionType.ALGORITHM_REPLACEMENT);
            suggestion.setDescription("Inefficient sorting algorithm detected. Use QuickSort, MergeSort, or Arrays.sort().");
            suggestion.setCurrentComplexity(Complexity.O_N_SQUARED);
            suggestion.setSuggestedComplexity(Complexity.O_N_LOG_N);
            suggestion.setCurrentPattern("Bubble Sort or Selection Sort");
            suggestion.setSuggestedPattern("QuickSort, MergeSort, or Java's Arrays.sort()");
            
            double savings = carbonCalculator.calculateSavings(
                Complexity.O_N_SQUARED, Complexity.O_N_LOG_N, analysis.getInputSize()
            );
            double savingsPercent = carbonCalculator.calculateSavingsPercentage(
                Complexity.O_N_SQUARED, Complexity.O_N_LOG_N, analysis.getInputSize()
            );
            
            suggestion.setEstimatedCarbonSavings(savings);
            suggestion.setSavingsPercentage(savingsPercent);
            suggestion.calculatePriority();
            
            suggestions.add(suggestion);
        }
        
        return suggestions;
    }
    
    /**
     * Check search algorithm efficiency
     */
    private List<OptimizationSuggestion> checkSearchAlgorithms(FunctionAnalysis analysis, MethodDeclaration method) {
        List<OptimizationSuggestion> suggestions = new ArrayList<>();
        
        String methodName = method.getNameAsString().toLowerCase();
        String methodBody = method.toString().toLowerCase();
        
        // Linear search in sorted array
        if ((methodName.contains("search") || methodName.contains("find")) && 
            !methodName.contains("binary") && 
            analysis.getComplexityResult().getTimeComplexity() == Complexity.O_N) {
            
            OptimizationSuggestion suggestion = new OptimizationSuggestion();
            suggestion.setFunctionName(analysis.getFunctionName());
            suggestion.setType(OptimizationSuggestion.SuggestionType.ALGORITHM_REPLACEMENT);
            suggestion.setDescription("Linear search detected. If data is sorted, use binary search for O(log n).");
            suggestion.setCurrentComplexity(Complexity.O_N);
            suggestion.setSuggestedComplexity(Complexity.O_LOG_N);
            suggestion.setCurrentPattern("Linear search");
            suggestion.setSuggestedPattern("Binary search (if data is sorted)");
            
            double savings = carbonCalculator.calculateSavings(
                Complexity.O_N, Complexity.O_LOG_N, analysis.getInputSize()
            );
            double savingsPercent = carbonCalculator.calculateSavingsPercentage(
                Complexity.O_N, Complexity.O_LOG_N, analysis.getInputSize()
            );
            
            suggestion.setEstimatedCarbonSavings(savings);
            suggestion.setSavingsPercentage(savingsPercent);
            suggestion.calculatePriority();
            
            suggestions.add(suggestion);
        }
        
        return suggestions;
    }
    
    /**
     * Check for redundant computations
     */
    private List<OptimizationSuggestion> checkRedundantComputations(FunctionAnalysis analysis, MethodDeclaration method) {
        List<OptimizationSuggestion> suggestions = new ArrayList<>();
        
        String methodName = method.getNameAsString().toLowerCase();
        Complexity complexity = analysis.getComplexityResult().getTimeComplexity();
        
        // Check for recursive Fibonacci
        if (methodName.contains("fibonacci") && complexity == Complexity.O_2_POW_N) {
            OptimizationSuggestion suggestion = new OptimizationSuggestion();
            suggestion.setFunctionName(analysis.getFunctionName());
            suggestion.setType(OptimizationSuggestion.SuggestionType.CACHING_OPPORTUNITY);
            suggestion.setDescription("Exponential recursive Fibonacci detected. Use memoization or dynamic programming.");
            suggestion.setCurrentComplexity(Complexity.O_2_POW_N);
            suggestion.setSuggestedComplexity(Complexity.O_N);
            suggestion.setCurrentPattern("Recursive Fibonacci without memoization");
            suggestion.setSuggestedPattern("Dynamic programming or memoization");
            suggestion.setPriority(OptimizationSuggestion.Priority.CRITICAL);
            
            double savings = carbonCalculator.calculateSavings(
                Complexity.O_2_POW_N, Complexity.O_N, Math.min(analysis.getInputSize(), 30)
            );
            double savingsPercent = carbonCalculator.calculateSavingsPercentage(
                Complexity.O_2_POW_N, Complexity.O_N, Math.min(analysis.getInputSize(), 30)
            );
            
            suggestion.setEstimatedCarbonSavings(savings);
            suggestion.setSavingsPercentage(savingsPercent);
            
            suggestions.add(suggestion);
        }
        
        return suggestions;
    }
}
