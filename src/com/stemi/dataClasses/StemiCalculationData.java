package com.stemi.dataClasses;

import java.util.ArrayList;

public class StemiCalculationData {

    private ArrayList<String> leadName = new ArrayList<String>();
    private ArrayList<Double> sTElevationMv = new ArrayList<Double>();
    private ArrayList<Double> sagittaMv = new ArrayList<Double>();
    private ArrayList<Integer> stMorphCode = new ArrayList<Integer>();
    private ArrayList<Integer> tombstoneFlag = new ArrayList<Integer>();
    private ArrayList<Double> qrsDuration = new ArrayList<Double>();
    private ArrayList<Double> tAmplitude = new ArrayList<Double>();
    private ArrayList<Double> rAmplitude = new ArrayList<Double>();
    private ArrayList<Double> sAmplitude = new ArrayList<Double>();
    private ArrayList<Double> qAmplitude = new ArrayList<Double>();
    private ArrayList<Integer> stElevationFlag = new ArrayList<>();
    private ArrayList<Integer> stConcaveDown = new ArrayList<>();
    private ArrayList<Integer> tombstoneStrong = new ArrayList<>();
    private int numberOfLeadsElevated;
    private int anyTombstoneElev;
    private int suspicious;
    private int[] territoryCounts = new int[4];         // 0 - septal, 1 - anterior, 2 - lateral, 3 - inferior

    private int[] territoryContiguous = new int[4];     // 0 - septal, 1 - anterior, 2 - lateral, 3 - inferior

    private int[] allContigCounts = new int[7];         // 0 - septal, 1 - anterior, 2 - lateral, 3 - inferior ,
                                                        // 4 - anteroSeptalContigCounts
                                                        // 5 - anteroLateralContigCounts , 6 - inferoLateralContigCounts
    private String summary;

    // -------- Add Methods --------
    public void addLeadName(String value) {
        leadName.add(value);
    }

    public void addsTElevationMv(Double value) {
        sTElevationMv.add(value);
    }
    
    public void addSagittaMv(Double value) {
        sagittaMv.add(value);
    }

    public void addStMorphCode(Integer value) {
        stMorphCode.add(value);
    }

    public void addTombstoneFlag(Integer value) {
        tombstoneFlag.add(value);
    }

    public void addQrsDuration(Double value) {
        qrsDuration.add(value);
    }

    public void addTAmplitude(Double value) {
        tAmplitude.add(value);
    }

    public void addRAmplitude(Double value) {
        rAmplitude.add(value);
    }

    public void addSAmplitude(Double value) {
        sAmplitude.add(value);
    }

    public void addQAmplitude(Double value) {
        qAmplitude.add(value);
    }

    public void addStElevationFlag(int value) {
         stElevationFlag.add(value);
    }

    public void addStConcaveDown(int value) {
         stConcaveDown.add(value);
    }

    public void addTombstoneStrong(int value) {
         tombstoneStrong.add(value);
    }

    public void setNumberOfLeadsElevated(int numberOfLeadsElevated) {
        this.numberOfLeadsElevated = numberOfLeadsElevated;
    }

    public void setAnyTombstoneElev(int anyTombstoneElev) {
        this.anyTombstoneElev = anyTombstoneElev;
    }

    public void setSuspicious(int suspicious) {
        this.suspicious = suspicious;
    }

    public void setTerritoryCounts(int[] territoryCounts) {
        this.territoryCounts = territoryCounts;
    }

    public void setTerritoryContiguous(int[] territoryContiguous) {
        this.territoryContiguous = territoryContiguous;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setAllContigCounts(int[] allContigCounts) {
        this.allContigCounts = allContigCounts;
    }

    // -------- Get (single by index) Methods --------
    public String getLeadName(int index) {
        return leadName.get(index);
    }

    public Double getSagittaMv(int index) {
        return sagittaMv.get(index);
    }

    public Double getsTElevationMv(int index) {
        return sTElevationMv.get(index);
    }

    public Integer getStMorphCode(int index) {
        return stMorphCode.get(index);
    }

    public int getTombstoneFlag(int index) {
        return tombstoneFlag.get(index);
    }

    public Double getQrsDuration(int index) {
        return qrsDuration.get(index);
    }

    public Double getTAmplitude(int index) {
        return tAmplitude.get(index);
    }

    public Double getRAmplitude(int index) {
        return rAmplitude.get(index);
    }

    public Double getSAmplitude(int index) {
        return sAmplitude.get(index);
    }

    public Double getQAmplitude(int index) {
        return qAmplitude.get(index);
    }

    public Integer getStElevationFlag(int index) {
        return stElevationFlag.get(index);
    }

    public Integer getStConcaveDown(int index) {
        return stConcaveDown.get(index);
    }

    public Integer getTombstoneStrong(int index) {
        return tombstoneStrong.get(index);
    }

    public int getAnyTombstoneElev() {
        return anyTombstoneElev;
    }

    public int getSuspicious() {
        return suspicious;
    }

    public int[] getTerritoryCounts() {
        return territoryCounts;
    }

    public int[] getTerritoryContiguous() {
        return territoryContiguous;
    }

    public String getSummary() {
        return summary;
    }

    // Complete list getters


    public ArrayList<String> getLeadName() {
        return leadName;
    }

    public ArrayList<Double> getSTElevationMv() {
        return sTElevationMv;
    }

    public ArrayList<Double> getSagittaMv() {
        return sagittaMv;
    }

    public ArrayList<Integer> getStMorphCode() {
        return stMorphCode;
    }

    public ArrayList<Integer> getTombstoneFlag() {
        return tombstoneFlag;
    }

    public ArrayList<Double> getQrsDuration() {
        return qrsDuration;
    }

    public ArrayList<Double> getTAmplitude() {
        return tAmplitude;
    }

    public ArrayList<Double> getRAmplitude() {
        return rAmplitude;
    }

    public ArrayList<Double> getSAmplitude() {
        return sAmplitude;
    }

    public ArrayList<Double> getQAmplitude() {
        return qAmplitude;
    }

    public ArrayList<Integer> getStElevationFlag() {
        return stElevationFlag;
    }

    public ArrayList<Integer> getStConcaveDown() {
        return stConcaveDown;
    }

    public ArrayList<Integer> getTombstoneStrong() {
        return tombstoneStrong;
    }

    public int[] getAllContigCounts() {
        return allContigCounts;
    }

    public int getNumberOfLeadsElevated() {
        return numberOfLeadsElevated;
    }
}
