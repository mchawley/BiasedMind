/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package u.manishchawley.biasedmind;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import u.manishchawley.biasedmind.setup.ExperimentResult;
import u.manishchawley.biasedmind.utils.Constants;
import u.manishchawley.biasedmind.utils.OutputGraphs;

/**
 *
 * @author User
 */
public class DisplayGraphs extends ApplicationFrame{
    
    public static final Logger log = Logger.getLogger(DisplayGraphs.class);
    
    private Map<Integer, OutputGraphs> graphs;
    private List<JSlider> toggles;
    private Map<int[], int[]> resultMap;
    private final int[] lows = {75,50,25,10,1};
    
    public DisplayGraphs(String title) throws IOException {
        super(title);
        
        log.info("Reading and adding experiment results data");
        graphs = new HashMap<>();
        toggles = new ArrayList<>();
        
        graphs.put(lows[0], new OutputGraphs(Constants.DATA_CSV_75P));
        graphs.put(lows[1], new OutputGraphs(Constants.DATA_CSV_50P));
        graphs.put(lows[2], new OutputGraphs(Constants.DATA_CSV_25P));
        graphs.put(lows[3], new OutputGraphs(Constants.DATA_CSV_10P));
        graphs.put(lows[4], new OutputGraphs(Constants.DATA_CSV_01P));
        
        generateMapping();
        createPanel();
    }
    
    private void createPanel(){
        JPanel chartPanel = new JPanel(new GridLayout(3, 3));
        graphs.entrySet().forEach((g) -> {
            chartPanel.add(new ChartPanel(g.getValue().createIndividualChart()));
        });
        
        JPanel sliderPanel = new JPanel(new GridLayout(1,10));
        for(int i=0;i<Constants.NUM_CLASS;i++){
            toggles.add(new JSlider(JSlider.VERTICAL, 0, 1, 1));
           
            sliderPanel.add(toggles.get(i));
        }
        JPanel controls = new JPanel(new BorderLayout());
        controls.add(sliderPanel, BorderLayout.CENTER);
        
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener((e) -> {
            int[] ratios = new int[Constants.NUM_CLASS];
            for(int i=0;i<Constants.NUM_CLASS;i++){
                ratios[i] = toggles.get(i).getValue()==toggles.get(i).getMaximum()?1:0;
            }
            log.info("Ratio selected: " + Arrays.toString(ratios));
            int[] map = getResultMap(ratios);
            log.info("Results mapping: " + Arrays.toString(map));
            for(int i=0;i<map.length;i++){
                if(map[i]==-1)
                    graphs.get(lows[i]).updateDataset(ExperimentResult.normalResult);
                else
                    graphs.get(lows[i]).updateDataset(graphs.get(lows[i]).getResults().get(map[i]));
            }
        });
        
        controls.add(refresh, BorderLayout.SOUTH);
        chartPanel.add(controls);
        this.add(chartPanel, BorderLayout.CENTER);
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
            List<ExperimentResult> results = new ArrayList<>();
            for(int j=0;j<lows.length;j++){
                int[] ratios = new int[Constants.NUM_CLASS];
                for(int k=0;k<Constants.NUM_CLASS;k++){
                    ratios[k] = rm.getKey()[k]==0?lows[j]:Constants.ALL_TRAIN;
                }
                String label = Arrays.toString(ratios).replaceAll("\\s+","").replaceAll("[\\[\\],]", "_");
                log.debug("Label to check: " + label);
                results.clear();
                results.addAll(graphs.get(lows[j]).getResults());
                log.debug("Example checking lot: " + j + ", "+ results.get(0).getLabel());
                rm.getValue()[j] = -1;
                for(int i=0;i<results.size();i++){
//                    log.debug("Labels: " + results.get(i).getLabel());
                    if(results.get(i).getLabel().equals(label)){
                        log.debug("Matched value at: " + i);
                        rm.getValue()[j] = i;
                        break;
                    }
                }
            }
        });
    }
    
    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        
        EventQueue.invokeLater(() -> {
            try {
                DisplayGraphs dg = new DisplayGraphs("Test");
                dg.pack();
                RefineryUtilities.centerFrameOnScreen(dg);
                dg.setVisible(true);
            } catch (IOException ex) {
                log.error(ex);
            }
        });
    }

    private int[] getResultMap(int[] ratios) {
        for(Map.Entry<int[], int[]> rm:resultMap.entrySet())
               if(Arrays.equals(rm.getKey(), ratios))
                    return rm.getValue();
        return null;
    }
    
}
