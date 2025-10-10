package com.stemi.libs;

public class SavitzkyGolay {

    /**
     * Generate Savitzky-Golay filter coefficients.
     * @param windowSize Must be an odd number.
     * @param polyOrder  Polynomial order (e.g., 1 for linear).
     * @return Array of convolution coefficients.
     */
    public static double[] savitzkyGolayCoefficients(int windowSize, int polyOrder) {
        if (windowSize % 2 == 0 || windowSize < 1) {
            throw new IllegalArgumentException("Window size must be a positive odd number.");
        }
        if (polyOrder >= windowSize) {
            throw new IllegalArgumentException("Polynomial order must be less than window size.");
        }

        int halfWindow = windowSize / 2;
        double[][] A = new double[windowSize][polyOrder + 1];

        // Build design matrix A
        for (int i = -halfWindow; i <= halfWindow; i++) {
            for (int j = 0; j <= polyOrder; j++) {
                A[i + halfWindow][j] = Math.pow(i, j);
            }
        }
        double[][] B;
        B = new double[polyOrder + 1][windowSize];

        // Compute pseudo-inverse: (Aᵀ A)⁻¹
        double[][] transposeTemp = transpose(A);
//        Utility ut = new Utility();
//        ut.viewData(B);
        double[][] ATA = multiply(transpose(A), A);
        double[][] ATAInv = invert(ATA);
        double[][] ATAInvAT = multiply(ATAInv, transpose(A));

        // The first row of ATAInvAT is the smoothing filter for the 0th derivative
        return ATAInvAT[0];
    }

    // Matrix transpose
    private static double[][] transpose(double[][] m) {
        double[][] result = new double[m[0].length][m.length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                result[j][i] = m[i][j];
        return result;
    }

    // Matrix multiplication
    private static double[][] multiply(double[][] a, double[][] b) {
        int rows = a.length, cols = b[0].length, shared = a[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                for (int k = 0; k < shared; k++)
                    result[i][j] += a[i][k] * b[k][j];
        return result;
    }

    // Matrix inversion (only for small square matrices, use optimized library for larger)
    private static double[][] invert(double[][] matrix) {
        int n = matrix.length;
        double[][] augmented = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(matrix[i], 0, augmented[i], 0, n);
            augmented[i][i + n] = 1.0;
        }

        // Gauss-Jordan elimination
        for (int i = 0; i < n; i++) {
            double diag = augmented[i][i];
            for (int j = 0; j < 2 * n; j++) {
                augmented[i][j] /= diag;
            }
            for (int k = 0; k < n; k++) {
                if (k == i) continue;
                double factor = augmented[k][i];
                for (int j = 0; j < 2 * n; j++) {
                    augmented[k][j] -= factor * augmented[i][j];
                }
            }
        }

        double[][] inverse = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(augmented[i], n, inverse[i], 0, n);
        }
        return inverse;
    }

   /* public static void main(String[] args) {
        int window = 501;
        int order = 1;

        double[] coeffs = savitzkyGolayCoefficients(window, order);
        System.out.println("Savitzky-Golay coefficients (first 10): " + Arrays.toString(Arrays.copyOf(coeffs, 10)));
    }*/
}

