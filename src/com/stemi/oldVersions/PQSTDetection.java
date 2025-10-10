package com.stemi.oldVersions;

import com.stemi.Filters;
import com.stemi.dataClasses.*;
import com.stemi.libs.Utility;

import java.util.ArrayList;

public class PQSTDetection {

    private Utility ut = new Utility();

    public AllCalculatedData detectPQSTFeatures(double[] ecg, int[] rPeaks, double baseLine, double fs) {
        //  Constants for amplitude conversion
//        com.arrthymia.RPeakDetection rPeakDetection = new com.arrthymia.RPeakDetection();
//        Loader ldh = new Loader();
//        ldh.viewData(rPeaks);
        Filters filters = new Filters();
        boolean flag = true;
//        System.out.println("rPeaks size====== "+rPeaks.length);
        StartAndEndIndexOfPoints startAndEndIndexOfPoints = new StartAndEndIndexOfPoints();
        int[] rrIntervalsInteger = new int[rPeaks.length-1];
        double[] rrIntervals = new double[rPeaks.length-1];
        double[] heartRate = new double[rPeaks.length-1];
        Features features = new Features();
        Amplitude amplitude = new Amplitude();
        Duration duration = new Duration();
        ArrayList<Double> stElevation = new ArrayList<Double>();
        double amplitudeToMv = 1.0/6250;      // Convert raw amplitude to mV
        double mvToMm = 10;
        // Calculate RR intervals in ms
        rrIntervalsInteger = ut.differentaition(rPeaks);
        for (int i = 0; i < rrIntervals.length; i++) {
            rrIntervals[i] = (double) rrIntervalsInteger[i];
            rrIntervals[i] = (rrIntervals[i]/fs) * 1000;
            heartRate[i] = 60 / ((double) rrIntervals[i] / 1000);
        }
        //  Adaptive bandpass filter for P-wave enhancement
        double[] pWaveBand = {0.5/ (fs/2), 15/ (fs/2)};       //  Frequency range for P-wave
        ArrayList<double[]> list = filters.pointDetectionButterFilter(4, pWaveBand, "bandpass");
        double[] b = list.get(0);
        double[] a = list.get(1);
        double[] ecgFiltered = filters.customFiltFiltNew(b, a, ecg);
//        com.arrthymia.LoaderHelper ldh = new com.arrthymia.LoaderHelper();
//        ldh.viewData(ecgFiltered);

        int rIdx = -1, qIdx = -1, sIdx = -1, rrInterval = -1, pIdxRR = -1, qStart = -1
                , sEnd = -1, pStart = -1;
        //  Process R-peaks, excluding the first and last cardiac cycles
        for (int i = 1; i < rPeaks.length-1; i++) {
            rIdx = rPeaks[i];
            // Store R peak information
            features.R.addIndex(rIdx);
            features.R.addValue(ecg[rIdx]);
            double rRawAmplitude = ecg[rIdx] - baseLine;
            amplitude.R.addColumnOneValue(rRawAmplitude);
            amplitude.R.addColumnTwoValue(rRawAmplitude * amplitudeToMv);
            amplitude.R.addColumnThreeValue(rRawAmplitude * amplitudeToMv * mvToMm);
            //    Detect Q wave
            try {
                qStart = (int) Math.max(0, rIdx - Math.round(0.06 * fs));
                qIdx = findLocalMin(ecg, qStart, rIdx);
                if (qIdx != -1) {
                    startAndEndIndexOfPoints.addQStart(qStart);
                    features.Q.addIndex(qIdx);
                    features.Q.addValue(ecg[qIdx]);
                    double qRawAmplitude = ecg[qIdx] - baseLine;
                    amplitude.Q.addColumnOneValue(qRawAmplitude);
                    amplitude.Q.addColumnTwoValue(qRawAmplitude * amplitudeToMv);
                    amplitude.Q.addColumnThreeValue(qRawAmplitude * amplitudeToMv * mvToMm);
                }
            } catch (Exception e) {
                System.out.println("Issue in Q wave calculation");
                e.printStackTrace();
            }
            try {
                // Detect S Peak
                sEnd = Math.min((rIdx + (int) Math.round(0.06 * fs)), ecg.length-1);
                sIdx = detectSPeak(ecg, rIdx, fs, baseLine);
                if (sIdx != -1) {
                    startAndEndIndexOfPoints.addSEnd(sEnd);
                    features.S.addIndex(sIdx);
                    features.S.addValue(ecg[sIdx]);
                    double sRawAmplitude = ecg[sIdx] - baseLine;
                    amplitude.S.addColumnOneValue(sRawAmplitude);
                    amplitude.S.addColumnTwoValue(sRawAmplitude * amplitudeToMv);
                    amplitude.S.addColumnThreeValue(sRawAmplitude * amplitudeToMv * mvToMm);
                }
            } catch (Exception e) {
                System.out.println("Issue in S wave calculation");
                e.printStackTrace();
            }
            //    Hybrid P Wave Detection with Window-Based Fallback
            try {
                if ((qIdx != -1)) // (&& i > 0 ) no need for this condition bcoz i starts from 1
                {
                    //    Calculate RR interval (current R peak to previous R peak)
                    rrInterval = rPeaks[i] - rPeaks[i - 1];       //RR interval in samples
                    //    Define dynamic range based on RR interval
//                    int pRangeStartRR = Math.max(0, qIdx - (int) Math.round(0.2 * rrInterval));    // Start 30% of RR interval before Q wave
//                    int pRangeEndRR = Math.max(0, qIdx - (int) Math.round(0.03 * rrInterval));  // End 3% of RR interval before Q wave
                    int pRangeStartRR = Math.max(0, qIdx - (int) Math.round(0.18 * rrInterval));    // Start 30% of RR interval before Q wave
                    int pRangeEndRR = Math.max(0, qIdx - (int) Math.round(0.04 * rrInterval));  // End 3% of RR interval before Q wave
                    //  Extract ECG data in the dynamic range
                    double[] pWindowDataRR = ut.fillDataIntoArray(pRangeStartRR, pRangeEndRR, ecgFiltered);
                    //    Find the maximum point in the dynamic range
                    int pIdxLocalRR = ut.findMaxIndex(pWindowDataRR);
                    pIdxRR = pRangeStartRR + pIdxLocalRR;   //  Convert to global index

                    //  Validate the detected P wave in the dynamic range
//                    valid_p_rr = ~isnan(p_idx_rr) && ...
//                    abs(ecg(p_idx_rr) - baseline) < 0.8 * abs(ecg(r_idx) - baseline) && ... % Amplitude < 80% of R wave
//                    p_idx_rr < q_idx; % Ensure P wave is before Q wave
                    if ((pIdxRR != -1) && (Math.abs(ecg[pIdxRR] - baseLine) > 0.0)
                            && (Math.abs(ecg[pIdxRR] - baseLine) < 0.8 * Math.abs(ecg[rIdx] - baseLine))
                            && (pIdxRR < qIdx)) {
                        startAndEndIndexOfPoints.addPStart(pRangeStartRR);
                        features.P.addIndex(pIdxRR);
                        features.P.addValue(ecg[pIdxRR]);
                        double pRawAmplitude = ecg[pIdxRR] - baseLine;
                        amplitude.P.addColumnOneValue(pRawAmplitude);
                        amplitude.P.addColumnTwoValue(pRawAmplitude * amplitudeToMv);
                        amplitude.P.addColumnThreeValue(pRawAmplitude * amplitudeToMv * mvToMm);
                    }
                    else if (qIdx != -1) {
                        flag = false;
                        //    else if (qIdx != -1) {  //This condition is not required already declared
                        flag = false;
                        System.out.println("Hybrid P Wave Detection else part need to be updated");
                        //    Fallback to window-based detection if no valid P wave is detected dynamically
                        //    Fixed window for P wave detection
                        int pRangeStartFixed = Math.max(0, qIdx - (int) Math.round(0.3 * fs));    // 300 ms before Q
                        int pRangeEndFixed = Math.max(0, qIdx - (int) Math.round(0.05 * fs));    // 50 ms before Q
                        double[] pWindowDataFixed = ut.fillDataIntoArray(pRangeStartFixed, pRangeEndFixed, ecgFiltered);
                        int pIdxLocalFixed = ut.findMaxIndex(pWindowDataFixed);
                        int pIdxFixed = pRangeStartFixed + pIdxLocalFixed;
//                        Validate the detected P wave in the fixed range
                        if (Math.abs(ecg[pIdxFixed] - baseLine) > 0.01 * (ut.findMax(ecg) - ut.findMin(ecg)))     // Validate amplitude
                        {
                            startAndEndIndexOfPoints.addPStart(pRangeStartFixed);
                            features.P.addIndex(pIdxFixed);
                            features.P.addValue(ecg[pIdxFixed]);
                            double pRawAmplitude = ecg[pIdxFixed] - baseLine;
                            amplitude.P.addColumnOneValue(pRawAmplitude);
                            amplitude.P.addColumnTwoValue(pRawAmplitude * amplitudeToMv);
                            amplitude.P.addColumnThreeValue(pRawAmplitude * amplitudeToMv * mvToMm);
                        } else {
                            //    If no valid P wave is detected, add placeholder
                            features.P.addIndex(0);
                            features.P.addValue(0);
                        }
                    }
                } else {
                    //    If no Q wave or invalid conditions, set placeholder
                    if (flag) {
                        features.P.addIndex(0);
                        features.P.addValue(0);
                    }
                }
            } catch (Exception e) {
                System.out.println("Issue in Hybrid P wave detection");
                e.printStackTrace();
            }
            try {
                //  Detect T wave with dynamic window based on RR interval
                // if i < length(r_peaks)   this condition is not required
                rrInterval = rPeaks[i + 1] - rPeaks[i];    //  Current RR interval in samples
                //  Define dynamic range based on RR interval
                int tRangeStartDynamic = rIdx + (int) Math.round(0.22 * rrInterval);      //  Start 22% into the RR interval
                int tRangeEndDynamic = rIdx + (int) Math.round(0.5 * rrInterval);   //  End at 50% of the RR interval
//                Ensure the range is within signal bounds
                tRangeStartDynamic = Math.min(Math.max(tRangeStartDynamic, 1), ecg.length-1);
                tRangeEndDynamic = Math.min(Math.max(tRangeEndDynamic, 1), ecg.length-1);
//                System.out.println(tRangeStartDynamic +" ,  "+tRangeEndDynamic);
                //  Extract ECG data in the dynamic range
                double[] tWindowDataDynamic = ut.fillDataIntoArray(tRangeStartDynamic, tRangeEndDynamic, ecg);
                //  Find the T-wave peak in the dynamic range
                int tMaxIdxDynamic = ut.findMaxIndex(tWindowDataDynamic);
                int tMinIdxDynamic = ut.findMinIndex(tWindowDataDynamic);
                double tMaxDynamic = ut.findMax(tWindowDataDynamic);
                double tMinDynamic = ut.findMin(tWindowDataDynamic);
                //  Select the T-wave peak based on deviation from baseline
                //   System.out.println(tMaxIdxDynamic+"  ,  "+tMinIdxDynamic);
                //    System.out.println(tMaxDynamic+"  ,  "+tMinDynamic);
                int tIdxDynamic = -1;
                if (Math.abs(tMaxDynamic - baseLine) > Math.abs(tMinDynamic - baseLine))
                    tIdxDynamic = tRangeStartDynamic + tMaxIdxDynamic;
                else
                    tIdxDynamic = tRangeStartDynamic + tMinIdxDynamic;
//                System.out.println("tIdxDynamic===  "+tIdxDynamic +"  rIdx==== "+rIdx +"   rPeaks[i+1]==== "+rPeaks[i+1]);
                //    Validate and store T-wave peak
                if ( (tIdxDynamic > rIdx) && (tIdxDynamic < rPeaks[i+1])) {  //Ensure T is after R and before the next R
                    startAndEndIndexOfPoints.addTStop(tRangeEndDynamic);
                    features.T.addIndex(tIdxDynamic);
                    features.T.addValue(ecg[tIdxDynamic]);
                    double tRawAmplitude = ecg[tIdxDynamic] - baseLine;
                    amplitude.T.addColumnOneValue(tRawAmplitude);
                    amplitude.T.addColumnTwoValue(tRawAmplitude * amplitudeToMv);
                    amplitude.T.addColumnThreeValue(tRawAmplitude * amplitudeToMv * mvToMm);
                }
                //    Fallback to fixed window if dynamic window fails
                if(features.T.Size()[0] == 0 || features.T.Size()[0] <= i )  {
                    int tRangeStartFixed = rIdx + (int) Math.round(0.1 * fs);  //  100 ms after R
                    int tRangeEndFixed = rIdx + (int) Math.round(0.5 * fs);   // 500 ms after R
                    tRangeStartFixed = Math.min(Math.max(tRangeStartFixed, 1), ecg.length - 1);
                    tRangeEndFixed = Math.min(Math.max(tRangeEndFixed, 1), ecg.length - 1);
//                    t_window_data_fixed = ecg(t_range_start_fixed:t_range_end_fixed);
                    double[] tWindowDataFixed = ut.fillDataIntoArray(tRangeStartFixed, tRangeEndFixed, ecg);
                    //    Find the T-wave peak in the fixed range
                    int tMaxIdxFixed = ut.findMaxIndex(tWindowDataFixed);
                    int tMinIdxFixed = ut.findMinIndex(tWindowDataFixed);
                    double tMaxFixed = ut.findMax(tWindowDataFixed);
                    double tMinFixed = ut.findMin(tWindowDataFixed);
                    //    Select the T-wave peak based on deviation from baseline
                    int tIdxFixed = -1;
                    if ( Math.abs(tMaxFixed - baseLine) > Math.abs(tMinFixed - baseLine) )
                        tIdxFixed = tRangeStartFixed + tMaxIdxFixed;
                    else
                        tIdxFixed = tRangeStartFixed + tMinIdxFixed;
                    //  Validate and store T-wave peak
                    if (tIdxFixed > rIdx) {
                        startAndEndIndexOfPoints.addTStop(tRangeEndDynamic);
                        features.T.addIndex(tIdxFixed);
                        features.T.addValue(ecg[tIdxFixed]);
                        double tRawAmplitude = ecg[tIdxFixed] - baseLine;
                        amplitude.T.addColumnOneValue(tRawAmplitude);
                        amplitude.T.addColumnTwoValue(tRawAmplitude * amplitudeToMv);
                        amplitude.T.addColumnThreeValue(tRawAmplitude * amplitudeToMv * mvToMm);
                    }
                }
            }catch (Exception e)  {
                System.out.println("Issue in T Wave Detection calculation");
                e.printStackTrace();
            }
            try {
                int jIdx = -1;
                if(sIdx != -1) {
                    jIdx = detectJPoint(ecg, sIdx, fs);
                    if(jIdx != -1) {
                        features.J.addIndex(jIdx);
                        features.J.addValue(ecg[jIdx]);
                        double jRawAmplitude = ecg[jIdx] - baseLine;
                        amplitude.J.addColumnOneValue(jRawAmplitude);
                        amplitude.J.addColumnTwoValue(jRawAmplitude * amplitudeToMv);
                        amplitude.J.addColumnThreeValue(jRawAmplitude * amplitudeToMv * mvToMm);
//                        Calculate ST elevation
                        stElevation.add(jRawAmplitude * amplitudeToMv);
                    }
                }
            }catch (Exception e)  {
                System.out.println("Issue in J Point Detection");
                e.printStackTrace();
            }
            try {
                if ( (features.P.Size()[0] != 0) && (qIdx != -1) ) {
                    //    P wave duration: Q wave index - P start index
                    int end = features.P.Size()[0] - 1;
              /*      duration.P.add( (int) Math.max(0, (((qIdx - features.P.getIndex(end)) / fs) * 1000 ) ) );
                    //    PR interval: R peak index - P start index
                    duration.PR.add( (int) Math.max(0,  (((rIdx - features.P.getIndex(end)) / fs) * 1000) ) );
      */
                    duration.P.add( (int) Math.max(0, ((qIdx - features.P.getIndex(end)) / fs) * 1000 ));
                    //    PR interval: R peak index - P start index
                    duration.PR.add( (int) Math.max(0, ((rIdx - features.P.getIndex(end)) / fs) * 1000) );
//  Just for testing 18 Mar 2025, above two lines are commented for testing
//                    int lastPEleIdx = startAndEndIndexOfPoints.getPStartSize() - 1;
//                    int lastQEleIdx = startAndEndIndexOfPoints.getQStartSize() - 1;
//                    duration.PR.add( (int) Math.max(0, ((startAndEndIndexOfPoints.getQStart(lastQEleIdx) -
//                            startAndEndIndexOfPoints.getPStart(lastPEleIdx)) / fs) * 1000) );
//                    duration.PR.add( (int) Math.max(0, ((qIdx -
//                            startAndEndIndexOfPoints.getPStart(lastPEleIdx)) / fs) * 1000) );

                }
                else{
                    duration.P.add(0);
                    duration.PR.add(0);
                }
                //  QRS duration: S wave index - Q wave index
                if ( (qIdx != -1) && (sIdx != -1) ) {
//                    duration.QRS.add((int) Math.max(0, ((sIdx - qIdx) / fs) * 1000));
//  Just for testing 18 Mar 2025, above two lines are commented for testing
                    int lastSEleIdx = startAndEndIndexOfPoints.getSEndSize() - 1;
//                    int lastQEleIdx = startAndEndIndexOfPoints.getQStartSize() - 1;
//                    duration.QRS.add((int) Math.max(0, ((startAndEndIndexOfPoints.getSEnd(lastSEleIdx) -
//                            startAndEndIndexOfPoints.getQStart(lastQEleIdx)) / fs) * 1000));
                    duration.QRS.add((int) Math.max(0, ((startAndEndIndexOfPoints.getSEnd(lastSEleIdx) -
                            qIdx) / fs) * 1000));
                }
                else
                    duration.QRS.add(0);
                if ((qIdx != -1) && (features.T.Size()[0] != 0) ) {
                    //    QT interval: T wave index - Q wave index
//                    int end = features.T.Size()[0] - 1;
//                    int qtInterval = (int) Math.max(0, ((features.T.getIndex(end) - qIdx) / fs) * 1000);
//  Just for testing 18 Mar 2025, above two lines are commented for testing
//                    int lastTEleIdx = startAndEndIndexOfPoints.getTEndSize() - 1;
                    int lastQEleIdx = startAndEndIndexOfPoints.getQStartSize() - 1;
//                    int qtInterval = (int) Math.max(0, ((startAndEndIndexOfPoints.getTEnd(lastTEleIdx)
//                            - startAndEndIndexOfPoints.getQStart(lastQEleIdx)) / fs) * 1000);

//                    System.out.println("Rahul  ---- tend " +startAndEndIndexOfPoints.getTEnd(lastTEleIdx)
//                    +" qIdx ====   "+qIdx);
                    int end = features.T.Size()[0] - 1;
                    int qtInterval = (int) Math.max(0, ((features.T.getIndex(end)
                            - startAndEndIndexOfPoints.getQStart(lastQEleIdx)) / fs) * 1000);

                    duration.QT.add(qtInterval);
                    //    QTc interval using Bazett's formula
                    if( (rrIntervals.length != 0) ) {
                        double rrIntervalSec = rrIntervals[i-1] / 1000; // Previous RR interval in seconds
                        double qtcInterval = qtInterval / Math.sqrt(rrIntervalSec);
                        duration.QTc.add(qtcInterval);
                    }else
                        duration.QTc.add(0.0);
                }else {
                    duration.QT.add(0);
                    duration.QTc.add(0.0);
                }
                //    Calculate durations for each wave
                if ( (qIdx != -1) )     // && (rIdx != -1) this condition not required
                    duration.Q.add((int) Math.max(0, ((rIdx - qIdx) / fs ) * 1000));  // Q wave duration in ms
                else
                    duration.Q.add(0);
                if ( (sIdx != -1))      // && (rIdx != -1) this condition not required
                    duration.R.add((int) Math.max(0, ((sIdx - rIdx) / fs ) * 1000));
                else
                    duration.R.add(0);
                if( (sIdx != -1) && (features.J.Size()[0] != 0)) {
                    int end = features.J.Size()[0] - 1;
                    duration.S.add((int) Math.max(0, ((features.J.getIndex(end) - sIdx) / fs) * 1000));  //S wave duration in ms
                }
                else
                    duration.S.add(0);
//                if( (features.T.Size()[0] != 0) && (features.T.Size()[0] > 1) ) {
                if( (features.T.Size()[0] > 1)) {
                    int end = features.T.Size()[0] - 1;
                    int tStart = features.T.getIndex(end-1);  // Use the second-to-last T wave start
                    int tStop = features.T.getIndex(end);   // Use the last T wave stop
                    duration.T.add((int) Math.max(0, ((tStop - tStart) / fs) * 1000)); //T wave duration in ms
                }
                else
                    duration.T.add(0);

            }catch(Exception e) {
                System.out.println("Issue in duration detection");
                e.printStackTrace();
            }
        }
        AllCalculatedData fullData = new AllCalculatedData();
        fullData.features = features;
        fullData.amplitude = amplitude;
        fullData.duration = duration;
        fullData.stElevation = stElevation;
        fullData.heartRate = heartRate;
        fullData.rrIntervals = rrIntervals;

//        features.displayFeaturesData(features.P);
//        amplitude.displayAmplitudeData(amplitude.J);
//        duration.displayDurationDoubleData(duration.QTc);
        return fullData;
    }

