package com.stemi.dataClasses;

import java.util.ArrayList;

public class TwoArrayListData {

    private ArrayList<Integer> Index = new ArrayList<Integer>();
    private ArrayList<Double> Value = new ArrayList<Double>();

    public void addIndex(int n)
    {
        this.Index.add(n);
    }
    public void addValue(double n)
    {
        this.Value.add(n);
    }

    public int getIndex(int n)
    {
       return this.Index.get(n);
    }
    public double getValue(int n)
    {
        return this.Value.get(n);
    }

    public int[] Size(){
        int[] size = new int[2];
        size[0] = Index.size();
        size[1] = Value.size();
        return size;
    }

    public ArrayList<Integer> getIndexList() {
        return Index;
    }

    public ArrayList<Double> getValueList() {
        return Value;
    }
}
