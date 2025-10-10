package com.stemi;

import com.stemi.dataClasses.*;
import com.stemi.libs.Utility;

import java.util.*;
import java.util.stream.IntStream;

public class ArrhythmiaDetection {

    private Map< String, ArrayList<String>> allLeadArrhythmiaSummary = new HashMap< String, ArrayList<String>>();
    private final Utility ut = new Utility();
    ArrhythmiaValues arrhythmiaValues = new ArrhythmiaValues();

    public String detectArrhythmia(CardiacStruct cardiacStruct, double[][] ecgData, double fs, HashMap<String, Double> map) {
        LoaderHelper ldh = new LoaderHelper();
        System.out.println("======================================== Starting Arrhythmia =====================");
        for (int i = 0; i < 9; i++) {
//        for (int i = 7; i < 8; i++) {
            ArrayList<String> arrythmias = new ArrayList<String>();
            CardiacData data = cardiacStruct.getLeadWiseCardiacStructLeadRangeFrom0To8(i);
            if (!data.getHeatRate().isEmpty()) {
                System.out.println("Running detectArrhythmia for Lead_" + i);
                //            ldh.viewData(data.getRrIntervals().stream().mapToInt(Double::intValue).toArray());
                //      CASE 1: Bradycardia / Tachycardia Based on Mean Heart Rate
                arrhythmiaValues.addLeadSingleValue(i);
                double meanHr = ut.mean(data.getHeatRate().stream().mapToDouble(Double::doubleValue).toArray());
                if (meanHr < 60) {
                    arrhythmiaValues.addHeartRateSingleValue("Bradycardia");
                    arrythmias.add("Bradycardia");          // index 0 for Bradycardia / Tachycardia
                } else if (meanHr > 100) {
                    arrhythmiaValues.addHeartRateSingleValue("Tachycardia");
                    arrythmias.add("Tachycardia");          // index 0 for Bradycardia / Tachycardia
                } else {
                    arrhythmiaValues.addHeartRateSingleValue("Normal");
                    arrythmias.add("Normal");          // index 0 for Bradycardia / Tachycardia
                }
                // CASE 2: Irregular Heart Rate Detection (Potential Atrial Fibrillation)
                double[] rrIntervals = data.getRrIntervals().stream().mapToDouble(Double::doubleValue).toArray();
                if (ut.variance(rrIntervals) > 500)
                    arrythmias.add("Possible Atrial Fibrillation");     // index 1 for Potential Atrial Fibrillation
    //            for (int j = 0; j < rrIntervals.length; j++) {
    //                if (rrIntervals[j] > 500) {
    //                    arrythmias.add("Possible Atrial Fibrillation");
    //                    break;
    //                }
    //            }

                //  CASE 3: Premature Ventricular Contractions (VPCs)
                double meanRR = ut.mean(rrIntervals);
                double pvcThreshold = 0.6 * meanRR;
                ArrayList<Integer> pvcIndices = ut.findIndexWhenArrayValuesAreLessThanThreshold(rrIntervals, pvcThreshold);
                if (!pvcIndices.isEmpty()) {
                    System.out.println("PVCs Detected as below : ");
                    arrhythmiaValues.addPVCSingleValue(pvcIndices.size());
                    arrythmias.add(" PVCs Detected: " + pvcIndices.size());
    //                for (int j = 0; j < pvcIndices.size(); j++) {
    //                    System.out.println(pvcIndices.get(j));
    //                }
                } else
                    arrhythmiaValues.addPVCSingleValue(0);
                //  CASE 4: Heart Block Detection (Prolonged PR Interval)
    //            ArrayList<Double> prLocal = data.duration.PR;
                ArrayList<Double> prLocal = data.getPrInterval();
                if (!prLocal.isEmpty() && ut.checkWhetherAnyArrayValueGreaterThanThreshold(prLocal, 200))
                    arrythmias.add("Prolonged PR Interval (Possible AV Block)");    // index 2  Prolonged PR Interval
                //  CASE 4A: PR Interval Classification
                String prCategory;
                double prValueDetected;
                if (!prLocal.isEmpty()) {
                    double medianPr = ut.median(prLocal.stream().mapToDouble(Double::doubleValue).toArray());
                    if (medianPr > 200) {
                        prCategory = "Prolonged";
                        prValueDetected = ut.findMax(prLocal);
                    } else if (medianPr < 120) {
                        prCategory = "Short";
                        prValueDetected = ut.findMin(prLocal);
                    } else {
                        prCategory = "Normal";
                        prValueDetected = ut.mean(prLocal.stream().mapToDouble(Double::doubleValue).toArray());
                    }
                    arrhythmiaValues.addPRIntervalSingleValue(prCategory);
                    arrythmias.add("PR Interval " + prValueDetected + " ms (" + prCategory + ")");      // index 3  PR Interval
                } else {

                    arrythmias.add("PR Interval: N/A");     // index 3  PR Interval
                    System.out.println("PR_Interval_ms field is missing for PR classification in Lead " + i);
                }
                //  CASE 4B: PR Interval Variability Calculation (PRVarCol)
                double prVarianceCoefficientPercentage;
                if (prLocal.size() > 1) {
                    //  Compute per‐cycle % variability
                    ArrayList<Double> cyclePrVar = new ArrayList<Double>();
                    for (int k = 1; k < prLocal.size(); k++) {
                        double prev = prLocal.get(k-1);
                        double curr = prLocal.get(k);
                        if (prev != 0)
                            cyclePrVar.add( (Math.round((Math.abs(curr - prev) / prev) * 100 ) * 1000 ) / 1000.0);

                    }

                    double meanPR, stdPR, localPrVar, globalPrVar;
    //                meanPR = ut.mean(prLocal.stream().mapToDouble(Double::doubleValue).toArray());
    //                stdPR = ut.calculateStd(prLocal);
    //                prVarianceCoefficientPercentage = (stdPR / meanPR) * 100;
    //                prVarianceCoefficientPercentage = (double) Math.round(prVarianceCoefficientPercentage * 100) / 100;
                    localPrVar = ut.findMax(cyclePrVar);
                    meanPR = ut.findMean(cyclePrVar);
                    if (meanPR > 0)
                        globalPrVar = Math.round((localPrVar / meanPR) * 100 * 1000 ) / 1000.0;
                    else
                        globalPrVar = 0;
                    prVarianceCoefficientPercentage = localPrVar;
                } else {
                    prVarianceCoefficientPercentage = 0;
                }
                // Create a summary string for display.
                System.out.println("PR Interval Variability: " + prVarianceCoefficientPercentage);
                arrhythmiaValues.addPRVarSingleValue(prVarianceCoefficientPercentage);
                arrhythmiaValues.addMultiPIntraRateSingleValue(prVarianceCoefficientPercentage);
                arrythmias.add("PR Interval Variability: " + prVarianceCoefficientPercentage + "% ");     // index 4  PR Interval Variability
                //  CASE 5: Ventricular Tachycardia (VT) Detection: High Heart Rate and Wide QRS
                ArrayList<Integer> qrsLocal = data.getQrsDuration();
                double meanQrs = ut.mean(qrsLocal.stream().mapToInt(Integer::intValue).toArray());
                if (meanHr >= 120 && meanQrs >= 120)
                    arrythmias.add("Ventricular Tachycardia");      // index 5  Ventricular Tachycardia

                //  CASE 6: QRS Morphology Classification and Variability
                if (!qrsLocal.isEmpty()) {
                    //  Classify each cycle using the helper function.
                    int narrow = 0, normal = 0, wide = 0;
                    for (int x : qrsLocal) {
                        if (x >= 120)
                            wide++;
                        else if (x <= 72)
                            narrow++;
                        else
                            normal++;
                    }
                    String dominantQrs = "NA";
                    double globalVariabilityPercent, localVariabilityPercent;
    //                if (wide >= narrow && wide >= normal)
    //                    dominantQrs = "Wide";
    //                else if (narrow >= wide && narrow >= normal)
    //                    dominantQrs = "Narrow";
    //                else if (normal >= wide && normal >= narrow)
    //                    dominantQrs = "Normal";
                    if (wide >= 1)
                        dominantQrs = "Wide";
                    else if (narrow >= 1)
                        dominantQrs = "Narrow";
                    else
                        dominantQrs = "Normal";
                    ArrayList<Double> cycleQRSVariabilityPercent = new ArrayList<>();
                    for (int j = 1; j < qrsLocal.size(); j++) {
                        double prev = qrsLocal.get(j-1);
                        double curr = qrsLocal.get(j);
                        if (prev != 0)
                            cycleQRSVariabilityPercent.add( (double) Math.round( ( (Math.abs(curr - prev)) / prev ) * 100 * 100 ) /100 );
                        cycleQRSVariabilityPercent.add(0.0);
                    }


                    //  Compute global variability.
                    double globalQrsMean = meanQrs;
                    double globalQrsStd = ut.calculateStdInt(qrsLocal);
                    globalVariabilityPercent = (globalQrsStd / globalQrsMean) * 100;
                    globalVariabilityPercent = (double) Math.round(globalVariabilityPercent * 100) / 100;
                    //  Compute local variability (set to 0 if not computable).

                    if (qrsLocal.size() > 1) {
    //                    int[] absoluteDiff = ut.differentaitionAbsolute(qrsLocal.stream().mapToInt(Integer::intValue).toArray());
    //                    double[] absoluteDiffNewQrs = new double[absoluteDiff.length];
    //                    int len = absoluteDiff.length;
    //                    List<Integer> newQrs = qrsLocal.stream().map(x -> x * 100).toList();
    //                    // last value in newQrs will be left because absoluteDiff array will have length 1 less than qrsLocal
    //
    //                    for (int j = 0; j < absoluteDiff.length; j++) {
    //                        absoluteDiffNewQrs[j] = (double) absoluteDiff[j] / newQrs.get(j);
    //                    }
    //                    localVariabilityPercent = ut.mean(absoluteDiffNewQrs) ;
    //                    localVariabilityPercent = calculateLocalVariabilityPercent(qrsLocal.stream().mapToDouble(Integer::doubleValue).toArray());
                        localVariabilityPercent = ut.findMax(cycleQRSVariabilityPercent);
                    } else
                        localVariabilityPercent = 0;
                    arrhythmiaValues.addQRSMorphologySingleValue(dominantQrs);
                    arrhythmiaValues.addGlobalVariabilitySingleValue(globalVariabilityPercent);
                    arrhythmiaValues.addLocalVariabilitySingleValue(localVariabilityPercent);
                    arrythmias.add("QRS Morphology: " + dominantQrs + " (Wide = " + wide + ", Narrow = " + narrow + ", Normal = " + normal + ");" +
                            " Global Variability = " + globalVariabilityPercent + "%, Avg Local Variability =" + localVariabilityPercent);
                    //  index 6 QRS Morphology and other related factors
                } else {
                    arrythmias.add("Lead_" + i + ", No valid QRS_Duration_ms values found for QRS classification.");
                    //  index 6 QRS Morphology and other related factors
                }

                //   CASE 7: Atrial Rate Calculation (Inter P-P Interval)
                if (!data.getPPeakIndex().isEmpty()) {
                    if (data.getPPeakIndex().size() > 1) {
                        int[] pPeaks = data.getPPeakIndex().stream().mapToInt(Integer::intValue).toArray();
    //                        double[] ppIntervals = Arrays.stream(Arrays.stream(ut.differentaition(pPeaks)).map(x -> x / 500).toArray()).mapToDouble(e->e).toArray();
                        double[] ppIntervals = Arrays.stream(ut.differentaition(pPeaks)).mapToDouble(e -> (double) e / 500).toArray();
    //                    ldh.viewData(ppIntervals);
                        double meanPP = ut.mean(ppIntervals);
                        double artialRate = (double) Math.round((60 / meanPP) * 100) / 100;
                        double artialStd = ut.calculateSD(ppIntervals);
                        double artialVariability = (artialStd / meanPP) * 100;
                        arrythmias.add("Atrial Rate: " + Math.round(artialRate * 1000) / 1000 + " bpm (Variability: " + artialVariability + ")");
                        arrhythmiaValues.addInterAtrialRateSingleValue(artialRate);
                        // index 7  Atrial Rate
                    } else {
                        arrhythmiaValues.addInterAtrialRateSingleValue(0.0);
                        System.out.println("Lead_" + i + " : Not enough P wave peaks for atrial rate calculation.");
                    }
                } else {
                    arrhythmiaValues.addInterAtrialRateSingleValue(0.0);
                    System.out.println("Lead_" + i + " : P_Peak_idx field is missing for atrial rate calculation.");
                }

                //  CASE 8: P Wave Presence/Absence Analysis
                if (!data.getPAmplitudeMv().isEmpty()) {
                    double[] pAmplitudes = data.getPAmplitudeMv().stream().
                            mapToDouble(Double::doubleValue).toArray();
                    String localPresence = classifyPWavePresence(pAmplitudes);
                    if (localPresence.equalsIgnoreCase("Mostly Absent"))
                        localPresence = "Absent";
                    double globalPMean = ut.mean(pAmplitudes);
                    String globalPPresence = "Present";
                    if (globalPMean < 0.03 )
                        globalPPresence = "Absent";
                    arrhythmiaValues.addPWavePresenceSingleValue(globalPPresence);
                    arrythmias.add("P Wave: " + globalPPresence);       // index 8 P Wave Presence ?
                    //  CASE 9: Sinus vs Non-Sinus P Wave Detection
                    String globalPWaveType = "Unknown";
                    if (globalPMean > 0.05)
                        globalPWaveType = "Sinus";
                    else
                        globalPWaveType = "Non-Sinus";
                    arrhythmiaValues.addPWaveTypeSingleValueSingleValue(globalPWaveType);
                    arrhythmiaValues.addMultiPWaveTypeSingleValue(globalPWaveType);
                    arrythmias.add("P Wave Type: " + globalPWaveType);      // index 9 P Wave Type
                } else {
                    System.out.println("P_Amplitude_mV field is missing in cardiac_table for Lead_" + i);
                    System.out.println("Lead_" + i + " : P_Amplitude_mV field is missing for P wave type detection.");
                }
                //  CASE 10: Multiple P-Wave Classification (Fibrillatory, Flutter, or Dissociated)
                if (!data.getPStartIndex().isEmpty() && !data.getTStopIndex().isEmpty()) {
                    double[] pAmplitudesSecondListArray = data.getPAmplitudeMv().stream().
                            mapToDouble(Double::doubleValue).toArray();
    //                System.out.println("pAmplitudesSecondListArray");
    //                ldh.viewData(pAmplitudesSecondListArray);
                    double globalPMean = ut.mean(pAmplitudesSecondListArray);
                    String globalPWaveType = "Unknown";
                    if (globalPMean <= 0)
                        globalPWaveType = "Non-Sinus";
                    else
                        globalPWaveType = "Sinus";

                    if (globalPWaveType.equalsIgnoreCase("Sinus")) {
                        int numCycles = rrIntervals.length;
                        int[] extraPCounts = new int[numCycles];
                        double[] intraRates = new double[numCycles];
                        boolean[] validCycles = new boolean[rrIntervals.length];
                        double[] cvPpArray = new double[numCycles];
                        for (int j = 1; j < numCycles; j++) {
                            int prevTEnd = data.getTStopIndex().get(j - 1);
                            int currPStart = data.getPStartIndex().get(j);
                            if (currPStart <= prevTEnd)
                                continue;
                            validCycles[j] = true;
                            double bufferMs = 30;      //  how many milliseconds to extend
                            int bufferSamples = (int) (Math.round((bufferMs / 1000) * fs));
                            int leftIdx = Math.max(0, prevTEnd - bufferSamples);
                            int rightIdx = Math.min(ecgData[i].length, (currPStart + bufferSamples));
                            double[] segment = ut.fillDataIntoArray(leftIdx, rightIdx, ecgData[i]);
                            if (segment.length < 2)
                                continue;
                            // Smooth with a small moving average
                            int windowSize = 20;
                            double[] windowArray = new double[windowSize];
                            windowArray = Arrays.stream(windowArray).map(e -> (double) 1 / windowSize).toArray();
                            Filters filters = new Filters();
                            double[] smoothedSegment = filters.myFIRFilter(windowArray, new double[]{1}, segment);
                            ArrayList<Integer> candidateIdxs = new ArrayList<Integer>();
                            ArrayList<Double> candidateAmps = new ArrayList<Double>();
                            double segmentMean = ut.mean(smoothedSegment);
                            double segmentStd = ut.calculateSD(smoothedSegment);
                            double prominenceThreshold = 0.05 * segmentStd;
                            for (int k = 1; k < smoothedSegment.length - 1; k++) {
                                if (smoothedSegment[k] > smoothedSegment[k - 1] && smoothedSegment[k] > smoothedSegment[k + 1]
                                        && (smoothedSegment[k] - segmentMean) > prominenceThreshold) {
                                    candidateIdxs.add(k);
                                    candidateAmps.add(segment[k]);
                                }
                            }
                            if (candidateIdxs.isEmpty())
                                continue;
                            List<Integer> candidateIdxsNew = candidateIdxs.stream().map(e -> e + prevTEnd).toList();
                            List<Double> candidateAmpsMv = candidateAmps.stream().map(e -> e * ((double) 1 / 6250)).toList();
                            double pMainAmp = pAmplitudesSecondListArray[j];
                            double tol = 0.10;
                            // similar_candidates and extraP, extraP_counts not defined
                            ArrayList<Integer> similar_candidates = new ArrayList<Integer>();
                            for (int k = 0; k < candidateIdxsNew.size(); k++) {
    //                            candidate_amps_mV <= (1/tol) * main_P_amp
                                if (candidateAmpsMv.get(k) >= tol * pMainAmp && candidateAmpsMv.get(k) <= (1 / tol) * pMainAmp)
                                    similar_candidates.add(candidateIdxsNew.get(k));
                            }

                            int extraP = Math.max(0, similar_candidates.size() - 1);
                            extraPCounts[j] = extraP;
    //                        Compute intra-atrial rate if we have at least two similar candidates
                            if (similar_candidates.size() > 1) {
                                // Code needs to be updated here......
                                // Case 10 : Issue
                                int[] cyclePpIntervals = ut.differentaition(similar_candidates.stream().mapToInt(Integer::intValue).toArray());
    //                            int[] cyclePpIntervals = ut.differentaition(similar_candidates.stream().map(e -> e/500).mapToInt(Integer::intValue).toArray());
                                double[] cyclePpIntervalsDouble = IntStream.of(cyclePpIntervals).mapToDouble(e -> e / (double) fs).toArray();
                                double intraRateCycle = 60 / ut.mean(cyclePpIntervalsDouble);
    //                                cvPpArray[j] = (ut.calculateSD(cyclePpIntervals) / ut.mean(cyclePpIntervalsDouble)) * 100;
                                cvPpArray[j] = (ut.calculateSD(cyclePpIntervals) / ut.mean(cyclePpIntervalsDouble)) * 100;
                                if (intraRateCycle <= 1000)
                                    intraRates[j] = intraRateCycle;
                                else {
                                    intraRates[j] = 0;
                                    System.out.println("Warning : Lead_" + i + " : Unrealistic intra atrial rate " + intraRateCycle + " bpm) replaced with 0.");
                                }
                            } else {
                                intraRates[j] = 0;
                                cvPpArray[j] = 0;
                            }
                        }
                        int[] validCyclesNew = new int[validCycles.length];
                        for (int j = 0; j < validCycles.length; j++) {
                            if (validCycles[j] && (extraPCounts[j] > 0))
                                validCyclesNew[j] = 1;
    //                        System.out.println(validCyclesNew[j]);
                        }
                        if (Arrays.stream(validCyclesNew).sum() > 0) {
                            double percentageExtra = ((double) Arrays.stream(validCyclesNew).sum() / numCycles) * 100;
                            double[] extraPCountsValidCycleNew = new double[extraPCounts.length];
                            ArrayList<Double> intraRatesValidCyclesList = new ArrayList<Double>();
                            double[] intraRatesValidCycles = new double[extraPCounts.length];
                            for (int j = 0; j < extraPCounts.length; j++) {
                                extraPCountsValidCycleNew[j] = extraPCounts[validCyclesNew[j]];
                                if (validCyclesNew[j] == 1) {
                                    int zzTemp = validCyclesNew[j] - 1;
                                    double zz = intraRates[validCyclesNew[j] - 1];
    //                                intraRatesValidCyclesList.add(intraRates[validCyclesNew[j] - 1]);
                                    intraRatesValidCyclesList.add(intraRates[j]);
    //                                    intraRatesValidCycles[j] = intraRates[validCyclesNew[j]];
                                }
                            }
                            double avgExtra = ut.mean(extraPCountsValidCycleNew);
    //                            double globalIntraRate = ut.mean(intraRatesValidCycles);
                            double globalIntraRate = ut.findMean(intraRatesValidCyclesList);
                            double cvIntra = 0;
                            if (globalIntraRate > 0)
                                cvIntra = (ut.calculateSD(intraRatesValidCycles) / globalIntraRate) * 100;

                            //  Now compute fibrillatory and flutter ratios *across validCycles
                            int[] fibArr = new int[validCyclesNew.length];
                            int[] flutterArr = new int[validCyclesNew.length];
                            double[] cvPpArrayValidCyclesNew = new double[validCyclesNew.length];
                            for (int j = 0; j < validCyclesNew.length; j++) {
                                cvPpArrayValidCyclesNew[j] = cvPpArray[validCyclesNew[j]];
                            }
                            for (int j = 0; j < validCyclesNew.length; j++) {
                                if (intraRatesValidCycles[j] > 350 && cvPpArrayValidCyclesNew[j] > 20)
                                    fibArr[j] = 1;
                                if (intraRatesValidCycles[j] >= 180 && intraRatesValidCycles[j] <= 350 && cvPpArrayValidCyclesNew[j] < 20)
                                    flutterArr[j] = 1;
                            }
                            double fibRatio = (double) Arrays.stream(fibArr).sum() / Arrays.stream(validCyclesNew).sum();
                            double flutterRatio = (double) Arrays.stream(flutterArr).sum() / Arrays.stream(validCyclesNew).sum();
                            double disociationRatio = Arrays.stream(extraPCountsValidCycleNew).filter(e -> e >= 1).sum()
                                    / Arrays.stream(validCyclesNew).sum();
                            String waveType;
                            if (fibRatio > 0.3)
                                waveType = "Fibrillatory ( " + fibRatio * 100 + " cycles )";

                            else if (flutterRatio > 0.1 && fibRatio < 0.3)
                                waveType = "Flutter ( " + flutterRatio * 100 + " cycles )";
                            else if (disociationRatio >= 0.8 && flutterRatio < 0.1 && fibRatio < 0.3)
                                waveType = "Dissociated ( " + disociationRatio * 100 + " cycles )";
                            else
                                waveType = "Normal Multiple P Waves";
                            arrhythmiaValues.addMultiPPercentageSingleValue(percentageExtra);
                            arrhythmiaValues.addMultiPAvgExtraSingleValue(avgExtra);
                            arrhythmiaValues.addMultiPIntraRateSingleValue(globalIntraRate);
                            arrhythmiaValues.addMultiPIntraVarSingleValue(cvIntra);
                            arrhythmiaValues.addIntraAtrialRateSingleValue(globalIntraRate);
    //                            arrhythmiaValues.addMultiPWaveTypeSingleValue(globalPWaveType);
                            //    Build the summary string exactly as before
                            arrythmias.add("Multiple P Waves: " + percentageExtra + "% cycles with extra P waves; Avg extra" +
                                    " P waves = " + avgExtra + " Intra Atrial Rate: " + globalIntraRate + " bpm " +
                                    "(Variability: " + cvIntra + ") Wave Type: " + waveType);
                            // index 10 Multiple P Waves, percentageExtra, avgExtra, globalIntraRate, Variability,
                        } else {
                            arrhythmiaValues.addMultiPPercentageSingleValue(0.0);
                            arrhythmiaValues.addMultiPAvgExtraSingleValue(0.0);
                            arrhythmiaValues.addMultiPIntraRateSingleValue(0.0);
                            arrhythmiaValues.addMultiPIntraVarSingleValue(0.0);
                            arrhythmiaValues.addIntraAtrialRateSingleValue(0.0);
    //                            arrhythmiaValues.addMultiPWaveTypeSingleValue(globalPWaveType);
                            System.out.println("Lead_" + i + " :No valid multi P-wave cycles detected.");
                        }
                    } else {
                        arrhythmiaValues.addMultiPPercentageSingleValue(0.0);
                        arrhythmiaValues.addMultiPAvgExtraSingleValue(0.0);
                        arrhythmiaValues.addMultiPIntraRateSingleValue(0.0);
                        arrhythmiaValues.addMultiPIntraVarSingleValue(0.0);
                        arrhythmiaValues.addIntraAtrialRateSingleValue(0.0);
                        System.out.println("Lead_" + i + " : P wave is non-sinus; skipping multiple P wave detection.");
                    }
                } else
                    System.out.println("Lead_" + i + " : Required fields T_Stop_idx or P_Start_idx are missing.");

                //CASE 11: Slurred R Wave and R-Wave Symmetry Analysis (WPW/Preexcitation)
                if (!data.getQPeakIndex().isEmpty() && !data.getRPeakIndex().isEmpty() && !data.getSPeakIndex().isEmpty()) {
                    double[] rRise = new double[data.getRPeakIndex().size()];
                    double[] rFall = new double[rRise.length];
                    double[] symmetryRatio = new double[rRise.length];
                    for (int j = 0; j < rRise.length; j++) {
                        rRise[j] = ((data.getRPeakIndex().get(j) - data.getQPeakIndex().get(j)) / fs) * 1000;
                        rFall[j] = ((data.getSPeakIndex().get(j) - data.getRPeakIndex().get(j)) / fs) * 1000;
                        symmetryRatio[j] = rRise[j] / rFall[j];
                    }
                    double avgSymmetryRatio = ut.mean(symmetryRatio);
                    String rSymmetryStatus;
                    if (Math.abs(avgSymmetryRatio) > 0.6)
                        rSymmetryStatus = "Abnormal R-wave symmetry (Possible WPW/Preexcitation)";
                    else
                        rSymmetryStatus = "Normal R-wave symmetry";
                    int[] rDuration = data.getRDuration().stream().mapToInt(Integer::intValue).toArray();
                    double avgRDuration = ut.mean(rDuration);
                    String rSlurStatus;
                    if (avgRDuration > 60)
                        rSlurStatus = "Slurred R wave detected";
                    else
                        rSlurStatus = "No slurred R wave";
                    arrhythmiaValues.addRWaveSymmetrySingleValue(rSymmetryStatus);
                    arrhythmiaValues.addRWaveDurationSingleValue(avgRDuration);
                    arrythmias.add("R-wave Symmetry: " + rSymmetryStatus + " (avg ratio = " + avgSymmetryRatio + "),  R-wave Duration: " + avgRDuration + " ms " + rSlurStatus);
                    // index 11 R-wave Symmetry, avg ratio, R-wave Duration
                } else
                    System.out.println("Lead_" + i + " : Required fields Q_Peak_idx, R_Peak_idx, or S_Peak_idx missing for R-wave symmetry analysis.");

                //  CASE 12: Escape Rhythm Detection (Integrated)
                if (rrIntervals.length >= 1) {
                    double[] validRRIntervals = rrIntervals;
                    if (rrIntervals.length >= 3) {
                        double rrVariability = (ut.calculateSD(validRRIntervals) / meanRR) * 100;
                        if (rrVariability < 10) {
                            arrhythmiaValues.addEscapeRhythmSingleValue("Detected");
                            arrythmias.add("Escape Rhythm Detected (Regular RR Intervals)");    // index 12 Escape Rhythm Detection
                        } else {
                            arrhythmiaValues.addEscapeRhythmSingleValue("Not Detected");
                            arrythmias.add("No Escape Rhythm (Irregular RR Intervals)");    // index 12 Escape Rhythm Detection
                        }
                        // CASE 13: Pause Detection (Integrated with CASE 12 in Java)
                        String str = "No Pause Detected";
                        double pauseThreshold = 2 * meanRR;
                        for (int j = 0; j < validRRIntervals.length; j++) {
                            if (validRRIntervals[j] > pauseThreshold) {
                                str = "Pause Detected (Prolonged RR Interval)";
                                break;
                            } else
                                str = "No Pause Detected";
                        }
                        arrhythmiaValues.addPauseDetectedSingleValue(str);
                        arrythmias.add(str);        // index 13 Pause detection
                    } else {
                        arrhythmiaValues.addEscapeRhythmSingleValue("Not Detected");
                        arrhythmiaValues.addPauseDetectedSingleValue("No Pause Detected");
                        System.out.println("Insufficient RR interval data for escape rhythm detection.");
                        System.out.println("Insufficient RR interval data for pause detection.");
                    }
                } else
                    System.out.println("RR_Interval_ms missing in cardiac_table. Escape rhythm detection skipped, Pause detection skipped.");
                // CASE 14: RR Variability Percentage Calculation
                double[] rrValues = data.getRrIntervals().stream().map(e -> e / (double) 1000).mapToDouble(Double::doubleValue).toArray();
                double cvRR, localRR;
                if (rrValues.length > 1) {
                    double[] rrValuesDiff = ut.differentaitionAbsolute(rrValues);
                    double rrMean = ut.mean(rrValues);
                    double[] localRrPercentArrayWithRrMean = Arrays.stream(rrValuesDiff).map(e ->
                            (double) Math.round((e / rrMean) * 100 * 100) / 100 ).toArray();
                    //  now pick the maximum variability for your scalar localRR
                    localRR = ut.findMax(localRrPercentArrayWithRrMean);
                    //  global variability: average of those local % changes
                    cvRR = ut.mean(localRrPercentArrayWithRrMean);
                } else {
                    cvRR = -1;
                    localRR = -1;
                }
                arrhythmiaValues.addGlobalRRVariabilitySingleValue(cvRR);
                arrhythmiaValues.addLocalRRVariabilitySingleValue(localRR);
                allLeadArrhythmiaSummary.put("Lead_" + i, arrythmias);
        } else {
                System.out.println("Skipping detectArrhythmia for Lead_" + i + " because heartRate list is empty for" +
                        " the lead");
//                arrhythmiaValues.addHeartRateSingleValue("NATD");
//                arrhythmiaValues.addPVCSingleValue(-1);
//                arrhythmiaValues.addPRIntervalSingleValue("NATD");
//                arrhythmiaValues.addPRVarSingleValue(-1.0);
//                arrhythmiaValues.addMultiPIntraRateSingleValue(-1.0);
//                arrhythmiaValues.addQRSMorphologySingleValue("NATD");
//                arrhythmiaValues.addGlobalVariabilitySingleValue(-1.0);
//                arrhythmiaValues.addLocalVariabilitySingleValue(-1.0);
//                arrhythmiaValues.addInterAtrialRateSingleValue(-1.0);
//                arrhythmiaValues.addPWavePresenceSingleValue("NATD");
//                arrhythmiaValues.addPWaveTypeSingleValueSingleValue("NATD");
//                arrhythmiaValues.addMultiPWaveTypeSingleValue("NATD");
//                arrhythmiaValues.addMultiPPercentageSingleValue(-1.0);
//                arrhythmiaValues.addMultiPAvgExtraSingleValue(-1.0);
//                arrhythmiaValues.addMultiPIntraRateSingleValue(-1.0);
//                arrhythmiaValues.addMultiPIntraVarSingleValue(-1.0);
//                arrhythmiaValues.addIntraAtrialRateSingleValue(-1.0);
//                arrhythmiaValues.addRWaveSymmetrySingleValue("NATD");
//                arrhythmiaValues.addRWaveDurationSingleValue(-1.0);
//                arrhythmiaValues.addEscapeRhythmSingleValue("NATD");
//                arrhythmiaValues.addPauseDetectedSingleValue("NATD");
//                arrhythmiaValues.addGlobalRRVariabilitySingleValue(-1.0);
//                arrhythmiaValues.addLocalRRVariabilitySingleValue(-1.0);
            }
        }
//        System.out.println(" ttttttttttt    "+arrhythmiaValues.get.size());
//        arrhythmiaValues.displayDouble(arrhythmiaValues.getIntraAtrialRateCol());
//        System.out.println(" ttttttttttt    "+arrhythmiaValues.getInterAtrialRateCol().size());
//        arrhythmiaValues.displayDouble(arrhythmiaValues.getrWaveDurationCol());
//        arrhythmiaValues.displayString(arrhythmiaValues.getEscapeRhythmCol());
//        arrhythmiaValues.displayInteger(arrhythmiaValues.getPvcsCol());
        return arrythmiaClassifier(allLeadArrhythmiaSummary, cardiacStruct , arrhythmiaValues, map);
    }


