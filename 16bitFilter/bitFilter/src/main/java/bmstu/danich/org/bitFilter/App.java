package bmstu.danich.org.bitFilter;

import java.util.ArrayList;

public class App {
    public static void main( String[] args ) {
    	ArrayList<Short> coeffs;
    	ArrayList<Short> exitOne;
    	ArrayList<Short> exitDelta;
    	inExcel input= new inExcel("coeffs.xlsx");
    	coeffs = input.parse();
    	constant1React reactionConOne = new constant1React(coeffs);
    	deltaReact reactionDelta = new deltaReact(coeffs);
    	exitOne = reactionConOne.ConstantReact();
    	exitDelta = reactionDelta.DeltaReact();
    	outExcel output = new outExcel("coeffsOut.xlsx", exitOne, exitDelta);
    	output.writeToExcel();
    	return;
    }
}
