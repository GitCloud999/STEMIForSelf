package com.stemi.dataClasses;

public class Amplitude {
    public ThreeArrayListData P = new ThreeArrayListData();
    public ThreeArrayListData Q = new ThreeArrayListData();
    public ThreeArrayListData R = new ThreeArrayListData();
    public ThreeArrayListData S = new ThreeArrayListData();
    public ThreeArrayListData T = new ThreeArrayListData();
    public ThreeArrayListData J = new ThreeArrayListData();

    public void displayAmplitudeData(ThreeArrayListData data) {
        for (int i = 0; i < data.Size()[0]; i++) {
            System.out.println("Amplitude=== "+data.getColumnOneValue(i) +"  -- "+data.getColumnTwoValue(i)+"  -- "+data.getColumnThreeValue(i));
        }
    }

//    public double findMean ()
}
