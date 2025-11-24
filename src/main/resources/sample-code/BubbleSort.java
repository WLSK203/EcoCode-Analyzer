// Sample Java code with O(n²) complexity
// This file is used for testing the EcoCode Analyzer

public class BubbleSort {
    
    /**
     * Inefficient bubble sort algorithm with O(n²) complexity
     * EcoCode Analyzer should detect this and suggest optimization
     */
    public static void bubbleSort(int[] arr) {
        int n = arr.length;
        
        // Nested loops - O(n²) complexity
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    // Swap elements
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        int[] data = {64, 34, 25, 12, 22, 11, 90};
        
        System.out.println("Original array:");
        printArray(data);
        
        bubbleSort(data);
        
        System.out.println("\nSorted array:");
        printArray(data);
    }
    
    /**
     * Print array - O(n) complexity
     */
    public static void printArray(int[] arr) {
        for (int value : arr) {
            System.out.print(value + " ");
        }
        System.out.println();
    }
}
