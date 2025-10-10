package com.stemi;

import com.stemi.dataClasses.TwelveLeadEcgData;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class LoaderHelper {

        public File[] loadDataFiles(String fldr)
        {
            File file1 = new File(fldr);
            File[] fileArr = file1.listFiles();
            if (fileArr.length==0)
            {
                System.out.println("No files found inside the directory");
                return null;
            }
            return fileArr;
        }

        public ArrayList<String> listAllFiles(File[] fileArr, String fileName)
        {
            ArrayList<String> fNameList = new ArrayList<String>();
            for (int i = 0; i < fileArr.length; i++) {
                String fName = fileArr[i].getName();
//                if(fileArr[i].isFile() && fName.endsWith(fileName) ) {
                if(fileArr[i].isFile() && fName.equalsIgnoreCase(fileName) ) {
                    fNameList.add(fName);
                    System.out.println(fName);
                }
            }
            return fNameList;
        }

/*
        public double[][] readFileDataTrial2(ArrayList<String> fNameList, String fldr, int k)
        {
            String fName = "";
            fName = fNameList.get(k);
//            System.out.println(fName);
            try {
                File file = new File(fldr + File.separator + fName);
                Scanner input = new Scanner(file);
                String[] line = input.nextLine().trim().replaceAll("\\s+", " ").split("\\s");
                int col = line.length;
                int rows = 1;           // rows start from zero 1 and not 0, bcoz 0th index row is used to find the column length and it will be skipped now when we calculate rows.
                while (input.hasNextLine())
                {
                    String s = input.nextLine();
                    rows++;
                }
                double[][] arr = new double[col][rows];
                Scanner input2 = new Scanner(file);
                int r = 0;
                while (input2.hasNextLine())
                {
                    String[] line2 = input2.nextLine().split("\t");
                    for (int i = 0; i < line2.length; i++) {
                        try{
                            arr[i][r] = Double.valueOf(line2[i]);
                        } catch (Exception e) {
                            arr[i][r] = 0;
                        }
                  arr[i][r] = Double.valueOf(line2[i]);
                    }
                    r++;
                }
                input.close();
                input2.close();
                return arr;
            }catch (Exception e)
            {
                System.out.println("Issue in file reading....... readFileDataTrial2() method");
                e.printStackTrace();
            }
            return null;
        }
*/

    public TwelveLeadEcgData readFileDataTrial3New(ArrayList<String> fNameList, String fldr)
    {
        String fName = "";
        fName = fNameList.get(0);
//        com.arrthymia.TwelveLeadEcgData twelveLeadEcgData = new com.arrthymia.TwelveLeadEcgData();
//            System.out.println(fName);
        try {
            File file = new File(fldr + File.separator + fName);
            Scanner input = new Scanner(file);
            String[] line = input.nextLine().trim().replaceAll("\\s+", " ").split("\\s");
            int col = line.length;
            int rows = 1;           // rows start from zero 1 and not 0, bcoz 0th index row is used to find the column length and it will be skipped now when we calculate rows.
            while (input.hasNextLine())
            {
                String s = input.nextLine();
                rows++;
            }
//                double[][] arr = new double[rows][col];
            double[][] arr = new double[col][rows];
            Scanner input2 = new Scanner(file);
            int r = 0;
            ArrayList<Double> listLead1 = new ArrayList<Double>();
            ArrayList<Double> listLead2 = new ArrayList<Double>();
            ArrayList<Double> listLead3 = new ArrayList<Double>();
            ArrayList<Double> listV1 = new ArrayList<Double>();
            ArrayList<Double> listV2 = new ArrayList<Double>();
            ArrayList<Double> listV3 = new ArrayList<Double>();
            ArrayList<Double> listV4 = new ArrayList<Double>();
            ArrayList<Double> listV5 = new ArrayList<Double>();
            ArrayList<Double> listV6 = new ArrayList<Double>();
            while (input2.hasNextLine())
            {
                String[] line2 = input2.nextLine().split("\t");
//                String[] line2 = input2.nextLine().split(",");
                for (int i = 0; i < line2.length; i++) {
                        if(i==0) {
                            listLead1.add(Double.valueOf(line2[i]));
                        }
                        if(i==1) {
                            listLead2.add(Double.valueOf(line2[i]));
                        }
                        if(i==2) {
                            listLead3.add(Double.valueOf(line2[i]));
                        }
                        if(i==3) {
                           listV1.add(Double.valueOf(line2[i]));
                        }if(i==4) {
                            listV2.add(Double.valueOf(line2[i]));
                        }if(i==5) {
                            listV3.add(Double.valueOf(line2[i]));
                        }if(i==6) {
                            listV4.add(Double.valueOf(line2[i]));
                        }if(i==7) {
                            listV5.add(Double.valueOf(line2[i]));
                        }if(i==8) {
                            listV6.add(Double.valueOf(line2[i]));
                        }
                }
            }
            TwelveLeadEcgData twelveLeadEcgData = new TwelveLeadEcgData(
                    listV1,
                    listV2,
                    listV3,
                    listV4,
                    listV5,
                    listV6,
                    listLead1,
                    listLead2,
                    listLead3
            );

//            twelveLeadEcgData.setLead1(listLead1);
//            twelveLeadEcgData.setLead2(listLead2);
//            twelveLeadEcgData.setLead3(listLead3);
//            twelveLeadEcgData.setV1(listV1);
//            twelveLeadEcgData.setV2(listV2);
//            twelveLeadEcgData.setV3(listV3);
//            twelveLeadEcgData.setV4(listV4);
//            twelveLeadEcgData.setV5(listV5);
//            twelveLeadEcgData.setV6(listV6);
            input.close();
            input2.close();
            return twelveLeadEcgData;
            }catch (Exception e)
            {
                System.out.println("Issue in file reading....... readFileDataTrial3New() method");
                e.printStackTrace();
            }
            return null;
        }

        public TwelveLeadEcgData readTextFileIntoTwelveLeadEcgData(String fldrNew, String fileName){
            ServiceClass service = new ServiceClass();
            File[] folder = loadDataFiles(fldrNew);
            ArrayList<String> listOfFiles = listAllFiles(folder, fileName);
//            System.out.println(listOfFiles.get(0));
            return readFileDataTrial3New(listOfFiles,fldrNew);
        }

        public void viewTwelveLeadList (TwelveLeadEcgData twelveLeadEcgData) {
            for (int i = 0; i < twelveLeadEcgData.getLead1().size(); i++) {
                System.out.print(twelveLeadEcgData.getLead1().get(i)+",  ");
                System.out.print(twelveLeadEcgData.getLead2().get(i)+",  ");
                System.out.print(twelveLeadEcgData.getLead3().get(i)+",  ");
                System.out.print(twelveLeadEcgData.getV1().get(i)+",  ");
                System.out.print(twelveLeadEcgData.getV2().get(i)+",  ");
                System.out.print(twelveLeadEcgData.getV3().get(i)+",  ");
                System.out.print(twelveLeadEcgData.getV4().get(i)+",  ");
                System.out.print(twelveLeadEcgData.getV5().get(i)+",  ");
                System.out.print(twelveLeadEcgData.getV6().get(i)+",  ");
                System.out.println();
            }
        }

    public void viewData(double[][] arr) {
        for (int i = 0; i < arr[0].length; i++) {
            for (int j = 0; j < arr.length; j++) {
                System.out.print(arr[j][i]+",  ");
            }
            System.out.println();
        }
    }

    public void viewData(boolean[][] arr) {
        for (int i = 0; i < arr[0].length; i++) {
            for (int j = 0; j < arr.length; j++) {
                System.out.print(arr[i][j]+",  ");
            }
            System.out.println();
        }
    }

    public void viewData(int[][] arr) {
        for (int i = 0; i < arr[0].length; i++) {
            for (int j = 0; j < arr.length; j++) {
                System.out.print(arr[j][i]+",  ");
            }
            System.out.println();
        }
    }

    public void viewData(double[] arr) {
        for (int j = 0; j < arr.length; j++) {
            System.out.println(j+" ===     "+arr[j]);
        }
    }

    public void viewData(int[] arr) {
        for (int j = 0; j < arr.length; j++) {
            System.out.println(j+" ===     "+arr[j]);
        }
    }
}



