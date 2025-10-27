package com.stemi.libs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class Utility {

    public double reduceValueAfterPoints(double value, int count)
    {
        long real = (long) value;
        double valueAfterPoints = value - real;
        double newValue = real + (((long) (valueAfterPoints * Math.pow(10,count))) / Math.pow(10,count));
        return newValue;
    }

    public int findMax(int[] arr) {
        if (arr.length == 0) {
//            System.out.println("array is empty....");
            return 0;
        }
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max)
                max = arr[i];
        }
        return max;
    }

    public double findMax(double[] arr) {
        if (arr.length == 0) {
//            System.out.println("array is empty....");
            return 0;
        }
        double max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max)
                max = arr[i];
        }
        return max;
    }

    public double findMax(ArrayList<Double> arr) {
        double max = arr.get(0);
        for (int i = 1; i < arr.size(); i++) {
            if (arr.get(i) > max)
                max = arr.get(i);
        }
        return max;
    }

    public double findMin(ArrayList<Double> arr) {
        double min = arr.get(0);
        for (int i = 1; i < arr.size(); i++) {
            if (arr.get(i) < min)
                min = arr.get(i);
        }
        return min;
    }

    public int findMinInIntegerList(ArrayList<Integer> arr) {
        int min = arr.get(0);
        for (int i = 1; i < arr.size(); i++) {
            if (arr.get(i) < min)
                min = arr.get(i);
        }
        return min;
    }

    public double findMaxAbsolute(double[] arr) {
        if (arr.length == 0) {
//            System.out.println("array is empty....");
            return 0;
        }
        double max = Math.abs(arr[0]);
        for (int i = 1; i < arr.length; i++) {
            if (Math.abs(arr[i]) > max)
                max = Math.abs(arr[i]);
        }
        return max;
    }

    public int findMaxIndex(double[] arr) {
        double max = arr[0];
        int maxIdx = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
                maxIdx = i;
            }
        }
        return maxIdx;
    }

    public double findMin(double[] arr) {
        double min = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < min)
                min = arr[i];
        }
        return min;
    }

    public double findMinAbsolute(double[] arr) {
        if (arr.length == 0) {
//            System.out.println("array is empty....");
            return 0;
        }
        double min = Math.abs(arr[0]);
        for (int i = 1; i < arr.length; i++) {
            if (Math.abs(arr[i]) < min)
                min = Math.abs(arr[i]);
        }
        return min;
    }

    public int findMinIndex(double[] arr) {
        double min = arr[0];
        int index = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < min) {
                min = arr[i];
                index = i;
            }
        }
        return index;
    }

    public int findMaxIndexAbsolute(double[] arr) {
        if (arr.length == 0) {
            return 0;
        }
        double max = Math.abs(arr[0]);
        int index = 0;
        for (int i = 1; i < arr.length; i++) {
            if (Math.abs(arr[i]) > max) {
                max = Math.abs(arr[i]);
                index = i;
            }
        }
        return index;
    }

    public int findMinIndexAbsolute(double[] arr) {
        double min = Math.abs(arr[0]);
        int index = 0;
        for (int i = 1; i < arr.length; i++) {
            if (Math.abs(arr[i]) < min) {
                min = Math.abs(arr[i]);
                index = i;
            }
        }
        return index;
    }

    public double findMax(double[] arr, int startIndex, int endIndex) {
        if ((startIndex < 0) || (endIndex > arr.length-1)) {
            System.out.println("Error in startIndex or EndIndex");
            return -1;
        } else {
            double max = arr[startIndex];
            double maxIdx = startIndex;
            for (int i = startIndex + 1; i <= endIndex; i++) {
                if (arr[i] > max) {
                    max = arr[i];
                    maxIdx = i;
                }
            }
            return (maxIdx - startIndex);
        }
    }

    public int findMaxComparative(double[] arr, int startIndex, int endIndex) {
        if ((startIndex < 0) || (endIndex > arr.length-1)) {
            System.out.println("Error in startIndex or EndIndex");
            return -1;
        } else {
            double max = arr[startIndex];
            int maxIdx = startIndex;
            for (int i = startIndex + 1; i <= endIndex; i++) {
                if (arr[i] > max) {
                    max = arr[i];
                    maxIdx = i;
                }
            }
            return (maxIdx - startIndex);
        }
    }

    public double[] fillDataIntoArray(int startIdx, int endIdx, double[] arr) {
//        System.out.println("fillDataIntoArray ----  "+startIdx+"     ------    "+endIdx);
        double[] newArr = new double[endIdx-startIdx+1];
        if (startIdx > endIdx)
            System.out.println("Start Index is greter than endIndex");
        else if (startIdx == endIdx)
            newArr[0] = arr[startIdx];
        else {
            int idx = 0;
            for (int i = startIdx; i <= endIdx; i++) {
                newArr[idx] = arr[i];
                idx++;
            }
        }
        return newArr;
    }

    public double[] differentaition(double[] arr) {
        double[] diffArray = new double[arr.length-1];
        for (int i = 0; i < diffArray.length; i++) {
            diffArray[i] = arr[i+1] - arr[i];
        }
        return diffArray;
    }

    public double[] differentaitionAbsolute(double[] arr) {
        double[] diffArray = new double[arr.length-1];
        for (int i = 0; i < diffArray.length; i++) {
            diffArray[i] = (double) Math.round(Math.abs(arr[i+1] - arr[i]) * 1000 ) / 1000;
        }
        return diffArray;
    }

    public int[] differentaitionAbsolute(int[] arr) {
        int[] diffArray = new int[arr.length-1];
        for (int i = 0; i < diffArray.length; i++) {
            diffArray[i] = Math.abs(arr[i+1] - arr[i]);
        }
        return diffArray;
    }


    public int[] differentaition(int[] arr) {
        int[] diffArray = new int[arr.length-1];
        for (int i = 0; i < diffArray.length; i++) {
            diffArray[i] = arr[i+1] - arr[i];
        }
        return diffArray;
    }

    public double mean(double[] arr) {
        double sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum+= arr[i];
        }
