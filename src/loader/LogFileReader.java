package loader;

import java.io.File;
import java.util.Collection;

import javax.swing.JFileChooser;

import main.EntityManager;
import main.InformationManager;
import processing.core.PApplet;
import rescuecore2.config.Config;
import rescuecore2.log.CommandsRecord;
import rescuecore2.log.FileLogReader;
import rescuecore2.messages.Command;
import rescuecore2.registry.Registry;
import rescuecore2.standard.entities.StandardWorldModel;
import rescuecore2.worldmodel.ChangeSet;

public class LogFileReader extends EntityManager {
	FileLogReader log;

	public LogFileReader(int scale, Config config, PApplet applet,
			InformationManager info) {
		super(scale, config, applet, info);
		super.simulationEnded = true;
	}

	public void selectFile() {
		// applet.selectInput("Select a file to process:", "fileSelected");
		JFileChooser chooser = new JFileChooser();
		int returnVal = chooser.showOpenDialog(super.applet);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			this.readFile(chooser.getSelectedFile());
		}
	}

	public void readFile(File selection) {
		if (selection == null) {
			applet.println("Window was closed or the user hit cancel.");
		} else {
			try {
				this.log = new FileLogReader(selection,
						Registry.SYSTEM_REGISTRY);
				super.managerlog = new FileLogReader(selection,
						Registry.SYSTEM_REGISTRY);
				super.world = StandardWorldModel.createStandardWorldModel(log
						.getWorldModel(0));
				// super.time = 50;
				super.endTime = log.getMaxTimestep();
				super.changes = new ChangeSet[super.endTime];
				for (int i = 0; i < super.changes.length; ++i) {
					if (log.getUpdates(i) == null)
						super.changes[i] = null;
					else
						super.changes[i] = log.getUpdates(i).getChangeSet();
				}
				super.commands = new Command[super.endTime + 1][];
				for (int i = 0; i < commands.length; ++i) {
					CommandsRecord cr = log.getCommands(i);
					if (cr != null) {
						Collection<Command> cc = cr.getCommands();
						if (cc != null) {
							super.commands[i] = new Command[cc.size()];
							int j = 0;
							for (Command c : cc) {
								super.commands[i][j] = c;
								j++;
							}
						} else {
							commands[i] = null;
						}
					} else {
						commands[i] = null;
					}
				}
				super.createScreenTransform(super.world);
			} catch (Exception e) {
				applet.println("selected wrong logfile.");
				e.printStackTrace();
			}
		}
	}
}