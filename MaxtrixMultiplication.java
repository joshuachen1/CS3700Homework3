import java.util.Arrays;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

public class MaxtrixMultiplication {
    public static void main(String[] args) {
        Phaser ph = new Phaser();
        float[][] matrixA, matrixB, matrixC;
        int m, n, p;

        m = n = p = 8;
        matrixA = fillMatrix(m, n);
        matrixB = fillMatrix(n, p);
        matrixC = new float[m][p];


        // 1 Thread
        long timeIn = System.nanoTime();
        new MaxtrixMultiplication().matMult(ph, matrixA, matrixB, matrixC, m, n, p);
        long timeOut = System.nanoTime() - timeIn;
        System.out.printf("Time with 1 Threads: %5.10f sec\n", (timeOut / 1e9));
        printMatrix(matrixC);

        // 2 Threads
        matrixC = new float[m][p];

        timeIn = System.nanoTime();
        new MaxtrixMultiplication().matMult(ph, matrixA, matrixB, matrixC, m / 2, n, p / 2);
        new MaxtrixMultiplication().matMult(ph, matrixA, matrixB, matrixC, m, n, p);
        timeOut = System.nanoTime() - timeIn;
        System.out.printf("Time with 2 Threads: %5.10f sec\n", (timeOut / 1e9));
        printMatrix(matrixC);

        // 4 Threads
        matrixC = new float[m][p];

        timeIn = System.nanoTime();
        new MaxtrixMultiplication().matMult(ph, matrixA, matrixB, matrixC, m / 4, n, p / 4);
        new MaxtrixMultiplication().matMult(ph, matrixA, matrixB, matrixC, m / 2, n, p / 2);
        new MaxtrixMultiplication().matMult(ph, matrixA, matrixB, matrixC, m * 3 / 4, n, p * 3 / 4);
        new MaxtrixMultiplication().matMult(ph, matrixA, matrixB, matrixC, m, n, p);
        timeOut = System.nanoTime() - timeIn;
        System.out.printf("Time with 4 Threads: %5.10f sec\n", (timeOut / 1e9));
        printMatrix(matrixC);
    }

    public void matMult(Phaser ph, float[][] A, float[][] B, float[][] C, int m2, int n, int p2) {
        ph.register();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int row = 0; row < m2; row++) {
                    for (int col = 0; col < p2; col++) {
                        C[row][col] = 0;
                        for (int k = 0; k < n; k++) {
                            C[row][col] += A[row][k] * B[k][col];
                        }
                    }
                }
                ph.arriveAndDeregister();
            }
        }).start();
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

    public static int[][] partialMatrix (int[][] current, boolean upper, int start, int end) {
        int[][] temp = new int [end - start][end - start];

        if (upper)
            for (int i = 0; i < current.length / 2; i++)
                temp[i] = Arrays.copyOfRange(current[i], start, end);

        else
            for (int i = 0; i < current.length / 2; i++)
                temp[i] = Arrays.copyOfRange(current[i + current.length / 2], start, end);

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
        System.out.println();
    }
}
