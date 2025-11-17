package com.stemi.libs;

import java.util.ArrayList;

public class FastFourierTransform {
    private double[][] arr;
    private double samplingRate = 500;

    public FastFourierTransform() {};
    public FastFourierTransform(double[][] arr) {
        this.arr = arr;
    }
    public FastFourierTransform(int fs) {
        this.samplingRate = fs;
    }

    public double[] analyize() {
        double[] sampledData = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            sampledData[i] = arr[i][0];
        }
        return sampledData;
    }

    public void display2(double[] mainArr)
    {
            for (double ans : mainArr)
                System.out.println(ans);
    }

    public double[] customDiff(double[] sampledData) {
//        System.out.println(sampledData[0][0]);
        double[] differences = new double[sampledData.length];
        for (int i = 1; i < sampledData.length; i++) {
            differences[i] = sampledData[i] - sampledData[i - 1];
        }
        return differences;
    }

    public double[][] customSingleSidedSpectrumOnlyForFft(double[] differences) {
        int N = differences.length;
        int N2 = (int) Math.pow(2, customNextPow2(N));
//        System.out.println("N2========= " + N2);
        double[] newDifferences = new double[N2];
        for (int i = 0; i < differences.length; i++) {
            newDifferences[i] = differences[i];
        }
//        return newDifferencences;
        double[] fft = fastFourierTransform(newDifferences);
        double[][] fftRealAndImaginaryData = seperateFFTRealAndImaginaryData(fft);
//  Now issue is unsolved that provides factor ::: value of factor is different for N=4 outside and inside the function fft in matlab and so in java
//        double[] fftReal = fftRealAndImaginaryData[0];
//        double[] fftImag = fftRealAndImaginaryData[1];
        return fftRealAndImaginaryData;
    }

    public double[][] customSingleSidedSpectrum(double[] differences) {
        int N = differences.length;
        int N2 = (int) Math.pow(2, customNextPow2(N));
        System.out.println("N2========= "+N2);
        double[] newDifferences = new double[N2];
        for (int i = 0; i < differences.length; i++) {
            newDifferences[i] = differences[i];
        }
//        return newDifferencences;
        double[] fft = fastFourierTransform(newDifferences);
        double[][] fftRealAndImaginaryData = seperateFFTRealAndImaginaryData(fft);
//  Now issue is unsolved that provides factor ::: value of factor is different for N=4 outside and inside the function fft in matlab and so in java
        double[] fftReal = fftRealAndImaginaryData[0];
        double[] fftImag = fftRealAndImaginaryData[1];
//        Utility ut = new Utility();
//        ut.viewData(fftReal);

//        Normalize the FFT output
        double[] P2 = normaliseFFT(fftReal,fftImag, N2);
//        Calculate points for single-sided spectrum
        int numPoints = (N2/2) + 1;
//        Extract single-sided spectrum
        double[] P1 = new double[numPoints];

        for (int i = 0; i < numPoints; i++) {
            P1[i] = P2[i] ;
        }
//        Frequency axis calculation
        double[] f = new double[numPoints];
        for (int i = 1; i < numPoints; i++) {
            f[i] = (samplingRate * i)/ N2;
//            System.out.println(f[i]);
        }
        double[][] p1_Freq = {f, P1};
       return p1_Freq;
    }

    public int customNextPow2(int n) {
        // Initializing Power
        int p = 0;
        int val = 1;
        try {
            while (val < n) {
                val = val * 2;
                p = p + 1;
            }
        } catch (Exception e) {
            System.out.println("Input must be a Positive Integer.");
            e.printStackTrace();
        }
        return p;
    }

    public ArrayList<double[]> customFactorCalculation(double[] x){
        int N = x.length;
        ArrayList<double[]> factorList = new ArrayList<double[]>();
        // factor_real will having 0 as their values even after calculation, So we need not to do its calculation
        double[] factor_real = new double[N/2];
        double[] factor_imag = new double[N/2];
        double q =0;
        double w_real = 0;
        double w_imag = -2*Math.PI;
        for (int i = 0; i < N/2; i++) {
            // -1 * is multiplied bcoz after transpose sign of the imaginary part changes
             q = -1 * (w_imag * i)/N;
             factor_real[i] = Math.exp(w_real) * Math.cos(q);
             factor_imag[i] = Math.exp(w_real) * Math.sin(q);
//            System.out.println(factor_real[i]+"+"+factor_imag[i]+"i");
        }
        factorList.add(factor_real);
        factorList.add(factor_imag);
        return factorList;
    }

    public double[] fastFourierTransform(double[] x)
    {
        int N = x.length;
        if (N == 1)
            return x;
        if(N%2!=0) {
            System.out.println("Length of the input array must be a power of 2");
        }
        double[] x_even = new double[N/2];
        double[] x_odd = new double[N/2];
        int ev,od;
        ev = 0;
        od = 0;
        for (int i = 0; i < x.length; i++) {
            if (i%2==0){
                x_even[ev] = x[i];
                ev++;
            }else {
                x_odd[od] = x[i];
                od++;
            }
        }
        double[] XEven = fastFourierTransform(x_even);
        double[] XOdd = fastFourierTransform(x_odd);


        double[] factorImaginary = customFactorCalculation(x).get(1);
        double[] factorReal = customFactorCalculation(x).get(0);

        double[] Xans = finalXCalculation(XEven, XOdd, factorImaginary, factorReal);
        return Xans;
    }

    public double[] finalXCalculation (double[] XEven, double[] XOdd, double[] factorImaginary, double[] factorReal)
    {
        ArrayList<double[]> arrayList = new ArrayList<double[]>();
        double[] a_real = new double[factorReal.length];
        double[] a_imag = new double[factorImaginary.length];
        if(XOdd.length<2) {
            int arraySizeManager = 0;
            for (int i = 0; i < factorImaginary.length; i++) {
                a_real[i] = XOdd[i+arraySizeManager] * factorReal[i];
                a_imag[i] = XOdd[i+arraySizeManager] * factorImaginary[i];
                arraySizeManager=arraySizeManager+1;
            }
        }else {
            double[] factor = complexMultiplication(XOdd, factorImaginary, factorReal);
            int ev = 0;
            int od = 0;
            for (int i = 0; i < factor.length; i++) {
                if (i % 2 == 0) {
                    a_real[ev] = factor[i];
                    ev++;
                } else {
                    a_imag[od] = factor[i];
                    od++;
                }
            }
        }
        if (XEven.length == 1) {
            double[] Xans = new double[XEven.length * 4];
            int index = 0;
            int arraySizeManagerNew = 0;
            for (int i = 0; i < Xans.length; i=i+4) {
                Xans[i] = XEven[index+arraySizeManagerNew] + a_real[index];
                Xans[i+1] = 0 + a_imag[index];
                Xans[i+2] = XEven[index+arraySizeManagerNew] - a_real[index];
                Xans[i+3] = 0 - a_imag[index];
                index++;
                arraySizeManagerNew++;
            }
            return Xans;
        }else {
            double[] Xans = new double[XEven.length * 2];
            int index = 0;
            int arraySizeManagerNew = 0;
            /*for (int i = 0; i < Xans.length; i=i+4) {
                Xans[i] = XEven[index+arraySizeManagerNew] + a_real[index];
                Xans[i+1] = XEven[index+arraySizeManagerNew+1] + a_imag[index];
                Xans[i+2] = XEven[index+arraySizeManagerNew] - a_real[index];
                Xans[i+3] = XEven[index+arraySizeManagerNew+1] - a_imag[index];
                index++;
                arraySizeManagerNew++;
            } */
        for (int i = 0; i < XEven.length; i=i+2) {
            Xans[i] = XEven[index+arraySizeManagerNew] + a_real[index];
            Xans[i+1] = XEven[index+arraySizeManagerNew+1] + a_imag[index];
            index++;
            arraySizeManagerNew++;
        }
        index = 0;
        arraySizeManagerNew = 0;
        for (int i = XEven.length; i < Xans.length; i=i+2) {
            Xans[i] = XEven[index+arraySizeManagerNew] - a_real[index];
            Xans[i+1] = XEven[index+arraySizeManagerNew+1] - a_imag[index];
            index++;
            arraySizeManagerNew++;
        }
            return Xans;
        }
    }
