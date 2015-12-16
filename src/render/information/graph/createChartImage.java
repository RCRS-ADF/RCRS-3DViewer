package render.information.graph;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import processing.core.PImage;

public class createChartImage {
	DefaultPieDataset piedata;
	DefaultCategoryDataset linedata;
	JFreeChart chart;

	public createChartImage(DefaultPieDataset data) {
		this.piedata = data;
	}

	public createChartImage(DefaultCategoryDataset data) {
		this.linedata = data;
	}

	public createChartImage(double[] data, String t, int time) {
		int dataLength = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i] != -1) {
				dataLength++;
			} else {
				break;
			}
		}
		int showDisplayData = 12;
		this.linedata = new DefaultCategoryDataset();
		if (dataLength > showDisplayData) {
			for (int i = showDisplayData - 1; i >= 0; i--) {
				this.linedata.addValue(data[dataLength - i - 1], t,
						Integer.toString(time - i));
			}
		} else {
			for (int i = 0; i < dataLength; i++) {
				this.linedata.addValue(data[i], t, Integer.toString(i));
			}
		}
	}

	public PImage getLineChartPImage(int width, int height, String title,
			String horizontal, String vertical) {
		chart = ChartFactory.createLineChart(title, horizontal, vertical,
				linedata, PlotOrientation.VERTICAL, true, false, false);
		chart.setBackgroundPaint(Color.GRAY);
		return new PImage(chart.createBufferedImage(width, height));
	}

	public PImage getPieChartPImage(int width, int height, String title) {
		chart = ChartFactory
				.createPieChart(title, piedata, false, false, false);
		chart.setBackgroundPaint(Color.GRAY);
		return new PImage(chart.createBufferedImage(width, height));
	}
}
