package com.stemi.libs;
import java.util.ArrayList;

public class Signals {

    // Method 1 --Synthesize Signal Length of two signals
    public  double[] synthesizeSignalLength (double[] n1, double[] n2)
    {
        double min = findMinimum(n1,n2);
        double max = findMaximum(n1,n2);
        int nNewLength = (int) (Math.abs(min) + Math.abs(max) + 1);
        double[] nNew =  new double[nNewLength];
        // Our new array nNew is created from minimum n1/n2 value to maximum n1/n2 value
        for (int i = 0; i < nNewLength; i++) {
            nNew[i] = min;
            min++;
        }
        return nNew;
    }
    // Method 2 -- for to rearrange signals x1 and x2 w.r.t nNew array
    public ArrayList<double[]> signalSynthesis(double[] n1, double[] n2, double[] x1, double[] x2){
        ArrayList<double[]> list = new ArrayList<double[]>();

        if((n1[0] == n2[0]) && (n1[n1.length - 1] == n2[n2.length - 1])) {
            list.add(x1);       // x1 signal after synthesis
            list.add(x2);       // x2 signal after synthesis
            list.add(n1);         // length of x1 and x2 signal after synthesis nNew
            return list;
        } else
        {
            double[] nNew = synthesizeSignalLength(n1,n2);
            double[] x1_New = new double[nNew.length];
            double[] x2_New = new double[nNew.length];
            if(nNew[0] == minimum(n1))
            {
                System.out.println("First One");
                //Entering values of x1 in x1_New and rest values be 0.
                for(int i = 0; i < x1.length; i++) {
                    x1_New[i] = x1[i];
                }
                int len = x2_New.length - x2.length;
                int index = 0;
                // Entering values of x2 in x2_New and rest values be 0
                for (int i = len; i < x2_New.length; i++) {
                    x2_New[i] = x2[index];
                    index++;
                }
            }
            else if (nNew[0] == minimum(n2)) {
                System.out.println("Second One");
                //Entering values of x2 in x2_New and rest values be 0.
                for (int i = 0; i < x2.length; i++) {
                    x2_New[i] = x2[i];
                }
                int len = x1_New.length - x1.length;
                int index = 0;
                // Entering values of x1 in x1_New and rest values be 0
                for (int i = len; i < x1_New.length; i++) {
                    x1_New[i] = x1[index];
                    index++;
                }
            }
            list.add(x1_New);       // x1 signal after synthesis
            list.add(x2_New);       // x2 signal after synthesis
            list.add(nNew);         // length of x1 and x2 signal after synthesis nNew
            return list;
        }
    }


    // Method 3 --Used for Signal Shifting. To shift n array w.r.t 'k'
    public double[] signalShifting(double[]n, double k) {
        for (int i = 0; i < n.length; i++) {
            n[i] = n[i] + k;
        }
        return n;
    }

    // Method 4 --Time Scaling
    public double[] signalScaling(double[] n, double alpha)
    {
        for (int i = 0; i <n.length ; i++)
        {
            n[i] = n[i] / alpha;
        }
        return n;
    }

    // Method 5 -- Signal Multiplication
    public double[] signalMultiplication(double[] n1, double[] x1, double[] n2, double[] x2)
    {
        ArrayList list = signalSynthesis(n1,n2, x1, x2);
        double[] x1New =(double[]) list.get(0);
        double[] x2New = (double[]) list.get(1);
        int len = x1New.length;
        double[] x1x2Multipliy = new double[len];
        for (int i = 0; i < len; i++) {
            x1x2Multipliy[i] = x1New[i] * x2New[i];
        }
       return x1x2Multipliy;
    }

    // Method 6 -- Signal Addition
    public double[] signalAddition(double[] n1, double[] x1, double[] n2, double[] x2)
    {
        ArrayList<double[]> listAdd = new ArrayList<double[]>();
        ArrayList<double[]> list = signalSynthesis(n1,n2,x1,x2);
        double[] x1New =(double[]) list.get(0);
        double[] x2New = (double[]) list.get(1);
        int len = x1New.length;
        double[] xAddedSignal = new double[len];
        for (int i = 0; i < len; i++) {
            xAddedSignal[i] = x1New[i] + x2New[i];
        }
        return xAddedSignal;
    }

