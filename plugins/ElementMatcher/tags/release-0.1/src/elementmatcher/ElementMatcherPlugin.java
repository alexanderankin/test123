package elementmatcher;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.msg.PluginUpdate;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import java.util.HashMap;
import java.util.Map;

// --- global
// BUG: dependent plugins reload not working correctly
// TODO: measure how long it takes to parse different files
// TODO: allow arbitrary settings panel for each provider
// + --- will implement if there would be provider that needs it (files?)
// TODO: background parsing???
// + --- will implement if slow parsing would be reported
// TODO: allow setting painter layer

// --- FileElementProvider
// TODO: files in current directory recognizer???
// TODO: recognize relative pathnames
// TODO: recognize unix pathnames
// TODO: provide auto-completion of pathnames after TAB (customized: enabled, key)
// TODO: recognize file:/+<path>, with %xx notion (e. g. %20 for space)
// BUG: in the following line only half of files matches  
// 16:22:42 [main] [message] Log: java.class.path=C:\Program Files\Java\jdk1.6.0_14\jre\lib\alt-rt.jar;C:\Program Files\Java\jdk1.6.0_14\jre\lib\charsets.jar;C:\Program Files\Java\jdk1.6.0_14\jre\lib\deploy.jar;C:\Program Files\Java\jdk1.6.0_14\jre\lib\javaws.jar;C:\Program Files\Java\jdk1.6.0_14\jre\lib\jce.jar;C:\Program Files\Java\jdk1.6.0_14\jre\lib\jsse.jar;C:\Program Files\Java\jdk1.6.0_14\jre\lib\management-agent.jar;C:\Program Files\Java\jdk1.6.0_14\jre\lib\plugin.jar;C:\Program Files\Java\jdk1.6.0_14\jre\lib\resources.jar;C:\Program Files\Java\jdk1.6.0_14\jre\lib\rt.jar;C:\Program Files\Java\jdk1.6.0_14\jre\lib\ext\dnsns.jar;C:\Program Files\Java\jdk1.6.0_14\jre\lib\ext\localedata.jar;C:\Program Files\Java\jdk1.6.0_14\jre\lib\ext\sunjce_provider.jar;C:\Program Files\Java\jdk1.6.0_14\jre\lib\ext\sunmscapi.jar;C:\Program Files\Java\jdk1.6.0_14\jre\lib\ext\sunpkcs11.jar;D:\Java\JEditCustomization\out\production\ElementMatcher;C:\Program Files\jEdit\jedit.jar;D:\Java\lib\collections-generic-4.01\collections-generic-4.01.jar;D:\Soft\Idea9\lib\junit-4.5.jar;C:\Program Files\jEdit\jars\kappalayout.jar;D:\Soft\Idea9\lib\idea_rt.jar


// --- RFCElementProvider
// TODO: RFCElementProvider: cache rfc's and show rfc title in tooltip
// TODO: RFCElementProvider: add action: open in browser tools.ietf.org/....

// --- DateElementProvider
// TODO: recognize dates like MS Word's Smart Tags (and show, like 25 June 2009, next Monday)
public class ElementMatcherPlugin extends EBPlugin {

    public static final String NAME = ElementMatcherPlugin.class.getName();
    public static final String OPTION_PREFIX = "options." + NAME + ".";

    private Map<JEditBuffer, ElementManager> elementsManagers = new HashMap<JEditBuffer, ElementManager>();
    private Map<JEditTextArea, ElementsTextAreaPainter> elementPainters = new HashMap<JEditTextArea, ElementsTextAreaPainter>();
    private ProviderManager providerManager = new ProviderManager();

    public static ElementMatcherPlugin getInstance() {
        return (ElementMatcherPlugin)jEdit.getPlugin(ElementMatcherPlugin.class.getName());
    }

    @Override
    public void start() {
        for (View view : jEdit.getViews()) {
            for (EditPane pane : view.getEditPanes()) {
                textAreaCreated(pane.getTextArea());
            }
        }
        for (Buffer buffer : jEdit.getBuffers()) {
            bufferCreated(buffer);
        }
    }

    @Override
    public void stop() {
        for (View view : jEdit.getViews()) {
            for (EditPane pane : view.getEditPanes()) {
                textAreaDestroyed(pane.getTextArea());
            }
        }
        for (Buffer buffer : jEdit.getBuffers()) {
            bufferDestroyed(buffer);
        }
    }

    @Override
    public void handleMessage(EBMessage message) {
        if (message instanceof EditPaneUpdate) {
            final EditPaneUpdate editPaneUpdate = (EditPaneUpdate)message;
            final JEditTextArea textArea = editPaneUpdate.getEditPane().getTextArea();
            final Object what = editPaneUpdate.getWhat();
            if (what == EditPaneUpdate.CREATED) {
                textAreaCreated(textArea);
            } else if (what == EditPaneUpdate.DESTROYED) {
                textAreaDestroyed(textArea);
            }
		} else if (message instanceof BufferUpdate) {
            final BufferUpdate bufferUpdate = (BufferUpdate)message;
            final JEditBuffer buffer = bufferUpdate.getBuffer();
            final Object what = bufferUpdate.getWhat();
            if (what == BufferUpdate.CREATED) {
                bufferCreated(buffer);
            } else if (what == BufferUpdate.CLOSING) {
                bufferDestroyed(buffer);
            }
        } else if (message instanceof PluginUpdate
                || message instanceof PropertiesChanged) {
            providerManager.reload();
            for (ElementManager elementManager : elementsManagers.values()) {
                elementManager.invalidateLines();
            }
        }
    }

    void repaintTextArea(JEditBuffer buffer, int startLine, int endLine) {
        for (JEditTextArea textArea : elementPainters.keySet()) {
            if (textArea.getBuffer().equals(buffer)) {
                textArea.invalidateLineRange(startLine, endLine);
                return;
            }
        }
    }

    private void textAreaCreated(JEditTextArea textArea) {
        final ElementsTextAreaPainter elementsTextAreaPainter = new ElementsTextAreaPainter(textArea);
        elementPainters.put(textArea, elementsTextAreaPainter);
    }

    private void textAreaDestroyed(JEditTextArea textArea) {
        final ElementsTextAreaPainter elementsTextAreaPainter = elementPainters.get(textArea);
        if (elementsTextAreaPainter != null) {
            elementsTextAreaPainter.close();
            elementPainters.remove(textArea);
        }
    }

    private void bufferCreated(JEditBuffer buffer) {
        final ElementManager elementManager = new ElementManager(this, buffer);
        elementsManagers.put(buffer, elementManager);
    }

    private void bufferDestroyed(JEditBuffer buffer) {
        final ElementManager elementManager = elementsManagers.get(buffer);
        if (elementManager != null) {
            elementManager.close();
            elementsManagers.remove(buffer);
        }
    }

    ElementManager getElementManager(JEditBuffer buffer) {
        if (!elementsManagers.containsKey(buffer)) {
            throw new IllegalStateException("Buffer not tracked: " + buffer);
        }
        return elementsManagers.get(buffer);
    }

    public ProviderManager getProviderManager() {
        return providerManager;
    }

}