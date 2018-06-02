package equalizer;

import java.io.*;
import javax.sound.sampled.*;

import com.sun.corba.se.impl.ior.ByteBuffer;

public class OutputAudio {
	private short[] bufferSamp; // ������������� ����� �������
	/*
	 ���� ������� ������ � ���, ��� �� ������ � ���� ������������������ ����� �������, ��������������
	 ������� �������� ����� ��������������� � ���/������ ���.
	 ����� ��������� ��������� �������� �����, ������� ���������� � �������� ����� ��� ���������������
	 ��������������� ���������� � ������� �������
	 FramePosition - ������� �������, � ����� ������ ������� �� 2 byte
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
