//{{{ imports
import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.*;

import com.imagero.io.*;
import com.imagero.reader.*;
//}}}

// exemple http://web.wanadoo.be/lud.soph/Image001.jpg
// exemple http://spacemul.emu-france.com/image2/autres/3do/16944.png

public class GfxView extends JPanel implements EBComponent,PropertyChangeListener {
	private View view;
	private GfxViewImagePanel imagePanel;
	private GfxViewHistoryDialog historyDlg;

	//{{{ +GfxView(View, String) : <init>
	public GfxView(View view, String position) {
		super(new BorderLayout());
		this.view = view;

		imagePanel = new GfxViewImagePanel();
		historyDlg = new GfxViewHistoryDialog(view);
		GfxViewToolBarPanel toolbarPanel = new GfxViewToolBarPanel();
		// Notify : changing title in toolbar panel upon URL change
		historyDlg.addPropertyChangeListener(toolbarPanel);
		// Notify : loading new image in picture panel upon URL change
		historyDlg.addPropertyChangeListener(this);
		// Notify : changing labels upon zoom parameters changes in picture panel
		imagePanel.addPropertyChangeListener(toolbarPanel);
		// Notify : changing zoom parameters in picture panel upon toolbar changes
		toolbarPanel.addPropertyChangeListener(imagePanel);

		add(toolbarPanel,BorderLayout.NORTH);
		add(imagePanel,BorderLayout.CENTER);
	} //}}}

	//{{{ +propertyChange(PropertyChangeEvent) : void
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().compareTo("UrlGfxView-path")==0) {
			loadImage(evt.getNewValue());
		}
	} //}}}

	//{{{ +loadRemoteImage() : void
	public void loadRemoteImage() {
			String inputValue = JOptionPane.showInputDialog(view,"Enter a valid drawing URL :");
			try {
				if (inputValue!=null) {
					loadImage(new URL(inputValue));
				}
			}
			catch(java.net.MalformedURLException except) {
				JOptionPane.showMessageDialog(view,
					"URL specified is Invalid", "Information", JOptionPane.INFORMATION_MESSAGE);
			}
	}//}}}

	//{{{ +loadImage(Object) : void
	public void loadImage(Object urlPath) {
			try {
				ImageReader imageReader;
				if (urlPath instanceof URL) {
					imageReader = ReaderFactory.createReader((URL)urlPath);
				}
				else {
					imageReader = ReaderFactory.createReader(urlPath.toString());
				}
				java.awt.image.ImageProducer ip = imageReader.getProducer(0);
				Image image = Toolkit.getDefaultToolkit().createImage(ip);
				imagePanel.loadImage(image);
				historyDlg.addEntry(urlPath);
			}
			catch (IOException except) {
				JOptionPane.showMessageDialog(view,"Picture file is invalid or its format unknown");
			}
	}//}}}

	//{{{ +showHistory() : void
	public void showHistory() {
		historyDlg.show();
	}//}}}

	//{{{ +loadNextFromHistory() : void
	public void loadNextFromHistory() {
		historyDlg.loadNext();
	}//}}}

	//{{{ +loadPrevFromHistory() : void
	public void loadPrevFromHistory() {
		historyDlg.loadPrev();
	}//}}}

	//{{{ +handleMessage(EBMessage) : void
	public void handleMessage(EBMessage message) {
	} //}}}
}

/* :folding=explicit:tabSize=2:indentSize=2: */