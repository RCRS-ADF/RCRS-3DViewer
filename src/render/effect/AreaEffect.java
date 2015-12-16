package render.effect;

import loader.OBJLoader;
import main.ViewerConfig;
import processing.core.PApplet;
import processing.core.PImage;

public class AreaEffect {

	public static void drawFire(float x, float y, float bHeight, int size, float[] nords, int fireCount,
			int fireInterval, PImage[] fireImage, PApplet applet) {

		// side
		float valueNX, valueNX2;
		float valueNY, valueNY2;
		int picNum;
		int count = (int) fireCount / 2;
		picNum = selectFirePic(count);
		applet.ambient(255, 255, 255);
		applet.pushMatrix();
		applet.textureMode(applet.NORMAL);
		applet.beginShape(applet.QUADS);
		applet.noStroke();
		for (int i = 0; i < nords.length - 2; i += 2) {
			valueNX = onNords(nords[i], x, fireInterval);
			valueNY = onNords(nords[i + 1], y, fireInterval);
			valueNX2 = onNords(nords[i + 2], x, fireInterval);
			valueNY2 = onNords(nords[i + 3], y, fireInterval);
			applet.beginShape(applet.QUAD);
			applet.texture(fireImage[picNum]);
			applet.vertex(nords[i], nords[i + 1], bHeight / (size + 1), 0.3f, 0.7f);
			applet.vertex(nords[i + 2], nords[i + 3], bHeight / (size + 1), 0.7f, 0.7f);
			applet.vertex(nords[i + 2] + valueNX2, nords[i + 3] + valueNY2, bHeight + 60 * ((size + 2) / 2), 0.7f,
					0.2f);
			applet.vertex(nords[i] + valueNX, nords[i + 1] + valueNY, bHeight + 60 * ((size + 2) / 2), 0.3f, 0.2f);
			applet.endShape();
		}
		valueNX = onNords(nords[0], x, fireInterval);
		valueNY = onNords(nords[1], y, fireInterval);
		valueNX2 = onNords(nords[nords.length - 2], x, fireInterval);
		valueNY2 = onNords(nords[nords.length - 1], y, fireInterval);
		applet.beginShape(applet.QUAD);
		applet.texture(fireImage[picNum]);
		applet.vertex(nords[0], nords[1], bHeight / (size + 1), 0.3f, 0.7f);
		applet.vertex(nords[nords.length - 2], nords[nords.length - 1], bHeight / (size + 1), 0.7f, 0.7f);
		applet.vertex(nords[nords.length - 2] + valueNX2, nords[nords.length - 1] + valueNY2,
				bHeight + 60 * ((size + 2) / 2), 0.7f, 0.2f);
		applet.vertex(nords[0] + valueNX, nords[1] + valueNY, bHeight + 60 * ((size + 2) / 2), 0.3f, 0.2f);
		applet.endShape();
		applet.popMatrix();
	}

