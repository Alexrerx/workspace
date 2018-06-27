package equalizer;

import java.io.*;
import javax.sound.sampled.*;
import java.math.*;
import java.nio.ByteBuffer;

public class AudioPlayer {
	private final int MAX_BUFF_SIZE = 50100;
	private volatile byte[] buffer; //Используется для хранения битовых отсчетов, по два бита на один отсчет
	private volatile short[] leftSampleBuffer;
	private volatile short[] rightSampleBuffer;
	private volatile short[][] leftSampleBufferAfterFilter;
	private volatile short[][] rightSampleBufferAfterFilter;
	private SourceDataLine audioLine;
	public boolean isPaused = false;
	public boolean echoActive = false;
	public boolean overdriveActive = false;
	public double[] gain = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
	private volatile boolean spectrBeforeUpdated = false;
	private volatile boolean spectrAfterUpdated = false;
	private double[] fftOffsetsBefore;
	private double[] fftOffsetsAfter;
	private LineListener listener;
	private AudioInputStream ais;
	private File apFile;
	private AudioThread at;
	public AudioPlayer(File file, View GUI) {
		apFile = file;
		buffer = new byte[MAX_BUFF_SIZE];
		rightSampleBuffer = new short[MAX_BUFF_SIZE / 4];
		leftSampleBuffer = new short[MAX_BUFF_SIZE / 4];
		rightSampleBufferAfterFilter = new short[6][rightSampleBuffer.length];
		leftSampleBufferAfterFilter = new short[6][leftSampleBuffer.length];
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
				Echo echoEffect = new Echo(MAX_BUFF_SIZE / 4);
				Overdrive overdriveEffect = new Overdrive(MAX_BUFF_SIZE / 4);
				Filter filter0 = new Filter(FilterInfo.COEFFS_OF_BAND_0);
				Filter filter1 = new Filter(FilterInfo.COEFFS_OF_BAND_1);
				Filter filter2 = new Filter(FilterInfo.COEFFS_OF_BAND_2);
				Filter filter3 = new Filter(FilterInfo.COEFFS_OF_BAND_3);
				Filter filter4 = new Filter(FilterInfo.COEFFS_OF_BAND_4);
				Filter filter5 = new Filter(FilterInfo.COEFFS_OF_BAND_5);
				audioLine.start();
				while (((number = ais.read(buffer)) != -1)&&(!this.isInterrupted())) {
					byteToShortArray();
					spectrAfterUpdated = false;
					spectrBeforeUpdated = true;
					if (isPaused) {
						for(;;) {
							try {
								this.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							if (!isPaused ) {
								break;
							}
						}
					}
					spectrAfterUpdated = true;
					if (echoActive) {
						leftSampleBuffer = echoEffect.createEffect(leftSampleBuffer);
						rightSampleBuffer = echoEffect.createEffect(rightSampleBuffer);				
					}
					else {
						echoEffect.endEffect(!echoActive);
					}
					if (overdriveActive) {
						leftSampleBuffer = overdriveEffect.createEffect(leftSampleBuffer);
						rightSampleBuffer = overdriveEffect.createEffect(rightSampleBuffer);
					}
					filter0.setGain(gain[0]);
					filter0.setInOffsets(leftSampleBuffer);
					leftSampleBufferAfterFilter[0] = filter0.getFilteredData();
					filter0.setInOffsets(rightSampleBuffer);
					rightSampleBufferAfterFilter[0] = filter0.getFilteredData();
					
					filter1.setGain(gain[1]);//GUIclone.getFilter2Slider().getValue()));
					filter1.setInOffsets(leftSampleBuffer);
					leftSampleBufferAfterFilter[1] = filter1.getFilteredData();
					filter1.setInOffsets(rightSampleBuffer);
					rightSampleBufferAfterFilter[1] = filter1.getFilteredData();
					
					filter2.setGain(gain[2]);//GUIclone.getFilter3Slider().getValue()));
					filter2.setInOffsets(leftSampleBuffer);
					leftSampleBufferAfterFilter[2] = filter2.getFilteredData();
					filter2.setInOffsets(rightSampleBuffer);
					rightSampleBufferAfterFilter[2] = filter2.getFilteredData();

					filter3.setGain(gain[3]);//GUIclone.getFilter4Slider().getValue()));
					filter3.setInOffsets(leftSampleBuffer);
					leftSampleBufferAfterFilter[3] = filter3.getFilteredData();
					filter3.setInOffsets(rightSampleBuffer);
					rightSampleBufferAfterFilter[3] = filter3.getFilteredData();
					
					filter4.setGain(gain[4]);//GUIclone.getFilter5Slider().getValue()));
					filter4.setInOffsets(leftSampleBuffer);
					leftSampleBufferAfterFilter[4] = filter4.getFilteredData();
					filter4.setInOffsets(rightSampleBuffer);
					rightSampleBufferAfterFilter[4] = filter4.getFilteredData();

					filter5.setGain(gain[5]);//GUIclone.getFilter6Slider().getValue()));
					filter5.setInOffsets(leftSampleBuffer);
					leftSampleBufferAfterFilter[5] = filter5.getFilteredData();
					filter5.setInOffsets(rightSampleBuffer);
					rightSampleBufferAfterFilter[5] = filter5.getFilteredData();
					sumOffsets();
					
					shortToByteArray();
					audioLine.write(buffer, 0, number);
					spectrAfterUpdated = true;
					spectrBeforeUpdated = false;
				}
			} catch (IOException e) {
					e.printStackTrace();
			}
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
	public void sumOffsets() {
		int typeCheck;
		for (int counter = 0; counter < leftSampleBuffer.length; ++counter) {
			typeCheck = leftSampleBufferAfterFilter[0][counter] +
					leftSampleBufferAfterFilter[1][counter] +
					leftSampleBufferAfterFilter[2][counter] +
					leftSampleBufferAfterFilter[3][counter] +
					leftSampleBufferAfterFilter[4][counter] +
					leftSampleBufferAfterFilter[5][counter];
			if (typeCheck > Short.MIN_VALUE && typeCheck < Short.MAX_VALUE) {
				this.leftSampleBuffer[counter] = (short)typeCheck;
			}
			else if (typeCheck < Short.MIN_VALUE) {
				this.leftSampleBuffer[counter] = Short.MIN_VALUE;
			}
			else {
				this.leftSampleBuffer[counter] = Short.MAX_VALUE;
			}
			typeCheck = rightSampleBufferAfterFilter[0][counter] +
					rightSampleBufferAfterFilter[1][counter] +
					rightSampleBufferAfterFilter[2][counter] +
					rightSampleBufferAfterFilter[3][counter] +
					rightSampleBufferAfterFilter[4][counter] +
					rightSampleBufferAfterFilter[5][counter];
			if (typeCheck > Short.MIN_VALUE && typeCheck < Short.MAX_VALUE) {
				this.rightSampleBuffer[counter] = (short)typeCheck;
			}
			else if (typeCheck < Short.MIN_VALUE) {
				this.rightSampleBuffer[counter] = Short.MIN_VALUE;
			}
			else {
				this.rightSampleBuffer[counter] = Short.MAX_VALUE;
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
