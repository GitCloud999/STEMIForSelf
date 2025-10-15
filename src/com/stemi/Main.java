package com.stemi;

import com.stemi.dataClasses.AllCalculatedDataNew;
import com.stemi.dataClasses.TwelveLeadEcgData;
import com.stemi.libs.FileWriterPointDetection;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
//        String fldrNew = "C:\\Users\\sabyr\\OneDrive\\Desktop\\Rahul\\Spandan_ultra_12L_filter_december_2024\\NewCases\\Distortion and signal processing case\\rawFiles\\Results_AfterVersion3_ClinicalTesting_Testing\\RawMatlabData\\9ColumnData\\";
        String fldrNew = "D:\\RAHUL\\DownloadsRahul\\dataSets250Hz_9ColumnDataSeperatedWithSpace\\";
//        String fldrNew = "D:\\RAHUL\\DownloadsRahul\\dataSets250Hz_9ColumnDataSeperatedWithSpace\\HumanTrials\\";
//        String fldrNew = "C:\\Users\\sabyr\\Downloads\\Spandan_ultra_12L_arrhythmia_code_10feb_2025\\data_sets";
//        String fldrNew = "D:\\RAHUL\\DownloadsRahul\\dataSets250Hz_9ColumnData\\";

//        String fileName = "1722569222110000_raw.txt";
//        String fileName = "1722573078570000_raw.txt";
//        String fileName = "ANE_20000_250hz_raw.txt";
//        String fileName = "ANE_20001_250hz_raw.txt";
//        String fileName = "ANE_20002_250hz_raw.txt";
//        String fileName = "CAL_05000_250hz_raw.txt";
//        String fileName = "CAL_10000_250hz_raw.txt";
//        String fileName = "CAL_15000_250hz_raw.txt";
        String fileName = "CAL_20000_250hz_raw.txt";
//        String fileName = "CAL_20002_250hz_raw.txt";

//        String fileName = "CAL_20100_250hz_raw.txt";
//        String fileName = "CAL_20110_250hz_raw.txt";
//        String fileName = "CAL_20160_250hz_raw.txt";
//        String fileName = "CAL_20200_250hz_raw.txt";
//        String fileName = "CAL_20210_250hz_raw.txt";
//        String fileName = "CAL_20260_250hz_raw.txt";
//        String fileName = "CAL_20500_250hz_raw.txt";
//        String fileName = "CAL_20502_250hz_raw.txt";
//        String fileName = "CAL_30000_250hz_raw.txt";
//        String fileName = "CAL_40000_250hz_raw.txt";
//        String fileName = "CAL_50000_250hz_raw.txt";

//        String fileName = "nsr_CONTEC-sIMULATOR .txt";
//        String fileName = "CAL_20000_250hz_raw.txt";
//        String fileName = "CAL_20100_250hz_raw.txt";
//        String fileName = "CAL_20200_250hz_raw.txt";
//        String fileName = "Cal_50000_raw.txt";
//        String fileName = "CAL_20100_250hz_raw.txt";
//        String fileName = "ANE_20000_250hz_raw.txt";
//        String fileName = "CAL_10000_250hz_raw.txt";
//        String fileName = "CAL_20000_250hz_raw.txt";
//        String fileName = "036_50hz_noise_raw.txt";
//        String fileName = "CAL50000_50Vurms_raw.txt";
//        String fileName = "087_with_50Vrms_raw.txt";
//        String fileName = "036_50hz_noise_raw.txt";
//        String fileName = "086_250hz_raw.txt";
//        String fileName = "087_60hz_raw.txt";

