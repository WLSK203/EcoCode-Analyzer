package com.ecocode.core;

import com.ecocode.models.Complexity;
import com.ecocode.models.ComplexityResult;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Detects algorithmic complexity by analyzing code structure
 * Uses pattern matching and heuristics to determine time complexity
 */
public class ComplexityDetector {

    /**
     * Analyze a Java method and detect its complexity
     */
    public ComplexityResult analyzeMethod(MethodDeclaration method) {
        ComplexityResult result = new ComplexityResult();
        result.setFunctionName(method.getNameAsString());
        result.setStartLine(method.getBegin().map(pos -> pos.line).orElse(0));
        result.setEndLine(method.getEnd().map(pos -> pos.line).orElse(0));

        // Analyze loop structures
        LoopAnalysisVisitor loopVisitor = new LoopAnalysisVisitor();
        method.accept(loopVisitor, null);

        int maxNestedLoops = loopVisitor.getMaxNestedDepth();
        boolean hasRecursion = detectRecursion(method);

        // Determine complexity based on patterns
        Complexity timeComplexity = determineTimeComplexity(maxNestedLoops, hasRecursion, method);
        result.setTimeComplexity(timeComplexity);

        // Space complexity (simplified - based on data structure allocations)
        Complexity spaceComplexity = determineSpaceComplexity(method);
        result.setSpaceComplexity(spaceComplexity);

        // Set confidence based on detection method
        result.setConfidenceScore(calculateConfidence(maxNestedLoops, hasRecursion));
        result.setDetectionMethod(getDetectionMethodDescription(maxNestedLoops, hasRecursion));

        // Add details
        result.addDetail("Maximum nested loop depth: " + maxNestedLoops);
        if (hasRecursion) {
            result.addDetail("Recursive function detected");
        }

        return result;
    }

    /**
     * Determine time complexity based on loop nesting and recursion
     */
    private Complexity determineTimeComplexity(int maxNestedLoops, boolean hasRecursion, MethodDeclaration method) {
        // Check for specific algorithm patterns first
        Complexity patternComplexity = detectKnownPatterns(method);
        if (patternComplexity != Complexity.UNKNOWN) {
            return patternComplexity;
        }

        // Recursion analysis
        if (hasRecursion) {
            if (containsMultipleRecursiveCalls(method)) {
                return Complexity.O_2_POW_N; // Fibonacci-like recursion
            }
            if (containsDivideAndConquer(method)) {
                return Complexity.O_N_LOG_N; // Merge sort, quick sort pattern
            }
            return Complexity.O_N; // Simple recursion
        }

        // Loop-based analysis
        switch (maxNestedLoops) {
            case 0:
                return Complexity.O_1; // No loops
            case 1:
                return detectSingleLoopComplexity(method);
            case 2:
                return Complexity.O_N_SQUARED;
            case 3:
                return Complexity.O_N_CUBED;
            default:
                return Complexity.O_N_CUBED; // 3+ nested loops
        }
    }

    /**
     * Detect complexity for single loop (might be O(n) or O(log n))
     */
    private Complexity detectSingleLoopComplexity(MethodDeclaration method) {
        // Check if loop variable is divided/multiplied (binary search pattern)
        LoopPatternVisitor patternVisitor = new LoopPatternVisitor();
        method.accept(patternVisitor, null);

        if (patternVisitor.hasDividePattern()) {
            return Complexity.O_LOG_N;
        }
        if (patternVisitor.hasMultiplyPattern()) {
            return Complexity.O_LOG_N;
        }

        return Complexity.O_N; // Standard linear loop
    }

    /**
     * Detect known algorithm patterns
     */
    private Complexity detectKnownPatterns(MethodDeclaration method) {
        String methodName = method.getNameAsString().toLowerCase();
        String methodBody = method.toString().toLowerCase();

        // Binary search pattern
        if (methodName.contains("binarysearch") || 
            (methodBody.contains("while") && methodBody.contains("mid") && 
             (methodBody.contains("left") || methodBody.contains("right")))) {
            return Complexity.O_LOG_N;
        }

        // Sorting algorithms
        if (methodName.contains("bubblesort") || methodName.contains("selectionsort")) {
            return Complexity.O_N_SQUARED;
        }
        if (methodName.contains("mergesort") || methodName.contains("quicksort")) {
            return Complexity.O_N_LOG_N;
        }

        // Factorial
        if (methodName.contains("factorial") && detectRecursion(method)) {
            return Complexity.O_N;
        }

        // Fibonacci (recursive)
        if (methodName.contains("fibonacci") && detectRecursion(method) && 
            containsMultipleRecursiveCalls(method)) {
            return Complexity.O_2_POW_N;
        }

        return Complexity.UNKNOWN;
    }

