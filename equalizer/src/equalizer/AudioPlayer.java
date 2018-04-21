package equalizer;

import java.io.*;
import javax.sound.sampled.*;

public class AudioPlayer {
	private final int MAX_BUFF_SIZE = 65900;
	private byte[] buffer; //Используется для хранения битовых отсчетов, по два бита на один отсчет
	private short[] sampleBuffer; //Используется для хранения отсчетов 1 шорт на один отсчет
	private Clip clip;
	private boolean echoActive = false;
	AudioInputStream ais;
	AudioInputStream outAis;
	public AudioPlayer(File file) {
		buffer = new byte[MAX_BUFF_SIZE];
		sampleBuffer = new short[MAX_BUFF_SIZE/2];
		try {
			ais = AudioSystem.getAudioInputStream(file);
			clip = AudioSystem.getClip();
			clip.open(ais);
			clip.setFramePosition(0);
			ais.read(buffer);
			int sampleCounter = 0;
			for (int counter = 0; counter < buffer.length-1; counter += 2) {
				sampleBuffer[sampleCounter] = (short)((short)buffer[counter] + (short)buffer[counter + 1]);
				++sampleCounter;
			}
			
		}
		catch (IOException | UnsupportedAudioFileException | LineUnavailableException exc) {
			exc.printStackTrace();
		}
	}
	public void play() {
		clip.start();
	}
	
	public void pause() {
		clip.stop();
	}
	
	public void stop() {
		clip.stop();
		clip.setFramePosition(0);
	}
	
	public void restart() {
		clip.start();
	}
	
}
