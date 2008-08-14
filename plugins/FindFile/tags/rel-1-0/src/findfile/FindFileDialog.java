package findfile;

//{{{ imports
import javax.swing.border.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;

import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.search.*;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.io.VFSManager;
//}}}

/**
 * The main search dialog.
 * @author Nicholas O'Leary
 * @version $Id: FindFileDialog.java,v 1.1.1.1 2003/11/20 17:19:14 olearyni Exp $
 */
public class FindFileDialog extends EnhancedDialog implements ActionListener
{
   
   //{{{ Private members
   private HistoryTextField pathField;
   private HistoryTextField filterField;
   private JCheckBox recursiveBox;
   private JCheckBox openAllResults;
   private View view;
   //}}}

   //{{{ Constructor
   /**
    * Constructor.
    * @param view The active View.
    */
   public FindFileDialog(View view)
   {
      this(view,null);
   } //}}}
 
   //{{{ Constructor
   /**
    * Constructor.
    * @param view The active View.
    * @param path The initial search path.
    */
   public FindFileDialog(View view, String path)
   {
      super(view,jEdit.getProperty("FindFilePlugin.find-dialog.title"),true);
      this.setResizable(false);
      GUIUtilities.loadGeometry(this,"FindFilePlugin");
      this.view = view;
      JPanel content = new JPanel(new BorderLayout());
      content.setBorder(new EmptyBorder(12,12,12,12));
      setContentPane(content);
      if (path==null) {
         String userHome = System.getProperty("user.home");
			String defaultPath = jEdit.getProperty("vfs.browser.defaultPath");
			if(defaultPath.equals("home"))
				path = userHome;
			else if(defaultPath.equals("working"))
				path = System.getProperty("user.dir");
			else if(defaultPath.equals("buffer"))
			{
				if(view != null)
				{
					Buffer buffer = view.getBuffer();
					path = buffer.getDirectory();
				}
				else
					path = userHome;
			} else {
				path = userHome;
			}
      }
      pathField = new HistoryTextField("FindFilePlugin.path");
		pathField.setColumns(25);
      pathField.setText(path);
      pathField.addActionListener(this);
      filterField = new HistoryTextField("FindFilePlugin.filter");
		filterField.setColumns(25);
      filterField.setText("*.*");
      filterField.addActionListener(this);
      JPanel innerContent = new JPanel(new GridBagLayout());
      innerContent.setBorder(new EmptyBorder(0,0,10,0));
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.gridx=0;gbc.gridy=0;
      JLabel pathLabel = new JLabel("Path:");
      pathLabel.setBorder(new EmptyBorder(0,0,0,12));
      innerContent.add(pathLabel,gbc);
      gbc.gridx = 1;
      innerContent.add(pathField,gbc);
      gbc.gridx = 0; gbc.gridy = 1;
      JLabel filterLabel = new JLabel("Filter:");
      filterLabel.setBorder(new EmptyBorder(0,0,0,12));
      innerContent.add(filterLabel,gbc);
      gbc.gridx = 1;
      innerContent.add(filterField,gbc);
      gbc.gridx=0;gbc.gridy=2;gbc.gridwidth = GridBagConstraints.REMAINDER;
      recursiveBox = new JCheckBox(jEdit.getProperty("FindFilePlugin.option-pane-labels.recursiveSearch"));
      recursiveBox.setSelected(jEdit.getProperty("options.FindFilePlugin.recursiveSearch","false").equals("true"));
      innerContent.add(recursiveBox,gbc);

      gbc.gridx=0;gbc.gridy=3;gbc.gridwidth = GridBagConstraints.REMAINDER;
      openAllResults = new JCheckBox(jEdit.getProperty("FindFilePlugin.option-pane-labels.openAllResults"));
      openAllResults.setSelected(jEdit.getProperty("options.FindFilePlugin.openAllResults","false").equals("true"));
      innerContent.add(openAllResults,gbc);
      
      content.add(BorderLayout.CENTER, innerContent);
      Box buttons = Box.createHorizontalBox();
      buttons.add(Box.createHorizontalGlue());
      JButton searchButton = new JButton("Search");
      searchButton.addActionListener(this);
      buttons.add(searchButton);
      buttons.add(Box.createHorizontalStrut(10));
      JButton cancelButton = new JButton("Cancel");
      cancelButton.addActionListener(this);
      buttons.add(cancelButton);
      buttons.add(Box.createHorizontalStrut(10));
      content.add(BorderLayout.SOUTH,buttons);
      pack();
   } //}}}
   
   //{{{ ok
   /**
    * Called when the dialog is confirmed.
    * Performs the search.
    */
   public void ok()
   {
      view.getDockableWindowManager().addDockableWindow("FindFilePlugin");
      FindFileResults results = (FindFileResults)
            view.getDockableWindowManager()
            .getDockable("FindFilePlugin");
      if (results == null)
      {
         setVisible(false);
         return;
      }
      results.searchStarted();
      SearchOptions options = new SearchOptions();
      options.path = pathField.getText();
      options.filter = filterField.getText();
      options.recursive = recursiveBox.isSelected();
      options.openResults = openAllResults.isSelected();
      FindFileRequest request = new FindFileRequest(view,results,options);
      VFSManager.runInWorkThread(request);
      GUIUtilities.saveGeometry(this,"FindFilePlugin");
      pathField.addCurrentToHistory();
      filterField.addCurrentToHistory();
      setVisible(false);
   } ///}}}
   
   //{{{ cancel
   /**
    * Called when the dialog is canceled.
    */
   public void cancel()
   {
      GUIUtilities.saveGeometry(this,"FindFilePlugin");
      setVisible(false);
   }//}}}

   //{{{ actionPerformed
   /**
    * Action handler for dialog buttons.
    */
   public void actionPerformed(ActionEvent e)
   {
      String s = e.getActionCommand();
      if (s.equals("Cancel")) {
         cancel();
      } else {
         ok();
      }
   } //}}}

}
