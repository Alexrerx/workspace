package bmstu.danich.org.bitFilter;

import java.util.ArrayList;

public class deltaReact {
	ArrayList<Short> coeffsH;
	ArrayList<Short> exit;
	public deltaReact(ArrayList<Short> coeffs) {
		coeffsH = coeffs;
	}
	public ArrayList<Short> DeltaReact() {
		exit = new ArrayList<Short>();
		int counterCoeff;
		short convResShort;
		double convRes = 0;
		System.out.println("Delta");
		ArrayList<Double> tempCoeffs = new ArrayList<Double>();
		ArrayList<Double> tempInput = new ArrayList<Double>();
		tempInput.add(1.0);
		for (int counter = 0; counter < 2*coeffsH.size(); ++counter) {
			if (counter < coeffsH.size()) {
				tempCoeffs.add((double)(coeffsH.get(counter)*Math.pow(2, -15)));
			}
			else tempCoeffs.add(0.0);
			tempInput.add(0.0);
		}
		for (counterCoeff = 0; counterCoeff < tempCoeffs.size(); ++counterCoeff) {
			convRes = 0;
			for (int counterConvolution = 0; counterConvolution <= counterCoeff; ++counterConvolution) {
				convRes += tempCoeffs.get(counterCoeff - counterConvolution)*tempInput.get(counterConvolution);
			}
			convResShort = (short)Math.round(convRes*Math.pow(2, 15));
			System.out.println(counterCoeff + " " + convRes + " "  + convResShort);
			exit.add(convResShort);
		}
		return exit;
	}
}