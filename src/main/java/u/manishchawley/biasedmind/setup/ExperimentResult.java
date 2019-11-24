/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package u.manishchawley.biasedmind.setup;

import java.util.Arrays;
import org.apache.log4j.Logger;
import u.manishchawley.biasedmind.utils.Constants;

/**
 *
 * @author Manish Chawley
 */
public class ExperimentResult {
    private int[] ratios;
    private int[] predictedCounts;
    private int[] actualCounts;
    private int[][] confusionMatrix;
    private double accuracy, precision, recall, F1;
    private double[] errorRatios;
    private double[] deviation;
    private String label;
    
    private static Logger log = Logger.getLogger(ExperimentResult.class);
    private static final String[] normalData = {"100", "100", "100", "100", "100", "100", "100", "100", "100", "100", "982", "1137", "1027", "1020", "983", "893", "952", "1030", "971", "1005", "980", "1135", "1032", "1010", "982", "892", "958", "1028", "974", "1009", "974", "0", "0", "0", "0", "0", "1", "2", "2", "1", "0", "1131", "0", "2", "0", "1", "1", "0", "0", "0", "1", "1", "1018", "4", "1", "0", "0", "4", "3", "0", "0", "0", "1", "1003", "0", "3", "0", "2", "1", "0", "0", "0", "2", "0", "974", "0", "3", "2", "0", "1", "1", "0", "0", "4", "0", "885", "1", "1", "0", "0", "5", "2", "1", "1", "1", "1", "946", "0", "1", "0", "0", "1", "4", "1", "0", "0", "0", "1016", "1", "5", "1", "0", "1", "1", "0", "2", "0", "2", "962", "5", "0", "2", "0", "4", "7", "1", "0", "1", "1", "993", "0.9902", "0.990192820026378", "0.990148570349334", "0.990165781852311"};
    public static final ExperimentResult normalResult = new ExperimentResult();

    private ExperimentResult() {
        ratios = new int[Constants.NUM_CLASS];
        predictedCounts = new int[Constants.NUM_CLASS];
        actualCounts = new int[Constants.NUM_CLASS];
        confusionMatrix = new int[Constants.NUM_CLASS][Constants.NUM_CLASS];
        errorRatios = new double[Constants.NUM_CLASS];
        deviation = new double[Constants.NUM_CLASS];
        
//        log.info("Received data: " + Arrays.deepToString(csvLine));
        
        for(int i=0;i<10;i++){
            //first 10 items as ratio
            int pad = 0;
            ratios[i] = Integer.parseInt(normalData[pad+i]);
            //next 10 predicted values
            pad+=10;
            predictedCounts[i] = Integer.parseInt(normalData[pad+i]);
            //next 10 actual values
            pad+=10;
            actualCounts[i] = Integer.parseInt(normalData[pad+i]);
            //next 100 confusion values
            pad+=10;
            //update confustion values
            pad+=100;
            //accuracy, precision, recall, F1
            pad+=4;
            // next 10 error ratios
            errorRatios[i] = ((double)predictedCounts[i] - (double)actualCounts[i])/(double)predictedCounts[i];
            deviation[i] = 1.0;
        }   
        
        label = Arrays.toString(ratios).replaceAll("\\s+","").replaceAll("[\\[\\],]", "_");
    }
    
    public ExperimentResult(String[] csvLine) {
        ratios = new int[Constants.NUM_CLASS];
        predictedCounts = new int[Constants.NUM_CLASS];
        actualCounts = new int[Constants.NUM_CLASS];
        confusionMatrix = new int[Constants.NUM_CLASS][Constants.NUM_CLASS];
        errorRatios = new double[Constants.NUM_CLASS];
        deviation = new double[Constants.NUM_CLASS];
        
//        log.info("Received data: " + Arrays.deepToString(csvLine));
        
        for(int i=0;i<10;i++){
            //first 10 items as ratio
            int pad = 0;
            ratios[i] = Integer.parseInt(csvLine[pad+i]);
            //next 10 predicted values
            pad+=10;
            predictedCounts[i] = Integer.parseInt(csvLine[pad+i]);
            //next 10 actual values
            pad+=10;
            actualCounts[i] = Integer.parseInt(csvLine[pad+i]);
            //next 100 confusion values
            pad+=10;
            //update confustion values
            pad+=100;
            //accuracy, precision, recall, F1
            pad+=4;
            // next 10 error ratios
            errorRatios[i] = ((double)predictedCounts[i] - (double)actualCounts[i])/(double)predictedCounts[i];
            deviation[i] = 1 + (errorRatios[i] + normalResult.getErrorRatios()[i]);
        }   
        
        label = Arrays.toString(ratios).replaceAll("\\s+","").replaceAll("[\\[\\],]", "_");
    }

    public int[] getRatios() {
        return ratios;
    }

    public int[] getPredictedCounts() {
        return predictedCounts;
    }

    public int[] getActualCounts() {
        return actualCounts;
    }

    public int[][] getConfusionMatrix() {
        return confusionMatrix;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public double getF1() {
        return F1;
    }

    public double[] getErrorRatios() {
        return errorRatios;
    }

    public double[] getDeviation() {
        return deviation;
    }

    public String getLabel() {
        return label;
    }
    
    
}
