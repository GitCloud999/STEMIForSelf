package com.stemi;

import com.stemi.dataClasses.AllCalculatedDataNew;
import com.stemi.libs.Utility;

import java.util.ArrayList;

public class CalculateLeadWiseSummaryNew {

    public ArrayList<ArrayList<Double>> leadWiseCalculation(int[] rPeaks, double[] rrIntervals, int leadIdx, AllCalculatedDataNew allData) {
        Utility ut = new Utility();
        ArrayList<ArrayList<Double>> listMain = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> subListOne = new ArrayList<Double>();
        double heartRateFinal = ut.mean(allData.heartRate);
        double bpmHeartRate = ut.calculateSD(allData.heartRate);
        subListOne.add(heartRateFinal); // 0 index
        subListOne.add(bpmHeartRate );  //  1 index
//        System.out.println("Lead_"+(leadIdx+1)+"heartRate & BPM ==========" +heartRateFinal +"  ,  "+ bpmHeartRate);
        double rrIntervalsMean = ut.mean(allData.rrIntervals);
        double bpmRrInterval = ut.calculateSD(allData.rrIntervals);
        subListOne.add(rrIntervalsMean);  //  2 index
        subListOne.add(bpmRrInterval );    //  3  index
        double stElevationMean = ut.findMean(allData.stElevation);
        double stElevationStd = ut.calculateStd(allData.stElevation);
        subListOne.add(stElevationMean);  //  4 index
        subListOne.add(stElevationStd );  //  5 index
        listMain.add(subListOne);     // 0 index MainList
        
        double pAmplitudeColumnTwoMean = ut.findMean(allData.amplitude.P.getColumnList(1));
        double bpmPAmplitudeColumnTwoStd = ut.calculateStd(allData.amplitude.P.getColumnList(1));
        double pAmplitudeColumnThreeMean = ut.findMean(allData.amplitude.P.getColumnList(2));
        double bpmPAmplitudeColumnThreeStd = ut.calculateStd(allData.amplitude.P.getColumnList(2));
        ArrayList<Double> subListForAmplitudeP = new ArrayList<Double>();
        subListForAmplitudeP.add(pAmplitudeColumnTwoMean);   //  0 index 
        subListForAmplitudeP.add(pAmplitudeColumnThreeMean);     // 1 index
        subListForAmplitudeP.add(bpmPAmplitudeColumnTwoStd  );     // 2 index
        subListForAmplitudeP.add(bpmPAmplitudeColumnThreeStd  );   // 3 index
        listMain.add(subListForAmplitudeP);      // 1 index MainList for amplitude.P
        
        double qAmplitudeColumnTwoMean = ut.findMean(allData.amplitude.Q.getColumnList(1));
        double bpmQAmplitudeColumnTwoStd = ut.calculateStd(allData.amplitude.Q.getColumnList(1));
        double qAmplitudeColumnThreeMean = ut.findMean(allData.amplitude.Q.getColumnList(2));
        double bpmQAmplitudeColumnThreeStd = ut.calculateStd(allData.amplitude.Q.getColumnList(2));
        ArrayList<Double> subListForAmplitudeQ = new ArrayList<Double>();
        subListForAmplitudeQ.add(qAmplitudeColumnTwoMean);       //  0 index
        subListForAmplitudeQ.add(qAmplitudeColumnThreeMean);     //  1 index
        subListForAmplitudeQ.add(bpmQAmplitudeColumnTwoStd  );     //  2 index
        subListForAmplitudeQ.add(bpmQAmplitudeColumnThreeStd );   //  3 index
        listMain.add(subListForAmplitudeQ);      // 2 index MainList for amplitude.Q

        double rAmplitudeColumnTwoMean = ut.findMean(allData.amplitude.R.getColumnList(1));
        double bpmRAmplitudeColumnTwoStd = ut.calculateStd(allData.amplitude.R.getColumnList(1));
        double rAmplitudeColumnThreeMean = ut.findMean(allData.amplitude.R.getColumnList(2));
        double bpmRAmplitudeColumnThreeStd = ut.calculateStd(allData.amplitude.R.getColumnList(2));
        ArrayList<Double> subListForAmplitudeR = new ArrayList<Double>();
        subListForAmplitudeR.add(rAmplitudeColumnTwoMean);       // 0 index
        subListForAmplitudeR.add(rAmplitudeColumnThreeMean);     //  1 index
        subListForAmplitudeR.add(bpmRAmplitudeColumnTwoStd );     //  2 index
        subListForAmplitudeR.add(bpmRAmplitudeColumnThreeStd  );   //  3 index
        listMain.add(subListForAmplitudeR);      // 3 index MainList for amplitude.R

        double sAmplitudeColumnTwoMean = ut.findMean(allData.amplitude.S.getColumnList(1));
        double bpmSAmplitudeColumnTwoStd = ut.calculateStd(allData.amplitude.S.getColumnList(1));
        double sAmplitudeColumnThreeMean = ut.findMean(allData.amplitude.S.getColumnList(2));
        double bpmSAmplitudeColumnThreeStd = ut.calculateStd(allData.amplitude.S.getColumnList(2));
        ArrayList<Double> subListForAmplitudeS = new ArrayList<Double>();
        subListForAmplitudeS.add(sAmplitudeColumnTwoMean);       //  0 index
        subListForAmplitudeS.add(sAmplitudeColumnThreeMean);     //  1 index
        subListForAmplitudeS.add(bpmSAmplitudeColumnTwoStd );     //  2 index
        subListForAmplitudeS.add(bpmSAmplitudeColumnThreeStd );   //  3 index
        listMain.add(subListForAmplitudeS);      // 4 index MainList for amplitude.S

        double tAmplitudeColumnTwoMean = ut.findMean(allData.amplitude.T.getColumnList(1));
        double bpmTAmplitudeColumnTwoStd = ut.calculateStd(allData.amplitude.T.getColumnList(1));
        double tAmplitudeColumnThreeMean = ut.findMean(allData.amplitude.T.getColumnList(2));
        double bpmTAmplitudeColumnThreeStd = ut.calculateStd(allData.amplitude.T.getColumnList(2));
        ArrayList<Double> subListForAmplitudeT = new ArrayList<Double>();
        subListForAmplitudeT.add(tAmplitudeColumnTwoMean);   // 0 index
        subListForAmplitudeT.add(tAmplitudeColumnThreeMean);     //  1 index
        subListForAmplitudeT.add(bpmTAmplitudeColumnTwoStd  );     //  2 index
        subListForAmplitudeT.add(bpmTAmplitudeColumnThreeStd  );   // 3 index
        listMain.add(subListForAmplitudeT);      // 5 index MainList for amplitude.T

        double jAmplitudeColumnTwoMean = ut.findMean(allData.amplitude.J.getColumnList(1));
        double bpmJAmplitudeColumnTwoStd = ut.calculateStd(allData.amplitude.J.getColumnList(1));
        double jAmplitudeColumnThreeMean = ut.findMean(allData.amplitude.J.getColumnList(2));
        double bpmJAmplitudeColumnThreeStd = ut.calculateStd(allData.amplitude.J.getColumnList(2));
        ArrayList<Double> subListForAmplitudeJ = new ArrayList<Double>();
        subListForAmplitudeJ.add(jAmplitudeColumnTwoMean);       // 0 index
        subListForAmplitudeJ.add(jAmplitudeColumnThreeMean);     //  1 index
        subListForAmplitudeJ.add(bpmJAmplitudeColumnTwoStd );     //  2 index
        subListForAmplitudeJ.add(bpmJAmplitudeColumnThreeStd );   //  3 index
        listMain.add(subListForAmplitudeJ);      // 6 index MainList for amplitude.J

//        double pDurationMean = ut.findMeanInt(allData.duration.P);
        double pDurationMean = ut.findMean(allData.duration.P);
        double qDurationMean = ut.findMeanInt(allData.duration.Q);
        double rDurationMean = ut.findMeanInt(allData.duration.R);
        double sDurationMean = ut.findMeanInt(allData.duration.S);
        double tDurationMean = ut.findMeanInt(allData.duration.T);
        double prDurationMean = ut.findMean(allData.duration.PR);
        double qrsDurationMean = ut.findMeanInt(allData.duration.QRS);
        double qtDurationMean = ut.findMeanInt(allData.duration.QT);
        double qtcDurationMean = ut.findMean(allData.duration.QTc);

        double bpmPDurationStd = ut.calculateStd(allData.duration.P);
        double bpmQDurationStd = ut.calculateStdInt(allData.duration.Q);
        double bpmRDurationStd = ut.calculateStdInt(allData.duration.R);
        double bpmSDurationStd = ut.calculateStdInt(allData.duration.S);
        double bpmTDurationStd = ut.calculateStdInt(allData.duration.T);
        double bpmPRDurationStd = ut.calculateStd(allData.duration.PR);
        double bpmQRSDurationStd = ut.calculateStdInt(allData.duration.QRS);
        double bpmQTDurationStd = ut.calculateStdInt(allData.duration.QT);
        double bpmQTcDurationStd = ut.calculateStd(allData.duration.QTc);
        ArrayList<Double> subListForDuration = new ArrayList<Double>();
        subListForDuration.add(pDurationMean );     //  0 index
        subListForDuration.add(bpmPDurationStd );       //  1 index
        subListForDuration.add(qDurationMean );      //  2 index
        subListForDuration.add(bpmQDurationStd );    //  3 index
        subListForDuration.add(rDurationMean );      //  4 index
        subListForDuration.add(bpmRDurationStd );    //  5 index
        subListForDuration.add(sDurationMean );      //  6 index
        subListForDuration.add(bpmSDurationStd );    //  7 index
        subListForDuration.add(tDurationMean );      //  8 index
        subListForDuration.add(bpmTDurationStd );    // 9 index
        subListForDuration.add(prDurationMean );     // 10 index
        subListForDuration.add(bpmPRDurationStd );   //  11 index
        subListForDuration.add(qrsDurationMean );    //  12 index
        subListForDuration.add(bpmQRSDurationStd );   // 13 index
        subListForDuration.add(qtDurationMean );     // 14 index
        subListForDuration.add(bpmQTDurationStd );   //  15 index

        subListForDuration.add(qtcDurationMean);     // 16 index
        subListForDuration.add(ut.reduceValueAfterPoints(bpmQTcDurationStd * 10, 3));   //  17 index

        listMain.add(subListForDuration);       // 7 index MainList for duration
        return listMain;

    }
}
