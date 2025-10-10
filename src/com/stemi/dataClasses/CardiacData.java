package com.stemi.dataClasses;

import java.util.ArrayList;

public class CardiacData {
//    Variables
        private ArrayList<Integer> cycle = new ArrayList<>();
        private ArrayList<Double> rrIntervals = new ArrayList<>();
        private ArrayList<Double> heatRate = new ArrayList<>();

        private ArrayList<Integer> pStartIndex = new ArrayList<>();
        private ArrayList<Integer> pPeakIndex = new ArrayList<>();
        private ArrayList<Integer> pStopIndex = new ArrayList<>();
        private ArrayList<Double> pAmplitudeMv = new ArrayList<>();
        private ArrayList<Double> pDuration = new ArrayList<>();

        private ArrayList<Integer> qStartIndex = new ArrayList<>();
        private ArrayList<Integer> qPeakIndex = new ArrayList<>();
        private ArrayList<Integer> qStopIndex = new ArrayList<>();
        private ArrayList<Double> qAmplitudeMv = new ArrayList<>();
        private ArrayList<Integer> qDuration = new ArrayList<>();

        private ArrayList<Integer> rStartIndex = new ArrayList<>();
        private ArrayList<Integer> rPeakIndex = new ArrayList<>();
        private ArrayList<Integer> rStopIndex = new ArrayList<>();
        private ArrayList<Double> rAmplitudeMv = new ArrayList<>();
        private ArrayList<Integer> rDuration = new ArrayList<>();

        private ArrayList<Integer> sStartIndex = new ArrayList<>();
        private ArrayList<Integer> sPeakIndex = new ArrayList<>();
        private ArrayList<Integer> sStopIndex = new ArrayList<>();
        private ArrayList<Double> sAmplitudeMv = new ArrayList<>();
        private ArrayList<Integer> sDuration = new ArrayList<>();

        private ArrayList<Integer> tStartIndex = new ArrayList<>();
        private ArrayList<Integer> tPeakIndex = new ArrayList<>();
        private ArrayList<Integer> tStopIndex = new ArrayList<>();
        private ArrayList<Double> tAmplitudeMv = new ArrayList<>();
        private ArrayList<Integer> tDuration = new ArrayList<>();

        private ArrayList<Integer> jStartIndex = new ArrayList<>();
        private ArrayList<Integer> jPeakIndex = new ArrayList<>();
        private ArrayList<Integer> jStopIndex = new ArrayList<>();
        private ArrayList<Double> jAmplitudeMv = new ArrayList<>();
        private ArrayList<Integer> jDuration = new ArrayList<>();

        private ArrayList<Integer> qtInterval = new ArrayList<>();
        private ArrayList<Double> qtcInterval = new ArrayList<>();
        private ArrayList<Double> stElevation = new ArrayList<>();
        private ArrayList<Double> prInterval = new ArrayList<>();
        private ArrayList<Integer> qrsDuration = new ArrayList<>();
        private ArrayList<Double> stSagitaMv = new ArrayList<>();
        private ArrayList<Integer> stMorphologyCode = new ArrayList<>();
        private ArrayList<Boolean> stTombstoneFlag = new ArrayList<>();
        
        
        // Getters & Setters individual value
        // ===== cycle =====
        public void addCycle(Integer value) {
                cycle.add(value);
        }
        public Integer getCycle(int index) {
                return cycle.get(index);
        }

        // ===== rrIntervals =====
        public void addRrInterval(Double value) {
                rrIntervals.add(value);
        }
        public Double getRrInterval(int index) {
                return rrIntervals.get(index);
        }

        // ===== heatRate =====
        public void addHeatRate(Double value) {
                heatRate.add(value);
        }
        public Double getHeatRate(int index) {
                return heatRate.get(index);
        }

        // ===== P wave =====
        public void addPStartIndex(Integer value) {
                pStartIndex.add(value);
        }
        public Integer getPStartIndex(int index) {
                return pStartIndex.get(index);
        }

        public void addPPeakIndex(Integer value) {
                pPeakIndex.add(value);
        }
        public Integer getPPeakIndex(int index) {
                return pPeakIndex.get(index);
        }

        public void addPStopIndex(Integer value) {
                pStopIndex.add(value);
        }
        public Integer getPStopIndex(int index) {
                return pStopIndex.get(index);
        }

        public void addPAmplitudeMv(Double value) {
                pAmplitudeMv.add(value);
        }
        public Double getPAmplitudeMv(int index) {
                return pAmplitudeMv.get(index);
        }

        public void addPDuration(Double value) {
                pDuration.add(value);
        }
        public Double getPDuration(int index) {
                return pDuration.get(index);
        }

        // ===== Q wave =====
        public void addQStartIndex(Integer value) {
                qStartIndex.add(value);
        }
        public Integer getQStartIndex(int index) {
                return qStartIndex.get(index);
        }

        public void addQPeakIndex(Integer value) {
                qPeakIndex.add(value);
        }
        public Integer getQPeakIndex(int index) {
                return qPeakIndex.get(index);
        }

