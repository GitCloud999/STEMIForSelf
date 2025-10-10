package com.stemi;

import com.stemi.libs.Utility;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class RPeakDetection {

    public ArrayList<Integer> improvedRPeakDetection(double[] ecg, double fs) {
        Filters filter = new Filters();
        Utility ut = new Utility();
        double f5Hz = 5;
        double f50Hz = 50;
        double[] wN = new double[2];
        wN[0] = f5Hz  / (fs/2);
        wN[1] = f50Hz  / (fs/2);
        boolean flag = true;
//        System.out.println(wN[0] +" , "+wN[1]);
        System.out.println("===================================================");
        ArrayList<double[]> list = filter.pointDetectionButterFilter(4, wN, "bandpass");
//        ArrayList<double[]> listTemp = filter.pointDetectionButterFilterArrhythmia(4, wN, "bandpass");
//        ArrayList<double[]> list = filter.butterFilterNew(4, wN, "stop");
        double[] b = list.get(0);
        double[] a = list.get(1);
        for (int i = 0; i < ecg.length; i++) {
            ecg[i] = ut.reduceValueAfterPoints(ecg[i], 4);
        }
        double[] ecgFiltered = filter.customFiltFiltNew(b, a, ecg);
        double[] ecgFilteredDiff = new double[ecg.length-1];
        for (int i = 0; i < ecgFiltered.length-1; i++) {
            ecgFilteredDiff[i] = (double) Math.round((ecgFiltered[i+1] - ecgFiltered[i]) * 1000) / 1000;
        }
        // Sliding Window Integration
        double windowSize3 = Math.round(0.120 * fs);
        double[] ecgEnergy = new double[ecgFilteredDiff.length];
        for (int i = 0; i < ecgFilteredDiff.length; i++) {
            ecgEnergy[i] = Math.pow(ecgFilteredDiff[i], 2);
        }
        double[] ecgEnergySmoothed = new double[ecgEnergy.length];
        ecgEnergySmoothed = filter.movingAverage(ecgEnergy, windowSize3);

//        ut.viewData(ecgEnergySmoothed);
        //    Adaptive Thresholding
        double baseLineThreshold = ut.findMax(ecgEnergySmoothed) * 0.2;
//        System.out.println(baseLineThreshold);
        double[] dynamicThreshold = filter.movingAverage(ecgEnergySmoothed, Math.round(0.8 * fs));
//        double[] dynamicThreshold9April2025 = filter.movingAverage9April2025(ecgEnergySmoothed, Math.round(0.8 * fs));
//        ut.viewData(dynamicThreshold);
        double[] threshold = new double[dynamicThreshold.length];
        for (int i = 0; i < threshold.length; i++) {
            threshold[i] = Math.max(dynamicThreshold[i], baseLineThreshold);
//            System.out.println(i+"   ----    "+threshold[i]);
        }
        // Identify candidate peaks
        ArrayList<Integer> candidateList = new ArrayList<Integer>();
//        int xi = 0;
        for (int i = 0; i < ecgEnergySmoothed.length; i++) {
            if (ecgEnergySmoothed[i] > threshold[i]) {
                candidateList.add(i);
//                System.out.println(xi+"   ----    "+i);
//                xi++;
            }
        }
        //   Local Maximum Refinement with Additional Features
        ArrayList<Integer> rPeaksList = new ArrayList<Integer>();
//        ArrayList<Integer> rPeaksStartIndexList = new ArrayList<Integer>();
//        ArrayList<Integer> rPeaksEndIndexList = new ArrayList<Integer>();
        double searchWindow = Math.round(0.08 * fs);
        int aIndexTemp = 1;
        for (int i = 0; i < candidateList.size(); i++) {
//        for (int i = 0; i < 2; i++) {
            int startIdx = (int) Math.max((candidateList.get(i) - searchWindow), 0);
            int endIdx = (int) Math.min((candidateList.get(i) + searchWindow), (ecgFiltered.length-1));
//            System.out.println(i+" ----------  "+startIdx+"   ,      "+endIdx);
            //    Find local maxima in the filtered signal
//            System.out.println( (ecgFiltered[startIdx]+"        "+ecgFiltered[endIdx]));
//            System.out.println( (startIdx+"        "+endIdx));
//            rPeaksStartIndexList.add(startIdx);
//            rPeaksEndIndexList.add(endIdx);
//            double localMaxIdx = ut.findMax(ecgFiltered, startIdx, endIdx);
//            double localMaxIdx = ut.findMax(ecgFiltered, startIdx, endIdx);
            int localMaxIdx = ut.findMaxComparative(ecgFiltered, startIdx, endIdx);
//            int rPeakIndex  = (int) (startIdx + localMaxIdx) - 1;
            // Changed on 13 feb 2025  bcoz findMax for condn i <= endIdx
            int rPeakIndex  = (int) (startIdx + localMaxIdx);
//            System.out.println(i+" ---------     "+rPeakIndex);
            // Changed on 12 Feb 2025   Commented above one and added below line
            double rPeakIndexDouble = 0, rPeakListLastDouble = 0;
            if (i > 0) {
                rPeakIndexDouble = (double) rPeakIndex;
                rPeakListLastDouble = (double) rPeaksList.get(rPeaksList.size() - 1);
            }
            //  Check slope to differentiate R and T peaks
            if ( (i>=1) && (((rPeakIndexDouble - rPeakListLastDouble) / fs ) < 0.2) ) {
                int sIdx;
                if (!rPeaksList.isEmpty())
                    sIdx = rPeaksList.get(rPeaksList.size()-1);
                else
                    sIdx = 0;
                double[] prevTempArr = ut.fillDataIntoArray(sIdx, rPeakIndex, ecgFiltered);
                double prevSlope = 0, curSlope = 0;

                if (!rPeaksList.isEmpty() && rPeakListLastDouble != rPeakIndexDouble) {
                    double[] prevTempArrDiff = ut.differentaition(prevTempArr);
                    prevSlope = ut.findMax(prevTempArrDiff);
                } else
                    prevSlope = 0.0;
                double[] curTempArr = ut.fillDataIntoArray(rPeakIndex, (int) Math.min( ( rPeakIndex + Math.round(0.1 * fs) ), (ecgFiltered.length - 1)), ecgFiltered);
                double[] curTempArrDiff = ut.differentaition(curTempArr);
                curSlope = ut.findMax(curTempArrDiff);
                if (curSlope < (prevSlope * 0.5) || ( (curSlope == prevSlope) && ( curSlope == 0.0) ) )    // Likely a T peak
                    continue;
            }
            rPeaksList.add(rPeakIndex);
        }
        ArrayList<Integer> refineRPeaksList = refineRPeaks(ecgFiltered, rPeaksList, fs, ut);
//        for (int i = 0; i < refineRPeaksList.size(); i++) {
//            System.out.println(i +" i ===== "+ refineRPeaksList.get(i));
//        }
        //  Enforce Physiological RR Interval
        double minRR = Math.round(0.25 * fs);       //Minimum RR interval (250 ms for SVT)
        return enforceRefractoryPeriod(refineRPeaksList, ecgFiltered, minRR);
    }

    public ArrayList<Integer> improvedRPeakDetectionVpc5August2025(double[] ecg, double fs) {
        Filters filter = new Filters();
        Utility ut = new Utility();
        double f5Hz = 5;
        double f50Hz = 50;
        double[] wN = new double[2];
        wN[0] = f5Hz  / (fs/2);
        wN[1] = f50Hz  / (fs/2);
//        System.out.println(wN[0] +" , "+wN[1]);
        System.out.println("===================================================");
        ArrayList<double[]> list = filter.pointDetectionButterFilter(4, wN, "bandpass");
        double[] b = list.get(0);
        double[] a = list.get(1);
        for (int i = 0; i < ecg.length; i++) {
            ecg[i] = ut.reduceValueAfterPoints(ecg[i], 4);
        }
        double[] ecgFiltered = filter.customFiltFiltNew(b, a, ecg);
        double[] ecgFilteredDiff = new double[ecg.length-1];
        for (int i = 0; i < ecgFiltered.length-1; i++) {
            ecgFilteredDiff[i] = (double) Math.round((ecgFiltered[i+1] - ecgFiltered[i]) * 1000) / 1000;
        }
        // Sliding Window Integration
        double[] ecgEnergy = new double[ecgFilteredDiff.length];
        for (int i = 0; i < ecgFilteredDiff.length; i++) {
            ecgEnergy[i] = (double) Math.round(Math.pow(ecgFilteredDiff[i], 2) * 100) / 100;
        }
        double windowSize3 = Math.round(0.120 * fs);
        double[] ecgEnergySmoothed = filter.movingAverage(ecgEnergy, windowSize3);
        //    Adaptive Thresholding
        double baseLineThreshold = ut.findMax(ecgEnergySmoothed) * 0.1;
        double[] dynamicThreshold = filter.movingAverage(ecgEnergySmoothed, Math.round(0.8 * fs));
        double[] threshold = new double[dynamicThreshold.length];
        for (int i = 0; i < threshold.length; i++) {
            threshold[i] = Math.max(dynamicThreshold[i], baseLineThreshold);
//            System.out.println(i+"   ----    "+threshold[i]);
        }
        // Identify candidate peaks
        ArrayList<Integer> candidateList = new ArrayList<Integer>();
        for (int i = 0; i < ecgEnergySmoothed.length; i++) {
            if (ecgEnergySmoothed[i] > threshold[i]) {
                candidateList.add(i);
            }
        }
        //   Local Maximum Refinement with Additional Features
        ArrayList<Integer> rPeaksList = new ArrayList<Integer>();
        double searchWindow = Math.round(0.08 * fs);
        //  --- New: global amplitude & slope thresholds ---
        double globalMax = ut.findMaxAbsolute(ecgFiltered);
        double ampMin = (double) Math.round(0.05 * globalMax * 100 ) /100;           //  % require ≥5% of max QRS amp
        double[] allSlopes = ut.differentaitionAbsolute(ecgFiltered);
        double slopeGlobalMax = ut.findMax(allSlopes);
        double slopeMin = (double) Math.round(0.2 * slopeGlobalMax * 1000 ) / 1000;
        for (int i = 0; i < candidateList.size(); i++) {
//        for (int i = 0; i < 2; i++) {
            int startIdx = (int) Math.max((candidateList.get(i) - searchWindow), 0);
            int endIdx = (int) Math.min((candidateList.get(i) + searchWindow), (ecgFiltered.length-1));
//            int localMaxIdx = ut.findMaxComparative(ecgFiltered, startIdx, endIdx);
            int localMaxIdx = ut.findMaxIndexAbsolute(ut.fillDataIntoArray(startIdx, endIdx, ecgFiltered));
            int rPeakIndex  = (startIdx + localMaxIdx);
            // 1) amplitude guard
            if (Math.abs(ecgFiltered[rPeakIndex]) < ampMin)
                continue;
            // 2) local‐slope guard
            double[] segment = ut.fillDataIntoArray( (int) Math.max(rPeakIndex - searchWindow , 0), (int) Math.min(rPeakIndex +
                    searchWindow, ecgFiltered.length - 1), ecgFiltered);
            double[] localSlopes = ut.differentaitionAbsolute(segment);
            if (ut.findMax(localSlopes) < slopeMin)
                continue;
            //  3) SVT / T-wave guard (unchanged)
            double rPeakIndexDouble = 0, rPeakListLastDouble = 0;
            if ( (!rPeaksList.isEmpty()) ) {
                rPeakIndexDouble = (double) rPeakIndex;
                rPeakListLastDouble = (double) rPeaksList.get(rPeaksList.size() - 1);
            }
            //  Check slope to differentiate R and T peaks
            if ( (!rPeaksList.isEmpty()) && (((rPeakIndexDouble - rPeakListLastDouble) / fs ) < 0.2) ) {
                int sIdx = rPeaksList.get(rPeaksList.size() - 1);
                double[] prevTempArr = ut.fillDataIntoArray(sIdx, rPeakIndex, ecgFiltered);
                double prevSlope = 0, nextSlope = 0;
                double[] prevTempArrDiff = ut.differentaition(prevTempArr);
                prevSlope = ut.findMax(prevTempArrDiff);
                double[] nextTempArr = ut.fillDataIntoArray(rPeakIndex, (int) Math.min( ( rPeakIndex + Math.round(0.1 * fs) ), (ecgFiltered.length - 1)), ecgFiltered);
                double[] nextTempArrDiff = ut.differentaition(nextTempArr);
                nextSlope = ut.findMax(nextTempArrDiff);
                if (nextSlope < (prevSlope * 0.5) || ( (nextSlope == prevSlope) && ( nextSlope == 0.0) ) )    // Likely a T peak
                    continue;
            }
            rPeaksList.add(rPeakIndex);
        }
        ArrayList<Integer> refineRPeaksList = refineRPeaks(ecgFiltered, rPeaksList, fs, ut);
        //  Enforce Physiological RR Interval
        double minRR = Math.round(0.20 * fs);       //Minimum RR interval (250 ms for SVT)
        rPeaksList = enforceRefractoryPeriod(refineRPeaksList, ecgFiltered, minRR);
        //  fallback #1: very low-amp or wide QRS
        if (rPeaksList.isEmpty())
            rPeaksList = findPeaks(ecgFiltered, 0.05 * ut.findMax(ecgFiltered), (int) Math.round(0.20 * fs));
        //  fallback #2: single largest deflection
        if (rPeaksList.isEmpty()) {
            int locIndex = ut.findMaxIndexAbsolute(ecgFiltered);
            rPeaksList.add(locIndex);
            rPeaksList = enforceRefractoryPeriod(rPeaksList, ecgFiltered, Math.round(0.20 * fs));
        }
        return rPeaksList;
    }

    public ArrayList<Integer> enforceRefractoryPeriod(ArrayList<Integer> refineRPeaksList, double[] ecgFiltered, double minRR) {
        ArrayList<Integer> filteredPeaks = new ArrayList<Integer>();

        for (Integer integer : refineRPeaksList) {
//            if isempty(filtered_peaks) || (r_peaks(i) - filtered_peaks(end)) > min_rr
            if (filteredPeaks.isEmpty() || (integer - filteredPeaks.get(filteredPeaks.size() - 1)) > minRR) {
                filteredPeaks.add(integer);
//                System.out.println("if --------------   "+refineRPeaksList.get(i));
            } else if ((Math.abs(ecgFiltered[integer])) > (Math.abs(ecgFiltered[filteredPeaks.get(filteredPeaks.size() - 1)]))) {
                filteredPeaks.set((filteredPeaks.size() - 1), integer);
//                System.out.println("else --------------   "+refineRPeaksList.get(i));
            }
        }

//        for (int i = 0; i < filteredPeaks.size(); i++) {
//            System.out.println(i+" -----------    "+filteredPeaks.get(i));
//        }
        return filteredPeaks;
    }

    public ArrayList<Integer> refineRPeaks(double[] ecgFiltered, ArrayList<Integer> rPeakList, double fs, Utility ut) {
        double searchWindow = Math.round(0.05 * fs);
        ArrayList<Integer> rPeaksRefined = new ArrayList<Integer>();
        for (int i = 0; i < rPeakList.size()-1; i++) {
            int startIdx = (int) Math.max(rPeakList.get(i) - searchWindow, 0);
            int endIdx = (int) Math.min(rPeakList.get(i) + searchWindow, ecgFiltered.length-1);
            //    Find local maxima in the filtered signal
//            double localMaxIdx = ut.findMax(ecgFiltered, startIdx, endIdx);
            int localMaxIdx = ut.findMaxComparative(ecgFiltered, startIdx, endIdx);
//            System.out.println("Refine ---------   "+localMaxIdx);
//            int refinedPeak  = (int) (startIdx + localMaxIdx) - 1;
            int refinedPeak  = (startIdx + localMaxIdx) ;
            rPeaksRefined.add(refinedPeak);
        }
        Set<Integer> setList = new LinkedHashSet<>(rPeaksRefined);   //Remove duplicate peaks
        rPeaksRefined.clear();
        rPeaksRefined.addAll(setList);
//        for (int i = 0; i < rPeaksRefined.size(); i++) {
//            System.out.println(rPeaksRefined.get(i));
//        }
//        System.out.println("*****************************************");
        return rPeaksRefined;
    }

    public ArrayList<Integer> findPeaks(double[] signal, double minPeakHeight, int minPeakDistance) {
        ArrayList<Integer> peakIndices = new ArrayList<>();
        int lastPeakIndex = -minPeakDistance;

        for (int i = 1; i < signal.length - 1; i++) {
            if (signal[i] > minPeakHeight && signal[i] > signal[i - 1] && signal[i] > signal[i + 1] &&
                    (i - lastPeakIndex) >= minPeakDistance) {
                peakIndices.add(i);
                lastPeakIndex = i;
            }
        }

        return peakIndices;
    }


    public void displayTwoArrays(double[] ecgEnergySmoothed, double[] threshold){
        for (int i = 0; i < threshold.length; i++) {
            System.out.println(i +"  =======  ecg = "+ecgEnergySmoothed[i] +"      threshold = "+threshold[i] +"    "
            +(ecgEnergySmoothed[i] > threshold[i]));
        }

    }
}