//        double zzLast = arr[arr.length-3];
        double m = sum/ arr.length;
        return (double) Math.round(m * 1000) / 1000;
    }

    public double mean(int[] arr) {
        double sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum+= arr[i];
        }
//        double zzLast = arr[arr.length-3];
        double m = sum/ arr.length;
        return (double) Math.round(m * 1000) / 1000;
    }

    public double[][] normalizeToRawScale(double[][] data, double[][] rawData) {
        int numColumns = data.length;
        double[][] normalizedData = new double[data.length][data[0].length];
        for (int i = 0; i < numColumns; i++) {
            double[] columnData = data[i];
            double[] rawColumnData = rawData[i];
            double rawDataMean = mean(rawColumnData);
            double rawStd = calculateSD(rawColumnData);
            double dataMean = mean(columnData);
            double dataStd = calculateSD(columnData);
            if (dataStd == 0 || rawStd == 0) {
                normalizedData[i]= columnData;
            }
            else {
//                normalized_data(:,i) =((column_data - data_mean) / data_std) * raw_std + raw_mean;
                for (int j = 0; j < columnData.length; j++) {
                   normalizedData[i][j] = (((columnData[j] - dataMean) / dataStd) * rawStd) + rawDataMean;
                }
            }
        }
        return normalizedData;
    }

    public double calculateSD(double[] numArray)
    {
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.length;

        for(double num : numArray) {
            sum += num;
        }
        double mean = sum/length;
        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }
        double sD = Math.sqrt(standardDeviation/(length - 1));
        return (double) Math.round(sD * 1000) / 1000;
    }

    public double calculateSD(int[] numArray)
    {
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.length;

        for(double num : numArray) {
            sum += num;
        }
        double mean = sum/length;
        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }
        double sD = Math.sqrt(standardDeviation/(length - 1));
        return (double) Math.round(sD * 1000) / 1000;
    }

    public double findMean(ArrayList<Double> dataList) {
        double[] arr = dataList.stream().mapToDouble((x)->(x)).toArray();
        return mean(arr);
    }

    public double calculateStd(ArrayList<Double> dataList) {
        double[] arr = dataList.stream().mapToDouble((x)->(x)).toArray();
        return calculateSD(arr);
    }

    public double findMeanInt(ArrayList<Integer> dataList) {
        int[] arr = dataList.stream().mapToInt((x)->(x)).toArray();
        return mean(arr);
    }

    public double calculateStdInt(ArrayList<Integer> dataList) {
        int[] arr = dataList.stream().mapToInt((x)->(x)).toArray();
        return calculateSD(arr);
    }

