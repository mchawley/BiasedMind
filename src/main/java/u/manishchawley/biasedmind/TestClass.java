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

/**
 *
 * @author Manish Chawley
 */
public class TestClass {
    
    private static Logger log = Logger.getLogger(TestClass.class);
    
    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        MNISTDatabase database = MNISTDatabase.getInstance();
        
        String key = String.valueOf(0);
        int[] ratios = {100, 100, 100, 100, 100, 100, 100, 100, 50 ,100};
        log.info(Arrays.toString(ratios).replaceAll("[\\[\\],]", "_"));
        log.info(database.trainFiles.get(String.valueOf(0)).size());
        String path = database.generateBiasedTrainData(ratios);
        database.destroyBiasedTrainData(path);
    }
        
}
