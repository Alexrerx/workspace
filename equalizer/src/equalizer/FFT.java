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
	double S0im = 0;
	double S0re = 0;
	double Wre = 0;
	double Wim = 0;
	double S1re = 0;
	double S1im = 0;
	double ReOffset = 0.0;
	double ImOffset = 0.0;
	double S0imF = 0;
	double S0reF = 0;
	double WreF = 0;
	double WimF = 0;
	double S1reF = 0;
	double S1imF = 0;
	double ReOffsetF = 0.0;
	double ImOffsetF = 0.0;
	public short[] offsets;
	double[] spectrAmplitude;
	final private int step = 100;
	final private int MAX_SPECTRUM_FREQUENCY = 22000;
	public FFT(){
		spectrAmplitude = new double[MAX_SPECTRUM_FREQUENCY];
	}
	public void setOffsets(short[] inOffsets) {
		this.offsets = inOffsets;
	}
	
	private double Wre(int k, int N) {
		Wre = Math.cos((-2 * Math.PI * k) / N);
		return Wre;
	}
	private double Wim(int k, int N) {
		Wim = Math.sin((-2 * Math.PI * k) / N);
		return Wim;
	}
	private double S0re(int k) {
		S0re = 0;
		for (int counter = 0; counter < offsets.length / 2; ++counter) {
			S0re += offsets[counter * 2] * Wre(k * counter, offsets.length / 2);
		}
		return S0re;
	}
	private double S0im(int k) {
		S0im = 0;
		for (int counter = 0; counter < offsets.length / 2; ++counter) {
			S0im += offsets[counter * 2] * Wim(k * counter, offsets.length / 2);
		}
		return S0im;
	}
	private double S1re(int k) {
		S1re = 0;
		for (int counter = 0; counter < offsets.length / 2; ++counter) {
			S1re += offsets[counter * 2 + 1] * Wre(k * counter, offsets.length / 2);
		}
		return S1re;
	}
	private double S1im(int k) {
		S1im = 0;
		for (int counter = 0; counter < offsets.length / 2; ++counter) {
			S1im += offsets[counter * 2 + 1] * Wim(k * counter, offsets.length / 2);
		}
		return S1im;
	}
	private void setAllFFTOffsets() {
		for (int counter = 0; counter < MAX_SPECTRUM_FREQUENCY / 2; counter += step) {
			S1reF = S1re(counter);
			S1imF = S1im(counter);
			S0reF = S0re(counter);
			S0imF = S0im(counter);
			WreF = Wre(counter, offsets.length);
			WimF = Wim(counter, offsets.length);
			ReOffset = S0reF + (WreF * S1reF - WimF * S1imF);
			ImOffset = S0imF + (WreF * S1imF + WimF * S1reF);
			spectrAmplitude[counter] =
					Math.sqrt(Math.pow(ReOffset, 2) + Math.pow(ImOffset, 2));
			ReOffset = S0reF - (WreF * S1reF - WimF * S1imF);
			ImOffset = S0imF - (WreF * S1imF + WimF * S1reF);
			spectrAmplitude[counter + MAX_SPECTRUM_FREQUENCY / 2] =
					Math.sqrt(Math.pow(ReOffset, 2) + Math.pow(ImOffset, 2));
			
		}
		return;
	}
	
	public double[] getSpectrumAmpl() {
		if (offsets == null) {
			return null;
		}
		setAllFFTOffsets();
		return spectrAmplitude;
	}
}
