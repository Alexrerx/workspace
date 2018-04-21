package equalizer;

import java.io.*;
import javax.sound.sampled.*;

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

	public AudioInputStream EchoEffect() {
		return audioOutStream;
	}
}
