package com.stemi.dataClasses;

import java.util.ArrayList;

public class ClassificationResults {

    private ArrayList<Integer> lead = new ArrayList<>();
    private ArrayList<String> tachycardia = new ArrayList<>();
    private ArrayList<String> bradycardia = new ArrayList<>();
    private ArrayList<String> pac = new ArrayList<>();
    private ArrayList<String> vpc = new ArrayList<>();
    private ArrayList<String> vt = new ArrayList<>();
    private ArrayList<String> svt = new ArrayList<>();
    private ArrayList<String> escapeBeat = new ArrayList<>();
    private ArrayList<String> pause = new ArrayList<>();
    private ArrayList<String> junctionalTachycardia = new ArrayList<>();
    private ArrayList<String> prematureJunctionalRhythm = new ArrayList<>();
    private ArrayList<String> mobitzType1AVBlock = new ArrayList<>();
    private ArrayList<String> mobitzType2AVBlock = new ArrayList<>();
    private ArrayList<String> preExcitationSyndrome = new ArrayList<>();
    private ArrayList<String> idioventricularRhythm = new ArrayList<>();
    private ArrayList<String> acceleratedIdioventricularRhythm = new ArrayList<>();
    private ArrayList<String> junctionalRhythm = new ArrayList<>();
    private ArrayList<String> atrialFlutter = new ArrayList<>();
    private ArrayList<String> atrialTachycardia = new ArrayList<>();
    private ArrayList<String> atrialFibrillation = new ArrayList<>();
    private ArrayList<String> completeHeartBlock = new ArrayList<>();
    private ArrayList<String> sinusArrhythmia = new ArrayList<>();
    private ArrayList<String> sinusRhythm = new ArrayList<>();
    private ArrayList<String> multiPClassification = new ArrayList<>();
    private ArrayList<String> firstDegreeAvBlock = new ArrayList<>();

    // --- Single-value adders and getters ---
    public void addLead(int n) { lead.add(n); }
    public int getLead(int i) { return lead.get(i); }
    public void addTachycardia(String value) { tachycardia.add(value); }
    public String getTachycardia(int index) { return tachycardia.get(index); }

    public void addBradycardia(String value) { bradycardia.add(value); }
    public String getBradycardia(int index) { return bradycardia.get(index); }

    public void addPac(String value) { pac.add(value); }
    public String getPac(int index) { return pac.get(index); }

    public void addVpc(String value) { vpc.add(value); }
    public String getVpc(int index) { return vpc.get(index); }

    public void addVt(String value) { vt.add(value); }
    public String getVt(int index) { return vt.get(index); }

    public void addSvt(String value) { svt.add(value); }
    public String getSvt(int index) { return svt.get(index); }

    public void addEscapeBeat(String value) { escapeBeat.add(value); }
    public String getEscapeBeat(int index) { return escapeBeat.get(index); }

    public void addPause(String value) { pause.add(value); }
    public String getPause(int index) { return pause.get(index); }

    public void addJunctionalTachycardia(String value) { junctionalTachycardia.add(value); }
    public String getJunctionalTachycardia(int index) { return junctionalTachycardia.get(index); }

    public void addPrematureJunctionalRhythm(String value) { prematureJunctionalRhythm.add(value); }
    public String getPrematureJunctionalRhythm(int index) { return prematureJunctionalRhythm.get(index); }

    public void addMobitzType1AVBlock(String value) { mobitzType1AVBlock.add(value); }
    public String getMobitzType1AVBlock(int index) { return mobitzType1AVBlock.get(index); }

    public void addMobitzType2AVBlock(String value) { mobitzType2AVBlock.add(value); }
    public String getMobitzType2AVBlock(int index) { return mobitzType2AVBlock.get(index); }

    public void addPreExcitationSyndrome(String value) { preExcitationSyndrome.add(value); }
    public String getPreExcitationSyndrome(int index) { return preExcitationSyndrome.get(index); }

    public void addIdioventricularRhythm(String value) { idioventricularRhythm.add(value); }
    public String getIdioventricularRhythm(int index) { return idioventricularRhythm.get(index); }

