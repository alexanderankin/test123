//{{{ imports
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import org.gjt.sp.jedit.*;
//}}}

public class GfxViewListModel extends AbstractListModel {
	private Vector container = new Vector();
	private String filename;
	private int maxfiles;
	
	//{{{ +GfxViewListModel() : <init>
	public GfxViewListModel() {
		// Quite simplistic !
		filename = jEdit.getSettingsDirectory() + File.separator + jEdit.getProperty("gfxview.options.historyfile");
		try {
			maxfiles = Integer.parseInt(jEdit.getProperty("gfxview.options.historylimits"));
			if (maxfiles < 1) {
				maxfiles = 5;
				jEdit.setProperty("gfxview.options.historylimits",""+maxfiles);
			}
		}
		catch (Exception execp) {
			maxfiles = 5;
		}
		readFile();
	} //}}}


	//{{{ +addEntry(Object) : void
	public void addEntry(Object url) {
		for (int i=0 ; i < container.size() ; i++) {
			if (container.elementAt(i).toString().compareTo(url.toString())==0) {
				return;
			}
		}
		container.insertElementAt(url,0);
		fireIntervalAdded(this,0,0);
		for (int i=container.size() ; i>=maxfiles ; i--) {
			container.removeElementAt(i-1);
			fireIntervalRemoved(this,i-1,i-1);
		}
		saveFile();
	} //}}}
	//{{{ +removeEntry(Object) : void
	public void removeEntry(Object url) {
		for (int i=0 ; i < container.size() ; i++) {
			if (container.elementAt(i).toString().compareTo(url.toString())==0) {
				container.removeElementAt(i);
				fireIntervalRemoved(this,container.size()-1,container.size()-1);
				saveFile();
				break; // for
			}
		}
	} //}}}


	//{{{ -readFile() : void
	private void readFile() {
		String buffer;
		int c;
		BufferedReader in=null;
		try {
			File input = new File(filename);
			in = new BufferedReader(new FileReader(input));
			while ((buffer=in.readLine()) != null) {
				if (buffer.length()>0) { // Eviter la ligne vide
					// On gère 2 cas :
					// => le fichier URL : object de type URL
					// => fichier local  : object de type String
					try {
						container.insertElementAt(new URL(buffer),0);
					}
					catch (MalformedURLException except) {
						container.insertElementAt(buffer,0);
					}
				}
			}
			in.close();
		}
		catch (IOException exception) {
//			if (in!=null) {
//				in.close();
//			}
		}
	}//}}}
	//{{{ -saveFile() : void
	private void saveFile() {
		PrintStream out=null;
		try {
			File output = new File(filename);
			out = new PrintStream(new FileOutputStream(output));
			for (int i=0 ; i < container.size() ; i++) {
				out.println(container.elementAt(i));
			}
			out.close();
		}
		catch(IOException exception) {
			if (out!=null) {
				out.close();
			}
		}
	}//}}}


	//{{{ +getSize() : int
	public int getSize() {
		return container.size();
	} //}}}
	//{{{ +getElementAt(int) : Object
	public Object getElementAt(int index) {
		return container.elementAt(index);
	} //}}}
}

/* :folding=explicit:tabSize=2:indentSize=2: */