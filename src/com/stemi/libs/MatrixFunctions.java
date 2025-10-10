package com.stemi.libs;

public class MatrixFunctions {

    public double[][] multiplyMatrices(int row1, int col1, double[][] A, int row2, int col2, double[][] B) {

            int i, j, k;
      
            // Check if multiplication is Possible
            if (row2 != col1) {
                System.out.println("Multiplication Not Possible");
                return new double[1][1];
            }

            // Matrix to store the result
            // The product matrix will
            // be of size row1 x col2
            double C[][] = new double[row1][col2];
            // Multiply the two matrices
            for (i = 0; i < row1; i++) {
                for (j = 0; j < col2; j++) {
                    for (k = 0; k < row2; k++)
                        C[i][j] += A[i][k] * B[k][j];
                }
            }

            // Print the result
//            System.out.println("Resultant Matrix:");
            return C;
    }

    public double[] matrixMultiplication(double[][] arr1, double[] arr2)  {
        // arr1 = m * n;   arr2 = n * q
        int m = arr1.length;
        int q = 1;
        double[] result = new double[m];
        if (arr1[0].length != arr2.length)  {
            System.out.println("Multiplication not possible");
        } else{
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < m; j++) {
                    result[i] = result[i] + arr1[i][j] * arr2[j];
                }
            }
        }
        return result;
    }


    public void printMatrix(int[][] M, int rowSize,int colSize)
    {
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++)
                System.out.print(M[i][j] + " ");
            System.out.println();
        }
    }

    public void printMatrix(double[][] M, int rowSize,int colSize)
    {
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++)
                System.out.print(M[i][j] + ",   ");
            System.out.println();
        }
    }

    public double[][] transpose(double[][] A)
    {
        int M = A.length;
        int N = A[0].length;
        double[][] B = new double[N][M];
        int i, j;
        for (i = 0; i < N; i++) {
            for (j = 0; j < M; j++) {
                B[i][j] = A[j][i];
            }
        }
        return B;
    }

    public double[][] lastRowInto1stColumn(double[][] A)
    {
        int M = A.length;
        int N = A[0].length;
        double[][] B = new double[N][M];
        int i, j;
        for (i = 0; i < N; i++) {
            for (j = M-1; j >=0; j--) {
                B[i][M-1-j] = A[j][M-1-i];
            }
        }
        return B;
    }

}
