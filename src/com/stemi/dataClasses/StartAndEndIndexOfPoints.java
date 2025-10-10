package com.stemi.dataClasses;

import java.util.ArrayList;

public class StartAndEndIndexOfPoints {
    public ArrayList<Integer> pStart = new ArrayList<Integer>();
    public ArrayList<Integer> qStart = new ArrayList<Integer>();
    public ArrayList<Integer> qEnd = new ArrayList<Integer>();
    public ArrayList<Integer> sStart = new ArrayList<Integer>();
    public ArrayList<Integer> sEnd = new ArrayList<Integer>();
    public ArrayList<Integer> tEnd = new ArrayList<Integer>();

    public ArrayList<Integer> pEnd = new ArrayList<Integer>();
    public ArrayList<Integer> tStart = new ArrayList<Integer>();
    public ArrayList<Integer> jIndex = new ArrayList<Integer>();


    // Setters
    public void addPStart(int n)
    {
        this.pStart.add(n);
    }
    public void addQStart(int n)
    {
        this.qStart.add(n);
    }
    public void addQEnd(int n) { this.qEnd.add(n); }
    public void addSStart(int n)
    {
        this.sStart.add(n);
    }
    public void addSEnd(int n) { this.sEnd.add(n); }
    public void addTStop(int n)
    {
        this.tEnd.add(n);
    }

    public void addPEnd(int n) { this.pEnd.add(n); }
    public void addTStart(int n) { this.tStart.add(n); }
    public void addJIndex(int n) { this.jIndex.add(n); }
//  Getters
    public int getPStart(int n) { return this.pStart.get(n);      }
    public int getQStart(int n) { return this.qStart.get(n);     }
    public int getQEnd(int n) { return this.qEnd.get(n);     }
    public int getSStart(int n) { return this.sStart.get(n);     }
    public int getSEnd(int n) { return this.sEnd.get(n);     }
    public int getTEnd(int n) { return this.tEnd.get(n);     }

    public int getPEnd(int n) { return this.pEnd.get(n);   }
    public int getTStart(int n) { return this.tStart.get(n);   }
    public int getJIndex(int n) { return this.jIndex.get(n);   }

//  Size
    public int getPStartSize(){ return this.pStart.size(); }
    public int getQStartSize(){ return this.qStart.size(); }
    public int getQEndSize(){ return this.qEnd.size(); }
    public int getSStartSize(){ return this.sStart.size(); }
    public int getSEndSize(){ return this.sEnd.size(); }
    public int getTEndSize(){ return this.tEnd.size(); }

    public int getPEndSize() { return this.pEnd.size();   }
    public int getTStartSize() { return this.tStart.size();   }
    public int getJIndexSize() { return this.jIndex.size();   }
    
    public void displayStartAndEndIndices(ArrayList<Integer> data) {
        for (int i = 0; i < data.size(); i++) {
            System.out.println("i = "+i+" data = "+data.get(i));
        }
    }
}
