package com.stemi.oldVersions;

import com.stemi.dataClasses.Amplitude;
import com.stemi.dataClasses.Features;
import com.stemi.dataClasses.StartAndEndIndexOfPoints;

import java.util.ArrayList;

public class AllCalculatedData {
    public Features features;
    public Amplitude amplitude;
    public Duration duration;
    public ArrayList<Double> stElevation;
    public double[] rrIntervals;
    public double[] heartRate;
    public StartAndEndIndexOfPoints startAndEndIndexOfPoints;

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

    public void displayAllDurationData(Duration duration){
        System.out.println("------------- duration.P -------------------");
        duration.displayDurationData(duration.P);
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
        duration.displayDurationData(duration.PR);
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

}
