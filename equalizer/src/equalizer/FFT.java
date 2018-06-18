package equalizer;
/*Формула для быстрого преобразования Фурье с прореживанием по времени
 S(k) = S0(k) + W(k, N) * S1(k);
 S(k + N/2) = S0(k) - W(k, N) * S1(k);
 W(k, N) = exp(-j * 2 * Pi * k / N) = cos(-2 * Pi * k / N) + j * sin(-2 * Pi * k / N);
 S0(k) = СУММ(s(2 * m) * W(m * k)), 0, N/2 - 1);
 S1(k) = СУММ(s(2 * m + 1) * W(m * k)), 0, N/2 - 1);
 k - частота гармоники
 m - номер отсчета
 s - массив отстчетов
 S - спектр
 */
public class FFT {
	public short[] offsets;
	double[] reFFTOffsets;
	double[] imaFFTOffsets;
	double[] spectrAmplitude;
	final private int step = 100;
	final private int MAX_SPECTRUM_FREQUENCY = 22000;
	public FFT(){
		reFFTOffsets = new double[MAX_SPECTRUM_FREQUENCY];
		imaFFTOffsets = new double[MAX_SPECTRUM_FREQUENCY];
		spectrAmplitude = new double[MAX_SPECTRUM_FREQUENCY];
	}
	public void setOffsets(short[] inOffsets) {
		this.offsets = inOffsets;
	}
	private void setAmplitude() {
		double offsetsSquared;
		for (int counter = 1; counter < MAX_SPECTRUM_FREQUENCY; counter += step) {
			offsetsSquared = Math.pow(reFFTOffsets[counter], 2)  + Math.pow(imaFFTOffsets[counter], 2);
			spectrAmplitude[counter] = Math.pow(offsetsSquared, 0.5);
		}
	}
	private double Wre(int k, int N) {
		double Wre = Math.cos((-2 * Math.PI * k) / N);
		return Wre;
	}
	private double Wim(int k, int N) {
		double Wim = Math.sin((-2 * Math.PI * k) / N);
		return Wim;
	}
	private double S0re(int k) {
		double S0re = 0;
		for (int counter = 1; counter < offsets.length / 2; ++counter) {
			S0re += offsets[2 * counter] * Wre(k * counter, offsets.length / 2);
		}
		return S0re;
	}
	private double S0im(int k) {
		double S0im = 0;
		for (int counter = 1; counter < offsets.length / 2; ++counter) {
			S0im += offsets[2 * counter] * Wim(k * counter, offsets.length / 2);
		}
		return S0im;
	}
	private double S1re(int k) {
		double S1re = 0;
		for (int counter = 1; counter < offsets.length / 2; ++counter) {
			S1re += offsets[2 * counter  + 1] * Wre(k * counter, offsets.length / 2);
		}
		return S1re;
	}
	private double S1im(int k) {
		double S1im = 0;
		for (int counter = 1; counter < offsets.length / 2; ++counter) {
			S1im += offsets[2 * counter  + 1] * Wim(k * counter, offsets.length / 2);
		}
		return S1im;
	}
	private void setReFFTOffsets() {
		double ReOffset = 0;
		double ReOffsetPlusHalfN = 0;
		for (int counter = 1; counter < MAX_SPECTRUM_FREQUENCY - offsets.length / 2; counter += step) {
			ReOffset = S0re(counter) + Wre(counter, offsets.length) * S1re(counter);
			ReOffsetPlusHalfN = S0re(counter) - Wre(counter, offsets.length) * S1re(counter);
			reFFTOffsets[counter] = ReOffset;
			reFFTOffsets[counter + (offsets.length / 2)] = ReOffsetPlusHalfN;
		}
		return;
	}
	private void setImFFTOffsets() {
		double ImOffset = 0;
		double ImOffsetPlusHalfN = 0;
		for (int counter = 1; counter < MAX_SPECTRUM_FREQUENCY - offsets.length / 2; counter += step) {
			ImOffset = S0im(counter) + Wim(counter, offsets.length) * S1im(counter);
			ImOffsetPlusHalfN = S0im(counter) - Wim(counter, offsets.length) * S1im(counter);
			reFFTOffsets[counter] = ImOffset;
			reFFTOffsets[counter + (offsets.length / 2)] = ImOffsetPlusHalfN;
		}
		return;
	}
	
	public double[] getSpectrumAmpl() {
		if (offsets == null) {
			return null;
		}
		setReFFTOffsets();
		setImFFTOffsets();
		setAmplitude();
		return spectrAmplitude;
	}
}
