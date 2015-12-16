package render.area;

import processing.core.PApplet;

import java.awt.geom.Rectangle2D;

import loader.OBJLoader;
import main.InformationManager;
import main.ViewerConfig;
import render.agent.EntityShape;
import render.area.draw.BlockadeRender;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.worldmodel.Entity;
import rescuecore2.standard.entities.Blockade;

public class BlockadeShape implements EntityShape {
	private int id;
	private int scale;
	private int[] apexes;

	private float centerX;
	private float centerY;

	private float height;

	private OBJLoader blockades;

	private int[] sideMaxApexes = new int[2];

	private int blockadeScale;
	private int blockadeNum;
	private float rotateY;

	private int repairCost;

	private BlockadeRender render;

	public BlockadeShape(Entity entity, ScreenTransform transform, int scale, OBJLoader blockade) {
		this.id = entity.getID().getValue();
		this.update(entity, transform);
		this.scale = scale;
		this.blockades = blockade;
		this.repairCost = 0;
		this.blockadeScale = 0;
		this.blockadeNum = 0;
		this.rotateY = 0;
		this.render = new BlockadeRender();
	}

	public void drawShape(int count, int animationRate, PApplet applet, ViewerConfig config) {
		if (!config.getFlag("Blockade") || apexes == null)
			return;

		this.render.drawBlockade(applet, blockades, apexes, blockadeNum, centerX, centerY, rotateY, blockadeScale,
				sideMaxApexes, config);
	}

	public int update(Entity entity, ScreenTransform transform) {
		Blockade b = (Blockade) entity;
		int[] apexes = b.getApexes();
		this.apexes = apexes;
		for (int i = 0; i < apexes.length; i += 2) {
			this.apexes[i] = transform.xToScreen(apexes[i]);
			this.apexes[i + 1] = transform.yToScreen(apexes[i + 1]);
		}
		Rectangle2D bounds = b.getShape().getBounds2D();
		this.centerX = transform.xToScreen(bounds.getCenterX());
		this.centerY = transform.yToScreen(bounds.getCenterY());
		this.repairCost = b.getRepairCost();

		this.blockadeScale = setBlockadeScale();

		this.height = (transform.xToScreen(bounds.getWidth()) * transform.yToScreen(bounds.getHeight())) / 80000;

		return InformationManager.NO_CHANGE;
	}

	public int getID() {
		return this.id;
	}

	private int setBlockadeScale() {
		if (repairCost <= 10)
			return 3;
		else if (repairCost <= 20)
			return 5;
		else if (repairCost <= 50)
			return 7;
		else
			return 10;
	}

	public int getRepairCost() {
		return this.repairCost;
	}
}