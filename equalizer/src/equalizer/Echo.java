package equalizer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

public class Echo {
	private int delay = 1000;
	private Queue<Short> delayQueue;
	private short[] outputAudioStream;
	private double wetBalance = 0.5;
	public Echo(int bufferSize) {
		delayQueue = new LinkedList<Short>();
		outputAudioStream = new short[bufferSize];
	}
	public int getDelay() {
		return delay;
	}
	public double getWetBalance() {
		return wetBalance;
	}
	public void setWetBalance(double balance) {
		this.wetBalance = balance;
		return;
	}
	public void setDelay(int delayO) {
		this.delay = delayO;
	}
	public synchronized short[] createEffect(short[] inputAudioStream) {
		int typeCheck;
		short delaySig;
		for (int sigCount = 0; sigCount < inputAudioStream.length; ++sigCount) {
			if ((delayQueue.size() + 1) <= delay) {
				delayQueue.add(inputAudioStream[sigCount]);
				outputAudioStream[sigCount] = inputAudioStream[sigCount];
			}
			else {
				delaySig = delayQueue.poll();
				delayQueue.add(inputAudioStream[sigCount]);
				typeCheck = (int)Math.ceil(wetBalance * (double)delaySig
						+ (1 - wetBalance) * (double)inputAudioStream[sigCount]);
				if ((typeCheck > Short.MAX_VALUE) && (typeCheck < Short.MIN_VALUE)) 
					outputAudioStream[sigCount] = inputAudioStream[sigCount];
				else outputAudioStream[sigCount] = (short) (typeCheck); 
			}
		}
		return outputAudioStream;
	}
	
	public synchronized void endEffect(boolean flag) {
		if (flag) {
			delayQueue.clear();
		}
	}
}

