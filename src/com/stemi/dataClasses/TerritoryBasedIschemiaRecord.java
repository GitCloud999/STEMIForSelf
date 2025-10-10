package com.stemi.dataClasses;

public class TerritoryBasedIschemiaRecord {

    private String territory;
    private int nLeads;
    private int nDepressed;
    private int nHorizDownDepressed;
    private boolean hasDeepDepressed;
    private int nTInverted;
    private boolean isPositive;
    private boolean isSupport;

    public TerritoryBasedIschemiaRecord() { }

    public TerritoryBasedIschemiaRecord(String territory, int nLeads, int nDepressed, int nHorizDownDepressed, boolean hasDeepDepressed, int nTInverted, boolean isPositive, boolean isSupport) {
        this.territory = territory;
        this.nLeads = nLeads;
        this.nDepressed = nDepressed;
        this.nHorizDownDepressed = nHorizDownDepressed;
        this.hasDeepDepressed = hasDeepDepressed;
        this.nTInverted = nTInverted;
        this.isPositive = isPositive;
        this.isSupport = isSupport;
    }

    // Getters
    public String getTerritory() {
        return territory;
    }

    public int getnLeads() {
        return nLeads;
    }

    public int getnDepressed() {
        return nDepressed;
    }

    public int getnHorizDownDepressed() {
        return nHorizDownDepressed;
    }

    public boolean isHasDeepDepressed() {
        return hasDeepDepressed;
    }

    public int getnTInverted() {
        return nTInverted;
    }

    public boolean getIsPositive() {
        return isPositive;
    }

    public boolean getIsSupport() {
        return isSupport;
    }
}
