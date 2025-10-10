package com.stemi.dataClasses;

public class Features {
    public TwoArrayListData P = new TwoArrayListData();
    public TwoArrayListData Q = new TwoArrayListData();
    public TwoArrayListData R = new TwoArrayListData();
    public TwoArrayListData S = new TwoArrayListData();
    public TwoArrayListData T = new TwoArrayListData();
    public TwoArrayListData J = new TwoArrayListData();

    public void displayFeaturesData(TwoArrayListData twoArrayListData) {
        for (int i = 0; i < twoArrayListData.Size()[0]; i++) {
            System.out.println(twoArrayListData.getIndex(i) +"  -- "+twoArrayListData.getValue(i));
        }
    }
}
