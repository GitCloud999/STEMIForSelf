package com.stemi.dataClasses;

import java.util.ArrayList;

public class DurationNew {
    public ArrayList<Double> P = new ArrayList<Double>();
    public ArrayList<Integer> Q = new ArrayList<Integer>();
    public ArrayList<Integer> R = new ArrayList<Integer>();
    public ArrayList<Integer> S = new ArrayList<Integer>();
    public ArrayList<Integer> T = new ArrayList<Integer>();
    public ArrayList<Double> PR = new ArrayList<Double>();
    public ArrayList<Integer> QRS = new ArrayList<Integer>();
    public ArrayList<Integer> QT = new ArrayList<Integer>();
    public ArrayList<Double> QTc = new ArrayList<Double>();

    public void displayDurationData(ArrayList<Integer> data) {
        for (int i = 0; i < data.size(); i++) {
            System.out.println("i = "+i+" data = "+data.get(i));
        }
    }

    public void displayDurationDoubleData(ArrayList<Double> data) {
        for (int i = 0; i < data.size(); i++) {
            System.out.println("i = "+i+" data = "+data.get(i));
        }
    }
}
