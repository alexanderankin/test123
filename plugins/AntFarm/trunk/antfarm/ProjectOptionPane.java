/*
 *  ProjectOptionPane.java - Plugin for running Ant builds from jEdit.
 *  Copyright (C) 2008 Gerard Smyth
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package antfarm;

import java.awt.*;
import java.awt.event.*;
import java.io.Console;
import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import common.gui.pathbuilder.*;
import org.apache.tools.ant.Project;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import projectviewer.config.ProjectOptions;
import projectviewer.vpt.VPTProject;
import org.apache.tools.ant.Target;

public class ProjectOptionPane extends AbstractOptionPane implements ActionListener
{

    private VPTProject proj;

    private JComboBox targetA;
    private JComboBox targetB;
    private JComboBox targetC;
    private JComboBox targetD;

    private JTextField buildScript;
    private JButton selectScript;

    private String[] buildTargets;


    public ProjectOptionPane()
    {
        super("AntProjectOptions");
        proj = ProjectOptions.getProject();
    }

    private String getProperty(String propName)
    {
        Object prop = proj.getProperty(propName);
        if (prop == null)
        {
            return "";
        }
        else
        {
            return prop.toString();
        }
    }

    private void setProperty(String propName, String propValue)
    {
        proj.setProperty(propName, propValue);
    }

    public void _init()
    {
        addComponent(new JLabel(jEdit.getProperty(AntFarmPlugin.OPTION_PREFIX
            + "pv.projectAntScript.label")));
        buildScript = new JTextField(getProperty(AntFarmPlugin.OPTION_PREFIX
            + "pv.projectAntScript"), 30);
        buildScript.setEditable(false);
        selectScript = new JButton(jEdit.getProperty(AntFarmPlugin.OPTION_PREFIX
            + "pv.projectAntScriptSelect.label"));

        selectScript.addActionListener(this);

        JPanel scriptPanel = new JPanel();
        scriptPanel.add(buildScript);
        scriptPanel.add(selectScript);

        addComponent(scriptPanel);

        addSeparator();

        JPanel tasksPanel = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        tasksPanel.setLayout(gridbag);
        GridBagConstraints c = new GridBagConstraints();

        tasksPanel.add(new JLabel(jEdit.getProperty(AntFarmPlugin.OPTION_PREFIX
            + "pv.targetA.label")));
        targetA = new JComboBox();
        addTargetOptions(targetA);
        targetA.setSelectedItem(getProperty(AntFarmPlugin.OPTION_PREFIX + "pv.targetA"));
        tasksPanel.add(targetA);
        tasksPanel.add(new JLabel(jEdit.getProperty(AntFarmPlugin.OPTION_PREFIX
            + "pv.targetB.label")));
        targetB = new JComboBox();
        addTargetOptions(targetB);
        targetB.setSelectedItem(getProperty(AntFarmPlugin.OPTION_PREFIX + "pv.targetB"));

        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        gridbag.setConstraints(targetB, c);
        tasksPanel.add(targetB);

        tasksPanel.add(new JLabel(jEdit.getProperty(AntFarmPlugin.OPTION_PREFIX
            + "pv.targetC.label")));
        targetC = new JComboBox();
        addTargetOptions(targetC);
        targetC.setSelectedItem(getProperty(AntFarmPlugin.OPTION_PREFIX + "pv.targetC"));
        tasksPanel.add(targetC);
        tasksPanel.add(new JLabel(jEdit.getProperty(AntFarmPlugin.OPTION_PREFIX
            + "pv.targetD.label")));
        targetD = new JComboBox();
        addTargetOptions(targetD);
        targetD.setSelectedItem(getProperty(AntFarmPlugin.OPTION_PREFIX + "pv.targetD"));
        tasksPanel.add(targetD);

        addComponent(tasksPanel);
    }

    /**
     * Adds an option to the provided combobox for
     * each target that has been found in the entered build file.
     */
    private void addTargetOptions(JComboBox box)
    {
        box.removeAllItems();

        if (buildTargets == null)
            findBuildTargets();

        for(int i = 0; i < buildTargets.length; i++)
        {
            box.addItem(buildTargets[i]);
        }

    }

    /**
     * Parses the specified build file and extracts the list
     * of targets it contains.
     * The buildTargets array is updated to contain a sorted list of targets.
     */
    private void findBuildTargets()
    {
        try
        {
            if (!buildScript.getText().equals(""))
            {
                //find the AntFarm instance so we can access the parse functionality
                AntFarm af = AntFarmPlugin.getAntFarm(jEdit.getActiveView());

                //use AntFarm to parse the build file. (This should not add it permanently)
                Project antProj = af.parseBuildFile(buildScript.getText());
                Hashtable targets = antProj.getTargets();

                List tempTargets = new ArrayList();

                Iterator it = targets.keySet().iterator();
                while (it.hasNext())
                {
                    String targetName = (String) it.next();
                    if (targetName == null)
                        continue;
                    Target target = (Target) targets.get(targetName);

                    if ((!AntFarmPlugin.supressSubTargets()) || (target.getDescription() != null))
                        tempTargets.add(targetName);
                }

                buildTargets = (String[]) tempTargets.toArray(new String[tempTargets.size()]);

                Arrays.sort(buildTargets);
                return;

            }
        }
        catch (Exception e)
        {
            Log.log(Log.ERROR, this, "error parsing build file", e);
        }
        buildTargets = new String[0];
    }


    public void _save()
    {
        setProperty(AntFarmPlugin.OPTION_PREFIX + "pv.projectAntScript", buildScript.getText());
        setProperty(AntFarmPlugin.OPTION_PREFIX + "pv.targetA", targetA.getSelectedItem().toString());
        setProperty(AntFarmPlugin.OPTION_PREFIX + "pv.targetB", targetB.getSelectedItem().toString());
        setProperty(AntFarmPlugin.OPTION_PREFIX + "pv.targetC", targetC.getSelectedItem().toString());
        setProperty(AntFarmPlugin.OPTION_PREFIX + "pv.targetD", targetD.getSelectedItem().toString());
    }

    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();

        if (source == selectScript)
        {
            JFileChooser chooser = new JFileChooser();
            if (!buildScript.getText().equals(""))
                chooser.setCurrentDirectory(new File(buildScript.getText()));
            else
            {
                chooser.setCurrentDirectory(new File(proj.getRootPath()));
            }
            int result = chooser.showDialog(this, jEdit
                .getProperty(AntFarmPlugin.OPTION_PREFIX + "file-dialog-approve"));
            if (result == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null)
            {
                buildScript.setText(chooser.getSelectedFile().getPath());
                findBuildTargets();
                addTargetOptions(targetA);
                addTargetOptions(targetB);
                addTargetOptions(targetC);
                addTargetOptions(targetD);
            }
        }

    }

}
