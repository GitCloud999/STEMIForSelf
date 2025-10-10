package com.stemi;

import com.stemi.libs.*;

import java.util.ArrayList;

public class Filters {

    public double[][] lowpass_filter_all(double[][] newDataArray, double Fs, double f_low) {
        double RC = 1 / (2 * Math.PI * f_low);
        double dt = 1 / Fs;
        double alpha = dt / (RC + dt);
        Utility ut = new Utility();
        RC = ut.reduceValueAfterPoints(RC,4);
        dt = ut.reduceValueAfterPoints(dt, 4);
        alpha = ut.reduceValueAfterPoints(alpha,4);
        int numColumns = newDataArray.length;
        double[][] filtered_data = new double[newDataArray.length][newDataArray[0].length];
        for (int i = 0; i < numColumns; i++) {
            double[] columnData = fillColumnData(newDataArray, i);
            double[] filteredColumn = new double[columnData.length];
            double yPrev = columnData[0];
            for (int n = 0; n < columnData.length; n++) {
                double xN = columnData[n];
                double yN = alpha * xN + (1 - alpha) * yPrev;
                filteredColumn[n] = yN;
                yPrev = yN;
            }
            for (int j = 0; j < columnData.length; j++) {
                filtered_data[i][j] = filteredColumn[j];
            }
        }
        return filtered_data;
    }

    // Highpass_filter_moving_average
    public double[][] highpass_filter_moving_average(double[][] data, double windowSize)  {
        if (windowSize <= 0 || windowSize%1 != 0)
            System.out.println("Window size must be a positive Integer");
        int numColumns = data.length;
        double[][] highPassFilteredData = new double[data.length][data[0].length];
        for (int i = 0; i < numColumns; i++) {
            double[] columnData = data[i];
            double[] movingAvgCustom = movingAverage(columnData, windowSize);
            double[] filteredColumn = new double[columnData.length];
            for (int j = 0; j < columnData.length; j++) {
//                System.out.println(j +" ========== " +movingAvgCustom[j]);
                filteredColumn[j] = columnData[j] - movingAvgCustom[j];
            }
            highPassFilteredData[i] = filteredColumn;
        }
        return highPassFilteredData;
    }


    // Notch_Filter
    public double[][] notch_filter(double[][] data, double Fs) {
        double f0_50Hz = 50;
        double w0_50Hz = (2 * Math.PI * f0_50Hz) / Fs;
        double r_50Hz = 0.6;
        double[] b50 = {1, -2*Math.cos(w0_50Hz), 1};
        double[] a50 = {1, -2*r_50Hz*Math.cos(w0_50Hz), Math.pow(r_50Hz, 2)};
        double f0_60Hz = 60;
        double w0_60Hz = (2 * Math.PI * f0_60Hz) / Fs;
        double r_60Hz = 0.6;
        double[] b60 = {1, -2*Math.cos(w0_60Hz), 1};
        double[] a60 = {1, -2*r_60Hz*Math.cos(w0_60Hz), Math.pow(r_60Hz, 2)};
        int numColumns = data.length;
        double[][] notchFilteredData = new double[data.length][data[0].length];
        for (int i = 0; i < numColumns; i++) {
            double[] columnData = data[i];
            double[] notchFilteredColumn = customFiltFilt(b50,a50, columnData);
            notchFilteredColumn = customFiltFilt(b60,a60, notchFilteredColumn);
            notchFilteredData[i] = notchFilteredColumn;
        }
        return notchFilteredData;
    }

    // Date :- 23_Dec_2024 Notch_Filter_ButterWorth After 23_Dec matlab file
    // design_notch_filter_custom function is also adjusted in this method only.
    public double[][] notch_filter_butterworth(double[][] data, double Fs) {
        double f50Hz = 50;
        double f60Hz = 60;
        double q = 50;       // Quality factor (adjustable for better filtering)
        double bw = f50Hz / q;
        double[] wN = new double[2];
        wN[0] = (f50Hz - (bw/2)) / (Fs/2);
        wN[1] = (f50Hz + (bw/2)) / (Fs/2);
        ArrayList<double[]> listArr = butterFilter(2, wN, "stop");
        double[] b50 = listArr.get(0);
        double[] a50 = listArr.get(1);
        listArr.clear();
        bw = f60Hz / q;
        wN[0] = (f60Hz - (bw/2)) / (Fs/2);
        wN[1] = (f60Hz + (bw/2)) / (Fs/2);
        listArr = butterFilter(2, wN, "stop");
        double[] b60 = listArr.get(0);
        double[] a60 = listArr.get(1);
        int numColumns = data.length;
        double[][] notchFilteredData = new double[data.length][data[0].length];
        for (int i = 0; i < numColumns; i++) {
//        for (int i = 0; i < 1; i++) {
            double[] columnData = data[i];
            double[] filtered50Hz = customFiltFiltNew(b50, a50, columnData);
            double[] filtered60Hz = customFiltFiltNew(b60, a60, filtered50Hz);
//            display(filtered60Hz);
            notchFilteredData[i] = filtered60Hz;
        }
//        Loader ld = new Loader();
//        ld.viewData(notchFilteredData);
        return notchFilteredData;
    }

//  Updated on 22 Mar 2025
    public double[][] notch_filter_butterworthNew(double[][] data, double Fs) {
        double f50Hz = 50;
        double f60Hz = 60;
        double q = 0.5;       // Quality factor (adjustable for better filtering)
        double bw = f50Hz / q;
        double[] wN = new double[2];
        wN[0] = (f50Hz - (bw/2)) / (Fs/2);
        wN[1] = (f50Hz + (bw/2)) / (Fs/2);
//        System.out.println(wN[0] +"            "+wN[1]);
        ArrayList<double[]> listArr = butterFilterNew(1, wN, "stop");
        double[] b50 = listArr.get(0);
        double[] a50 = listArr.get(1);
//        com.arrthymia.LoaderHelper ldh = new com.arrthymia.LoaderHelper();
//        ldh.viewData(b50);
        listArr.clear();
        bw = f60Hz / q;
        wN[0] = (f60Hz - (bw/2)) / (Fs/2);
        wN[1] = (f60Hz + (bw/2)) / (Fs/2);
//        System.out.println(wN[0]+"   ,     "+wN[1]);
        listArr = butterFilterNew(1, wN, "stop");
        double[] b60 = listArr.get(0);
        double[] a60 = listArr.get(1);
        int numColumns = data.length;
        double[][] notchFilteredData = new double[data.length][data[0].length];
        for (int i = 0; i < numColumns; i++) {
//        for (int i = 0; i < 1; i++) {
            double[] columnData = data[i];
            double[] filtered50Hz = customFiltFiltNew(b50, a50, columnData);
            double[] filtered60Hz = customFiltFiltNew(b60, a60, filtered50Hz);
            notchFilteredData[i] = filtered60Hz;
        }
        return notchFilteredData;
    }

