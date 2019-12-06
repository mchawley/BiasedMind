/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package u.manishchawley.biasedmind;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.jfree.ui.ApplicationFrame;
import u.manishchawley.biasedmind.setup.ExperimentResult;
import u.manishchawley.biasedmind.utils.Constants;
import u.manishchawley.biasedmind.utils.OutputGraphs;

/**
 *
 * @author User
 */
public class DisplayGraphs extends ApplicationFrame{
    
    public static final Logger log = Logger.getLogger(DisplayGraphs.class);
    
    private List<OutputGraphs> graphs;
    private Map<int[], int[]> resultMap;
    private int[] lows = {1,10,25,50,75};
    
    public DisplayGraphs(String title) throws IOException {
        super(title);
        
        graphs.add(new OutputGraphs(Constants.DATA_CSV_01P));
        graphs.add(new OutputGraphs(Constants.DATA_CSV_10P));
        graphs.add(new OutputGraphs(Constants.DATA_CSV_25P));
        graphs.add(new OutputGraphs(Constants.DATA_CSV_50P));
        graphs.add(new OutputGraphs(Constants.DATA_CSV_75P));
    }
    
    private void generateMapping(){
        resultMap = new HashMap<>();
        
        for(int i=0;i<1024;i++){
            String temp = "0000000000" + Integer.toBinaryString(1023-i);
            temp = temp.substring(temp.length()-10);
            int[] ratios = new int[Constants.NUM_CLASS];
            for(int k=0;k<Constants.NUM_CLASS;k++)
                ratios[k] = Integer.parseInt(temp.substring(k, k+1));
            resultMap.put(ratios, new int[lows.length]);
        }
        
        resultMap.entrySet().forEach((rm) -> {
            for(int j=0;j<lows.length;j++){
                int[] ratios = new int[Constants.NUM_CLASS];
                for(int k=0;k<Constants.NUM_CLASS;k++)
                    ratios[k] = rm.getValue()[k] * lows[j];
                String label = Arrays.toString(ratios).replaceAll("\\s+","").replaceAll("[\\[\\],]", "_");
                List<ExperimentResult> results = graphs.get(j).getResults();
                rm.getValue()[j] = -1;
                for(int i=0;i<results.size();i++){
                    if(results.get(i).getLabel().equals(label)){
                        rm.getValue()[j] = i;
                        break;
                    }
                }
            }
        });
    }
    
}