    public String arrythmiaClassifier(Map<String, ArrayList<String>> allLeadArrhythmiaSummary, CardiacStruct cardiacStruct
            , ArrhythmiaValues values, HashMap<String, Double> map) {
        ClassificationResults classificationResults = new ClassificationResults();
        for (int i = 0; i < values.getLeadCol().size(); i++) {
//        for (int i = 7; i < 8; i++) {
            //  Compute average heart rate from the cardiac data (using the Heart_Rate_bpm column)
//            int i = values.getLeadSingleValue(k);
            CardiacData data = cardiacStruct.getLeadWiseCardiacStructLeadRangeFrom0To8(i);
            classificationResults.addLead(values.getLeadSingleValue(i));
//            if (data != null) {
//            if (!values.getHeartRateSingleValue(i).equalsIgnoreCase("NATD") ) {
//                double averageHeartRate = -1;

            if (!data.getHeatRate().isEmpty())
            {
               double averageHeartRate = ut.findMean(data.getHeatRate());

//                else {
//                    System.out.println("HeartRate list is empty in arrythmiaClassifier() for Lead_" + values.getLeadSingleValue(i));
//                }
//            String pPresence = allLeadArrhythmiaSummary.get("Lead_"+i).get(8);
//            String pType = allLeadArrhythmiaSummary.get("Lead_"+i).get(9);
//            String multiPClass = allLeadArrhythmiaSummary.get("Lead_"+i).get(8);
                double localRrVariability = values.getLocalRRVariabilitySingleValue(i);
                double globalRrVariability = values.getGlobalRRVariabilitySingleValue(i);
                boolean isNarrow = values.getQRSMorphologySingleValue(i).equalsIgnoreCase("Narrow");
                boolean isWide = values.getQRSMorphologySingleValue(i).equalsIgnoreCase("Wide");
                boolean isNormal = values.getQRSMorphologySingleValue(i).equalsIgnoreCase("Normal");
                // Compute RR statistics from cardiac data (if available).
                double avgRR = -1, stdRR, cvRR;
                if (!data.getRrIntervals().isEmpty()) {
                    double[] rrValues = data.getRrIntervals().stream().mapToDouble(Double::doubleValue).toArray();
                    rrValues = Arrays.stream(rrValues).map(e -> e / 1000).toArray();
                    avgRR = ut.mean(rrValues);
                    stdRR = ut.calculateSD(rrValues);
                    cvRR = (stdRR / avgRR) * 100;

                } else {
                    cvRR = -1;
                }
                //  -------------------- Classification Rules ---------------------------
                //  ------------------ Tachycardia Detection ----------------------------
//                System.out.println(allLeadArrhythmiaSummary.get("Lead_" + i).get(6));
                if ((averageHeartRate > 100) && (averageHeartRate < 180)) {
                    if (localRrVariability < 25 && values.getPWavePresenceSingleValue(i).
                            equalsIgnoreCase("Present"))
                        classificationResults.addTachycardia("Sinus Tachycardia");
                    else if (localRrVariability < 20)
                        classificationResults.addTachycardia("Tachycardia");
                    else
                        classificationResults.addTachycardia("Tachycardia");
                } else
                    classificationResults.addTachycardia("No Tachycardia Found");

                //  --- Bradycardia ---
                if (averageHeartRate < 59.5) {
                    if (values.getPWaveTypeSingleValue(i).equalsIgnoreCase("Sinus"))
                        classificationResults.addBradycardia("Sinus Bradycardia");
                    else
                        classificationResults.addBradycardia("Bradycardia");
                } else
                    classificationResults.addBradycardia("No Bradycardia Found");

                //  --- PAC Detection ---
                if (!values.getLocalRRVariabilityCol().isEmpty() && !values.getGlobalRRVariabilityCol().isEmpty() &&
                        localRrVariability >= 15 && globalRrVariability <= 35 && (isNarrow || isNormal) ) {
                    if (values.getPWavePresenceSingleValue(i).equalsIgnoreCase("Absent"))
                        classificationResults.addPac("Premature Atrial Complex");

                    else if (values.getPWavePresenceSingleValue(i).equalsIgnoreCase("Present") &&
                            localRrVariability >= 10 && values.getPRVarSingleValue(i) > 30)
                        classificationResults.addPac("Premature Atrial Complex");
                    else
                        classificationResults.addPac("No PAC Found");
                } else
                    classificationResults.addPac("No PAC Found");

                //  --- VPC Detection ---
                double qrsVar = values.getLocalVariabilitySingleValue(i);
//                if ( (localRrVariability > 6 && qrsVar > 10 ) || (localRrVariability > 3.5 && isWide) ||
//                        (localRrVariability > 3.5 && qrsVar > 15 && isNormal) || (isNormal && values.getPWavePresenceSingleValue(i)
//                        .equalsIgnoreCase("Absent") && qrsVar > 15) )
                if ( ( localRrVariability > 25 && qrsVar > 25 ) || (localRrVariability > 15 && isWide) || (localRrVariability > 15 && qrsVar > 25 && isNormal)
                    || ( isNormal && values.getPWavePresenceSingleValue(i).equalsIgnoreCase("Absent") && qrsVar > 25)
                )
                    classificationResults.addVpc("Ventricular Premature Complex");
                else
                    classificationResults.addVpc("No VPC Found");

                //  --- VT Detection ---
                if (
                        ( (averageHeartRate >= 120 && averageHeartRate <= 145) && (localRrVariability < 15)
                        && (isWide || isNormal) && values.getPWavePresenceSingleValue(i).
                        equalsIgnoreCase("Absent") )
                                    ||
                        ( values.getPWavePresenceSingleValue(i).
                        equalsIgnoreCase("Present") && values.getInterAtrialRateSingleValue(i) > 125)
                    )
                {
                    // In the intermediate VT range (120 to 151 bpm), the P wave can be present.
                    classificationResults.addVt("Suspect Ventricular Tachycardia");
                } else if (averageHeartRate > 120 && localRrVariability < 15 && values.
                        getQRSMorphologySingleValue(i).equalsIgnoreCase("Wide") && values.getPWavePresenceSingleValue(i).
                        equalsIgnoreCase("Absent")) {
                    //  For rates above 151 bpm, require that the P wave be absent.
                    classificationResults.addVt("Ventricular Tachycardia");
                } else
                    classificationResults.addVt("No Ventricular Tachycardia Found");

                //  --- SVT Detection ---
                if (averageHeartRate > 150 && localRrVariability < 30 && isNarrow && values.getPWavePresenceSingleValue(i).
                        equalsIgnoreCase("Absent"))
                    classificationResults.addSvt("Supraventricular Tachycardia");
                else
                    classificationResults.addSvt("No Supraventricular Tachycardia Found");

                // --- Escape Beat Detection ---
//                if (avgRR > 1.01 && avgRR < 2) {
                double baselineRR =ut.findMean(cardiacStruct.getLeadWiseCardiacStructLeadRangeFrom0To8(i).getRrIntervals()) / 1000;
                double pauseThreshold = 1.5 * baselineRR;
                if (avgRR > pauseThreshold && avgRR < 2 * pauseThreshold ) {
                    if ( values.getMultiPWaveTypeSingleValue(i).equalsIgnoreCase("Sinus") && values.
                        getPWavePresenceSingleValue(i).equalsIgnoreCase("Present") && ( values.
                            getPRIntervalSingleValue(i).equalsIgnoreCase("Normal") ) && isNarrow
                    )
                        classificationResults.addEscapeBeat("Atrial Escape Beat");
                    else if ( values.getPWavePresenceSingleValue(i).equalsIgnoreCase("Absent") && isNarrow )
                        classificationResults.addEscapeBeat("Junctional Escape Beat");
                    else
                        classificationResults.addEscapeBeat("No Escape Beat Found");
                } else
                    classificationResults.addEscapeBeat("No Escape Beat Found");

                //  --- Pause Detection ---
                if (avgRR > 2 && avgRR <= 3) {
                    if (values.getMultiPWaveTypeSingleValue(i).equalsIgnoreCase("Sinus") && values.
                            getPWavePresenceSingleValue(i).equalsIgnoreCase("Present"))
                        classificationResults.addPause("Sinus Pause");
                    else if (values.getMultiPWaveTypeSingleValue(i).equalsIgnoreCase("Sinus") && values.
                            getPWavePresenceSingleValue(i).equalsIgnoreCase("Absent"))
                        classificationResults.addPause("Junctional Pause");
                } else
                    classificationResults.addPause("No Pause Found");

                //  --- Junctional Tachycardia Detection ---
                if (averageHeartRate > 100 && localRrVariability < 10 && isNormal && !values.getPWaveTypeSingleValue(i).equalsIgnoreCase("Sinus")
                        && values.getPWavePresenceSingleValue(i).equalsIgnoreCase("Present"))
                    classificationResults.addJunctionalTachycardia("Junctional Tachycardia");
                else
                    classificationResults.addJunctionalTachycardia("No Junctional Tachycardia Found");

                //  --- Premature Junctional Rhythm Detection ---
                if (localRrVariability > 25 && avgRR > 2 && avgRR < 11 && isNarrow && values.getPWaveTypeSingleValue(i).equalsIgnoreCase
                        ("Non-Sinus") && values.getPWavePresenceSingleValue(i).equalsIgnoreCase("Absent"))
                    classificationResults.addPrematureJunctionalRhythm("Premature Junctional Rhythm");
                else
                    classificationResults.addPrematureJunctionalRhythm("No Premature Junctional Rhythm Found");

                //  First Degree AV Block Detection (Using PR Interval Classification and PR Variability)
                boolean isProlonged = values.getPRIntervalSingleValue(i).toLowerCase().contains("Prolonged".toLowerCase());
                if (!cardiacStruct.getLeadWiseCardiacStructLeadRangeFrom0To8(i).getPrInterval().isEmpty())
                    if ( isProlonged && values.getPRVarSingleValue(i) < 50 && (isNarrow || isNormal) && values.
                            getPWaveTypeSingleValue(i).equalsIgnoreCase("Sinus") && values.
                            getPWavePresenceSingleValue(i).equalsIgnoreCase("Present")
                        )
                        classificationResults.addFirstDegreeAvBlock("First Degree AV Block");
                    else
                        classificationResults.addFirstDegreeAvBlock("No First Degree AV Block Found");
                else
                    classificationResults.addFirstDegreeAvBlock("PR Interval data not available.");

                //  --- Mobitz Type 1 AV Block Detection ---
                double intraAtrialRateValue = values.getIntraAtrialRateSingleValue(i);
                if ( localRrVariability > 25 && isProlonged && isNarrow && values.getPWaveTypeSingleValue(i).equalsIgnoreCase
                        ("Sinus") && (intraAtrialRateValue > 45 && intraAtrialRateValue < 100 ))
                    classificationResults.addMobitzType1AVBlock("Mobitz Type 1 AV Block");
                else
                    classificationResults.addMobitzType1AVBlock("No Mobitz Type 1 AV Block Found");

                //  --- Mobitz Type 2 AV Block Detection ---
                double interAtrialRateVal = values.getInterAtrialRateSingleValue(i);
//                double zz = values.getLocalVariabilitySingleValue(i);
                if (isProlonged && qrsVar < 57 && values.getLocalVariabilitySingleValue(i) > 19 &&
                        ( values.getPRVarSingleValue(i) < 16 && (interAtrialRateVal > 25 && interAtrialRateVal < 50) )
                )
                    classificationResults.addMobitzType2AVBlock("Mobitz Type II AV Block");
                else
                    classificationResults.addMobitzType2AVBlock("No Mobitz Type II AV Block Found");

                //   --- Pre-excitation Syndrome Detection ---
                // ------------ if Conditon need to be Updated here ------------------------
                if ( values.getPRIntervalSingleValue(i).equalsIgnoreCase("short") && (values.
                        getRWaveSymmetrySingleValue(i).toLowerCase().contains("Abnormal".toLowerCase()) ) && isWide &&
                        values.getPRVarSingleValue(i) < 16
                    )
                    classificationResults.addPreExcitationSyndrome("Pre-excitation Syndrome");
                else
                    classificationResults.addPreExcitationSyndrome("No Pre-excitation Syndrome");

                //  ----------------- Idioventricular Rhythm Detection ----------------------------
                if (averageHeartRate >= 20 && averageHeartRate <= 50 && localRrVariability <= 10 && isWide &&
                        values.getPWaveTypeSingleValue(i).equalsIgnoreCase("Non-Sinus") &&
                        values.getPWavePresenceSingleValue(i).equalsIgnoreCase("Absent"))
                    classificationResults.addIdioventricularRhythm("Idioventricular Rhythm");
                else
                    classificationResults.addIdioventricularRhythm("No Idioventricular Rhythm Found");

                //  --------- Accelerated Idioventricular Rhythm Detection ---------
                if (averageHeartRate >= 50 && averageHeartRate <= 100 && localRrVariability <= 10 && isWide &&
                        values.getPWaveTypeSingleValue(i).equalsIgnoreCase("Non-Sinus") &&
                        values.getPWavePresenceSingleValue(i).equalsIgnoreCase("Absent"))
                    classificationResults.addAcceleratedIdioventricularRhythm("Accelerated Idioventricular Rhythm");
                else
                    classificationResults.addAcceleratedIdioventricularRhythm("No Accelerated Idioventricular Rhythm Found");

                //   ---------- Junctional Rhythm Detection ----------------
                if (averageHeartRate >= 60 && averageHeartRate <= 100 && localRrVariability < 10
                        && (isNormal || isNarrow) && values.getPWaveTypeSingleValue(i).equalsIgnoreCase("non-sinus"))
                    classificationResults.addJunctionalRhythm("Junctional Rhythm");
                else
                    classificationResults.addJunctionalRhythm("No Junctional Rhythm Found");

                //  --- Atrial Flutter Detection ---
                if ( (localRrVariability >= 22 && intraAtrialRateValue >= 180 && intraAtrialRateValue < 350)
                        ||
                      ( localRrVariability <= 10 && intraAtrialRateValue >= 120 && intraAtrialRateValue < 350 )
                   )
                    classificationResults.addAtrialFlutter("Atrial Flutter");
                else
                    classificationResults.addAtrialFlutter("No Atrial Flutter Found");

                //  ---------------- Atrial Tachycardia Detection --------------
                if (averageHeartRate > 100 && interAtrialRateVal > 111 && interAtrialRateVal < 250 && isNarrow)
                    classificationResults.addAtrialTachycardia("Atrial Tachycardia");
                else
                    classificationResults.addAtrialTachycardia("No Atrial Tachycardia Found");

                //  --- Atrial Fibrillation Detection ---
                //% We now require that, in addition to the previous conditions,
                //% the local RR variability (localRRVar) and global RR variability (globalRRVar) are defined
                //% and at least 25%, because atrial fibrillation cannot be reliably detected without sufficient RR variability.
                if (averageHeartRate >= 100 && averageHeartRate < 150 && localRrVariability >= 30
                        && globalRrVariability >= 15) {
                    if (values.getPWaveTypeSingleValue(i).equalsIgnoreCase("Non-Sinus") && values.
                            getPWavePresenceSingleValue(i).equalsIgnoreCase("Absent") )
                        classificationResults.addAtrialFibrillation("Atrial Fibrillation");
                    else if (values.getPWaveTypeSingleValue(i).equalsIgnoreCase("Sinus") && values.
                            getIntraAtrialRateSingleValue(i) > 350 || values.getInterAtrialRateSingleValue(i) > 250)
                        classificationResults.addAtrialFibrillation("RVR Atrial Fibrillation");
                    else
                        classificationResults.addAtrialFibrillation("No Atrial Fibrillation Found");
                } else if (averageHeartRate < 60 && localRrVariability >= 20 && globalRrVariability >= 12) {
                    if (values.getPWaveTypeSingleValue(i).equalsIgnoreCase("Sinus") && intraAtrialRateValue > 350
                            || values.getInterAtrialRateSingleValue(i) > 250)
                        classificationResults.addAtrialFibrillation("Bradycardic Atrial Fibrillation");
                    else if (values.getPWaveTypeSingleValue(i).equalsIgnoreCase("Non-Sinus") && values.
                            getPWavePresenceSingleValue(i).equalsIgnoreCase("Absent"))
                        classificationResults.addAtrialFibrillation("Bradycardic Atrial Fibrillation");
                    else
                        classificationResults.addAtrialFibrillation("No Atrial Fibrillation Found");
                } else if (averageHeartRate >= 60 && averageHeartRate < 100 && localRrVariability >= 30 &&
                        globalRrVariability >= 15) {
                    if (values.getPWaveTypeSingleValue(i).equalsIgnoreCase("Non-Sinus") && values.getPWavePresenceSingleValue(i).
                            equalsIgnoreCase("Absent") && values.getInterAtrialRateSingleValue(i) > 250 || intraAtrialRateValue > 350)
                        classificationResults.addAtrialFibrillation("Atrial Fibrillation");
                    else
                        classificationResults.addAtrialFibrillation("No Atrial Fibrillation Found");
                } else
                    classificationResults.addAtrialFibrillation("No Atrial Fibrillation Found");
                //  --- Complete Heart Block Detection ---
                //% Criteria:
                //%   - Ventricular (QRS) rate (avgHR) is between 35 and 60 bpm.
                //%   - PR interval variability (prVar) is greater than 25%.
                //%   - Intra-atrial rate (intraAtrialRate) is greater than 75 bpm.
                if (averageHeartRate >= 15 && averageHeartRate < 60 && values.getPRVarSingleValue(i) > 90 && values.
                        getIntraAtrialRateSingleValue(i) > 50 && intraAtrialRateValue < 75)
                    classificationResults.addCompleteHeartBlock("Complete Heart Block");
                else
                    classificationResults.addCompleteHeartBlock("No Complete Heart Block Found");

                //  --- Sinus Arrhythmia Detection ---
                //% Robust detection using both the RR coefficient of variation (cvRR)
                //% and the local variability measure from the arrhyt%hmia struct (localVar).
                if (averageHeartRate >= 50 && averageHeartRate <= 100 && values.getPWaveTypeSingleValue(i).
                        equalsIgnoreCase("Sinus")) {
                    //    % Use a threshold of 15% (you can adjust this value).
                    if (globalRrVariability > 20 && localRrVariability > 35)
                        classificationResults.addSinusArrhythmia("Sinus Arrhythmia Detected");
                    else
                        classificationResults.addSinusArrhythmia("No Sinus Arrhythmia Found");

                    //  --- Sinus Rhythm Detection ---
                    if (averageHeartRate >= 60 && averageHeartRate <= 100 && values.getPWaveTypeSingleValue(i).
                            equalsIgnoreCase("Sinus") && isNormal || isNarrow || isWide)
                        classificationResults.addSinusRhythm("Sinus Rhythm Detected");
                    else
                        classificationResults.addSinusRhythm("No Sinus Rhythm Found");

                    //  --- MultiPClassification ---
                    // check below condition as value is zero
                    if (values.getMultiPWaveTypeSingleValue(i).equalsIgnoreCase("0"))
                        classificationResults.addMultiPClassification("0");
                    else
                        classificationResults.addMultiPClassification("Not Detected");
                } else {
                    classificationResults.addSinusArrhythmia("NA");
                    classificationResults.addSinusRhythm("NA");
                    classificationResults.addMultiPClassification("NA");
                }

                System.out.println("Arrhythmia classification function accessed all required data successfully.");
            } else {
                System.out.println("HeartRate list is empty in arrythmiaClassifier() for Lead_" + values.getLeadSingleValue(i));
                classificationResults.addTachycardia("NATD");
                classificationResults.addBradycardia("NATD");
                classificationResults.addPac("NATD");
                classificationResults.addVpc("NATD");
                classificationResults.addVt("NATD");
                classificationResults.addSvt("NATD");
                classificationResults.addEscapeBeat("NATD");
                classificationResults.addPause("NATD");
                classificationResults.addJunctionalTachycardia("NATD");
                classificationResults.addPrematureJunctionalRhythm("NATD");
                classificationResults.addFirstDegreeAvBlock("NATD");
                classificationResults.addMobitzType1AVBlock("NATD");
                classificationResults.addMobitzType2AVBlock("NATD");
                classificationResults.addPreExcitationSyndrome("NATD");
                classificationResults.addIdioventricularRhythm("NATD");
                classificationResults.addAcceleratedIdioventricularRhythm("NATD");
                classificationResults.addJunctionalRhythm("NATD");
                classificationResults.addAtrialFlutter("NATD");
                classificationResults.addAtrialTachycardia("NATD");
                classificationResults.addAtrialFibrillation("NATD");
                classificationResults.addCompleteHeartBlock("NATD");
                classificationResults.addSinusArrhythmia("NATD");
                classificationResults.addSinusRhythm("NATD");
                classificationResults.addMultiPClassification("NATD");

            }
        }
//        System.out.println("ccccccc  "+ classificationResults.getPac().size());
//        classificationResults.display(classificationResults.getPac());

        //  Define priority order for arrhythmias (only detected ones are considered)
        String[] priorityArray = {
                "Ventricular Tachycardia", "Suspect Ventricular Tachycardia",
                "RVR Atrial Fibrillation", "Atrial Fibrillation", "Bradycardic Atrial Fibrillation",
                "Atrial Flutter", "Sinus Pause", "Junctional Pause",
                "Complete Heart Block",
                "Mobitz Type II AV Block", "Mobitz Type 1 AV Block", "First Degree AV Block",
                "Supraventricular Tachycardia",
                "Junctional Tachycardia",
                "Premature Junctional Rhythm",
                "Pre-excitation Syndrome",
                "Atrial Tachycardia",
                "Accelerated Idioventricular Rhythm", "Idioventricular Rhythm",
                "Ventricular Premature Complex",
                "Premature Atrial Complex",
                "Atrial Escape Beat", "Junctional Escape Beat", "Junctional Rhythm",
                "Sinus Arrhythmia Detected", "Sinus Tachycardia", "Tachycardia",
                "Sinus Bradycardia", "Bradycardia",
                "Sinus Rhythm", "NATD"
//                "Sinus Rhythm", "NATD"
        };
        displayClassificationResultData(classificationResults);

        ArrayList<String> finalDecisions = arrhythmiaDecisionMaker(classificationResults, priorityArray);

        //  ----------- Rhythm type Identifier ----------------------

        //  Define threshold parameters
        double thresholdMax = 0.4;       //     Winning decision must have at least 50% of votes
        double thresholdEntropy = 1.0;      // Maximum allowed entropy
        Map<String, Object> rhythmIdentifier = rhythmSummaryReport(finalDecisions, thresholdMax, thresholdEntropy, priorityArray);
        double maxProb = ut.findMax((double[]) rhythmIdentifier.get("probs"));
        String rhythmType = (String) rhythmIdentifier.get("rhythmType");
        double entropyVal = (double) rhythmIdentifier.get("entropyVal");
        String summarySentence = "Based on the analysis, the final rhythm decision is "+rhythmType ;
        if ( !rhythmType.equalsIgnoreCase("Indeterminate"))
            summarySentence += " with a confidence of "+ (double) Math.round((maxProb * 100) * 100) / 100 +"%";
//        else {
            //  If indeterminate, explain the reason(s).
//            String reason = "";
//            if (maxProb < thresholdMax)
//                reason = "The highest candidate received only "+ (double) Math.round((maxProb * 100) * 100) / 100 + "% of the votes (threshold: "+thresholdMax;
//            if (entropyVal > thresholdEntropy)
//                reason += " The overall entropy is " + entropyVal + " bits, which exceeds the allowed threshold of "
//                        +thresholdEntropy + " bits";
//            summarySentence += " The outcome is indeterminate because "+reason;
//            summarySentence += " The outcome is Indeterminate";
//        }
        int heartRate = (int) Math.round(map.get("heartRate"));
        if (heartRate < 45 || heartRate > 150)
//            summarySentence = "Critical ECG." + "\n"+"Heart Rate : "+heartRate+" "+summarySentence;
            summarySentence = "Critical ECG." + "\n"+summarySentence+"\nIf you feel chest pain, palpitations, " +
            "breathlessness, or dizziness, take an Aspirin 150 mg (if not allergic) and consult your doctor. If the ECG" +
            " is abnormal but you have no symptoms, repeat the ECG every 3 hours. If it stays abnormal, see your doctor." +
            " Further tests like an echocardiogram or treadmill test may be advised.";
        else
        if (rhythmType.equalsIgnoreCase("Sinus Rhythm") )
            summarySentence = "Normal ECG." + "\n"+summarySentence;
//        else if (heartRate < 45 || heartRate > 150)
//            summarySentence = "Abnormal ECG." + "\n"+summarySentence+ "\nCritical ECG, Heart Rate :"+heartRate;
        else
            summarySentence = "Abnormal ECG." + "\n"+summarySentence+"\n If you feel chest pain, palpitations, " +
            "breathlessness, or dizziness, take an Aspirin 150 mg (if not allergic) and consult your doctor. If the ECG" +
            " is abnormal but you have no symptoms, repeat the ECG every 3 hours. If it stays abnormal, see your doctor." +
            " Further tests like an echocardiogram or treadmill test may be advised.";
//        onResultCompleteListener.onCompletedLead2MetaData(twelveLeadEcgData, map, summarySentence);
        return summarySentence;
    }

