package com.stemi;

import com.stemi.dataClasses.QrsInfo;
import com.stemi.dataClasses.TwelveLeadEcgData;
import com.stemi.libs.Utility;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class SignalProcessing {

/*
    public void signalProcessing(double[][] dataArray, String fldr, String clientName) {
        //   Filter Parameters
        double Fs = 500;        // Sampling frequency (Hz)
        double f_low = 150;     // Low-pass filter cutoff frequency (Hz)
        double f_high = 0.5;    // High-pass filter cutoff frequency (Hz)
        double windowSize = Math.round(Fs / f_high);    // Window size for moving average subtraction
        double[] timeVector = new double[dataArray[0].length];
        for (int i = 0; i < timeVector.length; i++) {
            timeVector[i] = i / Fs;
        }
        com.arrthymia.Filters filters = new com.arrthymia.Filters();
        double[][] lowFilteredDataReal = filters.lowpass_filter_all(dataArray, Fs, f_low);
        String filter = "lowPass";
//        writeFile(lowFilteredDataReal, fldr, clientName, filter);
        double[][] highPassFilteredData = filters.highpass_filter_moving_average(lowFilteredDataReal, windowSize);
        Utility ut = new Utility();
        double[][] highPassFilteredNormalizedData = ut.normalizeToRawScale(highPassFilteredData, lowFilteredDataReal);
//        filter = "highPass";
//        writeFile(highPassFilteredNormalizedData, fldr, clientName, filter);
        double[][] notchFilteredData = filters.notch_filter_butterworth(highPassFilteredNormalizedData, Fs);
        double[][] notchFilteredDataNormalized = ut.normalizeToRawScale(notchFilteredData, dataArray);
//        filter = "notch";
//        writeFile(notchFilteredDataNormalized, fldr, clientName, filter);
    }

*/

    void filterProcessing(double[][] dataArray, OnResultCompleteListener onResultCompleteListener) {
        //   Filter Parameters
        double Fs = 500;        // Sampling frequency (Hz)
        double fLow = 150;     // Low-pass filter cutoff frequency (Hz)
        double fHigh = 0.3;    // High-pass filter cutoff frequency (Hz)
        AutoChosingFilterCorners autoChosingFilterCorners = new AutoChosingFilterCorners();
        Filters filters = new Filters();
        double[] lpfcAndHpfc = autoChosingFilterCorners.detectingFilterCorners(dataArray,Fs, filters);
        fLow = lpfcAndHpfc[0];
        fHigh = lpfcAndHpfc[1];
        double[][] sgData, finalData, calculationData;
        try {
            double[][] lowPassFilteredData = filters.lowpass_filter_all(dataArray, Fs, fLow);
            int polynomialOrder = 2;
            double[][] polySubData = filters.polynomialBaseLineSubtraction(lowPassFilteredData, polynomialOrder);
            lowPassFilteredData = null;
//            com.arrthymia.LoaderHelper ldh = new com.arrthymia.LoaderHelper();
//            ldh.viewData(polySubData);

            UpdatingSavitzkyGolay upt = new UpdatingSavitzkyGolay();
            boolean foundPointFiveHzNoise = upt.checkForPointFiveHzNoise(polySubData, fHigh);
            if (foundPointFiveHzNoise) {
                //  baseSeconds = max(0.5, min(4.0, 4.0 / max(hp_fc, 0.05)));
                //    window_size = 2*floor((baseSeconds*Fs)/2) + 1;   % odd
                //    window_size = min(window_size, 501);             % cap for speed
                //    window_size = max(window_size, 21);              % avoid tiny kernels
                double baseSeconds = Math.max(0.5, Math.min(4, 4 / Math.max(fHigh, 0.05)));
                int windowSizeForSavitzky = 2 * (int) Math.floor(baseSeconds * Fs / 2) + 1;
                windowSizeForSavitzky = Math.min(windowSizeForSavitzky, 501);
                windowSizeForSavitzky = Math.max(windowSizeForSavitzky, 21);

                int sgPolyOrder = 1;
                //1) SG smoothing (baseline-like) ----------
                double[] sgFilterNew = upt.customCalculateSavGolCoefficients(windowSizeForSavitzky, sgPolyOrder);
//                sgData = filters.fastSavitzkyGolayEcg(polySubData, sgFilterNew);
                sgData = filters.fastSavitzkyGolayBaseline(polySubData, sgFilterNew);
                if (windowSizeForSavitzky >= 501 && fHigh < 0.25)
                    sgData = filters.fastSavitzkyGolayBaseline(sgData, sgFilterNew);
                //  2) Choose alpha, then subtract at the output ----------
                double[] stdRatio = new double[sgData.length];
                Utility ut = new Utility();
                for (int i = 0; i < sgData.length; i++) {
                    stdRatio[i] = ut.calculateStd(Arrays.stream(sgData[i]).boxed().collect(Collectors.toCollection(ArrayList::new)))
                            / ut.calculateStd(Arrays.stream(polySubData[i]).boxed().collect(Collectors.toCollection(ArrayList::new))) + Math.ulp(1.0);
                }
                double medRatio = ut.medianDouble(Arrays.stream(stdRatio).boxed().collect(Collectors.toCollection(ArrayList::new)));
                // Piecewise safeguard
                double alpha = 0.3;
                if (medRatio < 0.90)
                    alpha = 1;
                else if (medRatio < 0.97)
                    alpha = 0.6;
//                double zz = polySubData[0][5009];
//                double zz2 = sgData[0][5009];
                for (int i = 0; i < sgData.length; i++) {
                    for (int e = 0; e < sgData[i].length; e++)
                        sgData[i][e] = polySubData[i][e] - alpha * sgData[i][e];
                }
            }else {
                sgData = polySubData;
            }
            upt = null;
//            polySubData = null;
            boolean[] foundNoise = filters.conditionalNotchFiltered(sgData);
            finalData = new double[dataArray.length][dataArray[0].length];
            calculationData = new double[finalData.length][finalData[0].length];
            for (int i = 0; i < dataArray.length; i++) {
                if (foundNoise[i]) {
                    // FinalData is NotchFilteredData, if noise found
                    System.out.println("Applying 50/60 Hz Notch Filter for Lead_"+i+"...");
                    calculationData[i] = filters.notch_filter_butterworthNew_Individual(sgData[i], Fs);
                    finalData[i] = filters.notchFilterButterworthNewIndividualQualityFactorOne(sgData[i], Fs);
                } else {
                    calculationData[i] = sgData[i];
                    finalData[i] = sgData[i];
                }
            }
            sgData = null;

//            String fldrOut = "D:\\RAHUL\\DownloadsRahul\\fldrOut";
//            writeFile(finalData, fldrOut, "Vishal_Medanta_raw.txt","a");
//            LoaderHelper ldh = new LoaderHelper();
//            ldh.viewData(finalData[0]);

            boolean flag = false;

            // Commented on 13 Nov 2025
//            for (int lead = 0; lead < finalData.length; lead++) {
//                QrsInfo qrsInfo = checkForPanTompkins(finalData[lead], Fs, filters, lead);
//                for(boolean x : qrsInfo.getQrsPresent())
//                {
//                    if (x) {
//                        flag = x;
//                        break;
//                    }
//                }
//            }

            // Below code updated on 13 Nov 2025
            for (int lead = 0; lead < calculationData.length; lead++) {
                QrsInfo qrsInfo = checkForPanTompkins(calculationData[lead], Fs, filters, lead);
                for(boolean x : qrsInfo.getQrsPresent())
                {
                    if (x) {
                        flag = x;
                        break;
                    }
                }
            }
            for (int i = 0; i < calculationData.length; i++) {
                calculationData[i] = filters.movingAverageForWindowSizeOfTwo(calculationData[i]);
            }

//            LoaderHelper ldh = new LoaderHelper();
//            ldh.viewData(calculationData);
            filters = null;
            System.gc();
            PointDetectionNew pt = new PointDetectionNew(finalData);
            if (!flag) {
                TwelveLeadEcgData twelveLeadEcgData = pt.createTwelveLeadData(onResultCompleteListener);
                HashMap<String, Double> hashMap = new HashMap<>();
                hashMap.put("heartRate", 0.0);
                hashMap.put("prInterval", 0.0);
                hashMap.put("qrsInterval", 0.0);
                hashMap.put("qtInterval", 0.0);
                hashMap.put("qtcInterval", 0.0);
                String arrhythmia = "Since valid point detection has not been performed, the data is not qualified for arrhythmia detection.";
                String stemi = "Since valid point detection has not been performed, the data is not qualified for stemi detection.";
                String ischemia = "Since valid point detection has not been performed, the data is not qualified for ischemia detection.";
                arrhythmia += "\n "+stemi+"\n "+ischemia;
                onResultCompleteListener.onCompletedLead2MetaData(twelveLeadEcgData, hashMap, arrhythmia);
                return;
            }
            pt.findPointsForAllColumns(Fs, onResultCompleteListener, calculationData);

        } catch (Exception e) {
            onResultCompleteListener.onFailed("Error in filterProcessing() method");
            e.printStackTrace();
        }


//        onResultCompleteListener.onComplete(twelveLeadEcgData);
    }

    private QrsInfo checkForPanTompkins(double[] ecgSignalPerLead, double fs, Filters filters, int lead) {
        Utility ut = new Utility();
        int n = ecgSignalPerLead.length;
        double[] ecgMoved = filters.movingAverage(ecgSignalPerLead, 9.0);
        double[] ecgDiffOld = ut.differentaition(ecgMoved);
        double[] ecgDiffNew = new double[ecgDiffOld.length + 1];
        ecgDiffNew = Arrays.copyOf(ecgDiffOld, ecgDiffOld.length + 1);
        ecgDiffNew[ecgDiffNew.length - 1] = ecgDiffOld[ecgDiffOld.length - 1];
        double slopeRange = ut.findMax(ecgDiffNew);
    //    Robust Asystole check: low amplitude (in raw counts) OR low slope
        double rawThreshold = 0.05 * 6250;
        double ampRange = ut.findMax(ecgMoved) - ut.findMin(ecgMoved);
        ArrayList<Integer> qrsLocal = new ArrayList<Integer>();
        ArrayList<Double> qrsAmplitude = new ArrayList<Double>();
        QrsInfo qrsInfo = new QrsInfo();
        if (ampRange < rawThreshold || slopeRange < rawThreshold)
        {
            System.out.println("Asystole (flatline) detected in Lead_"+lead+" : no QRS complexes'");
            boolean[] qrsPresent = new boolean[n];
            boolean[] qrsAbsent = new boolean[n];
            Arrays.fill(qrsAbsent, true);
            qrsInfo.setQrsLocal(qrsLocal); qrsInfo.setQrsAmplitude(qrsAmplitude);
            qrsInfo.setQrsPresent(qrsPresent); qrsInfo.setQrsAbsent(qrsAbsent);
            return qrsInfo;
        } else {
            double[] sq = DoubleStream.of(ecgDiffNew).map(e -> e * e).toArray();
            double movingWindow = Math.round(0.150 * fs);
            double[] h = new double[(int) movingWindow];
            Arrays.fill(h, (double) Math.round((1.0/movingWindow) * 10000)  / 10000 );
//                double[] movingWindowIntegration = ut.convolutionCustom(sq, h);
            double[] movingWindowIntegration = filters.customConvSame(sq, h);
//                double[] movingWindowIntegration = ut.convolutionCustomForNotch(sq, h);
            double[] thresholdVector = new double[movingWindowIntegration.length];
            //  Adaptive threshold initialization
            double spki = 0.2 * ut.findMax(movingWindowIntegration);
            double npki = 0.5 * spki;
            double thresholdOne = npki + 0.25 * (spki - npki);
            double refractory = Math.round(.200 * fs);
            ArrayList<Integer> lastQrsArrayList = new ArrayList<>();
            int lastQrs   = -1;
            double amplitudeThreshold = 0.10 * ampRange;
            //  Peak detection loop
            for (int j = 1; j < movingWindowIntegration.length -1; j++) {
                thresholdVector[j] = thresholdOne;
                if (movingWindowIntegration[j] > movingWindowIntegration[j-1] && movingWindowIntegration[j] >= movingWindowIntegration[j+1]) {
                    if (movingWindowIntegration[j] > thresholdOne && lastQrsArrayList.isEmpty() || j - lastQrs > refractory) {
                        int sw = (int) Math.round(0.050 * fs);
                        int idx0 = Math.max(j - sw, 1);
                        double[] patchArr = ut.fillDataIntoArray(idx0, j, ecgMoved);
                        int iMax = ut.findMaxIndex(patchArr);
                        int rLoc = idx0 + iMax - 1;
                        if (ecgMoved[rLoc] > amplitudeThreshold) {
                            qrsLocal.add(rLoc);
                            qrsAmplitude.add(ecgMoved[rLoc]);
                            lastQrs = j;
                            lastQrsArrayList.add(lastQrs);
                            spki = 0.25 * movingWindowIntegration[j] + 0.75 * spki;
                        } else
                            npki = 0.125 * movingWindowIntegration[j] + 0.875 * npki;
                    } else
                        npki = 0.125 * movingWindowIntegration[j] + 0.875 * npki;

                }
                thresholdOne = npki + 0.25 * (spki - npki);
            }
            // Fallback for pacemaker rhythm
            if (qrsLocal.isEmpty()) {
                System.out.println("Paced-rhythm fallback: selecting segment maxima for Lead_"+lead);
                int winFallback = (int) fs;
                int end = ecgSignalPerLead.length - winFallback;
                for (int i = 0; i < end; i = i + winFallback) {
                    double[] seg = ut.fillDataIntoArray(i, (i + winFallback - 1), ecgMoved);
                    int maxIndex = ut.findMaxIndex(seg);
                    int rLocs = i + maxIndex;
                    qrsLocal.add(rLocs);
                    qrsAmplitude.add(ecgMoved[rLocs]);
                }
                ArrayList<ArrayList<Integer>> uniqueList = ut.customUnique(qrsLocal);
                qrsLocal = uniqueList.get(0);
                qrsAmplitude = uniqueList.get(1).stream().map(e-> ecgMoved[e]).collect(Collectors.
                        toCollection(ArrayList<Double> :: new ));
            }

            //  fill edges for plotting
            thresholdVector[0] = thresholdVector[1];
            thresholdVector[thresholdVector.length - 1] = thresholdVector[thresholdVector.length - 2];
            //  Build presence/absence vectors
            boolean[] qrsPresent = new boolean[n];
            boolean[] qrsAbsent = new boolean[n];
            Arrays.fill(qrsAbsent, true);
            for (int j = 0; j < qrsLocal.size(); j++) {
                qrsPresent[qrsLocal.get(j)] = true;
                qrsAbsent[qrsLocal.get(j)] = false;
            }
            qrsInfo.setQrsLocal(qrsLocal); qrsInfo.setQrsAmplitude(qrsAmplitude);
            qrsInfo.setQrsPresent(qrsPresent); qrsInfo.setQrsAbsent(qrsAbsent);
            return qrsInfo;
        }
    }

    public void writeFile(double[][] arr, String fldr, String clientName, String filterName) {
//        clientName = clientName.split("_raw")[0];
        try {
            PrintWriter outputStream;
//            outputStream = new PrintWriter(fldr + File.separator + "Result" + File.separator + clientName + "_lowPass_9Col_filtered.txt");
//            outputStream = new PrintWriter(fldr + File.separator + "Result_Notch" + File.separator + clientName + "_notch_9Col_filtered.txt");
            outputStream = new PrintWriter(fldr + File.separator +  File.separator + clientName );
//            outputStream.println(String.format("%-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s", "Lead1", "Lead2", "Lead3", "V1", "V2", "V3", "V4", "V5", "V6"));
            if (filterName.equals("error")) {
                outputStream.println(String.format("Unable to read file data. "));
            } else {
                for (int i = 0; i < arr[0].length; i++) {
                    int a = 0;
                    outputStream.println(String.format("%-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s", arr[a][i], arr[a + 1][i], arr[a + 2][i], arr[a + 3][i], arr[a + 4][i], arr[a + 5][i], arr[a + 6][i], arr[a + 7][i], arr[a + 8][i]));
                }
            }
            outputStream.close();
            System.out.println(clientName + " File created successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
