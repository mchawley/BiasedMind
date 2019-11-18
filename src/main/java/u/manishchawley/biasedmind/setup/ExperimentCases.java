/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package u.manishchawley.biasedmind.setup;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.deeplearning4j.util.ModelSerializer;
import u.manishchawley.biasedmind.utils.Constants;
import u.manishchawley.biasedmind.setup.Experiment;

/**
 *
 * @author Manish Chawley
 */
public class ExperimentCases {
    private static Logger log = Logger.getLogger(ExperimentCases.class);
    
    private static final ExperimentCases INSTANCE = new ExperimentCases();
    private List<String []> experimentCases;
    private int experimentNo;
    private String[] currentExperiment;
    
    private ExperimentCases(){
        readExperimentCases();
    }

    public static ExperimentCases getINSTANCE() {
        return INSTANCE;
    }
    
    public Experiment getNextExperiment(){
        
        for(int i=experimentNo; i<experimentCases.size(); i++){
            if(experimentCases.get(i)[Constants.NUM_CLASS].equals(String.valueOf(0))){
                experimentNo = i;
                break;
            }
        }
        currentExperiment = experimentCases.get(experimentNo);
        log.info("Current experiment no: " + experimentNo);
        log.info("Experiment setup: " + Arrays.deepToString(currentExperiment));
        Experiment experiment = new Experiment();
        experiment.ratiosFromString(currentExperiment);
        experiment.setExperimentNo(experimentNo);
        return experiment;
    }
    
    public void failedExperiment(Experiment experiment) throws IOException{
        log.warn("Experiment failed");
        log.info("Updating status of experiment");
        experimentCases.get(experiment.getExperimentNo())[Constants.NUM_CLASS] = String.valueOf(2);
        CSVWriter writer = new CSVWriter(new FileWriter(Constants.SETUP_CSV, false));
        writer.writeAll(experimentCases);
        writer.flush();
        writer.close();
    }
    
    public void completedExperiment(Experiment experiment) throws IOException{
        log.info("Experiment Stats: " + experiment.getEvaluation().stats());
        
        log.info("Updating experiment evaluation: " + experiment.getExperimentNo());
        log.info(Arrays.deepToString(experimentCases.get(experiment.getExperimentNo())));
        
        String[] writeLine = experiment.getEvaluationString();
        CSVWriter writer = new CSVWriter(new FileWriter(Constants.DATA_CSV, true));
        writer.writeNext(writeLine);
        writer.flush();
        writer.close();
        
        log.info("Saving model");
        File modelFile = new File(Constants.MODEL_PATH + "\\" + Arrays.toString(experiment.getRatios()).replaceAll("\\s+","").replaceAll("[\\[\\],]", "_") + ".zip");
        ModelSerializer.writeModel(experiment.getModel(), modelFile, true);
        
        log.info("Updating status of experiment");
        experimentCases.get(experiment.getExperimentNo())[Constants.NUM_CLASS] = String.valueOf(1);
        writer = new CSVWriter(new FileWriter(Constants.SETUP_CSV, false));
        writer.writeAll(experimentCases);
        writer.flush();
        writer.close();
    }

    private void readExperimentCases() {
        try {
            log.info("Reading experiment cases from: " + Constants.SETUP_CSV);
            FileReader reader = new FileReader(Constants.SETUP_CSV);
            CSVReader csvReader = new CSVReader(reader);
            experimentCases = csvReader.readAll();
//            for(String[] line:experimentCases){
//                log.info(Arrays.deepToString(line));
//            }
        } catch (IOException ex) {
            log.error(ex);
        }
    }

    public int getNoOfCases() {
        return experimentCases.size();
    }
    
    public boolean hasNextExperiment() {
        return experimentCases.size()>experimentNo;
    }
}
