// Matrix Multiplication - O(n^3) complexity
public class MatrixMultiply {
    public static int[][] multiply(int[][] a, int[][] b) {
        int n = a.length;
        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }
    public static void printMatrix(int[][] m) {
        for (int[] row : m) {
            for (int val : row) System.out.printf("%4d", val);
            System.out.println();
        }
    }
    public static void main(String[] args) {
        int[][] a = {{1,2},{3,4}};
        int[][] b = {{5,6},{7,8}};
        printMatrix(multiply(a, b));
    }
}
