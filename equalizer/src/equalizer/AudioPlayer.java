package equalizer;

import java.io.*;
import javax.sound.sampled.*;
import java.math.*;
import java.nio.ByteBuffer;

public class AudioPlayer {
	private final int MAX_BUFF_SIZE = 160320;
	private volatile byte[] buffer; //Используется для хранения битовых отсчетов, по два бита на один отсчет
	private volatile short[] leftSampleBuffer;
	private volatile short[] rightSampleBuffer;
	private volatile short[] leftSampleBufferCopy;
	private volatile short[] rightSampleBufferCopy;
	private volatile double gain;
	private SourceDataLine audioLine;
	private boolean isPaused = false;
	public boolean echoActive = false;
	private View GUIclone;
	private volatile boolean spectrBeforeUpdated = false;
	private volatile boolean spectrAfterUpdated = false;
	private double[] fftOffsetsBefore;
	private double[] fftOffsetsAfter;
	private LineListener listener;
	private AudioInputStream ais;
	private File apFile;
	private AudioThread at;
	public AudioPlayer(File file, View GUI) {
		GUIclone = GUI;
		apFile = file;
		buffer = new byte[MAX_BUFF_SIZE];
		rightSampleBuffer = new short[MAX_BUFF_SIZE / 4];
		leftSampleBuffer = new short[MAX_BUFF_SIZE / 4];
		rightSampleBufferCopy = new short[MAX_BUFF_SIZE / 4];
		leftSampleBufferCopy = new short[MAX_BUFF_SIZE / 4];
		try {
			ais = AudioSystem.getAudioInputStream(file);
			AudioFormat format = ais.getFormat();			
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			audioLine = (SourceDataLine)AudioSystem.getLine(info);
			audioLine.open(format);
			at = new AudioThread();
			ais.read(buffer);
		}
		catch (IOException | UnsupportedAudioFileException | LineUnavailableException exc) {
			exc.printStackTrace();
		}
	}
	public void play() {
		if (!at.runs) {
			at.start();
		}
		else at.playA();
	}
	
	public void pause() {
		at.pauseA();
	}
	
	public void stop() {
		at.stopA();
	}
	
