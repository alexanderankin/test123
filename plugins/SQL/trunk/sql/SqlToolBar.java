package sql;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

import javax.swing.*;

import org.gjt.sp.jedit.*;
//import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.*;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    14 Февраль 2003 г.
 */
public class SqlToolBar
     extends JToolBar
     implements ActionListener, EBComponent
{

  protected JComboBox serverList;

  private View view;
  private JComboBox combo;
  private JButton save;
  private JButton saveAs;
  private JButton reload;
  private JButton props;
  private JButton prefs;
  private JLabel title;

  private final static String SHOW_TITLE_PROP = "sql.toolbar.server.showTitle";
  private final static String SHOW_TOOLBAR_PROP = "sql.toolbar.showToolBar";


  /**
   *Constructor for the SqlToolBar object
   *
   * @param  view  Description of Parameter
   */
  public SqlToolBar( View view )
  {
    super();
    this.view = view;

    final Insets nullInsets = new Insets( 0, 0, 0, 0 );

    serverList = new JComboBox( SqlServerRecord.getAllNames() );

    serverList.setSelectedItem( SqlUtils.getSelectedServerName() );
    serverList.setEditable( false );
    serverList.addItemListener(
      new ItemListener()
      {
        public void itemStateChanged( ItemEvent evt )
        {
          if ( evt.getStateChange() == evt.SELECTED )
          {
            final Object sel = evt.getItem();
            if ( !( sel == null ||
                sel == SqlUtils.getSelectedServerName() ) )
              SqlUtils.setSelectedServerName( sel.toString() );
          }
        }
      } );

    setFloatable( false );
    putClientProperty( "JToolBar.isRollover", Boolean.TRUE );

    addSeparator( new Dimension( 5, 5 ) );// just add a little space at begin, looks better

    add( serverList );
//    addSeparator();

    add( Box.createGlue() );

    updateTitle();
  }


  /**
   * adds itself to EditBus on display
   */
  public void addNotify()
  {
    super.addNotify();
    EditBus.addToBus( this );
  }


  /**
   * removes itself from EditBus on undisplay
   */
  public void removeNotify()
  {
    super.removeNotify();
    EditBus.removeFromBus( this );
  }


  /**
   *  Description of the Method
   *
   * @param  message  Description of Parameter
   */
  public void handleMessage( EBMessage message )
  {
    if ( message instanceof PropertiesChanged )
      updateTitle();
    else if ( message instanceof SqlServerChanged )
      handleSqlServerChanged( (SqlServerChanged) message );
    else if ( message instanceof SqlServerListChanged )
      handleSqlServerListChanged( (SqlServerListChanged) message );

  }


  /**
   *  Description of the Method
   *
   * @param  evt  Description of Parameter
   */
  public void actionPerformed( ActionEvent evt )
  {
    /*
     *  if ( evt.getSource() == save )
     *  SessionManager.getInstance().saveCurrentSession( view );
     *  else if ( evt.getSource() == saveAs )
     *  SessionManager.getInstance().saveCurrentSessionAs( view );
     *  else if ( evt.getSource() == reload )
     *  SessionManager.getInstance().reloadCurrentSession( view );
     *  else if ( evt.getSource() == props )
     *  SessionManager.getInstance().showSessionPropertiesDialog( view );
     *  else if ( evt.getSource() == prefs )
     *  SessionManager.getInstance().showSessionManagerDialog( view );
     */
  }



  private void handleSqlServerChanged( SqlServerChanged msg )
  {
    final String newServer = msg.getNewServer();
    SwingUtilities.invokeLater(
      new Runnable()
      {
        public void run()
        {
          serverList.setSelectedItem( newServer );
        }
      } );
  }


  private void handleSqlServerListChanged( SqlServerListChanged msg )
  {
    SwingUtilities.invokeLater(
      new Runnable()
      {
        public void run()
        {
          serverList.setModel( new DefaultComboBoxModel( SqlServerRecord.getAllNames() ) );
          serverList.setSelectedItem( SqlUtils.getSelectedServerName() );
        }
      } );
  }


  private void updateTitle()
  {
    if ( showTitle() )
      addTitle();
    else
      removeTitle();
  }


  private void addTitle()
  {
    if ( title != null )
      return;// already added

    title = new JLabel( jEdit.getProperty( "sql.toolbar.server.title" ) );
    add( title, 0 );
    revalidate();
  }


  private void removeTitle()
  {
    if ( title == null )
      return;// already removed

    remove( title );
    revalidate();
    title = null;
  }


  /**
   *  Description of the Method
   */
  public static void clearProperties()
  {
    SqlPlugin.unsetProperty( SHOW_TOOLBAR_PROP );
    SqlPlugin.unsetProperty( SHOW_TITLE_PROP );
  }


  /**
   *  Description of the Method
   *
   * @param  show  Description of Parameter
   */
  public static void showToolBar( boolean show )
  {
    SqlPlugin.setProperty( SHOW_TOOLBAR_PROP, Boolean.toString( show ) );
  }


  /**
   *  Description of the Method
   *
   * @param  show  Description of Parameter
   */
  public static void showTitle( boolean show )
  {
    SqlPlugin.setProperty( SHOW_TITLE_PROP, Boolean.toString( show ) );
  }


  /**
   *  Description of the Method
   *
   * @return    Description of the Returned Value
   */
  public static boolean showToolBar()
  {
    return Boolean.valueOf( SqlPlugin.getProperty( SHOW_TOOLBAR_PROP ) ).booleanValue();
  }


  /**
   *  Description of the Method
   *
   * @return    Description of the Returned Value
   */
  public static boolean showTitle()
  {
    return Boolean.valueOf( SqlPlugin.getProperty( SHOW_TITLE_PROP ) ).booleanValue();
  }

}