//-----------------------------------------------------------------------------------------------------------------------------------

    public ArrayList<double[]> testingMethod(double[] XEven, double[] XOdd, double[] factorImaginary, double[] factorReal){
        ArrayList<double[]> arrayList = new ArrayList<double[]>();
        double[] a_real = new double[factorReal.length];
        double[] a_imag = new double[factorImaginary.length];
        int arraySizeManager = 0;
        for (int i = 0; i < factorImaginary.length; i++) {
            a_real[i] = XOdd[i+arraySizeManager] * factorReal[i];
            a_imag[i] = XOdd[i+arraySizeManager] * factorImaginary[i];
            arraySizeManager=arraySizeManager+1;
        }
        arrayList.add(a_real);
        arrayList.add(a_imag);
        return arrayList;
    }

    public double[][] seperateFFTRealAndImaginaryData(double[] fftData)
    {
        double[] fftReal = new double[fftData.length/2];
        double[] fftImag = new double[fftData.length/2];
        int ev = 0;
        int od = 0;
        for (int i = 0; i < fftData.length; i++) {
            if (i % 2 == 0) {
                fftReal[ev] = fftData[i];
                ev++;
            } else {
                fftImag[od] = fftData[i];
                od++;
            }
        }
        double[][] fftRealAndImag = {fftReal, fftImag};
        return fftRealAndImag;
    }

    //Complex values multiplication
    public double[] complexMultiplication(double[] XOdd, double[] factorImaginary, double[] factorReal )
    {
        double[] factor = new double[factorImaginary.length+factorReal.length];
        int ev = 0;
        int od = 0;
        for (int i = 0; i <factor.length ; i++) {
            if (i%2==0) {
                factor[i] = factorReal[ev];
                ev++;
            }
            else {
                factor[i] = factorImaginary[od];
                od++;
            }
        }
        double[] z = new double[XOdd.length];
        if (XOdd.length>0)
        {
            for (int i = 0; i < XOdd.length; i=i+2) {
                double a = XOdd[i];
                double b = XOdd[i+1];
                double x = factor[i];
                double y = factor[i+1];
                double zReal = 0;
                double zImag = 0;
                zReal = a*x - (b*y);
                zImag = b*x + a * y;
                z[i] = zReal;
                z[i+1] = zImag;
            }
            return z;
        }
        return new double[1];
    }

    //    Normalize the FFT output
    public double[] normaliseFFT(double[] fftReal, double[] fftImag, int N2)
    {
        double[] absArr = new double[fftReal.length];
        double customReal = 0;
        double customImag = 0;
        for (int i = 0; i < fftReal.length; i++) {
            customReal = fftReal[i]/N2;
            customImag = fftImag[i]/N2;
            absArr[i] = Math.sqrt(customReal * customReal + customImag * customImag) ;
        }
        return absArr;
    }

    public double[] absoluteOfFFT(double[] fftReal, double[] fftImag)
    {
        double[] absArr = new double[fftReal.length];
        double customReal = 0;
        double customImag = 0;
        for (int i = 0; i < fftReal.length; i++) {
            customReal = fftReal[i];
            customImag = fftImag[i];
            absArr[i] = Math.sqrt(customReal * customReal + customImag * customImag) ;
        }
        return absArr;
    }




}

