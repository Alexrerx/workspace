package equalizer;

import java.util.ArrayList;
import java.util.Queue;

import sun.net.TelnetProtocolException;

public class Filter {
	private double gain = 1;
	private double[] filterCoeffs;
	private short[] inOffsets;
	private short[] outOffsets;
	private ArrayList<Short> prevOffsetsForFilter;
	public Filter(double[] coeffs, int sampleSize) {
		filterCoeffs = coeffs;
		outOffsets = new short[sampleSize];
		prevOffsetsForFilter = new ArrayList<Short>();
	}
	public void setInOffsets(short[] offsets) {
		inOffsets = offsets;
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
		for (counterOffset = 0; counterOffset < inOffsets.length; ++counterOffset) {
			if (prevOffsetsForFilter.size() != filterCoeffs.length) {
				prevOffsetsForFilter.add(inOffsets[counterOffset]);
			}
			else {
				prevOffsetsForFilter.remove(0);
				prevOffsetsForFilter.add(inOffsets[counterOffset]);
			}
			for (int counterConvolution = 0; (counterConvolution < filterCoeffs.length)
					&& (prevOffsetsForFilter.size() > counterConvolution); ++counterConvolution) {
				temp += ((double)gain * (
						(double)prevOffsetsForFilter.get(prevOffsetsForFilter.size() - 1 - counterConvolution)
						* filterCoeffs[counterConvolution]));
			}
			if ((int)Math.floor(temp) > Short.MIN_VALUE && (int)Math.floor(temp) < Short.MAX_VALUE)
				outOffsets[counterOffset] = (short)((int)Math.floor(temp));
			else if ((int)Math.floor(temp) < Short.MIN_VALUE)
				outOffsets[counterOffset] = Short.MIN_VALUE;
			else outOffsets[counterOffset] = Short.MAX_VALUE;
			temp = 0;
		}
	}

	
	public short[] getFilteredData() {
		filterOffsets();
		return outOffsets;
	}

}
