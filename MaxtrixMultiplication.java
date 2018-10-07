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

        long timeIn, timeOut;

        // 1 Thread
        matrixC = new float[m][p];
        timeIn = System.nanoTime();

        new MaxtrixMultiplication().matMult(ph, matrixA, matrixB, matrixC, 0, m, n, 0, p);

        timeOut = System.nanoTime() - timeIn;
        System.out.printf("Time with 1 Threads: %5.10f sec\n", (timeOut / 1e9));
        printMatrix(matrixC);

        // 2 Threads
        matrixC = new float[m][p];
        timeIn = System.nanoTime();

        // Quad 1 & 3
        new MaxtrixMultiplication().matMult(ph, matrixA, matrixB, matrixC, 0, m, n, 0, p / 2);
        // Quad 2 & 4
        new MaxtrixMultiplication().matMult(ph, matrixA, matrixB, matrixC, 0, m, n, p / 2, p);

        timeOut = System.nanoTime() - timeIn;
        System.out.printf("Time with 2 Threads: %5.10f sec\n", (timeOut / 1e9));
        printMatrix(matrixC);
        
        // 4 Threads
        matrixC = new float[m][p];
        timeIn = System.nanoTime();

        // Quad 1
        new MaxtrixMultiplication().matMult(ph, matrixA, matrixB, matrixC, 0, m / 2, n, 0, p / 2);
        // Quad 2
        new MaxtrixMultiplication().matMult(ph, matrixA, matrixB, matrixC, 0, m / 2, n, p / 2, p);
        // Quad 3
        new MaxtrixMultiplication().matMult(ph, matrixA, matrixB, matrixC, m / 2, m, n, 0, p / 2);
        // Quad 4
        new MaxtrixMultiplication().matMult(ph, matrixA, matrixB, matrixC, m / 2, m, n, p / 2, p);

        timeOut = System.nanoTime() - timeIn;
        System.out.printf("Time with 4 Threads: %5.10f sec\n", (timeOut / 1e9));
        printMatrix(matrixC);
    }

    public void matMult(Phaser ph, float[][] A, float[][] B, float[][] C, int m1, int m2, int n, int p1, int p2) {
        ph.register();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int row = m1; row < m2; row++) {
                    for (int col = p1; col < p2; col++) {
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
