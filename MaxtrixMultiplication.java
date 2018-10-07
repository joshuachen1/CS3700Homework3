import java.util.concurrent.ThreadLocalRandom;

public class MaxtrixMultiplication {
    public static void main(String[] args) {
        float[][] matrixA, matrixB, matrixC;
        int m, n, p;

        // 1 Thread
        for (int size = 4; size <= 128; size *= 2) {
            m = n = p = size;

            matrixA = fillMatrix(m, n);
            matrixB = fillMatrix(n, p);
            matrixC = new float[m][p];

            // 1 Thread
            long timeIn = System.nanoTime();
            matMult(matrixA, matrixB, matrixC, m, n, p, 1);
            long timeOut = System.nanoTime() - timeIn;

            System.out.printf("Time with 1 Thread (%d x %d): %d (ns)\n", m, p, timeOut);
        }


        /*
        System.out.println("Matrix A:");
        printMatrix(matrixA);
        System.out.println("\nMatrix B:");
        printMatrix(matrixB);
        System.out.println("\nMatrix C:");
        printMatrix(matrixC);
        */
    }

    public static void matMult (float[][] A, float[][] B, float[][] C, int m, int n, int p, int numThreads) {

        // Classical Matrix Multiplication
        for (int row = 0; row < m; row++) {
            for (int col = 0; col < p; col++) {
                for (int i = 0; i < n; i++) {
                    C[row][col] += A[row][i] * B[i][col];
                }
            }
        }
    }

    public static float[][] fillMatrix(int row, int col) {
        float[][] temp = new float[row][col];

        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[0].length; j++) {
                temp[i][j] = ThreadLocalRandom.current().nextFloat();
            }
        }
        return temp;
    }

    public static void printMatrix (float[][] m) {
        for (int row = 0; row < m.length; row++) {
            System.out.print("[");
            for (int col = 0; col < m.length; col++) {
                if (col == m.length - 1)
                    System.out.printf("%-5.2f", m[row][col]);
                else
                    System.out.printf("%-5.2f", m[row][col]);
            }
            System.out.println("]");
        }
    }
}