//  Function created on 25 Mar 2025
    public double[] convolutionCustomForNotch(double[] x , double[] h) {
        int nX = x.length, xIndex;
        int nH = h.length;
        int nY = nX + nH -1;
        double acc;
        double[] y = new double[nY];
        for (int i = 0; i < nY; i++) {
            acc = 0;
            for (int k = 0; k < nH; k++) {
                xIndex = i - k;
                if (xIndex >= 0 && xIndex < nX)
                    acc += x[xIndex] * h[k];
            }
            y[i] = acc;
        }
        return y;
    }


    public double[] convolutionCustom(double[] x , double[] h) {
        int n_x = x.length;
        int n_h = h.length;
        int lenOutput = n_x + n_h - 1;
        double[] xPadded = new double[lenOutput];
        for (int i = 0; i < lenOutput; i++) {
            if(i < x.length){
                xPadded[i] = x[i];
            }
        }
        Signals sg = new Signals();
        double[] hFlipped = sg.reverse(h);
        double[] y = new double[lenOutput];
        for (int n = 0; n < lenOutput; n++) {
            double sum =0;
            if (n + n_h - 1 < xPadded.length) {
                for (int i = 0; i < (n+n_h-1); i++) {
                    double a = 0;
                    if(i < hFlipped.length)
                        a = xPadded[i] * hFlipped[i];
                    sum+= a;
                }
                y[n] = sum;
            } else {
                int b = Math.min(xPadded.length, (n + n_h - 1));
                double[] overlap =new double[b-n];
                for (int i = 0; i < overlap.length; i++) {
                    overlap[i] = xPadded[i+n];
                }
                double[] hSubset = new double[overlap.length];
                for (int i = 0; i < hSubset.length; i++) {
                    hSubset[i] = hFlipped[i];
                }
                for (int i = 0; i < overlap.length; i++) {
                    sum += overlap[i] * hSubset[i];
                }
                y[n] = sum;
            }
        }
       return y;
    }

    public double[] convolutionCustomForPointDetection(double[] x , double[] h) {
        int n_x = x.length;
        int n_h = h.length;
        int lenOutput = n_x + n_h - 1;
        double[] xPadded = new double[lenOutput];
        for (int i = 0; i < lenOutput; i++) {
            if(i < x.length){
                xPadded[i] = x[i];
            }
        }
        Signals sg = new Signals();
        double[] hFlipped = sg.reverse(h);
        double[] y = new double[lenOutput];
        for (int n = 0; n < lenOutput; n++) {
            double sum =0;
            if (n + n_h - 1 < xPadded.length) {
                for (int i = 0; i < (n+n_h-1); i++) {
                    double a = 0;
                    if(i < hFlipped.length)
                        a = xPadded[i] * hFlipped[i];
                    sum+= a;
                }
                y[n] = sum;
            } else {
                int b = Math.min(xPadded.length, (n + n_h - 1));
                double[] overlap =new double[b-n];
                for (int i = 0; i < overlap.length; i++) {
                    overlap[i] = xPadded[i+n];
                }
                double[] hSubset = new double[overlap.length];
                for (int i = 0; i < hSubset.length; i++) {
                    hSubset[i] = hFlipped[i];
                }
                for (int i = 0; i < overlap.length; i++) {
                    sum += overlap[i] * hSubset[i];
                }
                y[n] = sum;
            }
        }
        for (int i = 1; i < y.length; i++) {
            y[i] = 0.0;
        }
        return y;
    }


    public void viewData(double[][] arr) {
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

    public int[] findFirstAndLastIndexWhenSlopesExceedsThresholdValue(double[] slopes, double thr) {
        // Compares with Absolute values of the array.
        int[] indices = new int[2];
        int first = -1, last = -1;
        for (int i = 0; i < slopes.length; i++) {
            if (Math.abs(slopes[i]) > thr) {
                last = i;
                if (first == -1)
                    first = i;
            }
        }
        indices[0] = first;
        indices[1] = last;
       return indices;
    }

    public int findFirstIndexWhenSlopesExceedsThresholdValue(double[] slopes, double thr) {
        // -----------  Compares with Normal values of the array.  -------------
        int first = -1;
        for (int i = 0; i < slopes.length; i++) {
            if (slopes[i] > thr) {
                first = i;
                break;
            }
        }
        return first;
    }

    public int findFirstIndexWhenAbsoluteSlopeValuesExceedsThresholdValue(double[] slopes, double thr) {
        // Compares with Absolute values of the array.
        int first = -1;
        for (int i = 0; i < slopes.length; i++) {
            if (Math.abs(slopes[i]) > thr) {
                first = i;
                break;
            }
        }
       return first;
    }

    public int findLastIndexWhenAbsoluteSlopeValuesExceedsThresholdValue(double[] slopes, double thr) {
        // Compares with Absolute values of the array.
        int last = -1;
        for (int i = 0; i < slopes.length; i++) {
            if (Math.abs(slopes[i]) > thr) {
                last = i;
            }
        }
        return last;
    }

    public int customFindLastIndexWhenSlopesFallShortOffThresholdValue(double[] slopes, double thr) {
        int last = -1;
        for (int i = 0; i < slopes.length; i++) {
            if (slopes[i] < thr) {
                last = i;
            }
        }
        return last;
    }

    public ArrayList<Integer> findIndexWhereValueIsGreaterThanZero(double[] arr) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 0)
                list.add(i);
        }
        return list;
    }

    public boolean checkWhetherAllArrayValueGreaterThanZero(double[] arr) {
        boolean flag = true;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] <= 0) {
                flag = false;
                return flag;
            }
        }
        return  flag;
    }

    public boolean checkWhetherAnyArrayValueGreaterThanZero(double[] arr) {
        boolean flag = false;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 0) {
                flag = true;
                return flag;
            }
        }
        return  flag;
    }

    public boolean checkWhetherAnyArrayValueLessThanZero(double[] arr) {
        boolean flag = false;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < 0) {
                flag = true;
                return flag;
            }
        }
        return  flag;
    }

    public boolean checkWhetherAnyArrayValueGreaterThanThreshold(ArrayList<Double> list, int threshold) {
        boolean flag = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) > threshold) {
                flag = true;
                return flag;
            }
        }
        return  flag;
    }

    public boolean checkWhetherAnyArrayValueLessThanThreshold(ArrayList<Double> list, int threshold) {
        boolean flag = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) < threshold) {
                flag = true;
                return flag;
            }
        }
        return  flag;
    }

    public ArrayList<Integer> findIndexWhenArrayValuesAreLessThanThreshold(double[] rrIntervals, double threshold) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < rrIntervals.length; i++) {
            if (rrIntervals[i] < threshold)
                list.add(i);
        }
        return list;
    }

    public double variance(double[] data) {
        double sum = 0;
        double count = 0;

        // First pass: compute mean, ignoring NaN
        for (double value : data) {
            if (!Double.isNaN(value)) {
                sum += value;
                count++;
            }
        }

        if (count <= 1) {
            return Double.NaN;  // Undefined for n <= 1
        }

        double mean = sum / count;

        // Second pass: compute squared differences
        double sqDiffSum = 0;
        for (double value : data) {
            if (!Double.isNaN(value)) {
                double diff = value - mean;
                sqDiffSum += diff * diff;
            }
        }

        // Return sample variance (normalized by n - 1)
        return sqDiffSum / (count - 1);
    }

    public void bubbleSortAssecending(int[] arr)
    {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j+1])
                {
                    int temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
            }
        }
    }

    public boolean chechkWhetherAnyStringInTheArrayListEqualsGivenString(ArrayList<String> list, String testString) {
        for (String str : list) {
            if (str.equalsIgnoreCase(testString))
                return true;
        }
        return false;
    }

    public boolean chechkWhetherAnyStringInTheArrayListContainsGivenPartOfString(ArrayList<String> list, String testString) {
        for (String str : list) {
            if (str.toLowerCase().contains(testString.toLowerCase()))
                return true;
        }
        return false;
    }

    public ArrayList<ArrayList<Integer>> customUnique(ArrayList<Integer> list) {
        Map<Integer, Integer> indexMap = new TreeMap<>();
        for (int i = 0; i < list.size() - 1; i++) {
            if (!indexMap.containsKey(list.get(i)))
                indexMap.put(list.get(i), i);
        }
        ArrayList<ArrayList<Integer>> mainList = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> unique = new ArrayList<>(indexMap.keySet());
        ArrayList<Integer> uniqueValueFirstIndicies = new ArrayList<>(indexMap.values());
        mainList.add(unique);  // 0 index --- qrsLocal
        mainList.add(uniqueValueFirstIndicies); // 1 index ----- indices for qrsAmplitude
        return mainList;
    }

    public double median (double[] arr) {
        Arrays.sort(arr);
        int n = arr.length;
        if (n % 2 == 0)
            return ( arr[n/2 - 1] + arr[n/2]) / 2.0;
        return arr[n/2];
    }

    public double median (int[] arr) {
        Arrays.sort(arr);
        int n = arr.length;
        if (n % 2 == 0)
            return ( arr[n/2 - 1] + arr[n/2]) / 2.0;
        return arr[n/2];
    }

    public double median(ArrayList<Integer> list) {
        int[] arr = list.stream().mapToInt(Integer::intValue).toArray();
        Arrays.sort(arr);
        int n = arr.length;
        if (n % 2 == 0)
            return ( arr[n/2 - 1] + arr[n/2]) / 2.0;
        return (double) arr[n/2];
    }

    public double medianDouble(ArrayList<Double> list) {
        double[] arr = list.stream().mapToDouble(Double::doubleValue).toArray();
        Arrays.sort(arr);
        int n = arr.length;
        if (n % 2 == 0)
            return ( arr[n/2 - 1] + arr[n/2]) / 2.0;
        return (double) arr[n/2];
    }

    public int medianBoolean(ArrayList<Boolean> list) {
        int[] arr = list.stream().mapToInt(x-> (x) ? 1:0).toArray();
        Arrays.sort(arr);
        int n = arr.length;
        if (n % 2 == 0)
            return ( arr[n/2 - 1] + arr[n/2]) / 2;
        return arr[n/2];
    }

    public int findIfAnyNonZero(int[] arr) {
        for (int x : arr) {
            if (x == 1)
                return 1;
        }
        return  0;
    }

}
