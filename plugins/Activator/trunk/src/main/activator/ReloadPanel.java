package activator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

import common.gui.actions.*;
import common.gui.util.*;

public class ReloadPanel extends JPanel {
	ArrayList jars = new ArrayList();
	ConstraintFactory cf = new ConstraintFactory();
	
	public ReloadPanel() {
		setLayout(new GridBagLayout());
		update();
	}
	
	public void update() {
		removeAll();
		PluginJAR[] jarArray = jEdit.getPluginJARs();
		for (int i = 0; i < jarArray.length; i++) {
			jars.add(jarArray[i]);
		}
		
		int row = 0;
		for (int i = 0; i < jars.length; i++) {
			if (jars[i].getPlugin() == null) {
				continue;
			}
			if (jars[i].getPlugin() instanceof EditPlugin.Deferred) {
				add(new JLabel(jars[i].getFile().getName()),cf.buildConstraints(0,row,1,1));
			} else {
				add(new JLabel(jEdit.getProperty("plugin."+jars[i].getPlugin().getClassName()+".name","No name property")),cf.buildConstraints(0,row,1,1));
			}
			add(new JButton(new Reload(jars[i])),cf.buildConstraints(1,row,1,1));
			row++;
		}
	}
}

class Reload extends CustomAction {
	private PluginJAR jar;
	public Reload(PluginJAR jar) {
		super("Reload");
		this.jar=jar;
	}
	
	public void actionPerformed(ActionEvent event) {
		Log.log(Log.DEBUG,this,"Reloading "+jar);
	}
}
