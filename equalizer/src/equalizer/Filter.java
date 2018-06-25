package equalizer;

import sun.net.TelnetProtocolException;

public class Filter {
	private double gain = 1;
	private double[] filterCoeffs;
	private short[] inOffsets;
	private short[] outOffsets;
	public Filter(double[] coeffs) {
		filterCoeffs = coeffs;
	}
	public void setInOffsets(short[] offsets) {
		inOffsets = offsets;
		outOffsets = new short[inOffsets.length];
	}
	public double getGain() {
		return gain;
	}
	public void setGain(double newGain) {
		gain = newGain;
	}

	//Операция свертки
	private void filterOffsets() {
		int counterOffset = 0;
		double temp = 0;
		int groupSize = filterCoeffs.length;
		for (int group = 0; group < inOffsets.length / groupSize; ++group) {
			for (counterOffset = 0; counterOffset < filterCoeffs.length; ++counterOffset) {
				for (int counterConvolution = 0; counterConvolution <= counterOffset; ++counterConvolution) {
					temp += ((double)gain * (
							(double)inOffsets[counterOffset + group * groupSize - counterConvolution]
							* filterCoeffs[counterConvolution]));
				}
				if ((int)Math.floor(temp) > Short.MIN_VALUE && (int)Math.floor(temp) < Short.MAX_VALUE)
					outOffsets[counterOffset + group * groupSize] = (short)((int)Math.floor(temp));
				else if ((int)Math.floor(temp) < Short.MIN_VALUE)
					outOffsets[counterOffset + group * groupSize] = Short.MIN_VALUE;
				else outOffsets[counterOffset + group * groupSize] = Short.MAX_VALUE;
				temp = 0;
			}
		}
	}

	
	public short[] getFilteredData() {
		filterOffsets();
		return outOffsets;
	}

}
