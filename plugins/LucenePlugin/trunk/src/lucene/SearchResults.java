package lucene;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import marker.FileMarker;
import marker.tree.SourceLinkTree;
import marker.tree.SourceLinkTree.SourceLinkParentNode;

import org.gjt.sp.jedit.View;

@SuppressWarnings("serial")
public class SearchResults extends JPanel {
	private JTextField symbolTF;
	private SourceLinkTree tree;
	
	public SearchResults(final View view)
	{
		super(new BorderLayout());
		JPanel symbolPanel = new JPanel(new BorderLayout());
		add(symbolPanel, BorderLayout.NORTH);
		symbolPanel.add(new JLabel("Search for:"), BorderLayout.WEST);
		symbolTF = new JTextField(40);
		symbolTF.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					search(symbolTF.getText());
			}
		});
		symbolPanel.add(symbolTF, BorderLayout.CENTER);
		tree = new SourceLinkTree(view);
		add(new JScrollPane(tree), BorderLayout.CENTER);
	}
	public void search(String text) {
		Vector<FileLine> lines;
		try {
			lines = LucenePlugin.search(text);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		tree.clear();
		if (lines == null)
			return;
		SourceLinkParentNode pn = tree.addSourceLinkParent(text);
		for (int i = 0; i < lines.size(); i++)
		{
			FileLine line = lines.get(i);
			pn.addSourceLink(new FileMarker(line.file, line.line - 1));
		}
	}
}
