package equalizer;

import java.io.*;
import javax.sound.sampled.*;
import java.math.*;
import java.nio.ByteBuffer;

public class AudioPlayer {
	private final int MAX_BUFF_SIZE = 44100;
	private byte[] buffer; //Используется для хранения битовых отсчетов, по два бита на один отсчет
	private short[] sampleBuffer; //Используется для хранения отсчетов 1 шорт на один отсчет
	private SourceDataLine audioLine;
	private boolean isPaused = false;
	private boolean echoActive = false;
	private LineListener listener;
	private AudioInputStream ais;
	private File apFile;
	private AudioThread at;
	public AudioPlayer(File file) {
		apFile = file;
		buffer = new byte[MAX_BUFF_SIZE];
		sampleBuffer = new short[MAX_BUFF_SIZE/2];
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
				int number = ais.read(buffer);
				audioLine.start();
				while (((number = ais.read(buffer)) != -1)&&(!this.isInterrupted())) {
					audioLine.write(buffer, 0, number);
					/*System.out.println("Buffer1");
					for (int counter = 0; counter < 10; ++counter) {
						System.out.print(buffer[counter]);
						System.out.print(' ');
					}
					byteToShortArray(buffer);
					shortToByteArray(sampleBuffer);
					System.out.println("Buffer2");
					for (int counter = 0; counter < 10; ++counter) {
						System.out.print(buffer[counter]);
						System.out.print(' ');
					}
					System.out.println("BufferSample");
					for (int counter = 0; counter < 5; ++counter) {
						System.out.print(sampleBuffer[counter]);
						System.out.print(' ');
					}
					*/
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
	public short[] byteToShortArray(byte[] array) {
		int sampleCounter = 0;
		ByteBuffer conversion = ByteBuffer.allocate(2);
		for (int counter = 0; counter < array.length-1; counter += 2) {
			conversion.put(0, array[counter]);
			conversion.put(1, array[counter + 1]);
			sampleBuffer[sampleCounter] = conversion.getShort(0);
			++sampleCounter;
		}
		return sampleBuffer;
	}
	public byte[] shortToByteArray(short[] array) {
		int byteCounter = 0;
		ByteBuffer conversion = ByteBuffer.allocate(2);
		for (int counter = 0; counter < array.length-1; ++counter) {
			conversion.putShort(0, array[counter]);
			buffer[byteCounter] = conversion.get(0);
			buffer[byteCounter + 1] = conversion.get(1);
			byteCounter += 2;
		}
		return buffer;
	}
	
}
