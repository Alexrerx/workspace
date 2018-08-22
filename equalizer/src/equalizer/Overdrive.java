package equalizer;import com.sun.prism.image.CompoundTexture;

public class Overdrive {
	private short[] inOffsets;
	private short[] outOffsets;
	private int overdriveMax = 2000;
	private int overdrivePower = 1;
	private double overdriveLvl = 1.0;
	private double overdriveStep = 0.1;
	private int prevOffset;
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
				prevOffset = inOffsets[counter];
				if ((inOffsets[counter] < prevOffset) && (overdriveLvl - overdriveStep >= 1)) {
					overdriveLvl -= overdriveStep;
				}
				else if (inOffsets[counter] > prevOffset) {
					overdriveLvl += overdriveStep;
				}
				if ((overdrivePower * overdriveMax * overdriveStep) < Short.MAX_VALUE) {
					outOffsets[counter] = (short)Math.ceil(overdrivePower
							* overdriveMax * overdriveStep);
				}
				else outOffsets[counter] = Short.MAX_VALUE;
			}
			else if (inOffsets[counter] < -1 * overdriveMax) {
				if ((inOffsets[counter] > prevOffset) && (overdriveLvl - overdriveStep >= 1)) {
					overdriveLvl -= overdriveStep;
				}
				else if (inOffsets[counter] < prevOffset) {
					overdriveLvl += overdriveStep;
				}
				if ((-1 * overdrivePower * overdriveMax * overdriveStep) > Short.MIN_VALUE) {
					outOffsets[counter] = (short)Math.ceil(-1 * overdrivePower
							* overdriveMax * overdriveStep);
				}
				else outOffsets[counter] = Short.MIN_VALUE;
			}
			else {
				outOffsets[counter] = inOffsets[counter];
				overdriveLvl = 1.0;
			}
		}
		return outOffsets;
	}
}
	