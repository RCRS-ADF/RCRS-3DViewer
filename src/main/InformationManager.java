package main;

import java.awt.Button;

import org.jfree.data.general.DefaultPieDataset;

import processing.core.PApplet;
import processing.core.PImage;
import render.information.DrawLineChart;
import render.information.DrawPieChart;
import render.information.frame.InfomationFrame;
import render.information.frame.LocalApplet;
import render.information.graph.CreateLineChart;
import render.information.graph.createChartImage;
import org.jfree.data.general.DefaultPieDataset;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class InformationManager extends PApplet {

	public static final int NO_CHANGE = -1;

	public static final int BUILDING_HEATING = 0;
	public static final int BUILDING_BURNING = 1;
	public static final int BUILDING_INFERNO = 2;
	public static final int BUILDING_EXTINGUISH = 3;
	public static final int BUILDING_BURNT_OUT = 4;
	public static final int HUMAN_DEAD = 5;

	private double[] score;
	private double[] population;
	private double[] numBurnedBuilding;
	private double[] blockadeCounts;

	private int livePopulation = 0;
	private int deadPopulation = 0;

	private int heatingBuilding = 0;
	private int burntoutBuilding = 0;
	private int unBurntBuilding = 0;

	private int allPopulation = 0;
	private int refugePopulation = 0;
	private int evacuationPopulation = 0;

	private int currentTime;
	private int maxTime;
	private int startTime;

	private double maxScore = 0;

	CreateLineChart gc = new CreateLineChart(this);

	DefaultPieDataset populationData;
	DefaultPieDataset buildingData;
	DefaultPieDataset refugeData;

	PImage chartImage;
	  private Button button;

	public InformationManager() {
	    button = new Button(0, 0, 225, 30, true);
	    populationData = new DefaultPieDataset();      
	    buildingData = new DefaultPieDataset();      
	    refugeData = new DefaultPieDataset();
	}

	public void draw() {
		background(128);

		pushStyle();
		textAlign(CENTER, TOP);
		textSize(10);
	    button.draw(this);
		popStyle();

		pushStyle();
		// score darw Graph

		switch (button.getFlag()) {
		case 0:
			translate(0, 40);
			gc.setVMax(score);
			BigDecimal now = new BigDecimal(score[currentTime]);
			BigDecimal max = new BigDecimal(gc.getVMax());
			gc.setLabelName("Time", "Score");
			gc.setTittle("Score  " + String.valueOf(now.setScale(3, RoundingMode.CEILING)) + "/"
					+ max.setScale(3, RoundingMode.CEILING));
			gc.setData(score, currentTime);
			gc.draw(score, currentTime, 400, 200);

			translate(0, 230);
			gc.setVMax(population);
			gc.setLabelName("Time", "population");
			gc.setTittle("Population  " + String.valueOf((int) population[currentTime - 1]) + "/"
					+ String.valueOf(allPopulation));
			gc.setData(population, currentTime);
			gc.draw(population, currentTime, 400, 200);

			translate(0, 230);
			gc.setLabelName("Time", "Burned Building");
			gc.setTittle("Burned Building  " + String.valueOf((int) numBurnedBuilding[currentTime - 1]));
			gc.setVMax(numBurnedBuilding);
			gc.setData(numBurnedBuilding, currentTime);
			gc.draw(numBurnedBuilding, currentTime, 400, 200);

			translate(0, 230);
			gc.setVMax(blockadeCounts);
			gc.setLabelName("Time", "Blockade Count");
			gc.setTittle("Blockade Count  " + String.valueOf((int) blockadeCounts[currentTime - 1]) + "/"
					+ String.valueOf(gc.getVMax()));
			gc.setData(blockadeCounts, currentTime);
			gc.draw(blockadeCounts, currentTime, 400, 200);
			break;
		case 1:
			translate(0, 40);
			refugeData.clear();
			refugeData.setValue("Refuge\n" + refugePopulation, refugePopulation);
			evacuationPopulation = allPopulation - refugePopulation - deadPopulation;
			refugeData.setValue("Evacuation\n" + evacuationPopulation, evacuationPopulation);
			refugeData.setValue("Dead\n" + deadPopulation, deadPopulation);
			image(new createChartImage(refugeData).getPieChartPImage(400, 200, "Refuge"), 0, 30);

			translate(0, 230);
			buildingData.clear();
			buildingData.setValue("HeatingBuilding\n" + heatingBuilding, heatingBuilding);
			buildingData.setValue("BurntOutBuilding\n" + burntoutBuilding, burntoutBuilding);
			buildingData.setValue("unBurntBuilding\n" + unBurntBuilding, unBurntBuilding);
			image(new createChartImage(buildingData).getPieChartPImage(400, 200, "BurnedBuilding"), 0, 30);
			break;
		}
		popStyle();

	}

	public void mousePressed() {
		button.push();
	}

	public void init(int maxTime, int startTime, int population) {
		this.currentTime = 0;
		this.maxTime = maxTime;
		this.startTime = startTime;

		this.score = new double[maxTime - startTime];
		this.population = new double[maxTime - startTime];
		this.numBurnedBuilding = new double[maxTime - startTime];
		this.blockadeCounts = new double[maxTime - startTime];

		this.score[0] = 0;
		this.population[0] = population;
		this.numBurnedBuilding[0] = 0;
		this.blockadeCounts[0] = 0;

		this.livePopulation = 0;
		this.deadPopulation = 0;

		this.heatingBuilding = 0;
		this.burntoutBuilding = 0;
		this.unBurntBuilding = 0;

		this.allPopulation = population;
		this.refugePopulation = 0;

		for (int i = 1; i < maxTime - startTime; ++i) {
			this.score[i] = -1;
			this.population[i] = -1;
			this.numBurnedBuilding[i] = -1;
			this.blockadeCounts[i] = -1;
		}
	}

	public void setBurnedBuilding(int time, int fire_count) {
		this.numBurnedBuilding[time] = fire_count;
	}

	public void setPopulation(int time, int livePopulation) {
		this.population[time] = livePopulation;
	}

	public void setBlockadeCount(int time, int blockadeCount) {
		this.blockadeCounts[time] = blockadeCount;
	}

	public void setPopulationData(int live, int dead) {
		this.livePopulation = live;
		this.deadPopulation = dead;
	}

	public void setBuildingData(int heating, int burnt_out, int unburnt) {
		this.heatingBuilding = heating;
		this.burntoutBuilding = burnt_out;
		this.unBurntBuilding = unburnt;
	}

	public void setRefugeData(int refugepopulation) {
		this.refugePopulation = refugepopulation;
	}

	public void nextTime(int t) {
		int time = t - this.startTime;
		if (time <= 0 || time >= maxTime)
			return;

		this.currentTime = time;
		this.population[time] = this.population[time - 1];
		this.numBurnedBuilding[time] = this.numBurnedBuilding[time - 1];
		this.blockadeCounts[time] = this.blockadeCounts[time - 1];
	}

	public void setScore(int t, double score) {
		int time = t - this.startTime;
		if (time <= 0 || time >= maxTime)
			return;

		this.score[time] = score;
	}

	public boolean checkMousePos(int x, int y, int width, int height) {
		if ((this.mouseX >= x && x + width >= this.mouseX) && (this.mouseY >= y && y + height >= this.mouseY))
			return true;
		return false;
	}

	private class Button {
		private String label1;
		private String label2;
		private int x;
		private int y;
		private int width;
		private int height;

		private boolean on1;
		private boolean on2;

		public Button(int x, int y, int width, int height, boolean b) {
			this.label1 = "LineChart";
			this.label2 = "PieChart";
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;

			this.on1 = b;
			this.on2 = false;
		}

		public void draw(PApplet p) {
			int tempH = this.width / 3;
			p.pushStyle();
			p.stroke(100);
			p.strokeWeight(5);
			if (on1)
				p.fill(180);
			else
				p.fill(20);
			p.rect(x, y, tempH, this.height);
			if (on1)
				p.fill(50);
			else
				p.fill(200);
			p.text(label1, x + tempH / 2, y);
			if (on2)
				p.fill(180);
			else
				p.fill(20);
			p.rect(tempH, y, tempH, this.height);
			if (on2)
				p.fill(50);
			else
				p.fill(200);
			p.text(label2, tempH + tempH / 2, y);
			p.popStyle();
		}

		public void push() {
			int tempH = this.width / 3;
			if (checkMousePos(this.x, this.y, tempH, this.height) && !on1) {
				on1 = true;
				on2 = false;
			} else if (checkMousePos(tempH, this.y, tempH, this.height) && !on2) {
				on1 = false;
				on2 = true;
			}
		}

		public int getFlag() {
			int result = 0;

			if (on2)
				result = 1;

			return result;
		}
	}
}