package equalizer;import com.sun.prism.image.CompoundTexture;
//
public class Overdrive {
	private short[] inOffsets;
	private short[] outOffsets;
	private final short overdriveMax = 2000; 
	public Overdrive(int bufferSize) {
		outOffsets = new short[bufferSize];
	}
	public short[] createEffect(short[] inOffsetsO) {
		inOffsets = inOffsetsO;
		for (int counter = 0; counter < inOffsets.length; ++counter) {
			if (inOffsets[counter] > overdriveMax) {
				outOffsets[counter] = overdriveMax;
			}
			else if (inOffsets[counter] < -1 * overdriveMax) {
				outOffsets[counter] = -1 * overdriveMax;
			}
			else outOffsets[counter] = inOffsets[counter];
		}
		return outOffsets;
	}
}
	