package com.stemi;

import com.stemi.dataClasses.AllCalculatedDataNew;
import com.stemi.dataClasses.TwelveLeadEcgData;

import java.util.ArrayList;
import java.util.HashMap;

public interface OnResultCompleteListener {
    void onComplete(int[] rPeaks, double[] rrIntervals, int leadIdx, AllCalculatedDataNew allData, ArrayList<ArrayList<Double>> summaryList);
    void rPeaksLessThanTwo(int[] rPeaks, int leadIdx);
    void onFailed(String message);
    void onCompletedLead2MetaData(TwelveLeadEcgData twelveLeadEcgData, HashMap<String, Double> hashMap, String arrhythmiaSummary,
                                  String stemiResult, String ischemiaResult);

}
