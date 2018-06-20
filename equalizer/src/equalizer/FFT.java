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
	double[] spectrAmplitude;
	final private int step = 100;
	final private int MAX_SPECTRUM_FREQUENCY = 22000;
	public FFT(){
		spectrAmplitude = new double[MAX_SPECTRUM_FREQUENCY];
	}
	public void setOffsets(short[] inOffsets) {
		this.offsets = new short[inOffsets.length / 2];
		int counter2 = 0;
		for (int counter1 = 0; counter1 < inOffsets.length; counter1 += 2) {
			this.offsets[counter2] = inOffsets[counter1];
			++counter2;
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
		for (int counter = 0; counter < offsets.length / 2; ++counter) {
			S0re += offsets[counter * 2] * Wre(k * counter, offsets.length / 2);
		}
		return S0re;
	}
	private double S0im(int k) {
		double S0im = 0;
		for (int counter = 0; counter < offsets.length / 2; ++counter) {
			S0im += offsets[counter * 2] * Wim(k * counter, offsets.length / 2);
		}
		return S0im;
	}
	private double S1re(int k) {
		double S1re = 0;
		for (int counter = 0; counter < offsets.length / 2; ++counter) {
			S1re += offsets[counter * 2 + 1] * Wre(k * counter, offsets.length / 2);
		}
		return S1re;
	}
	private double S1im(int k) {
		double S1im = 0;
		for (int counter = 0; counter < offsets.length / 2; ++counter) {
			S1im += offsets[counter * 2 + 1] * Wim(k * counter, offsets.length / 2);
		}
		return S1im;
	}
	private void setAllFFTOffsets() {
		double ReOffset = 0.0;
		double ImOffset = 0.0;
		double pWre = 0.0;
		double pWim = 0.0;
		double pS1re = 0.0;
		double pS1im = 0.0;
		double pS0re = 0.0;
		double pS0im = 0.0;
		for (int counter = 0; counter < MAX_SPECTRUM_FREQUENCY; counter += step) {
			pWre = Wre(counter, offsets.length);
			pWim = Wim(counter, offsets.length);
			pS1re = S1re(counter);
			pS1im = S1im(counter);
			pS0re = S0re(counter);
			pS0im = S0im(counter);
			ReOffset = pS0re + (pWre * pS1re - pWim * pS1im);
			ImOffset = pS0im + (pWre * pS1im + pWim * pS1re);
			spectrAmplitude[counter] =
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
