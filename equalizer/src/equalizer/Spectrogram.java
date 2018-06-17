package equalizer;

import org.jfree.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.data.xy.XYSeries;

public class Spectrogram extends JFreeChart {
	public Spectrogram(Plot plot) {
		super(plot);
		// TODO Auto-generated constructor stub
	}
	double[] data;
	XYSeries series = new XYSeries("Spectrogram");
	JFreeChart spectogram;
	ChartPanel chartPanel;
	public void fillChart(double[] offsets) {
		
	}
}