    //    Updated on 15 July 2025
    public double[] notch_filter_butterworthNew_Individual(double[] data, double Fs) {
        double f50Hz = 50;
        double f60Hz = 60;
        double q = 0.5;       // Quality factor (adjustable for better filtering)
        double bw = f50Hz / q;
        double[] wN = new double[2];
        wN[0] = (f50Hz - (bw/2)) / (Fs/2);
        wN[1] = (f50Hz + (bw/2)) / (Fs/2);
//        System.out.println(wN[0] +"            "+wN[1]);
        ArrayList<double[]> listArr = butterFilterNew(1, wN, "stop");
        double[] b50 = listArr.get(0);
        double[] a50 = listArr.get(1);
//        LoaderHelper ldh = new LoaderHelper();
//        ldh.viewData(b50);
        listArr.clear();
        bw = f60Hz / q;
        wN[0] = (f60Hz - (bw/2)) / (Fs/2);
        wN[1] = (f60Hz + (bw/2)) / (Fs/2);
        listArr = butterFilterNew(1, wN, "stop");
        double[] b60 = listArr.get(0);
        double[] a60 = listArr.get(1);
        // let
//        double[] b50 = {0.5792, 0.4208, 0};
//        double[] a50 = {1,0,0};
        double[] filtered50Hz = customFiltFiltNew(b50, a50, data);
        double[] filtered60Hz = customFiltFiltNew(b60, a60, filtered50Hz);
        return filtered60Hz;
    }



