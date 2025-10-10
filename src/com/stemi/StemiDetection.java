package com.stemi;

import com.stemi.dataClasses.*;
import com.stemi.libs.Utility;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StemiDetection {

    public void detectStemi(CardiacStruct cardiacStruct, double fs, OnResultCompleteListener onResultCompleteListener,
                            TwelveLeadEcgData twelveLeadEcgData, HashMap<String, Double> map, Utility ut) {
        StemiCalculationData stemiData = detectStemiPrivate(cardiacStruct, ut);
        HashMap<String, Object> stemiResults = classifyStemi(stemiData);
        stemiDecissionMaker(stemiResults);
        IschemiaDetection ischemiaDetection = new IschemiaDetection();
        IschemiaData ischemiaData = ischemiaDetection.detectIschemia(cardiacStruct, fs, stemiData, ut);
        ischemiaDetection.classifyIschemia(ischemiaData, stemiData.getSTElevationMv());
    }


    private void stemiDecissionMaker(HashMap<String, Object> stemiResults) {
        String bbb = (String) stemiResults.get("bbb");
        String bbbReason = (String) stemiResults.get("bbbReason");
        if (bbb.equalsIgnoreCase("None") && bbbReason.isEmpty())
            bbbReason = "QRS pattern consistent with bundle branch block";
        //  ---------- gather STEMI evidence ----------
        boolean tombstonePresent = false;
        //Territory hints (any one of these may exist)
        ArrayList<String> territoryList = (ArrayList<String>) stemiResults.get("territoryLabels");
        int[] counts = (int[]) stemiResults.get("counts");
        int nElev = -1;
        String topLabel = "";
        if (!territoryList.isEmpty())
            topLabel = territoryList.get(0);
        else {
            String[] cand = {"Septal", "Anterior", "Lateral", "Inferior", "Anteroseptal","Anterolateral","Inferolateral"};
            String bestName = ""; int bestVal = -1;
            if (cand.length == counts.length) {
                for (int i = 0; i < cand.length; i++) {
                    if (counts[i] > bestVal ) {
                        bestVal = counts[i];
                        bestName = cand[i];
                    }
                }
                if (bestVal > 1)
                    topLabel = bestName + " MI";
            }
        }
        // If still empty but strong evidence exists, pick a generic MI:
        if (topLabel.isEmpty() && (tombstonePresent || nElev >= 2))
            topLabel = "ST-Elevation MI ";
        if (topLabel.isEmpty())
            topLabel = "No STEMI";

        String[] normMap = {"anterior","anteroseptal","anterolateral","inferior","inferolateral","lateral"};
        String[] normMapValue = {"Anterior Wall MI","Anteroseptal MI","Anterolateral MI","Inferior MI","Inferolateral MI","Lateral MI"};
        for (int i = 0; i < normMap.length; i++) {
            if (topLabel.equalsIgnoreCase(normMap[i]))
                topLabel = normMapValue[i];
        }
        //  ---------- derive STRENGTH ----------
        String topStrength = "";
        if (topStrength.isEmpty()) {
            if (tombstonePresent)
                topStrength = "DEFINITE";
            else if (nElev != -1) {
                if ( nElev >= 2)
                    topStrength = "Probable";
                else if ( nElev == 1)
                    topStrength = "Possible";
                else
                    topStrength = "None";
            }
            else {
                //  fallback: check keywords in rationale
                String rat = ((String) stemiResults.get("summary")).toLowerCase();
                rat = rat.toLowerCase();
                if (rat.contains("definite"))
                    topStrength = "DEFINITE";
                else if (rat.contains("probable") || rat.contains("suggest"))
                    topStrength = "Probable";
                else if (rat.contains("possible"))
                    topStrength = "Possible";
                else
                    topStrength = "Possible";
            }
        }
        //  Special guard: if label ended as "No STEMI", force strength to "None"
        if (topLabel.equalsIgnoreCase("No Stemi"))
            topStrength = "None";
        //---------- compose text ----------
        String mainLine = "Final STEMI decision: ";
        if (bbb.equalsIgnoreCase("LBBB") && !tombstonePresent && topStrength.equalsIgnoreCase
                ("DEFINITE") && topLabel.equalsIgnoreCase("No Stemi"))
            mainLine += "INDETERMINATE due to LBBB — " + topLabel + " , " + topStrength;
        else
            mainLine += topStrength +" , "+ topLabel;

        //  BBB line (no ternary operator)
        String bbLine = "";
        if ( ! bbb.equalsIgnoreCase("None")) {
            bbLine = " | BBB: " +bbb;
            if (!bbbReason.isEmpty())
                bbLine += " - "+bbbReason;
        }
        // Tombstone line
        String tombLine = "";
        if (tombstonePresent)
            tombLine = " | Tombstone morphology present";
        String finalText = "";
        finalText = mainLine + bbLine + tombLine;
        finalText +=  "\n Rationale → " + (String) stemiResults.get("summary");
        System.out.println("----------------------------- Final Stemi Decission --------------------------------");
        System.out.println(finalText);
    }

    private HashMap<String, Object> classifyStemi(StemiCalculationData stemiData) {
        HashMap<String, Object> results = new HashMap<>();
        int[] allContigCounts = stemiData.getAllContigCounts();
        int septalCounts = allContigCounts[0];
        int anteriorCounts = allContigCounts[1];
        int lateralCounts = allContigCounts[2];
        int inferiorCounts = allContigCounts[3];
        int anteroSeptalCounts = allContigCounts[4];
        int anteroLateralCounts = allContigCounts[5];
        int inferoLateralCounts = allContigCounts[6];
//        results.put("territoryScores", allContigCounts);
        results.put("counts", allContigCounts);
        ArrayList<Integer> tomb = stemiData.getTombstoneStrong();
        int tombSeptal = (septalCounts == 1 && tomb.get(0) == 1) ? 1 : 0;
        int tombAnterior = (anteriorCounts == 1 && tomb.get(1) == 1) ? 1 : 0;
        int tombLateral = (lateralCounts == 1 && tomb.get(2) == 1) ? 1 : 0;
        int tombInferior = (inferiorCounts == 1 && tomb.get(3) == 1) ? 1 : 0;
        int tombAnteroSeptal = (anteroSeptalCounts == 1 && tomb.get(4) == 1) ? 1 : 0;
        int tombAnteroLateral = (anteroLateralCounts == 1 && tomb.get(5) == 1) ? 1 : 0;
        int tombInferoLateral = (inferoLateralCounts == 1 && tomb.get(6) == 1) ? 1 : 0;
        //  Strong categories (≥2 elevated in that group)
        ArrayList<String> territoryLabels = new ArrayList<String>();
        if (anteroSeptalCounts >= 2) {
            territoryLabels.add("Anteroseptal MI " + tombAnteroSeptal + " ");
        }
        else {
            if (septalCounts >= 2)
                territoryLabels.add("Septal MI "+ tombSeptal +" ");
            if (anteriorCounts >= 2)
                territoryLabels.add("Anterior MI " +tombAnterior +" ");
        }

        if (anteroLateralCounts >= 2)
            territoryLabels.add("Anterolateral MI " + tombLateral +" ");
        else {
            if (lateralCounts >= 2)
                territoryLabels.add("Lateral MI " +tombLateral+" ");
        }

        //  Inferior: only lead II → be conservative
        if (inferiorCounts >= 1) {
            //    % If also lateral elevated → inferolateral
            if (inferoLateralCounts >= 2)
                territoryLabels.add("Inferolateral MI " + tombInferoLateral + " ");
            else
                territoryLabels.add("Possible Inferior MI " + tombLateral + " || " + tombInferior + " ");
        }

        //  If nothing matched but global STEMI suspicion true, give a generic label
        if (territoryLabels.isEmpty() && stemiData.getSuspicious() == 1)
            territoryLabels.add("STEMI pattern (territory indeterminate)");
        //  Use median QRS across all leads (robust)
        results.put("territoryLabels", territoryLabels);
        Utility ut = new Utility();
        double qrsMedian = ut.medianDouble(stemiData.getQrsDuration());
        String bbb = "None";
        String bbbReason = "";
        boolean wideQrs = (qrsMedian >= 120);
        //  RBBB heuristic: wide QRS AND relatively prominent R (or rsR') in V1 and deeper S in V6
        boolean isRbbbLike = wideQrs && (stemiData.getRAmplitude(3) > 0) && (stemiData.getSAmplitude(8) < 0);
        //   LBBB heuristic: wide QRS AND broad/positive R in I or V6, small/absent Q in I, and deep S in V1
        boolean isLbbbLike = wideQrs && (stemiData.getRAmplitude(0) > 0) || (stemiData.getRAmplitude(8) >0)
                && (stemiData.getQAmplitude(0) >= -0.02) && (stemiData.getSAmplitude(3) < 0);

        if (isLbbbLike && !isRbbbLike) {
            bbb = "LBBB";
            bbbReason = "QRS≥120 ms, broad/positive R in I/V6, deep S in V1, absent/small Q in I.";
        }
        else if (isRbbbLike && !isLbbbLike) {
            bbb = "RBBB";
            bbbReason = "QRS≥120 ms, prominent R/rsR' in V1, deep S in V6.";
        }
        else if ( wideQrs && (isRbbbLike || isLbbbLike) ) {
            bbb = "Possible LBBB/RBBB";
            bbbReason = "Wide QRS with mixed or incomplete amplitude patterns.";
        }
        //  MI territory line
        String parts = "";
        if (!territoryLabels.isEmpty())
            parts = "MI territory: " + territoryLabels+" ; ";
        else
            parts = "MI territory: none assigned"+" ; ";
        parts += "Counts — Septal:"+ septalCounts +"  Anterior:"+ anteriorCounts +"  Lateral:"+ lateralCounts +"  " +
                "Anteroseptal:"+ anteroSeptalCounts +"  Anterolateral:"+ anteroLateralCounts +"  " +
                "Inferior:"+ inferiorCounts + "  Inferolateral:"+ inferoLateralCounts +" ; ";
        if (bbb.equalsIgnoreCase("None"))
            parts += " BBB: None ; ";
        else {
            parts += bbb +" ; ";
            parts += bbbReason +" ; ";
        }
        results.put("bbb", bbb);
        results.put("bbbReason", bbbReason);
        results.put("summary", parts);
        return results;
    }

    private StemiCalculationData detectStemiPrivate(CardiacStruct cardiacStruct, Utility ut) {
        StemiCalculationData stemiData = new StemiCalculationData();
        String[] leadAlias = {"Lead1", "Lead2", "Lead3", "V1", "V2", "V3", "V4", "V5", "V6"};
        for (int i = 0; i < 9; i++) {
            CardiacData data = cardiacStruct.getLeadWiseCardiacStructLeadRangeFrom0To8(i);
            stemiData.addLeadName(leadAlias[i]);
            stemiData.addsTElevationMv( ut.median(data.getStElevation().stream().mapToDouble(Double::doubleValue).toArray()));
            stemiData.addSagittaMv(ut.median(data.getStSagitaMv().stream().mapToDouble(Double::doubleValue).toArray()));
            stemiData.addStMorphCode((int) ut.median(data.getStMorphologyCode()));
            stemiData.addTombstoneFlag(ut.medianBoolean(data.getStTombstoneFlag()));
            stemiData.addQrsDuration(ut.median(data.getQrsDuration()));
            stemiData.addTAmplitude( ut.findMean(data.getTAmplitudeMv()) );
            stemiData.addRAmplitude( ut.median(data.getRAmplitudeMv().stream().mapToDouble(Double::doubleValue).toArray()) );
            stemiData.addSAmplitude( ut.median(data.getSAmplitudeMv().stream().mapToDouble(Double::doubleValue).toArray()) );
            stemiData.addQAmplitude( ut.median(data.getQAmplitudeMv().stream().mapToDouble(Double::doubleValue).toArray()));


        }
        //  Territory groups with available labels
        String[] septal = {"V1","V2"};
        String[] anterior = {"V3","V4"};
        String[] lateral = {"Lead1","V5","V6"};
        String[] inferior = {"Lead2","Lead3"};
        String[] preCordialLeads = {"V1","V2","V3","V4","V5","V6"};
        double[] thr = new double[stemiData.getLeadName().size()];
        for (int i = 0; i < stemiData.getLeadName().size(); i++) {
            if (stemiData.getLeadName(i).equalsIgnoreCase(preCordialLeads[0]) || stemiData.getLeadName(i).
                    equalsIgnoreCase(preCordialLeads[1]) || stemiData.getLeadName(i).equalsIgnoreCase(
                    preCordialLeads[2]) || stemiData.getLeadName(i).equalsIgnoreCase(preCordialLeads[3]) ||
                    stemiData.getLeadName(i).equalsIgnoreCase(preCordialLeads[4]) || stemiData.getLeadName(i).
                    equalsIgnoreCase(preCordialLeads[5])
            )
               thr[i] = 0.2;
            else
               thr[i] = 0.1;
        }
        int[] stElevationFlag = IntStream.range(0, stemiData.getLeadName().size()).map(i-> (stemiData.
                getsTElevationMv(i) >= thr[i]) ? 1:0).toArray();
        int[] stConcaveDown = IntStream.range(0, stemiData.getLeadName().size()).map(i-> ((
                stemiData.getStMorphCode(i) == -1) || (stemiData.getSagittaMv(i) < -0.05)) ? 1 : 0).toArray();
        int[] tombstoneStrong = IntStream.range(0, stemiData.getLeadName().size()).map(i->
                (stemiData.getTombstoneFlag(i) >= 1) ? 1 : 0 ).toArray();
        int septalContigCounts = stElevationFlag[3] + stElevationFlag[4];
        int anteriorContigCounts = stElevationFlag[5] + stElevationFlag[6];
        int lateralContigCounts = stElevationFlag[0] + stElevationFlag[7] + stElevationFlag[8];
        int inferiorContigCounts = stElevationFlag[1] + stElevationFlag[2];
        int anteroSeptalContigCounts = stElevationFlag[3] + stElevationFlag[4] + stElevationFlag[5] + stElevationFlag[6];
        int anteroLateralContigCounts = stElevationFlag[5] + stElevationFlag[6] + stElevationFlag[7] + stElevationFlag[8]
                + stElevationFlag[0];
        int inferoLateralContigCounts = stElevationFlag[1] + stElevationFlag[7] + stElevationFlag[8] + stElevationFlag[0];
        int[] primaryContigCounts = {septalContigCounts, anteriorContigCounts, lateralContigCounts, inferiorContigCounts};
        int[] allContigCounts = {septalContigCounts, anteriorContigCounts, lateralContigCounts, inferiorContigCounts,
                anteroSeptalContigCounts, anteroLateralContigCounts, inferoLateralContigCounts};
        // territoryFlags
        int septalTerritoryFlags = (septalContigCounts >= 2) ? 1 : 0;
        int anteriorTerritoryFlags = (anteriorContigCounts >= 2) ? 1 : 0;
        int lateralTerritoryFlags = (lateralContigCounts >= 2) ? 1 : 0;
        int inferiorTerritoryFlags = (inferiorContigCounts >= 2) ? 1 : 0;
        int[] allTerritoryFlags = {septalTerritoryFlags, anteriorTerritoryFlags, lateralTerritoryFlags , inferiorTerritoryFlags};
        int nElevContiguous = ut.findMax(primaryContigCounts);
        int anyTerritoryContiguous = ut.findIfAnyNonZero(primaryContigCounts);
        //Global STEMI heuristic:
        //% (A) ≥2 elevated in any one territory (contiguity), or
        //% (B) any elevated lead with strong tombstone morphology
        int anyTombstoneElev = 0;
        for (int i = 0; i < stElevationFlag.length; i++) {
            if (stElevationFlag[i] == 1 && tombstoneStrong[i] == 1)
                anyTombstoneElev = 1;
        }
        int suspicious = (anyTerritoryContiguous == 1 || anyTombstoneElev == 1) ? 1 : 0;
        for (int i = 0; i < stElevationFlag.length; i++) {
            stemiData.addStElevationFlag(stElevationFlag[i]);
            stemiData.addStConcaveDown(stConcaveDown[i]);
            stemiData.addTombstoneStrong(tombstoneStrong[i]);
        }
        stemiData.setNumberOfLeadsElevated(Arrays.stream(stElevationFlag).sum());
        stemiData.setAnyTombstoneElev( anyTombstoneElev);
        stemiData.setSuspicious(suspicious);
        stemiData.setTerritoryCounts(primaryContigCounts);
        stemiData.setTerritoryContiguous(allTerritoryFlags);
        stemiData.setAllContigCounts(allContigCounts);
        String summary = "";
        if (suspicious == 1)
            summary += "STEMI suspected ";
        else
            summary += "No STEMI pattern flagged; ";
        summary += "Contiguous-elevated (max territory): "+nElevContiguous+"; ";
        if (anyTombstoneElev == 1)
            summary += "Tombstone morphology present; ";
        summary += "ST ref: ST_Elevation_mV; ";
        stemiData.setSummary(summary);
        return  stemiData;

    }
}
