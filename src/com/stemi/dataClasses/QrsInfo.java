package com.stemi.dataClasses;

import java.util.ArrayList;

public class QrsInfo {

    private ArrayList<Integer> qrsLocal = new ArrayList<>();
    private ArrayList<Double> qrsAmplitude = new ArrayList<>();
    private boolean[] qrsPresent;
    private boolean[] qrsAbsent;

    public QrsInfo() { }

    public QrsInfo(ArrayList<Integer> qrsLocal, ArrayList<Double> qrsAmplitude, boolean[] qrsPresent, boolean[] qrsAbsent) {
        this.qrsLocal = qrsLocal;
        this.qrsAmplitude = qrsAmplitude;
        this.qrsPresent = qrsPresent;
        this.qrsAbsent = qrsAbsent;
    }

    public ArrayList<Integer> getQrsLocal() {
        return qrsLocal;
    }

    public void setQrsLocal(ArrayList<Integer> qrsLocal) {
        this.qrsLocal = qrsLocal;
    }

    public ArrayList<Double> getQrsAmplitude() {
        return qrsAmplitude;
    }

    public void setQrsAmplitude(ArrayList<Double> qrsAmplitude) {
        this.qrsAmplitude = qrsAmplitude;
    }

    public boolean[] getQrsPresent() {
        return qrsPresent;
    }

    public void setQrsPresent(boolean[] qrsPresent) {
        this.qrsPresent = qrsPresent;
    }

    public boolean[] getQrsAbsent() {
        return qrsAbsent;
    }

    public void setQrsAbsent(boolean[] qrsAbsent) {
        this.qrsAbsent = qrsAbsent;
    }
}