    public ArrayList<double[]> butterFilter(int order, double[] wN, String type) {
//        poles = exp(1i * pi * (1:2*order) / (2*order)).';
        double[] polesReal = new double[2*order];
        double[] polesImag = new double[2*order];
        Utility ut = new Utility();
        for (int i = 1; i <= (2*order); i++) {
            double q = Math.PI * i / (2 * order);
            polesReal[i-1] =  ut.reduceValueAfterPoints(Math.cos(q), 4);
            polesImag[i-1] =  ut.reduceValueAfterPoints(Math.sin(q),4);
        }
        ArrayList<Integer> listA = new ArrayList<Integer>();
        for (int i = 0; i < polesImag.length; i++) {
            if(polesImag[i] < 0 ) {
                listA.add(i);
            }
        }
//        Loader ld = new Loader();
//        ld.viewData(polesImag);
        double[] polesRealNew = new double[polesReal.length - listA.size()];
        double[] polesImagNew = new double[polesReal.length - listA.size()];
        int idxList = 0;
        int idxArr = 0;
        for (int i = 0; i < polesReal.length; i++) {
            if( !listA.isEmpty() && i == (int) listA.get(idxList))
            {
                idxList++;
                continue;
            }
            polesRealNew[idxArr] = polesReal[i];
            polesImagNew[idxArr] = polesImag[i];
            idxArr++;
        }
        for (int i = 0; i < wN.length; i++) {
            wN[i] = Math.tan(Math.PI * wN[i]/2);
        }
        if(type.equals("low") || type.equals("high"))   {
            for (int i = 0; i < polesRealNew.length; i++) {
                // Doubtfull point
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!! for checking");
                polesRealNew[i] = wN[0] * polesRealNew[i];
                polesImagNew[i] = wN[0] * polesImagNew[i];
            }
        }
        else if (type.equals("bandpass") || type.equals("stop")) {
            for (int i = 0; i < polesRealNew.length; i++) {
                double a = Math.sqrt(wN[0] * wN[1]);
                polesRealNew[i] = polesRealNew[i] * a;
                polesImagNew[i] = polesImagNew[i] * a;
//                System.out.println(polesRealNew[i] +" + "+polesImagNew[i]);
            }
        }
//        Loader ld = new Loader();
//        ld.viewData(polesRealNew);
        double[] a = poly(polesRealNew);
        double[] sumWn = {0};
        for (int i = 0; i < wN.length; i++) {
            sumWn[0] += -1 * wN[i];
        }
        double[] b = poly(sumWn);
        double sumB = 0;
        for (int i = 0; i < b.length; i++) {
            sumB += b[i];
        }
        for (int i = 0; i < b.length; i++) {
            b[i] = b[i] / sumB;
        }
        ArrayList<double[]> listArr = new ArrayList<double[]>();
        listArr.add(b);
        listArr.add(a);
        return listArr;
    }

//   Changed on 22 Mar 2025
    public ArrayList<double[]> butterFilterNew(int order, double[] wN, String type) {
//        poles = exp(1i * pi * (1:2*order) / (2*order)).';
        double[] polesReal = new double[2*order];
        double[] polesImag = new double[2*order];
        double[] wP = new double[wN.length];
        Utility ut = new Utility();
        double sumA = 0 , sumB = 0, h0 = 0;
        for (int i = 1; i <= order; i++) {
//            double theta_k = (2 * i - 1) * Math.PI / (2 * i);
            double theta_k = (2 * i - 1) * Math.PI / (2 * order);
//            polesReal[i-1] =  Math.round( (-1) * Math.sin(theta_k) * 1000 ) / 1000;
            polesReal[i-1] =   (double) Math.round( (-1) * Math.sin(theta_k) * 1000) / 1000;
            polesImag[i-1] = (double) Math.round( Math.cos(theta_k) * 1000 ) / 1000;
        }
//        ut.viewData(polesImag);
//        pre-warp
        if (type.equalsIgnoreCase("low") || type.equalsIgnoreCase("high")) {
            if (wN.length != 1 ) {
                System.out.println("Error ----- For low or high, must have single Wn");
            }
            for (int i = 0; i < wN.length; i++)
                wP[i] = Math.tan((Math.PI * wN[i]) / 2);
        }
        else {
            if (wN.length != 2 ) {
                System.out.println("Error ----- For stop, Wn=[Wlow, Wupper]");
            }
            wN[0] = Math.tan( (Math.PI * wN[0]) / 2 );
            wN[1] = Math.tan( (Math.PI * wN[1]) / 2 );
//            System.out.println("wN==   "+wN[0]+"          ,       "+wN[1]);
        }
        switch (type)
        {
            case "low":
                for (int i = 0; i < polesReal.length; i++) {
                    polesReal[i] = polesReal[i] * wP[i];
                    polesImag[i] = polesImag[i] * wP[i];
                }
                break;
            case "high":
                for (int i = 0; i < polesReal.length; i++) {
                    polesReal[i] =  wP[i] / polesReal[i];
                    polesImag[i] =  wP[i] / polesImag[i];
                }
                break;
            case "stop":
                double wCenter = (double) Math.round(Math.sqrt(wN[0] * wN[1]) * 10000 ) / 10000;
                double bw = (double) Math.round( (wN[1] - wN[0]) * 10000 ) / 10000;
//                double bw = 0.3511;
//                System.out.println("wCenter =  "+wCenter+"    bw = "+bw);
                for (int i = 0; i < order; i++) {
                    polesReal[i] = (bw * polesReal[i]) / ( (polesReal[i] * polesReal[i]) + (wCenter * wCenter) );
                    polesImag[i] = (bw * polesImag[i]) / ( (polesImag[i] * polesImag[i]) + (wCenter * wCenter) );
//                    double abSquare = (polesReal[i] * polesReal[i]) - (polesImag[i] * polesImag[i]);
//                    double polesRealMult = (bw * polesReal[i]);
//                    double abSquarePluswCenterSquare =  ( abSquare ) + ( (double) Math.round((wCenter * wCenter) * 10000) / 10000);
//                    polesReal[i] = (bw * polesReal[i]) / ( ( abSquare ) + (wCenter * wCenter) );
//                    polesImag[i] = (bw * polesImag[i]) / ( (2 * polesReal[i] * polesImag[i]) + (wCenter * wCenter) );
                }
                break;
        }
//        System.out.println("POLES==   "+polesReal[0]+"          ,       "+polesImag[0]);
//        bilinear transform
        double[] zPolesReal = new double[order];
        double[] zPolesImag = new double[order];
        for (int m = 0; m < order; m++) {
            zPolesReal[m] = (2+polesReal[m]) / (2-polesReal[m]);
        }
//        double[] aNew = poly(zPolesReal);
        double[] aNew = polyForNotch(zPolesReal);
//        System.out.println("--------------------------------");
//        ut.viewData(aNew);
        double[] bNew = new double[aNew.length];
        for (int i = 0; i < aNew.length; i++) {
            bNew[i] = aNew[0];
            sumB += bNew[i];
            sumA += aNew[i];
        }
//        Normalization
        switch (type) {
            case "low":
                // gain at DC
                h0 = sumB / sumA;
                for (int i = 0; i < bNew.length; i++) {
                    bNew[i] = bNew[i] / h0;
                }
                break;
            case "high":
                double sumNum=0, sumDen=0, hPi;
                int nCoef = aNew.length;
                for (int i = 0; i < nCoef; i++) {
                     sumNum+= bNew[i] * Math.pow(-1, i);
                     sumDen+= aNew[i] * Math.pow(-1, i);
                }
                hPi = sumNum / sumDen;
                for (int i = 0; i < bNew.length; i++) {
                    bNew[i] = bNew[i] / hPi;
                }
                break;
            case "stop":
                h0 = sumB / sumA;
                for (int i = 0; i < bNew.length; i++) {
                    bNew[i] = bNew[i] / h0;
                }
                break;
        }
        ArrayList<double[]> listArr = new ArrayList<double[]>();
        listArr.add(bNew);
        listArr.add(aNew);
        return listArr;
    }

