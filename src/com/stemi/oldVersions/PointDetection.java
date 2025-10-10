package com.stemi.oldVersions;

import com.stemi.OnResultCompleteListener;
import com.stemi.RPeakDetection;
import com.stemi.dataClasses.*;
import com.stemi.libs.Utility;

import java.util.ArrayList;
import java.util.HashMap;

public class PointDetection {
    private double[][] notchFilteredData;
    PointDetection(double[][] data) {
        this.notchFilteredData = data;
    }

    void findPointsForAllColumns(double Fs, OnResultCompleteListener onResultCompleteListener) {
//        String fileName = clientName.split(".txt")[0]+"_amplitude_duration.txt";
//        System.out.println(fileName);
        String header = "Lead-wise Amplitudes and Durations";
//      Split leads into two groups for plotting
        int numOfLeads = notchFilteredData.length;
        int[] group1 = {0,1,2,3,4,5,6,7,8}; // First 5 leads
//        int[] group2 = {5,6,7,8};    // Remaining leads
//       Process and plot each group
        processAndPlotGroup(Fs, group1, onResultCompleteListener);
//        System.out.println(" -------------------------- Group 2 Start ------------");
//        processAndPlotGroup(Fs, group2, clientName, onResultCompleteListener);


    }

    public void processAndPlotGroup(double fs, int[] group, OnResultCompleteListener onResultCompleteListener) {
        double[] ecg = new double[notchFilteredData[0].length];
        RPeakDetection rPeakDetection = new RPeakDetection();
        Utility ut = new Utility();
        HashMap<String, Double> map = new HashMap<String, Double>();
        for (int leadIdx = group[0]; leadIdx <= group[group.length - 1]; leadIdx++) {
//        for (int leadIdx = 0; leadIdx < 1; leadIdx++) {
            ecg = notchFilteredData[group[leadIdx - group[0]]];
            ArrayList<Integer> rPeaksList = rPeakDetection.improvedRPeakDetection(ecg, fs);
            int[] rPeaks = new int[rPeaksList.size()];
            for (int j = 0; j < rPeaks.length; j++) {
                rPeaks[j] = rPeaksList.get(j);
//                System.out.println(j+"   ------- "+rPeaks[j]);
            }
//            System.out.println("---------- leadIdx ========   "+leadIdx);
            double baseLine = ut.mean(ecg);
            PQSTDetection pqst = new PQSTDetection();
//            com.arrthymia.PQSTDetectionNew pqstNew = new com.arrthymia.PQSTDetectionNew();
            if (rPeaks.length >= 2 ) {
                AllCalculatedData allData = pqst.detectPQSTFeatures(ecg, rPeaks, baseLine, fs);
//                AllCalculatedData allData = pqstNew.detectPQSTFeatures(ecg, rPeaks, baseLine, fs);
                Features features = allData.features;
                Amplitude amplitude = allData.amplitude;
                Duration duration = allData.duration;
                double[] rrIntervals = allData.rrIntervals;
                double[] heartRate = allData.heartRate;
                ArrayList<Double> stElevation = allData.stElevation;
//            allData.displayAllFeaturesData(features);
//            allData.displayAllAmplitudeData(amplitude);
//            allData.displayAllDurationData(duration);
//            allData.displayAllStElevation(allData.stElevation);
//            Loader ld = new Loader();
//            ld.viewData(allData.rrIntervals);

//            Calculate QTc using Bazett's formula
                double[][] qtcIntervals = new double[duration.QT.size()][rrIntervals.length];
                if ((!duration.QT.isEmpty()) && (rrIntervals.length != 0)) {
                    for (int j = 0; j < qtcIntervals.length; j++) {
                        for (int k = 0; k < qtcIntervals[0].length; k++) {
                            qtcIntervals[j][k] = duration.QT.get(j) / Math.sqrt(rrIntervals[k] / 1000);
//                        System.out.print(qtcIntervals[j][k]+",  ");
                        }
//                    System.out.println();
                    }
                }
            CalculateLeadWiseSummary summary = new CalculateLeadWiseSummary();
            ArrayList<ArrayList<Double>> summaryList = summary.leadWiseCalculation(rPeaks, rrIntervals, leadIdx, allData);
            if(leadIdx == 1)
                map = getLead2MetaData(summaryList);
//            onResultCompleteListener.onComplete(rPeaks, rrIntervals, leadIdx, allData, summaryList);
            }   // rPeaks length end condition
            else {
                System.out.println("Into else part for one rPeak..............  ");
                onResultCompleteListener.rPeaksLessThanTwo(rPeaks, leadIdx);
//                writeIntoFileForOneRPeak(rPeaks, leadIdx, clientName);
            }

        }
        TwelveLeadEcgData twelveLeadEcgData;
        try {
            twelveLeadEcgData= createTwelveLeadData(onResultCompleteListener);
//            onResultCompleteListener.onCompletedLead2MetaData(twelveLeadEcgData, map);
        } catch (Exception e) {
            onResultCompleteListener.onFailed("Error in creating "+e.getMessage());
        }

    }

    public HashMap<String, Double> getLead2MetaData(ArrayList<ArrayList<Double>> summaryList) {
        HashMap<String, Double> hashMap = new HashMap<String, Double>();
        hashMap.put("heartRate", (double) Math.round(summaryList.get(0).get(0)) );
        hashMap.put("heartRateBpm", (double) Math.round(summaryList.get(0).get(1)) );
        hashMap.put("prInterval", (double) Math.round(summaryList.get(7).get(10)) );
        hashMap.put("prIntervalBpm", (double) Math.round(summaryList.get(7).get(11)));
        hashMap.put("qrsInterval", (double) Math.round(summaryList.get(7).get(12)));
        hashMap.put("qrsIntervalBpm", (double) Math.round(summaryList.get(7).get(13)));
        hashMap.put("qtInterval", (double) Math.round(summaryList.get(7).get(14)));
        hashMap.put("qtIntervalBpm", (double) Math.round(summaryList.get(7).get(15)));
        hashMap.put("qtcInterval", (double) Math.round(summaryList.get(7).get(16)));
        hashMap.put("qtcIntervalBpm", (double) Math.round(summaryList.get(7).get(17)));
        return hashMap;
    }

    public TwelveLeadEcgData createTwelveLeadData(OnResultCompleteListener onResultCompleteListener) {
        TwelveLeadEcgData twelveLeadEcgData = new TwelveLeadEcgData();
        for (int i = 0; i < 9; i++) {
            ArrayList<Double> list = new ArrayList<Double>();
            for (int j = 0; j < notchFilteredData[i].length; j++) {
                list.add(Double.valueOf(notchFilteredData[i][j]));
            }
            if (list.isEmpty())
                onResultCompleteListener.onFailed("Empty list");
            if (i == 0) {
                twelveLeadEcgData.setLead1(list);
            }
            if (i == 1) {
                twelveLeadEcgData.setLead2(list);
            }
            if (i == 2) {
                twelveLeadEcgData.setLead3(list);
            }
            if (i == 3) {
                twelveLeadEcgData.setV1(list);
            }
            if (i == 4) {
                twelveLeadEcgData.setV2(list);
            }
            if (i == 5) {
                twelveLeadEcgData.setV3(list);
            }
            if (i == 6) {
                twelveLeadEcgData.setV4(list);
            }
            if (i == 7) {
                twelveLeadEcgData.setV5(list);
            }
            if (i == 8) {
                twelveLeadEcgData.setV6(list);
            }
        }
        return twelveLeadEcgData;
    }

}
