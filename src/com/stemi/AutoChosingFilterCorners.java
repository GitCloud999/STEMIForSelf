package com.stemi;

import com.stemi.libs.FastFourierTransform;
import com.stemi.libs.Utility;

import java.util.Arrays;
import java.util.stream.IntStream;

public class AutoChosingFilterCorners {


    public double[] detectingFilterCorners(double[][] ecg, double fs, Filters filters) {
        Utility ut = new Utility();
        FastFourierTransform fftObj = new FastFourierTransform();
        int nLeads = ecg.length;
        double[] energy40 = new double[nLeads];
        double[] energy100 = new double[nLeads];
        double[] energy150 = new double[nLeads];
        double[] hpPeakFreq = new double[nLeads];
        for (int i = 0; i < nLeads; i++) {
//        for (int i = 0; i < 1; i++) {
            double ecgMean = ut.mean(ecg[i]);
            double[] ecgAndMeanDiff = Arrays.stream( ecg[i] ).map(e -> e - ecgMean).toArray();
            double[][] fft = fftObj.customSingleSidedSpectrumOnlyForFft(ecgAndMeanDiff);
            double[] absoluteFft = fftObj.absoluteOfFFT(fft[0], fft[1]);
            double[] freqs = IntStream.range(0, absoluteFft.length).mapToDouble(e ->
                    (double) Math.round((double) e * (fs / absoluteFft.length) * 10000) / 10000 ).toArray();
            int[] idxHp = IntStream.range(0, freqs.length).filter(e -> freqs[e] >= 0.01 && freqs[e] <= 0.5 ).toArray();
//            if (idxHp.length > 0) {
//                double max = freqs[idxHp[0]];
//                int maxIndex = 0;
//                for (int j = 0; j < idxHp.length; j++) {
//                    if (freqs[idxHp[j]] > max) {
//                        max = freqs[idxHp[j]];
//                        maxIndex = idxHp[j];
//                    }
//                }
//                hpPeakFreq[i] = freqs[maxIndex];
//            }
            if (idxHp.length > 0) {
                double max = absoluteFft[idxHp[0]];
                int maxIndex = idxHp[0];
                for (int j = 0; j < idxHp.length; j++) {
                    if (absoluteFft[idxHp[j]] > max) {
                        max = absoluteFft[idxHp[j]];
                        maxIndex = idxHp[j];
                    }
                }
                hpPeakFreq[i] = freqs[maxIndex];
            }
            else
                hpPeakFreq[i] = 0.3;
            //  use narrow ±5 Hz windows; clamp to Nyquist
            double fNyq = fs / 2;
            int win = 5;
            //   idx40  = (freqs >= max(0,40-win))  & (freqs <= min(fNyq,40+win));
            int[] idx40 = IntStream.range(0, freqs.length).filter( e -> (freqs[e] >= Math.max(0, 40 - win))
                    && (freqs[e] <= Math.min(fNyq, 40 + win))).toArray();
            int[] idx100 = IntStream.range(0, freqs.length).filter( e -> (freqs[e] >= Math.max(0, 100 - win))
                    && (freqs[e] <= Math.min(fNyq, 100 + win))).toArray();
            int[] idx150 = IntStream.range(0, freqs.length).filter( e -> (freqs[e] >= Math.max(0, 150 - win))
                    && (freqs[e] <= Math.min(fNyq, 150 + win))).toArray();
            energy40[i] = Arrays.stream(idx40).mapToDouble(
                    e -> (double) Math.round(
                    absoluteFft[e] * absoluteFft[e] * 10000 ) / 10000.0
            ).sum();
            energy100[i] = Arrays.stream(idx100).mapToDouble(
                    e -> (double) Math.round(
                    absoluteFft[e] * absoluteFft[e] * 10000 ) / 10000.0
            ).sum();

            energy150[i] = Arrays.stream(idx150).mapToDouble(
                    e -> (double) Math.round(
                    absoluteFft[e] * absoluteFft[e] * 10000 ) / 10000.0
            ).sum();
//            ut.viewData(freqs);
//            System.out.println("-------------------------------------------------------");
        }
        double hpPeakMed = ut.median(hpPeakFreq);
        //  Snap hp_fc to nearest of the allowed set
        double[] hpSet = {0.05, 0.10, 0.20, 0.30, 0.50};
        int nearestIdx = ut.findMinIndex(Arrays.stream(hpSet).map(e -> Math.abs( e - hpPeakMed)).toArray());
        double hpfc = hpSet[nearestIdx];
        double energy40Med = ut.median(energy40);
        double energy100Med = ut.median(energy100);
        double energy150Med = ut.median(energy150);
//        double ratio40over100 = energy40Med / (energy100Med + Math.ulp(1.0));
//        double ratio40over150 = energy40Med  / (energy150Med + Math.ulp(1.0));
        double ratio100over150= energy100Med / (energy150Med + Math.ulp(1.0));
        double lpfc = 150;
        if (ratio100over150 >= 1.25)
            lpfc = 100;
        double [] lpfcAndHpfc = {lpfc, hpfc};
        return lpfcAndHpfc;

    }
}