    public ArrayList<double[]> pointDetectionButterFilter(int order, double[] wN, String type) {
        double[] a = new double[1];
        double[] polesReal = new double[2*order];
        double[] polesImag = new double[2*order];
        Utility ut = new Utility();
        for (int i = 1; i <= (2*order); i++) {
            double q = Math.PI * i / (2 * order);
            polesReal[i-1] =  ut.reduceValueAfterPoints(Math.cos(q), 4);
            polesImag[i-1] =  ut.reduceValueAfterPoints(Math.sin(q),4);
        }
        ArrayList<double[]> listA = new ArrayList<double[]>();
        ArrayList<double[]> listBB = new ArrayList<double[]>();
        if (type.equals("bandpass") || type.equals("stop")) {
            double[] scaleForBandPassAndStop = new double[2*order];
            a[0] = 1.0;
            for (int i = 0; i < scaleForBandPassAndStop.length; i++) {
                if(i < order)
                    scaleForBandPassAndStop[i] = -wN[0];
                else
                    scaleForBandPassAndStop[i] = -wN[1];
            }
            Filters filters = new Filters();
            double[] bb = filters.poly(scaleForBandPassAndStop);
            listBB.add(bb);
            double sum = 0;
            for (int i = 0; i < bb.length; i++) {
                sum+=bb[i];
            }
            for (int i = 0; i < bb.length; i++) {
                bb[i] = bb[i] / sum;
            }
        }
        double[] bLast = listBB.get(listBB.size()-1);
        listA.add(bLast);
        listA.add(a);
        return listA;
    }

//    public ArrayList<double[]> pointDetectionButterFilterArrhythmia(int order, double[] wN, String bandpass) {
//
//
//    }

    public double[] poly(double[] roots) {
        double[] p = {1};
//        for r = roots'
//        p = conv(p, [1, -r]); % Multiply (x - root)
//                % p2 = convolution(p2, length(p2), [1, -r], length([1, -r]));
//        end
        for (int i = 0; i < roots.length; i++) {
            double[] rootsNew = {1, -roots[i]};
            Utility ut = new Utility();
//            p = ut.convolutionCustom(p, rootsNew);
            p = ut.convolutionCustomForPointDetection(p, rootsNew);
        }
//        double[] y = p;
//        for (int j = 0; j < y.length; j++) {
//            System.out.println(y[j]);
//        }
        return p;
    }

//    Updated on 25 Mar --- No change except new function convolutionCustomForNotch()
//    called.
    public double[] polyForNotch(double[] roots) {
        double[] p = {1};
//        for r = roots'
//        p = conv(p, [1, -r]); % Multiply (x - root)
//                % p2 = convolution(p2, length(p2), [1, -r], length([1, -r]));
//        end
        for (int i = 0; i < roots.length; i++) {
            double[] rootsNew = {1, -roots[i]};
            Utility ut = new Utility();
//            p = ut.convolutionCustom(p, rootsNew);
            p = ut.convolutionCustomForNotch(p, rootsNew);
//            p = ut.convolutionCustomForPointDetection(p, rootsNew);
        }
//        double[] y = p;
//        for (int j = 0; j < y.length; j++) {
//            System.out.println(y[j]);
//        }
        return p;
    }

    public double[] customFiltFiltNew(double[] b50, double[] a50, double[] x) {
        int nfilt = Math.max(b50.length, a50.length) - 1;
        double[] xPadded;
        int edge = 3 * nfilt;       // Length of edge extension
        if (x.length <= edge) {
            System.out.println("Signal length must be greater than 3 times the filter order.");
            xPadded = new double[1];
        } else {
            int len = x.length + (2 * edge);
            xPadded = new double[x.length + (2 * edge)];
            xPadded[0] = x[0];
            xPadded[xPadded.length - 1] = x[x.length - 1];
            ArrayList<Double> list = new ArrayList<>();
            for (int i = 0; i < edge; i++) {
                xPadded[i] = 2 * x[0] - x[edge - 1 + 1 - i];
//                double val = 2 * x[0] - x[edge - 1 + 1 - i];
//                System.out.println(i+"     ===  "+val);
            }
            for (int i = 0; i < x.length; i++) {
                xPadded[edge + i] = x[i];
            }
            int idx = x.length - 2;
            for (int i = x.length - edge; i < x.length; i++) {
                xPadded[i + (2 * edge)] = 2 * x[x.length - 1] - x[idx];
                idx--;
            }
        }
        Utility ut = new Utility();
//        ut.viewData(xPadded);
        double[] yForward = customFilterNew(b50,a50,xPadded);
        double[] yReverse = reverse(yForward);
        double[] yBackward = customFilterNew(b50, a50, yReverse);
        double[] y = reverse(yBackward);
        double[] yWithoutPadding = new double[y.length - (2 * edge)];
        for (int i = 0; i < y.length; i++) {
            if (i > edge-1 & i < y.length - edge )
                yWithoutPadding[i-edge] = y[i];
        }
//        display(yWithoutPadding);
        return  yWithoutPadding;
    }

//    Apply Savitzky-Golay Filter
    public double[][] savitzkyGolayFilter(int windowSize, int polynomialOrder, double[][] polySubData) {
        double[] baseLineNew = new double[polySubData[0].length];
        double[][] filteredDataArray = new double[polySubData.length][polySubData[0].length];
        if(windowSize%2==0)
            System.out.println("Window Size must be an Odd Number");
        else {
            for (int i = 0; i < polySubData.length; i++) {
//                for (int i = 0; i < 1; i++) {
                double[] rawLead = polySubData[i];
//                 1) Baseline Removal via Savitzky-Golay
                    baseLineNew = customSavitzkyGolay(rawLead, windowSize, polynomialOrder);
                double[] filteredLead = new double[rawLead.length];
                for (int j = 0; j < baseLineNew.length; j++) {
                    filteredLead[j] = rawLead[j] - baseLineNew[j];
                }
                filteredDataArray[i] = filteredLead;
//                    System.out.println("----------------------------------------------------------");
//                    display(filteredLead);
            }
        }
        return filteredDataArray;
    }

