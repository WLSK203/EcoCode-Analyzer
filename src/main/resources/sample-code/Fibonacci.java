// Sample Java code with O(2^n) exponential complexity
// This demonstrates the worst-case scenario for carbon emissions

public class Fibonacci {
    
    /**
     * Recursive Fibonacci with exponential O(2^n) complexity
     * VERY INEFFICIENT - EcoCode Analyzer should flag this as CRITICAL
     */
    public static long fibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        // Multiple recursive calls without memoization
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
    
    /**
     * Optimized Fibonacci using dynamic programming - O(n) complexity
     * This is what the analyzer should suggest
     */
    public static long fibonacciOptimized(int n) {
        if (n <= 1) return n;
        
        long[] dp = new long[n + 1];
        dp[0] = 0;
        dp[1] = 1;
        
        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        
        return dp[n];
    }
    
    public static void main(String[] args) {
        int n = 10;
        
        System.out.println("Fibonacci(" + n + ") = " + fibonacci(n));
        System.out.println("Fibonacci Optimized(" + n + ") = " + fibonacciOptimized(n));
    }
}
