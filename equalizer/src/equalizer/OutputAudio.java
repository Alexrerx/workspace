package equalizer;

import java.io.*;
import javax.sound.sampled.*;

import com.sun.corba.se.impl.ior.ByteBuffer;

public class OutputAudio {
	private short[] bufferSamp; // непроигранная часть буффера
	/*
	 Суть данного класса в том, что мы кидаем в него невоспроизведенную часть буффера, предварительно
	 закинув основной поток воспроизведения в сон/удалив его.
	 После обработки создается выходной поток, который передается в основной класс для воспроизведения
	 Воспроизведение начинается с нулевой позиции
	 FramePosition - позиция отсчета, в нашем случае массива из 2 byte
	 */
	private AudioInputStream audioOutStream;
	public OutputAudio(short[] buffer) {
		bufferSamp = buffer;
	}

	public InputStream getNewInputStream() {
		java.nio.ByteBuffer tempArray = java.nio.ByteBuffer.allocate(2*bufferSamp.length);
		for (int counter = 0; counter < bufferSamp.length; ++counter) {
			tempArray.putShort(bufferSamp[counter]);
		}
		InputStream newInputStream = new ByteArrayInputStream(tempArray.array());
		return newInputStream;
	}
}