    double[] customSavitzkyGolay(double[] rawLead, int windowSize, int polynomialOrder) {
        if (windowSize%2==0) {
            System.out.println("WindowSize must be an Odd number.");
            return new double[1];
        }
        if (polynomialOrder >= windowSize) {
            System.out.println("Polynomial order must be less than the window size.");
            return new double[1];
        }
        int n = rawLead.length;
        double[] temp = new double[1];
        int halfWindow = (int) Math.floor(windowSize/2);
        double[] baseline = new double[n];
        double[] offSet = fillOffSetData(halfWindow);

        for (int i = 0; i < n; i++) {
//        for (int i = 0; i < 1; i++) {
//      Local window boundaries
//            start_idx = i - half_window;
//            end_idx   = i + half_window;
            int startIdx = i - halfWindow;
            int endIdx = i + halfWindow;
//      Adjust for left boundary
            int leftOffset = 0;
            if (startIdx < 0) {
                leftOffset = -startIdx;
                startIdx = 0;
            }
//      Adjust for right boundary
            if (endIdx >= n)
                endIdx = n-1;
//        Extract local data
            double[] localY =  fillData(startIdx, endIdx, rawLead);
            int localLength = localY.length;
            double[] localX = fillData(leftOffset, leftOffset + localLength - 1, offSet);
//            display(localX);
//            System.out.println("----------------------------------------------------------");
            double[] p = customPolyfit(localX, localY, polynomialOrder);
//            for (double ans : p)
//                System.out.println(ans);
            double[] t = {0};
            temp = customPolyval(p,t);
            baseline[i] = temp[0];
        }
        return baseline;
    }

    //    Polynomial Baseline Subtraction
    public double[][] polynomialBaseLineSubtraction(double[][] filteredData, int polyOrder) {
        double[] leadLp, tSamples, p, baselinePoly, leadPolySub;
        double[][] polyBaseSubArray;
        polyBaseSubArray= new double[filteredData.length][filteredData[0].length];
        int n;
        for (int i = 0; i < filteredData.length; i++) {
//        for (int i = 0; i < 1; i++) {
            leadLp = filteredData[i];
            n = leadLp.length;
            tSamples = new double[n];
            for (int j = 0; j < n; j++) {
                tSamples[j] = (double) j+1;
            }
            p = customPolyfit(tSamples, leadLp, polyOrder);
            baselinePoly = customPolyval(p, tSamples);
            leadPolySub = new double[n];
            for (int j = 0; j < leadLp.length; j++) {
                leadPolySub[j] = leadLp[j] - baselinePoly[j];
            }
            polyBaseSubArray[i] = leadPolySub;
        }
//        display(leadPolySub);
        return polyBaseSubArray;
    }

    public double[] movingAverage(double[] data, double windowSize) {
        if(windowSize <= 0 ) {
            System.out.println("Window size must be a positive integer.");
        }
//      Preallocate the moving average array
        int nSamples = data.length;
        double[] movingAvg = new double[nSamples];
        for (int i = 0; i < nSamples; i++) {
            int startIndex = Math.max(0, (i - (int) (Math.floor(windowSize/2))));
            int endIndex = Math.min(nSamples-1, (i + (int) (Math.floor(windowSize/2))));
//             Compute the mean of the window
//            System.out.println("Start Idx==== " + startIndex + " End Idx === "+ endIndex);
            movingAvg[i] = findMean(data, startIndex,endIndex);
//            System.out.println(i +"  ======  "+movingAvg[i]);
        }
        return movingAvg;
    }

/*    public double[] movingAverage9April2025(double[] data, double windowSize) {
        if(windowSize <= 0 ) {
            System.out.println("Window size must be a positive integer.");
        }
//      Preallocate the moving average array
        double[] paddedData = new double[data.length + (int) windowSize/2];
        for (int i = 0; i < data.length; i++) {
            paddedData[i] = data[i];
        }
        int nSamples = paddedData.length;
        double[] movingAvg = new double[nSamples];
        for (int i = 0; i < nSamples; i++) {
            int startIndex = Math.max(0, (i - (int) (Math.floor(windowSize/2))));
            int endIndex = Math.min(nSamples-1, (i + (int) (Math.floor(windowSize/2))));
//             Compute the mean of the window
//            System.out.println("Start Idx==== " + startIndex + " End Idx === "+ endIndex);
            movingAvg[i] = findMean(paddedData, startIndex,endIndex);
            if (i > data.length-1)
                return movingAvg;
//            System.out.println(i +"  ======  "+movingAvg[i]);
        }
        return movingAvg;
    }*/


    public double[][] fastSavitzkyGolayEcg(double[][] ecgData, double[] sgFilter) {
        double[][] ecgFilteredAfterFastSavitzky = new double[ecgData.length][ecgData[0].length];
        for (int col = 0; col < ecgData.length; col++) {
//        for (int col = 0; col < 1; col++) {
            double[] baseLine = customConvSame(ecgData[col], sgFilter);
            ecgFilteredAfterFastSavitzky[col] = baseLineSubtractionEcg(ecgData[col], baseLine);
        }
        return ecgFilteredAfterFastSavitzky;
    }
    
    public double[] baseLineSubtractionEcg(double[] ecgColumn, double[] baseLine){
        double[] ecgColumnNew = new double[ecgColumn.length];
        for (int i = 0; i < ecgColumn.length; i++) {
            ecgColumnNew[i] = Math.round((ecgColumn[i] - baseLine[i]) * 1000);
            ecgColumnNew[i] = ecgColumnNew[i] / 1000;
        }
        return ecgColumnNew;
    }

