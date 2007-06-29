/*
Copyright (C) 2006  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

Note: The ctags invocation code was taken from the CodeBrowser
plugin by Gerd Knops.
*/

package ctags.sidekick;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSManager;

import sidekick.SideKickParsedData;
import sidekick.SideKickParser;
import sidekick.SideKickPlugin;
import errorlist.DefaultErrorSource;


public class Parser extends SideKickParser {

	private static final String SPACES = "\\s+";
	private JPanel panel = null;
	private ButtonGroup groupingBG = null;
	private JToggleButton groupByKindButton;
	private JToggleButton groupByNamespaceButton;
	private JToggleButton groupByNamespaceFlatButton;
	private JToggleButton sortByLineButton;
	private JToggleButton sortByNameButton;
	private JToggleButton sortByNameFoldsFirstButton;

	public Parser(String serviceName)
	{
		super(serviceName);
	}

	private static class FoldInvalidator implements Runnable {
		private Buffer buffer;
		private SideKickParsedData data;
		public FoldInvalidator(Buffer buffer, SideKickParsedData data) {
			this.buffer = buffer;
			this.data = data;
		}
		public void run() {
			buffer.setProperty(SideKickPlugin.PARSED_DATA_PROPERTY,data);
			if(buffer.getProperty("folding").equals(FoldHandler.CTAGS_SIDE_KICK_FOLD_HANDLER))
				buffer.invalidateCachedFoldLevels();
		}
	}

