package com.stemi;

import com.stemi.dataClasses.*;
import com.stemi.libs.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PQSTDetectionNew {

    private Utility ut = new Utility();


    /*// Method Updated on 08 August 2025
    // Method before 16 Oct 2025
     public AllCalculatedDataNew detectPQSTFeatures(double[] ecg, int[] rPeaks, double baseLine, double fs) {
        //  Constants for amplitude conversion
        Filters filters = new Filters();
        boolean flag = true;
        StartAndEndIndexOfPoints startAndEndIndexOfPoints = new StartAndEndIndexOfPoints();
        int[] rrIntervalsInteger = new int[rPeaks.length-1];
        double[] rrIntervals = new double[rPeaks.length-1];
        double[] heartRate = new double[rPeaks.length-1];
        Features features = new Features();
        Amplitude amplitude = new Amplitude();
        DurationNew duration = new DurationNew();
        ArrayList<Double> stElevation = new ArrayList<Double>();
        ArrayList<Double> stSagitaMv = new ArrayList<>();
        ArrayList<Integer> stMorphologyCode = new ArrayList<>();
        ArrayList<Boolean> stTombstoneFlag = new ArrayList<>();
        double amplitudeToMv = 1.0/6250;      // Convert raw amplitude to mV
        double mvToMm = 10;
        double rrThreshold = 0;
        // Calculate RR intervals in ms
        rrIntervalsInteger = ut.differentaition(rPeaks);
        for (int i = 0; i < rrIntervals.length; i++) {
            rrIntervals[i] = (double) rrIntervalsInteger[i];
            rrIntervals[i] = (rrIntervals[i]/fs) * 1000;
            heartRate[i] = (double) Math.round( (60 / ((double) rrIntervals[i] / 1000)) * 100 ) / 100;
        }
        //  Adaptive bandpass filter for P-wave enhancement
        double[] pWaveBand = {0.5/ (fs/2), 15/ (fs/2)};       //  Frequency range for P-wave
        ArrayList<double[]> list = filters.pointDetectionButterFilter(4, pWaveBand, "bandpass");
        double[] b = list.get(0);
        double[] a = list.get(1);
        double[] ecgFiltered = filters.customFiltFiltNew(b, a, ecg);
//        com.arrthymia.LoaderHelper ldh = new com.arrthymia.LoaderHelper();
//        ldh.viewData(a);
        int rIdx = -1, qIdx = -1, sIdx = -1, rrInterval = -1, pIdxRR = -1, qStart = -1, qEnd = -1
                , sEnd = -1, pStart = -1, qIdxTemp = -1;
        //  Process R-peaks, excluding the first and last cardiac cycles
        for (int i = 1; i < rPeaks.length-1; i++) {
            double qRawAmplitude = 0, rRawAmplitude = 0;
            int pStartLocal = -1, pEndLocal = -1;
            rIdx = rPeaks[i];
            // Store R peak information
            features.R.addIndex(rIdx);
            features.R.addValue(ecg[rIdx]);
            rRawAmplitude = ecg[rIdx] - baseLine;
            amplitude.R.addColumnOneValue(rRawAmplitude);
            amplitude.R.addColumnTwoValue(rRawAmplitude * amplitudeToMv);
            amplitude.R.addColumnThreeValue(rRawAmplitude * amplitudeToMv * mvToMm);
            //    Detect Q wave
            try {
//                double roundTemp =  Math.round(0.06 * fs);
//                qStart = (int) Math.max(0, rIdx - Math.round(0.06 * fs));
                qStart = (int) Math.max(0, rIdx - Math.round(0.035 * fs));
                qEnd = (int) Math.max(0, rIdx - Math.round(0.01 * fs));         // 10 ms before R
                if (qEnd > qStart) {
                    qIdxTemp = findLocalMin(ecg, qStart, qEnd);
                    double rawAmplitudeQTemp = ecg[qIdxTemp] - baseLine;
                    rawAmplitudeQTemp = rawAmplitudeQTemp * amplitudeToMv;
                    if (rawAmplitudeQTemp < -0.08)
                        qIdx = qIdxTemp;
                    else
                        qIdx = qIdxTemp + 5;
                }
                if (qIdx != -1) {
                    startAndEndIndexOfPoints.addQStart(qStart);
                    startAndEndIndexOfPoints.addQEnd(qEnd);
                    features.Q.addIndex(qIdx);
                    features.Q.addValue(ecg[qIdx]);
                    qRawAmplitude = ecg[qIdx] - baseLine;
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
//                sEnd = Math.min((rIdx + (int) Math.round(0.06 * fs)), ecg.length-1);
                sIdx = detectSPeak(ecg, rIdx, fs, baseLine, startAndEndIndexOfPoints);
                if (sIdx != -1) {
//                    startAndEndIndexOfPoints.addSEnd(sEnd);
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
//                    double thrSamples = Math.round(0.57 * fs);
//                    double thrSamplesTwo = Math.round(1.40 * fs);
                    double fracStart;
                    double thrSamples = 350;
                    double thrSamplesTwo = 700;
                    int pRangeStartRR = -1, pRangeEndRR = -1;
                    if (rrInterval > thrSamples && rrInterval <= thrSamplesTwo) {
                        fracStart =  0.35;
                    } else if (rrInterval > thrSamplesTwo) {
                        fracStart = 0.30;
                    } else {
                        fracStart = 0.32;
                    }
                    double fracEnd = 0.04;
                    pRangeStartRR = Math.max(0, qIdx - (int) Math.round(fracStart * rrInterval));    // Start 30% of RR interval before Q wave
                    pRangeEndRR = Math.max(0, qIdx - (int) Math.round(fracEnd * rrInterval));
                    double[] pWaveBandWithOne = {1/ (fs/2), 15/ (fs/2)};       //  Frequency range for P-wave
                    ArrayList<double[]> list2 = filters.pointDetectionButterFilter(5, pWaveBandWithOne, "bandpass");
                    double[] bBp = list.get(0);
                    double[] aBp = list.get(1);
                    double[] pWindowDataRR = ut.fillDataIntoArray(pRangeStartRR, pRangeEndRR, ecgFiltered);
                    double[] filtSeg = filters.customFiltFiltNew(bBp, aBp, pWindowDataRR);
                    //    Find the maximum point in the dynamic range
//                    int pIdxLocalRR = ut.findMaxIndex(pWindowDataRR);
                    int pIdxLocalRR = ut.findMaxIndex(filtSeg);
                    pIdxRR = pRangeStartRR + pIdxLocalRR;   //  Convert to global index
                    double[] segRR = pWindowDataRR;
                    double dt = 1/fs;
                    //     7a) Smooth the window with a 20 ms moving‐average
                    double winMs = 40;
                    int winPts = (int) Math.max(2, Math.round((winMs/1000) * fs));
                    if (winPts%2==0){
                        winPts = winPts+1;
                    }
                    double[] smoothed = filters.movingAverage(segRR, winPts);
                    //  7c) Threshold at 30% of peak absolute slope
                    double[] slopesSm = ut.differentaition(smoothed);
                    for (int j = 0; j < slopesSm.length; j++) {
                        slopesSm[j] = slopesSm[j] / dt;
                    }
//                    ldh.viewData(slopesSm);
                    double thr = 0.45 * ut.findMaxAbsolute(slopesSm);
                    int[] indicesLocation = ut.findFirstAndLastIndexWhenSlopesExceedsThresholdValue(slopesSm, thr);
                    int onsetLoc = indicesLocation[0];
                    int offsetLoc = indicesLocation[1];
                    if (onsetLoc == -1) {
                        onsetLoc = 0;
                    }
                    if (offsetLoc == -1) {
                        offsetLoc = slopesSm.length-1;
                    }
                    int pStartNew = pRangeStartRR + onsetLoc;
                    int pEndNew = pRangeStartRR + offsetLoc + 1 + 1;
                    pStartLocal = pStartNew;
                    pEndLocal = pEndNew;
                    startAndEndIndexOfPoints.addPStart(pStartNew);
                    startAndEndIndexOfPoints.addPEnd(pEndNew);
                    //  Validate the detected P wave in the dynamic range
                    if ((pIdxRR != -1) && (Math.abs(ecg[pIdxRR] - baseLine) > 0.0)
                            && (Math.abs(ecg[pIdxRR] - baseLine) < 0.8 * Math.abs(ecg[rIdx] - baseLine))
                            && (pIdxRR < qIdx)) {
                        features.P.addIndex(pIdxRR);
                        features.P.addValue(ecg[pIdxRR]);
                        double pRawAmplitude = ecg[pIdxRR] - baseLine;
                        amplitude.P.addColumnOneValue(pRawAmplitude);
                        amplitude.P.addColumnTwoValue(pRawAmplitude * amplitudeToMv);
                        amplitude.P.addColumnThreeValue(pRawAmplitude * amplitudeToMv * mvToMm);
                    }
                    else {
                        //  Fallback to fixed window if invalid
                        flag = false;
                        //    else if (qIdx != -1) {  //This condition is not required already declared
                        System.out.println("Hybrid P Wave Detection else part need to be updated");
                        //    Fallback to window-based detection if no valid P wave is detected dynamically
                        //    Fixed window for P wave detection
                        int pRangeStartFixed = Math.max(0, qIdx - (int) Math.round(0.40 * fs));    // 350 ms before Q
                        int pRangeEndFixed = Math.max(0, qIdx - (int) Math.round(0.05 * fs));    // 50 ms before Q
                        double[] pWindowDataFixed = ut.fillDataIntoArray(pRangeStartFixed, pRangeEndFixed, ecgFiltered);
                        int pIdxLocalFixed = ut.findMaxIndex(pWindowDataFixed);
                        int pIdxFixed = pRangeStartFixed + pIdxLocalFixed;
//                        Validate the detected P wave in the fixed range
                        if (Math.abs(ecg[pIdxFixed] - baseLine) > 0.01 * (ut.findMax(ecg) - ut.findMin(ecg)))     // Validate amplitude
                        {
                            features.P.addIndex(pIdxFixed);
                            features.P.addValue(ecg[pIdxFixed]);
                            pStartLocal = Math.max(0, pIdxFixed - (int) Math.round(0.04 * fs));
                            pEndLocal = Math.min(ecg.length - 1, pIdxFixed + (int) Math.round(0.05 * fs));
                            double pRawAmplitude = ecg[pIdxFixed] - baseLine;
                            amplitude.P.addColumnOneValue(pRawAmplitude);
                            amplitude.P.addColumnTwoValue(pRawAmplitude * amplitudeToMv);
                            amplitude.P.addColumnThreeValue(pRawAmplitude * amplitudeToMv * mvToMm);
                        } else {
                            //    If no valid P wave is detected, add placeholder
                            features.P.addIndex(0);
                            features.P.addValue(0);
                            amplitude.P.addColumnOneValue(0);
                            amplitude.P.addColumnTwoValue(0);
                            amplitude.P.addColumnThreeValue(0);
                        }
                    }
                } else {
                    //    If no Q wave or invalid conditions, set placeholder
                    if (flag) {
                        features.P.addIndex(0);
                        features.P.addValue(0);
                        amplitude.P.addColumnOneValue(0);
                        amplitude.P.addColumnTwoValue(0);
                        amplitude.P.addColumnThreeValue(0);
                    }
                }
            } catch (Exception e) {
                System.out.println("Issue in Hybrid P wave detection");
                e.printStackTrace();
            }
            try {
                //  Detect T wave with dynamic window based on RR interval
                // if i < length(r_peaks)   this condition is not required
                double startFrac, endFrac;
                rrInterval = rPeaks[i + 1] - rPeaks[i];    //  Current RR interval in samples
                //  Define dynamic range based on RR interval
                rrThreshold = 300;
                double rrThresholdTwo = 500;
                boolean tWaveDetected = false;
                // Adjust T detection window fractions
                if (rrInterval < rrThreshold )
                {
                    startFrac = 0.20;
                    endFrac = 0.60;
                } else if (rrInterval > rrThresholdTwo) {
                    startFrac = 0.10;
                    endFrac = 0.45;
                } else {
                    startFrac = 0.10;
                    endFrac = 0.50;
                }
                //  3) Build & bound dynamic window
                int tStart = rIdx + (int) Math.round(startFrac * rrInterval);      //  Start 22% into the RR interval
                int tStop = rIdx + (int) Math.round(endFrac * rrInterval);   //  End at 50% of the RR interval
                tStart = Math.max(0, Math.min(tStart, ecg.length - 1));
                tStop = Math.max(0, Math.min(tStop, ecg.length - 1));
//                Ensure the range is within signal bounds
//                System.out.println(tRangeStartDynamic +" ,  "+tRangeEndDynamic);
                //  Extract ECG data in the dynamic range
                double[] seg = new double[0];
                if (tStop > tStart)
                    seg = ut.fillDataIntoArray(tStart, tStop, ecg);
                if (seg != null ) {
                    //    5) Find T-peak (largest absolute deflection)
                    int tMaxIdx = ut.findMaxIndex(seg);
                    int tMinIdx = ut.findMinIndex(seg);
                    double tMax = ut.findMax(seg);
                    double tMin = ut.findMin(seg);
                    int tIdx, peakLoc = -1;
                    if (Math.abs(tMax - baseLine) > Math.abs(tMin - baseLine)) {
                        tIdx = tStart + tMaxIdx;
                        peakLoc = tMaxIdx;
                    }
                    else  {
                        tIdx = tStart + tMinIdx;
                        peakLoc = tMinIdx;
                    }
                    //  6) Slope-based refinement of T_Start/T_End
                    double dt = 1/fs;
                    double winMs = 20;
                    double winSamples = Math.max(3, Math.round((winMs/1000) * fs));
                    if (winSamples % 2 == 0)
                        winSamples++;
                    double[] segSmooth = filters.movingAverage(seg, winSamples);
                    double[] slope = ut.differentaition(segSmooth);
                    for (int j = 0; j < slope.length; j++) {
                        slope[j] = slope[j] / dt;
                    }
                    double thr = 0.2 * ut.findMaxAbsolute(slope);
                    //  find the first and last time the slope exceeds thr
                    double[] pre = ut.fillDataIntoArray(0, peakLoc - 1, slope);
                    int loc1 = ut.findFirstIndexWhenSlopesExceedsThresholdValue(pre, thr);
                    if (loc1 == -1)
                        loc1 = 0;
//                    T_Start = t_start + loc1 - 1;
//                    T_Start = min(T_Start, t_idx-1);
                    int tStartNew = tStart + loc1;
                    tStartNew = Math.min(tStartNew, tIdx - 1);
                    //  6b) T_End: last negative slope after peak
//                            post = slp(peakLoc:end);
//                    loc2 = find(post<-thr,1,'last');
                    double[] post = ut.fillDataIntoArray(peakLoc, slope.length-1, slope);
                    int loc2 = ut.customFindLastIndexWhenSlopesFallShortOffThresholdValue(post, -thr);
                    if (loc2 == -1)
                        loc2 = slope.length-1;
                    else
                        loc2 = loc2 + peakLoc;
                    // T_End in matlab = tStopNew in java.
                    int tStopNew = tStart + loc2;
                    tStopNew = Math.max(tStopNew, tIdx + 1);
                    //  7) Store starts/ends
                    startAndEndIndexOfPoints.addTStart( tStartNew );
                    startAndEndIndexOfPoints.addTStop( tStopNew );
                    //    Validate and store T-wave peak
                    if ((tIdx > rIdx) && (tIdx < rPeaks[i + 1])) {  //Ensure T is after R and before the next R
                        features.T.addIndex(tIdx);
                        features.T.addValue(ecg[tIdx]);
                        double tRawAmplitude = ecg[tIdx] - baseLine;
                        amplitude.T.addColumnOneValue(tRawAmplitude);
                        amplitude.T.addColumnTwoValue(tRawAmplitude * amplitudeToMv);
                        amplitude.T.addColumnThreeValue(tRawAmplitude * amplitudeToMv * mvToMm);
                    }
                    tWaveDetected = true;
                }

                //    Fallback to fixed window if dynamic window fails
//                if(features.T.Size()[0] == 0 || features.T.Size()[0] <= i )  {  // commented on 12 May 2025
                if ( !tWaveDetected ) {

                    int peakLocFallback = -1;
                    if(tStop > tStart) {
//                    t_window_data_fixed = ecg(t_range_start_fixed:t_range_end_fixed);
                        double[] tWindowFallback = ut.fillDataIntoArray(tStart, tStop, ecg);
                        //    Find the T-wave peak in the fixed range
                        int tMaxIdxFallback = ut.findMaxIndex(tWindowFallback);
                        int tMinIdxFallback = ut.findMinIndex(tWindowFallback);
                        double tMaxFallback = ut.findMax(tWindowFallback);
                        double tMinFallback = ut.findMin(tWindowFallback);
                        //    Select the T-wave peak based on deviation from baseline
                        int tIdxFallback = -1;
                        if (Math.abs(tMaxFallback - baseLine) > Math.abs(tMinFallback - baseLine)) {
                            tIdxFallback = tStart + tMaxIdxFallback;
                            peakLocFallback = tMaxIdxFallback;
                        }
                        else {
                            tIdxFallback = tStart + tMinIdxFallback;
                            peakLocFallback = tMinIdxFallback;
                        }
                        //  NEW: slope‐based refinement in fallback too ---
                        // 2) **Smooth** the fallback window
                        double dt = 1/fs;
                        int winMs = 20;
                        int winSamples = (int) Math.max(2, Math.round((double) winMs/1000) * fs);
                        double[] segFallbackFixed = filters.movingAverage(tWindowFallback, winSamples);
                        //  3) Compute slopes on smoothed fallback
                        double[] slopesFallback = ut.differentaition(segFallbackFixed);
                        for (int j = 0; j < slopesFallback.length; j++) {
                            slopesFallback[j] = slopesFallback[i] / dt;
                        }
                        double thrFallback = 0.2 * ut.findMaxAbsolute(slopesFallback);
                        //  4) Onset
                        double[] preFallback = ut.fillDataIntoArray(0, peakLocFallback -1, slopesFallback);
                        int startLocFallback = ut.findFirstIndexWhenAbsoluteSlopeValuesExceedsThresholdValue(preFallback, thrFallback);
                        if (startLocFallback == -1)
                            startLocFallback = 0;
                        //  5) Offset
                        double[] postFallback = ut.fillDataIntoArray(peakLocFallback, slopesFallback.length-1, slopesFallback);
                        int endLocFallback = ut.findLastIndexWhenAbsoluteSlopeValuesExceedsThresholdValue(postFallback, thrFallback);
                        if (endLocFallback == -1)
                            endLocFallback = slopesFallback.length - 1;
                        else
                            endLocFallback = endLocFallback + peakLocFallback;
                        int tStartFallbackNew = tStart + startLocFallback;
                        int tStopFallbackNew = tStart + endLocFallback;
                        startAndEndIndexOfPoints.addTStart(tStartFallbackNew);
                        startAndEndIndexOfPoints.addTStop(tStopFallbackNew);
                        //  Validate and store T-wave peak
                        if (tIdxFallback > rIdx) {
                            features.T.addIndex(tIdxFallback);
                            features.T.addValue(ecg[tIdxFallback]);
                            double tRawAmplitude = ecg[tIdxFallback] - baseLine;
                            amplitude.T.addColumnOneValue(tRawAmplitude);
                            amplitude.T.addColumnTwoValue(tRawAmplitude * amplitudeToMv);
                            amplitude.T.addColumnThreeValue(tRawAmplitude * amplitudeToMv * mvToMm);
                        }
                    }
                }
            }catch (Exception e)  {
                System.out.println("Issue in T Wave Detection calculation");
                e.printStackTrace();
            }
            int jIdx = -1;
            try {
                if(sIdx != -1) {
                    jIdx = detectJPoint(ecg, sIdx, fs, startAndEndIndexOfPoints);
                    if(jIdx != -1) {
                        features.J.addIndex(jIdx);
                        features.J.addValue(ecg[jIdx]);
                        double zz = ecg[jIdx];
                        double jRawAmplitude = ecg[jIdx] - baseLine;
                        amplitude.J.addColumnOneValue(jRawAmplitude);
                        amplitude.J.addColumnTwoValue(jRawAmplitude * amplitudeToMv);
                        amplitude.J.addColumnThreeValue(jRawAmplitude * amplitudeToMv * mvToMm);
//                        Calculate ST elevation
                        stElevation.add(jRawAmplitude * amplitudeToMv);

                        //  Concavity of ST segemnt elevation check
                        //---- ST at J, J+60 ms, J+80 ms (mV) ----
                        int off60 = (int) Math.round(0.060 * fs);
                        int off80 = (int) Math.round(0.080 * fs);
                        int idxJ60 = Math.min(ecg.length-1, jIdx + off60);
                        int idxJ80 = Math.min(ecg.length-1, jIdx + off80);
                        double stJMv = (ecg[jIdx] - baseLine) * amplitudeToMv;
                        double stJ60Mv = (ecg[idxJ60] - baseLine) * amplitudeToMv;
                        double stJ80Mv = (ecg[idxJ80] - baseLine) * amplitudeToMv;
                        int tOnForSt = -1;
                        if (!startAndEndIndexOfPoints.tStart.isEmpty() ) {
                            int tStart = startAndEndIndexOfPoints.getTStart(startAndEndIndexOfPoints.getTStartSize() - 1);
                            if (tStart > jIdx + Math.round(0.02 * fs))
                                tOnForSt = tStart;
                            else
                                tOnForSt = Math.min(ecg.length - 1, jIdx + off80);
                        } else
                            tOnForSt = Math.min(ecg.length-1, jIdx + off80);
                        SagittaData sagitta = stSagittaSimple(ecg, fs, baseLine, jIdx, tOnForSt, amplitudeToMv);
                        stSagitaMv.add(sagitta.getSagittaMv());
                        stMorphologyCode.add(sagitta.getCode());
                        stTombstoneFlag.add(sagitta.getTombStoneFlag());
                    }
                }
            }catch (Exception e)  {
                System.out.println("Issue in J Point Detection");
                e.printStackTrace();
            }
            try {
               *//* if ( (features.P.Size()[0] != 0) && (qIdx != -1) ) {
                    //    P wave duration: Q wave index - P start index
                    int end = features.P.Size()[0] - 1;
                    duration.P.add( (int) Math.max(0, ((qIdx - features.P.getIndex(end)) / fs) * 1000 ));
                    //    PR interval: R peak index - P start index
                    duration.PR.add( (int) Math.max(0, ((rIdx - features.P.getIndex(end)) / fs) * 1000) );
                }
                *//*
                if ( startAndEndIndexOfPoints.getPStartSize() != 0 && startAndEndIndexOfPoints.getPEndSize() != 0 ) {
                    int pStartLastEle = startAndEndIndexOfPoints.getPStartSize() - 1;
                    int pEndLastEle = startAndEndIndexOfPoints.getPEndSize() - 1;
                    int curentEnd = startAndEndIndexOfPoints.getPEnd(pEndLastEle);
                    int currentStart = startAndEndIndexOfPoints.getPStart(pStartLastEle);

//      Before 13 Aug 2025
//                    duration.P.add( Math.max(0, ( (curentEnd - currentStart) / fs) * 1000) );
//                    duration.PR.add( Math.max(0, (qIdx - currentStart) / fs * 1000 ) );

                    duration.P.add( Math.max(0, ( (pEndLocal - pStartLocal) / fs) * 1000) );
                    if (qRawAmplitude < 280 && rRawAmplitude < 0.06)
                        duration.PR.add( Math.max(0, (sIdx - pStartLocal) / fs * 1000 ) );
                    else
                        duration.PR.add( Math.max(0, (qIdx - pStartLocal) / fs * 1000 ) );
                }
                else{
                    duration.P.add(0.0);
                    duration.PR.add(0.0);
                }
                //  QRS duration: S wave index - Q wave index
                if ( (qIdx != -1) && (jIdx != -1) ) {
                    duration.QRS.add((int) Math.max(0, ((jIdx - qIdx) / fs) * 1000));
//                    System.out.println("jIdx--   "+jIdx+"      qIdx--   "+qIdx);
//                    duration.QRS.add((int) Math.max(0, (jIdx - qIdx) ));
                }
                else
                    duration.QRS.add(0);
                if ((qIdx != -1) && (features.T.Size()[0] != 0) ) {
                    //    QT interval: T wave index - Q wave index
//                    int end = features.T.Size()[0] - 1;
//                    int qtInterval = (int) Math.max(0, ((features.T.getIndex(end) - qIdx) / fs) * 1000);
//  Just for testing 18 Mar 2025, above two lines are commented for testing
//                    int lastTEleIdx = startAndEndIndexOfPoints.getTEndSize() - 1;
//                    int qtInterval = (int) Math.max(0, ((startAndEndIndexOfPoints.getTEnd(lastTEleIdx)
//                            - startAndEndIndexOfPoints.getQStart(lastQEleIdx)) / fs) * 1000);

//                    System.out.println("Rahul  ---- tend " +startAndEndIndexOfPoints.getTEnd(lastTEleIdx)
//                    +" qIdx ====   "+qIdx);
//                    int end = features.T.Size()[0] - 1;
//                    int temp = features.T.getIndex(end);
//                    int qtInterval = (int) Math.max(0, ((features.T.getIndex(end) - qIdx) / fs) * 1000);
//                    duration.QT.add(qtInterval);

                    int temp = startAndEndIndexOfPoints.getTEndSize() - 1 ;
                    int zz1 = startAndEndIndexOfPoints.getTEnd(temp);
                    int qtInterval = (int) Math.max(0, ((startAndEndIndexOfPoints.getTEnd(temp) - qIdx) / fs) * 1000);
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
//                    duration.R.add((int) Math.max(0, ((sIdx - qIdx) / fs ) * 1000));
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
//        AllCalculatedData fullData = new AllCalculatedData();
        AllCalculatedDataNew fullData = new AllCalculatedDataNew();
        fullData.features = features;
        fullData.amplitude = amplitude;
        fullData.duration = duration;
        fullData.stElevation = stElevation;
        fullData.heartRate = heartRate;
        fullData.rrIntervals = rrIntervals;
        fullData.startAndEndIndexOfPoints = startAndEndIndexOfPoints;
        fullData.stSagitaMv = stSagitaMv;
        fullData.stMorphologyCode = stMorphologyCode;
        fullData.stTombstoneFlag = stTombstoneFlag;

//        features.displayFeaturesData(features.P);
//        amplitude.displayAmplitudeData(amplitude.J);
//        duration.displayDurationDoubleData(duration.QTc);
        return fullData;
    }

*/

    // Method before 16 Oct 2025
    public AllCalculatedDataNew detectPQSTFeatures(double[] ecg, int[] rPeaks, double baseLine, double fs) {
        //  Constants for amplitude conversion
        Filters filters = new Filters();
        boolean flag = true;
        StartAndEndIndexOfPoints startAndEndIndexOfPoints = new StartAndEndIndexOfPoints();
        int[] rrIntervalsInteger = new int[rPeaks.length-1];
        double[] rrIntervals = new double[rPeaks.length-1];
        double[] heartRate = new double[rPeaks.length-1];
        Features features = new Features();
        Amplitude amplitude = new Amplitude();
        DurationNew duration = new DurationNew();
        ArrayList<Double> stElevation = new ArrayList<Double>();
        ArrayList<Double> stSagitaMv = new ArrayList<>();
        ArrayList<Integer> stMorphologyCode = new ArrayList<>();
        ArrayList<Boolean> stTombstoneFlag = new ArrayList<>();
        double amplitudeToMv = 1.0/6250;      // Convert raw amplitude to mV
        double mvToMm = 10;
        double rrThreshold = 0;
        // Calculate RR intervals in ms
        rrIntervalsInteger = ut.differentaition(rPeaks);
        for (int i = 0; i < rrIntervals.length; i++) {
            rrIntervals[i] = (double) rrIntervalsInteger[i];
            rrIntervals[i] = (rrIntervals[i]/fs) * 1000;
            heartRate[i] = (double) Math.round( (60 / ((double) rrIntervals[i] / 1000)) * 100 ) / 100;
        }
        //  Adaptive bandpass filter for P-wave enhancement
        double[] pWaveBand = {0.5/ (fs/2), 15/ (fs/2)};       //  Frequency range for P-wave
        ArrayList<double[]> list = filters.pointDetectionButterFilter(4, pWaveBand, "bandpass");
        double[] b = list.get(0);
        double[] a = list.get(1);
        double[] ecgFiltered = filters.customFiltFiltNew(b, a, ecg);
//        com.arrthymia.LoaderHelper ldh = new com.arrthymia.LoaderHelper();
//        ldh.viewData(a);
        int rIdx = -1, qIdx = -1, sIdx = -1, rrInterval = -1, pIdxRR = -1, qStart = -1, qEnd = -1
                , sEnd = -1, pStart = -1, qIdxTemp = -1;
        //  Process R-peaks, excluding the first and last cardiac cycles
        for (int i = 1; i < rPeaks.length-1; i++) {
            double qRawAmplitude = 0, rRawAmplitude = 0;
            int pStartLocal = -1, pEndLocal = -1;
            rIdx = rPeaks[i];
            // Store R peak information
            features.R.addIndex(rIdx);
            features.R.addValue(ecg[rIdx]);
            rRawAmplitude = ecg[rIdx] - baseLine;
            amplitude.R.addColumnOneValue(rRawAmplitude);
            amplitude.R.addColumnTwoValue(rRawAmplitude * amplitudeToMv);
            amplitude.R.addColumnThreeValue(rRawAmplitude * amplitudeToMv * mvToMm);
            //    Detect Q wave
            try {
                qStart = (int) Math.max(0, rIdx - Math.round(0.040 * fs));
                qEnd = (int) Math.max(0, rIdx - Math.round(0.01 * fs));         // 10 ms before R
                if (qEnd > qStart) {
//                    qIdxTemp = findLocalMin(ecg, qStart, qEnd);
//                    double rawAmplitudeQTemp = ecg[qIdxTemp] - baseLine;
//                    rawAmplitudeQTemp = rawAmplitudeQTemp * amplitudeToMv;
//                    if (rawAmplitudeQTemp < -0.08)
//                        qIdx = qIdxTemp;
//                    else
//                        qIdx = qIdxTemp + 5;

                    double[] segQ = ut.fillDataIntoArray(qStart, qEnd, ecg);
                    double dt = 1 / fs;
                    double[] dQ = Arrays.stream(ut.differentaition(segQ)).map( e -> ( (double) Math.round(
                            (e / dt) * 100) / 100 ) ).toArray();
                    int[] zcMinArray = IntStream.range(0, dQ.length - 1).filter(e ->  (dQ[e] < 0) && (dQ[e+1] >= 0) ).toArray();
                    ArrayList<Integer> zcMinList = new ArrayList<>();
                    if (zcMinArray.length > 0) {
                        for (int j = 0; j < zcMinArray.length; j++) {
                            boolean keep = (zcMinArray[j] >= 1) && zcMinArray[j] <= segQ.length - 1;
                            if (keep)
                                zcMinList.add(zcMinArray[j]);
                        }
                    }
                    int qIdx0 = -1;
                    if ( !zcMinList.isEmpty() ) {
                        final int qStartFinal  = qStart;
                        ArrayList<Integer> candAbs = zcMinList.stream().map(zcMin -> (zcMin + qStartFinal )).
                                collect(Collectors.toCollection(ArrayList::new));
//                        qIdx0 = ut.findMinInIntegerList(candAbs);
                        double[] ecgCandAbs = candAbs.stream().mapToDouble(e -> ecg[e]).toArray();
                        int k = ut.findMinIndex(ecgCandAbs);
                        qIdx0 = candAbs.get(k);

                    }
                    else {
                        int kMin = ut.findMinIndexAbsolute(Arrays.stream(dQ).toArray());
                        qIdx0 = qStart + kMin;
                    }
                    if ((qIdx0 == -1) || (qIdx0 < qStart) || (qIdx0 > qEnd) ) {
                        int kMin = ut.findMinIndex(Arrays.stream(segQ).toArray());
                        qIdx0 = qStart + kMin;
                    }
                    //   Depth check (mV) to nudge shallow Q slightly toward R
                    double qMv = (ecg[qIdx0] - baseLine) * amplitudeToMv;
                    if (qMv > -0.08) {
                        int bump = Math.max(0, (int) Math.round(0.005 * fs));
                        qIdx = Math.min(qIdx0 + bump,  Math.max(rIdx - 1, qStart));
                    } else
                        qIdx = qIdx0;


                }
                if (qIdx != -1) {
                    startAndEndIndexOfPoints.addQStart(qStart);
                    startAndEndIndexOfPoints.addQEnd(qEnd);
                    features.Q.addIndex(qIdx);
                    features.Q.addValue(ecg[qIdx]);
                    qRawAmplitude = ecg[qIdx] - baseLine;
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
//                sEnd = Math.min((rIdx + (int) Math.round(0.06 * fs)), ecg.length-1);
                sIdx = detectSPeak(ecg, rIdx, fs, baseLine, startAndEndIndexOfPoints);
                if (sIdx != -1) {
//                    startAndEndIndexOfPoints.addSEnd(sEnd);
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
//                    double thrSamples = Math.round(0.57 * fs);
//                    double thrSamplesTwo = Math.round(1.40 * fs);
                    double fracStart;
                    double thrSamples = 350;
                    double thrSamplesTwo = 700;
                    int pRangeStartRR = -1, pRangeEndRR = -1;
                    if (rrInterval > thrSamples && rrInterval <= thrSamplesTwo) {
                        fracStart =  0.35;
                    } else if (rrInterval > thrSamplesTwo) {
                        fracStart = 0.30;
                    } else {
                        fracStart = 0.32;
                    }
                    double fracEnd = 0.04;
                    pRangeStartRR = Math.max(0, qIdx - (int) Math.round(fracStart * rrInterval));    // Start 30% of RR interval before Q wave
                    pRangeEndRR = Math.max(0, qIdx - (int) Math.round(fracEnd * rrInterval));
                    double[] pWaveBandWithOne = {1/ (fs/2), 15/ (fs/2)};       //  Frequency range for P-wave
                    ArrayList<double[]> list2 = filters.pointDetectionButterFilter(5, pWaveBandWithOne, "bandpass");
                    double[] bBp = list.get(0);
                    double[] aBp = list.get(1);
                    double[] pWindowDataRR = ut.fillDataIntoArray(pRangeStartRR, pRangeEndRR, ecgFiltered);
                    double[] filtSeg = filters.customFiltFiltNew(bBp, aBp, pWindowDataRR);
                    //    Find the maximum point in the dynamic range
//                    int pIdxLocalRR = ut.findMaxIndex(pWindowDataRR);
                    int pIdxLocalRR = ut.findMaxIndex(filtSeg);
                    pIdxRR = pRangeStartRR + pIdxLocalRR;   //  Convert to global index
                    double[] segRR = pWindowDataRR;
                    double dt = 1/fs;
                    //     7a) Smooth the window with a 20 ms moving‐average
                    double winMs = 40;
                    int winPts = (int) Math.max(2, Math.round((winMs/1000) * fs));
                    if (winPts%2==0){
                        winPts = winPts+1;
                    }
                    double[] smoothed = filters.movingAverage(segRR, winPts);
                    //  7c) Threshold at 30% of peak absolute slope
                    double[] slopesSm = ut.differentaition(smoothed);
                    for (int j = 0; j < slopesSm.length; j++) {
                        slopesSm[j] = slopesSm[j] / dt;
                    }
//                    ldh.viewData(slopesSm);
                    double thr = 0.45 * ut.findMaxAbsolute(slopesSm);
                    int[] indicesLocation = ut.findFirstAndLastIndexWhenSlopesExceedsThresholdValue(slopesSm, thr);
                    int onsetLoc = indicesLocation[0];
                    int offsetLoc = indicesLocation[1];
                    if (onsetLoc == -1) {
                        onsetLoc = 0;
                    }
                    if (offsetLoc == -1) {
                        offsetLoc = slopesSm.length-1;
                    }
                    int pStartNew = pRangeStartRR + onsetLoc;
                    int pEndNew = pRangeStartRR + offsetLoc + 1 + 1;
                    pStartLocal = pStartNew;
                    pEndLocal = pEndNew;
                    startAndEndIndexOfPoints.addPStart(pStartNew);
                    startAndEndIndexOfPoints.addPEnd(pEndNew);
                    //  Validate the detected P wave in the dynamic range
                    if ((pIdxRR != -1) && (Math.abs(ecg[pIdxRR] - baseLine) > 0.0)
                            && (Math.abs(ecg[pIdxRR] - baseLine) < 0.8 * Math.abs(ecg[rIdx] - baseLine))
                            && (pIdxRR < qIdx)) {
                        features.P.addIndex(pIdxRR);
                        features.P.addValue(ecg[pIdxRR]);
                        double pRawAmplitude = ecg[pIdxRR] - baseLine;
                        amplitude.P.addColumnOneValue(pRawAmplitude);
                        amplitude.P.addColumnTwoValue(pRawAmplitude * amplitudeToMv);
                        amplitude.P.addColumnThreeValue(pRawAmplitude * amplitudeToMv * mvToMm);
                    }
                    else {
                        //  Fallback to fixed window if invalid
                        flag = false;
                        //    else if (qIdx != -1) {  //This condition is not required already declared
                        System.out.println("Hybrid P Wave Detection else part need to be updated");
                        //    Fallback to window-based detection if no valid P wave is detected dynamically
                        //    Fixed window for P wave detection
                        int pRangeStartFixed = Math.max(0, qIdx - (int) Math.round(0.40 * fs));    // 350 ms before Q
                        int pRangeEndFixed = Math.max(0, qIdx - (int) Math.round(0.05 * fs));    // 50 ms before Q
                        double[] pWindowDataFixed = ut.fillDataIntoArray(pRangeStartFixed, pRangeEndFixed, ecgFiltered);
                        int pIdxLocalFixed = ut.findMaxIndex(pWindowDataFixed);
                        int pIdxFixed = pRangeStartFixed + pIdxLocalFixed;
//                        Validate the detected P wave in the fixed range
                        if (Math.abs(ecg[pIdxFixed] - baseLine) > 0.01 * (ut.findMax(ecg) - ut.findMin(ecg)))     // Validate amplitude
                        {
                            features.P.addIndex(pIdxFixed);
                            features.P.addValue(ecg[pIdxFixed]);
                            pStartLocal = Math.max(0, pIdxFixed - (int) Math.round(0.04 * fs));
                            pEndLocal = Math.min(ecg.length , pIdxFixed + (int) Math.round(0.05 * fs));

                            startAndEndIndexOfPoints.pStart.set(startAndEndIndexOfPoints.getPStartSize() -1 , pStartLocal);
                            startAndEndIndexOfPoints.pEnd.set(startAndEndIndexOfPoints.getPEndSize() - 1, pEndLocal);

                            double pRawAmplitude = ecg[pIdxFixed] - baseLine;
                            amplitude.P.addColumnOneValue(pRawAmplitude);
                            amplitude.P.addColumnTwoValue(pRawAmplitude * amplitudeToMv);
                            amplitude.P.addColumnThreeValue(pRawAmplitude * amplitudeToMv * mvToMm);
                        } else {
                            //    If no valid P wave is detected, add placeholder
                            features.P.addIndex(0);
                            features.P.addValue(0);
                            amplitude.P.addColumnOneValue(0);
                            amplitude.P.addColumnTwoValue(0);
                            amplitude.P.addColumnThreeValue(0);
                        }
                    }
                } else {
                    //    If no Q wave or invalid conditions, set placeholder
                    if (flag) {
                        features.P.addIndex(0);
                        features.P.addValue(0);
                        amplitude.P.addColumnOneValue(0);
                        amplitude.P.addColumnTwoValue(0);
                        amplitude.P.addColumnThreeValue(0);
                    }
                }
            } catch (Exception e) {
                System.out.println("Issue in Hybrid P wave detection");
                e.printStackTrace();
            }
            try {
                //  Detect T wave with dynamic window based on RR interval
                // if i < length(r_peaks)   this condition is not required
                double startFrac, endFrac;
                rrInterval = rPeaks[i + 1] - rPeaks[i];    //  Current RR interval in samples
                //  Define dynamic range based on RR interval
                rrThreshold = 300;
                double rrThresholdTwo = 500;
                boolean tWaveDetected = false;
                // Adjust T detection window fractions
                if (rrInterval < rrThreshold )
                {
                    startFrac = 0.20;
                    endFrac = 0.60;
                } else if (rrInterval > rrThresholdTwo) {
                    startFrac = 0.10;
                    endFrac = 0.45;
                } else {
                    startFrac = 0.10;
                    endFrac = 0.50;
                }
                //  3) Build & bound dynamic window
                int tStart = rIdx + (int) Math.round(startFrac * rrInterval);      //  Start 22% into the RR interval
                int tStop = rIdx + (int) Math.round(endFrac * rrInterval);   //  End at 50% of the RR interval
                tStart = Math.max(0, Math.min(tStart, ecg.length - 1));
                tStop = Math.max(0, Math.min(tStop, ecg.length - 1));
//                Ensure the range is within signal bounds
//                System.out.println(tRangeStartDynamic +" ,  "+tRangeEndDynamic);
                //  Extract ECG data in the dynamic range
                double[] seg = new double[0];
                if (tStop > tStart)
                    seg = ut.fillDataIntoArray(tStart, tStop, ecg);
                if (seg != null ) {
                    //    5) Find T-peak (largest absolute deflection)
                    int tMaxIdx = ut.findMaxIndex(seg);
                    int tMinIdx = ut.findMinIndex(seg);
                    double tMax = ut.findMax(seg);
                    double tMin = ut.findMin(seg);
                    int tIdx, peakLoc = -1;
                    if (Math.abs(tMax - baseLine) > Math.abs(tMin - baseLine)) {
                        tIdx = tStart + tMaxIdx;
                        peakLoc = tMaxIdx;
                    }
                    else  {
                        tIdx = tStart + tMinIdx;
                        peakLoc = tMinIdx;
                    }
                    //  6) Slope-based refinement of T_Start/T_End
                    double dt = 1/fs;
                    double winMs = 20;
                    double winSamples = Math.max(3, Math.round((winMs/1000) * fs));
                    if (winSamples % 2 == 0)
                        winSamples++;
                    double[] segSmooth = filters.movingAverage(seg, winSamples);
                    double[] slope = ut.differentaition(segSmooth);
                    for (int j = 0; j < slope.length; j++) {
                        slope[j] = slope[j] / dt;
                    }
                    double thr = 0.2 * ut.findMaxAbsolute(slope);
                    //  find the first and last time the slope exceeds thr
                    double[] pre = ut.fillDataIntoArray(0, peakLoc - 1, slope);
                    int loc1 = ut.findFirstIndexWhenSlopesExceedsThresholdValue(pre, thr);
                    if (loc1 == -1)
                        loc1 = 0;
//                    T_Start = t_start + loc1 - 1;
//                    T_Start = min(T_Start, t_idx-1);
                    int tStartNew = tStart + loc1;
                    tStartNew = Math.min(tStartNew, tIdx - 1);
                    //  6b) T_End: last negative slope after peak
//                            post = slp(peakLoc:end);
//                    loc2 = find(post<-thr,1,'last');
                    double[] post = ut.fillDataIntoArray(peakLoc, slope.length-1, slope);
                    int loc2 = ut.customFindLastIndexWhenSlopesFallShortOffThresholdValue(post, -thr);
                    if (loc2 == -1)
                        loc2 = slope.length-1;
                    else
                        loc2 = loc2 + peakLoc;
                    // T_End in matlab = tStopNew in java.
                    int tStopNew = tStart + loc2;
                    tStopNew = Math.max(tStopNew, tIdx + 1);
                    //  7) Store starts/ends
                    startAndEndIndexOfPoints.addTStart( tStartNew );
                    startAndEndIndexOfPoints.addTStop( tStopNew );
                    //    Validate and store T-wave peak
                    if ((tIdx > rIdx) && (tIdx < rPeaks[i + 1])) {  //Ensure T is after R and before the next R
                        features.T.addIndex(tIdx);
                        features.T.addValue(ecg[tIdx]);
                        double tRawAmplitude = ecg[tIdx] - baseLine;
                        amplitude.T.addColumnOneValue(tRawAmplitude);
                        amplitude.T.addColumnTwoValue(tRawAmplitude * amplitudeToMv);
                        amplitude.T.addColumnThreeValue(tRawAmplitude * amplitudeToMv * mvToMm);
                    }
                    tWaveDetected = true;
                }

                //    Fallback to fixed window if dynamic window fails
//                if(features.T.Size()[0] == 0 || features.T.Size()[0] <= i )  {  // commented on 12 May 2025
                if ( !tWaveDetected ) {

                    int peakLocFallback = -1;
                    if(tStop > tStart) {
//                    t_window_data_fixed = ecg(t_range_start_fixed:t_range_end_fixed);
                        double[] tWindowFallback = ut.fillDataIntoArray(tStart, tStop, ecg);
                        //    Find the T-wave peak in the fixed range
                        int tMaxIdxFallback = ut.findMaxIndex(tWindowFallback);
                        int tMinIdxFallback = ut.findMinIndex(tWindowFallback);
                        double tMaxFallback = ut.findMax(tWindowFallback);
                        double tMinFallback = ut.findMin(tWindowFallback);
                        //    Select the T-wave peak based on deviation from baseline
                        int tIdxFallback = -1;
                        if (Math.abs(tMaxFallback - baseLine) > Math.abs(tMinFallback - baseLine)) {
                            tIdxFallback = tStart + tMaxIdxFallback;
                            peakLocFallback = tMaxIdxFallback;
                        }
                        else {
                            tIdxFallback = tStart + tMinIdxFallback;
                            peakLocFallback = tMinIdxFallback;
                        }
                        //  NEW: slope‐based refinement in fallback too ---
                        // 2) **Smooth** the fallback window
                        double dt = 1/fs;
                        int winMs = 20;
                        int winSamples = (int) Math.max(2, Math.round((double) winMs/1000) * fs);
                        double[] segFallbackFixed = filters.movingAverage(tWindowFallback, winSamples);
                        //  3) Compute slopes on smoothed fallback
                        double[] slopesFallback = ut.differentaition(segFallbackFixed);
                        for (int j = 0; j < slopesFallback.length; j++) {
                            slopesFallback[j] = slopesFallback[i] / dt;
                        }
                        double thrFallback = 0.2 * ut.findMaxAbsolute(slopesFallback);
                        //  4) Onset
                        double[] preFallback = ut.fillDataIntoArray(0, peakLocFallback -1, slopesFallback);
                        int startLocFallback = ut.findFirstIndexWhenAbsoluteSlopeValuesExceedsThresholdValue(preFallback, thrFallback);
                        if (startLocFallback == -1)
                            startLocFallback = 0;
                        //  5) Offset
                        double[] postFallback = ut.fillDataIntoArray(peakLocFallback, slopesFallback.length-1, slopesFallback);
                        int endLocFallback = ut.findLastIndexWhenAbsoluteSlopeValuesExceedsThresholdValue(postFallback, thrFallback);
                        if (endLocFallback == -1)
                            endLocFallback = slopesFallback.length - 1;
                        else
                            endLocFallback = endLocFallback + peakLocFallback;
                        int tStartFallbackNew = tStart + startLocFallback;
                        int tStopFallbackNew = tStart + endLocFallback;
                        startAndEndIndexOfPoints.addTStart(tStartFallbackNew);
                        startAndEndIndexOfPoints.addTStop(tStopFallbackNew);
                        //  Validate and store T-wave peak
                        if (tIdxFallback > rIdx) {
                            features.T.addIndex(tIdxFallback);
                            features.T.addValue(ecg[tIdxFallback]);
                            double tRawAmplitude = ecg[tIdxFallback] - baseLine;
                            amplitude.T.addColumnOneValue(tRawAmplitude);
                            amplitude.T.addColumnTwoValue(tRawAmplitude * amplitudeToMv);
                            amplitude.T.addColumnThreeValue(tRawAmplitude * amplitudeToMv * mvToMm);
                        }
                    }
                }
            }catch (Exception e)  {
                System.out.println("Issue in T Wave Detection calculation");
                e.printStackTrace();
            }

            // -------- NEW: iso-electric per beat (PR preferred, TP fallback) --------
            int qrsOnsetIdx = -1;
            if(qStart != -1)
                qrsOnsetIdx = qStart;
            else if (qIdx != -1)
                qrsOnsetIdx = qIdx;
            else
                qrsOnsetIdx = Math.max(0, rIdx - (int) Math.round(0.012 * fs));
            int nextPStartIndex = -1;
//            System.out.println("i ==== " + i);
            HashMap<String, Object> isoElectricData = detectIsoElectricPerBeat( ecg, fs, pEndLocal, nextPStartIndex, qrsOnsetIdx, startAndEndIndexOfPoints.getTEnd(startAndEndIndexOfPoints.getTEndSize()-1), amplitudeToMv);
            // If iso missing, fall back to baseline for amplitude reference:
            double refVal = baseLine;
            Object isoVal = isoElectricData.get("isoValue");
            if (  isoVal != null && isoVal instanceof Double )
                refVal = (double) isoElectricData.get("isoValue");

            //  -------- OVERWRITE amplitudes for P/Q/R/S/T w.r.t. iso line --------
            //        % R
            if (rIdx != -1) {
                rRawAmplitude = ecg[rIdx] - refVal;
                amplitude.R.getColumnList(0).remove(amplitude.R.getColumnList(0).size() - 1);
                amplitude.R.getColumnList(1).remove(amplitude.R.getColumnList(1).size() - 1);
                amplitude.R.getColumnList(2).remove(amplitude.R.getColumnList(2).size() - 1);

                amplitude.R.addColumnOneValue(rRawAmplitude);
                amplitude.R.addColumnTwoValue(rRawAmplitude * amplitudeToMv);
                amplitude.R.addColumnThreeValue(rRawAmplitude * amplitudeToMv * mvToMm);
            }
            //        % Q
            if (qIdx != -1) {
                qRawAmplitude = ecg[qIdx] - refVal;
                amplitude.Q.getColumnList(0).remove(amplitude.Q.getColumnList(0).size() - 1);
                amplitude.Q.getColumnList(1).remove(amplitude.Q.getColumnList(1).size() - 1);
                amplitude.Q.getColumnList(2).remove(amplitude.Q.getColumnList(2).size() - 1);

                amplitude.Q.addColumnOneValue(qRawAmplitude);
                amplitude.Q.addColumnTwoValue(qRawAmplitude * amplitudeToMv);
                amplitude.Q.addColumnThreeValue(qRawAmplitude * amplitudeToMv * mvToMm);
            }
            //        % S
            if (sIdx != -1) {
                double sRawAmplitude = ecg[sIdx] - refVal;
                amplitude.S.getColumnList(0).remove(amplitude.S.getColumnList(0).size() - 1);
                amplitude.S.getColumnList(1).remove(amplitude.S.getColumnList(1).size() - 1);
                amplitude.S.getColumnList(2).remove(amplitude.S.getColumnList(2).size() - 1);

                amplitude.S.addColumnOneValue(sRawAmplitude);
                amplitude.S.addColumnTwoValue(sRawAmplitude * amplitudeToMv);
                amplitude.S.addColumnThreeValue(sRawAmplitude * amplitudeToMv * mvToMm);
            }
            //        % P
            if ( !features.P.getIndexList().isEmpty() ) {
                int pPeak = features.P.getIndex(features.P.getIndexList().size() - 1);
                double pRawAmplitude = ecg[pPeak] - refVal;
                amplitude.P.getColumnList(0).remove(amplitude.P.getColumnList(0).size() - 1);
                amplitude.P.getColumnList(1).remove(amplitude.P.getColumnList(1).size() - 1);
                amplitude.P.getColumnList(2).remove(amplitude.P.getColumnList(2).size() - 1);

                amplitude.P.addColumnOneValue(pRawAmplitude);
                amplitude.P.addColumnTwoValue(pRawAmplitude * amplitudeToMv);
                amplitude.P.addColumnThreeValue(pRawAmplitude * amplitudeToMv * mvToMm);
            }
            //        % T
            if (!features.T.getIndexList().isEmpty()) {
                int tPeak = features.T.getIndex(features.T.getIndexList().size() - 1);
                double tRawAmplitude = ecg[tPeak] - refVal;
                amplitude.T.getColumnList(0).remove(amplitude.T.getColumnList(0).size() - 1);
                amplitude.T.getColumnList(1).remove(amplitude.T.getColumnList(1).size() - 1);
                amplitude.T.getColumnList(2).remove(amplitude.T.getColumnList(2).size() - 1);

                amplitude.T.addColumnOneValue(tRawAmplitude);
                amplitude.T.addColumnTwoValue(tRawAmplitude * amplitudeToMv);
                amplitude.T.addColumnThreeValue(tRawAmplitude * amplitudeToMv * mvToMm);
            }
            int jIdx = -1;
            try {
                if(sIdx != -1) {
                    jIdx = detectJPoint(ecg, sIdx, fs, startAndEndIndexOfPoints);
                    if(jIdx != -1) {
                        features.J.addIndex(jIdx);
                        features.J.addValue(ecg[jIdx]);
                        double zz = ecg[jIdx];
                        double jRawAmplitude = ecg[jIdx] - baseLine;
                        amplitude.J.addColumnOneValue(jRawAmplitude);
                        amplitude.J.addColumnTwoValue(jRawAmplitude * amplitudeToMv);
                        amplitude.J.addColumnThreeValue(jRawAmplitude * amplitudeToMv * mvToMm);
//                        Calculate ST elevation
                        //  Concavity of ST segemnt elevation check
                        //---- ST at J, J+60 ms, J+80 ms (mV) ----
                        int off60 = (int) Math.round(0.060 * fs);
                        int off80 = (int) Math.round(0.080 * fs);
                        int idxJ60 = Math.min(ecg.length-1, jIdx + off60);
                        int idxJ80 = Math.min(ecg.length-1, jIdx + off80);
                        double stJMv = (ecg[jIdx] - refVal) * amplitudeToMv;
                        double stJ60Mv = (ecg[idxJ60] - refVal) * amplitudeToMv;
                        double stJ80Mv = (ecg[idxJ80] - refVal) * amplitudeToMv;
                        stElevation.add( (double) Math.round(stJMv * 10000) / 10000);
                        int tOnForSt = -1;
                        if (!startAndEndIndexOfPoints.tStart.isEmpty() ) {
                            int tStart = startAndEndIndexOfPoints.getTStart(startAndEndIndexOfPoints.getTStartSize() - 1);
                            if (tStart > jIdx + Math.round(0.02 * fs))
                                tOnForSt = tStart;
                            else
                                tOnForSt = Math.min(ecg.length - 1, jIdx + off80);
                        } else
                            tOnForSt = Math.min(ecg.length-1, jIdx + off80);
                        SagittaData sagitta = stSagittaSimple(ecg, fs, baseLine, jIdx, tOnForSt, amplitudeToMv);
                        stSagitaMv.add(sagitta.getSagittaMv());
                        stMorphologyCode.add(sagitta.getCode());
                        stTombstoneFlag.add(sagitta.getTombStoneFlag());
                    }
                }
            }catch (Exception e)  {
                System.out.println("Issue in J Point Detection");
                e.printStackTrace();
            }
            try {
               /* if ( (features.P.Size()[0] != 0) && (qIdx != -1) ) {
                    //    P wave duration: Q wave index - P start index
                    int end = features.P.Size()[0] - 1;
                    duration.P.add( (int) Math.max(0, ((qIdx - features.P.getIndex(end)) / fs) * 1000 ));
                    //    PR interval: R peak index - P start index
                    duration.PR.add( (int) Math.max(0, ((rIdx - features.P.getIndex(end)) / fs) * 1000) );
                }
                */
                if ( startAndEndIndexOfPoints.getPStartSize() != 0 && startAndEndIndexOfPoints.getPEndSize() != 0 ) {
                    int pStartLastEle = startAndEndIndexOfPoints.getPStartSize() - 1;
                    int pEndLastEle = startAndEndIndexOfPoints.getPEndSize() - 1;
                    int curentEnd = startAndEndIndexOfPoints.getPEnd(pEndLastEle);
                    int currentStart = startAndEndIndexOfPoints.getPStart(pStartLastEle);

//      Before 13 Aug 2025
//                    duration.P.add( Math.max(0, ( (curentEnd - currentStart) / fs) * 1000) );
//                    duration.PR.add( Math.max(0, (qIdx - currentStart) / fs * 1000 ) );

                    duration.P.add( Math.max(0, ( (pEndLocal - pStartLocal) / fs) * 1000) );
                    if (qRawAmplitude < 280 && rRawAmplitude < 0.06)
                        duration.PR.add( Math.max(0, (sIdx - pStartLocal) / fs * 1000 ) );
                    else
                        duration.PR.add( Math.max(0, (qIdx - pStartLocal) / fs * 1000 ) );
                }
                else{
                    duration.P.add(0.0);
                    duration.PR.add(0.0);
                }
                //  QRS duration: S wave index - Q wave index
                if ( (qIdx != -1) && (jIdx != -1) ) {
                    duration.QRS.add((int) Math.max(0, ((jIdx - qIdx) / fs) * 1000));
//                    System.out.println("jIdx--   "+jIdx+"      qIdx--   "+qIdx);
//                    duration.QRS.add((int) Math.max(0, (jIdx - qIdx) ));
                }
                else
                    duration.QRS.add(0);
                if ((qIdx != -1) && (features.T.Size()[0] != 0) ) {
                    //    QT interval: T wave index - Q wave index
//                    int end = features.T.Size()[0] - 1;
//                    int qtInterval = (int) Math.max(0, ((features.T.getIndex(end) - qIdx) / fs) * 1000);
//  Just for testing 18 Mar 2025, above two lines are commented for testing
//                    int lastTEleIdx = startAndEndIndexOfPoints.getTEndSize() - 1;
//                    int qtInterval = (int) Math.max(0, ((startAndEndIndexOfPoints.getTEnd(lastTEleIdx)
//                            - startAndEndIndexOfPoints.getQStart(lastQEleIdx)) / fs) * 1000);

//                    System.out.println("Rahul  ---- tend " +startAndEndIndexOfPoints.getTEnd(lastTEleIdx)
//                    +" qIdx ====   "+qIdx);
//                    int end = features.T.Size()[0] - 1;
//                    int temp = features.T.getIndex(end);
//                    int qtInterval = (int) Math.max(0, ((features.T.getIndex(end) - qIdx) / fs) * 1000);
//                    duration.QT.add(qtInterval);

                    int temp = startAndEndIndexOfPoints.getTEndSize() - 1 ;
                    int zz1 = startAndEndIndexOfPoints.getTEnd(temp);
                    int qtInterval = (int) Math.max(0, ((startAndEndIndexOfPoints.getTEnd(temp) - qIdx) / fs) * 1000);
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
//                    duration.R.add((int) Math.max(0, ((sIdx - qIdx) / fs ) * 1000));
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
//        AllCalculatedData fullData = new AllCalculatedData();
        AllCalculatedDataNew fullData = new AllCalculatedDataNew();
        fullData.features = features;
        fullData.amplitude = amplitude;
        fullData.duration = duration;
        fullData.stElevation = stElevation;
        fullData.heartRate = heartRate;
        fullData.rrIntervals = rrIntervals;
        fullData.startAndEndIndexOfPoints = startAndEndIndexOfPoints;
        fullData.stSagitaMv = stSagitaMv;
        fullData.stMorphologyCode = stMorphologyCode;
        fullData.stTombstoneFlag = stTombstoneFlag;

//        features.displayFeaturesData(features.P);
//        amplitude.displayAmplitudeData(amplitude.P);
//        duration.displayDurationDoubleData(duration.QTc);
        return fullData;
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

//     updated on 25 Oct 2025
    public int detectSPeak(double[] ecg, int rIdx, double fs, double baseline,
                           StartAndEndIndexOfPoints startAndEndIndexOfPoints) {
        //    Define the search window (20–60 ms after the R peak)
        int sIdx;
        int searchStart = rIdx + (int) Math.round(0.005 * fs);
        int searchEnd = rIdx + (int) Math.round(0.100 * fs);
        //    Ensure search range is within signal bounds
        searchStart = Math.max(searchStart, 0);
        searchEnd = Math.min(searchEnd, ecg.length-1);
        if (searchEnd <= searchStart) {
            System.out.println("In detectSPeak end less than start");
            return -1;
        }
        //  Search for the local minimum within the window
        double[] segment = ut.fillDataIntoArray(searchStart, searchEnd, ecg);
        //  Keep a small guard so we don’t hug the R shoulder
        int minOff = Math.max(0,(int) Math.round(0.006 * fs));          // ~6 ms from window start
        //  Slope (no amplitude use)
        double dt = 1/ fs;
        double[] dSeg = Arrays.stream(ut.differentaition(segment)).map(e -> e / dt).toArray();
        //  Zero-crossings (indices relative to 'segment'):
        int[] zcMin = IntStream.range(1, dSeg.length - 1 ).filter( e -> (
                (dSeg[e] < 0) && (dSeg[e+1] >= 0) && (dSeg[e+1] >= minOff) )
        ).toArray();   //   valleys
        int[] zcMax = IntStream.range(0, dSeg.length - 1).filter( e -> (
                (dSeg[e] > 0) && (dSeg[e+1] <= 0) && (dSeg[e] >= minOff) )
        ).toArray();   //  peaks
        //  Prefer first valley zero-crossing (trough-like S) ----
        if (zcMin.length > 0) {
            int k = zcMin[0];
            sIdx = searchStart + k;
            sIdx = Math.max(0, Math.min(sIdx, ecg.length-1));
            startAndEndIndexOfPoints.addSStart(searchStart);
            startAndEndIndexOfPoints.addSEnd(searchEnd);
            return  sIdx;
        }
        //  Else first peak zero-crossing (positive S is allowed) ----
        if (zcMax.length > 0) {
            int k = zcMax[0];
            sIdx = searchStart + k;
            sIdx = Math.max(0, Math.min(sIdx, ecg.length-1));
            startAndEndIndexOfPoints.addSStart(searchStart);
            startAndEndIndexOfPoints.addSEnd(searchEnd);
            return  sIdx;
        }
        //  Else take the most negative slope (steepest down) ----
        if (dSeg.length > 0) {
            int kMinSlope = ut.findMinIndex(dSeg);
            // keep off borders
            if (kMinSlope >= minOff) {
                kMinSlope = Math.max(1, Math.min(kMinSlope, ecg.length - 2));
                sIdx = searchStart + kMinSlope;
                sIdx = Math.max(0, Math.min(sIdx, ecg.length - 1));
                startAndEndIndexOfPoints.addSStart(searchStart);
                startAndEndIndexOfPoints.addSEnd(searchEnd);
                return sIdx;
            }
            //  Final fallback: minimum |slope| (closest to flat) ----
            int kFlat = ut.findMinIndexAbsolute(dSeg);
            kFlat = Math.max(1, Math.min(kFlat, ecg.length - 2));
            sIdx = searchStart + kFlat;
            sIdx = Math.max(0, Math.min(sIdx, ecg.length-1));
            startAndEndIndexOfPoints.addSStart(searchStart);
            startAndEndIndexOfPoints.addSEnd(searchEnd);
            return  sIdx;
        }
        // Degenerate window (very short)
        else {
            sIdx = searchStart;
            startAndEndIndexOfPoints.addSStart(searchStart);
            startAndEndIndexOfPoints.addSEnd(searchEnd);
            return sIdx;
        }




       /* sPeak code before 25 Oct 2025

        int minIdx = ut.findMinIndex(segment);
        double minValue = ut.findMin(segment);
        //   2) If there’s a negative trough, pick that
        if (minValue < baseline) {
            sIdx = minIdx + searchStart;
            startAndEndIndexOfPoints.addSStart(searchStart);
            startAndEndIndexOfPoints.addSEnd(searchEnd);
            return  sIdx;
        }
        int maxIdx = ut.findMaxIndex(segment);
        double maxValue = ut.findMax(segment);
        //   3) Else if there’s a positive peak, pick that
        if (maxValue > baseline) {
            sIdx = maxIdx + searchStart;
            startAndEndIndexOfPoints.addSStart(searchStart);
            startAndEndIndexOfPoints.addSEnd(searchEnd);
            return sIdx;
        }
        //  4) Otherwise, slope‐based fallback exactly as before
        double[] slopes = ut.differentaition(segment);
        for (int i = 0; i < slopes.length; i++) {
            slopes[i] = slopes[i] / dt;
        }
        int slopeMinIdx = ut.findMinIndex(slopes);
        sIdx = searchStart + slopeMinIdx;
        startAndEndIndexOfPoints.addSStart(searchStart);
        startAndEndIndexOfPoints.addSEnd(searchEnd);
        return sIdx;
        */
    }

    public int detectJPoint(double[] ecg, int sIdx, double fs, StartAndEndIndexOfPoints startAndEndIndexOfPoints) {
        //  Define the search range for J-point (10–60 ms after S-point)
//        search_range = round(0.04 * fs):round(0.06 * fs); % 10–20 ms
//        int searchStart = (int) Math.round(0.02 * fs);
//        int tempEnd = (int) Math.round(0.06 * fs);
//        com.arrthymia.LoaderHelper ldh = new com.arrthymia.LoaderHelper();
//        ldh.viewData(ecg);
        if (sIdx == -1 || sIdx >= ecg.length)
            return -1;
        boolean hasPositive = false, hasNegative = false;
        int jPoint = -1;
        double[] leftSlope, rightSlope;
        double amplitudeToMv = 1.0/(double) 6250;
        int searchStart = sIdx + (int) Math.round(0.004 * fs);
        int searchEnd = sIdx + (int) Math.round(0.08 * fs);
        Filters filters = new Filters();
        int idxInf = -1;
        int[] searchRange = new int[searchEnd - searchStart +1];
//        search_end   = min(max(search_end,1),   length(ecg));
        searchStart = Math.min(Math.max(searchStart, 0), ecg.length - 1);
        searchEnd = Math.min(Math.max(searchEnd, 0), ecg.length - 1);
        if (searchEnd <= searchStart)
            return -1;
        int[] jRange = new int[searchEnd - searchStart +1];
        double[] rawSlopes = new double[jRange.length-1];
        for (int i = 0; i < jRange.length; i++) {
            jRange[i] = searchStart + i;
        }
        double[] segRaw = new double[jRange.length];
        for (int i = 0; i < segRaw.length; i++) {
            segRaw[i] = ecg[jRange[i]];
        }
        //  Detect inflection point or minimum slope change
//        for (int i = 1; i < jRange.length; i++) {
//            rawSlopes[i-1] = Math.abs(ecg[jRange[i]] - ecg[jRange[i-1]]);
//            if (!hasPositive && rawSlopes[i] > 0)
//                hasPositive = true;
//            if (!hasNegative && rawSlopes[i] < 0)
//                hasNegative = true;
//        }
        rawSlopes = ut.differentaition(segRaw);
        hasPositive = ut.checkWhetherAnyArrayValueGreaterThanZero(rawSlopes);
        hasNegative = ut.checkWhetherAnyArrayValueLessThanZero(rawSlopes);
        if ( hasPositive && hasNegative){
            int winMs = 15;
            int winPts = (int) Math.max(2, Math.round(((double ) winMs/1000)* fs));
            double[] bMov = new double[winPts];
            Arrays.fill(bMov, (double) 1/winPts);
            double[] segSm = filters.customFiltFiltNew(bMov, new double[]{1}, segRaw);
            double[] slopesSm = ut.differentaition(segSm);
            ArrayList<Integer> posIdx = ut.findIndexWhereValueIsGreaterThanZero(slopesSm);
            if (posIdx.size() >= 12)
                idxInf = posIdx.get((posIdx.size() - 1) - 11 );
            else if (!posIdx.isEmpty())
                idxInf = posIdx.get(posIdx.size()-1);
            else
                idxInf = ut.findMinIndexAbsolute(slopesSm);
        } else
            idxInf = ut.findMinIndexAbsolute(rawSlopes);
        //  4) Map to global index
        jPoint = jRange[idxInf];
        // 5) Pattern-1/4/5 check → **narrow-window** fallback
        //  pat1: all-positive on both sides
        //  pat4: right has +,–,+
        // pat5: left has +,–,+
        leftSlope = ut.fillDataIntoArray(0, idxInf-1, rawSlopes);
        rightSlope = ut.fillDataIntoArray(idxInf, rawSlopes.length-1, rawSlopes);
        boolean pat1 = ut.checkWhetherAllArrayValueGreaterThanZero(leftSlope) &&
                ut.checkWhetherAllArrayValueGreaterThanZero(rightSlope);
        boolean pat4 = ut.checkWhetherAnyArrayValueGreaterThanZero(rightSlope) &&
                ut.checkWhetherAnyArrayValueLessThanZero(rightSlope);
        boolean pat5 = ut.checkWhetherAnyArrayValueGreaterThanZero(leftSlope) &&
                ut.checkWhetherAnyArrayValueLessThanZero(leftSlope);
        if (pat1 || pat4 || pat5) {
            //      NARROW WINDOW: 10–50 ms after S, pick min-abs slope there ---
            int searchStart2 = sIdx + (int) Math.round(0.01 * fs);
            int searchEnd2 = sIdx + (int) Math.round(0.05 * fs);
            searchStart2 = Math.max(0, Math.min(searchStart2, ecg.length-1));
            searchEnd2 = Math.max(0, Math.min( searchEnd2, ecg.length-1));
            if (searchEnd2 > searchStart2) {
                int[] jr2 = new int[searchEnd2 - searchStart2 + 1];
                double[] jr2Ecg = new double[searchEnd2 - searchStart2 + 1];
                for (int i = 0; i < jr2.length; i++) {
                    jr2[i] = searchStart2 + i;
                    jr2Ecg[i] = ecg[jr2[i]];
                }
                double[] slopes2 = ut.differentaition(jr2Ecg);
                int k = ut.findMinIndexAbsolute(slopes2);
                jPoint = jr2[k] - 5;
            }
        }
        //  6) Depressed override: if both S & J < 0 mV and nearly equal → J = S
        double sAmplitude = ecg[sIdx] * amplitudeToMv;
        double jAmplitude = ecg[jPoint] * amplitudeToMv;
        double tol = 0.05;
        if (sAmplitude < 0 && jAmplitude < 0 && Math.abs(jAmplitude - sAmplitude) <= tol)
            jPoint = sIdx;
        //  7) Clamp final index
        jPoint = Math.max(0, Math.min(jPoint, ecg.length-1));
        return jPoint;
    }

    private SagittaData stSagittaSimple(double[] ecg, double fs, double baseLine, int jIdx, int tstartIdx, double amplitudeToMv) {
        int n = ecg.length-1;
        SagittaData sagittaData = new SagittaData();
        if ( jIdx < 2 || jIdx >= n) {
            // provide a return statement
        }
        if (tstartIdx <= jIdx + 2)
            tstartIdx = (int) Math.min(n , jIdx + Math.round(0.08 * fs));
        int x1 = jIdx; int x2 = tstartIdx;
        if (x2 <= x1 + 2) {
            // provide a return statement
        }
        double xmDouble = Math.round(((x1 + x2) / 2.0));
        int xm = (int) xmDouble;
        double y1 = (ecg[x1] - baseLine) * amplitudeToMv;
        double y2 = (ecg[x2] - baseLine) * amplitudeToMv;
        double ym = (ecg[xm] - baseLine) * amplitudeToMv;
        double yChordMid = y1 + ( (y2-y1) * ( (double) (xm - x1) / (x2 - x1) ) );
        double sagittaMv = (double) Math.round((ym - yChordMid) * 10000 ) / 10000;
        double thrFlat = 0.05;      // mV: small bow → flat
        double thrTombstone = 0.15;     //  mV: strong concave-down
        int code = 0;       //  flat/linear
        if (sagittaMv > thrFlat)
            code = 1;
        else if (sagittaMv < -thrFlat)
            code = -1;
        boolean tombStoneFlag = (code == -1) && Math.abs(sagittaMv) >= thrTombstone;
        sagittaData.setSagittaMv(sagittaMv);
        sagittaData.setCode(code);
        sagittaData.setTombStoneFlag(tombStoneFlag);
        return sagittaData;
    }


    private HashMap<String, Object> detectIsoElectricPerBeat(double[] ecg, double fs, int pEnd, int nextPStartIndex, int qrsOnsetIdx, int tEnd, double amplitudeToMv) {
        // ---- PR segment: P_Stop+1 ... QRS_onset-1 ----
        HashMap<String, Object> isoElectricData = new HashMap<>();
        int ecgLength = ecg.length, isoIdx = -1;
        if (pEnd > -1 && qrsOnsetIdx > pEnd ) {
            int s = Math.max(0, Math.min(pEnd + 1 , ecgLength - 1));
            int e = Math.max(0, Math.min(qrsOnsetIdx - 1 , ecgLength - 1));
            if (e > s) {
                double[] seg = ut.fillDataIntoArray(s, e , ecg);
                double[] d = ut.differentaitionAbsolute(seg);
                if (d.length > 0) {
                    int k =  ut.findMinIndex(d);
                    isoIdx = s + k ;
                } else {
                    isoIdx = (int) Math.round( (double) (s + e) / 2);
                }
                isoElectricData.put("isoIdx", isoIdx);
                isoElectricData.put("isoValue", ecg[isoIdx]);
                isoElectricData.put("isoSource", "PR");
                return isoElectricData;
            }
        }

        //  ---- TP segment: T_Stop → next P (if known) else short post-T window ----
        if (tEnd > 0) {
            int s2 = -1, e2 = -1;
            if (nextPStartIndex != -1 && nextPStartIndex > tEnd) {
                s2 = Math.max(0, Math.min(tEnd + 1, ecgLength -1));
                e2 = Math.max(0, Math.min(nextPStartIndex - 1, ecgLength -1));
            }
            else {
                s2 = Math.max(0, Math.min(tEnd + (int) Math.round(0.020 * fs), ecgLength - 1));
                e2 = Math.max(0, Math.min(tEnd + (int) Math.round(0.080 * fs), ecgLength - 1));
            }
            if (e2 > s2) {
                double[] seg2 = ut.fillDataIntoArray(s2, e2 , ecg);
                double[] d2 = ut.differentaitionAbsolute(seg2);
                if (d2.length > 0){
                    int k2 = ut.findMinIndex(d2);
                    isoIdx = s2 + k2;
                } else
                    isoIdx = (int) Math.round( (double) (s2 + e2) / 2);
            }
            isoElectricData.put("isoIdx", isoIdx);
            isoElectricData.put("isoValue", ecg[isoIdx]);
            isoElectricData.put("isoSource", "TP");
            return isoElectricData;
        }
        //  ---- Fallback: quiet short patch after QRS onset ----
        if (qrsOnsetIdx != -1 && qrsOnsetIdx < ecgLength) {
            int s3 = -1, e3 = -1;
            s3 = Math.max(0, Math.min(qrsOnsetIdx + (int) Math.round(0.120 * fs), ecgLength - 1));
            e3 = Math.max(0, Math.min(qrsOnsetIdx + (int) Math.round(0.180 * fs), ecgLength - 1));

            if (e3 > s3) {
                double[] seg3 = ut.fillDataIntoArray(s3, e3 , ecg);
                double[] d3 = ut.differentaitionAbsolute(seg3);
                if (d3.length > 0){
                    int k3 = ut.findMinIndex(d3);
                    isoIdx = s3 + k3;
                } else
                    isoIdx = (int) Math.round( (double) (s3 + e3) / 2);
            }
            isoElectricData.put("isoIdx", isoIdx);
            isoElectricData.put("isoValue", ecg[isoIdx]);
            isoElectricData.put("isoSource", "Fallback");
            return isoElectricData;
        } else {
            //  Last resort
            isoIdx = Math.max(0, (int) Math.round((double) ecgLength / 2));
            isoElectricData.put("isoIdx", isoIdx);
            isoElectricData.put("isoValue", ecg[isoIdx]);
            isoElectricData.put("isoSource", "Fallback");
            return isoElectricData;
        }
    }



}
