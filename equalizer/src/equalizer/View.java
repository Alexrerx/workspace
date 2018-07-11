package equalizer;
import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import static javax.swing.GroupLayout.Alignment.*;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.IOException;
import java.util.Hashtable;

public class View extends JFrame {
	private JButton playButton, stopButton, openFileButton;
	private JSlider filter1Slider, filter2Slider, filter3Slider;
	private JSlider filter4Slider, filter5Slider, filter6Slider;
	private JSlider echoDelaySlider, echoWetBalanceSlider;
	private JSlider overdriveMaxSlider, overdrivePowerSlider;
	private JCheckBox echoCheckBox, overdriveCheckBox;
	public ChartPanel spectrogramBefore;
	public ChartPanel spectrogramAfter;
	private JFreeChart spectrBefore;
	private JFreeChart spectrAfter;
	private XYSeries spectrPlotBefore;
	private XYSeries spectrPlotAfter;
	private XYDataset spectrPlotDataBefore;
	private XYDataset spectrPlotDataAfter;
	final static int sW = 5; // Ширина слайдеров
	final static int sH = 80; // Высота слайдеров
	final static int cW = 5;	//Ширина чекбокса
	final static int cH = 5;	//Высота чекбокса
	final static int pBW = 40;	//Ширина кнопок плеера
	final static int pBH = 10;	//Высота кнопок плеера
	final static int oBW = 50;	//Ширина кнопки открытия файла
	final static int oBH = 10;	//Высота кнопки открытия файла
	static final int FPS_MIN = -60;
	static final int FPS_MAX = 20;
	static final int FPS_INIT = 10;
	static final int ECHO_DELAY_MIN = 0;
	static final int ECHO_DELAY_MAX = 50000;
	static final int ECHO_DELAY_INIT = 500;
	static final int WET_BALANCE_MAX = 10;
	static final int WET_BALANCE_MIN = 0;
	static final int WET_BALANCE_INIT = 1;
	static final int OVERDRIVE_MAX = 30000;
	static final int OVERDRIVE_MIN = 0;
	static final int OVERDRIVE_INIT = 500;
	static final int OVERDRIVE_POWER_MAX = 10;
	static final int OVERDRIVE_POWER_MIN = 1;
	static final int OVERDRIVE_POWER_INIT = 1;
	
	public View() {
		JFrame frame = new JFrame("Equalizer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initializeComponents();
		GroupLayout layout = new GroupLayout(frame.getContentPane());
		frame.getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true); 
        layout.setAutoCreateContainerGaps(true); 
        layout.setHorizontalGroup(layout.createSequentialGroup()
        	.addGroup(layout.createParallelGroup(CENTER)
        			.addComponent(openFileButton)
        			.addComponent(playButton)
        			.addComponent(echoCheckBox)
        			.addGroup(layout.createSequentialGroup()
        					.addComponent(echoDelaySlider)
        					.addComponent(echoWetBalanceSlider))
        			.addComponent(spectrogramBefore))
        	.addGroup(layout.createParallelGroup(CENTER)
        			.addComponent(filter1Slider))
        	.addGroup(layout.createParallelGroup(CENTER)
        			.addComponent(filter2Slider))
        	.addGroup(layout.createParallelGroup(CENTER)
        			.addComponent(filter3Slider))
        	.addGroup(layout.createParallelGroup(CENTER)
        			.addComponent(filter4Slider))
        	.addGroup(layout.createParallelGroup(CENTER)
        			.addComponent(filter5Slider))
        	.addGroup(layout.createParallelGroup(CENTER)
        			.addComponent(filter6Slider))
        	.addGroup(layout.createParallelGroup(CENTER)
        			.addComponent(stopButton)
        			.addComponent(overdriveCheckBox)
        			.addGroup(layout.createSequentialGroup()
        					.addComponent(overdriveMaxSlider)
        					.addComponent(overdrivePowerSlider))
        			.addComponent(spectrogramAfter))
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
        	.addComponent(openFileButton)
        	.addGroup(layout.createParallelGroup(BASELINE)
        			.addComponent(playButton)
        			.addComponent(stopButton))
        	.addGroup(layout.createParallelGroup(BASELINE)
        			.addComponent(echoCheckBox)
        			.addComponent(overdriveCheckBox))
        	.addGroup(layout.createParallelGroup(BASELINE)
        			.addComponent(filter1Slider)
        			.addComponent(filter2Slider)
        			.addComponent(filter3Slider)
        			.addComponent(filter4Slider)
        			.addComponent(filter5Slider)
        			.addComponent(filter6Slider)
        			.addComponent(echoDelaySlider)
        			.addComponent(echoWetBalanceSlider)
        			.addComponent(overdriveMaxSlider)
        			.addComponent(overdrivePowerSlider))
        	.addGroup(layout.createParallelGroup(BASELINE)
        			.addComponent(spectrogramBefore)
        			.addComponent(spectrogramAfter))
        );
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
	}
	