    public void addAcceleratedIdioventricularRhythm(String value) { acceleratedIdioventricularRhythm.add(value); }
    public String getAcceleratedIdioventricularRhythm(int index) { return acceleratedIdioventricularRhythm.get(index); }

    public void addJunctionalRhythm(String value) { junctionalRhythm.add(value); }
    public String getJunctionalRhythm(int index) { return junctionalRhythm.get(index); }

    public void addAtrialFlutter(String value) { atrialFlutter.add(value); }
    public String getAtrialFlutter(int index) { return atrialFlutter.get(index); }

    public void addAtrialTachycardia(String value) { atrialTachycardia.add(value); }
    public String getAtrialTachycardia(int index) { return atrialTachycardia.get(index); }

    public void addAtrialFibrillation(String value) { atrialFibrillation.add(value); }
    public String getAtrialFibrillation(int index) { return atrialFibrillation.get(index); }

    public void addCompleteHeartBlock(String value) { completeHeartBlock.add(value); }
    public String getCompleteHeartBlock(int index) { return completeHeartBlock.get(index); }

    public void addSinusArrhythmia(String value) { sinusArrhythmia.add(value); }
    public String getSinusArrhythmia(int index) { return sinusArrhythmia.get(index); }

    public void addSinusRhythm(String value) { sinusRhythm.add(value); }
    public String getSinusRhythm(int index) { return sinusRhythm.get(index); }

    public void addMultiPClassification(String value) { multiPClassification.add(value); }
    public String getMultiPClassification(int index) { return multiPClassification.get(index); }

    public String getFirstDegreeAvBlock(int index) {  return firstDegreeAvBlock.get(index);  }
    public void addFirstDegreeAvBlock(String value) { firstDegreeAvBlock.add(value); }


    // Complete ArrayList getters
    public ArrayList<String> getTachycardia() {
        return tachycardia;
    }

    public ArrayList<String> getBradycardia() {
        return bradycardia;
    }

    public ArrayList<String> getPac() {
        return pac;
    }

    public ArrayList<String> getVpc() {
        return vpc;
    }

    public ArrayList<String> getVt() {
        return vt;
    }

    public ArrayList<String> getSvt() {
        return svt;
    }

    public ArrayList<String> getEscapeBeat() {
        return escapeBeat;
    }

    public ArrayList<String> getPause() {
        return pause;
    }

    public ArrayList<String> getJunctionalTachycardia() {
        return junctionalTachycardia;
    }

    public ArrayList<String> getPrematureJunctionalRhythm() {
        return prematureJunctionalRhythm;
    }

    public ArrayList<String> getMobitzType1AVBlock() {
        return mobitzType1AVBlock;
    }

    public ArrayList<String> getMobitzType2AVBlock() {
        return mobitzType2AVBlock;
    }

    public ArrayList<String> getPreExcitationSyndrome() {
        return preExcitationSyndrome;
    }

    public ArrayList<String> getIdioventricularRhythm() {
        return idioventricularRhythm;
    }

    public ArrayList<String> getAcceleratedIdioventricularRhythm() {
        return acceleratedIdioventricularRhythm;
    }

    public ArrayList<String> getJunctionalRhythm() {
        return junctionalRhythm;
    }

    public ArrayList<String> getAtrialFlutter() {
        return atrialFlutter;
    }

    public ArrayList<String> getAtrialTachycardia() {
        return atrialTachycardia;
    }

    public ArrayList<String> getAtrialFibrillation() {
        return atrialFibrillation;
    }

    public ArrayList<String> getCompleteHeartBlock() {
        return completeHeartBlock;
    }

    public ArrayList<String> getSinusArrhythmia() {
        return sinusArrhythmia;
    }

    public ArrayList<String> getSinusRhythm() {
        return sinusRhythm;
    }

    public ArrayList<String> getMultiPClassification() {
        return multiPClassification;
    }

    public ArrayList<String> getFirstDegreeAvBlock() {
        return firstDegreeAvBlock;
    }



    // Size of classificationResult is calculated by size of tachycardia length
    public int size() {
        return tachycardia.size();
    }

    // display
    public void display(ArrayList<String> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }


}
