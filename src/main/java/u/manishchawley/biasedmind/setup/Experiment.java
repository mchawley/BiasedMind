/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package u.manishchawley.biasedmind.setup;

import java.util.Arrays;
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
        }
        System.out.println(Arrays.toString(this.ratios));
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
