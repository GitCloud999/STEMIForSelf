package com.stemi.libs;

import com.stemi.dataClasses.AllCalculatedDataNew;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileWriterPointDetection {

    public void writeIntoFileForOneRPeak(int[] rPeaks, int leadIdx) {
//        String fldr = "C:\\Users\\sabyr\\OneDrive\\Desktop\\Rahul\\Spandan_ultra_12L_filter_december_2024\\NewCases\\Distortion and signal processing case\\rawFiles\\Spandan Ultra 12 L clinical Trials TXT_Data\\Results_PointDetection";
//        String fldr = "C:\\Users\\sabyr\\OneDrive\\Desktop\\Rahul\\Spandan_ultra_12L_filter_december_2024\\NewCases\\Distortion and signal processing case\\rawFiles\\Spandan Ultra 12 L clinical Trials TXT_Data\\JavaResults\\Results_Softwares_PointDetection_Clinical_28Feb2025";
        String[] roman = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        try {
//            String directoryName = fldr + "\\" + clientName + "_Java_raw_amplitude_andDurations";
            String directoryName = "Results";
            File directory = new File(directoryName);
            if (!directory.exists()) {
                directory.mkdir();
            }
            File file = new File(directory.getAbsolutePath() + File.separator + "Lead_" + roman[leadIdx] + "_CycleDataJava.txt");
            PrintWriter pw = new PrintWriter(file);
            String[] column = {"Cycle", "RR Int (ms)", "P Amp (mV)", "P Amp (mm)", "Q Amp (mV)",
                    "Q Amp (mm)", "R Amp (mV)", "R Amp (mm)", "S Amp (mV)", "S Amp (mm)",
                    "T Amp (mV)", "T Amp (mm)", "J Amp (mV)", "J Amp (mm)", "P Dur (ms)",
                    "Q Dur (ms)", "R Dur (ms)", "S Dur (ms)", "T Dur (ms)", "PR Int (ms)",
                    "QRS Dur (ms)", "QT Int (ms)", "QTc (ms)", "ST Elev (mV)", "ST Elev (mm)"};
            pw.println("Cardiac Cycle-Wise Data for Lead " + roman[leadIdx]);
            pw.println();
            for (String s : column) {
                pw.print(String.format("%-10s", s));
            }
            pw.println();
            String[] data = new String[25];
            data[0] = "1";
            pw.print(String.format("%-10s", data[0]));
            for (int i = 1; i < data.length; i++) {
                data[i] = Double.toString(0.0);
                pw.print(String.format("%-10s", data[i]));
            }
            pw.println();
            pw.println();
            pw.println("======================= Summary ======================================");
            pw.println();
            pw.println("Heart Rate : "+0 +" +- "+0+" bpm");
            pw.println("RR Intervals : "+0+" +- "+0+" ms");
            pw.println("P Amplitude : "+0+" +- "+0+" mV, "+0 +" +- "+0+" mm");
            pw.println("Q Amplitude : "+0+" +- "+0+" mV, "+0 +" +- "+0+" mm");
            pw.println("R Amplitude : "+0+" +- "+0+" mV, "+0+" +- "+0+" mm");
            pw.println("S Amplitude : "+0+" +- "+0+" mV, "+0+" +- "+0+" mm");
            pw.println("T Amplitude : "+0+" +- "+0+" mV, "+0 +" +- "+0+" mm");
            pw.println("J Amplitude : "+0+" +- "+0+" mV, "+0 +" +- "+0+" mm");

            pw.println("P Duration : "+0 +" +- "+ 0 + " ms");
            pw.println("Q Duration : "+0 +" +- "+ 0 + " ms");
            pw.println("R Duration : "+0 +" +- "+ 0 + " ms");
            pw.println("S Duration : "+0 +" +- "+ 0 + " ms");
            pw.println("T Duration : "+0 +" +- "+ 0 + " ms");
            pw.println("PR Interval : "+0 +" +- "+ 0 + " ms");
            pw.println("QRS Interval : "+0 +" +- "+ 0 + " ms");
            pw.println("QT Interval : "+0 +" +- "+ 0 + " ms");

            pw.println("STElevation : "+0 +" +- "+ 0 + " mV, "+0 +" +- "+0);

            pw.close();
            System.out.println("Lead_"+ (leadIdx + 1)+ " Created............");
        } catch (Exception e) {
            System.out.println("Not able to write file.............");
            e.printStackTrace();
        }
    }


    public void writeIntoFile(int[] rPeaks, double[] rrIntervals, int leadIdx, AllCalculatedDataNew allData, ArrayList<ArrayList<Double>> summaryList) {
//        String fldrOutOld = "C:\\Users\\sabyr\\OneDrive\\Desktop\\Rahul\\Spandan_ultra_12L_filter_december_2024\\NewCases\\Distortion and signal processing case\\rawFiles\\Spandan Ultra 12 L clinical Trials TXT_Data\\JavaResults\\Results_PointDetection_Clinical_24Feb2025";
//        String clientName = "1722569222110000_raw.txt";
//        String fldrOut = "C:\\Users\\sabyr\\OneDrive\\Desktop\\Rahul\\Spandan_ultra_12L_filter_december_2024\\NewCases\\Distortion and signal processing case\\rawFiles\\Spandan Ultra 12 L clinical Trials TXT_Data\\JavaResults\\Results_Softwares_PointDetection_Clinical_28Feb2025";
//        String fldrOut = "Results";
        String[] roman = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        try {
//            String directoryName = fldrOut +"\\"+clientName+"_Java_raw_amplitude_andDurations";
            String directoryName = "Results";
            File directory = new File(directoryName);
            if (! directory.exists()){
                directory.mkdir();
            }
            File file = new File(directory.getAbsolutePath()+File.separator+"Lead_"+roman[leadIdx]+"_CycleDataJava.txt");
            System.out.println(file.getAbsolutePath());
            PrintWriter pw = new PrintWriter(file);
            String[] column = {"Cycle",	"RR Int (ms)",	"P Amp (mV)",	"P Amp (mm)",	"Q Amp (mV)",
                    "Q Amp (mm)",	"R Amp (mV)", 	"R Amp (mm)", 	"S Amp (mV)", 	"S Amp (mm)",
                    "T Amp (mV)", 	"T Amp (mm)", 	"J Amp (mV)",	"J Amp (mm)",	"P Dur (ms)",
                    "Q Dur (ms)",	"R Dur (ms)",	"S Dur (ms)",	"T Dur (ms)",	"PR Int (ms)",
                    "QRS Dur (ms)",	"QT Int (ms)",	"QTc (ms)",	"ST Elev (mV)",	"ST Elev (mm)" };
            pw.println("Cardiac Cycle-Wise Data for Lead "+roman[leadIdx]);
            pw.println();
            for (String s : column) {
                pw.print(String.format("%-10s", s));
            }
            Utility ut = new Utility();
            String[][] data = fillDataForTable(allData, rPeaks, column);
            pw.println();
            for (int r = 0; r < data.length; r++) {
//            for (int r = 0; r < 1; r++) {
                for (int i = 0; i < data[0].length; i++) {
                    if(data[r][i] != null) {
                        if ( (i < 14) || (i > 21) ) {
                            double temp = 0.0;

                            double test = Double.parseDouble(data[r][i]);
                            temp = (double) Math.round(test * 1000) / 1000;
                            data[r][i] = Double.toString(temp);
                        }
                    }
                    if (data[r][i] == null) {
                        data[r][i] = "0";
                    }
                    pw.print(String.format("%-10s", data[r][i]));
                }
                pw.println("---------------------------------------------------------------------------");
            }


            writeSummary(pw, summaryList);
//            if(leadIdx == 1)
//                showLead2Data(summaryList);
            pw.close();
            System.out.println("Lead_"+ (leadIdx + 1)+ " Created............");
        } catch (Exception e) {
            System.out.println("Not able to write file.............");
            e.printStackTrace();
        }
    }

    private void writeSummary(PrintWriter pw, ArrayList<ArrayList<Double>> summaryList) {
        pw.println();
        pw.println();
        pw.println("======================= Summary ======================================");
        pw.println();
        pw.println("Heart Rate : "+Math.round((summaryList.get(0).get(0))) +" +- "+summaryList.get(0).get(1)+" bpm");
        pw.println("RR Intervals : "+summaryList.get(0).get(2)+" +- "+summaryList.get(0).get(3)+" ms");
        pw.println("P Amplitude : "+summaryList.get(1).get(0)+" +- "+summaryList.get(1).get(2)
                +" mV, "+summaryList.get(1).get(1) +" +- "+summaryList.get(1).get(3)+" mm,  "
                +(double) Math.round(summaryList.get(1).get(0) * 1000 * 1000) / 1000+" +- "+(double) Math.round(summaryList.get(1).get(2) * 1000 * 1000) / 1000+ " uV");

        pw.println("Q Amplitude : "+summaryList.get(2).get(0)+" +- "+summaryList.get(2).get(2)
                +" mV, "+summaryList.get(2).get(1) +" +- "+summaryList.get(2).get(3)+" mm, "
                +(double) Math.round(summaryList.get(2).get(0) * 1000 * 1000) / 1000+" +- "+(double) Math.round(summaryList.get(2).get(2) * 1000 * 1000) / 1000+ " uV");

        pw.println("R Amplitude : "+summaryList.get(3).get(0)+" +- "+summaryList.get(3).get(2)
                +" mV, "+summaryList.get(3).get(1) +" +- "+summaryList.get(3).get(3)+" mm,  "
                +(double) Math.round(summaryList.get(3).get(0) * 1000 * 1000) / 1000+" +- "+(double) Math.round(summaryList.get(3).get(2) * 1000 * 1000) / 1000+" uV");

        pw.println("S Amplitude : "+summaryList.get(4).get(0)+" +- "+summaryList.get(4).get(2)
                +" mV, "+summaryList.get(4).get(1) +" +- "+summaryList.get(4).get(3)+" mm,  "
                +(double) Math.round(summaryList.get(4).get(0) * 1000 * 1000) / 1000+" +- "+(double) Math.round(summaryList.get(4).get(2) * 1000 * 1000) / 1000+" uV");

        pw.println("T Amplitude : "+summaryList.get(5).get(0)+" +- "+summaryList.get(5).get(2)
                +" mV, "+summaryList.get(5).get(1) +" +- "+summaryList.get(5).get(3)+" mm,  "
                +(double) Math.round(summaryList.get(5).get(0) * 1000 * 1000) / 1000+" +- "+(double) Math.round(summaryList.get(5).get(2) * 1000 * 1000) / 1000+ " uV");
        pw.println("J Amplitude : "+summaryList.get(6).get(0)+" +- "+summaryList.get(6).get(2)
                +" mV, "+summaryList.get(6).get(1) +" +- "+summaryList.get(6).get(3)+" mm,  "
                +(double) Math.round(summaryList.get(6).get(0) * 1000 * 1000) / 1000+" +- "+(double) Math.round(summaryList.get(6).get(2) * 1000 * 1000) / 1000+ " uV");

        pw.println("P Duration : "+summaryList.get(7).get(0) +" +- "+ summaryList.get(7).get(1) + " ms");
        pw.println("Q Duration : "+summaryList.get(7).get(2) +" +- "+ summaryList.get(7).get(3) + " ms");
        pw.println("R Duration : "+summaryList.get(7).get(4) +" +- "+ summaryList.get(7).get(5) + " ms");
        pw.println("S Duration : "+summaryList.get(7).get(6) +" +- "+ summaryList.get(7).get(7) + " ms");
        pw.println("T Duration : "+summaryList.get(7).get(8) +" +- "+ summaryList.get(7).get(9) + " ms");
        pw.println("PR Interval : "+summaryList.get(7).get(10) +" +- "+ summaryList.get(7).get(11) + " ms");
        pw.println("QRS Interval : "+summaryList.get(7).get(12) +" +- "+ summaryList.get(7).get(13) + " ms");
        pw.println("QT Interval : "+summaryList.get(7).get(14) +" +- "+ summaryList.get(7).get(15) + " ms");
        pw.println("QTc Interval : "+summaryList.get(7).get(16) +" +- "+ summaryList.get(7).get(17) + " ms");

        pw.println("STElevation : "+summaryList.get(0).get(4) +" +- "+ summaryList.get(0).get(5) + " mV, "
                +(double) Math.round(summaryList.get(0).get(4) * 10 * 1000) / 1000 +" +- "+(double) Math.round(summaryList.get(0).get(5) * 10 * 1000) / 1000+",  "
                +(double) Math.round(summaryList.get(0).get(4) * 1000 * 1000) / 1000 +" +- "+ (double) Math.round(summaryList.get(0).get(5) * 1000 * 1000) / 1000+ " uV ");

        pw.println();
        pw.println();
        pw.println();
        pw.println();
        pw.println("======================= Short Summary ======================================");
        pw.println();
        pw.println();
        pw.println("Heart Rate : "+Math.round((summaryList.get(0).get(0))) +" +- "+summaryList.get(0).get(1)+" bpm");
        pw.println("PR Interval : "+summaryList.get(7).get(10) +" +- "+ summaryList.get(7).get(11) + " ms");
        pw.println("QRS Interval : "+summaryList.get(7).get(12) +" +- "+ summaryList.get(7).get(13) + " ms");
        pw.println("QT Interval : "+summaryList.get(7).get(14) +" +- "+ summaryList.get(7).get(15) + " ms");
        pw.println("QTc Interval : "+summaryList.get(7).get(16) +" +- "+ summaryList.get(7).get(17) + " ms");


    }

    private String[][] fillDataForTable(AllCalculatedDataNew allData, int[] rPeaks, String[] column) {
        String[][] combinedData = new String[rPeaks.length][column.length];
//        System.out.println(data.length);
        for (int i = 0; i < rPeaks.length; i++)
        {
            String[] data = new String[column.length];
            data[0] = Integer.toString(i+1);
            if(i < allData.rrIntervals.length)
                data[1] = Double.toString(allData.rrIntervals[i]);
            else
                data[1] = "0";
            if (i < allData.amplitude.P.Size()[1])
                data[2] =  Double.toString(allData.amplitude.P.getColumnTwoValue(i));
            else
                data[2] = "0";
            if (i < allData.amplitude.P.Size()[2])
                data[3] = Double.toString(allData.amplitude.P.getColumnThreeValue(i));
            else
                data[3] = "0";
            if (i < allData.amplitude.Q.Size()[1])
                data[4] = Double.toString(allData.amplitude.Q.getColumnTwoValue(i));
            else
                data[5] = "0";
            if (i < allData.amplitude.Q.Size()[2])
                data[5] = Double.toString(allData.amplitude.Q.getColumnThreeValue(i));
            else
                data[6] = "0";
            if (i < allData.amplitude.R.Size()[1])
                data[6] = Double.toString(allData.amplitude.R.getColumnTwoValue(i));
            else
                data[6] = "0";
            if (i < allData.amplitude.R.Size()[2])
                data[7] = Double.toString(allData.amplitude.R.getColumnThreeValue(i));
            else
                data[7] = "0";
            if (i < allData.amplitude.S.Size()[1])
                data[8] = Double.toString(allData.amplitude.S.getColumnTwoValue(i));
            else
                data[8] = "0";
            if (i < allData.amplitude.S.Size()[2])
                data[9] = Double.toString(allData.amplitude.S.getColumnThreeValue(i));
            else
                data[9] = "0";
            if (i < allData.amplitude.T.Size()[1])
                data[10] = Double.toString(allData.amplitude.T.getColumnTwoValue(i));
            else
                data[10] = "0";
            if (i < allData.amplitude.T.Size()[2])
                data[11] = Double.toString(allData.amplitude.T.getColumnThreeValue(i));
            else
                data[11] = "0";
            if (i < allData.amplitude.J.Size()[1])
                data[12] = Double.toString(allData.amplitude.J.getColumnTwoValue(i));
            else
                data[12] = "0";
            if (i < allData.amplitude.J.Size()[2])
                data[13] = Double.toString(allData.amplitude.J.getColumnThreeValue(i));
            else
                data[13] = "0";
            if (i < allData.duration.P.size())
                data[14] = Double.toString(allData.duration.P.get(i));
            else
                data[14] = "0";
            if (i < allData.duration.Q.size())
                data[15] = Integer.toString(allData.duration.Q.get(i));
            else
                data[15] = "0";
            if (i < allData.duration.R.size())
                data[16] = Integer.toString(allData.duration.R.get(i));
            else
                data[16] = "0";
            if (i < allData.duration.S.size())
                data[17] = Integer.toString(allData.duration.S.get(i));
            else
                data[17] = "0";
            if (i < allData.duration.T.size())
                data[18] = Integer.toString(allData.duration.T.get(i));
            else
                data[18] = "0";
            if (i < allData.duration.PR.size())
                data[19] = Double.toString(allData.duration.PR.get(i));
            else
                data[19] = "0";
            if (i < allData.duration.QRS.size())
                data[20] = Integer.toString(allData.duration.QRS.get(i));
            else
                data[20] = "0";
            if (i < allData.duration.QT.size())
                data[21] = Integer.toString(allData.duration.QT.get(i));
            else
                data[21] = "0";
            if (i < allData.duration.QTc.size())
                data[22] = Double.toString(allData.duration.QTc.get(i));
            else
                data[22] = "0";
            if (i < allData.stElevation.size()) {
                data[23] = Double.toString(allData.stElevation.get(i));
                data[24] = Double.toString(allData.stElevation.get(i) * 10);
            }
            else {
                data[23] = "0";
                data[24] = "0";
            }
            combinedData[i] = data;
        }
//        Loader ldh = new Loader();
//        ldh.viewData(combinedData);
        return combinedData;
    }

    private void showLead2Data(ArrayList<ArrayList<Double>> summaryList){
        System.out.println("======================= Summary From Lead 2 ======================================");
        System.out.println();
        System.out.println("Heart Rate : "+summaryList.get(0).get(0) +" +- "+summaryList.get(0).get(1)+" bpm");
        System.out.println("RR Intervals : "+summaryList.get(0).get(2)+" +- "+summaryList.get(0).get(3)+" ms");
        System.out.println("P Amplitude : "+summaryList.get(1).get(0)+" +- "+summaryList.get(1).get(2)
                +" mV, "+summaryList.get(1).get(1) +" +- "+summaryList.get(1).get(3)+" mm");
        System.out.println("Q Amplitude : "+summaryList.get(2).get(0)+" +- "+summaryList.get(2).get(2)
                +" mV, "+summaryList.get(2).get(1) +" +- "+summaryList.get(2).get(3)+" mm");
        System.out.println("R Amplitude : "+summaryList.get(3).get(0)+" +- "+summaryList.get(3).get(2)
                +" mV, "+summaryList.get(3).get(1) +" +- "+summaryList.get(3).get(3)+" mm");
        System.out.println("S Amplitude : "+summaryList.get(4).get(0)+" +- "+summaryList.get(4).get(2)
                +" mV, "+summaryList.get(4).get(1) +" +- "+summaryList.get(4).get(3)+" mm");
        System.out.println("T Amplitude : "+summaryList.get(5).get(0)+" +- "+summaryList.get(5).get(2)
                +" mV, "+summaryList.get(5).get(1) +" +- "+summaryList.get(5).get(3)+" mm");
        System.out.println("J Amplitude : "+summaryList.get(6).get(0)+" +- "+summaryList.get(6).get(2)
                +" mV, "+summaryList.get(6).get(1) +" +- "+summaryList.get(6).get(3)+" mm");

        System.out.println("P Duration : "+summaryList.get(7).get(0) +" +- "+ summaryList.get(7).get(1) + " ms");
        System.out.println("Q Duration : "+summaryList.get(7).get(2) +" +- "+ summaryList.get(7).get(3) + " ms");
        System.out.println("R Duration : "+summaryList.get(7).get(4) +" +- "+ summaryList.get(7).get(5) + " ms");
        System.out.println("S Duration : "+summaryList.get(7).get(6) +" +- "+ summaryList.get(7).get(7) + " ms");
        System.out.println("T Duration : "+summaryList.get(7).get(8) +" +- "+ summaryList.get(7).get(9) + " ms");
        System.out.println("PR Duration : "+summaryList.get(7).get(10) +" +- "+ summaryList.get(7).get(11) + " ms");
        System.out.println("QRS Duration : "+summaryList.get(7).get(12) +" +- "+ summaryList.get(7).get(13) + " ms");
        System.out.println("QT Duration : "+summaryList.get(7).get(14) +" +- "+ summaryList.get(7).get(15) + " mm");
        System.out.println("QTc Interval : "+summaryList.get(7).get(16) +" +- "+ summaryList.get(7).get(17) + " ms");

        System.out.println("STElevation : "+summaryList.get(0).get(4) +" +- "+ summaryList.get(0).get(5) + " mV, "
                +summaryList.get(0).get(4) * 10 +" +- "+summaryList.get(0).get(5) * 10 );


    }

}
