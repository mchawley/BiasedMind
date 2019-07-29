/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package u.manishchawley.biasedmind;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import u.manishchawley.biasedmind.utils.MNISTDatabase;
import org.nd4j.evaluation.classification.Evaluation;
import u.manishchawley.biasedmind.setup.Experiment;
import u.manishchawley.biasedmind.setup.ExperimentCases;

/**
 *
 * @author Manish Chawley
 */
public class TestClass {
    
    private static Logger log = Logger.getLogger(TestClass.class);
    
    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        MNISTDatabase database = MNISTDatabase.getInstance();
        ExperimentCases cases = ExperimentCases.getINSTANCE();
        
        while(cases.hasNextExperiment()){
            Experiment experiment = cases.getNextExperiment();
            int[] ratios = experiment.getRatios();
//            log.info(Arrays.toString(ratios));
            String path = database.generateBiasedTrainData(ratios);
            MNISTClassifier classifier = new MNISTClassifier();
            Evaluation eval = classifier.trainModel(path);
            experiment.setEvaluation(eval);
            cases.completedExperiment(experiment);
            database.destroyBiasedTrainData(path);
        }
    }
        
}
