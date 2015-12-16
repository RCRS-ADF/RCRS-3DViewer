package render.area;

import main.InformationManager;
import main.ViewerConfig;
import processing.core.PApplet;
import processing.core.PImage;
import render.area.draw.BuildingRender;
import render.area.draw.RoadRender;
import render.effect.AreaEffect;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.StandardEntityConstants.Fieryness;
import rescuecore2.worldmodel.Entity;

public class BuildingShape extends AreaShape {
	private PImage[] image;
	private PImage[] fireImage;
	private PImage[] smokeImage;

	private float bHeight;
	private int scale;

	private Fieryness fieryness;

	private int fireCount;
	private int smokeCount, smokedelate;

	private float x, y;

	private int burning;
	private int smoking;

	private BuildingRender render;
	private RoadRender roadrender;

	public BuildingShape(Entity entity, ScreenTransform transform, float bHeight, int scale, PImage[] image,
			PImage[] fireImage, PImage[] smokeImage, PImage[] icons) {
		super(entity, transform, scale, icons, bHeight);
		this.fieryness = Fieryness.UNBURNT;

		Building b = (Building) entity;

		this.image = image;
		this.fireImage = fireImage;
		this.smokeImage = smokeImage;

		this.bHeight = bHeight;
		this.scale = 50;// agent size

		this.fieryness = b.getFierynessEnum();

		this.x = transform.xToScreen(b.getX());
		this.y = transform.yToScreen(b.getY());

		this.fireCount = 0;
		this.smokeCount = 0;
		this.burning = 0;
		this.smoking = 0;

		this.render = new BuildingRender();
	}

	public int update(Entity entity, ScreenTransform transform) {
		super.update(entity, transform);
		Building b = (Building) entity;

		Fieryness f = b.getFierynessEnum();

		if (this.fieryness.equals(f)) {
			return InformationManager.NO_CHANGE;
		} else {
			this.fieryness = f;

			switch (f) {
			case BURNING:// On fire a bit more.
				return InformationManager.BUILDING_BURNING;
			case BURNT_OUT:// Completely burnt out.
				return InformationManager.BUILDING_BURNT_OUT;
			case HEATING:// On fire a bit.
				return InformationManager.BUILDING_HEATING;
			case INFERNO:// On fire a lot.
				return InformationManager.BUILDING_INFERNO;
			case MINOR_DAMAGE:// Extinguished but minor damage.
				return InformationManager.BUILDING_EXTINGUISH;
			case MODERATE_DAMAGE:// Extinguished but moderate damage.
				return InformationManager.BUILDING_EXTINGUISH;
			case SEVERE_DAMAGE:// Extinguished but major damage.
				return InformationManager.BUILDING_EXTINGUISH;
			case UNBURNT:// Not burnt at all.
				break;
			case WATER_DAMAGE:// Not burnt at all, but has water damage.
				break;
			}
		}
		return InformationManager.NO_CHANGE;
	}

	public void drawShape(int count, int animationRate, PApplet applet, ViewerConfig config) {
		if (!config.getFlag("Building") || nords == null)
			return;

		switch (this.fieryness) {
		case BURNING:
			buildingPre(2, 2, count);
			applet.ambient(200, 100, 0);
			break;
		case BURNT_OUT:
			buildingPre(0, 2, count);
			applet.ambient(50, 50, 50);
			break;
		case HEATING:
			buildingPre(1, 1, count);
			applet.ambient(125, 125, 0);
			break;
		case INFERNO:
			buildingPre(3, 3, count);
			applet.ambient(255, 50, 0);
			break;
		case MINOR_DAMAGE:
			buildingPre(0, 0, count);
			applet.ambient(25, 100, 170);
			break;
		case MODERATE_DAMAGE:
			buildingPre(0, 0, count);
			applet.ambient(50, 100, 170);
			break;
		case SEVERE_DAMAGE:
			buildingPre(0, 0, count);
			applet.ambient(100, 100, 170);
			break;
		case UNBURNT:
			buildingPre(0, 0, count);
			applet.ambient(100, 100, 100);
			break;
		case WATER_DAMAGE:
			buildingPre(0, 0, count);
			applet.ambient(0, 100, 255);
			break;
		}

		this.render.drawBuilding(applet, super.nords, super.x, super.y, super.areaHeight, super.passable, this.bHeight,
				super.scale, this.scale, this.areaName, super.REFUGE, super.icon, this.image, config);

	}

	private void buildingPre(int firelevel, int smokelevel, int count) {
		this.burning = firelevel;
		this.smoking = smokelevel;
		if (burning > 0) {
			if (count % 2 == 0) {
				fireCount++;
			}
		} else {
			fireCount = 0;
		}
		if (smoking > 0) {
			if (burning == 0) {
				if (count % 30 == 0) {
					smokedelate++;
				}
			}
			if (count % 3 == 0) {
				smokeCount++;
			}
		} else {
			smokeCount = 0;
		}
	}

	public void drawShape(PApplet applet, ViewerConfig config) {
		drawEffect(applet, x, y, bHeight, super.nords, fireCount, fireImage, smokeImage, smoking, burning, smokeCount,
				config);
	}

	public void setBHeight(float bheight) {
		this.bHeight = bheight;
	}

	public float getBHeight() {
		return this.bHeight;
	}

	public void drawEffect(PApplet applet, float x, float y, float bHeight, float[] nords, int fireCount,
			PImage[] fireImage, PImage[] smokeImage, int smoking, int burning, int smokeCount, ViewerConfig config) {
		switch (config.getDetail()) {
		case ViewerConfig.HIGH:
			if (smoking > 0) {
				AreaEffect.drawSmoke(x, y, bHeight, 0, 4, nords, smokeCount, 2, smokeImage, applet);
			}
			if (burning > 0) {
				if (smoking > 0) {
					AreaEffect.drawSmoke(x, y, bHeight, smoking, 3, nords, smokeCount, -100, smokeImage, applet);
				}
				AreaEffect.drawFire(x, y, bHeight, burning, nords, fireCount, -50, fireImage, applet);
			}
			break;
		default:
			break;
		}
	}
}