//        String fileName = "001_250hz_raw.txt";
//        String fileName = "002_250hz_raw.txt";
//        String fileName = "003_250hz_raw.txt";
//        String fileName = "004_250hz_raw.txt";
//        String fileName = "005_250hz_raw.txt";
//        String fileName = "008_250hz_raw.txt";
//        String fileName = "009_250hz_raw.txt";
//        String fileName = "011_250hz_raw.txt";
//        String fileName = "012_250hz_raw.txt";
//        String fileName = "021_250hz_raw.txt";
//        String fileName = "028_250hz_raw.txt";
//        String fileName = "032_250hz_raw.txt";
//        String fileName = "033_250hz_raw.txt";
//        String fileName = "035_250hz_raw.txt";
//        String fileName = "003_250hz_raw.txt";
//        String fileName = "ANE_20001_250hz_raw.txt";
//        String fileName = "ANE_20002_250hz_raw.txt";
//        String fileName = "ANE_20002_250hz_raw.txt";
//        ------------------- Medanta -------------------
//        String fileName = "Vishal_Medanta_raw.txt";
//        String fileName = "Vikram_Medanta.txt";
//        String fileName = "Shashi_medanta.txt";
//        String fileName = "varinder Medanta.txt";
//        String fileName = "Rajendra Medanta.txt";
//        String fileName = "1745465983740000-Babul-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745400323510000-Faisar-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745467160710000-Pravin-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745464834300000-Shashi-report_twelve_lead (1)_with_replicas.txt";
//        String fileName = "1745465348220000-Sonam-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745467719740000-Somveer-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745552151370000-Christian-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745551722470000-Krishanaly-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745550910860000-Mridula-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745552653690000-Saket-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745638371220000-Anuj-report_twelve_lead (1)_with_replicas.txt";
//        String fileName = "1745636702600000-Bishnu-report_twelve_lead (1)_with_replicas.txt";
//        String fileName = "1745637125880000-Rajendra-report_twelve_lead (1)_with_replicas.txt";
//        String fileName = "1745637570550000-Varinder-report_twelve_lead (1)_with_replicas.txt";
//        String fileName = "1745637993380000-Vikram-report_twelve_lead (1)_with_replicas.txt";
//        String fileName = "1745813841360000-Madhav-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745813045790000-Nancy finda-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745811374930000-Shivani-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745813471830000-Suraj-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745811876780000-Yeshwant-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745900279240000-Bbupendra-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745896276850000-Chandra-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745899423850000-Pabitra-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745897638440000-Suman-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745897126890000-Suman-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745399801150000-Vishal-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745984229720000-Sunil kumar-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745984704240000-Davinder-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745983822840000-Amar-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745986694340000-Manvinder-report_twelve_lead_with_replicas.txt";
//        String fileName = "1745985756230000-Sanjah-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746071340670000-Deepak-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746074113190000-Jagdish-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746074559750000-JS-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746073753680000-Seshmani-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746071664540000-Suchi-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746181206740000-Abdul-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746180530910000-Dhooram-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746243026740000-Madhav-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746243483110000-Gokarna-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746243879470000-Yogesh-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746244291600000-Rajiv kumar-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746246004820000-Rajeev-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746414943240000-Madhur-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746415455570000-Shruti-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746416514210000-kimda-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746417071720000-Rajesh Kumar-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746417545480000-Anita-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746502774760000-Bishal-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746503382180000-Hiteshi-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746504767920000-Ashwini-report_twelve_lead_with_replicas.txt";
//        String fileName = "1746505243750000-Reema-report_twelve_lead_with_replicas.txt";



//        ----------- Clinical -----------
//        String fileName = "1722937370330000_raw.txt";
//        String fileName = "1722937850780000_raw.txt";
//        String fileName = "1722939490630000_raw.txt";
//        String fileName = "1723004044490000_raw.txt";
//        String fileName = "1723004540790000_raw.txt";
//        String fileName = "1723005406780000_raw.txt";
//        String fileName = "1723006570620000_raw.txt";
//        String fileName = "1723010218310000_raw.txt";
//        String fileName = "1723011574640000_raw.txt";
        
        // -------------------- Arrhythmia -----------------------------
