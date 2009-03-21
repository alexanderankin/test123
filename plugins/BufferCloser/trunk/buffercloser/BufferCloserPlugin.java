package buffercloser;

import java.util.ArrayList;
import java.util.Set;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.bufferset.BufferSet;
import org.gjt.sp.jedit.bufferset.BufferSetManager;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.util.Log;

public class BufferCloserPlugin extends EBPlugin {
	public void handleMessage(EBMessage message) {
		if (!jEdit.getBooleanProperty("buffercloser.enabled")) return;
		if (message instanceof EditPaneUpdate) {
			EditPaneUpdate epu = (EditPaneUpdate) message;
			if (epu.getWhat() == epu.BUFFER_CHANGED) {
				EditPane ep = epu.getEditPane();
				Buffer b = ep.getBuffer();
				BufferSet bs = ep.getBufferSet();
				for (View v: jEdit.getViews()) {
					for (EditPane epc: v.getEditPanes()) {
						if (epc == ep) continue;
						if (epc.getBufferSet() == bs) continue;
						if (epc.getBufferSet() == jEdit.getGlobalBufferSet()) continue;
						if (epc.getBufferSet().indexOf(b) < 0) continue;
                        // found it open somewhere else thats not our bufferset!
						jEdit.getBufferSetManager().removeBuffer(epc, b);
					}
				}

			}
		}
	}
}