    private ArrayList<String> arrhythmiaDecisionMaker(ClassificationResults cResults, String[] priorityArray) {
        ArrayList<String> finalDecisions = new ArrayList<String>();
//        System.out.println("cResult=================  "+cResults.size());
        for (int i = 0; i < cResults.size(); i++) {
//        for (int i = 0; i < 1; i++) {
            if (cResults.getTachycardia(i).equalsIgnoreCase("NATD")) {
                System.out.println("RPeaks less than than two for Lead_"+cResults.getLead(i)+" so unable to determine arrhythmia for the lead.");
                finalDecisions.add("NATD");
                continue;
            }
            ArrayList<String> detectedArr = new ArrayList<String>();
            if ( !cResults.getTachycardia(i).startsWith("No") )
                detectedArr.add(cResults.getTachycardia(i));
            if ( !cResults.getBradycardia(i).startsWith("No") )
                detectedArr.add(cResults.getBradycardia(i));
            if ( !cResults.getPac(i).startsWith("No") )
                detectedArr.add(cResults.getPac(i));
            if ( !cResults.getVpc(i).startsWith("No") )
                detectedArr.add(cResults.getVpc(i));
            if ( !cResults.getVt(i).startsWith("No") )
                detectedArr.add(cResults.getVt(i));
            if ( !cResults.getSvt(i).startsWith("No") )
                detectedArr.add(cResults.getSvt(i));
            if ( !cResults.getEscapeBeat(i).startsWith("No") )
                detectedArr.add(cResults.getEscapeBeat(i));
            if ( !cResults.getPause(i).startsWith("No") )
                detectedArr.add(cResults.getPause(i));
            if ( !cResults.getJunctionalTachycardia(i).startsWith("No") )
                detectedArr.add(cResults.getJunctionalTachycardia(i));
            if ( !cResults.getPrematureJunctionalRhythm(i).startsWith("No") )
                detectedArr.add(cResults.getPrematureJunctionalRhythm(i));
            if ( !cResults.getFirstDegreeAvBlock(i).startsWith("No") )
                detectedArr.add(cResults.getFirstDegreeAvBlock(i));
            if ( !cResults.getMobitzType1AVBlock(i).startsWith("No") )
                detectedArr.add(cResults.getMobitzType1AVBlock(i));
            if ( !cResults.getMobitzType2AVBlock(i).startsWith("No") )
                detectedArr.add(cResults.getMobitzType2AVBlock(i));
            if ( !cResults.getPreExcitationSyndrome(i).startsWith("No") )
                detectedArr.add(cResults.getPreExcitationSyndrome(i));
            if ( !cResults.getIdioventricularRhythm(i).startsWith("No") )
                detectedArr.add(cResults.getIdioventricularRhythm(i));
            if ( !cResults.getAcceleratedIdioventricularRhythm(i).startsWith("No") )
                detectedArr.add(cResults.getAcceleratedIdioventricularRhythm(i));
            if ( !cResults.getJunctionalRhythm(i).startsWith("No") )
                detectedArr.add(cResults.getJunctionalRhythm(i));
            if ( !cResults.getAtrialFlutter(i).startsWith("No") )
                detectedArr.add(cResults.getAtrialFlutter(i));
            if ( !cResults.getAtrialTachycardia(i).startsWith("No") )
                detectedArr.add(cResults.getAtrialTachycardia(i));
            if ( !cResults.getAtrialFibrillation(i).startsWith("No") )
                detectedArr.add(cResults.getAtrialFibrillation(i));
            if ( !cResults.getCompleteHeartBlock(i).startsWith("No") )
                detectedArr.add(cResults.getCompleteHeartBlock(i));
            if ( !cResults.getSinusArrhythmia(i).startsWith("No") || cResults.getMultiPClassification(i).startsWith("NA"))
                detectedArr.add(cResults.getSinusArrhythmia(i));
            if ( !cResults.getSinusRhythm(i).startsWith("No") || cResults.getMultiPClassification(i).startsWith("NA"))
                detectedArr.add(cResults.getSinusRhythm(i));
            if ( !cResults.getMultiPClassification(i).startsWith("No") || cResults.getMultiPClassification(i).startsWith("NA") )
                detectedArr.add(cResults.getMultiPClassification(i));
            // Determine the highest-priority arrhythmia that is detected
            String chosenArrhythmia = "Sinus Rhythm";       //  Default if none are found
            outer:
            for (String priority : priorityArray) {
                for (String detected : detectedArr) {
                    if (priority.equalsIgnoreCase(detected)) {
                        chosenArrhythmia = priority; // Return on the first (highest-priority) match
                        break outer;
                    }
                }
            }
            System.out.println("Final Decision for Lead_"+cResults.getLead(i)+" : "+chosenArrhythmia);
            finalDecisions.add(chosenArrhythmia);
        }
        return finalDecisions;
    }