    // Method 7 -- Signal Folding/Time reversal
    public ArrayList<double[]> signalFoldingNew(double[] n, double[] x)
    {
        double[] x1New = reverse(x);
        double[] n1New = signalAmplitudeScaling(n,-1);
        n1New = reverse(n1New);
        ArrayList<double[]> list = new ArrayList<double[]>();
        list.add(x1New);            // 0 index of the list gives the signal array
        list.add(n1New);            // 1 index of the list gives the n array;
        return  list;
    }

    // Method 8 -- Amplitude Scaling
    public double[] signalAmplitudeScaling(double[] a, double beta)
    {
        int len = a.length;
        double[] aNew = new double[len];
        for (int i = 0; i < len; i++) {
            aNew[i] = beta * a[i];
        }
        return aNew;
    }

    // Method 9 -- Finding Even and Odd Components of a Signal
    public ArrayList<double[]> signalEvenAndOddParts(double[] n, double[] x)
    {
        ArrayList<double[]> list = signalFoldingNew(n,x);
        double[] xFolded = (double[]) list.get(0);
        double[] nFolded = (double[]) list.get(1);
        double[] xEven = signalAddition(n, x, nFolded, xFolded);
        xEven = signalAmplitudeScaling(xEven, 0.5);

        // Now to find odd parts
        double[] y = signalAmplitudeScaling(xFolded, -1);
        double[] xOdd = signalAddition(n, x, nFolded, y);
        xOdd = signalAmplitudeScaling(xOdd, 0.5);
        list.clear();
        list.add(xEven);    //To get Even part index =0;
        list.add(xOdd);    // To get Odd part index = 1;
        return list;
    }

    // Method 10 Convolution  --- Using Tabular method after signal synthesis
    public double[] convolution(double[] n1, double[] n2, double[] x1, double[] x2)
    {
        ArrayList list = signalSynthesis(n1,n2, x1, x2);
        double[] x1New =(double[]) list.get(0);
        double[] x2New = (double[]) list.get(1);
        double[] nNew = (double[]) list.get(2);
//        for(double ans : x1New) {
//            System.out.print(ans + ", ");
//        }
        // Now length of signal x1New = x2New. Now Performing Tabular Multilication
        double[][] arr = new double[x1New.length][x2New.length];
        for (int i = 0; i < x2New.length; i++) {
            for (int j = 0; j < x1New.length; j++) {
                arr[i][j] = x2New[i] * x1New[j];
            }
        }
        double[] y = new double[x1New.length + x2New.length - 1];   // length of y = row + col - 1
        for (int k = 0; k < x2New.length; k++) {
            int i = k;
            int j = 0;
            double sum = 0;
            while (i >= 0) {
                sum += arr[i][j];
                i--;
                j++;
            }
            y[k] = sum;
        }
        for (int k = 1; k < x1New.length; k++) {
            int i = x2New.length-1;
            int j = k;
            double sum = 0;
            while (j < x1New.length)
            {
                sum += arr[i][j];
                i--;
                j++;
            }
            y[x1New.length + k - 1] = sum;
        }
        return y;
    }


//--------------------------------- private methods Start here.--------------------------
    private double findMinimum(double[] a, double[] b) {
        double minA = minimum(a);
        double minB = minimum(b);
        if (minA < minB) {
            return minA;
        }
        else{
            return minB;
        }
    }

    private double findMaximum(double[] a, double[] b) {
        double maxA = maximum(a);
        double maxB = maximum(b);
        if (maxA > maxB) {
            return maxA;
        }
        else{
            return maxB;
        }
    }

    private double maximum(double[] a) {
        double max = a[0];
        for (int i = 1; i < a.length; i++) {
            if (a[i] > max)
                max = a[i];
        }
        return max;
    }

    // private method to find minimum in an array
    private double minimum(double[] a){
        double min = a[0];
        for (int i = 1; i < a.length; i++) {
            if (a[i] < min)
                min = a[i];
        }
        return min;
    }

    // private method to reverse an array
    public double[] reverse(double[] n)
    {
        int len = n.length;
        double[] nReverse = new double[len];
        for (int i = 0; i < len; i++) {
           nReverse[i] = n[len - 1 - i];
        }
        return nReverse;
    }
}
