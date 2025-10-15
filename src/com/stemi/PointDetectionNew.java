package com.stemi;

import com.stemi.dataClasses.*;
import com.stemi.libs.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PointDetectionNew {
    private double[][] finalData;
    PointDetectionNew(double[][] data) {
        this.finalData = data;
    }

    void findPointsForAllColumns(double Fs, OnResultCompleteListener onResultCompleteListener) {
//        String fileName = clientName.split(".txt")[0]+"_amplitude_duration.txt";
//        System.out.println(fileName);
//        String header = "Lead-wise Amplitudes and Durations";
//      Split leads into two groups for plotting
        int numOfLeads = finalData.length;
        int[] group1 = {0,1,2,3,4,5,6,7,8}; // First 5 leads
//        int[] group2 = {5,6,7,8};    // Remaining leads
//       Process and plot each group
        processAndPlotGroup(Fs, group1, onResultCompleteListener);
//        System.out.println(" -------------------------- Group 2 Start ------------");
//        processAndPlotGroup(Fs, group2, clientName, onResultCompleteListener);


    }

    public void processAndPlotGroup(double fs, int[] group, OnResultCompleteListener onResultCompleteListener) {
        double[] ecg = new double[finalData[0].length];
        RPeakDetection rPeakDetection = new RPeakDetection();
        Utility ut = new Utility();
        HashMap<String, Double> map = new HashMap<String, Double>();
//        ArrayList<com.arrthymia.CalculateLeadWiseSummaryNew> hashMapArrayList = new ArrayList<com.arrthymia.CalculateLeadWiseSummaryNew>();
        ArrayList<HashMap<String, Double>> hashMapArrayList = new ArrayList<HashMap<String, Double>>();
        CardiacStruct cardiacStruct = new CardiacStruct();
        for (int leadIdx = group[0]; leadIdx <= group[group.length - 1]; leadIdx++) {
//        for (int leadIdx = 0; leadIdx < 1; leadIdx++) {
            HashMap<String, Double> mapLocal = new HashMap<String, Double>();
            ecg = finalData[group[leadIdx - group[0]]];
//            ArrayList<Integer> rPeaksList = rPeakDetection.improvedRPeakDetection(ecg, fs);
            ArrayList<Integer> rPeaksList = rPeakDetection.improvedRPeakDetectionVpc5August2025(ecg, fs);
            int[] rPeaks = new int[rPeaksList.size()];
            for (int j = 0; j < rPeaks.length; j++) {
                rPeaks[j] = rPeaksList.get(j);
//                System.out.println(j+"   ------- "+rPeaks[j]);
            }
//            System.out.println("---------- leadIdx ========   "+leadIdx);
            double baseLine = ut.mean(ecg);
//            com.arrthymia.PQSTDetection pqst = new com.arrthymia.PQSTDetection();
            PQSTDetectionNew pqstNew = new PQSTDetectionNew();
            if (rPeaks.length > 2) {
//                AllCalculatedData allData = pqst.detectPQSTFeatures(ecg, rPeaks, baseLine, fs);
//                AllCalculatedData allData = pqstNew.detectPQSTFeatures(ecg, rPeaks, baseLine, fs);
                AllCalculatedDataNew allData = pqstNew.detectPQSTFeatures(ecg, rPeaks, baseLine, fs);
//                Features features = allData.features;
//                Amplitude amplitude = allData.amplitude;
//                Duration duration = allData.duration;
                DurationNew duration = allData.duration;
                double[] rrIntervals = allData.rrIntervals;
//                double[] heartRate = allData.heartRate;
//                ArrayList<Double> stElevation = allData.stElevation;
//            allData.displayAllFeaturesData(features);
//            allData.displayAllAmplitudeData(amplitude);
//            allData.displayAllDurationData(duration);
//            allData.displayAllStElevation(allData.stElevation);
//                allData.displayAllStartAndEndIndices(allData.startAndEndIndexOfPoints);
//                System.out.println("Amplitude P size "+ Arrays.toString(allData.amplitude.P.Size()));
//                System.out.println("-------------------------------");
//                LoaderHelper ld = new LoaderHelper();
//                ld.viewData(allData.rrIntervals);
                CardiacData cardiacData = getCardiacDataObject(allData, leadIdx);

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
                CalculateLeadWiseSummaryNew summary = new CalculateLeadWiseSummaryNew();
                ArrayList<ArrayList<Double>> summaryList = summary.leadWiseCalculation(rPeaks, rrIntervals, leadIdx, allData);
                mapLocal = getLeadMetaData(summaryList);
                hashMapArrayList.add(mapLocal);
                if (leadIdx == 0)
                    cardiacStruct.setLead0CardiacStruct(cardiacData);
                if (leadIdx == 1)
                    cardiacStruct.setLead1CardiacStruct(cardiacData);
                if (leadIdx == 2)
                    cardiacStruct.setLead2CardiacStruct(cardiacData);
                if (leadIdx == 3)
                    cardiacStruct.setLead3CardiacStruct(cardiacData);
                if (leadIdx == 4)
                    cardiacStruct.setLead4CardiacStruct(cardiacData);
                if (leadIdx == 5)
                    cardiacStruct.setLead5CardiacStruct(cardiacData);
                if (leadIdx == 6)
                    cardiacStruct.setLead6CardiacStruct(cardiacData);
                if (leadIdx == 7)
                    cardiacStruct.setLead7CardiacStruct(cardiacData);
                if (leadIdx == 8) {
                    map = getMeanOfArrayListHashMap(hashMapArrayList);
                    cardiacStruct.setLead8CardiacStruct(cardiacData);
                }
                // map = getLead2MetaData(summaryList);
//                onResultCompleteListener.onComplete(rPeaks, rrIntervals, leadIdx, allData, summaryList);
            }   // rPeaks length end condition
            else {
                System.out.println("Into else part for one rPeak for Lead_" + leadIdx + "..............  ");
//                CardiacData cardiacData = null;
                mapLocal = getDefaultMetaData();
                hashMapArrayList.add(mapLocal);
                if (leadIdx == 8) {
                    map = getMeanOfArrayListHashMap(hashMapArrayList);
                }
//                onResultCompleteListener.rPeaksLessThanTwo(rPeaks, leadIdx);
//                writeIntoFileForOneRPeak(rPeaks, leadIdx, clientName);
            }
        }
        TwelveLeadEcgData twelveLeadEcgData;
        twelveLeadEcgData = createTwelveLeadData(onResultCompleteListener);
        String arrhythmiaResult = "";
        try{
//            onResultCompleteListener.onCompletedLead2MetaData(twelveLeadEcgData, map);
            ArrhythmiaDetection arrhythmia = new ArrhythmiaDetection();
            arrhythmiaResult = arrhythmia.detectArrhythmia(cardiacStruct, finalData, fs, map);
        } catch (Exception e) {
            System.out.println("\033[1mError *** Issue in Arrhythmia Detection ---- \033[0m");
            e.printStackTrace();
        }
        try {
            StemiDetection stemi = new StemiDetection();
            stemi.detectStemi(cardiacStruct, fs, onResultCompleteListener, twelveLeadEcgData, map, ut, arrhythmiaResult);
        } catch (Exception e) {
            System.out.println("\033[1mError *** Issue in STEMI Detection ---- \033[0m");
            e.printStackTrace();
        }
    }

    //  Method updated on 02 May 2025,
//    private HashMap<String, Double> getMeanOfArrayListHashMap(ArrayList<HashMap<String, Double>> list) {
//        // Currently mean of 4 lead data i.e Lead2, V6, V7, V8
//        HashMap<String, Double> hashMap = new HashMap<>();
//
//            System.out.println("heartRate-----   " +
//                    list.get(1).get("heartRate") +
//                    "  ," + list.get(6).get("heartRate") + "  ," + list.get(7).get("heartRate") + "  ," + list.get(8).get("heartRate"));
//
//
//            System.out.println("prInterval-----   " +
//                    list.get(1).get("prInterval") +
//                    "  ," + list.get(6).get("prInterval") + "  ," + list.get(7).get("prInterval") + "  ," + list.get(8).get("prInterval"));
//
//
//            System.out.println("qrsInterval-----   " +
//                    list.get(1).get("qrsInterval") +
//                    "  ," + list.get(6).get("qrsInterval") + "  ," + list.get(7).get("qrsInterval") + "  ," + list.get(8).get("qrsInterval"));
//
//            System.out.println("qtInterval-----   " +
//                    list.get(1).get("qtInterval") +
//                    "  ," + list.get(6).get("qtInterval") + "  ," + list.get(7).get("qtInterval") + "  ," + list.get(8).get("qtInterval"));
//
//            hashMap.put("heartRate", (double) Math.round((
//                    list.get(1).get("heartRate") +
//                            list.get(6).get("heartRate") + list.get(7).get("heartRate") + list.get(8).get("heartRate")
//            ) / 4));
//
//            hashMap.put("prInterval", (double) Math.round((
//                    list.get(1).get("prInterval") +
//                            list.get(6).get("prInterval") + list.get(7).get("prInterval") + list.get(8).get("prInterval")
//            ) / 4));
//            hashMap.put("qrsInterval", (double) Math.round((
//                    list.get(1).get("qrsInterval") +
//                            list.get(6).get("qrsInterval") + list.get(7).get("qrsInterval") + list.get(8).get("qrsInterval")
//            ) / 4));
//            hashMap.put("qtInterval", (double) Math.round((
//                    list.get(1).get("qtInterval") +
//                            list.get(6).get("qtInterval") + list.get(7).get("qtInterval") + list.get(8).get("qtInterval")
//            ) / 4));
//            hashMap.put("qtcInterval", (double) Math.round((
//                    list.get(1).get("qtcInterval") +
//                            list.get(6).get("qtcInterval") + list.get(7).get("qtcInterval") + list.get(8).get("qtcInterval")
//            ) / 4));
//        return hashMap;
//    }

    private HashMap<String, Double> getMeanOfArrayListHashMap(ArrayList<HashMap<String, Double>> list) {
        // Currently mean of 4 lead data i.e Lead2, V4, V5, V6
        HashMap<String, Double> hashMap = new HashMap<>();

//        System.out.println("heartRate-----   " +
//                list.get(1).get("heartRate") +
//                "  ," + list.get(6).get("heartRate") + "  ," + list.get(7).get("heartRate") + "  ," + list.get(8).get("heartRate"));
//
//
//        System.out.println("prInterval-----   " +
//                list.get(1).get("prInterval") +
//                "  ," + list.get(6).get("prInterval") + "  ," + list.get(7).get("prInterval") + "  ," + list.get(8).get("prInterval"));
//
//
//        System.out.println("qrsInterval-----   " +
//                list.get(1).get("qrsInterval") +
//                "  ," + list.get(6).get("qrsInterval") + "  ," + list.get(7).get("qrsInterval") + "  ," + list.get(8).get("qrsInterval"));
//
//        System.out.println("qtInterval-----   " +
//                list.get(1).get("qtInterval") +
//                "  ," + list.get(6).get("qtInterval") + "  ," + list.get(7).get("qtInterval") + "  ," + list.get(8).get("qtInterval"));
        hashMap.put("heartRate", calculateMeanData("heartRate",list.get(1).get("heartRate") ,
                list.get(6).get("heartRate") , list.get(7).get("heartRate") , list.get(8).get("heartRate")));
//        hashMap.put("heartRate", (double) Math.round((
//                list.get(1).get("heartRate") +
//                        list.get(6).get("heartRate") + list.get(7).get("heartRate") + list.get(8).get("heartRate")
//        ) / 4));

        hashMap.put("prInterval", calculateMeanData("prInterval", list.get(1).get("prInterval") ,
                list.get(6).get("prInterval") , list.get(7).get("prInterval") , list.get(8).get("prInterval")));

//        hashMap.put("prInterval", (double) Math.round((
//                list.get(1).get("prInterval") +
//                        list.get(6).get("prInterval") + list.get(7).get("prInterval") + list.get(8).get("prInterval")
//        ) / 4));

        hashMap.put("qrsInterval", calculateMeanData("qrsInterval", list.get(1).get("qrsInterval") ,
                list.get(6).get("qrsInterval") , list.get(7).get("qrsInterval") , list.get(8).get("qrsInterval")));

//        hashMap.put("qrsInterval", (double) Math.round((
//                list.get(1).get("qrsInterval") +
//                        list.get(6).get("qrsInterval") + list.get(7).get("qrsInterval") + list.get(8).get("qrsInterval")
//        ) / 4));

        hashMap.put("qtInterval", calculateMeanData("qtInterval", list.get(1).get("qtInterval") ,
                list.get(6).get("qtInterval") , list.get(7).get("qtInterval") , list.get(8).get("qtInterval")));

//        hashMap.put("qtInterval", (double) Math.round((
//                list.get(1).get("qtInterval") +
//                        list.get(6).get("qtInterval") + list.get(7).get("qtInterval") + list.get(8).get("qtInterval")
//        ) / 4));

        hashMap.put("qtcInterval", calculateMeanData("qtcInterval", list.get(1).get("qtcInterval") ,
                list.get(6).get("qtcInterval") , list.get(7).get("qtcInterval") , list.get(8).get("qtcInterval")));

//        hashMap.put("qtcInterval", (double) Math.round((
//                list.get(1).get("qtcInterval") +
//                        list.get(6).get("qtcInterval") + list.get(7).get("qtcInterval") + list.get(8).get("qtcInterval")
//        ) / 4));
        return hashMap;
    }

//  Method created on 14 July 2025
    public double calculateMeanData(String label , Double... values) {
        double sum = 0;
        double count = 0;
        System.out.print(label+"-----   ");
        for (Double value : values) {
            if (value != null) {
                sum+= value;
                count++;
                System.out.print(value+",  ");
            }
        }
        System.out.println();
        return ((double) (Math.round(sum/count) * 100) / 100);
    }

//  Method created on 01 May 2025
    public HashMap<String, Double> getLeadMetaData(ArrayList<ArrayList<Double>> summaryList) {
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

    public HashMap<String, Double> getDefaultMetaData() {
        HashMap<String, Double> hashMap = new HashMap<String, Double>();
        hashMap.put("heartRate", null );
        hashMap.put("heartRateBpm", null );
        hashMap.put("prInterval", null );
        hashMap.put("prIntervalBpm", null);
        hashMap.put("qrsInterval", null);
        hashMap.put("qrsIntervalBpm", null);
        hashMap.put("qtInterval", null);
        hashMap.put("qtIntervalBpm", null);
        hashMap.put("qtcInterval", null);
        hashMap.put("qtcIntervalBpm", null);
        return hashMap;
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
            for (int j = 0; j < finalData[i].length; j++) {
                list.add(Double.valueOf(finalData[i][j]));
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
                derivedList(twelveLeadEcgData);
            }
        }
        return twelveLeadEcgData;
    }

    private void derivedList(TwelveLeadEcgData twelveLeadEcgData) {
//        ArrayList<Double> avr = IntStream.range(0, twelveLeadEcgData.getLead1().size()).mapToObj(index ->
//                -1 * ( (twelveLeadEcgData.getLead1().get(index) + twelveLeadEcgData.getLead2().get(index)) / 2) ).collect(
//                Collectors.toCollection(ArrayList<Double>::new));
//        ArrayList<Double> avl = IntStream.range(0, twelveLeadEcgData.getLead1().size()).mapToObj(index ->
//                ( (twelveLeadEcgData.getLead1().get(index) - twelveLeadEcgData.getLead3().get(index)) / 2) ).collect(
//                Collectors.toCollection(ArrayList<Double>::new));
//        ArrayList<Double> avf = IntStream.range(0, twelveLeadEcgData.getLead1().size()).mapToObj(index ->
//                ( (twelveLeadEcgData.getLead2().get(index) + twelveLeadEcgData.getLead3().get(index)) / 2) ).collect(
//                Collectors.toCollection(ArrayList<Double>::new));
        ArrayList<Double> avr = IntStream.range(0, twelveLeadEcgData.getLead1().size()).mapToObj(index ->
            (double) Math.round(-1 * ( (twelveLeadEcgData.getLead1().get(index) + twelveLeadEcgData.getLead2().
                    get(index)) / 2) ) ).collect( Collectors.toCollection(ArrayList<Double>::new));
        ArrayList<Double> avl = IntStream.range(0, twelveLeadEcgData.getLead1().size()).mapToObj(index ->
            (double) Math.round( (twelveLeadEcgData.getLead1().get(index) - twelveLeadEcgData.getLead3().get(index))
                    / 2) ).collect( Collectors.toCollection(ArrayList<Double>::new));
        ArrayList<Double> avf = IntStream.range(0, twelveLeadEcgData.getLead1().size()).mapToObj(index ->
            (double) Math.round( (twelveLeadEcgData.getLead2().get(index) + twelveLeadEcgData.getLead3().get(index))
                    / 2) ).collect( Collectors.toCollection(ArrayList<Double>::new));
        twelveLeadEcgData.setAvr(avr);
        twelveLeadEcgData.setAvl(avl);
        twelveLeadEcgData.setAvf(avf);
    }

//    30 May 2025
    private CardiacData getCardiacDataObject(AllCalculatedDataNew allData, int leadIdx) {
        int zz = allData.rrIntervals.length - 1;
        int zzz = allData.amplitude.P.getColumnList(1).size();
        CardiacData cardiacData = new CardiacData();
    //  RStart, REnd, JStart, JEnd, JDuration not calculated, so not saved in CardiacData
        for (int i = 0; i < allData.rrIntervals.length - 1; i++) {        // last rrInterval is left.
            cardiacData.addCycle(i);
            cardiacData.addRrInterval(allData.rrIntervals[i]);
            cardiacData.addHeatRate(allData.heartRate[i]);
            // P data
            cardiacData.addPStartIndex(allData.startAndEndIndexOfPoints.getPStart(i));
            cardiacData.addPPeakIndex(allData.features.P.getIndex(i));
            cardiacData.addPStopIndex(allData.startAndEndIndexOfPoints.getPEnd(i));
            cardiacData.addPAmplitudeMv(allData.amplitude.P.getColumnTwoValue(i));
            cardiacData.addPDuration(allData.duration.P.get(i));
            // Q data
            cardiacData.addQStartIndex(allData.startAndEndIndexOfPoints.getQStart(i));
            cardiacData.addQPeakIndex(allData.features.Q.getIndex(i));
            cardiacData.addQStopIndex(allData.startAndEndIndexOfPoints.getQEnd(i));
            cardiacData.addQAmplitudeMv(allData.amplitude.Q.getColumnTwoValue(i));
            cardiacData.addQDuration(allData.duration.Q.get(i));

            // R data
            cardiacData.addRPeakIndex(allData.features.R.getIndex(i));
            cardiacData.addRAmplitudeMv(allData.amplitude.R.getColumnTwoValue(i));
            cardiacData.addRDuration(allData.duration.R.get(i));

            // S data
            cardiacData.addSStartIndex(allData.startAndEndIndexOfPoints.getSStart(i));
            cardiacData.addSPeakIndex(allData.features.S.getIndex(i));
            cardiacData.addSStopIndex(allData.startAndEndIndexOfPoints.getSEnd(i));
            cardiacData.addSAmplitudeMv(allData.amplitude.S.getColumnTwoValue(i));
            cardiacData.addSDuration(allData.duration.S.get(i));

            //  T data
            cardiacData.addTStartIndex(allData.startAndEndIndexOfPoints.getTStart(i));
            cardiacData.addTPeakIndex(allData.features.T.getIndex(i));
            cardiacData.addTStopIndex(allData.startAndEndIndexOfPoints.tEnd.get(i));
            cardiacData.addTAmplitudeMv(allData.amplitude.T.getColumnTwoValue(i));
            cardiacData.addTDuration(allData.duration.T.get(i));

            // J data
            cardiacData.addJPeakIndex(allData.features.J.getIndex(i));
            cardiacData.addJAmplitudeMv(allData.amplitude.J.getColumnTwoValue(i));

            // PR, QRS Duration, qt, qtc Interval, st Elevation,
            cardiacData.addQrsDuration(allData.duration.QRS.get(i));
            cardiacData.addPrInterval(allData.duration.PR.get(i));
            cardiacData.addQtInterval(allData.duration.QT.get(i));
            cardiacData.addQtcInterval(allData.duration.QTc.get(i));
            cardiacData.addStElevation(allData.stElevation.get(i));

            // stSagitaMv, stMorphologyCode, stTombstoneFlag
            cardiacData.addStSagitaMv(allData.stSagitaMv.get(i));
            cardiacData.addStMorphologyCode(allData.stMorphologyCode.get(i));
            cardiacData.addStTombstoneFlag(allData.stTombstoneFlag.get(i));
        }
        return cardiacData;
    }

}
