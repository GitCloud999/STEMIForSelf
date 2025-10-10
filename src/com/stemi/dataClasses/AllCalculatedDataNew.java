package com.stemi.dataClasses;

import java.util.ArrayList;

public class AllCalculatedDataNew {
    public Features features;
    public Amplitude amplitude;
    public DurationNew duration;
    public ArrayList<Double> stElevation;
    public double[] rrIntervals;
    public double[] heartRate;
    public StartAndEndIndexOfPoints startAndEndIndexOfPoints;
    public ArrayList<Double> stSagitaMv;
    public ArrayList<Integer> stMorphologyCode;
    public ArrayList<Boolean> stTombstoneFlag;


    public void displayAllFeaturesData(Features features){
        System.out.println("------------- features.P -------------------");
        features.displayFeaturesData(features.P);
        System.out.println("***********************************************");
        System.out.println("------------- features.Q -------------------");
        features.displayFeaturesData(features.Q);
        System.out.println("***********************************************");

        System.out.println("------------- features.R -------------------");
        features.displayFeaturesData(features.R);
        System.out.println("***********************************************");

        System.out.println("------------- features.S -------------------");
        features.displayFeaturesData(features.S);
        System.out.println("***********************************************");

        System.out.println("------------- features.T -------------------");
        features.displayFeaturesData(features.T);
        System.out.println("***********************************************");

        System.out.println("------------- features.J -------------------");
        features.displayFeaturesData(features.J);
        System.out.println("***********************************************");

    }
    
    public void displayAllAmplitudeData(Amplitude amplitude){
        System.out.println("------------- amplitude.P -------------------");
        amplitude.displayAmplitudeData(amplitude.P);
        System.out.println("***********************************************");
        System.out.println("------------- amplitude.Q -------------------");
        amplitude.displayAmplitudeData(amplitude.Q);
        System.out.println("***********************************************");
        System.out.println("------------- amplitude.R -------------------");
        amplitude.displayAmplitudeData(amplitude.R);
        System.out.println("***********************************************");
        System.out.println("------------- amplitude.S -------------------");
        amplitude.displayAmplitudeData(amplitude.S);
        System.out.println("***********************************************");
        System.out.println("------------- amplitude.T -------------------");
        amplitude.displayAmplitudeData(amplitude.T);
        System.out.println("***********************************************");
        System.out.println("------------- amplitude.J -------------------");
        amplitude.displayAmplitudeData(amplitude.J);
        System.out.println("***********************************************");
    }

    public void displayAllDurationData(DurationNew duration){
        System.out.println("------------- duration.P -------------------");
        duration.displayDurationDoubleData(duration.P);
        System.out.println("***********************************************");
        System.out.println("------------- duration.Q -------------------");
        duration.displayDurationData(duration.Q);
        System.out.println("***********************************************");
        System.out.println("------------- duration.R -------------------");
        duration.displayDurationData(duration.R);
        System.out.println("***********************************************");
        System.out.println("------------- duration.S -------------------");
        duration.displayDurationData(duration.S);
        System.out.println("***********************************************");
        System.out.println("------------- duration.T -------------------");
        duration.displayDurationData(duration.T);
        System.out.println("***********************************************");
        System.out.println("------------- duration.PR -------------------");
        duration.displayDurationDoubleData(duration.PR);
        System.out.println("***********************************************");
        System.out.println("------------- duration.QRS -------------------");
        duration.displayDurationData(duration.QRS);
        System.out.println("***********************************************");
        System.out.println("------------- duration.QT -------------------");
        duration.displayDurationData(duration.QT);
        System.out.println("***********************************************");
        System.out.println("------------- duration.QTc -------------------");
        duration.displayDurationDoubleData(duration.QTc);
        System.out.println("***********************************************");
    }

    public void displayAllStElevation(ArrayList<Double> stElevation){
        for (int i = 0; i < stElevation.size(); i++) {
            System.out.println("i == "+i+" -------- stElevation ==  "+stElevation.get(i));
        }
    }

    public void displayAllStartAndEndIndices(StartAndEndIndexOfPoints startAndEnd) {
        System.out.println("------------- startAndEndIndexOfPoints.pStart -------------------");
        startAndEnd.displayStartAndEndIndices(startAndEnd.pStart);
        System.out.println("***********************************************");
        System.out.println("------------- startAndEndIndexOfPoints.pEnd -------------------");
        startAndEnd.displayStartAndEndIndices(startAndEnd.pEnd);
        System.out.println("***********************************************");
        System.out.println("------------- startAndEndIndexOfPoints.qStart -------------------");
        startAndEnd.displayStartAndEndIndices(startAndEnd.qStart);
        System.out.println("***********************************************");
        System.out.println("------------- startAndEndIndexOfPoints.qEnd -------------------");
        startAndEnd.displayStartAndEndIndices(startAndEnd.qEnd);
        System.out.println("***********************************************");
        System.out.println("------------- startAndEndIndexOfPoints.sStart -------------------");
        startAndEnd.displayStartAndEndIndices(startAndEnd.sStart);
        System.out.println("***********************************************");
        System.out.println("------------- startAndEndIndexOfPoints.sEnd -------------------");
        startAndEnd.displayStartAndEndIndices(startAndEnd.sEnd);
        System.out.println("***********************************************");
        System.out.println("------------- startAndEndIndexOfPoints.tStart -------------------");
        startAndEnd.displayStartAndEndIndices(startAndEnd.tStart);
        System.out.println("***********************************************");
        System.out.println("------------- startAndEndIndexOfPoints.tEnd -------------------");
        startAndEnd.displayStartAndEndIndices(startAndEnd.tEnd);
        System.out.println("***********************************************");

    }

}