        public void addQStopIndex(Integer value) {
                qStopIndex.add(value);
        }
        public Integer getQStopIndex(int index) {
                return qStopIndex.get(index);
        }

        public void addQAmplitudeMv(Double value) {
                qAmplitudeMv.add(value);
        }
        public Double getQAmplitudeMv(int index) {
                return qAmplitudeMv.get(index);
        }

        public void addQDuration(Integer value) {
                qDuration.add(value);
        }
        public Integer getQDuration(int index) {
                return qDuration.get(index);
        }

        // ===== R wave =====
        public void addRStartIndex(Integer value) {
                rStartIndex.add(value);
        }
        public Integer getRStartIndex(int index) {
                return rStartIndex.get(index);
        }

        public void addRPeakIndex(Integer value) {
                rPeakIndex.add(value);
        }
        public Integer getRPeakIndex(int index) {
                return rPeakIndex.get(index);
        }

        public void addRStopIndex(Integer value) {
                rStopIndex.add(value);
        }
        public Integer getRStopIndex(int index) {
                return rStopIndex.get(index);
        }

        public void addRAmplitudeMv(Double value) {
                rAmplitudeMv.add(value);
        }
        public Double getRAmplitudeMv(int index) {
                return rAmplitudeMv.get(index);
        }

        public void addRDuration(Integer value) {
                rDuration.add(value);
        }
        public Integer getRDuration(int index) {
                return rDuration.get(index);
        }

        // ===== S wave =====
        public void addSStartIndex(Integer value) {
                sStartIndex.add(value);
        }
        public Integer getSStartIndex(int index) {
                return sStartIndex.get(index);
        }

        public void addSPeakIndex(Integer value) {
                sPeakIndex.add(value);
        }
        public Integer getSPeakIndex(int index) {
                return sPeakIndex.get(index);
        }

        public void addSStopIndex(Integer value) {
                sStopIndex.add(value);
        }
        public Integer getSStopIndex(int index) {
                return sStopIndex.get(index);
        }

        public void addSAmplitudeMv(Double value) {
                sAmplitudeMv.add(value);
        }
        public Double getSAmplitudeMv(int index) {
                return sAmplitudeMv.get(index);
        }

        public void addSDuration(Integer value) {
                sDuration.add(value);
        }
        public Integer getSDuration(int index) {
                return sDuration.get(index);
        }

        // ===== T wave =====
        public void addTStartIndex(Integer value) {
                tStartIndex.add(value);
        }
        public Integer getTStartIndex(int index) {
                return tStartIndex.get(index);
        }

        public void addTPeakIndex(Integer value) {
                tPeakIndex.add(value);
        }
        public Integer getTPeakIndex(int index) {
                return tPeakIndex.get(index);
        }

        public void addTStopIndex(Integer value) {
                tStopIndex.add(value);
        }
        public Integer getTStopIndex(int index) {
                return tStopIndex.get(index);
        }

        public void addTAmplitudeMv(Double value) {
                tAmplitudeMv.add(value);
        }
        public Double getTAmplitudeMv(int index) {
                return tAmplitudeMv.get(index);
        }

        public void addTDuration(Integer value) {
                tDuration.add(value);
        }
        public Integer getTDuration(int index) {
                return tDuration.get(index);
        }

        // ===== J wave =====
        public void addJStartIndex(Integer value) {
                jStartIndex.add(value);
        }
        public Integer getJStartIndex(int index) {
                return jStartIndex.get(index);
        }

        public void addJPeakIndex(Integer value) {
                jPeakIndex.add(value);
        }
        public Integer getJPeakIndex(int index) {
                return jPeakIndex.get(index);
        }

        public void addJStopIndex(Integer value) {
                jStopIndex.add(value);
        }
        public Integer getJStopIndex(int index) {
                return jStopIndex.get(index);
        }

        public void addJAmplitudeMv(Double value) {
                jAmplitudeMv.add(value);
        }
        public Double getJAmplitudeMv(int index) {
                return jAmplitudeMv.get(index);
        }

        public void addJDuration(Integer value) {
                jDuration.add(value);
        }
        public Integer getJDuration(int index) {
                return jDuration.get(index);
        }

        // ===== Intervals =====
        public void addQtInterval(Integer value) {
                qtInterval.add(value);
        }
        public Integer getQtInterval(int index) {
                return qtInterval.get(index);
        }

        public void addQtcInterval(Double value) {
                qtcInterval.add(value);
        }
        public Double getQtcInterval(int index) {
                return qtcInterval.get(index);
        }

        public void addStElevation(Double value) {
                stElevation.add(value);
        }
        public Double getStElevation(int index) {
                return stElevation.get(index);
        }

        public void addPrInterval(Double value) {
                prInterval.add(value);
        }
        public Double getPrInterval(int index) {
                return prInterval.get(index);
        }

        public void addQrsDuration(Integer value) {
                qrsDuration.add(value);
        }
        public Integer getQrsDuration(int index) {
                return qrsDuration.get(index);
        }

