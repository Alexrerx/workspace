package bmstu.danich.org.bitFilter;

import java.util.ArrayList;

public class constant1React {
	ArrayList<Short> coeffsH;
	ArrayList<Short> exit;
	public constant1React(ArrayList<Short> coeffs) {
		coeffsH = coeffs;
	}
	public ArrayList<Short> ConstantReact() {
		exit = new ArrayList<Short>();
		int counterCoeff;
		short convResShort;
		double convRes = 0;
		System.out.println("Constant 1");
		ArrayList<Double> tempCoeffs = new ArrayList<Double>();
		for (int counter = 0; counter < 2*coeffsH.size(); ++counter) {
			if (counter < coeffsH.size()) {
				tempCoeffs.add((double)(coeffsH.get(counter)*Math.pow(2, -15)));
			}
			else tempCoeffs.add(0.0);
		}
		for (counterCoeff = 0; counterCoeff < tempCoeffs.size(); ++counterCoeff) {
			convRes = 0;
			for (int counterConvolution = 0; counterConvolution <= counterCoeff; ++counterConvolution) {
				convRes += tempCoeffs.get(counterCoeff - counterConvolution);
			}
			convResShort = (short)Math.round(convRes*Math.pow(2, 15));
			System.out.println(counterCoeff + " " + convRes + " "  + convResShort);
			exit.add(convResShort);
		}
		return exit;
	}
}
