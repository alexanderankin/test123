package slime;
/**
 * @author Damien Radtke
 * class SlimeMenuProvider
 * Dynamic menu for listing available REPL's in the plugin menu
 */
//{{{ Imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.menu.DynamicMenuProvider;
import slime.SlimePlugin;
//}}}
public class SlimeMenuProvider implements DynamicMenuProvider {
	public boolean updateEveryTime() {
		return false;
	}
	
	public void update(JMenu superMenu) {
		JMenu menu = new JMenu("Start");
		String[] names = ServiceManager.getServiceNames("slime.REPL");
		for (int i = 0; i<names.length; i++) {
			add(menu, names[i]);
		}
		superMenu.add(menu, 0);
	}
	
	protected void add(final JMenu menu, final String name) {
		JMenuItem item = new JMenuItem(name);
		menu.add(item);
		item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SlimePlugin.startREPL(name);
				}
		});
	}
}