        public void addStSagitaMv(Double value) { stSagitaMv.add(value); }
        public Double getStSagitaMv(int index) { return stSagitaMv.get(index); }

        public void addStMorphologyCode(Integer value) {
                stMorphologyCode.add(value);
        }
        public Integer getStMorphologyCode(int index) {
                return stMorphologyCode.get(index);
        }

        public void addStTombstoneFlag(Boolean value) {
                stTombstoneFlag.add(value);
        }
        public Boolean getStTombstoneFlag(int index) {
                return stTombstoneFlag.get(index);
        }

        // Getters for complete arrayList
        public ArrayList<Integer> getCycle() {
                return cycle;
        }

        public ArrayList<Double> getRrIntervals() {
                return rrIntervals;
        }

        public ArrayList<Double> getHeatRate() {
                return heatRate;
        }

        // P Wave
        public ArrayList<Integer> getPStartIndex() {
                return pStartIndex;
        }

        public ArrayList<Integer> getPPeakIndex() {
                return pPeakIndex;
        }

        public ArrayList<Integer> getPStopIndex() {
                return pStopIndex;
        }

        public ArrayList<Double> getPAmplitudeMv() {
                return pAmplitudeMv;
        }

        public ArrayList<Double> getPDuration() {
                return pDuration;
        }

        // Q Wave
        public ArrayList<Integer> getQStartIndex() {
                return qStartIndex;
        }

        public ArrayList<Integer> getQPeakIndex() {
                return qPeakIndex;
        }

        public ArrayList<Integer> getQStopIndex() {
                return qStopIndex;
        }

        public ArrayList<Double> getQAmplitudeMv() {
                return qAmplitudeMv;
        }

        public ArrayList<Integer> getQDuration() {
                return qDuration;
        }

        // R Wave
        public ArrayList<Integer> getRStartIndex() {
                return rStartIndex;
        }

        public ArrayList<Integer> getRPeakIndex() {
                return rPeakIndex;
        }

        public ArrayList<Integer> getRStopIndex() {
                return rStopIndex;
        }

        public ArrayList<Double> getRAmplitudeMv() {
                return rAmplitudeMv;
        }

        public ArrayList<Integer> getRDuration() {
                return rDuration;
        }

        // S Wave
        public ArrayList<Integer> getSStartIndex() {
                return sStartIndex;
        }

        public ArrayList<Integer> getSPeakIndex() {
                return sPeakIndex;
        }

        public ArrayList<Integer> getSStopIndex() {
                return sStopIndex;
        }

        public ArrayList<Double> getSAmplitudeMv() {
                return sAmplitudeMv;
        }

        public ArrayList<Integer> getSDuration() {
                return sDuration;
        }

        // T Wave
        public ArrayList<Integer> getTStartIndex() {
                return tStartIndex;
        }

        public ArrayList<Integer> getTPeakIndex() {
                return tPeakIndex;
        }

        public ArrayList<Integer> getTStopIndex() {
                return tStopIndex;
        }

        public ArrayList<Double> getTAmplitudeMv() {
                return tAmplitudeMv;
        }

        public ArrayList<Integer> getTDuration() {
                return tDuration;
        }

        // J Wave
        public ArrayList<Integer> getJStartIndex() {
                return jStartIndex;
        }

        public ArrayList<Integer> getJPeakIndex() {
                return jPeakIndex;
        }

        public ArrayList<Integer> getJStopIndex() {
                return jStopIndex;
        }

        public ArrayList<Double> getJAmplitudeMv() {
                return jAmplitudeMv;
        }

        public ArrayList<Integer> getJDuration() {
                return jDuration;
        }

        // Intervals & Others
        public ArrayList<Integer> getQtInterval() {
                return qtInterval;
        }

        public ArrayList<Double> getQtcInterval() {
                return qtcInterval;
        }

        public ArrayList<Double> getStElevation() {
                return stElevation;
        }

        public ArrayList<Double> getPrInterval() {
                return prInterval;
        }

        public ArrayList<Integer> getQrsDuration() {
                return qrsDuration;
        }

        public ArrayList<Double> getStSagitaMv() { return stSagitaMv; }

        public ArrayList<Integer> getStMorphologyCode() {
                return stMorphologyCode ;
        }

        public ArrayList<Boolean> getStTombstoneFlag() {
                return stTombstoneFlag ;
        }

        // Display methods
        public void displayIntegerCardiacData(ArrayList<Integer> list) {
                for (int i = 0; i < list.size(); i++)
                        System.out.println("i = "+i+" data = "+list.get(i));
        }

        public void displayDoubleCardiacData(ArrayList<Double> list) {
                for (int i = 0; i < list.size(); i++)
                        System.out.println("i = "+i+" data = "+list.get(i));
        }

        public void displayBooleanCardiacData(ArrayList<Double> list) {
                for (int i = 0; i < list.size(); i++)
                        System.out.println("i = "+i+" data = "+list.get(i));
        }

}