    /**
     * Detect if method is recursive
     */
    private boolean detectRecursion(MethodDeclaration method) {
        String methodName = method.getNameAsString();
        AtomicReference<Boolean> isRecursive = new AtomicReference<>(false);

        method.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(com.github.javaparser.ast.expr.MethodCallExpr n, Void arg) {
                super.visit(n, arg);
                if (n.getNameAsString().equals(methodName)) {
                    isRecursive.set(true);
                }
            }
        }, null);

        return isRecursive.get();
    }

    /**
     * Check if method contains multiple recursive calls (like Fibonacci)
     */
    private boolean containsMultipleRecursiveCalls(MethodDeclaration method) {
        String methodName = method.getNameAsString();
        AtomicInteger callCount = new AtomicInteger(0);

        method.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(com.github.javaparser.ast.expr.MethodCallExpr n, Void arg) {
                super.visit(n, arg);
                if (n.getNameAsString().equals(methodName)) {
                    callCount.incrementAndGet();
                }
            }
        }, null);

        return callCount.get() >= 2;
    }

    /**
     * Detect divide-and-conquer pattern (like merge sort)
     */
    private boolean containsDivideAndConquer(MethodDeclaration method) {
        String body = method.toString().toLowerCase();
        return body.contains("/2") || body.contains("/ 2") || 
               body.contains("mid") && detectRecursion(method);
    }

    /**
     * Determine space complexity
     */
    private Complexity determineSpaceComplexity(MethodDeclaration method) {
        // Simplified: Check for array/list allocations
        AtomicReference<Boolean> hasArrayAllocation = new AtomicReference<>(false);
        AtomicReference<Boolean> hasRecursion = new AtomicReference<>(detectRecursion(method));

        method.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(com.github.javaparser.ast.expr.ArrayCreationExpr n, Void arg) {
                super.visit(n, arg);
                hasArrayAllocation.set(true);
            }

            @Override
            public void visit(com.github.javaparser.ast.expr.ObjectCreationExpr n, Void arg) {
                super.visit(n, arg);
                String type = n.getTypeAsString().toLowerCase();
                if (type.contains("list") || type.contains("array") || type.contains("map")) {
                    hasArrayAllocation.set(true);
                }
            }
        }, null);

        if (hasArrayAllocation.get()) {
            return Complexity.O_N;
        }
        if (hasRecursion.get()) {
            return Complexity.O_N; // Recursion uses stack space
        }

        return Complexity.O_1;
    }

    /**
     * Calculate confidence score based on detection method
     */
    private double calculateConfidence(int nestedLoops, boolean hasRecursion) {
        if (nestedLoops > 0 || hasRecursion) {
            return 0.85; // High confidence for clear patterns
        }
        return 0.70; // Medium confidence for O(1) detection
    }

    /**
     * Get description of detection method
     */
    private String getDetectionMethodDescription(int nestedLoops, boolean hasRecursion) {
        if (hasRecursion) {
            return "Recursion pattern analysis";
        }
        if (nestedLoops > 0) {
            return "Loop nesting analysis";
        }
        return "Static analysis";
    }

    /**
     * Visitor to analyze loop nesting
     */
    private static class LoopAnalysisVisitor extends VoidVisitorAdapter<Void> {
        private int currentDepth = 0;
        private int maxDepth = 0;

        @Override
        public void visit(ForStmt n, Void arg) {
            currentDepth++;
            maxDepth = Math.max(maxDepth, currentDepth);
            super.visit(n, arg);
            currentDepth--;
        }

        @Override
        public void visit(WhileStmt n, Void arg) {
            currentDepth++;
            maxDepth = Math.max(maxDepth, currentDepth);
            super.visit(n, arg);
            currentDepth--;
        }

        @Override
        public void visit(DoStmt n, Void arg) {
            currentDepth++;
            maxDepth = Math.max(maxDepth, currentDepth);
            super.visit(n, arg);
            currentDepth--;
        }

        @Override
        public void visit(ForEachStmt n, Void arg) {
            currentDepth++;
            maxDepth = Math.max(maxDepth, currentDepth);
            super.visit(n, arg);
            currentDepth--;
        }

        public int getMaxNestedDepth() {
            return maxDepth;
        }
    }

    /**
     * Visitor to detect specific loop patterns (like i/=2 for binary search)
     */
    private static class LoopPatternVisitor extends VoidVisitorAdapter<Void> {
        private boolean dividePattern = false;
        private boolean multiplyPattern = false;

        @Override
        public void visit(com.github.javaparser.ast.expr.AssignExpr n, Void arg) {
            super.visit(n, arg);
            String expr = n.toString().toLowerCase();
            if (expr.contains("/=") || expr.contains("/ 2")) {
                dividePattern = true;
            }
            if (expr.contains("*=") || expr.contains("* 2")) {
                multiplyPattern = true;
            }
        }

        @Override
        public void visit(com.github.javaparser.ast.expr.UnaryExpr n, Void arg) {
            super.visit(n, arg);
            String expr = n.toString().toLowerCase();
            if (expr.contains("/")) {
                dividePattern = true;
            }
        }

        public boolean hasDividePattern() {
            return dividePattern;
        }

        public boolean hasMultiplyPattern() {
            return multiplyPattern;
        }
    }
}
