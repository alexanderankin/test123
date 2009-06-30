/*
Copyright (C) 2009  Shlomy Reinstein

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
*/

package ocr;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;


public class OCRPlugin extends EditPlugin {
	static public void readPicture(View view)
	{
		JFileChooser dlg = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("Picture file", "pbm");
		dlg.setFileFilter(filter);
		if (dlg.showOpenDialog(view) != JFileChooser.APPROVE_OPTION)
			return;
		String text = readText(dlg.getSelectedFile());
		Buffer b = jEdit.openFile(view, "imported.txt");
		if (b == null)
			return;
		b.insert(0, text);
	}
	
	static private String readText(File f)
	{
		String path = f.getAbsolutePath();
		String exe = jEdit.getProperty("options.ocr.gocrPath");
		String [] cmd = { exe, path };
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			InputStream is = p.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null)
				sb.append(line + "\n");
			p.waitFor();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
