package equalizer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

public class Echo {
	private final int delay = 2000;
	private Queue<Short> delayQueueMono;
	private Queue<Short> delayQueueStereo;
	public Echo() {
		delayQueueMono = new LinkedList<Short>();
		delayQueueStereo = new LinkedList<Short>();
	}

	public synchronized short[] createEffect(short[] inputAudioStream) {
		int typeCheckMono;
		int typeCheckStereo;
		short delaySigMono;
		short delaySigStereo;
		short[] outputAudioStream = new short[inputAudioStream.length];
		for (int sigCount = 0; sigCount < inputAudioStream.length; sigCount += 2) {
			if (((delayQueueMono.size() + 1) <= delay)) {
				delayQueueMono.add(inputAudioStream[sigCount]);
				delayQueueStereo.add(inputAudioStream[sigCount + 1]);
				outputAudioStream[sigCount] = inputAudioStream[sigCount];
				outputAudioStream[sigCount + 1] = inputAudioStream[sigCount + 1];
			}
			else {
				delaySigMono = delayQueueMono.poll();
				delaySigStereo = delayQueueStereo.poll();
				delayQueueMono.add(inputAudioStream[sigCount]);
				delayQueueStereo.add(inputAudioStream[sigCount + 1]);
				typeCheckMono = (int) (0.5 * delaySigMono + 0.5 * inputAudioStream[sigCount]);
				typeCheckStereo = (int) (0.5 * delaySigStereo + 0.5 * inputAudioStream[sigCount + 1]);
				if (typeCheckMono > Short.MAX_VALUE) outputAudioStream[sigCount] = inputAudioStream[sigCount];
				else if (typeCheckMono < Short.MIN_VALUE) outputAudioStream[sigCount] = inputAudioStream[sigCount];
				else outputAudioStream[sigCount] = (short) ((short) typeCheckMono); 
				if (typeCheckStereo > Short.MAX_VALUE) outputAudioStream[sigCount + 1] = inputAudioStream[sigCount + 1];
				else if (typeCheckStereo < Short.MIN_VALUE) outputAudioStream[sigCount + 1] = inputAudioStream[sigCount + 1];
				else outputAudioStream[sigCount + 1] = (short) ((short) typeCheckStereo) ; 
			}
		}
		return outputAudioStream;
	}
	
	public synchronized void endEffect(boolean flag) {
		if (flag) {
			delayQueueMono.clear();
			delayQueueStereo.clear();
		}
	}
}

//Добавить метод очищения очереди по внешнему сигналу
