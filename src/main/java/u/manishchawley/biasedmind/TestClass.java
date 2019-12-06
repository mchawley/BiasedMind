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
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import u.manishchawley.biasedmind.utils.MNISTDatabase;
import org.nd4j.evaluation.classification.Evaluation;
import u.manishchawley.biasedmind.setup.Experiment;
import u.manishchawley.biasedmind.setup.ExperimentCases;
import u.manishchawley.biasedmind.utils.Constants;

/**
 *
 * @author Manish Chawley
 */
public class TestClass {
    
    private static Logger log = Logger.getLogger(TestClass.class);
    
    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        
//        MNISTDatabase database = new MNISTDatabase();
//        System.out.println(database.trainFiles.get("0").get(0));
//        int[] ratios = {100,10,10,10,10,10,10,10,10,100};
//        database.generateBiasedTrainDataOptimized(ratios);
        
        ExperimentCases cases = ExperimentCases.getINSTANCE();
        
        log.info("Experiments setup file: " + Constants.SETUP_CSV);
        log.info("Experiments output file: " + Constants.DATA_CSV);
        log.info("Experiments low ratio: " + Constants.LESS_TRAIN);
        log.info("Experiments high ratio: " + Constants.ALL_TRAIN);   
        
        while(cases.hasNextExperiment()){
            Experiment experiment = cases.getNextExperiment();
            int[] ratios = experiment.getRatios();
//            log.info(Arrays.toString(ratios));
            MNISTDatabase database = new MNISTDatabase();
            String path = database.generateBiasedTrainDataOptimized(ratios);
            MNISTClassifier classifier = new MNISTClassifier();
            Evaluation eval = classifier.trainModel(path);
            MultiLayerNetwork model = classifier.getModel();
            if(eval!=null){
                experiment.setEvaluation(eval);
                experiment.setModel(model);
                cases.completedExperiment(experiment);
            }else cases.failedExperiment(experiment);
//            database.destroyBiasedTrainData(path);
        }
    }
        
}
