package equalizer;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.jfree.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class SpectrogramBefore {
	View GUIclone;
	AudioPlayer aPlayerClone;
	SpectrogramThread spT;
	public SpectrogramBefore(View GUI, AudioPlayer aPlayer) {
		this.GUIclone = GUI;
		this.aPlayerClone = aPlayer;
	}
	class SpectrogramThread extends Thread {
		public void run() {
			short[] samplesBuffer;
			FFT fft = new FFT();
			XYSeries spectrPlotInput = new XYSeries("Input");
			GUIclone.getSpectrBefore().getChart().getXYPlot().setDataset(new XYSeriesCollection(spectrPlotInput));
			int counter = 0;
			double[] amplitudes;
			if (aPlayerClone.getThread() == null) {
				for(;;) {
					if (aPlayerClone != null) break;
				}
			}
			while(aPlayerClone.getThread().isAlive()) {
				if (!aPlayerClone.spectrAfterIsUpdated() && aPlayerClone.spectrBeforeIsUpdated()) {
					samplesBuffer = aPlayerClone.getLeftSampledBuffer();
					fft.setOffsets(samplesBuffer);
					amplitudes = fft.getSpectrumAmpl();
					spectrPlotInput.clear();
					for (counter = 0; counter < 22000; counter += 100) {
						spectrPlotInput.add(counter, 20*Math.log10(Math.abs(Math.ceil(amplitudes[counter]))));
					}
					GUIclone.spectrogramBefore.updateUI();
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