    public Map<String, Object> rhythmSummaryReport(ArrayList<String> finalDecisions, double thresholdMax, double thresholdEntropy, String[] priorityArray) {
        Map<String, Object> map = new HashMap<String, Object>();
        HashMap<String, Integer> frequencyOfStrings = new HashMap<String, Integer>();
        for (String item : finalDecisions) {
            frequencyOfStrings.put(item, frequencyOfStrings.getOrDefault(item, 0) + 1);
        }
        String[] uniqueDecision = frequencyOfStrings.keySet().toArray(new String[0]);
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < priorityArray.length; i++) {
            for (int j = 0; j < uniqueDecision.length; j++) {
                if (priorityArray[i].equalsIgnoreCase(uniqueDecision[j])) {
                    list.add(i);
                    break;
                }
            }
        }
//        Arrays.stream(uniqueDecision).forEach(System.out::println);
        int[] arr = list.stream().mapToInt(Integer::intValue).toArray();
        String[] uniqueDecisionPriorityBased = new String[arr.length];
        for (int i = 0; i < arr.length; i++) {
            uniqueDecisionPriorityBased[i] = priorityArray[arr[i]];
        }
//        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
//        for (int i = 0; i < arr.length; i++) {
//            System.out.println(uniqueDecisionPriorityBased[i]+" , "+frequencyOfStrings.get(priorityArray[arr[i]]));
//        }
        int[] count = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            count[i] = frequencyOfStrings.get(priorityArray[arr[i]]);
        }
        int totalVotes = Arrays.stream(count).sum();
        //  Convert counts to probabilities.
        double[] probs = IntStream.of(count).mapToDouble( e-> (double) Math.round(( (double) e / totalVotes) * 1000) / 1000).toArray();