	public void restart() {
		at.interrupt();
		at = new AudioThread();
		at.run();
		try {
			at.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public class AudioThread extends Thread {
		public boolean runs = false;
		public void run() {
			runs = true;
			try {
				ais.close();
				try {
					ais = AudioSystem.getAudioInputStream(apFile);
				} catch (UnsupportedAudioFileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int number;
				Echo echoEffectLeft = new Echo();
				Echo echoEffectRight = new Echo();
				Filter filter0 = new Filter(FilterInfo.COEFFS_OF_BAND_0);
				Filter filter1 = new Filter(FilterInfo.COEFFS_OF_BAND_1);
				Filter filter2 = new Filter(FilterInfo.COEFFS_OF_BAND_2);
				Filter filter3 = new Filter(FilterInfo.COEFFS_OF_BAND_3);
				Filter filter4 = new Filter(FilterInfo.COEFFS_OF_BAND_4);
				Filter filter5 = new Filter(FilterInfo.COEFFS_OF_BAND_5);
				short[] leftSampleBufferT = new short[leftSampleBuffer.length];
				short[] rightSampleBufferT = new short[rightSampleBuffer.length];
				audioLine.start();
				while (((number = ais.read(buffer)) != -1)&&(!this.isInterrupted())) {
					byteToShortArray();
					spectrAfterUpdated = false;
					spectrBeforeUpdated = true;
					try {
						this.sleep(0);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					spectrAfterUpdated = true;
					if (echoActive) {
						leftSampleBuffer = echoEffectLeft.createEffect(leftSampleBuffer);
						rightSampleBuffer = echoEffectRight.createEffect(rightSampleBuffer);				
					}
					else {
						echoEffectLeft.endEffect(!echoActive);
						echoEffectRight.endEffect(!echoActive);
					}
					
					gain = Math.pow(10, (GUIclone.getFilter1Slider().getValue() / 20));
					filter0.setGain(gain);
					filter0.setInOffsets(leftSampleBuffer);
					leftSampleBufferCopy = filter0.getFilteredData();
					filter0.setInOffsets(rightSampleBuffer);
					rightSampleBufferCopy = filter0.getFilteredData();
					
					gain = Math.pow(10, (GUIclone.getFilter2Slider().getValue() / 20));
					filter1.setGain(gain);//GUIclone.getFilter2Slider().getValue()));
					filter1.setInOffsets(leftSampleBuffer);
					leftSampleBufferT = filter1.getFilteredData();
					filter1.setInOffsets(rightSampleBuffer);
					rightSampleBufferT = filter1.getFilteredData();
					sumOffsets(leftSampleBufferT, rightSampleBufferT);
					
					gain = Math.pow(10, (GUIclone.getFilter3Slider().getValue() / 20));
					filter2.setGain(gain);//GUIclone.getFilter3Slider().getValue()));
					filter2.setInOffsets(leftSampleBuffer);
					leftSampleBufferT = filter2.getFilteredData();
					filter2.setInOffsets(rightSampleBuffer);
					rightSampleBufferT = filter2.getFilteredData();
					sumOffsets(leftSampleBufferT, rightSampleBufferT);
					
					gain = Math.pow(10, (GUIclone.getFilter4Slider().getValue() / 20));
					filter3.setGain(gain);//GUIclone.getFilter4Slider().getValue()));
					filter3.setInOffsets(leftSampleBuffer);
					leftSampleBufferT = filter3.getFilteredData();
					filter3.setInOffsets(rightSampleBuffer);
					rightSampleBufferT = filter3.getFilteredData();
					sumOffsets(leftSampleBufferT, rightSampleBufferT);
					
					gain = Math.pow(10, (GUIclone.getFilter5Slider().getValue() / 20));
					filter4.setGain(gain);//GUIclone.getFilter5Slider().getValue()));
					filter4.setInOffsets(leftSampleBuffer);
					leftSampleBufferT = filter4.getFilteredData();
					filter4.setInOffsets(rightSampleBuffer);
					rightSampleBufferT = filter4.getFilteredData();
					sumOffsets(leftSampleBufferT, rightSampleBufferT);
					
					gain = Math.pow(10, (GUIclone.getFilter6Slider().getValue() / 20));
					filter5.setGain(gain);//GUIclone.getFilter6Slider().getValue()));
					filter5.setInOffsets(leftSampleBuffer);
					leftSampleBufferT = filter5.getFilteredData();
					filter5.setInOffsets(rightSampleBuffer);
					rightSampleBufferT = filter5.getFilteredData();
					sumOffsets(leftSampleBufferT, rightSampleBufferT);
					leftSampleBuffer = leftSampleBufferCopy;
					rightSampleBuffer = rightSampleBufferCopy;
					rightSampleBufferCopy = new short[MAX_BUFF_SIZE / 4];
					leftSampleBufferCopy = new short[MAX_BUFF_SIZE / 4];
					
					shortToByteArray();
					audioLine.write(buffer, 0, number);
					spectrAfterUpdated = true;
					spectrBeforeUpdated = false;
					try {
						this.sleep(0);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
					e.printStackTrace();
			}
		}
		public void pauseA() {
			audioLine.stop();
			return;
		}
		public void playA() {
			audioLine.start();
		}
		public void stopA() {
			audioLine.stop();
			audioLine.flush();
		}
	}
	public void byteToShortArray() {
		int sampleCounter = 0;
		ByteBuffer conversion = ByteBuffer.allocate(2);
		for (int counter = 0; counter < buffer.length; counter += 4) {
			conversion.put(1, buffer[counter]);
			conversion.put(0, buffer[counter + 1]);
			leftSampleBuffer[sampleCounter] = conversion.getShort(0);
			conversion.put(1, buffer[counter + 2]);
			conversion.put(0, buffer[counter + 3]);
			rightSampleBuffer[sampleCounter] = conversion.getShort(0);
			++sampleCounter;
		}
		return;
	}
	public void shortToByteArray() {
		int byteCounter = 0;
		for (int counter = 0; counter < MAX_BUFF_SIZE / 4; ++counter) {
			buffer[byteCounter] = (byte)(leftSampleBuffer[counter]);
			buffer[byteCounter + 1] = (byte)(leftSampleBuffer[counter] >> 8);
			buffer[byteCounter + 2] = (byte)(rightSampleBuffer[counter]);
			buffer[byteCounter + 3] = (byte)(rightSampleBuffer[counter] >> 8);
			byteCounter += 4;
		}
		return;
	}
	public void sumOffsets(short[] offsetsToSumLeft, short[] offsetsToSumRight) {
		int typeCheck;
		for (int counter = 0; counter < offsetsToSumRight.length; ++counter) {
			typeCheck = offsetsToSumLeft[counter] + this.leftSampleBufferCopy[counter];
			if (typeCheck > Short.MIN_VALUE && typeCheck < Short.MAX_VALUE) {
				this.leftSampleBufferCopy[counter] = (short)typeCheck;
			}
			else if (typeCheck < Short.MIN_VALUE) {
				this.leftSampleBufferCopy[counter] = Short.MIN_VALUE;
			}
			else {
				this.leftSampleBufferCopy[counter] = Short.MAX_VALUE;
			}
			typeCheck = offsetsToSumRight[counter] + this.rightSampleBufferCopy[counter];
			if (typeCheck > Short.MIN_VALUE && typeCheck < Short.MAX_VALUE) {
				this.rightSampleBufferCopy[counter] = (short)typeCheck;
			}
			else if (typeCheck < Short.MIN_VALUE) {
				this.rightSampleBufferCopy[counter] = Short.MIN_VALUE;
			}
			else {
				this.rightSampleBufferCopy[counter] = Short.MAX_VALUE;
			}
		}
	}
	public int getSamplingFreq() {
		return MAX_BUFF_SIZE;
	}
	public boolean spectrAfterIsUpdated() {
		return spectrAfterUpdated;
	}
	public boolean spectrBeforeIsUpdated() {
		return spectrBeforeUpdated;
	}
	public Thread getThread() {
		return at;
	}
	public short[] getLeftSampledBuffer() {
		return leftSampleBuffer;
	}
	public short[] getRightSampledBuffer() {
		return rightSampleBuffer;
	}
}
