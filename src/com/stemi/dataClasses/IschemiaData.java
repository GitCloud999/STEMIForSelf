package com.stemi.dataClasses;

import java.util.ArrayList;
import java.util.HashMap;

public class IschemiaData {
    private StemiCalculationData stemiData;
    private ArrayList<String> territory;
    private ArrayList<String> stSlope;
    private ArrayList<Boolean> stDepressionFlag;
    private ArrayList<Boolean> stHorizOrDownFlag;
    private ArrayList<Boolean> tInverssionFlag;
    private boolean[] stPersistent;
    private boolean[] stPersistentStrong;
    private boolean[] tPersistentInverse;
    private HashMap<String, Boolean> territoryCounts;
    private int nLeadStDepression;
    private int nLeadsTInverse;
    private boolean suspicious;
    private boolean globalSuspicious;
    private String ischemiaSummary;

    public StemiCalculationData getStemiData() {
        return stemiData;
    }

    public void setStemiData(StemiCalculationData stemiData) {
        this.stemiData = stemiData;
    }

    public ArrayList<String> getTerritory() {
        return territory;
    }

    public void setTerritory(ArrayList<String> territory) {
        this.territory = territory;
    }

    public ArrayList<String> getStSlope() {
        return stSlope;
    }

    public void setStSlope(ArrayList<String> stSlope) {
        this.stSlope = stSlope;
    }

    public ArrayList<Boolean> getStDepressionFlag() {
        return stDepressionFlag;
    }

    public void setStDepressionFlag(ArrayList<Boolean> stDepressionFlag) {
        this.stDepressionFlag = stDepressionFlag;
    }

    public ArrayList<Boolean> getStHorizOrDownFlag() {
        return stHorizOrDownFlag;
    }

    public void setStHorizOrDownFlag(ArrayList<Boolean> stHorizOrDownFlag) {
        this.stHorizOrDownFlag = stHorizOrDownFlag;
    }

    public ArrayList<Boolean> getTInverssionFlag() {
        return tInverssionFlag;
    }

    public void setTInverssionFlag(ArrayList<Boolean> tInverssionFlag) {
        this.tInverssionFlag = tInverssionFlag;
    }

    public boolean[] getStPersistent() {
        return stPersistent;
    }

    public void setStPersistent(boolean[] stPersistent) {
        this.stPersistent = stPersistent;
    }

    public boolean[] getStPersistentStrong() {
        return stPersistentStrong;
    }

    public void setStPersistentStrong(boolean[] stPersistentStrong) {
        this.stPersistentStrong = stPersistentStrong;
    }

    public boolean[] getTPersistentInverse() {
        return tPersistentInverse;
    }

    public void setTPersistentInverse(boolean[] tPersistentInverse) {
        this.tPersistentInverse = tPersistentInverse;
    }

    public HashMap<String, Boolean> getTerritoryCounts() {
        return territoryCounts;
    }

    public void setTerritoryCounts(HashMap<String, Boolean> territoryCounts) {
        this.territoryCounts = territoryCounts;
    }

    public int getNLeadStDepression() {
        return nLeadStDepression;
    }

    public void setNLeadStDepression(int nLeadStDepression) {
        this.nLeadStDepression = nLeadStDepression;
    }

    public int getNLeadsTInverse() {
        return nLeadsTInverse;
    }

    public void setNLeadsTInverse(int nLeadsTInverse) {
        this.nLeadsTInverse = nLeadsTInverse;
    }

    public boolean isSuspicious() {
        return suspicious;
    }

    public void setSuspicious(boolean suspicious) {
        this.suspicious = suspicious;
    }

    public boolean isGlobalSuspicious() {
        return globalSuspicious;
    }

    public void setGlobalSuspicious(boolean globalSuspicious) {
        this.globalSuspicious = globalSuspicious;
    }

    public String getIschemiaSummary() {
        return ischemiaSummary;
    }

    public void setIschemiaSummary(String ischemiaSummary) {
        this.ischemiaSummary = ischemiaSummary;
    }
}