//        Arrays.stream(probs).forEach(System.out::println);
        double d = Math.ulp(1.0);
        double entropyVal = 0;
        for (int i = 0; i < probs.length; i++) {
            entropyVal -= probs[i] * ( ( Math.log(probs[i] + d)) / Math.log(2) );
        }
        //  Determine the candidate with the maximum probability.
        double maxProb = ut.findMax(probs);
        int maxIndex = ut.findMaxIndex(probs);
        String rhythmType = "Indeterminate";
        if (maxProb >= thresholdMax)
            rhythmType = uniqueDecisionPriorityBased[maxIndex];
        if (rhythmType.equalsIgnoreCase("NATD"))
            rhythmType = "Indeterminate";
        map.put("rhythmType" , rhythmType);
        map.put("uniqueDecisionPriorityBased", uniqueDecisionPriorityBased);
        map.put("probs", probs);
        map.put("entropyVal", entropyVal);
        return map;
    }

    //    --------------------------------------- Helper Methods --------------------------------------------------
    public String classifyPWavePresence(double[] pAmplitudes) {
        double threshold = 0.03;
        String pPresence = "NA";
        if (pAmplitudes.length == 0)
            return pPresence;
        int len = Arrays.stream(pAmplitudes).filter(val -> Math.abs(val) < threshold).toArray().length;
        double absencePercentage = ( (double) len / pAmplitudes.length) * 100;
        if (absencePercentage > 25)
            pPresence = "Mostly Absent";
        else
            pPresence = "Present";
        return pPresence;
    }

    public double calculateLocalVariabilityPercent(double[] validQRS) {
        if (validQRS == null || validQRS.length < 2) {
            return 0; // not enough data
        }

        double sum = 0.0;
        int count = 0;

        for (int i = 0; i < validQRS.length - 1; i++) {
            double diff = validQRS[i + 1] - validQRS[i];
            double absDiff = Math.abs(diff);
            double base = validQRS[i];

            if ( base != 0.0) {
                double percent = (absDiff / base) * 100.0;
                sum += percent;
                count++;
            }
        }

        return count > 0 ? (double) Math.round((sum / count) * 10 ) / 10 : 0;
    }