/*public double[] finalXCalculation (double[] XEven, double[] XOdd, double[] factorImaginary, double[] factorReal)
{
    ArrayList<double[]> arrayList = new ArrayList<double[]>();
    double[] a_real = new double[factorReal.length];
    double[] a_imag = new double[factorImaginary.length];
    if(XOdd.length<2) {
        int arraySizeManager = 0;
        for (int i = 0; i < factorImaginary.length; i++) {
            a_real[i] = XOdd[i+arraySizeManager] * factorReal[i];
            a_imag[i] = XOdd[i+arraySizeManager] * factorImaginary[i];
            arraySizeManager=arraySizeManager+1;
        }
    }else {
        double[] factor = complexMultiplication(XOdd, factorImaginary, factorReal);
        int ev = 0;
        int od = 0;
        for (int i = 0; i < factor.length; i++) {
            if (i % 2 == 0) {
                a_real[ev] = factor[i];
                ev++;
            } else {
                a_imag[od] = factor[i];
                od++;
            }
        }
    }
    if (XEven.length == 1) {
        double[] Xans = new double[XEven.length * 4];
        int index = 0;
        int arraySizeManagerNew = 0;
        for (int i = 0; i < Xans.length; i=i+4) {
            Xans[i] = XEven[index+arraySizeManagerNew] + a_real[index];
            Xans[i+1] = 0 + a_imag[index];
            Xans[i+2] = XEven[index+arraySizeManagerNew] - a_real[index];
            Xans[i+3] = 0 - a_imag[index];
            index++;
            arraySizeManagerNew++;
        }
        return Xans;
    }else {
        double[] Xans = new double[XEven.length * 2];
        int index = 0;
        int arraySizeManagerNew = 0;
        for (int i = 0; i < Xans.length; i=i+4) {
            Xans[i] = XEven[index+arraySizeManagerNew] + a_real[index];
            Xans[i+1] = XEven[index+arraySizeManagerNew+1] + a_imag[index];
            Xans[i+2] = XEven[index+arraySizeManagerNew] - a_real[index];
            Xans[i+3] = XEven[index+arraySizeManagerNew+1] - a_imag[index];
            index++;
            arraySizeManagerNew++;
//                System.out.print(Xans[i]+" + "+Xans[i+1]);
//                System.out.println();
//                System.out.print(Xans[i+2]+" + "+Xans[i+3]);
//                System.out.println();
        }
        return Xans;
    }
}*/

