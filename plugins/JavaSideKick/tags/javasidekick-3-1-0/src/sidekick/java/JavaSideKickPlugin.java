/*
Copyright (c) 2005, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

	* Redistributions of source code must retain the above copyright notice,
	this list of conditions and the following disclaimer.
	* Redistributions in binary form must reproduce the above copyright notice,
	this list of conditions and the following disclaimer in the documentation
	and/or other materials provided with the distribution.
	* Neither the name of the <ORGANIZATION> nor the names of its contributors
	may be used to endorse or promote products derived from this software without
	specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package sidekick.java;
//{{{ imports
import java.util.*;
import java.awt.event.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.buffer.*;
import errorlist.*;
import sidekick.*;
import sidekick.java.util.*;
import sidekick.java.node.*;
//}}}
public class JavaSideKickPlugin extends EditPlugin {
	public final static String NAME = "sidekick.java";
	public final static String OPTION_PREFIX = "options.sidekick.java.";
	public final static String PROPERTY_PREFIX = "plugin.sidekick.java.";
	public final static DefaultErrorSource ERROR_SOURCE = new DefaultErrorSource( "JavaSideKick" );
	public void start() {
		ErrorSource.registerErrorSource( ERROR_SOURCE );
		// EditBus.addToBus(this);
	}
	public void stop() {
		ErrorSource.unregisterErrorSource( ERROR_SOURCE );
		// EditBus.removeFromBus(this);
	}

	public static void insertImportAtCursor(EditPane editPane) {
		// Get some position information
		View view = editPane.getView();
		JEditTextArea textArea = editPane.getTextArea();
		JEditBuffer buffer = textArea.getBuffer();

		int line = textArea.getCaretLine();
		int offset = textArea.getLineStartOffset(line);
		int caret = textArea.getCaretPosition()-offset;
		String lineText = textArea.getLineText(line);
		if (caret == lineText.length()) caret--;
		String noWordSep = (String) textArea.getBuffer().getMode().getProperty("noWordSep");
		int start = TextUtilities.findWordStart(lineText, caret-1, noWordSep);
		int end = TextUtilities.findWordEnd(lineText, caret, noWordSep);
		if (end == -1) end = lineText.length();
		// Get the word
		String word = lineText.substring(start, end);
		String[] classes = Locator.getInstance().getClassName(word);
		if (classes.length == 0) {
			return;
		}

		List candids = new ArrayList(classes.length);
		for (int i = 0; i < classes.length; i++) {
			candids.add(new JavaCompletionFinder.JavaCompletionCandidate(
						classes[i],
						TigerLabeler.getClassIcon()));
		}
		JavaImportCompletion complete = new JavaImportCompletion(view, word, candids);

		if (classes.length == 1) {
			// Just insert it
			complete.insert(0);
		} else {
			// Construct the popup
			new SideKickCompletionPopup(view, null, offset+end, complete, false);
		}
	}

	/*
	public void handleMessage(EBMessage message) {
		if (PVHelper.isProjectViewerAvailable()) {
			if (message instanceof projectviewer.event.ViewerUpdate) {
				projectviewer.event.ViewerUpdate update = (projectviewer.event.ViewerUpdate) message;
				if (update.getType() == projectviewer.event.ViewerUpdate.Type.PROJECT_LOADED) {
					if (update.getNode() instanceof projectviewer.vpt.VPTProject) {
						projectviewer.vpt.VPTProject proj = (projectviewer.vpt.VPTProject) update.getNode();
						Locator.getInstance().reloadProjectJars(proj);
						Locator.getInstance().reloadProjectClassNames(proj);
					}
				}
			}
		}
	}
	*/
}

