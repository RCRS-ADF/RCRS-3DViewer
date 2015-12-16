package render.information;

import processing.core.PApplet;
import render.information.frame.LocalApplet;
import render.information.graph.CreateLineChart;

public class DrawLineChart extends LocalApplet {
	CreateLineChart gc;
	int h = 0;
	int w = 0;

	public DrawLineChart(PApplet app_parent, int width, int height) {
		super(app_parent);
		gc = new CreateLineChart(this);// 繧ｰ繝ｩ繝輔?ｮ菴懈??
		this.w = width;
		this.h = height;
	}

	public void setLabelName(String horizontal, String vertical) {
		gc.setLabelName(horizontal, vertical);
	}

	public void setVMax(double[] data) {
		gc.setVMax(data);
	}

	public void setTittle(String tit) {
		gc.setTittle(tit);
	}

	public void setData(double data[], int time) {
		gc.setData(data, time);
	}

	@Override
	public void setup() {
		// TODO Auto-generated method stub
		size(this.w + 100, this.h);
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		background(128);
		//gc.draw(this.w, this.h);
	}

}