/*    private void displayArrhythmiaValues(ArrhythmiaValues values) {
        for (int i = 0; i < 9; i++) {
            System.out.println(values.getLeadSingleValue(i)+" , "+values.getHeartRateSingleValue(i) +" , "+values.getInterAtrialRateSingleValue(i)+" , "
                    +values.get);
        }

    }*/

    public void displayClassificationResultData(ClassificationResults cResults) {
        String[] column = {"Tachycardia", "Bradycardia", "PAC", "VPC", "VT", "SVT", "EscapeBeat", "Pause",
                "JunctionalTachycardia", "PrematureJunctionalRhythm", "FirstDegreeAVBlock", "MobitzType1AVBlock",
                "MobitzType2AVBlock", "PreExcitationSyndrome", "IdioventricularRhythm",
                "AcceleratedIdioventricularRhythm", "JunctionalRhythm", "AtrialFlutter", "AtrialTachycardia",
                "AtrialFibrillation", "CompleteHeartBlock", "SinusArrhythmia", "SinusRhythm", "MultiPClassification"
        };
        System.out.println(String.format("%-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s",column[0], column[1],
                column[2], column[3], column[4], column[5], column[6], column[7], column[8], column[9], column[10],
                column[11], column[12], column[13], column[14], column[15], column[16], column[17], column[18],
                column[19], column[20], column[21], column[22], column[23] )
        );
        System.out.println(" ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- " +
                "-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------" +
                "-------------------------------------------------------------------------------------------------------------------------------------------------------------------------- ");
        for (int i = 0; i < cResults.size(); i++) {
            System.out.println(String.format("%-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s",
                    "Lead = "+cResults.getLead(i)+" | "+cResults.getTachycardia(i)+" | ",
                    cResults.getBradycardia(i)+" | ",
                    cResults.getPac(i)+" | ",
                    cResults.getVpc(i)+" | ",
                    cResults.getVt(i)+" | ",
                    cResults.getSvt(i)+" | ",
                    cResults.getEscapeBeat(i)+" | ",
                    cResults.getPause(i)+" | ",
                    cResults.getJunctionalTachycardia(i)+" | ",
                    cResults.getPrematureJunctionalRhythm(i)+" | ",
                    cResults.getFirstDegreeAvBlock(i)+" | ",
                    cResults.getMobitzType1AVBlock(i)+" | ",
                    cResults.getMobitzType2AVBlock(i)+" | ",
                    cResults.getPreExcitationSyndrome(i)+" | ",
                    cResults.getIdioventricularRhythm(i)+" | ",
                    cResults.getAcceleratedIdioventricularRhythm(i)+" | ",
                    cResults.getJunctionalRhythm(i)+" | ",
                    cResults.getAtrialFlutter(i)+" | ",
                    cResults.getAtrialTachycardia(i)+" | ",
                    cResults.getAtrialFibrillation(i)+" | ",
                    cResults.getCompleteHeartBlock(i)+" | ",
                    cResults.getSinusArrhythmia(i)+" | ",
                    cResults.getSinusRhythm(i)+" | ",
                    cResults.getMultiPClassification(i)+" | "
            ));
        }

    }

//    public void displayArrhythmia() {
//        String[] key = {"Lead_0","Lead_1","Lead_2","Lead_3","Lead_4","Lead_5","Lead_6","Lead_7","Lead_8",};
////        for (int i = 0; i < allLeadSummary.size(); i++) {
//        for (int i = 0; i < 9; i++) {
////        for (int i = 0; i < 1; i++) {
//            if (allLeadArrhythmiaSummary.get(key[i]) != null)     {
//                System.out.println("-------------------- Arrythmia data for Lead_" + i + "  ------------------------");
//                System.out.println(allLeadArrhythmiaSummary.get(key[i]));
//            }
//        }
//
//    }


}
