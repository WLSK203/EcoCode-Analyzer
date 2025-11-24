// Sample Java code with O(log n) complexity
// This demonstrates efficient code that should get a good rating

public class BinarySearch {
    
    /**
     * Binary search algorithm with O(log n) complexity
     * EFFICIENT - EcoCode Analyzer should rate this well
     */
    public static int binarySearch(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] == target) {
                return mid;
            }
            
            if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return -1; // Not found
    }
    
    public static void main(String[] args) {
        int[] sortedArray = {2, 5, 8, 12, 16, 23, 38, 45, 56, 67, 78};
        int target = 23;
        
        int result = binarySearch(sortedArray, target);
        
        if (result != -1) {
            System.out.println("Element found at index: " + result);
        } else {
            System.out.println("Element not found");
        }
    }
}
