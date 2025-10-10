package com.stemi;

import com.stemi.libs.CustomQR;
import com.stemi.libs.FastFourierTransform;
import com.stemi.libs.MatlabInbuiltFunctions;
import com.stemi.libs.MatrixFunctions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UpdatingSavitzkyGolay {

    public boolean checkForPointFiveHzNoise(double[][] polySubEcg) {
        FastFourierTransform fftObj = new FastFourierTransform(500);
        MatlabInbuiltFunctions mf = new MatlabInbuiltFunctions();
        Filters filters = new Filters();
        FastFourierTransform ft = new FastFourierTransform();
        LoaderHelper ldh = new LoaderHelper();
        double[] peakValueAndFoundPeakFlag05= new double[2];
        boolean found05HzNoise = false;
        double freqRange = 0.2;         // +/- tolerance around 0.5 Hz (adjust as needed)
        double thresholdAmplitude = 1.0 * 100000;  // amplitude threshold (adjust as needed)
        int len = polySubEcg[0].length;
        double N2 = Math.pow(2, fftObj.customNextPow2(len));
//        System.out.println("N2 ==========  "+N2);
        for (int leadIdx = 0; leadIdx < polySubEcg.length; leadIdx++) {
//        for (int leadIdx = 0; leadIdx < 1; leadIdx++) {
            double[][] fftNew = fftObj.customSingleSidedSpectrumOnlyForFft(polySubEcg[leadIdx]);
            double[] magFft = new double[fftNew[0].length];
            for (int i = 0; i < fftNew[0].length; i++) {
                magFft[i] = mf.absolute(fftNew[0][i], fftNew[1][i]);
            }

            double[] freqAxis = new double[(int) N2];
            for (int i = 0; i < N2; i++) {
                freqAxis[i] = (double) Math.round( ((double) i) * (500.0 / N2) *10000) / 10000 ;
//                System.out.println(freqAxis[i]);
            }
            peakValueAndFoundPeakFlag05 = filters.hasNearPeakFrequency(freqAxis, magFft, 0.3
                    , freqRange, thresholdAmplitude);
            if (peakValueAndFoundPeakFlag05[0] == 1) {
                found05HzNoise = true;
                System.out.println("Lead " + leadIdx + " 0.5 Hz interference found (peak0.5 = "
                        + peakValueAndFoundPeakFlag05[1]);
                break;
            }
        }
            return found05HzNoise;
    }

/*

    public double[] calculateSavGolCoefficients(int windowSize, int polyOrder) {
        if (windowSize%2 == 0)
            System.out.println("Window Size must be an odd integer");
        if (polyOrder >= windowSize)
            System.out.println("PolyOrder must be less than window size");
//        half_w = floor(window_size / 2);
//        xvec   = (-half_w : half_w)';  % column vector, length=window_size
        int halfWindow = (int) Math.floor( windowSize / 2);
        double[] xvec = new double[windowSize];
        for (int i = 0; i < windowSize; i++) {
            xvec[i] = -halfWindow + i;
        }
        double[][] A, B;
        //        Build Vandermonde matrix A
        A = new double[polyOrder+1][windowSize];
        B = new double[polyOrder+1][windowSize];
        for (int k = 0; k <= polyOrder; k++) {
            for (int l = 0; l < xvec.length; l++) {
                A[k][l] = Math.pow(xvec[l], polyOrder - k);
            }
        }
        double[] temp =  A[0];
        B[0] = A[1];
        B[1] = temp;
        RealMatrix realMatrixA = new Array2DRowRealMatrix(B);
        //  Pseudo-inverse for least-squares
        SingularValueDecomposition svd = new SingularValueDecomposition(realMatrixA);
//        DecompositionSolver solver = svd.getSolver();

        RealMatrix u = svd.getUT();
        RealMatrix sigmaOld = svd.getS();
        double[] sigma = svd.getSingularValues();
        RealMatrix vt = svd.getVT();
        double[][] UU = vt.getData();
        double[][] V = u.getData();
        double tol = Math.max(A.length, A[0].length) * Math.ulp(svd.getNorm());
        int r = 0;
        for (double v : sigma)
            r+= (v > tol) ? 1 : 0;

        MatrixFunctions mF = new MatrixFunctions();
        for (int i = 0; i < sigma.length; i++) {
            sigma[i] = 1/sigma[i];
        }
        double[][] X = new double[V.length][V[0].length];
        for (int i = 0; i < sigma.length; i++) {
            for (int j = 0; j < V.length; j++) {
                X[i][j] = V[i][j] * sigma[i];
            }
        }
        RealMatrix xReal = new Array2DRowRealMatrix(X);
        RealMatrix uu = new Array2DRowRealMatrix(UU);
        RealMatrix tU = uu.transpose();
        RealMatrix c = tU.multiply(xReal);
        c = c.transpose();
        double val;
        double[] newArr = new double[c.getData()[0].length];
        for (int i = 0; i < c.getData()[0].length; i++) {
            val = Math.round(c.getData()[0][i] * 10000);
            newArr[i] = val / 10000;
        }
//        System.out.println(MatrixUtils.checkMultiplicationCompatible(V, sigma));
//        Compute the pseudo-inverse of S
//        S_pinv = zeros(size(A,2), size(A,1)); % Create transposed zero matrix
//        double[][] sPinv = new double[][]

//        System.out.println(svd.getConditionNumber());
//        System.out.println(svd.getInverseConditionNumber());
//        System.out.println(svd.getNorm());
//        System.out.println(svd.ge);
//        System.out.println("u======   "+u.getData().length);
//        System.out.println("u[1] ======   "+u.getData()[0].length);
        return newArr;
    }
*/

 /*   private boolean[][] customBsxFunLE(int[] rowIdx, int[] colIdx, int k) {
        int rows = rowIdx.length;
        int cols = colIdx.length;
        boolean[][] result = new boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = rowIdx[i] <= (colIdx[j] + k);
            }
        }
        return result;
    }
*/

    public double[] customCalculateSavGolCoefficients(int windowSize, int polyOrder) {
        if (windowSize % 2 == 0)
            System.out.println("Window Size must be an odd integer");
        if (polyOrder >= windowSize)
            System.out.println("PolyOrder must be less than window size");
        int halfWindow = (int) Math.floor(windowSize / 2);
        double[] xvec = new double[windowSize];
        for (int i = 0; i < windowSize; i++) {
            xvec[i] = -halfWindow + i;
        }
        double[][] A, B;
        //        Build Vandermonde matrix A
        A = new double[polyOrder + 1][windowSize];
        B = new double[polyOrder + 1][windowSize];
        for (int k = 0; k <= polyOrder; k++) {
            for (int l = 0; l < xvec.length; l++) {
                A[k][l] = Math.pow(xvec[l], polyOrder - k);
            }
        }
        double[] temp = A[0];
        B[0] = A[1];
        B[1] = temp;
        Map<String, double[][]> mapSUV = customSvd(B);
        double[][] S = mapSUV.get("S");
        double[][] U = mapSUV.get("U");
        double[][] V = mapSUV.get("V");
        double[] bNormed = customNorm(B);

        double tol = Math.max(B.length, B[0].length) * Math.ulp(bNormed[0]);
        double tol2 = Math.max(B.length, B[0].length) * Math.ulp(bNormed[1]);
//        System.out.println(tol);
//        System.out.println(tol2);
        double[][] sPinv = new double[B[0].length][B.length];
        for (int i = 0; i < B.length; i++) {
            if (S[i][i] > tol)
                sPinv[i][i] = 1 / S[i][i];
        }
        MatrixFunctions mt = new MatrixFunctions();
        sPinv = mt.transpose(sPinv);
        double[][] pinvATemp = mt.multiplyMatrices(V.length, V[0].length, V, sPinv.length, sPinv[0].length, sPinv);
        double[][] pinvA = mt.multiplyMatrices(pinvATemp.length, pinvATemp[0].length,pinvATemp, U.length,U[0].length,U);
        return pinvA[0];
//        com.arrthymia.LoaderHelper ldh = new com.arrthymia.LoaderHelper();
//        ldh.viewData(pinvA);
    }

    private Map<String, double[][]> customSvd(double[][] a) {
        int m = a.length;
        int n = a[0].length;
        MatrixFunctions mt = new MatrixFunctions();
        double[][] aTranspose = mt.transpose(a);
//        System.out.println(aTranspose.length+"          ,           "+a.length);
        double[][] aAtMult = mt.multiplyMatrices(aTranspose.length, aTranspose[0].length, aTranspose, a.length, a[0].length, a);
        double[][] aTAMult = mt.multiplyMatrices(a.length, a[0].length, a, aTranspose.length, aTranspose[0].length, aTranspose);
// Cannot view the whole 501 * 501 matrix through loaderHelper view() method, If needed
//  then use view by using 1D array. Ex - aAtMult[0], aAtMult[1]
        Map<String, double[][]> map = customEigen(aTAMult);
        double[][] V = map.get("v");
        double[][] Dv = map.get("d");
        System.out.println("Custom Eigne 500");
        Map<String, double[][]> map2 = customEigenfor500(aAtMult);
        double[][] U = map2.get("u");
        double[][] Du = map2.get("du");
        LoaderHelper ldh = new LoaderHelper();
//        ldh.viewData(map2.get("d")[0]);
        double[] sv = new double[map.get("d").length];
        for (int i = 0; i < map.get("d").length; i++) {
            for (int j = 0; j < map.get("d")[0].length; j++) {
                if (i == j)
                    sv[i] = Math.sqrt(map.get("d")[i][j]);
            }
        }
        sorting(sv, "descen");
        int k = Math.min(m, n);
        double[][] S = new double[m][n];
        for (int i = 0; i < S.length; i++) {
            for (int j = 0; j < S[0].length; j++) {
                if (i == j)
                    S[i][j] = (double) Math.round(sv[i] * 10000 ) / 10000;
            }
        }
        for (int i = 0; i < V.length; i++) {
            double norm = customNorm(V[i]);
            for (int j = 0; j < V[0].length; j++) {
                V[i][j] = (double) Math.round((V[i][j] / norm) * 10000) / 10000;
            }
        }
        for (int i = 0; i < U.length; i++) {
            double norm = customNorm(U[i]);
            for (int j = 0; j < U[0].length; j++) {
                U[i][j] = (double) Math.round((U[i][j] / norm) * 10000) / 10000;
            }
        }
        map.clear();
        map.put("U", U);
        map.put("V", V);
        map.put("S", S);
        return map;
//        ldh.viewData(U[499]);


//        customEigen(aAtMult);
//        double[] basis = getBasis(aTAMult);

//        double[][] tempArr = {{1,2,3}, {4,5,6}, {7,8,9}};
//        double[][] tempArrRowReverse = mt.lastRowInto1stColumn(aAtMult);
//        double[][] aAtMultRowReverse = mt.lastRowInto1stColumn(aAtMult);
//        mt.printMatrix(tempArrRowReverse, tempArrRowReverse.length, tempArrRowReverse[0].length);
//        System.out.println(aAtMult.length+"   ,   "+aAtMult[500].length);
//        double[][] aAtMultTranspose = mt.transpose(aAtMult);
//        double[][] atAMult = mt.multiplyMatrices(a.length, a[0].length, a, aTranspose.length, aTranspose[0].length, aTranspose);


//        com.arrthymia.LoaderHelper ldh = new com.arrthymia.LoaderHelper();
//        ldh.viewData(aAtMult);
//        System.out.println("==========================================================================");
//        System.out.println();
//        ldh.viewData(basis);
//        System.out.println(multiplyOne.length+"         ,         "+multiplyOne[0].length);
    }

    private double customNorm(double[] arr) {
        double sum = 0.0;
        for(double x : arr)
            sum+= x * x;
        return Math.sqrt(sum);
    }

    private double[] customNorm(double[][] arr) {
        double[] normArr = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            double sum = 0;
            for (int j = 0; j < arr[0].length; j++)
                sum += arr[i][j] * arr[i][j];
            normArr[i] = Math.sqrt(sum);
        }
        return normArr;
    }

    private double[] sorting(double[] arr, String type) {
        double tempVar;
        if (type.equalsIgnoreCase("descen")) {
            for (int i = 0; i < arr.length - 1; i++) {
                for (int j = 0; j < arr.length - 1; j++) {
                    if (arr[i] < arr[j + 1]) {
                        tempVar = arr[j + 1];
                        arr[j + 1] = arr[i];
                        arr[i] = tempVar;
                    }
                }
            }
        } else {
            for (int i = 0; i < arr.length - 1; i++) {
                for (int j = 0; j < arr.length - 1; j++) {
                    if (arr[i] > arr[j + 1]) {
                        tempVar = arr[j + 1];
                        arr[j + 1] = arr[i];
                        arr[i] = tempVar;
                    }
                }
            }
        }
        return arr;
    }

    private Map<String, double[][]> customEigen(double[][] a) {
        int n = a.length;
        double[][] v = customEye(n);
        double[][] q = new double[v.length][v[0].length];
        double[][] r = new double[v.length][v[0].length];
        double[][] aNew = new double[a.length][a[0].length];
        double[][] vNew = new double[v.length][v[0].length];
        int maxIteration = 100;
        double tol = 0.00000001;
        for (int i = 0; i < maxIteration; i++) {
//        for (int i = 0; i < 1; i++) {
            double mu = a[n-1][n-1];
            // customEye here is commented b'coz for every iteration customEye parameters is n which is constant,
            // so we can use value of v here.
//            double[][] eye = customEye(n);
            for (int j = 0; j < v.length; j++) {
                for (int k = 0; k < v[0].length; k++) {
                    r[j][k] = a[j][k] - (mu * v[j][k]);
                }
            }
            CustomQR.QRResult qr = CustomQR.customQR(r);
//            CustomQR.printMatrix("Q",qr.Q);
            q = qr.Q;
            r = qr.R;
            for (int j = 0; j < a.length; j++) {
                for (int k = 0; k < a[0].length; k++) {
                    aNew[j][k] = r[j][k] * q[j][k] + mu * v[j][k];
                    vNew[j][k] = v[j][k] * q[j][k];
//                    System.out.print(aNew[j][k]+",    ");
                }
//                System.out.println();
            }
//            com.arrthymia.LoaderHelper ldh = new com.arrthymia.LoaderHelper();
//            ldh.viewData(aNew[0]);
            double[][] offDiag = customTrialTwo(aNew, -1);
            double[] offDiagAbs = new double[offDiag.length * offDiag[0].length];
            double max = offDiag[0][0];
            for (int j = 0; j < offDiag.length; j++) {
                for (int k = 1; k < offDiag[0].length; k++) {
                    if (max > offDiag[j][k])
                        max = offDiag[j][k];
                }
            }
            if (max < tol)
                break;
        }
        double[][] d = customDiagonal(diagonal(aNew));
        Map<String, double[][]> map = new HashMap<String, double[][]>();
        map.put("v", vNew);
        map.put("d", d);
        return map;
//        com.arrthymia.LoaderHelper ldh = new com.arrthymia.LoaderHelper();
//        ldh.viewData(D);
    }

    private double[] diagonal(double[][] arr) {
        if (arr.length == arr[0].length) {
        double[] diag = new double[arr.length];
            for (int i = 0; i < arr.length; i++)
                diag[i] = arr[i][i];
            return diag;
        } else {
            System.out.println("Square array required.");
            return new double[1];
        }
    }

    private double[][] customEye(int n) {
        double[] ones = new double[n];
        //            System.out.println(ones[i]);
        Arrays.fill(ones, 1);
        return customDiagonal(ones);
    }

    private double[][] customDiagonal(double[] a) {
//        com.arrthymia.LoaderHelper ldh = new com.arrthymia.LoaderHelper();
//        ldh.viewData(a);
        int m = a.length;
        double[][] d = new double[m][m];
        for (int i = 0; i < m; i++) {
            d[i][i] = a[i];
        }
        return d;
    }

    private double[] customDiagonal(double[][] a ) {
        int m = a.length;
        int n = a.length;
        if (m == n) {
            double[] d = new double[m];
            for (int i = 0; i < m; i++) {
                d[i] = a[i][i];
//                System.out.println(d[i]);
            }
            return d;
        }
        return new double[1];
    }

    private double customTrial(double[][] aNew, int k) {
        int r = aNew[0].length;
        int c = aNew.length;
        int[] rows = new int[r];
        int[] cols = new int[c];
        for (int j = 0; j < r; j++) {
            rows[j] = j;
        }
        MatrixFunctions mt = new MatrixFunctions();
        for (int j = 0; j < c; j++) {
            cols[j] = j+k;
        }
        return 0.0;
    }

    private double[][] customTrialTwo(double[][] aNew, int k) {
        int r = aNew.length;
        int c = aNew[0].length;
        int[] rows = new int[r];
        int[] cols = new int[c];
        for (int j = 0; j < r; j++) {
            rows[j] = j+1;
        }
        for (int j = 0; j < c; j++) {
            cols[j] = j+1;
        }
         int[][] mask = customNdGrid(rows, cols, k);
        double[][] L = new double[r][c];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                L[i][j] = (double) mask[i][j] * aNew[i][j];
            }
        }
        return L;
    }

    private int[][] customNdGrid(int[] xOld, int[] yOld, int k) {
        int m = xOld.length;
        int n = yOld.length;
        int[][] x = new int[m][n];
        int[][] y = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                x[i][j] = xOld[i];
                y[i][j] = yOld[j];
            }
        }
        return checkMask(x, y, k);
    }

    private int[][] checkMask(int[][] x, int[][] y, int k) {
        int[][] bool = new int[x.length][x[0].length];
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[0].length; j++) {
                if ( x[i][j] <= (y[i][j] + k) )
                    bool[i][j] = 1;
            }
        }
        return bool;
    }

  /*  private void customBsxFunc(int[][] rows, int[][] cols) {
        int mA = rows.length;
        int nA = rows[0].length;
        int mB = cols.length;
        int nB = cols[0].length;
        int[][] colsNew = new int[1][2];
        int[][] rowsNew = new int[1][2];
        if (mA==1 && mB>1) {
            rows = customRepmat(rows, mB, 1);
        }
        else if (mB==1 && mA>1)     {
            cols = customRepmat(cols, mA,1);
            colsNew[0][0] = cols[1][0];
            colsNew[0][1] = cols[1][1];
            customRepmatTwo(cols, mA, 1);
        }
//        if (nA==1 && nB>1) {
//            rows = customRepmat(rows, 1, nB);
//            rowsNew[0][0] = rows[1][0];
//            rowsNew[0][1] = rows[1][0];
//        }
        else if (nB==1 && nA>1)
            cols = customRepmat(cols, 1, nA);

    }
*/
    private int[][] customRepmat(int[][] a, int m, int n) {
        int rows = a.length;
        int cols = a[0].length;
        int[][] B = new int[rows * m][cols * n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        B[i * rows + r][j * cols + c] = a[r][c];
                    }
                }
            }
        }
        return B;
    }

    private void customRepmatTwo(int[][] a , int m, int n){
        int[][] b1 = new int[m][m];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                b1[i][j] = a[a.length-1][j];
            }
        }

        LoaderHelper ldh = new LoaderHelper();