	private void initializeComponents() {
		spectrPlotBefore = new XYSeries("Input");
		spectrPlotAfter = new XYSeries("Output");
		spectrPlotDataBefore = new XYSeriesCollection(spectrPlotBefore);
		spectrPlotDataAfter = new XYSeriesCollection(spectrPlotAfter);
		spectrBefore = ChartFactory.createXYLineChart("Input",
				"Freq, kHz", "Amplitude, dB", spectrPlotDataBefore, PlotOrientation.VERTICAL,
				true, false, false);
		spectrAfter = ChartFactory.createXYLineChart("Output",
				"Freq, kHz", "Amplitude, dB", spectrPlotDataAfter, PlotOrientation.VERTICAL,
				true, false, false);
        XYPlot xyPlotBefore = (XYPlot) spectrBefore.getPlot();
        xyPlotBefore.setDomainCrosshairVisible(true);
        xyPlotBefore.setRangeCrosshairVisible(true);
        NumberAxis domainXBefore = (NumberAxis) xyPlotBefore.getDomainAxis();
        domainXBefore.setRange(0.00, 22000.00);
        NumberAxis domainYBefore = (NumberAxis) xyPlotBefore.getRangeAxis();
        domainYBefore.setRange(0.00, 200.00);
        XYPlot xyPlotAfter = (XYPlot) spectrAfter.getPlot();
        xyPlotAfter.setDomainCrosshairVisible(true);
        xyPlotAfter.setRangeCrosshairVisible(true);
        NumberAxis domainXAfter = (NumberAxis) xyPlotAfter.getDomainAxis();
        domainXAfter.setRange(0.00, 22000.00);
        NumberAxis domainYAfter = (NumberAxis) xyPlotAfter.getRangeAxis();
        domainYAfter.setRange(0.00, 200.00);
		spectrogramBefore = new ChartPanel(spectrBefore);
		spectrogramAfter = new ChartPanel(spectrAfter);
		spectrogramBefore.setPreferredSize(new Dimension(700, 700));
		spectrogramAfter.setPreferredSize(new Dimension(700, 700));
		playButton = new JButton("Play");
		stopButton = new JButton("Stop");
		openFileButton = new JButton("Open File");
		Hashtable labelTable = new Hashtable();
		for (int counter = FPS_MIN; counter <= FPS_MAX; counter += FPS_INIT ) {
			labelTable.put(new Integer(counter), new JLabel(String.valueOf(counter)));
		}
		filter1Slider = new JSlider(JSlider.VERTICAL, FPS_MIN, FPS_MAX, FPS_INIT);
		filter2Slider = new JSlider(JSlider.VERTICAL, FPS_MIN, FPS_MAX, FPS_INIT);
		filter1Slider.setLabelTable(labelTable);
		filter2Slider.setLabelTable(labelTable);
		filter1Slider.setPaintLabels(true);
		filter2Slider.setPaintLabels(true);
		filter3Slider = new JSlider(JSlider.VERTICAL, FPS_MIN, FPS_MAX, FPS_INIT);
		filter4Slider = new JSlider(JSlider.VERTICAL, FPS_MIN, FPS_MAX, FPS_INIT);
		filter3Slider.setLabelTable(labelTable);
		filter4Slider.setLabelTable(labelTable);
		filter3Slider.setPaintLabels(true);
		filter4Slider.setPaintLabels(true);
		filter5Slider = new JSlider(JSlider.VERTICAL, FPS_MIN, FPS_MAX, FPS_INIT);
		filter6Slider = new JSlider(JSlider.VERTICAL, FPS_MIN, FPS_MAX, FPS_INIT);
		filter5Slider.setLabelTable(labelTable);
		filter6Slider.setLabelTable(labelTable);
		filter5Slider.setPaintLabels(true);
		filter6Slider.setPaintLabels(true);
		filter1Slider.setValue(0);
		filter2Slider.setValue(0);
		filter3Slider.setValue(0);
		filter4Slider.setValue(0);
		filter5Slider.setValue(0);
		filter6Slider.setValue(0);
		echoDelaySlider = new JSlider(JSlider.VERTICAL, ECHO_DELAY_MIN, ECHO_DELAY_MAX, ECHO_DELAY_INIT);
		echoWetBalanceSlider = new JSlider(JSlider.VERTICAL, WET_BALANCE_MIN,
				WET_BALANCE_MAX, WET_BALANCE_INIT);
		echoDelaySlider.setValue(1000);
		echoWetBalanceSlider.setValue(5);
		overdriveMaxSlider = new JSlider(JSlider.VERTICAL, OVERDRIVE_MIN, OVERDRIVE_MAX, OVERDRIVE_INIT);
		overdrivePowerSlider = new JSlider(JSlider.VERTICAL, OVERDRIVE_POWER_MIN,
				OVERDRIVE_MAX, OVERDRIVE_INIT);
		overdriveMaxSlider.setValue(25000);
		overdrivePowerSlider.setValue(1);
		echoCheckBox = new JCheckBox("Echo");
		overdriveCheckBox = new JCheckBox("Overdrive");
		playButton.setSize(pBW, pBH);
		stopButton.setSize(pBW, pBH);
		openFileButton.setSize(oBW, oBH);
		filter1Slider.setSize(sW, sH);
		filter2Slider.setSize(sW, sH);
		echoCheckBox.setSize(cW, cH);
		overdriveCheckBox.setSize(cW, cH);
	}
	
