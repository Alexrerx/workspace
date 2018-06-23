package equalizer;

import sun.net.TelnetProtocolException;

public class Filter {
	private int gain = 1;
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
	public int getGain() {
		return gain;
	}
	public void setGain(int newGain) {
		gain = newGain;
	}
	private void filterOffsetsFilter0() {
		int counterOffset = 0;
		double temp = 0;
		int groupSize = filterCoeffs.length;
		for (int group = 0; group < inOffsets.length / groupSize; ++group) {
			for (counterOffset = 0; counterOffset < filterCoeffs.length; ++counterOffset) {
				for (int counterConvolution = 0; counterConvolution <= counterOffset; ++counterConvolution) {
					temp += ((double)gain * (
							(double)inOffsets[counterConvolution + group * groupSize - counterConvolution]
							* filterCoeffs[counterOffset]));
				}
				outOffsets[counterOffset + group * groupSize] = (short)Math.ceil(temp);
				temp = 0;
			}
		}
	}
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
				outOffsets[counterOffset + group * groupSize] = (short)((short)Math.ceil(temp)
						+ inOffsets[counterOffset + group * groupSize]);
				temp = 0;
			}
		}
	}
	public short[] getFilteredData() {
		filterOffsets();
		return outOffsets;
	}
	public short[] getFilteredDataFilter0() {
		filterOffsetsFilter0();
		return outOffsets;
	}
}
