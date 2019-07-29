/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package u.manishchawley.biasedmind.setup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.nd4j.evaluation.classification.ConfusionMatrix;
import org.nd4j.evaluation.classification.Evaluation;
import u.manishchawley.biasedmind.utils.Constants;

/**
 *
 * @author Manish Chawley
 */
public class Experiment {
    private int[] ratios;
    private Evaluation evaluation;
    private int experimentNo;
    
    public void ratiosFromString(String[] ratios){
        this.ratios = new int[Constants.NUM_CLASS];
        for(int i=0; i<Constants.NUM_CLASS; i++){
            this.ratios[i] = Integer.valueOf(ratios[i]);
            if(this.ratios[i]==1)
                this.ratios[i]=Constants.ALL_TRAIN;
            else
                this.ratios[i]=Constants.LESS_TRAIN;
        }
    }
    
    public String[] getEvaluationString(){
        List<String> evalList = new ArrayList<>();
        ConfusionMatrix<Integer> confusion = evaluation.getConfusionMatrix();
        for(int i=0;i<ratios.length; i++)
            evalList.add(String.valueOf(ratios[i]));
        for(int i=0;i<Constants.NUM_CLASS;i++)
            evalList.add(String.valueOf(confusion.getPredictedTotal(i)));
        for(int i=0;i<Constants.NUM_CLASS;i++)
            evalList.add(String.valueOf(confusion.getActualTotal(i)));
        for(int i=0;i<Constants.NUM_CLASS;i++)
            for(int j=0;j<Constants.NUM_CLASS;j++)
                evalList.add(String.valueOf(confusion.getCount(i, j)));
        
        return evalList.toArray(new String[0]);
    } 

    public int[] getRatios() {
        return ratios;
    }

    public void setRatios(int[] ratios) {
        this.ratios = ratios;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    public int getExperimentNo() {
        return experimentNo;
    }

    public void setExperimentNo(int experimentNo) {
        this.experimentNo = experimentNo;
    }
    
    
}
