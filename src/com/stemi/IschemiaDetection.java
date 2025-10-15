package com.stemi;

import com.stemi.dataClasses.CardiacStruct;
import com.stemi.dataClasses.IschemiaData;
import com.stemi.dataClasses.StemiCalculationData;
import com.stemi.dataClasses.TerritoryBasedIschemiaRecord;
import com.stemi.libs.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IschemiaDetection {

    public IschemiaData detectIschemia(CardiacStruct cardiacStruct, double fs, StemiCalculationData stemiData, Utility ut) {
        //  ------- Lead label → territory mapping ----------
        ArrayList<String> territory = new ArrayList<>();
        boolean hasJ60 = false;
        boolean hasJ80 = false;
        double stDepressionThreshold = -0.15;  //   ST depression threshold
        double stFlatBand = 0.02;   //  horizontal band
        double downSlopeBand = -0.02;   //  downsloping band
        double tInverseThreshold = -0.13;    //  significant T inversion
        ArrayList<String> slope = new ArrayList<>();
        ArrayList<Boolean> stDepressionFlag = new ArrayList<>();
        ArrayList<Boolean> stHorizOrDownFlag = new ArrayList<>();
        ArrayList<Boolean> tInverssionFlag = new ArrayList<>();
        boolean[] stPersistent = new boolean[stemiData.getLeadName().size()];
        boolean[] stPersistentStrong = new boolean[stemiData.getLeadName().size()];
        boolean[] tPersistentInverse = new boolean[stemiData.getLeadName().size()];
        for (int i = 0; i < stemiData.getLeadName().size(); i++) {
            String leadName = stemiData.getLeadName(i);
            if (leadName.equalsIgnoreCase("V1") || leadName.equalsIgnoreCase("V2"))
                territory.add("Septal");
            else if (leadName.equalsIgnoreCase("V3") || leadName.equalsIgnoreCase("V4"))
                territory.add("Anterior");
            else if (leadName.equalsIgnoreCase("V5") || leadName.equalsIgnoreCase("V6") ||
                    leadName.equalsIgnoreCase("Lead1"))
                territory.add("Lateral");
            else if (leadName.equalsIgnoreCase("Lead2") || leadName.equalsIgnoreCase("Lead3"))
                territory.add("Inferior");
            else
                territory.add("Unknown");
            //  ---------- Beat-by-beat rule thresholds ----------
            //Classify slope if both J60/J80 are present:
            if (hasJ60 && hasJ80) {
                // Not Updated as hasJ60 and hasJ80 both are not defined


            } else {
                slope.add("Unknown");
            }
            // Flags
            stDepressionFlag.add(stemiData.getsTElevationMv(i) <= stDepressionThreshold );
            stHorizOrDownFlag.add(  stDepressionFlag.get(i) && (slope.get(i).equalsIgnoreCase("horizontal") ||
                    slope.get(i).equalsIgnoreCase("downsloping") || slope.get(i).equalsIgnoreCase
                    ("Unknown")) );
            tInverssionFlag.add(stemiData.getTAmplitude(i) <= tInverseThreshold);
            //  -------- Beat-by-beat vs Persistent check ----------
            if (!stemiData.getSTElevationMv().isEmpty()) {
                //  mean(ST_all <= -0.15, 'omitnan') > 0.5;
                //  perLead.ST_Persistent(i) = mean(ST_all <= -0.15, 'omitnan') > 0.5;
                stPersistent[i] = ut.mean( stemiData.getSTElevationMv().stream().mapToInt( x ->
                        (x <= -0.15) ? 1 : 0).toArray()) > 0.5;
                stPersistentStrong[i] = ut.mean( stemiData.getSTElevationMv().stream().mapToInt( x ->
                        (x <= -0.15) ? 1 : 0).toArray()) > 0.5;
            }
            if (! stemiData.getTAmplitude().isEmpty())
                tPersistentInverse[i] = ut.mean( stemiData.getTAmplitude().stream().mapToInt( x ->
                        (x <= -0.20) ? 1 : 0).toArray()) > 0.5;
        }

        //  ------ Territory-level persistence ---------
        String[] territoriesName = {"Septal","Anterior","Lateral","Inferior"};
        boolean[] terrSuspicious = new boolean[territoriesName.length];
        for (int i = 0; i < territoriesName.length; i++) {
            String name = territoriesName[i];
            int[] mask = territory.stream().mapToInt(x -> x.equalsIgnoreCase(name) ? 1 : 0 ).toArray();
            int nSt = IntStream.range(0, mask.length).filter(a -> mask[a] == 1).map(a -> (stPersistent[a]) ? 1 : 0).sum();
            int nSts = IntStream.range(0, mask.length).filter(a -> mask[a] == 1).map(a -> (stPersistentStrong[a]) ? 1 : 0).sum();
            int nT = IntStream.range(0, mask.length).filter(a -> mask[a] == 1).map(a -> (tPersistentInverse[a]) ? 1 : 0).sum();
            if (nSt >= 2 && nT >= 1)
                terrSuspicious[i] = true;
            else if (nSts >= 2 && nT >= 1) {
                terrSuspicious[i] = true;
            }
        }
        boolean globalSuspicious = IntStream.range(0, terrSuspicious.length).anyMatch(i -> terrSuspicious[i]);
        int nLeadStDepression = (int) stDepressionFlag.stream().filter(Boolean::booleanValue).count();
        int nLeadsTInverse = (int) tInverssionFlag.stream().filter(Boolean::booleanValue).count();
        HashMap<String, Boolean> territoryCounts = new HashMap<>();
        territoryCounts.put("Septal", terrSuspicious[0]);
        territoryCounts.put("Anterior", terrSuspicious[1]);
        territoryCounts.put("Lateral", terrSuspicious[2]);
        territoryCounts.put("Inferior", terrSuspicious[3]);

        IschemiaData ischemiaData = new IschemiaData();
        ischemiaData.setStemiData(stemiData);
        ischemiaData.setTerritory(territory);
        ischemiaData.setStSlope(slope);
        ischemiaData.setStDepressionFlag(stDepressionFlag);
        ischemiaData.setStHorizOrDownFlag(stHorizOrDownFlag);
        ischemiaData.setTInverssionFlag(tInverssionFlag);
        ischemiaData.setStPersistent(stPersistent);
        ischemiaData.setStPersistentStrong(stPersistentStrong);
        ischemiaData.setTPersistentInverse(tPersistentInverse);
        ischemiaData.setTerritoryCounts(territoryCounts);
        ischemiaData.setNLeadStDepression(nLeadStDepression);
        ischemiaData.setNLeadsTInverse(nLeadsTInverse);
        ischemiaData.setSuspicious( nLeadStDepression > 2 || nLeadsTInverse >= 1 );
        ischemiaData.setGlobalSuspicious(globalSuspicious);
        // Summary
        if (globalSuspicious)
            ischemiaData.setIschemiaSummary("Definite ischemia (persistent across strip");
        else if (ischemiaData.isSuspicious())
            ischemiaData.setIschemiaSummary("Possible ischemia (transient/beat-level)");
        else
            ischemiaData.setIschemiaSummary("No ischemia flagged");
        return ischemiaData;
    }


    public String classifyIschemia(IschemiaData ischemiaData, ArrayList<Double> stElevationMv) {
        ArrayList<String>  uniqueTerritories = ischemiaData.getTerritory().stream().distinct().collect(Collectors.toCollection(ArrayList::new));
        ArrayList<TerritoryBasedIschemiaRecord>  uniqueTerritoriesAllDataList = new ArrayList<>();
        double sTDeepThreshold = -0.16;
        for (int i = 0; i < uniqueTerritories.size(); i++) {
            ArrayList<Integer> matchingIndices = new ArrayList<>();
            for (int j = 0; j < ischemiaData.getTerritory().size(); j++) {
                if (uniqueTerritories.get(i).equalsIgnoreCase(ischemiaData.getTerritory().get(j)))
                    matchingIndices.add(j);
            }
            int nDepressedSum = 0 , nHorizDownDepressedSum = 0, nTInversionFlagSum = 0;
            boolean flag = true, hasDeppDepressed = false;
            for (int j = 0; j < matchingIndices.size(); j++) {
                if (ischemiaData.getStDepressionFlag().get(matchingIndices.get(j)))
                    nDepressedSum++;
                if (ischemiaData.getStDepressionFlag().get(matchingIndices.get(j)) && ischemiaData.getStHorizOrDownFlag().get(matchingIndices.get(j)))
                    nHorizDownDepressedSum++;
                if ( flag && stElevationMv.get(matchingIndices.get(j)) <= sTDeepThreshold && ischemiaData.getStHorizOrDownFlag().get(matchingIndices.get(j)) )
                {
                    flag = false;
                    hasDeppDepressed = true;
                }
                if (ischemiaData.getTInverssionFlag().get(matchingIndices.get(j)))
                    nTInversionFlagSum++;
            }
            boolean isPositive = (nHorizDownDepressedSum >= 3 || hasDeppDepressed);
            boolean isSupport = (nTInversionFlagSum >= 3);

            uniqueTerritoriesAllDataList.add(new TerritoryBasedIschemiaRecord( uniqueTerritories.get(i),
                            matchingIndices.size(),
                            nDepressedSum,
                            nHorizDownDepressedSum,
                            hasDeppDepressed,
                            nTInversionFlagSum,
                            isPositive,
                            isSupport
                    )
            );
        }
        ArrayList<Integer> isPos = new ArrayList<>();
        ArrayList<Integer> isSupport = new ArrayList<>();
        for (int i = 0; i < uniqueTerritoriesAllDataList.size(); i++) {
            isPos.add(uniqueTerritoriesAllDataList.get(i).getIsPositive()? 1 : 0);
            isSupport.add(uniqueTerritoriesAllDataList.get(i).getIsSupport() ? 1 : 0);
        }
//        ArrayList<Integer> isPos = IntStream.range(0, uniqueTerritoriesAllDataList.size()).mapToObj(i -> ( uniqueTerritoriesAllDataList.get(i).
//                getnHorizDownDepressed() >= 2 || uniqueTerritoriesAllDataList.get(i).isHasDeepDepressed() ) ? 1 : 0).collect(Collectors.toCollection(ArrayList::new));
//        ArrayList<Integer> isSupport = uniqueTerritoriesAllDataList.stream().map(
//                territoryBasedIschemiaRecord -> (territoryBasedIschemiaRecord.getnTInverted()
//                        >=2) ? 1 : 0 ).collect(Collectors.toCollection(ArrayList::new));

        ArrayList<String> primaryTerritories = IntStream.range(0, isPos.size()).filter(i -> (isPos.get(i) == 1)).
                mapToObj(i -> uniqueTerritoriesAllDataList.get(i).getTerritory()).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> supportTerritories = IntStream.range(0, isSupport.size()).filter(i -> (isSupport.get(i) == 1)).
                mapToObj(i -> uniqueTerritoriesAllDataList.get(i).getTerritory()).collect(Collectors.toCollection(ArrayList::new));

        ArrayList<String> primaryLabels = IntStream.range(0, primaryTerritories.size()).mapToObj(i ->
                primaryTerritories.get(i) + " ischemia").collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> supportLabels = IntStream.range(0, supportTerritories.size()).mapToObj(i ->
                supportTerritories.get(i) + " ischemia").collect(Collectors.toCollection(ArrayList::new));
        HashMap<String, Object> finalDecission = new HashMap<>();

        //  Primary territories with counts

        String rationale = "";
        String parts = "";
        if (!primaryTerritories.isEmpty())
        {
            for (int i = 0; i < primaryTerritories.size(); i++) {
                for (int j = 0; j < uniqueTerritoriesAllDataList.size(); j++) {
                    if (uniqueTerritoriesAllDataList.get(j).getTerritory().equalsIgnoreCase(primaryTerritories.get(i))) {
                        parts += primaryTerritories.get(i) + " : " + uniqueTerritoriesAllDataList.get(j).getnHorizDownDepressed()
                                + " horiz/down depressed ";
                        if (uniqueTerritoriesAllDataList.get(j).isHasDeepDepressed())
                            parts += ", deep ≤ -0.10 mV present";
                        if (uniqueTerritoriesAllDataList.get(j).getnTInverted() >= 3)
                            parts += ", T-inv = "+uniqueTerritoriesAllDataList.get(j).getnTInverted();
                    }
                }
                if (i < primaryTerritories.size()-1)
                    parts += "\n";
            }
        }
        // % Support-only territories (that weren’t primary)
        if (!supportTerritories.isEmpty()) {

            //support_only = setdiff(string(support_territories), string(primary_territories));
            //for i = 1:numel(support_only)
            //    terr = support_only(i);
            //    r = T(strcmp(T.Territory, terr), :);
            //    seg = terr + sprintf(" (support): T-inv=%d", r.N_T_Inverted);
            //    parts(end+1,1) = seg;
            //end

        }
        if (parts.isEmpty())
            rationale = "No ischemia criteria met.";
        else {
            //  s = "Evidence -> " + strjoin(parts, " | ");
            rationale = "Evidence -> "+parts;
        }
        //  % ---------- Summary line ----------
        //if isempty(primary_labels)
        //    class_summary = "No ischemia pattern flagged.";
        //else
        //    class_summary = "Ischemia suspected: " + join(primary_labels, ", ");
        //    if ~isempty(support_labels)
        //        class_summary = class_summary + " | Support: T-wave inversion in " + join(support_labels, ", ");
        //    end
        //end
        String classSumarry = "";
        if (primaryLabels.isEmpty())
            classSumarry = "No ischemia pattern flagged.";
        else {
            classSumarry = "Ischemia suspected: " + String.join(", ", primaryLabels);
            if (!supportLabels.isEmpty())
                classSumarry += " | Support: T-wave inversion in " +String.join(", ", supportLabels);
        }
        System.out.println("---------------- Classify Ischemia Results -------------");
        System.out.println(classSumarry);
        return ischemiaDecissionMaker(primaryLabels, supportLabels, finalDecission,uniqueTerritoriesAllDataList, uniqueTerritories, rationale);
    }

    private String ischemiaDecissionMaker(ArrayList<String> primaryLabels, ArrayList<String> supportLabels,
                                        HashMap<String, Object> finalDecission, ArrayList<TerritoryBasedIschemiaRecord>
                                                uniqueTerritoriesAllDataList, ArrayList<String> uniqueTerritories, String rationale) {
        String[] territoryArray = {"Septal","Anterior","Lateral","Inferior","Unknown"};
        for (int i = 0; i < territoryArray.length; i++) {
            String terr = territoryArray[i];
            for (int j = 0; j < primaryLabels.size(); j++) {
                if (terr.equalsIgnoreCase(primaryLabels.get(j) + " ischemia")) {
                    finalDecission.put("has" + terr.substring(0, 1).toUpperCase(), 1);
                    break;
                }
            }
        }
        finalDecission.put("hasAnyPrimary", !primaryLabels.isEmpty());
        finalDecission.put("hasAnySupport", !supportLabels.isEmpty());
        finalDecission.put("status", "no-data");
        if ( (boolean) finalDecission.get("hasAnyPrimary"))
            finalDecission.put("status", "ischemia-suspected");
        else if ( (boolean) finalDecission.get("hasAnySupport"))
            finalDecission.put("status", "possible-ischemia-support-only");
        else
            finalDecission.put("status", "no-ischemia-flagged");


        String resultStr = "";
        if (((String) finalDecission.get("status")).equalsIgnoreCase(("ischemia-suspected"))) {
            String x = "Ischemia suspected";
            x += "\n Primary territories: ";
            resultStr = resultStr +String.join(", ", primaryLabels);
            if (((boolean)finalDecission.get("hasAnySupport")))
                resultStr += " Supportive evidence (T-wave inversion): " + String.join(", ", supportLabels);
        }
        else if (((String) finalDecission.get("status")).equalsIgnoreCase(("possible-ischemia-support-only"))) {
            resultStr += " Possible ischemia (supportive evidence only) ";
            resultStr += "Supportive territories (T-wave inversion): " + String.join(", ",
                    supportLabels.stream().distinct().collect(Collectors.toList()));
        }
        else {
            resultStr += "No ischemia pattern flagged.";
        }
        if (!uniqueTerritoriesAllDataList.isEmpty()) {
            for (int i = 0; i < uniqueTerritories.size(); i++) {
                resultStr += " \n "+uniqueTerritoriesAllDataList.get(i).getTerritory()+" : horiz/down depressed= " +
                        uniqueTerritoriesAllDataList.get(i).getnHorizDownDepressed() + " , deep <= -0.10mV = "+
                        (uniqueTerritoriesAllDataList.get(i).isHasDeepDepressed()? "Yes" : "No") + ", T-inv= " +
                        uniqueTerritoriesAllDataList.get(i).getnTInverted();
            }
        }
        //  if strlength(rationale) > 0
        //    lines(end+1) = "Evidence -> " + rationale;
        //end
        if (!rationale.isEmpty())
            resultStr += " \n \n" + rationale;
//        System.out.println("------------------------- Ischemia Final Result --------------------------------");
//        System.out.println(resultStr);
        return resultStr;
    }


}
