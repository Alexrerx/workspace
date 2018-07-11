package equalizer;
//Здесь
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class Controller implements ActionListener, ChangeListener, ItemListener {
	private View GUI;
	private boolean isPlaying = false;
	private boolean isLaunched = false;
	JFileChooser fileOpen;
	File file;
	AudioPlayer aPlayer;
	private final int numberOfChannels = 2;
	
	public Controller() {
		this.GUI = new View();
		addActionListeners();
	}
	
	private void addActionListeners() {
		GUI.getPlayButton().addActionListener(this);
		GUI.getStopButton().addActionListener(this);
		GUI.getOpenFileButton().addActionListener(this);
		GUI.getEchoCheckBox().addItemListener(this);
		GUI.getOverdriveCheckBox().addItemListener(this);
		GUI.getFilter1Slider().addChangeListener(this);
		GUI.getFilter2Slider().addChangeListener(this);
		GUI.getFilter3Slider().addChangeListener(this);
		GUI.getFilter4Slider().addChangeListener(this);
		GUI.getFilter5Slider().addChangeListener(this);
		GUI.getFilter6Slider().addChangeListener(this);
		GUI.getEchoDelaySlider().addChangeListener(this);
		GUI.getEchoWetBalanceSlider().addChangeListener(this);
		GUI.getOverdriveMaxSlider().addChangeListener(this);
		GUI.getOverdrivePowerSlider().addChangeListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (((JButton)e.getSource()) == GUI.getPlayButton()) {
			if (!isLaunched) {
				aPlayer.play();
				aPlayer.isPaused = false;
				isPlaying = true;
				isLaunched = true;
				GUI.getPlayButton().setText("Pause");
				SpectrogramBefore specBef = new SpectrogramBefore(GUI, aPlayer);
				SpectrogramAfter specAft = new SpectrogramAfter(GUI, aPlayer);
				specBef.RunForest();
				specAft.RunForest();
			}
			else if(isLaunched && isPlaying){
				//Добавить паузу аудиозаписи
				aPlayer.isPaused = true;
				isPlaying = false;
				GUI.getPlayButton().setText("Play");
			}
			else {
				aPlayer.isPaused = false;
				isPlaying = true;
				GUI.getPlayButton().setText("Paused");
			}
				
		}
		if (((JButton)e.getSource()) == GUI.getOpenFileButton()) {
			fileOpen = new JFileChooser();
			int ret = fileOpen.showDialog(null, "Open File");
			if (ret == fileOpen.APPROVE_OPTION) {
				File file = fileOpen.getSelectedFile();
				aPlayer = new AudioPlayer(file, GUI);
				//Добавить дальнейшие действия с файлом
			}
		}
		if (((JButton)e.getSource()) == GUI.getStopButton()) {
			if (GUI.getStopButton().getText() == "Stop") {
				//Добавить остановку песни 
				GUI.getStopButton().setText("Restart");
			}
			else {
				//Добавить перезапуск файла
				GUI.getStopButton().setText("Stop");
			}
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if (((JSlider)e.getSource()) == GUI.getFilter1Slider()) {
			aPlayer.gain[0] = Math.pow(10, (GUI.getFilter1Slider().getValue() / 20));
		}
		if (((JSlider)e.getSource()) == GUI.getFilter2Slider()) {
			aPlayer.gain[1] = Math.pow(10, (GUI.getFilter2Slider().getValue() / 20));
		}
		if (((JSlider)e.getSource()) == GUI.getFilter3Slider()) {
			aPlayer.gain[2] = Math.pow(10, (GUI.getFilter3Slider().getValue() / 20));
		}
		if (((JSlider)e.getSource()) == GUI.getFilter4Slider()) {
			aPlayer.gain[3] = Math.pow(10, (GUI.getFilter4Slider().getValue() / 20));
		}
		if (((JSlider)e.getSource()) == GUI.getFilter5Slider()) {
			aPlayer.gain[4] = Math.pow(10, (GUI.getFilter5Slider().getValue() / 20));
		}
		if (((JSlider)e.getSource()) == GUI.getFilter6Slider()) {
			aPlayer.gain[5] = Math.pow(10, (GUI.getFilter6Slider().getValue() / 20));
		}
		if (((JSlider)e.getSource()) == GUI.getEchoDelaySlider()) {
			aPlayer.echoDelay = GUI.getEchoDelaySlider().getValue();
		}
		if (((JSlider)e.getSource()) == GUI.getEchoWetBalanceSlider()) {
			aPlayer.echoWetBalance = (double)GUI.getEchoWetBalanceSlider().getValue() / 10;
		}
		if (((JSlider)e.getSource()) == GUI.getOverdriveMaxSlider()) {
			aPlayer.overdriveMax = GUI.getOverdriveMaxSlider().getValue();
		}
		if (((JSlider)e.getSource()) == GUI.getOverdrivePowerSlider()) {
			aPlayer.overdrivePower = GUI.getOverdrivePowerSlider().getValue();
		}
		
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (((JCheckBox)e.getSource()) == GUI.getEchoCheckBox()) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				//Добавить включение эффекта ЭХО
				aPlayer.echoActive = true;
			}
			else {
				//Добавить выключение эффекта ЭХО
				aPlayer.echoActive = false;
			}
		}
		if (((JCheckBox)e.getSource()) == GUI.getOverdriveCheckBox()) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				aPlayer.overdriveActive = true;
			}
			else {
				aPlayer.overdriveActive = false;
			}	
		}
	}
}