	public JButton getPlayButton() {
		return playButton;
	}
	
	public JButton getStopButton() {
		return stopButton;
	}
	
	public JButton getOpenFileButton() {
		return openFileButton;
	}
	
	public JSlider getFilter1Slider() {
		return filter1Slider;
	}
	
	public JSlider getFilter2Slider() {
		return filter2Slider;
	}
	
	public JSlider getFilter3Slider() {
		return filter3Slider;
	}
	
	public JSlider getFilter4Slider() {
		return filter4Slider;
	}
	
	public JSlider getFilter5Slider() {
		return filter5Slider;
	}
	
	public JSlider getFilter6Slider() {
		return filter6Slider;
	}
	
	public JSlider getEchoDelaySlider() {
		return echoDelaySlider;
	}
	
	public JSlider getEchoWetBalanceSlider() {
		return echoWetBalanceSlider;
	}
	
	public JSlider getOverdriveMaxSlider() {
		return overdriveMaxSlider;
	}
	
	public JSlider getOverdrivePowerSlider() {
		return overdrivePowerSlider;
	}
	
	public JCheckBox getEchoCheckBox() {
		return echoCheckBox;
	}
	
	public JCheckBox getOverdriveCheckBox() {
		return overdriveCheckBox;
	}
	
	public ChartPanel getSpectrBefore() {
		return spectrogramBefore;
	}
	
	public ChartPanel getSpectrAfter() {
		return spectrogramAfter;
	}
	
	public void updateChartBefore(JFreeChart input) {
		spectrogramBefore.setChart(input);
		spectrBefore = input;
	}
	
	public static void main(String[] args) { 
	        try { 
	            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"); 
	        } catch (UnsupportedLookAndFeelException ex) { 
	            ex.printStackTrace(); 
	        } catch (IllegalAccessException ex) { 
	            ex.printStackTrace(); 
	        } catch (InstantiationException ex) { 
	            ex.printStackTrace(); 
	        } catch (ClassNotFoundException ex) { 
	            ex.printStackTrace(); 
	        } 
	 
	        UIManager.put("swing.boldMetal", Boolean.FALSE); 
	 
	        javax.swing.SwingUtilities.invokeLater(new Runnable() { 
	            public void run() { 
	               new View(); 
	            } 
	        }); 
	} 
} 

