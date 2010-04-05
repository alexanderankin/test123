
package sidekick.java.options;

import projectviewer.config.OptionsService;
import projectviewer.vpt.VPTProject;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.OptionGroup;


public class PVOptionsService implements OptionsService {
    /**
     * This method should return the option pane to be shown. As with
     * regular jEdit option panes, the label to be shown in the dialog
     * should be defined by the "option.[pane_name].label" property.
     *
     * @param   proj    The project that will be edited.
     *
     * @return An OptionPane instance, or null for no option pane.
     */
     public OptionPane getOptionPane(VPTProject proj) {
          return new PVClasspathOptionPane(proj);   
     }


    /**
     * This should return an OptionGroup to be shown. As with regular
     * jEdit option groups, the label to be shown in the dialog
     * should be defined by the "option.[group_name].label" property.
     *
     * @param   proj    The project that will be edited.
     *
     * @return null for no option group.
     */
     public OptionGroup getOptionGroup(VPTProject proj) {
          return null;   
     }

}