//        String fileName ="Afib_fine_raw.txt";
//         String fileName ="Afib_coarse_raw.txt";
//         String fileName ="atrial_flutter_raw.txt";
//         String fileName ="SVT_raw.txt";
//         String fileName ="VT_raw.txt";
//         String fileName ="VT_coarse_raw.txt";
//         String fileName ="VT_fine_raw.txt";
//         String fileName ="Sinus_Arrhythmia_raw.txt";
//         String fileName ="Atrial_tachycardia_raw.txt";
//        String fileName ="Atrial_PAC_raw.txt";
//         String fileName ="nsr_CONTEC-sIMULATOR.TXT";
//         String fileName ="Asystole_raw.txt";
//         String fileName ="PVC_6_min_raw.txt";
//         String fileName ="PVC_1_raw.txt";
//         String fileName ="PVC_12_min_raw.txt";
//         String fileName ="PVC_24_min_raw.txt";
//         String fileName ="Frequent_multifocal_PVC_raw.txt";
//         String fileName ="DynamicRange_raw.txt";
//         String fileName ="Bigeminy_raw.txt";
//         String fileName ="Trigeminy_raw.txt";
//         String fileName ="Paired_PVC_raw.txt";
//         String fileName ="Run_5_PVC_raw.txt";
//         String fileName ="V_FIB_coarse_raw.txt";
//         String fileName ="V_FIB_fine_raw.txt";
//         String fileName ="Atrial_PAC_raw.txt";
//         String fileName ="Nodal_PNC_raw.txt";
//         String fileName ="PVC1_LV_FOCUS_raw.txt";
//         String fileName ="PVC_1_raw.txt";
//         String fileName ="PVC_RV_FOCUS_raw.txt";
//         String fileName ="Multifocal_PVc_raw.txt";
//         String fileName ="First_degree_AVB_raw.txt";
//         String fileName ="Second_degree_AVB_raw.txt";
//         String fileName ="Third_degree_AVB_raw.txt";

        //        ------------------------- Arrhythmia Ver_1.0 Results Analysis --------------------------
//        String fldrNew = "D:\\RAHUL\\DownloadsRahul\\dataSets250Hz_9ColumnDataSeperatedWithSpace\\Arrhythmia_Formatted txt files_9Columns\\";
//        String fileName ="1753430162934690-Atrial fibrillation-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753430308324250-Atrial fibrillation-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753430433042570-Atrial-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753430557327220-Sinus-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753430997773360-Missed-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753431129527100-Atrial-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753431247601290-Nodal-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753431376375950-Supravent-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753431518215660-PVCs 6 per-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753431629744270-PVCs 24 per-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753431763821200-Bigeminy-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753431916336450-Freq-report_twelve_lead (1)_with_replicas.txt";
//        String fileName ="1753432034429310-Trigeminy-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753432172986440-Paired-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753432262675830-Run 5-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753432396585490-Run 11-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753432510361430-Vent-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753432611885830-Vent Fib-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753432737629300-Vent Fib-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753433625688580-Atrial-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753433878291790-Nodal-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753434071819480-1st Degree heart-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753434160330440-2nd degree heart-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753434254437490-3rd degree heart-report_twelve_lead_with_replicas.txt";

        //        -------------------------------- Crash Cases ------------------------------------------
//        String fldrNew = "D:\\RAHUL\\DownloadsRahul\\dataSets250Hz_9ColumnDataSeperatedWithSpace\\Arrhythmia_Formatted txt files_9Columns\\";
//        String fileName ="Srhu_evp_2024_1370_1723532970467_with_replicas.txt";
//        String fileName ="Srhu_evp_2024_1387_1724484294645_with_replicas.txt";
//        String fileName ="Srhu_evp_2024_1387_1724484294645_with_replicas.txt";
//        String fileName ="Srhu_evp_2024_1402_1724732327335_with_replicas.txt";
//        String fileName ="Srhu_evp_2024_1403_1724732888876_with_replicas.txt";
//        String fileName ="Srhu_evp_2024_1407_1724734922442_with_replicas.txt";
//        String fileName ="Srhu_evp_2024_1404_1724733793817_with_replicas.txt";
//        String fileName ="Srhu_evp_2024_1416_1724739633173_with_replicas.txt";
//        String fileName ="Srhu_evp_2024_1494_1724999733904_with_replicas.txt";
//        String fileName ="Srhu_evp_2024_1521_1725082389441_with_replicas.txt";

