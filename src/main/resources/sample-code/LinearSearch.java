// LinearSearch - O(n) complexity
public class LinearSearch {
    public static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) return i;
        }
        return -1;
    }
    public static int sumArray(int[] arr) {
        int sum = 0;
        for (int val : arr) sum += val;
        return sum;
    }
    public static void main(String[] args) {
        int[] arr = {2, 3, 4, 10, 40};
        int result = linearSearch(arr, 10);
        System.out.println("Found at index: " + result);
    }
}