    public int detectJPoint(double[] ecg, int sIdx, double fs) {
        //  Define the search range for J-point (10–60 ms after S-point)
//        search_range = round(0.04 * fs):round(0.06 * fs); % 10–20 ms
        int tempStart = (int) Math.round(0.04 * fs);
        int tempEnd = (int) Math.round(0.06 * fs);
        int[] searchRange = new int[tempEnd-tempStart+1];
        int[] jRange = new int[tempEnd-tempStart+1];
        double[] slopes = new double[jRange.length-1];
        for (int i = 0; i < searchRange.length; i++) {
            searchRange[i] = tempStart + i;
            jRange[i] = searchRange[i] + sIdx;
        }
        //    Ensure the range is within bounds
        for (int i = 0; i < jRange.length; i++) {
            if (jRange[i] >= ecg.length) {
                System.out.println("jRange out of bound for i =" + i + "jRange[i] = " + jRange[i]);
                return -1;
            }
        }
        if(jRange.length == 1)
            return jRange[0];
        //  Detect inflection point or minimum slope change
        for (int i = 1; i < jRange.length; i++) {
            slopes[i-1] = Math.abs(ecg[jRange[i]] - ecg[jRange[i-1]]);
        }
        int minSlopeIdx = ut.findMinIndex(slopes);
//        j_point = j_range(min_slope_idx); % Map back to the original index
        return jRange[minSlopeIdx];
    }