//        -------------------------------- St Peter's ------------------------------------------
//        String fldrNew = "D:\\RAHUL\\DownloadsRahul\\dataSets250Hz_9ColumnDataSeperatedWithSpace\\Arrhythmia_Formatted txt files_9Columns\\";
//        String fileName ="1755679927807450-Ane-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755684636664650-001-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755689815250980-AFL-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755690140949320-AT-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755696307812770-I01-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755696530161200-I02-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755696661291530-Io3-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755696944128540-Io4-report_twelve_lead (1)_with_replicas.txt";
//        String fileName ="1755697170504550-Io5-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755697280212350-Io6-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755697376397190-Io7-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755697474548570-Io8-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755697568018660-Io9-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755697681773110-I10-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755697783615400-I11-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755697891736840-I12-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755697987954660-I13-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755698080148180-I14-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755698174446790-I15-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755698269443420-I16-report_twelve_lead (1)_with_replicas.txt";
//        String fileName ="1755698269443420-I16-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755698366171560-I17-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755698459720100-I18-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755698573626310-I19-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755698713589570-I20-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755698822846760-I21-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755699007363340-I22-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755699110905500-I23-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755699253826910-I24-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755699365535970-I25-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755700276118320-I26-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755700363770660-I27-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755700464341480-I28-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755700555406630-I29-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755700665677260-I30-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755700757274220-I31-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755700853975920-I32-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755700974983320-I33-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755701073820320-I34-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755701175004440-I35-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755701273611740-I36-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755701379592410-I37-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755701468937220-I38-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755701576940390-I39-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755701698850420-I40-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755701814723790-I41-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755701908773710-I42-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755702034735980-I43-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755702121469550-I44-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755702213890290-I45-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755702359958110-I46-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755702453457970-I47-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755702565046840-I48-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755702658353720-I49-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755702755222720-I50-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755764820267350-I51-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755764968087770-I52-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755768044681460-I52-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755768536328530-I53-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755768746660320-I54-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755768851140170-I55-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755768936406790-I56-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755769017135950-I57-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755769103013800-I58-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755769212667570-I59-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755769293674110-I60-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755769375591350-I61-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755769457441790-I62-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755769546515860-I63-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755769630232450-I64-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755769706093820-I65-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755769794412780-I66-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755769873860230-I67-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755769955066780-I68-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755770037785180-I69-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755770145288720-I70-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755770209492410-I71-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755770294634710-I72-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755770371311960-I73-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755770449936560-I74-report_twelve_lead_with_replicas.txt";
//        String fileName ="1755770524539630-I75-report_twelve_lead_with_replicas.txt";

//        ---------------------  Failure Cases  ---------------------------------
//        String fileName = "1756298676065490-A fib-report_twelve_lead_with_replicas.txt";
//        String fileName = "1756298676065490-A fib-report_twelve_lead_with_replicas.txt";

//        ------------------------ Mit -------------------------------------------
//        String fldrNew = "D:\\RAHUL\\DownloadsRahul\\dataSets250Hz_9ColumnDataSeperatedWithSpace\\Arrhythmia_Formatted txt files_9Columns\\";
//        String fileName = "1755870471040250-100-report_twelve_lead_with_replicas.txt";
//        String fileName = "1755873651335290-100-report_twelve_lead_with_replicas.txt";
//        String fileName = "1755874725641970-107-report_twelve_lead_with_replicas.txt";
//        String fileName = "1755874472178440-104-report_twelve_lead_with_replicas.txt";
//        String fileName = "1755874926549660-109-report_twelve_lead_with_replicas.txt";
//        String fileName = "1755875018023790-111-report_twelve_lead_with_replicas.txt";
//        String fileName = "1755875345562120-114-report_twelve_lead_with_replicas.txt";
//        String fileName = "1755876984418550-201-report_twelve_lead_with_replicas.txt";
//        String fileName = "1755876722028850-123-report_twelve_lead_with_replicas.txt";
//        String fileName = "1755877151694170-203-report_twelve_lead_with_replicas.txt";
//        String fileName = "1755877688224910-212-report_twelve_lead_with_replicas.txt";
//        String fileName = "1755877767465550-213-report_twelve_lead_with_replicas.txt";
//        String fileName = "1755877858890640-214-report_twelve_lead_with_replicas.txt";
//        String fileName = "1755878038782810-217-report_twelve_lead_with_replicas.txt";
//        String fileName = "1755878295122110-221-report_twelve_lead_with_replicas.txt";
//        String fileName = "1755878383773360-222-report_twelve_lead_with_replicas.txt";
//        String fileName = "1755878877471480-233-report_twelve_lead_with_replicas.txt";

