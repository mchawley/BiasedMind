/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package u.manishchawley.biasedmind.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
/**
 *
 * @author Manish Chawley
 */
public class MNISTDatabase {
    
    private static Logger log = Logger.getLogger(MNISTDatabase.class);
    
    private static final MNISTDatabase INSTANCE = new MNISTDatabase();
    private static final String REUSABLE_FOLDER = "reusable_setup";
    public Map<String, List<String>> trainFiles, testFiles;
    
    public MNISTDatabase() {
        log.info("MNIST database initialized");
        if (!new File(Constants.MNIST_PATH + "/mnist_png").exists()) {
            String localFilePath = Constants.MNIST_PATH + "/mnist_png.tar.gz";
            log.info("Downloading database from: " + Constants.DATA_URL);
            try {
                if (DataUtilities.downloadFile(Constants.DATA_URL, localFilePath)) {
                    DataUtilities.extractTarGz(localFilePath, Constants.MNIST_PATH);
                }
            } catch (IOException ex) {
                log.error(ex);
            }
        }
        generateFileList();
    }
    
    public static MNISTDatabase getInstance(){
        return INSTANCE;
    }

    private void generateFileList() {
        trainFiles = new HashMap<>();
        File mnistTrain = new File(Constants.MNIST_PATH + "/mnist_png/training");
        File[] dirList = mnistTrain.listFiles();
        for(File dir:dirList){
//            log.info(dir.getName() + ": " + dir.listFiles().length);
            trainFiles.put(dir.getName(), Arrays.asList(dir.list()));
        }
        testFiles = new HashMap<>();
        File mnistTest = new File(Constants.MNIST_PATH + "/mnist_png/testing");
        dirList = mnistTest.listFiles();
        for(File dir:dirList){
//            log.info(dir.getName() + ": " + dir.listFiles().length);
            testFiles.put(dir.getName(), Arrays.asList(dir.list()));
        }
    }
    
    public String generateBiasedTrainDataOptimized(int[] ratios) throws IOException{
        if(ratios.length != Constants.NUM_CLASS) return null;
        String folderPath = Constants.MNIST_PATH + "\\mnist_png\\" + REUSABLE_FOLDER;
        log.info("Using existing biased folder" + folderPath);
        
        Map<String, List<String>> currentFiles = new HashMap<>();
        File folder = new File(folderPath);
        if(!folder.exists())
            folder.mkdir();
        File[] dirList = folder.listFiles();
        for(File dir:dirList){
            currentFiles.put(dir.getName(), Arrays.asList(dir.list()));
        }
        
        Map<String, List<String>> requiredFiles = new HashMap<>();
        for(int i=0; i<Constants.NUM_CLASS; i++){
            if(ratios[i]!=100) {
                int numFiles = trainFiles.get(String.valueOf(i)).size();
                int toKeep = numFiles * ratios[i] / 100;
                log.info("Number of files to keep for " + i +": " + toKeep);
                Random r = new Random(Constants.SEED);
                int[] numToKeep = r.ints(toKeep, 0, numFiles).distinct().toArray();
                List<String> filesToCopy = trainFiles.get(String.valueOf(i));
                for(int j=0; j<numToKeep.length; j++){
                    if(!requiredFiles.containsKey(String.valueOf(i))){
                        requiredFiles.put(String.valueOf(i), new ArrayList<>());
                    }
                    requiredFiles.get(String.valueOf(i)).add(filesToCopy.get(j));
                }
            }else {
                log.info("Keeping all files for " + i +": " + trainFiles.get(String.valueOf(i)).size());
                requiredFiles.put(String.valueOf(i), trainFiles.get(String.valueOf(i)));
            }
        }
        
        for(int i=0; i<Constants.NUM_CLASS; i++){
            List<String> existingList = currentFiles.get(String.valueOf(i));
            List<String> requiredList = requiredFiles.get(String.valueOf(i));
            
            String trainDirectory = Constants.MNIST_PATH + "\\mnist_png\\training\\" + String.valueOf(i) + "\\";
            String reuseDirectory = folderPath + "\\" + String.valueOf(i) + "\\";
            
            if(!new File(folderPath + "\\" + String.valueOf(i)).exists())
                new File(folderPath + "\\" + String.valueOf(i)).mkdir();
            
            if(existingList!=null)
                for(String file:existingList){
                    if(!requiredList.contains(file)){
                        //delete from the current Directory
                        log.info("Deleting file: " + reuseDirectory + file);
                        Files.delete(new File(reuseDirectory + file).toPath());
                    }
                }
            for(String file:requiredList){
                if(existingList==null || !existingList.contains(file)){
                    //copy to the current Directory
                    log.info("Copying file: " + trainDirectory + file);
                    Files.copy(new File(trainDirectory + file).toPath()
                            , new File(reuseDirectory + file).toPath()
                            , StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        
        return folderPath;
    }
    
    public String generateBiasedTrainData(int[] ratios) throws IOException{
        if(ratios.length != Constants.NUM_CLASS) return null;
        String folderName = "training" + Arrays.toString(ratios).replaceAll("\\s+","").replaceAll("[\\[\\],]", "_") + "tmp";
        String path = Constants.MNIST_PATH + "/mnist_png/" + folderName;
        File biasedDir = new File(path);     
        log.info("New Biased Data folder: " + path);
        
        if(biasedDir.exists())
//            return path;
            destroyBiasedTrainData(path);
        
        biasedDir.mkdir();
        for(int i=0; i<Constants.NUM_CLASS; i++){
            new File(path + "/" + String.valueOf(i)).mkdir();
            if(ratios[i]!=100) {
                int numFiles = trainFiles.get(String.valueOf(i)).size();
                int toKeep = numFiles * ratios[i] / 100;
                log.info("Number of files to keep for " + i +": " + toKeep);
                Random r = new Random(Constants.SEED);
                int[] numToKeep = r.ints(toKeep, 0, numFiles).distinct().toArray();
                List<String> filesToCopy = trainFiles.get(String.valueOf(i));
                for(int j=0; j<numToKeep.length; j++){
                    Files.copy(new File(filesToCopy.get(j)).toPath()
                            , new File(new File(filesToCopy.get(j)).getPath().replace("training", folderName)).toPath()
                            , StandardCopyOption.REPLACE_EXISTING);
                }
            }else {
                log.info("Keeping all files for " + i +": " + trainFiles.get(String.valueOf(i)).size());
                for(String file:trainFiles.get(String.valueOf(i))){
                    Files.copy(new File(file).toPath()
                            , new File(new File(file).getPath().replace("training", folderName)).toPath()
                            , StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        return path;
    }
    
    public void destroyBiasedTrainData(String path) throws IOException {
        log.warn("Deleting directory :" + path);
        FileUtils.deleteDirectory(new File(path));
    }

}
