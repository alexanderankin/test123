package console.commando;

import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import console.ConsolePlugin;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
/**
 * A view for a CommandoCommand
 * 
 * @author ezust
 *
 */
public class CommandoButton extends JButton implements ActionListener, MouseListener
{

	boolean visible;
	CommandoCommand command;
	JPopupMenu contextMenu;
	JMenuItem  hide;
	JMenuItem customize;
	
	public CommandoButton(CommandoCommand command) {
		String name = command.getLabel();
		setText(name);
		this.command = command;
		contextMenu = new JPopupMenu();
		addMouseListener(this);
		visible = jEdit.getBooleanProperty("commando.visible." + name);
		setVisible(visible);
		hide = new JMenuItem(jEdit.getProperty("commando.hide"));
		hide.addActionListener(this);
		customize = new JMenuItem(jEdit.getProperty("commando.customize"));
		customize.addActionListener(this);
		contextMenu.add(hide);
  	    contextMenu.add(customize);
		// add(contextMenu);
	}
	/**
	 * TODO
	 *
	 */
	public void customize() {
		String userDir = ConsolePlugin.getUserCommandDirectory();
		try {
			String name = command.getShortLabel() + ".xml";
			File f = new File(userDir, name);
			if (!f.exists()) {
				Reader reader = command.openStream();
				FileWriter writer = new FileWriter(f);
				int bytes;
				char[] buf = new char[200];
				while ((bytes = reader.read(buf)) > 0) {
					writer.write(buf, 0, bytes);
				}
				writer.close();
				reader.close();
			}
			View v = jEdit.getActiveView();
			jEdit.openFile(v, f.getAbsolutePath());
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == hide) {
			visible = false;
			jEdit.setBooleanProperty("commando.visible." + getText() , visible);
			setVisible(visible);
		}
		if (e.getSource() == customize) {
			customize();
		}
	}
	public void mouseClicked(MouseEvent e)
	{
		if (e.isPopupTrigger()) {
			contextMenu.setVisible(false);
		}
		
	}
	public void mousePressed(MouseEvent e)
	{
		if (e.isPopupTrigger()) {
            contextMenu.show(e.getComponent(), e.getX(), e.getY());
			contextMenu.setVisible(true);
		}
	}
	public void mouseReleased(MouseEvent e)
	{
        if (e.isPopupTrigger()) {
            contextMenu.show(e.getComponent(), e.getX(), e.getY());
			contextMenu.setVisible(true);
        }
	}
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	public void mouseExited(MouseEvent e)
	{

	}
    
}
