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
	private JSlider filter1Slider, filter2Slider;
	private JCheckBox echoCheckBox, overdriveCheckBox;
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
        			.addComponent(filter1Slider))
        	.addGroup(layout.createParallelGroup(CENTER)
        			.addComponent(stopButton)
        			.addComponent(overdriveCheckBox)
        			.addComponent(filter2Slider))
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
        			.addComponent(filter2Slider))
        );
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
	}
	
	private void initializeComponents() {
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
	
	public JCheckBox getEchoCheckBox() {
		return echoCheckBox;
	}
	
	public JCheckBox getOverdriveCheckBox() {
		return overdriveCheckBox;
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

