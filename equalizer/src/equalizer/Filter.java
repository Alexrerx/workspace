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
							(double)inOffsets[counterOffset + group * groupSize]
							* filterCoeffs[counterOffset  - counterConvolution]));
				}
				if ((int)Math.ceil(temp) > Short.MIN_VALUE && (int)Math.ceil(temp) < Short.MAX_VALUE)
				outOffsets[counterOffset + group * groupSize] = (short)Math.ceil(temp);
				else if ((int)Math.ceil(temp) < Short.MIN_VALUE)
					outOffsets[counterOffset + group * groupSize] = Short.MIN_VALUE;
				else outOffsets[counterOffset + group * groupSize] = Short.MAX_VALUE;
				temp = 0;
			}
		}
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
							(double)inOffsets[counterOffset + group * groupSize]
							* filterCoeffs[counterOffset - counterConvolution]));
				}
				if (((int)Math.ceil(temp)
						+ inOffsets[counterOffset + group * groupSize]) > Short.MIN_VALUE
						&& ((int)Math.ceil(temp)
						+ inOffsets[counterOffset + group * groupSize]) < Short.MAX_VALUE)
					outOffsets[counterOffset + group * groupSize] = (short)((int)Math.ceil(temp)
							+ inOffsets[counterOffset + group * groupSize]);
				else if (((int)Math.ceil(temp)
						+ inOffsets[counterOffset + group * groupSize]) < Short.MIN_VALUE)
					outOffsets[counterOffset + group * groupSize] = Short.MIN_VALUE;
				else outOffsets[counterOffset + group * groupSize] = Short.MAX_VALUE;
				temp = 0;
			}
		}
	}
	/*private void filterOffsets() {
		int multiplication;
		for(int i = 0; i <  inOffsets.length - FilterInfo.COUNT_OF_COEFS; i++) {
			for(int j = 0; j < FilterInfo.COUNT_OF_COEFS; j++) {
				multiplication =  (int) (this.inOffsets[i] * this.filterCoeffs[j]);
				this.outOffsets[i+j] += gain * (short)(multiplication);
			}	
		}
		return;
	}*/

	
	public short[] getFilteredData() {
		filterOffsets();
		return outOffsets;
	}
	public short[] getFilteredDataFilter0() {
		filterOffsetsFilter0();
		return outOffsets;
	}
}
