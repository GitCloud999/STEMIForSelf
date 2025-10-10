package com.stemi;

import com.stemi.dataClasses.TwelveLeadEcgData;

public class ServiceClass {

    public void startSignalProcessing(TwelveLeadEcgData twelveLeadEcgData, OnResultCompleteListener onResultCompleteListener) {
        SignalProcessing sp = new SignalProcessing();
        try {
            double[][] mainArray = new double[9][twelveLeadEcgData.getLead1().size()];
            for (int j = 0; j < twelveLeadEcgData.getLead1().size(); j++) {
                mainArray[0][j] = twelveLeadEcgData.getLead1().get(j);
                mainArray[1][j] = twelveLeadEcgData.getLead2().get(j);
                mainArray[2][j] = twelveLeadEcgData.getLead3().get(j);
                mainArray[3][j] = twelveLeadEcgData.getV1().get(j);
                mainArray[4][j] = twelveLeadEcgData.getV2().get(j);
                mainArray[5][j] = twelveLeadEcgData.getV3().get(j);
                mainArray[6][j] = twelveLeadEcgData.getV4().get(j);
                mainArray[7][j] = twelveLeadEcgData.getV5().get(j);
                mainArray[8][j] = twelveLeadEcgData.getV6().get(j);
            }
            sp.filterProcessing(mainArray, onResultCompleteListener);
        } catch (Exception exp) {
            exp.printStackTrace();
            onResultCompleteListener.onFailed(exp.getMessage());
        }
    }
}
