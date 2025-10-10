package com.stemi.libs;
public class CustomQR {

    // Class to return both Q and R matrices
    public static class QRResult {
        public double[][] Q;
        public double[][] R;

        public QRResult(double[][] Q, double[][] R) {
            this.Q = Q;
            this.R = R;
        }
    }

    public static QRResult customQR(double[][] A) {
        int m = A.length;
        int n = A[0].length;

        double[][] Q = identityMatrix(m);
        double[][] R = copyMatrix(A);

        for (int k = 0; k < Math.min(m - 1, n); k++) {
            // Extract x = R[k:m][k]
            double[] x = new double[m - k];
            for (int i = 0; i < m - k; i++) {
                x[i] = R[k + i][k];
            }

            double normX = norm(x);
            double[] v;

            if (normX == 0) {
                v = x.clone();
            } else {
                double sigma = -Math.signum(x[0]);
                if (sigma == 0) sigma = -1;

                v = x.clone();
                v[0] -= sigma * normX;
                v = normalize(v);
            }

            // Apply Householder reflection to R
            for (int j = k; j < n; j++) {
                double dot = 0;
                for (int i = 0; i < v.length; i++) {
                    dot += v[i] * R[k + i][j];
                }
                for (int i = 0; i < v.length; i++) {
                    R[k + i][j] -= 2 * v[i] * dot;
                }
            }

            // Apply reflection to Q
            for (int i = 0; i < m; i++) {
                double dot = 0;
                for (int j = 0; j < v.length; j++) {
                    dot += Q[i][k + j] * v[j];
                }
                for (int j = 0; j < v.length; j++) {
                    Q[i][k + j] -= 2 * dot * v[j];
                }
            }
        }

        return new QRResult(Q, R);
    }

    // Identity matrix creation
    private static double[][] identityMatrix(int size) {
        double[][] I = new double[size][size];
        for (int i = 0; i < size; i++) {
            I[i][i] = 1.0;
        }
        return I;
    }

    // Deep copy of a matrix
    private static double[][] copyMatrix(double[][] A) {
        int m = A.length;
        int n = A[0].length;
        double[][] B = new double[m][n];
        for (int i = 0; i < m; i++) {
            System.arraycopy(A[i], 0, B[i], 0, n);
        }
        return B;
    }

    // Euclidean norm of a vector
    private static double norm(double[] x) {
        double sum = 0.0;
        for (double val : x) {
            sum += val * val;
        }
        return Math.sqrt(sum);
    }

    // Normalize a vector
    private static double[] normalize(double[] v) {
        double norm = norm(v);
        if (norm == 0) return v.clone();
        double[] res = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            res[i] = v[i] / norm;
        }
        return res;
    }

    // Print matrix for debugging
    public static void printMatrix(String name, double[][] M) {
        System.out.println(name + ":");
        for (double[] row : M) {
            for (double val : row) {
                System.out.printf("%8.4f ", val);
            }
            System.out.println();
        }
    }

}
