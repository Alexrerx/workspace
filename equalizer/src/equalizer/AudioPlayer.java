package equalizer;

import java.io.*;
import javax.sound.sampled.*;
import java.math.*;
import java.nio.ByteBuffer;

public class AudioPlayer {
	private final int MAX_BUFF_SIZE = 22000 * 4;
	private volatile byte[] buffer; //Используется для хранения битовых отсчетов, по два бита на один отсчет
	private volatile short[] leftSampleBufferIn;
	private volatile short[] rightSampleBufferIn;
	private volatile short[] leftSampleBufferOut;
	private volatile short[] rightSampleBufferOut;
	private volatile short[][] leftSampleBufferAfterFilter;
	private volatile short[][] rightSampleBufferAfterFilter;
	private SourceDataLine audioLine;
	public boolean isPaused = false;
	public boolean echoActive = false;
	public double echoWetBalance = 0.5;
	public int echoDelay = 1000;
	public boolean overdriveActive = false;
	public int overdriveMax = 25000;
	public int overdrivePower = 1;
	public double[] gain = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
	private volatile boolean spectrBeforeUpdated = false;
	private volatile boolean spectrAfterUpdated = false;
	private AudioInputStream ais;
	private File apFile;
	private AudioThread at;
	public AudioPlayer(File file, View GUI) {
		apFile = file;
		buffer = new byte[MAX_BUFF_SIZE];
		rightSampleBufferIn = new short[MAX_BUFF_SIZE / 4];
		leftSampleBufferIn = new short[MAX_BUFF_SIZE / 4];
		rightSampleBufferOut = new short[MAX_BUFF_SIZE / 4];
		leftSampleBufferOut = new short[MAX_BUFF_SIZE / 4];
		rightSampleBufferAfterFilter = new short[6][rightSampleBufferOut.length];
		leftSampleBufferAfterFilter = new short[6][leftSampleBufferOut.length];
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
				Echo echoEffectLeft = new Echo(MAX_BUFF_SIZE / 4);
				Echo echoEffectRight = new Echo(MAX_BUFF_SIZE / 4);
				Overdrive overdriveEffect = new Overdrive(MAX_BUFF_SIZE / 4);
				Filter filter0Right = new Filter(FilterInfo.COEFFS_OF_BAND_0, MAX_BUFF_SIZE / 4);
				Filter filter1Right = new Filter(FilterInfo.COEFFS_OF_BAND_1, MAX_BUFF_SIZE / 4);
				Filter filter2Right = new Filter(FilterInfo.COEFFS_OF_BAND_2, MAX_BUFF_SIZE / 4);
				Filter filter3Right = new Filter(FilterInfo.COEFFS_OF_BAND_3, MAX_BUFF_SIZE / 4);
				Filter filter4Right = new Filter(FilterInfo.COEFFS_OF_BAND_4, MAX_BUFF_SIZE / 4);
				Filter filter5Right = new Filter(FilterInfo.COEFFS_OF_BAND_5, MAX_BUFF_SIZE / 4);
				Filter filter0Left = new Filter(FilterInfo.COEFFS_OF_BAND_0, MAX_BUFF_SIZE / 4);
				Filter filter1Left = new Filter(FilterInfo.COEFFS_OF_BAND_1, MAX_BUFF_SIZE / 4);
				Filter filter2Left = new Filter(FilterInfo.COEFFS_OF_BAND_2, MAX_BUFF_SIZE / 4);
				Filter filter3Left = new Filter(FilterInfo.COEFFS_OF_BAND_3, MAX_BUFF_SIZE / 4);
				Filter filter4Left = new Filter(FilterInfo.COEFFS_OF_BAND_4, MAX_BUFF_SIZE / 4);
				Filter filter5Left = new Filter(FilterInfo.COEFFS_OF_BAND_5, MAX_BUFF_SIZE / 4);
				audioLine.start();
				while (((number = ais.read(buffer)) != -1)&&(!this.isInterrupted())) {
					byteToShortArray();
					spectrBeforeUpdated = true;
					spectrAfterUpdated = false;
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
					leftSampleBufferOut = leftSampleBufferIn.clone();
					rightSampleBufferOut = rightSampleBufferIn.clone();
					System.gc();
					if (echoActive) {
						echoEffectLeft.setDelay(echoDelay);
						echoEffectLeft.setWetBalance(echoWetBalance);
						leftSampleBufferOut = echoEffectLeft.createEffect(leftSampleBufferOut);
						echoEffectRight.setDelay(echoDelay);
						echoEffectRight.setWetBalance(echoWetBalance);
						rightSampleBufferOut = echoEffectRight.createEffect(rightSampleBufferOut);				
					}
					else {
						echoEffectLeft.endEffect(!echoActive);
						echoEffectRight.endEffect(!echoActive);
					}
					if (overdriveActive) {
						overdriveEffect.setOverdriveMax(overdriveMax);
						overdriveEffect.setOverdrivePower(overdrivePower);
						leftSampleBufferOut = overdriveEffect.createEffect(leftSampleBufferOut);
						rightSampleBufferOut = overdriveEffect.createEffect(rightSampleBufferOut);
					}
					filter0Left.setGain(gain[0]);
					filter0Right.setGain(gain[0]);
					filter0Left.setInOffsets(leftSampleBufferOut);
					leftSampleBufferAfterFilter[0] = filter0Left.getFilteredData();
					filter0Right.setInOffsets(rightSampleBufferOut);
					rightSampleBufferAfterFilter[0] = filter0Right.getFilteredData();
					
					filter1Left.setGain(gain[1]);
					filter1Right.setGain(gain[1]);
					filter1Left.setInOffsets(leftSampleBufferOut);
					leftSampleBufferAfterFilter[1] = filter1Left.getFilteredData();
					filter1Right.setInOffsets(rightSampleBufferOut);
					rightSampleBufferAfterFilter[1] = filter1Right.getFilteredData();

					filter2Left.setGain(gain[2]);
					filter2Right.setGain(gain[2]);
					filter2Left.setInOffsets(leftSampleBufferOut);
					leftSampleBufferAfterFilter[2] = filter2Left.getFilteredData();
					filter2Right.setInOffsets(rightSampleBufferOut);
					rightSampleBufferAfterFilter[2] = filter2Right.getFilteredData();
					
					filter3Left.setGain(gain[3]);
					filter3Right.setGain(gain[3]);
					filter3Left.setInOffsets(leftSampleBufferOut);
					leftSampleBufferAfterFilter[3] = filter3Left.getFilteredData();
					filter3Right.setInOffsets(rightSampleBufferOut);
					rightSampleBufferAfterFilter[3] = filter3Right.getFilteredData();
					
					filter4Left.setGain(gain[4]);
					filter4Right.setGain(gain[4]);
					filter4Left.setInOffsets(leftSampleBufferOut);
					leftSampleBufferAfterFilter[4] = filter4Left.getFilteredData();
					filter4Right.setInOffsets(rightSampleBufferOut);
					rightSampleBufferAfterFilter[4] = filter4Right.getFilteredData();
					
					filter5Left.setGain(gain[5]);
					filter5Right.setGain(gain[5]);
					filter5Left.setInOffsets(leftSampleBufferOut);
					leftSampleBufferAfterFilter[5] = filter5Left.getFilteredData();
					filter5Right.setInOffsets(rightSampleBufferOut);
					rightSampleBufferAfterFilter[5] = filter5Right.getFilteredData();
					
					sumOffsets();
					spectrBeforeUpdated = false;
					spectrAfterUpdated = true;
					shortToByteArray();
					audioLine.write(buffer, 0, number);
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
			leftSampleBufferIn[sampleCounter] = conversion.getShort(0);
			conversion.put(1, buffer[counter + 2]);
			conversion.put(0, buffer[counter + 3]);
			rightSampleBufferIn[sampleCounter] = conversion.getShort(0);
			++sampleCounter;
		}
		return;
	}
	public void shortToByteArray() {
		int byteCounter = 0;
		for (int counter = 0; counter < MAX_BUFF_SIZE / 4; ++counter) {
			buffer[byteCounter] = (byte)(leftSampleBufferOut[counter]);
			buffer[byteCounter + 1] = (byte)(leftSampleBufferOut[counter] >> 8);
			buffer[byteCounter + 2] = (byte)(rightSampleBufferOut[counter]);
			buffer[byteCounter + 3] = (byte)(rightSampleBufferOut[counter] >> 8);
			byteCounter += 4;
		}
		return;
	}
	public void sumOffsets() {
		int typeCheck;
		for (int counter = 0; counter < leftSampleBufferOut.length; ++counter) {
			typeCheck = leftSampleBufferAfterFilter[0][counter] +
					leftSampleBufferAfterFilter[1][counter] +
					leftSampleBufferAfterFilter[2][counter] +
					leftSampleBufferAfterFilter[3][counter] +
					leftSampleBufferAfterFilter[4][counter] +
					leftSampleBufferAfterFilter[5][counter];
			if (typeCheck > Short.MIN_VALUE && typeCheck < Short.MAX_VALUE) {
				this.leftSampleBufferOut[counter] = (short)typeCheck;
			}
			else if (typeCheck < Short.MIN_VALUE) {
				this.leftSampleBufferOut[counter] = Short.MIN_VALUE;
			}
			else {
				this.leftSampleBufferOut[counter] = Short.MAX_VALUE;
			}
			typeCheck = rightSampleBufferAfterFilter[0][counter] +
					rightSampleBufferAfterFilter[1][counter] +
					rightSampleBufferAfterFilter[2][counter] +
					rightSampleBufferAfterFilter[3][counter] +
					rightSampleBufferAfterFilter[4][counter] +
					rightSampleBufferAfterFilter[5][counter];
			if (typeCheck > Short.MIN_VALUE && typeCheck < Short.MAX_VALUE) {
				this.rightSampleBufferOut[counter] = (short)typeCheck;
			}
			else if (typeCheck < Short.MIN_VALUE) {
				this.rightSampleBufferOut[counter] = Short.MIN_VALUE;
			}
			else {
				this.rightSampleBufferOut[counter] = Short.MAX_VALUE;
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
	public short[] getLeftSampledBufferIn() {
		return leftSampleBufferIn;
	}
	public short[] getRightSampledBufferIn() {
		return rightSampleBufferIn;
	}
	public short[] getLeftSampledBufferOut() {
		return leftSampleBufferOut;
	}
	public short[] getRightSampledBufferOut() {
		return rightSampleBufferOut;
	}
}