    public double[] customConvSame(double[] x, double[] h) {
        int xLength = x.length;
        int hLength = h.length;
        int convoLength = xLength + hLength - 1;
        double[] yFull = new double[convoLength];
//        Naive convolution for 'full'
//        int xIndex;
        for (int n = 0; n < convoLength; n++) {
            double sum = 0;        //  Accumulate partial sum
//            Overlap indices in [n - Nh + 1, n]
            for (int k = 0; k < hLength; k++) {
                int xIndex =  n - k ;
                if (xIndex >= 0 && xIndex < xLength)
                    sum += x[xIndex] * h[k];
            }
            yFull[n] = sum;
        }
//        com.arrthymia.LoaderHelper ld = new com.arrthymia.LoaderHelper();
//        ld.viewData(yFull);
        int startIdx = (int) Math.floor((hLength) /2 );
        int endIdx = (int) startIdx + (xLength - 1);
        return fillData(startIdx, endIdx, yFull);
    }

/*

    public boolean conditionalNotchFiltered(double[][] ecg)  {
//        Define threshold for amplitude in the FFT
        FastFourierTransform fftObj = new FastFourierTransform(500);
        MatlabInbuiltFunctions mf = new MatlabInbuiltFunctions();
        double thresholdAmplitude = 5 * 10000;   // arbitrary, adjust per your data
        double frequencyRange = 0.5;    // +/- 0.5 Hz around 50 or 60
        boolean foundNoise = false;
        int len = ecg[0].length;
        double N2 = Math.pow(2, fftObj.customNextPow2(len));
//        System.out.println("N2=== "+N2);
//        Check each lead for powerline interference
        for (int leadIdx = 0; leadIdx < ecg.length; leadIdx++) {
//        for (int leadIdx = 0; leadIdx < 1; leadIdx++) {
            double[] peakValueAndFoundPeakFlag50= new double[2];
            double[] peakValueAndFoundPeakFlag60= new double[2];
            double[][] fftNew = fftObj.customSingleSidedSpectrumOnlyForFft(ecg[leadIdx]);
//            ldh.viewData(fftNew[0]);
            double[] magFft = new double[fftNew[0].length];
            for (int i = 0; i < fftNew[0].length; i++) {
                magFft[i] = mf.absolute(fftNew[0][i], fftNew[1][i]);
            }
            double[] freqAxis = new double[(int)N2];
            for (int i = 0; i < N2; i++) {
                freqAxis[i] = (double) Math.round( ((double) i) * (500.0 / N2) *10000) / 10000 ;
            }
            peakValueAndFoundPeakFlag50 = hasNearPeakFrequency(freqAxis, magFft, 50, frequencyRange, thresholdAmplitude);
            peakValueAndFoundPeakFlag60 = hasNearPeakFrequency(freqAxis, magFft, 60, frequencyRange, thresholdAmplitude);
            if (peakValueAndFoundPeakFlag50[0] == 1 || peakValueAndFoundPeakFlag60[0] == 1) {
                foundNoise = true;
                System.out.println("Lead " + leadIdx + " 50/60 Hz interference found (peak50= " + peakValueAndFoundPeakFlag50[1] +
                        ", peak60 = " + peakValueAndFoundPeakFlag60[1]);
                break;
            }
        }
        return foundNoise;
    }
*/


    //  edited on 15 July 2025
    public boolean[] conditionalNotchFiltered(double[][] ecg)  {
//        Define threshold for amplitude in the FFT
        FastFourierTransform fftObj = new FastFourierTransform(500);
        MatlabInbuiltFunctions mf = new MatlabInbuiltFunctions();
        double thresholdAmplitude = 5 * 10000;   // arbitrary, adjust per your data
        double snrThreshold5060 = 12.5;
        double frequencyRange = 0.5;    // +/- 0.5 Hz around 50 or 60
        boolean foundNoise = false;
        boolean[] foundNoiseArray = new boolean[ecg.length];
        int len = ecg[0].length;
        double N2 = Math.pow(2, fftObj.customNextPow2(len));
//        System.out.println("N2=== "+N2);
//        Check each lead for powerline interference
        for (int leadIdx = 0; leadIdx < ecg.length; leadIdx++) {
//        for (int leadIdx = 0; leadIdx < 1; leadIdx++) {
            double[] peakValueAndFoundPeakFlag50= new double[2];
            double[] peakValueAndFoundPeakFlag60= new double[2];
            double[][] fftNew = fftObj.customSingleSidedSpectrumOnlyForFft(ecg[leadIdx]);
//            ldh.viewData(fftNew[0]);
            double[] magFft = new double[fftNew[0].length];
            for (int i = 0; i < fftNew[0].length; i++) {
                magFft[i] = mf.absolute(fftNew[0][i], fftNew[1][i]);
            }
            double[] freqAxis = new double[(int)N2];
            for (int i = 0; i < N2; i++) {
                freqAxis[i] = (double) Math.round( ((double) i) * (500.0 / N2) *10000) / 10000 ;
            }
            peakValueAndFoundPeakFlag50 = hasNearPeakFrequency12July2025(freqAxis, magFft, 50, frequencyRange, thresholdAmplitude, snrThreshold5060);
            peakValueAndFoundPeakFlag60 = hasNearPeakFrequency12July2025(freqAxis, magFft, 60, frequencyRange, thresholdAmplitude, snrThreshold5060);
            if (peakValueAndFoundPeakFlag50[0] == 1 || peakValueAndFoundPeakFlag60[0] == 1) {
                foundNoiseArray[leadIdx] = true;
                System.out.println("Lead " + leadIdx + " 50/60 Hz interference found (peak50= " + peakValueAndFoundPeakFlag50[1] +
                        ", peak60 = " + peakValueAndFoundPeakFlag60[1]);
//                break;
            }
        }
        return foundNoiseArray;
    }