	private JToggleButton createButton(final String action, String title) {
		JToggleButton btn = new JToggleButton(title);
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jEdit.getAction(action).invoke(jEdit.getActiveView());
			}
		});
		String tooltip = jEdit.getProperty(action + ".label");
		if (tooltip != null)
			btn.setToolTipText(tooltip);
		return btn;
	}
	
	@Override
	public JPanel getPanel() {
		if (panel != null)
			return panel;
		panel = new JPanel(new GridLayout(1, 1));
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.add(new JLabel("Grouping:"));
		groupingBG = new ButtonGroup();
		groupByKindButton = createButton("group-by-kind", "K");
		toolbar.add(groupByKindButton);
		groupingBG.add(groupByKindButton);
		groupByNamespaceButton = createButton("group-by-namespace", "N");
		toolbar.add(groupByNamespaceButton);
		groupingBG.add(groupByNamespaceButton);
		groupByNamespaceFlatButton = createButton("group-by-namespace-flat", "NF");
		toolbar.add(groupByNamespaceFlatButton);
		groupingBG.add(groupByNamespaceFlatButton);
		toolbar.add(new JLabel("Sorting:"));
		ButtonGroup sortingBG = new ButtonGroup();
		sortByLineButton = createButton("sort-by-line", "L");
		toolbar.add(sortByLineButton);
		sortingBG.add(sortByLineButton);
		sortByNameButton = createButton("sort-by-name", "N");
		toolbar.add(sortByNameButton);
		sortingBG.add(sortByNameButton);
		sortByNameFoldsFirstButton = createButton("sort-by-name-folds-first", "NF");
		toolbar.add(sortByNameFoldsFirstButton);
		sortingBG.add(sortByNameFoldsFirstButton);
		panel.add(toolbar);
		return panel;
	}

	private void updatePanel(Buffer buffer) {
		if (panel == null)
			return;
		String mode = buffer.getMode().getName();
		String mapperName = ModeOptionsPane.getProperty(mode, OptionPane.MAPPER);
		if (mapperName.equals(jEdit.getProperty(OptionPane.NAMESPACE_MAPPER_NAME)))
			groupByNamespaceButton.setSelected(true);
		else if (mapperName.equals(jEdit.getProperty(OptionPane.FLAT_NAMESPACE_MAPPER_NAME)))
			groupByNamespaceFlatButton.setSelected(true);
		else
			groupByKindButton.setSelected(true);
		if (jEdit.getBooleanProperty(OptionPane.SORT, false))
		{
			if (jEdit.getBooleanProperty(OptionPane.FOLDS_BEFORE_LEAFS, true))
				sortByNameFoldsFirstButton.setSelected(true);
			else
				sortByNameButton.setSelected(true);
		}
		else
			sortByLineButton.setSelected(true);
	}
	@Override
	public SideKickParsedData parse(Buffer buffer,
									DefaultErrorSource errorSource)
	{		
		updatePanel(buffer);
		ParsedData data =
			new ParsedData(buffer, buffer.getMode().getName());
		runctags(buffer, errorSource, data);
		VFSManager.runInAWTThread(new FoldInvalidator(buffer, data));
		return data;
	}

	private File createTempFile(Buffer buffer)
	{
		String prefix = buffer.getName();
		String suffix = null;
		int idx = prefix.indexOf(".");
		if (idx > 0)
		{
			suffix = prefix.substring(idx);
			prefix = prefix.substring(0,idx);
		}
		File f = null;
		try
		{
			f = File.createTempFile(prefix, suffix);
			FileWriter fw = new FileWriter(f);
			int size = buffer.getLength();
			int offset = 0;
			while (size > 0)
			{
				int c = 16 * 1024;
				if (c > size)
					c = size;
				fw.write(buffer.getText(offset, c));
				offset += c;
				size -= c;
			}
			fw.close();
		}
		catch(Exception e)
		{
			return null;
		}
		return f;
	}
	
	private void runctags(Buffer buffer, DefaultErrorSource errorSource,
						  ParsedData data)
	{
		String ctagsExe = jEdit.getProperty("options.CtagsSideKick.ctags_path");
		String path = buffer.getPath();
		File f = null;
		if (MiscUtilities.isURL(path)) // A remote file (URL)
		{
			f = createTempFile(buffer);
			if (f == null)
				return;
			path = f.getAbsolutePath();
		}
		String mode = buffer.getMode().getName();
		String options = ModeOptionsPane.getProperty(mode, Plugin.CTAGS_MODE_OPTIONS);
		if (options == null)
			options = "";
		Vector<String> cmdLine = new Vector<String>();
		cmdLine.add(ctagsExe);
		cmdLine.add("--fields=KsSz");
		cmdLine.add("--excmd=pattern");
		cmdLine.add("--sort=no");
		cmdLine.add("--fields=+n");
		cmdLine.add("--extra=-q");
		cmdLine.add("-f");
		cmdLine.add("-");
		if (path.endsWith("build.xml"))
			cmdLine.add("--language-force=ant");
		String [] customOptions = options.split(SPACES);
		for (int i = 0; i < customOptions.length; i++)
			cmdLine.add(customOptions[i]);
		cmdLine.add(path);
		String [] args = new String[cmdLine.size()]; 
		cmdLine.toArray(args);
		Process p;
		BufferedReader in = null;
		try {
			p = Runtime.getRuntime().exec(args);
			in = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			String line;
			Tag prevTag = null;
			while ((line=in.readLine()) != null)
			{
				Hashtable<String, String> info =
					new Hashtable<String, String>();
				if (line.endsWith("\n") || line.endsWith("\r"))
					line = line.substring(0, line.length() - 1);
				String fields[] = line.split("\t");
				if (fields.length < 3)
					continue;
				info.put("k_tag", fields[0]);
				info.put("k_pat", fields[2]);
				// extensions
				for (int i = 3; i < fields.length; i++)
				{
					String pair[] = fields[i].split(":", 2);
					if (pair.length != 2)
						continue;
					info.put(pair[0], pair[1]);
				}
				Tag curTag = new Tag(buffer, info);
				if (prevTag != null)
				{	// Set end position of previous tag and add it to the tree
					// (If both tags are on the same line, make the previous tag
					// end at the name of the current one.)
					LinePosition prevEnd;
					int curLine = curTag.getLine();
					if (curLine == prevTag.getLine())
					{
						String def = buffer.getLineText(curLine);
						Pattern pat = Pattern.compile("\\b" + curTag.getName() + "\\b");
						Matcher mat = pat.matcher(def);
						int pos = mat.find() ? mat.start() : -1;
						if (pos == -1) // nothing to do, share assets...
							prevEnd = new LinePosition(buffer, curLine, false); 
						else
						{
							prevEnd = new LinePosition(buffer, curLine, pos);
							curTag.setStart(
									new LinePosition(buffer, curLine, pos));
						}
					}
					else
						prevEnd = new LinePosition(buffer, curLine - 1, false);
					prevTag.setEnd(prevEnd);
					data.add(prevTag);
				}
				prevTag = curTag;
			}
			if (prevTag != null)
			{
				prevTag.setEnd(new LinePosition(buffer));
				data.add(prevTag);
			}
			data.done();
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (f != null)
			f.delete();
	}
}
