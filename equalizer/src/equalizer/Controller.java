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
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (((JButton)e.getSource()) == GUI.getPlayButton()) {
			if (!isPlaying) {
				aPlayer.play();
				isPlaying = true;
				GUI.getPlayButton().setText("Pause");
				Spectrogram spec = new Spectrogram(GUI, aPlayer);
				spec.RunForest();
			}
			else {
				//Добавить паузу аудиозаписи
				aPlayer.pause();
				isPlaying = false;
				GUI.getPlayButton().setText("Play");
			}
		}
		if (((JButton)e.getSource()) == GUI.getOpenFileButton()) {
			fileOpen = new JFileChooser();
			int ret = fileOpen.showDialog(null, "Open File");
			if (ret == fileOpen.APPROVE_OPTION) {
				File file = fileOpen.getSelectedFile();
				aPlayer = new AudioPlayer(file);
				//Добавить дальнейшие действия с файлом
			}
		}
		if (((JButton)e.getSource()) == GUI.getStopButton()) {
			if (GUI.getStopButton().getText() == "Stop") {
				//Добавить остановку песни 
				aPlayer.stop();
				GUI.getStopButton().setText("Restart");
			}
			else {
				//Добавить перезапуск файла
				aPlayer.restart();
				GUI.getStopButton().setText("Stop");
			}
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if (((JSlider)e.getSource()) == GUI.getFilter1Slider()) {
			//Добавить изменение состояния фильтра 1
		}
		if (((JSlider)e.getSource()) == GUI.getFilter2Slider()) {
			//Добавить изменение состояния фильтра 2
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
				//Добавить включение эффекта Овердрайв
			}
			else {
				//Добавить выключение эффекта Овердрайв
			}	
		}
	}
}
	/*class Spectrogram extends SwingWorker<JFreeChart, Integer> {
		@Override
		protected JFreeChart doInBackground() throws Exception {
			short[] samplesBuffer;
			FFT fft = new FFT();
			XYSeries spectrPlot;
			XYDataset spectrPlotData;
			JFreeChart spectr = null;
			double[] amplitudes;
			while (aPlayer.getThread() == null) {
			}
			while (aPlayer.getThread().isAlive()) {
				if (aPlayer.spectrBeforeIsUpdated()) {
					aPlayer.getThread().sleep(50);
					samplesBuffer = aPlayer.getSampledBuffer();
					fft.setOffsets(samplesBuffer);
					amplitudes = fft.getSpectrumAmpl();
					spectrPlot = new XYSeries("Input");
					for (int counter = 0; counter < 0; counter += 500) {
						spectrPlot.add(counter / 1000, 20*Math.log10(amplitudes[counter]));
					}
					spectrPlotData = new XYSeriesCollection(spectrPlot);
					spectr = ChartFactory.createXYLineChart("Input",
							"Freq, kHz", "Amplitude, dB", spectrPlotData, PlotOrientation.VERTICAL,
							true, false, false);
					return spectr;
				}
			}
			return spectr;
		}
		@Override
		protected void done() {
			try {
				GUI.spectrogramBefore =  new ChartPanel(get());
				GUI.spectrogramBefore.setPreferredSize(new Dimension(700, 700));
				GUI.revalidate();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
	}*/