//      --------------------------- MIT BH AF ---------------------
//        String fileName = "1756112386551710-4936-report_twelve_lead_with_replicas.txt";
//        String fileName = "1756112727008480-6426-report_twelve_lead_with_replicas.txt";
//        String fileName = "1756113074027180-7162-report_twelve_lead_with_replicas.txt";
//        String fileName = "1756113368910610-7910-report_twelve_lead_with_replicas.txt";

//       --------------------- Cases to test 3 Sept ------------
//        String fileName ="1753430433042570-Atrial-report_twelve_lead_with_replicas.txt";
//        String fileName ="1753434254437490-3rd degree heart-report_twelve_lead_with_replicas.txt";

//        String fileName = "1756709600485200-Atrial-report_twelve_lead_with_replicas.txt";
//        String fileName = "1756710176499470-3rd deg Heart-report_twelve_lead_with_replicas.txt";


        LoaderHelper ldh = new LoaderHelper();
        TwelveLeadEcgData twelveLeadEcgData = ldh.readTextFileIntoTwelveLeadEcgData(fldrNew, fileName);
//        com.arrthymia.TwelveLeadEcgData twelveLeadEcgData1 = new com.arrthymia.TwelveLeadEcgData();
        ServiceClass service = new ServiceClass();
        FileWriterPointDetection fl = new FileWriterPointDetection();
        service.startSignalProcessing(twelveLeadEcgData, new OnResultCompleteListener() {
//            @Override
//            public void onComplete(int[] rPeaks, double[] rrIntervals, int leadIdx, AllCalculatedData allData, ArrayList<ArrayList<Double>> summaryList) {
//                //actual work
////                ldh.viewTwelveLeadList(twelveLeadEcgData);
//                fl.writeIntoFile(rPeaks, rrIntervals, leadIdx, allData, summaryList);
//            }

            @Override
            public void onComplete(int[] rPeaks, double[] rrIntervals, int leadIdx, AllCalculatedDataNew allData, ArrayList<ArrayList<Double>> summaryList) {
                //actual work
//                ldh.viewTwelveLeadList(twelveLeadEcgData);
                fl.writeIntoFile(rPeaks, rrIntervals, leadIdx, allData, summaryList);
            }

            @Override
            public void rPeaksLessThanTwo(int[] rPeaks, int leadIdx) {
                System.out.println("Issue in lead "+leadIdx);
                fl.writeIntoFileForOneRPeak(rPeaks, leadIdx);
                // error work
            }

            @Override
            public void onFailed(String msg) {
                System.out.println("Error found "+msg);
                // error work~
            }

            @Override
            public void onCompletedLead2MetaData(TwelveLeadEcgData twelveLeadEcgData, HashMap<String, Double> hashMap, String arrhythmiaSummary,
                                                 String stemiResult, String ischemiaResult) {

//                System.out.println("======================= Summary From Lead 2 ======================================");
//                System.out.println("======================= Average Summary From Lead1 to V6 ======================================");
                System.out.println("======================= Average Summary From Lead2 and V4 to V6 ======================================");
                System.out.println("HeartRate: "+hashMap.get("heartRate") );
                System.out.println("PR Interval : "+hashMap.get("prInterval") );
                System.out.println("QRS Interval : "+hashMap.get("qrsInterval") );
                System.out.println("QT Interval : "+hashMap.get("qtInterval") );
                System.out.println("QTc Interval : "+hashMap.get("qtcInterval") );
                System.out.println();
                System.out.println(arrhythmiaSummary);
                System.out.println();
                System.out.println("----------------------------- Final Stemi Decission --------------------------------");
                System.out.println(stemiResult);
                System.out.println();
                System.out.println("------------------------- Ischemia Final Result --------------------------------");
                System.out.println(ischemiaResult);

            }
        });
    }
}