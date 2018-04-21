package equalizer;

public abstract class Effect {
	protected short[] inputAudioStream;
	public abstract short[] getOutputAudioStream();
	public abstract void createEffect();
}