//        ldh.viewData(a);

    }

    private Map<String, double[][]> customEigenfor500(double[][] a) {
        int n = a.length;
        double[][] v = customEye(n);
        double[][] q = new double[v.length][v[0].length];
        double[][] r = new double[v.length][v[0].length];
        double[][] aNewFinal = new double[a.length][a[0].length];
        double[][] vNewFinal = new double[v.length][v[0].length];
        int maxIteration = 100;
        double tol = 0.00000001;
//        for (int i = 0; i < maxIteration; i++) {
        for (int i = 0; i < maxIteration; i++) {
            double[][] aNew = new double[a.length][a[0].length];
            double[][] vNew = new double[a.length][a[0].length];
            double mu = a[n-1][n-1];
//            System.out.println("mu=======   "+mu);
            double[][] eye = customEye(n);
            double[][] rOld = new double[v.length][eye[0].length];
            for (int j = 0; j < eye.length; j++) {
                for (int k = 0; k < eye[0].length; k++) {
                    rOld[j][k] = a[j][k] - (mu * eye[j][k]);
                }
            }
            CustomQR.QRResult qr = CustomQR.customQR(rOld);
//            CustomQR.printMatrix("Q",qr.Q);
            q = qr.Q;
            r = qr.R;
            MatrixFunctions mt = new MatrixFunctions();
            q = mt.transpose(q);
            r = mt.transpose(r);

//            if (i == 1) {
//                com.arrthymia.LoaderHelper ldh = new com.arrthymia.LoaderHelper();
//                ldh.viewData(q[0]);
//            }
            double[][] vT = new double[v[0].length][v.length];
            double[][] qrMultiply = mt.multiplyMatrices(q.length, q[0].length, q, r.length, r[0].length, r);
//            if (i == 1) {
//                com.arrthymia.LoaderHelper ldh = new com.arrthymia.LoaderHelper();
//                ldh.viewData(qrMultiply[0]);
//            }
            double[][] qReal = mt.transpose(q);
//            vNew = mt.multiplyMatrices(v.length, v[0].length, v, q.length, q[0].length, q);
            vNew = mt.multiplyMatrices(v.length, v[0].length, v, qReal.length, qReal[0].length, qReal);
            double[][] vNewTranspose = mt.transpose(vNew);
//            if (i ==1 ) {
//                com.arrthymia.LoaderHelper ldh = new com.arrthymia.LoaderHelper();
//                ldh.viewData(vNewTranspose[1]);
//            }
//            if (i == 1){
////                v = mt.transpose(v);
//                vT = mt.transpose(v);
//                vNew = mt.multiplyMatrices(vT.length, vT[0].length, vT, q.length, q[0].length, q);
//            }
            for (int j = 0; j < a.length; j++) {
                for (int k = 0; k < a[0].length; k++) {
                    aNew[j][k] = qrMultiply[j][k] + mu * eye[j][k];
//                    System.out.print(aNew[j][k]+",    ");
                }
//                System.out.println();
            }
            double[][] offDiag = customTrialTwo(aNew, -1);
            offDiag = mt.transpose(offDiag);
            double max = 0;
            for (int j = 0; j < offDiag.length; j++) {
                for (int k = 0; k < offDiag[0].length; k++) {
                    max = Math.max(max, Math.abs(offDiag[j][k]));
                }
            }
            if (max < tol) {
                System.out.println("Max value :   "+max+"    Iteration :   "+i);
                aNewFinal = aNew;
//                vNewFinal = vNew;
                vNewFinal = vNewTranspose;
                break;
            }
            a = aNew;
            v = vNew;
            if (i == maxIteration-1) {
                System.out.println("    Iteration :   "+i);
                aNewFinal = aNew;
                vNewFinal = vNewTranspose;
            }
        }
        double[][] d = customDiagonal(diagonal(aNewFinal));
//        com.arrthymia.LoaderHelper ldh = new com.arrthymia.LoaderHelper();
//        ldh.viewData(d[500]);
        Map<String, double[][]> map = new HashMap<String, double[][]>();
        map.put("u", vNewFinal);
        map.put("du", d);

        return map;
    }

}
