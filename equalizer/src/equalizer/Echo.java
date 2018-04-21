package equalizer;

public class Echo extends Effect {
	private final int delay = 5000;
	public void setInputSampleStream(short[] inputAudioStream) {
		this.inputAudioStream = inputAudioStream;
	}

	@Override
	public short[] getOutputAudioStream() {
		return this.inputAudioStream;
	}

	@Override
	public synchronized void createEffect() {
		short delayAmpl;
		int typeCheck;
		int delayPosition = 0;
		for (int sigCount = delay; sigCount <= inputAudioStream.length; ++sigCount) {
			delayAmpl = inputAudioStream[delayPosition];
			typeCheck = (int)inputAudioStream[sigCount] + (int)delayAmpl;
			if (typeCheck > Short.MIN_VALUE && typeCheck < Short.MAX_VALUE) {
				inputAudioStream[sigCount] = (short)typeCheck;
				++delayPosition;
			}
		}
	}
}