	public static void drawSmoke(float x, float y, float bHeight, int start_point, int size, float[] nords,
			int smokeCount, int smokeInterval, PImage[] smokeImage, PApplet applet) {

		// side
		float valueNX, valueNX2;
		float valueNY, valueNY2;
		int picNum;
		int count = (int) smokeCount / 2;
		picNum = selectSmokePic(count);
		applet.ambient(255, 255, 255);
		applet.pushMatrix();
		applet.textureMode(applet.NORMAL);
		applet.beginShape(applet.QUADS);
		applet.noStroke();
		for (int i = 0; i < nords.length - 2; i += 2) {
			valueNX = onNords(nords[i], x, smokeInterval);
			valueNY = onNords(nords[i + 1], y, smokeInterval);
			valueNX2 = onNords(nords[i + 2], x, smokeInterval);
			valueNY2 = onNords(nords[i + 3], y, smokeInterval);
			applet.beginShape(applet.QUAD);
			applet.texture(smokeImage[picNum]);
			applet.vertex(nords[i], nords[i + 1], bHeight / (start_point + 1), 0.2f, 0.8f);
			applet.vertex(nords[i + 2], nords[i + 3], bHeight / (start_point + 1), 0.8f, 0.8f);
			applet.vertex(nords[i + 2] + valueNX2, nords[i + 3] + valueNY2, bHeight + 60 * ((size + 1) / 2), 0.8f, 0);
			applet.vertex(nords[i] + valueNX, nords[i + 1] + valueNY, bHeight + 60 * ((size + 1) / 2), 0.2f, 0);
			applet.endShape();
			applet.beginShape(applet.QUAD);
		}
		valueNX = onNords(nords[0], x, smokeInterval);
		valueNY = onNords(nords[1], y, smokeInterval);
		valueNX2 = onNords(nords[nords.length - 2], x, smokeInterval);
		valueNY2 = onNords(nords[nords.length - 1], y, smokeInterval);
		applet.beginShape(applet.QUAD);
		applet.texture(smokeImage[picNum]);
		applet.vertex(nords[0], nords[1], bHeight / (start_point + 1), 0.2f, 0.8f);
		applet.vertex(nords[nords.length - 2], nords[nords.length - 1], bHeight / (start_point + 1), 0.8f, 0.8f);
		applet.vertex(nords[nords.length - 2] + valueNX2, nords[nords.length - 1] + valueNY2,
				bHeight + 60 * ((size + 1) / 2), 0.8f, 0);
		applet.vertex(nords[0] + valueNX, nords[1] + valueNY, bHeight + 60 * ((size + 1) / 2), 0.2f, 0);
		applet.endShape();
		applet.popMatrix();
	}

	private static int selectFirePic(int num) {
		if (num < 10) {
			return num;
		} else {
			int i = 0;
			i = (num - 10) % 20;
			i = i + 10;
			return i;
		}
	}

	private static int selectSmokePic(int num) {
		if (num < 15) {
			return num;
		} else {
			int i = 0;
			i = (num - 15) % 15;
			i = i + 15;
			return i;
		}
	}

	private static float onNords(float buildingPoint, float nordsPoint, int distance) {
		float dis;
		if (buildingPoint > nordsPoint) {
			dis = (buildingPoint - nordsPoint) / distance;
			return -dis;
		} else if (buildingPoint < nordsPoint) {
			dis = (nordsPoint - buildingPoint) / distance;
			return dis;
		} else {
			return 0;
		}
	}

	public static void drawText(float x, float y, float z, String str, ViewerConfig config, PApplet applet) {
		applet.pushStyle();
		applet.ambient(255, 240, 220);
		applet.stroke(80);
		applet.strokeWeight(3);
		applet.pushMatrix();
		applet.translate(x, y, z);
		applet.rotateZ((float) (Math.PI * 2 - config.getRoll()));
		applet.rotateX((float) (Math.PI * 2 - config.getYaw()));
		applet.textSize(20);
		applet.textAlign(applet.CENTER, applet.TOP);
		applet.beginShape();
		applet.vertex(-30, 30);
		applet.vertex(-30, -30);
		applet.vertex(50, -30);
		applet.vertex(50, 30);
		applet.endShape();
		applet.ambient(10, 10, 10);
		applet.text(str, 10, -10, 0);
		applet.popMatrix();
		applet.popStyle();
	}

	public static void drawAreaIcon(PApplet applet, float centerX, float centerY, float areaHeight, int scale,
			PImage icon, ViewerConfig config) {
		applet.pushStyle();
		applet.ambient(255, 255, 255);
		applet.noStroke();
		applet.pushMatrix();
		applet.translate(centerX, centerY, (scale / 50) + areaHeight);
		applet.rotateZ((float) (Math.PI * 2 - config.getRoll()));
		applet.rotateX((float) (Math.PI * 2 - config.getYaw()));
		applet.beginShape();
		applet.texture(icon);
		applet.vertex(-50, -50, 0, 0, 0);
		applet.vertex(-50, 50, 0, 0, 1);
		applet.vertex(50, 50, 0, 1, 1);
		applet.vertex(50, -50, 0, 1, 0);
		applet.endShape();
		applet.popMatrix();
		applet.popStyle();
	}
}