package projectviewer.config;
 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
//import javax.swing.table.*;
import javax.swing.table.TableColumnModel.*;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import projectviewer.ProjectPlugin;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.*;


//import javax.swing.DefaultCellEditor;
//import javax.swing.table.TableCellRenderer;
//import javax.swing.table.DefaultTableCellRenderer;
 
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
 
public class ProjectAppConfig extends AbstractOptionPane  {
 
  public ProjectAppConfig(String name) {
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
    //this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    addComponent(jPanel1);
    jPanel1.add(extLabel,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(21, 28, 0, 0), 16, 3));
    jPanel1.add(extField,  new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 27, 0, 0), 68, 2));
    jPanel1.add(appLabel,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(21, 39, 0, 83), 36, 2));
    jPanel1.add(appField,  new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 36, 0, 0), 181, 6));
    jPanel1.add(cmdAdd,  new GridBagConstraints(2, 0, 1, 2, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(36, 8, 0, 28), 5, 0));
    jPanel1.add(jScrollPane1,  new GridBagConstraints(0, 3, 3, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(11, 4, 0, 0), -40, -194));
    jScrollPane1.getViewport().add(appTable, null);
    col =appTable.getColumnModel().getColumn(1);
    col.setPreferredWidth(appTable.getColumnModel().getColumn(0).getWidth() * 3);
    cmdAdd.addActionListener(new
         ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
                if (extField.getText().length() >= 1 && appField.getText().length() >=1) 
                {
                    apps.addAppExt(extField.getText(), appField.getText());
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
   
    }
 
 	public void _save()
	{
	           try {
                        apps.storeExts();
                    }
                    catch (java.io.IOException e)
                    { System.err.println("error storing values");}
         
		//jEdit.getSettingsDirectory() + java.io.File.separator + "fileassocs.properties";
	}//}}}
  
  String[] columnNames={"Extension:", "Application:" };
  JPanel jPanel1 = new JPanel();
  JLabel extLabel = new JLabel();
  JLabel appLabel = new JLabel();
  JTextField appField = new JTextField();
  JTextField extField = new JTextField();
  JScrollPane jScrollPane1 = new JScrollPane();
  appLauncher apps = new appLauncher(jEdit.getSettingsDirectory() + java.io.File.separator + "fileassocs.properties");
  appModel model = new appModel(apps);
  JTable appTable = new JTable(model);
  TableColumn col = null;
  JButton cmdAdd = new JButton();
  JButton cmdSave = new JButton();
  private static final int WIDTH = 425;
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

