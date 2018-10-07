import java.util.concurrent.ThreadLocalRandom;

public class MaxtrixMultiplication {
    public static void main(String[] args) {
        float[][] matrixA, matrixB, matrixC;
        int m, n, p;

        m = n = p = 16;
        matrixA = fillMatrix(m, n);
        matrixB = fillMatrix(n, p);
        matrixC = new float[m][p];

        matMultThread1(matrixA, matrixB, matrixC, m, n, p);

        matMultThread2(matrixA, matrixB, matrixC, m, n, p);
        
    }

    public static void matMultThread1 (float[][] A, float[][] B, float[][] C, int m, int n, int p) {

        Thread t0 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int row = 0; row < m; row++) {
                    for (int col = 0; col < p; col++) {
                        C[row][col] = 0;
                        for (int k = 0; k < n; k++) {
                            C[row][col] += A[row][k] * B[k][col];
                        }
                    }
                }
            }
        });

        long timeIn = System.nanoTime();
        t0.start();
        long timeOut = System.nanoTime() - timeIn;
        System.out.printf("Time with 1 Threads: %5.10f sec\n", (timeOut / 1e9));
    }
    public static void matMultThread2 (float[][] A, float[][] B, float[][] C, int m, int n, int p) {

        // Starts from C[0][0] to C[m/2][p/2]
        Thread t0 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int row = 0; row < m / 2; row++) {
                    for (int col = 0; col < p / 2; col++) {
                        C[row][col] = 0;
                        for (int k = 0; k < n; k++) {
                            C[row][col] += A[row][k] * B[k][col];
                        }
                    }
                }
            }
        });

        // Starts from C[m/2][p/2] to C[m][p]
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int row = m / 2; row < m; row++) {
                    for (int col = p / 2; col < p; col++) {
                        C[row][col] = 0;
                        for (int k = 0; k < n; k++) {
                            C[row][col] += A[row][k] * B[k][col];
                        }
                    }
                }
            }
        });

        long timeIn = System.nanoTime();
        t0.start();
        t1.start();
        long timeOut = System.nanoTime() - timeIn;
        System.out.printf("Time with 2 Threads: %5.10f sec\n", (timeOut / 1e9));
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
