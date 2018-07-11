package equalizer;import com.sun.prism.image.CompoundTexture;


public class Overdrive {
	private short[] inOffsets;
	private short[] outOffsets;
	private int overdriveMax = 2000;
	private int overdrivePower = 1;
	public Overdrive(int bufferSize) {
		outOffsets = new short[bufferSize];
	}
	public void setOverdriveMax(int overdriveMaxO) {
		this.overdriveMax = overdriveMaxO;
		return;
	}
	public void setOverdrivePower(int power) {
		this.overdrivePower = power;
		return;
	}
	public int getOverdriveMax() {
		return this.overdriveMax;
	}
	public int getOverdrivePower() {
		return overdrivePower;
	}
	public short[] createEffect(short[] inOffsetsO) {
		inOffsets = inOffsetsO;
		for (int counter = 0; counter < inOffsets.length; ++counter) {
			if (inOffsets[counter] > overdriveMax) {
				if ((-1 * overdrivePower * overdriveMax) < Short.MAX_VALUE) {
					outOffsets[counter] = (short) (overdrivePower * overdriveMax);
				}
				else outOffsets[counter] = Short.MAX_VALUE;
			}
			else if (inOffsets[counter] < -1 * overdriveMax) {
				if ((-1 * overdrivePower * overdriveMax) > Short.MIN_VALUE) {
					outOffsets[counter] = (short) (-1 * overdrivePower * overdriveMax);
				}
				else outOffsets[counter] = Short.MIN_VALUE;
			}
			else outOffsets[counter] = inOffsets[counter];
		}
		return outOffsets;
	}
}
	