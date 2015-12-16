package render.action;

import processing.core.PApplet;
import processing.core.PImage;
import main.ViewerConfig;

public class AgentAction {
	public static void drawAmbulanceTeamAction(PApplet applet, int moveX, int moveY, float markHeight,
			PImage actionImage, ViewerConfig config) {
		applet.stroke(0, 255, 0);
		switch (config.getDetail()) {
		case ViewerConfig.HIGH:
			applet.ambient(100, 255, 100);
		case ViewerConfig.LOW:
			applet.pushStyle();
			applet.pushMatrix();
			applet.noStroke();
			applet.ambient(255, 255, 255);
			applet.translate(moveX, moveY, markHeight / 2);
			applet.rotateZ((float) (Math.PI * 2 - config.getRoll()));
			applet.rotateX((float) (Math.PI * 2 - config.getYaw()));
			applet.beginShape(applet.QUAD);
			applet.texture(actionImage);
			applet.vertex(-30, 30, 0, 1);
			applet.vertex(-30, -30, 0, 0);
			applet.vertex(50, -30, 1, 0);
			applet.vertex(50, 30, 1, 1);
			applet.endShape();
			applet.popMatrix();
			applet.popStyle();
			break;
		}
	}

	public static void drawCivilianAction(PApplet applet, int moveX, int moveY, float messageHeight, String sayMessage,
			ViewerConfig config) {
		applet.pushStyle();
		applet.ambient(255, 240, 220);
		applet.stroke(80);
		applet.strokeWeight(3);
		applet.pushMatrix();
		applet.translate(moveX, moveY, messageHeight);
		applet.rotateZ((float) (Math.PI * 2 - config.getRoll()));
		applet.rotateX((float) (Math.PI * 2 - config.getYaw()));
		applet.textSize(20);
		applet.textAlign(applet.CENTER, applet.TOP);
		applet.beginShape();
		applet.vertex(-30, 30);
		applet.vertex(-30, -30);
		applet.vertex(50, -30);
		applet.vertex(50, 30);
		applet.vertex(15, 30);
		applet.vertex(0, 50);
		applet.vertex(5, 30);
		applet.endShape();
		applet.ambient(10, 10, 10);
		applet.text(sayMessage, 10, -10, 0);
		applet.popMatrix();
		applet.popStyle();
	}

	public static void drawFireBrigadeAction(PApplet applet, int posX, int posY, int targetX, int targetY,
			float targetZ, ViewerConfig config) {
		applet.stroke(188, 226, 255, 200);
		switch (config.getDetail()) {
		case ViewerConfig.HIGH:
		case ViewerConfig.LOW:
			applet.pushStyle();
			applet.pushMatrix();
			applet.noFill();
			applet.strokeWeight(15);
			applet.bezier(posX, posY, 0, targetX - ((targetX - posX) / 4), targetY - ((targetY - posY) / 4),
					targetZ + (targetZ / 5), targetX - ((targetX - posX) / 2), targetY - ((targetY - posY) / 2),
					targetZ + (targetZ / 10), targetX, targetY, targetZ - (targetZ / 10));
			applet.popMatrix();
			applet.popStyle();
			break;
		default:
			applet.line(posX, posY, targetX, targetY);
			break;
		}
	}

	public static void drawPoliceForceAction(PApplet applet, int moveX, int moveY, float markHeight, int posX, int posY,
			int targetX, int targetY, PImage actionImage, ViewerConfig config) {
		applet.stroke(255, 0, 0);
		switch (config.getDetail()) {
		case ViewerConfig.HIGH:
			applet.pushStyle();
			applet.pushMatrix();
			applet.stroke(80);
			applet.ambient(255, 255, 255);
			applet.translate(moveX, moveY, markHeight / 2);
			applet.rotateZ((float) (Math.PI * 2 - config.getRoll()));
			applet.rotateX((float) (Math.PI * 2 - config.getYaw()));
			applet.beginShape(applet.QUAD);
			applet.texture(actionImage);
			applet.vertex(-30, 30, 0, 1);
			applet.vertex(-30, -30, 0, 0);
			applet.vertex(50, -30, 1, 0);
			applet.vertex(50, 30, 1, 1);
			applet.endShape();
			applet.popMatrix();
			applet.popStyle();
			break;
		}
	}

	public static void RectPoint(int posX, int posY, int targetX, int targetY, PApplet applet) {
		int y0 = 0;
		int x0 = 0;
		float x_width = 0, y_height = 0;
		float pointX1, pointX2, pointX3, pointX4;
		float pointY1, pointY2, pointY3, pointY4;
		if ((targetY - posY) == 0) {
			y0 = 10;
		} else if ((targetX - posX) == 0) {
			x0 = 10;
		} else {
			float hypotenuse = applet.sqrt((targetY - posY) * (targetY - posY) + (targetX - posX) * (targetX - posX));
			x_width = 20 * ((targetY - posY) / hypotenuse);
			y_height = (-1) * (20 * ((targetX - posX) / hypotenuse));
		}
		pointX1 = posX + x0 + x_width;
		pointY1 = posY + y0 + y_height;
		pointX2 = posX - x0 - x_width;
		pointY2 = posY - y0 - y_height;
		pointX3 = targetX - x0 - x_width;
		pointY3 = targetY - y0 - y_height;
		pointX4 = targetX + x0 + x_width;
		pointY4 = targetY + y0 + y_height;

		applet.pushStyle();
		applet.pushMatrix();
		applet.noFill();
		applet.translate(0, 0, 8);
		applet.quad(pointX1, pointY1, pointX2, pointY2, pointX3, pointY3, pointX4, pointY4);
		applet.translate(0, 0, 0);
		applet.popMatrix();
		applet.popStyle();
	}

}