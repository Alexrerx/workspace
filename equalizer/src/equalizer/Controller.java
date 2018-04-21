package equalizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Controller implements ActionListener, ChangeListener, ItemListener {
	private View GUI;
	private boolean isPlaying = false;
	JFileChooser fileOpen;
	File file;
	AudioPlayer aPlayer;
	
	public Controller() {
		this.GUI = new View();
		addActionListeners();
	}
	
	private void addActionListeners() {
		GUI.getPlayButton().addActionListener(this);
		GUI.getStopButton().addActionListener(this);
		GUI.getOpenFileButton().addActionListener(this);
		GUI.getEchoCheckBox().addItemListener(this);
		GUI.getOverdriveCheckBox().addItemListener(this);
		GUI.getFilter1Slider().addChangeListener(this);
		GUI.getFilter2Slider().addChangeListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (((JButton)e.getSource()) == GUI.getPlayButton()) {
			if (!isPlaying) {
				//�������� ������ �����������
				aPlayer.play();
				isPlaying = true;
				GUI.getPlayButton().setText("Pause");
			}
			else {
				//�������� ����� �����������
				aPlayer.pause();
				isPlaying = false;
				GUI.getPlayButton().setText("Play");
			}
		}
		if (((JButton)e.getSource()) == GUI.getOpenFileButton()) {
			fileOpen = new JFileChooser();
			int ret = fileOpen.showDialog(null, "Open File");
			if (ret == fileOpen.APPROVE_OPTION) {
				File file = fileOpen.getSelectedFile();
				aPlayer = new AudioPlayer(file);
				//�������� ���������� �������� � ������
			}
		}
		if (((JButton)e.getSource()) == GUI.getStopButton()) {
			if (GUI.getStopButton().getText() == "Stop") {
				//�������� ��������� ����� 
				aPlayer.stop();
				GUI.getStopButton().setText("Restart");
			}
			else {
				//�������� ���������� �����
				aPlayer.restart();
				GUI.getStopButton().setText("Stop");
			}
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if (((JSlider)e.getSource()) == GUI.getFilter1Slider()) {
			//�������� ��������� ��������� ������� 1
		}
		if (((JSlider)e.getSource()) == GUI.getFilter2Slider()) {
			//�������� ��������� ��������� ������� 2
		}
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (((JCheckBox)e.getSource()) == GUI.getEchoCheckBox()) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				//�������� ��������� ������� ���
			}
			else {
				//�������� ���������� ������� ���
			}
		}
		if (((JCheckBox)e.getSource()) == GUI.getOverdriveCheckBox()) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				//�������� ��������� ������� ���������
			}
			else {
				//�������� ���������� ������� ���������
			}	
		}
	}
}
