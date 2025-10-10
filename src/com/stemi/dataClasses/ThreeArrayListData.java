package com.stemi.dataClasses;

import java.util.ArrayList;

public class ThreeArrayListData {

    private ArrayList<Double> columnOne = new ArrayList<Double>();
    private ArrayList<Double> columnTwo = new ArrayList<Double>();
    private ArrayList<Double> columnThree = new ArrayList<Double>();


    public void addColumnOneValue(double n)
    {
        this.columnOne.add(n);
    }
    public void addColumnTwoValue(double n)
    {
        this.columnTwo.add(n);
    }
    public void addColumnThreeValue(double n)
    {
        this.columnThree.add(n);
    }

    public double getColumnOneValue(int n)
    {
        return this.columnOne.get(n);
    }
    public double getColumnTwoValue(int n) {
        return this.columnTwo.get(n);
    }
    public double getColumnThreeValue(int n)
    {
        return this.columnThree.get(n);
    }

    public int[] Size(){
        int[] size = new int[3];
        size[0] = columnOne.size();
        size[1] = columnTwo.size();
        size[2] = columnThree.size();
        return size;
    }

    public ArrayList<Double> getColumnList(int n) {
        try {
            if (n == 0)
                return columnOne;
            if (n == 1)
                return columnTwo;
            if (n == 2)
                return columnThree;
        } catch (Exception e) {
            System.out.println("n can have only three value : - {0,1,2}");
            e.printStackTrace();
        }
        return new ArrayList<Double>();
    }


}
