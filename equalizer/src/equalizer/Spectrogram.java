package equalizer;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.jfree.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Spectrogram {
	View GUIclone;
	AudioPlayer aPlayerClone;
	SpectrogramThread spT;
	public Spectrogram(View GUI, AudioPlayer aPlayer) {
		this.GUIclone = GUI;
		this.aPlayerClone = aPlayer;
	}
	class SpectrogramThread extends Thread {
		public void run() {
			short[] samplesBuffer;
			FFT fft = new FFT();
			XYSeries spectrPlotInput = new XYSeries("Input");
			XYSeries spectrPlotOutput = new XYSeries("Output");
			GUIclone.getSpectrBefore().getChart().getXYPlot().setDataset(new XYSeriesCollection(spectrPlotInput));
			GUIclone.getSpectrAfter().getChart().getXYPlot().setDataset(new XYSeriesCollection(spectrPlotOutput));
			int counter = 0;
			double[] amplitudes;
			if (aPlayerClone.getThread() == null) {
				for(;;) {
					if (aPlayerClone != null) break;
				}
			}
			while(aPlayerClone.getThread().isAlive()) {
				if (aPlayerClone.spectrBeforeIsUpdated()) {
					samplesBuffer = aPlayerClone.getSampledBuffer();
					fft.setOffsets(samplesBuffer);
					amplitudes = fft.getSpectrumAmpl();
					spectrPlotInput.clear();
					for (counter = 0; counter < 22000; counter += 100) {
						spectrPlotInput.add(counter, 20*Math.log10(Math.abs(Math.ceil(amplitudes[counter]))));
					}
					GUIclone.spectrogramBefore.updateUI();
					System.gc();
				}
				if (aPlayerClone.spectrAfterIsUpdated()) {
					samplesBuffer = aPlayerClone.getSampledBuffer();
					fft.setOffsets(samplesBuffer);
					amplitudes = fft.getSpectrumAmpl();
					spectrPlotOutput.clear();
					for (counter = 0; counter < 22000; counter += 100) {
						spectrPlotOutput.add(counter, 20*Math.log10(Math.abs(Math.ceil(amplitudes[counter]))));
					}
					GUIclone.spectrogramAfter.updateUI();
					System.gc();
				}
			}
		}
	}
	public void RunForest() {
		spT = new SpectrogramThread();
		spT.start();
	}
}
