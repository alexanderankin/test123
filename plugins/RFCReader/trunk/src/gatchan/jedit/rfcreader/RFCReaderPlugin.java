package gatchan.jedit.rfcreader;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import java.text.MessageFormat;

/**
 * @author Matthieu Casanova
 * @version $Id: Server.java,v 1.33 2007/01/05 15:15:17 matthieu Exp $
 */
public class RFCReaderPlugin extends EditPlugin
{
    public static void openRFC(View view, int rfcNum)
    {
        String mirrorId = jEdit.getProperty(RFCHyperlink.MIRROR_PROPERTY);
        String pattern = jEdit.getProperty("options.rfcreader.rfcsources." + mirrorId + ".url");
        String url = MessageFormat.format(pattern, String.valueOf(rfcNum));
        jEdit.openFile(view, url);
    }
}
