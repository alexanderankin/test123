package projectviewer.config;
 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import javax.swing.table.TableColumnModel.*;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import projectviewer.ProjectPlugin;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Matthew Payne
 * @version 1.0
 *
 *Todo: 
 *  Add entry
 *  Delete Entry
 *  Launch app
 *  resize columns
 */
 
public class ProjectAppConfigPane extends AbstractOptionPane  {
 
 public ProjectAppConfigPane(String name) {
    super(name);
    
    try {
      
      jbInit();
       
      
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  

  protected void _init()
	{
		
	}
	
	
/* public static void main(String[] args) {
    ProjectAppConfig ProjectAppConfig = new ProjectAppConfig();
    ProjectAppConfig.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ProjectAppConfig.show();
 
  } */
  private void jbInit() throws Exception {
 
 //   setTitle("JEdit ProjectViewer Associations");
 //   setSize(WIDTH, HEIGHT);
    jPanel1.setLayout(gridBagLayout1);
    extLabel.setText("Extension:");
    appLabel.setText("Application:");
    cmdAdd.setText("Add");
    cmdChooseFile = new JButton();
    cmdChooseFile.setText("...");
    //apps = ProjectViewerConfig.getAppLauncherInstance();
    //this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    addComponent(jPanel1);
    jPanel1.add(extLabel,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(21, 10, 0, 0), 16, 3));
    jPanel1.add(extField,  new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 68, 2));
    jPanel1.add(appLabel,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(21, 20, 0, 83), 36, 2));
    jPanel1.add(appField,  new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 181, 6));
   
   jPanel1.add(cmdChooseFile,  new GridBagConstraints(2, 0, 1, 2, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(36, 8, 0, 0), 1, 0));
   
    jPanel1.add(cmdAdd,  new GridBagConstraints(3, 0, 1, 2, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(36, 8, 0, 28), 5, 0));
    jPanel1.add(jScrollPane1,  new GridBagConstraints(0, 3, 4, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(11, 4, 0, 0), -40, -194));
    jScrollPane1.getViewport().add(appTable, null);
    col =appTable.getColumnModel().getColumn(1);
    col.setPreferredWidth(appTable.getColumnModel().getColumn(0).getWidth() * 5);
    cmdAdd.addActionListener(new
         ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
                if (extField.getText().length() >= 1 && appField.getText().length() >=1) 
                {
                    apps.addAppExt(extField.getText(), replaceString(appField.getText(), "\\", "/"));
                    extField.setText("");
                    appField.setText("");
                    try {
                        apps.storeExts();
                    }
                    catch (java.io.IOException e)
                    { System.err.println("error storing values");}
                    try {
                        apps.loadExts();
                    
                    }
                    catch (java.io.IOException e)
                    { System.err.println("error loading values");}
                     model.requestRefresh();
                  
                }
            }
         }); 
   
    	cmdChooseFile.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
                doChoose();
            
        }
        });

	popmenu = new JPopupMenu();
        menuActions = new javax.swing.JMenuItem();
        jSep = new javax.swing.JSeparator();
        delApp= new javax.swing.JMenuItem();
        
        popmenu.setName("popmenu");
        menuActions.setText("Actions");
        menuActions.setEnabled(false);
        popmenu.add(menuActions);
        popmenu.add(jSep);

        delApp.setText("Delete");
        
        delApp.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
                deleteRow();
        }
        });
        
                
        popmenu.add(delApp); 
        
         appTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (javax.swing.SwingUtilities.isRightMouseButton(evt))
                popmenu.show((Component)evt.getSource(), evt.getX(), evt.getY());
            }
        });
  
   
    }
  
   public void doChoose() {
	// Used for selected and executable file
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
			return;
		try { appField.setText(chooser.getSelectedFile().getPath());}
		catch  (   Exception Excp) {;;}
     }
  
   private static String replaceString(String aSearch, String aFind, String aReplace)
    {
    String result = aSearch;
    if (result != null && result.length() > 0)
        {
        int a = 0;
        int b = 0;
        while (true)
           {
            a = result.indexOf(aFind, b);
            if (a != -1)
                {
                result = result.substring(0, a) + aReplace + result.substring(a + aFind.length());
                b = a + aReplace.length();
            }
            else
            break;
        }
    }
    return result;
    }		   
    
   private void deleteRow() {
        // Deletes a row from the Table:
        int targetRow;
        String keyCol;
        javax.swing.JDialog bal = new javax.swing.JDialog();
       
        if (appTable.getSelectedRowCount() > 0) {
            targetRow = appTable.getSelectedRow();
            keyCol = (String)appTable.getValueAt(targetRow, 0);
            apps.removeAppExt(keyCol);
            model.requestRefresh();
        }
        
    }
 
 	public void _save()
	{
	           try {
                        apps.storeExts();
                    }
                    catch (java.io.IOException e)
                    { System.err.println("error storing values");}
         
		
	}//}}}

   private javax.swing.JSeparator jSep;
   private javax.swing.JPopupMenu popmenu;
   private javax.swing.JMenuItem menuActions;
   private javax.swing.JMenuItem delApp;
   //private appLauncher apps;
   //private ProjectViewerConfig config;
 
   //private static appLauncher;
  
  String[] columnNames={"Extension:", "Application:" };
  JPanel jPanel1 = new JPanel();
  JLabel extLabel = new JLabel();
  JLabel appLabel = new JLabel();
  JTextField appField = new JTextField();
  JTextField extField = new JTextField();
  JScrollPane jScrollPane1 = new JScrollPane();
  appLauncher apps = apps = ProjectViewerConfig.getAppLauncherInstance();//new appLauncher(jEdit.getSettingsDirectory() + java.io.File.separator + "projectviewer/fileassocs.properties");  
	  	 
  appModel model = new appModel(apps);
  JTable appTable = new JTable(model);
  TableColumn col = null;
  JButton cmdAdd = new JButton();
  JButton cmdChooseFile;
  JButton cmdSave = new JButton();
  private static final int WIDTH = 450;
  private static final int  HEIGHT = 300;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  private boolean DEBUG = true;
  }
 
class appModel extends AbstractTableModel
{
   /**
      Constructs an AppList table model.
      @param appAssoc  the collection of extentions and associations
   */
   public appModel(appLauncher appList)
   {
     appSet = appList.getAppList();
   }
 
   public int getRowCount()
   {
      
       return appSet.size();
      
   }
 
   public void requestRefresh() 
   {
     /* Used to refresh the table */
       super.fireTableDataChanged();
   }
   
   public int getColumnCount()
   {
       return 2;
   }
 
   public Object getValueAt(int r, int c)
   {
      String sValue;
 
      Iterator iter = appSet.iterator();
       int iCurrentRow = 0;
 
        while (iter.hasNext()) {
        Map.Entry entry = (Map.Entry)iter.next();
             if (iCurrentRow == r)
                switch(c)
                {
                  case 0: return entry.getKey();
                  case 1: return entry.getValue();
                }
            Object key = entry.getKey();
            Object value = entry.getValue();
          iCurrentRow++;
        }
      return "no value found";
   } 
 
   public String getColumnName(int c)
   {
 
      if (c == 0)
        return "Extension";
      else
        return "Application";
   }

   private Set appSet;
 
}

