package ctagsinterface.index;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;

import ctagsinterface.index.TagIndex.DocHandler;
import ctagsinterface.main.CtagsInterfacePlugin;

@SuppressWarnings("serial")
public class QueryDialog extends JFrame {

	private JTextField query;
	private DefaultListModel model;
	private JList list;

	public QueryDialog(JFrame parent)
	{
		setTitle("Query Dialog");
		setLayout(new BorderLayout());
		JPanel p = new JPanel();
		add(p, BorderLayout.NORTH);
		p.setLayout(new BorderLayout());
		p.add(new JLabel("Query:"), BorderLayout.WEST);
		query = new JTextField();
		p.add(query, BorderLayout.CENTER);
		model = new DefaultListModel();
		list = new JList(model);
		add(new JScrollPane(list), BorderLayout.CENTER);
		JButton go = new JButton("Search");
		p.add(go, BorderLayout.EAST);
		go.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.removeAllElements();
				CtagsInterfacePlugin.getIndex().runQuery(query.getText(),
					1000000, new DocHandler()
				{
					public void handle(Document doc)
					{
						String s = "";
						for (Fieldable f: doc.getFields())
							s += f.name() + ":" + f.stringValue() + "  ||  ";
						model.addElement(s);
					}
				});
			}
		});
		pack();
		setVisible(true);
	}
}