    public double[] myFIRFilter(double[] b, double[] a, double[] x) {
        /**
         * Applies a simple FIR filter to the input signal.
         *
         * @param b Numerator coefficients (filter kernel)
         * @param a Denominator coefficients (must be {1.0})
         * @param x Input signal array
         * @return Filtered signal array
         * @throws IllegalArgumentException if a is not [1.0] or arrays are invalid
         */
        if (b == null || a == null || x == null) {
            throw new IllegalArgumentException("Input arrays must not be null.");
        }

        if (a.length != 1 || a[0] != 1.0) {
            throw new IllegalArgumentException("This function only supports FIR filters (a = 1.0).");
        }

        int N = b.length;
        int L = x.length;
        double[] y = new double[L];

        for (int n = 0; n < L; n++) {
            for (int k = 0; k < N; k++) {
                if (n - k >= 0) {
                    y[n] += b[k] * x[n - k];
                }
            }
        }

        return y;
    }

    public double[] hasNearPeakFrequency(double[] freqAxis, double[] magFft, double centerFrequency, double bandwidth,double thresholdAmplitude) {
        double lowFrequency = centerFrequency - bandwidth;
        double highFrequency  = centerFrequency + bandwidth;
        LoaderHelper ldh = new LoaderHelper();
        double foundPeakFlag;
        double peakValue = -1;
        double[] peakValueAndFoundPeakFlag= new double[2];
        Utility ut = new Utility();
//        System.out.println(lowFrequency +"      "+highFrequency);
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < freqAxis.length; i++) {
            if (freqAxis[i] >= lowFrequency && freqAxis[i] <= highFrequency)
                list.add(i);
        }
        if (list.isEmpty())
        {
            foundPeakFlag = 0.0;
            peakValueAndFoundPeakFlag[0] = 0;
            peakValueAndFoundPeakFlag[1] = -1;
            return peakValueAndFoundPeakFlag;
        }
        double[] regionAmp = new double[list.size()];
        for (int j = 0; j < list.size(); j++) {
            regionAmp[j] = magFft[list.get(j)];
        }
//        ldh.viewData(regionAmp);
        peakValue = ut.findMax(regionAmp);
        if (peakValue > thresholdAmplitude)
            foundPeakFlag = 1;
        else
            foundPeakFlag = 0.0;
        peakValueAndFoundPeakFlag[0] = foundPeakFlag;
        peakValueAndFoundPeakFlag[1] = peakValue;
        return peakValueAndFoundPeakFlag;
    }

    //    15 July 2025
    public double[] hasNearPeakFrequency12July2025(double[] freqAxis, double[] magFft, double centerFrequency, double bandwidth,double thresholdAmplitude, double snrThreshold5060) {
        double lowFrequency = centerFrequency - bandwidth;
        double highFrequency  = centerFrequency + bandwidth;
        LoaderHelper ldh = new LoaderHelper();
        double foundPeakFlag;
        double peakValue = -1;
        double[] peakValueAndFoundPeakFlag= new double[2];
        Utility ut = new Utility();
//        System.out.println(lowFrequency +"      "+highFrequency);
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < freqAxis.length; i++) {
            if (freqAxis[i] >= lowFrequency && freqAxis[i] <= highFrequency)
                list.add(i);
        }
        if (list.isEmpty())
        {
            foundPeakFlag = 0.0;
            peakValueAndFoundPeakFlag[0] = 0;
            peakValueAndFoundPeakFlag[1] = -1;
            return peakValueAndFoundPeakFlag;
        }
        double[] regionAmp = new double[list.size()];
        for (int j = 0; j < list.size(); j++) {
            regionAmp[j] = magFft[list.get(j)];
        }
//        ldh.viewData(regionAmp);
        peakValue = ut.findMax(regionAmp);

