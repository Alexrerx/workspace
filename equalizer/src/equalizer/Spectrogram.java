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
			XYSeries spectrPlot;
			JFreeChart spectr;
			int counter = 0;
			double[] amplitudes;
			if (aPlayerClone.getThread() == null) {
				for(;;) {
					if (aPlayerClone != null) break;
				}
			}
			while(aPlayerClone.getThread().isAlive()) {
				if (aPlayerClone.spectrBeforeIsUpdated()) {
					//samplesBuffer = aPlayerClone.getSampledBuffer();
					//fft.setOffsets(samplesBuffer);
					//amplitudes = fft.getSpectrumAmpl();
					spectrPlot = new XYSeries("Input");
					for (counter = 1; counter < 22000; counter += 100) {
						spectrPlot.add(counter, 20*Math.log10(Math.abs(Math.ceil(counter/*amplitudes[counter]*/))));
					}
					GUIclone.getSpectrBefore().getChart().getXYPlot().setDataset(new XYSeriesCollection(spectrPlot));
					GUIclone.spectrogramBefore.updateUI();
					System.gc();
				}
			}
		}
	}
	public void RunForest() {
		spT = new SpectrogramThread();
		try {
			SwingUtilities.invokeAndWait(spT);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//SwingUtilities.invokeLater(spT);
		spT.start();
	}
}
