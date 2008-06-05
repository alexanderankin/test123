/*
 *  AntTaskSelector.java - Plugin for running Ant builds from jEdit.
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import console.Console;
import ebrowse.DataItem;
import ebrowse.datasources.MapDataSource;
import ebrowse.SelectionHandler;
import org.apache.tools.ant.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;
import projectviewer.vpt.VPTProject;


/**
 * Ant Task Selector
 * Uses the EBrowse pluginto provide a list of the targets in the provided build file
 * and enables the user to easily select one to run.
 *
 * @author Gerard Smyth, based on the equivalent class in AntHelper by Beau Tateyama (btateyama@yahoo.com)
 */
public class AntTaskSelector extends MapDataSource implements SelectionHandler
{
    /**
     * Gets the title attribute of the FileDataSelector object
     *
     * @return   The title value
     */
    public String getTitle()
    {
        return "Execute Ant Task:";
    }


    /**
     * Handles selection of an entry in the task list.
     * This uses antfarm to run the target
     *
     * @param argDataItem  Description of the Parameter
     */
    public void handleSelection(View view, DataItem argDataItem)
    {
        Console console = AntFarmPlugin.getConsole(view);
        console.run(console.getShell(), console.getOutput(), "+" + argDataItem.getDataSource());
        console.run(console.getShell(), console.getOutput(), "!" + argDataItem.getAlias());
    }



    /**
     * Gets the dataItems to display in the Ebrowse list.
     * This is the list of targets that can be run.
     *
     * @return   The dataItem arry
     */
    public DataItem[] getDataItems()
    {
        DataItem[] toreturn = null;

        try
        {
            //get the AntFarm instance
            AntFarm af = AntFarmPlugin.getAntFarm(jEdit.getActiveView());

            //unfortuantely, due to the design of the MapDataSource class, we have no
            //way of passing any params in to use here, so need to redetermine the
            //build file to use, even though this has already been done in AntFarmPlugin
            VPTProject currentProj = AntFarmPlugin.getCurrentProject(jEdit.getActiveView());
            String buildFile = currentProj.getProperty(AntFarmPlugin.OPTION_PREFIX
                    + "pv.projectAntScript");

            //use AntFarm to parse the build file. (This should not add it permanently)
            Project antProj = af.parseBuildFile(buildFile);
            Hashtable targets = antProj.getTargets();

            ArrayList dataItems = new ArrayList();

            Iterator it = targets.keySet().iterator();
            while (it.hasNext())
            {
                String targetName = (String) it.next();
                if (targetName == null)
                    continue;
                Target target = (Target) targets.get(targetName);
                String desc = target.getDescription();
                if (desc == null)
                {
                    //check if we should include targets without a description
                    if (!AntFarmPlugin.supressSubTargets())
                    {
                        desc = "{0} (" + getTaskList(target) + ")";
                        dataItems.add(new DataItem(targetName, desc, buildFile));
                    }
                }
                else
                {
                    desc = "{0} (" + desc + ")";
                    dataItems.add(new DataItem(targetName, desc, buildFile));
                }
            }

            toreturn = (DataItem[]) dataItems.toArray(new DataItem[dataItems.size()]);

        }
        catch (Exception e)
        {
            Log.log(Log.WARNING, this, "Unable to parse project build file", e);
        }

        return toreturn;
    }


    /**
     * Gets the taskList attribute of the AntTaskSelector class
     *
     * @param argTarget  Description of the Parameter
     * @return           The taskList value
     */
    static String getTaskList(Target argTarget)
    {
        StringBuffer sb = new StringBuffer();
        Task[] tasks = argTarget.getTasks();
        for (int i = 0; i < tasks.length; i++)
        {
            sb.append(tasks[i].getTaskName());
            if (i + 1 < tasks.length)
            {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