    public int findLocalMin(double[] ecg, int startIdx, int endIdx) {
        // Ensure indices are within bounds
        int minIdx = -1;
        startIdx = Math.max(startIdx, 1);
        endIdx = Math.min(endIdx, ecg.length-1);
        // Find the index of the minimum value in the specified range
        double[] localMinArr  = ut.fillDataIntoArray(startIdx, endIdx, ecg);
        int localMinIdx = ut.findMinIndex(localMinArr);
        minIdx = startIdx + localMinIdx;
        return minIdx;
    }

    public int detectSPeak(double[] ecg, int rIdx, double fs, double baseline){
        //    Define the search window (20–60 ms after the R peak)
        int sIdx;
        int searchStart = rIdx + (int) Math.round(0.01 * fs);
        int searchEnd = rIdx + (int) Math.round(0.06 * fs);
        //    Ensure search range is within signal bounds
        searchStart = Math.max(searchStart, 1);
        searchEnd = Math.min(searchEnd, ecg.length-1);
        //  Search for the local minimum within the window
        double[] localMinArrSPeak = ut.fillDataIntoArray(searchStart, searchEnd, ecg);
        int minIdx = ut.findMinIndex(localMinArrSPeak);
        double minValue = ut.findMin(localMinArrSPeak);
        minIdx = minIdx + searchStart;
        //    Validate the S peak based on amplitude relative to the baseline
        if (Math.abs(minValue - baseline) > 0.02 * (ut.findMax(ecg) - ut.findMin(ecg)))     // Dynamic threshold
            sIdx = minIdx;
        else
            sIdx = -1;         // Return empty if no valid S peak is found
        return sIdx;
    }


}
