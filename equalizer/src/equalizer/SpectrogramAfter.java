package equalizer;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import equalizer.SpectrogramBefore.SpectrogramThread;

public class SpectrogramAfter {
		View GUIclone;
		AudioPlayer aPlayerClone;
		SpectrogramThread spT;
		public SpectrogramAfter(View GUI, AudioPlayer aPlayer) {
			this.GUIclone = GUI;
			this.aPlayerClone = aPlayer;
		}
		class SpectrogramThread extends Thread {
			public void run() {
				short[] samplesBuffer;
				FFT fft = new FFT();
				XYSeries spectrPlotOutput = new XYSeries("Output");
				GUIclone.getSpectrAfter().getChart().getXYPlot().setDataset(new XYSeriesCollection(spectrPlotOutput));
				int counter = 0;
				double[] amplitudes;
				if (aPlayerClone.getThread() == null) {
					for(;;) {
						if (aPlayerClone != null) break;
					}
				}
				while(aPlayerClone.getThread().isAlive()) {
					if (aPlayerClone.spectrAfterIsUpdated() && !aPlayerClone.spectrBeforeIsUpdated()) {
						samplesBuffer = aPlayerClone.getLeftSampledBufferOut();
						fft.setOffsets(samplesBuffer);
						amplitudes = fft.getSpectrumAmpl();
						spectrPlotOutput.clear();
						for (counter = 0; counter < 22000; counter += 100) {
							spectrPlotOutput.add(counter, 20*Math.log10(Math.abs(amplitudes[counter])));
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

