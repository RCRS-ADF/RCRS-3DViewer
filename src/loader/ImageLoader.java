package loader;

import java.io.File;

import processing.core.PApplet;
import processing.core.PImage;

public class ImageLoader {
	public static final int FIRE = 0;
	public static final int AMBULANCE = 1;
	public static final int POLICE = 2;
	public static final int REFUGE = 3;
	public static final int GAS = 4;
	public static final int HYDRANT = 5;

	// top0 right1 left2 front3 back4
	PImage[] rescue;
	PImage[] ambulance;
	PImage[] police;
	PImage[] firebrigade;
	PImage[] civilian;
	PImage[] fires;
	PImage[] smokes;
	PImage ambulanceAction;
	PImage policeAction;

	PImage[] building;
	PImage road;

	PImage[] icons;

	public ImageLoader(PApplet applet) {
		this.ambulance = new PImage[5];
		this.ambulance[0] = applet.loadImage("images" + File.separator + "Ambulance" + File.separator + "top.jpg");
		this.ambulance[1] = applet.loadImage("images" + File.separator + "Ambulance" + File.separator + "side2.jpg");
		this.ambulance[2] = applet.loadImage("images" + File.separator + "Ambulance" + File.separator + "side1.jpg");
		this.ambulance[3] = applet.loadImage("images" + File.separator + "Ambulance" + File.separator + "front.jpg");
		this.ambulance[4] = applet.loadImage("images" + File.separator + "Ambulance" + File.separator + "back.jpg");
		this.ambulanceAction = applet
				.loadImage("images" + File.separator + "Ambulance" + File.separator + "icon_res.png");

		this.police = new PImage[5];
		this.police[0] = applet.loadImage("images" + File.separator + "Police" + File.separator + "top.jpg");
		this.police[1] = applet.loadImage("images" + File.separator + "Police" + File.separator + "side2.jpg");
		this.police[2] = applet.loadImage("images" + File.separator + "Police" + File.separator + "side1.jpg");
		this.police[3] = applet.loadImage("images" + File.separator + "Police" + File.separator + "front.jpg");
		this.police[4] = applet.loadImage("images" + File.separator + "Police" + File.separator + "back.jpg");
		this.policeAction = applet.loadImage("images" + File.separator + "Police" + File.separator + "icon_gar.png");

		this.firebrigade = new PImage[5];
		this.firebrigade[0] = applet.loadImage("images" + File.separator + "Firebrigade" + File.separator + "top.jpg");
		this.firebrigade[1] = applet
				.loadImage("images" + File.separator + "Firebrigade" + File.separator + "side2.jpg");
		this.firebrigade[2] = applet
				.loadImage("images" + File.separator + "Firebrigade" + File.separator + "side1.jpg");
		this.firebrigade[3] = applet
				.loadImage("images" + File.separator + "Firebrigade" + File.separator + "front.jpg");
		this.firebrigade[4] = applet.loadImage("images" + File.separator + "Firebrigade" + File.separator + "back.jpg");

		// ---------------------------------------------------------------------------------------------//
		// 2014/11/06 henkou satou
		// this.building = new PImage[2];
		// this.building[0] =
		// applet.loadImage("images"+File.separator+"building.jpg");
		// this.building[1] =
		// applet.loadImage("images"+File.separator+"entrance.jpg");

		this.building = new PImage[2];
		this.building[0] = applet.loadImage("images" + File.separator + "building2.0.png");
		this.building[1] = applet.loadImage("images" + File.separator + "entrance2.0.png");
		// ---------------------------------------------------------------------------------------------//

		this.icons = new PImage[6];
		this.icons[0] = applet.loadImage("images" + File.separator + "FireStation-64x64.png");
		this.icons[1] = applet.loadImage("images" + File.separator + "AmbulanceCentre-64x64.png");
		this.icons[2] = applet.loadImage("images" + File.separator + "PoliceOffice-64x64.png");
		this.icons[3] = applet.loadImage("images" + File.separator + "Refuge-64x64.png");
		this.icons[4] = applet.loadImage("images" + File.separator + "GasStation-64x64.png");
		this.icons[5] = applet.loadImage("images" + File.separator + "Hydrant-64x64.png");

		this.road = applet.loadImage("images" + File.separator + "road.jpg");

		this.fires = new PImage[30];
		for (int i = 0; i < 60; i = i + 2) {
			if (i == 0) {
				this.fires[i] = applet
						.loadImage("images" + File.separator + "Fire" + File.separator + "fire" + i + ".png");
			} else {
				this.fires[i / 2] = applet
						.loadImage("images" + File.separator + "Fire" + File.separator + "fire" + i + ".png");
			}
		}

		this.smokes = new PImage[60];
		for (int i = 0; i < 60; i = i + 2) {
			if (i == 0) {
				this.smokes[i] = applet
						.loadImage("images" + File.separator + "Smoke" + File.separator + "smoke" + i + ".png");
			} else {
				this.smokes[i / 2] = applet
						.loadImage("images" + File.separator + "Smoke" + File.separator + "smoke" + i + ".png");
			}
		}
	}

	public PImage[] getBuildingImage() {
		return this.building;
	}

	public PImage[] getIcons() {
		return this.icons;
	}

	public PImage getRoadImage() {
		return this.road;
	}

	public PImage[] getAmbulanceImage() {
		return this.ambulance;
	}

	public PImage getActionAmbulanceImage() {
		return this.ambulanceAction;
	}

	public PImage[] getPoliceImage() {
		return this.police;
	}

	public PImage getPoliceActionImage() {
		return this.policeAction;
	}

	public PImage[] getFirebrigadeImage() {
		return this.firebrigade;
	}

	public PImage[] getFire() {
		return this.fires;
	}

	public PImage[] getSmoke() {
		return this.smokes;
	}
}