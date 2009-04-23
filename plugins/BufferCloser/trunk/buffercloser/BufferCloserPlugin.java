package buffercloser;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.bufferset.BufferSet;
import org.gjt.sp.jedit.msg.EditPaneUpdate;

/**
 * This plugin enforces exclusive buffersets - in other words,
 * a buffer can only be open in one non-global bufferset - once it
 * is opened in another bufferset, it is closed in the previous one.  
 * 
 * @author ezust
 *
 */
public class BufferCloserPlugin extends EBPlugin
{
	public void handleMessage(EBMessage message)
	{
		if (!jEdit.getBooleanProperty("buffercloser.enabled")) return;
		if (!(message instanceof EditPaneUpdate)) return;
		EditPaneUpdate epu = (EditPaneUpdate) message;
		if (epu.getWhat() != epu.BUFFER_CHANGED) return;
		EditPane ep = epu.getEditPane();
		Buffer b = ep.getBuffer();
		BufferSet bs = ep.getBufferSet();
		for (View v : jEdit.getViews())
		{
			for (EditPane epc : v.getEditPanes())
			{
				if (epc == ep) continue;
				if (epc.getBufferSet() == bs) continue;
				if (epc.getBufferSet() == jEdit.getGlobalBufferSet()) continue;
				if (epc.getBufferSet().indexOf(b) < 0) continue;
				// found it open somewhere else thats not our
				// bufferset!
				jEdit.getBufferSetManager().removeBuffer(epc, b);
			}
		}
	}
}