//        double[] magX = list.stream().mapToDouble(e-> magFft[e]).toArray();
        ArrayList<Double> magX = new ArrayList<Double>();
        int j = 0;
        for (int i = 0; i < magFft.length; i++) {
            if ( j < list.size() && i == list.get(j) ) {
                j++;
            } else
                magX.add(magFft[i]);
        }
        double noiseFloor = ut.findMean(magX);
        double snr = peakValue / (noiseFloor + Math.ulp(1.0));
        if ( (peakValue > thresholdAmplitude) && ((snr >= 2.5 && snr <= 6) || (snr >= snrThreshold5060)) )
            foundPeakFlag = 1;
        else
            foundPeakFlag = 0.0;
        peakValueAndFoundPeakFlag[0] = foundPeakFlag;
        peakValueAndFoundPeakFlag[1] = peakValue;
        return peakValueAndFoundPeakFlag;
    }


    //    ------------------------ Private Methods ----------------------------------------------

    private double findMean(double[] data, int startIndex, int endIndex) {
        int n = (endIndex - startIndex) + 1;
        int index = 0;
        double[] arr = new double[n];
        for (int i = startIndex; i <= endIndex; i++) {
            arr[index] = data[i];
            index++;
        }
        Utility ut = new Utility();
        return ut.mean(arr);
    }

    private double[] fillColumnData(double[][] newDataArray, int n) {
        int len = newDataArray[0].length;
        double[] columnData = new double[len];
        for (int i = 0; i < len; i++) {
            columnData[i] = newDataArray[n][i];
        }
        return columnData;
    }

    public double[] customFiltFilt(double[] b50, double[] a50, double[] columnData) {
        double[] yForward = customFilter(b50, a50, columnData);
        double[] yReverse = reverse(yForward);
        double[] yBackward = customFilter(b50, a50, yReverse);
        double[] y = reverse(yBackward);
        return  y;
    }

    private double[] customFilterNew(double[] b, double[] a, double[] columnData) {
        int nSamples = columnData.length;
        int nOrder = Math.max(a.length,b.length) - 1;
//        System.out.println("Order==========    "+nOrder);
        double[] bNew = new double[nOrder+1];
        double[] aNew = new double[nOrder+1];
        for (int i = 0; i < bNew.length; i++) {
            if (i < b.length)
                bNew[i] = b[i];
            if (i < a.length)
                aNew[i] = a[i];
        }
        double[] y = new double[nSamples];
        double[] xPad = new double[nOrder + nSamples];
        for (int j = 0; j < xPad.length; j++) {
            if(j >= nOrder) {
                xPad[j] = columnData[j-nOrder];
            }
        }
        double[] yPad = new double[nOrder];
        for (int i = 0; i < nSamples; i++) {
            double xN = xPad[i + nOrder];
            double yN = bNew[0]  * xN;
            for (int k = 0; k < nOrder; k++) {
                double xK = xPad[i + (nOrder-1) - k];
                double yK = yPad[(nOrder-1) - k ];
                yN = yN + bNew[k+1] * xK - aNew[k+1] * yK;
            }
            y[i] = yN;
            if(yPad.length > 1) {
                yPad[0] = yPad[1];
                yPad[1] = yN;
            } else {
                yPad[0] = yN;
            }
        }
        return y;
    }

    private double[] customFilter(double[] b, double[] a, double[] columnData) {
        int nSamples = columnData.length;
        int nOrder = a.length - 1;
        double[] y = new double[nSamples];
        double[] xPad = new double[nOrder + nSamples];
        for (int j = 0; j < xPad.length; j++) {
            if(j >= nOrder) {
                xPad[j] = columnData[j-nOrder];
            }
        }
        double[] yPad = new double[nOrder];
        for (int i = 0; i < nSamples; i++) {
            double xN = xPad[i + nOrder];
            double yN = b[0]  * xN;
            for (int k = 0; k < nOrder; k++) {
                double xK = xPad[i + (nOrder-1) - k];
                double yK = yPad[(nOrder-1) - k ];
                yN = yN + b[k+1] * xK - a[k+1] * yK;     // Check this point indexing might be different
            }
            y[i] = yN;
            yPad[0] = yPad[1];
            yPad[1] = yN;
        }
        return y;
    }

    private double[] reverse(double[] arr) {
        double[] newArr = new double[arr.length];
        int n = arr.length;
        for (int i = 0; i < n; i++) {
            newArr[i] = arr[n-1-i];
        }
        return newArr;
    }

    private void display(double[] arr)
    {
        for (int i = 0; i < arr.length; i++) {
            System.out.println("i = "+i+"           "+arr[i]);
        }
    }

    private double[] fillOffSetData(int halfWindow) {
        double[] xvec = new double[(2 * halfWindow) + 1];
        for (int i = 0; i < xvec.length; i++) {
            xvec[i] = - halfWindow + i;
        }
//        display(xvec);
        return xvec;
    }

    private double[] customPolyfit(double[] x, double[] y, int deg) {
        if(x.length != y.length) {
            System.out.println("Error in customPolyfit method x and y length not equal");
            return new double[1];
        }
        int n = x.length;
        double[] p, rhs;
        double[][] A, aTranspose, lhs, lhsInverse;
//        Build Vandermonde matrix A
        A = new double[deg+1][n];
        for (int k = 0; k <= deg; k++) {
            for (int l = 0; l < x.length; l++) {
                A[k][l] = Math.pow(x[l], deg-k);
            }
        }
        MatrixFunctions mat = new MatrixFunctions();
        aTranspose = mat.transpose(A);
        lhs = new double[aTranspose.length][A[0].length];
        rhs = new double[A.length];
        try {
            lhs = mat.multiplyMatrices(A.length, A[0].length, A, aTranspose.length, aTranspose[0].length, aTranspose);
            rhs = simpleRowMultiplication(aTranspose, y);
            Inverse inv = new Inverse();
            lhsInverse = inv.findInverseOfMatrix(lhs);
            p = new double[lhsInverse.length];
            p = simpleRowMultiplication(lhsInverse, rhs);
            return p;
//            customPolyval(p,0);
        } catch (Exception e) {
            System.out.println("Issue in matrix multiplication");
            e.printStackTrace();
            return new double[1];
        }
    }

    private double[] customPolyval(double[] p, double[] x ) {
        double[] y = new double[x.length];
        for (int i = 0; i < p.length; i++) {
            for (int j = 0; j < y.length; j++) {
                y[j] = y[j] * x[j] + p[i];
            }
        }
        return y;
    }

    private double[] simpleRowMultiplication(double[][] aTranspose, double[] y) {
        double[] rhs = new double[aTranspose[0].length];
        for (int i = 0; i < aTranspose[0].length; i++) {
            double sum = 0;
            for (int j = 0; j < aTranspose.length; j++) {
                sum+= y[j] * aTranspose[j][i];
//                System.out.print(aTranspose[j][i]+", ");
            }
//            System.out.println();
            rhs[i] = sum;
        }
        return rhs;
    }


    private double[] fillData(int startIdx, int endIdx, double[] arr) {
        double[] newArr = new double[endIdx-startIdx+1];
//        System.out.println(startIdx+" , "+ endIdx);
        int idx = 0;
        for (int i = startIdx; i <= endIdx; i++) {
            newArr[idx] = arr[i];
            idx++;
        }
        return newArr;
    }



}
