/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package u.manishchawley.biasedmind.utils;

import com.opencsv.CSVReader;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import org.apache.log4j.BasicConfigurator;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;
import u.manishchawley.biasedmind.setup.ExperimentResult;

/**
 *
 * @author Manish Chawley
 */
public class OutputGraphs extends ApplicationFrame{
    private List<ExperimentResult> results;
    
    private XYSeriesCollection xysc;
    private String title = "String match";
    private int counter;
    
    public OutputGraphs() throws IOException {
        super(OutputGraphs.class.getName());
        
        results = new ArrayList<>();
        CSVReader reader = new CSVReader(new FileReader(Constants.DATA_CSV));
        List<String[]> lines = reader.readAll();
        lines.forEach((line) -> {
            results.add(new ExperimentResult(line));
        });
        
        xysc = new XYSeriesCollection();
        updateDataset(ExperimentResult.normalResult);
        counter=0;
        createPanel();
    }
    
    private void createPanel(){
        JFreeChart chart = createIndividualChart();
        this.add(new ChartPanel(chart), BorderLayout.CENTER);
        
        JButton next = new JButton(">>");
        JButton previous = new JButton("<<");
        
        next.addActionListener((e) -> {
            updateDataset(getResult(1));
        });
        
        previous.addActionListener((e) -> {
            updateDataset(getResult(-1));
        });
        
        this.add(next, BorderLayout.EAST);
        this.add(previous, BorderLayout.WEST);
    }
    
    private void updateDataset(ExperimentResult result){
        xysc.removeAllSeries();
        XYSeries normal, deviant, ratio, direction;
        double directionx, directiony;
        
        normal = new XYSeries("Normal");
        deviant = new XYSeries("Deviant");
        ratio = new XYSeries("Train ratios");
        direction = new XYSeries("Direction");
        directionx = 0.0;
        directiony = 0.0;
        for(int i=0; i<Constants.NUM_CLASS;i++){
            normal.add(i*360.0/Constants.NUM_CLASS, 1);
            deviant.add(i*360.0/Constants.NUM_CLASS, result.getDeviation()[i]);
            directionx+=result.getDeviation()[i]*Math.cos(2.0*Math.PI*i/Constants.NUM_CLASS);
            directiony+=result.getDeviation()[i]*Math.sin(2.0*Math.PI*i/Constants.NUM_CLASS);
            ratio.add(i*360.0/Constants.NUM_CLASS, (double)result.getRatios()[i]/200.0);
            ratio.add(i*360.0/Constants.NUM_CLASS, 0.0);
        }
        direction.add(360+180.0*Math.atan2(directionx, directiony),Math.hypot(directionx, directiony));
        direction.add(0.0,0.0);
        title = result.getLabel();
        xysc.addSeries(normal);
        xysc.addSeries(deviant);
        xysc.addSeries(ratio);
//        xysc.addSeries(direction) ;
    }
    
    public JFreeChart createIndividualChart(){
        
        //making chart according to requirement
        DefaultPolarItemRenderer renderer = new DefaultPolarItemRenderer();
        
        ValueAxis radiusAxis = new NumberAxis();
        
        PolarPlot plot = new PolarPlot(xysc, radiusAxis, renderer){
            @Override
            protected List refreshAngleTicks() {
                List<NumberTick> ticks = new ArrayList<>();
                for(int i=0; i<Constants.NUM_CLASS;i++)
                    ticks.add(new NumberTick(i*360.0/Constants.NUM_CLASS, "Predicted " + i, TextAnchor.CENTER, TextAnchor.BOTTOM_LEFT, 0));
                return ticks;
            }
        };
        
        plot.setAngleTickUnit(new TickUnit(360.0/Constants.NUM_CLASS) {
        });
        plot.getAxis().setRange(0.0, 1.2);
        plot.getAxis().setVisible(false);
        plot.setAngleLabelFont(new Font(Font.DIALOG, Font.PLAIN, 10));
        plot.setAngleGridlinesVisible(true);
        plot.setRadiusGridlinesVisible(false);
        
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesFillPaint(1, Color.GREEN);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesFilled(1, true);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {6.0f, 6.0f}, 0.0f));
        renderer.setSeriesPaint(2, Color.LIGHT_GRAY);
        renderer.setSeriesPaint(3, Color.RED);
//        renderer.setSeriesStroke(2, new BasicStroke(0.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[] {6.0f, 6.0f}, 0.0f));
        
        JFreeChart chart = new JFreeChart(title, new Font(Font.DIALOG, Font.BOLD, 11), plot, false);
        xysc.addChangeListener((dce) -> {
            chart.setTitle(title);
        });
        return chart;
    }
    
    public ExperimentResult getResult(int i) {
        counter+=i;
        if(counter<0)
            counter = results.size()-1;
        if(counter==results.size())
            counter = 0;
        
        return results.get(counter);
    }
    
    private void generateImages() {
        results.forEach((result) -> {
            try {
                updateDataset(result);
                ChartUtilities.saveChartAsPNG(new File(Constants.IMAGE_PATH + "\\" + result.getLabel() + ".png"), createIndividualChart(), 400, 350);
            } catch (IOException ex) {
                Logger.getLogger(OutputGraphs.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        
        EventQueue.invokeLater(() -> {
            OutputGraphs outputGraphs;
            try {
                outputGraphs = new OutputGraphs();
                outputGraphs.pack();
                RefineryUtilities.centerFrameOnScreen(outputGraphs);
                outputGraphs.setVisible(true);
                
            } catch (IOException ex) {
                Logger.getLogger(OutputGraphs.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        OutputGraphs graphs = new OutputGraphs();
//        graphs.generateImages();
        
    }

}
