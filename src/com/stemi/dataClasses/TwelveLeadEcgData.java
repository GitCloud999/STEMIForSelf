package com.stemi.dataClasses;

import java.util.ArrayList;


public class TwelveLeadEcgData {
    private ArrayList<Double> v1;
    private ArrayList<Double> v2;
    private ArrayList<Double> v3;
    private ArrayList<Double> v4;
    private ArrayList<Double> v5;
    private ArrayList<Double> v6;
    private ArrayList<Double> lead1;
    private ArrayList<Double> lead2;
    private ArrayList<Double> lead3;
    private ArrayList<Double> avr;
    private ArrayList<Double> avl;
    private ArrayList<Double> avf;

    public TwelveLeadEcgData() {
    }

    public TwelveLeadEcgData(ArrayList<Double> v1, ArrayList<Double> v2, ArrayList<Double> v3, ArrayList<Double> v4, ArrayList<Double> v5, ArrayList<Double> v6, ArrayList<Double> lead1, ArrayList<Double> lead2, ArrayList<Double> lead3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
        this.v5 = v5;
        this.v6 = v6;
        this.lead1 = lead1;
        this.lead2 = lead2;
        this.lead3 = lead3;
    }

    public TwelveLeadEcgData(ArrayList<Double> v1, ArrayList<Double> v2, ArrayList<Double> v3, ArrayList<Double> v4, ArrayList<Double> v5, ArrayList<Double> v6, ArrayList<Double> lead1, ArrayList<Double> lead2, ArrayList<Double> lead3, ArrayList<Double> avr, ArrayList<Double> avl, ArrayList<Double> avf) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
        this.v5 = v5;
        this.v6 = v6;
        this.lead1 = lead1;
        this.lead2 = lead2;
        this.lead3 = lead3;
        this.avr = avr;
        this.avl = avl;
        this.avf = avf;
    }

    public ArrayList<Double> getV1() {
        return v1;
    }

    public void setV1(ArrayList<Double> v1) {
        this.v1 = v1;
    }

    public ArrayList<Double> getV2() {
        return v2;
    }

    public void setV2(ArrayList<Double> v2) {
        this.v2 = v2;
    }

    public ArrayList<Double> getV3() {
        return v3;
    }

    public void setV3(ArrayList<Double> v3) {
        this.v3 = v3;
    }

    public ArrayList<Double> getV4() {
        return v4;
    }

    public void setV4(ArrayList<Double> v4) {
        this.v4 = v4;
    }

    public ArrayList<Double> getV5() {
        return v5;
    }

    public void setV5(ArrayList<Double> v5) {
        this.v5 = v5;
    }

    public ArrayList<Double> getV6() {
        return v6;
    }

    public void setV6(ArrayList<Double> v6) {
        this.v6 = v6;
    }

    public ArrayList<Double> getLead1() {
        return lead1;
    }

    public void setLead1(ArrayList<Double> lead1) {
        this.lead1 = lead1;
    }

    public ArrayList<Double> getLead2() {
        return lead2;
    }

    public void setLead2(ArrayList<Double> lead2) {
        this.lead2 = lead2;
    }

    public ArrayList<Double> getLead3() {
        return lead3;
    }

    public void setLead3(ArrayList<Double> lead3) {
        this.lead3 = lead3;
    }

    public ArrayList<Double> getAvr() {
        return avr;
    }

    public void setAvr(ArrayList<Double> avr) {
        this.avr = avr;
    }

    public ArrayList<Double> getAvl() {
        return avl;
    }

    public void setAvl(ArrayList<Double> avl) {
        this.avl = avl;
    }

    public ArrayList<Double> getAvf() {
        return avf;
    }

    public void setAvf(ArrayList<Double> avf) {
        this.avf = avf;
    }

}
