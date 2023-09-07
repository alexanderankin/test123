package json;

import beauty.BeautyThread;
import beauty.beautifiers.Beautifier;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;


public class JSONPlugin extends EditPlugin {
    public static final String NAME = "json";


    public void start() {
    }

    public void stop() {
    }

    public static void minify( Buffer buffer, View view ) {
        boolean showErrorDialogs = jEdit.getBooleanProperty( "beauty.general.showErrorDialogs", true );
        Beautifier beautifier = new Minify();
        new BeautyThread( buffer, view, showErrorDialogs, beautifier ).run();
    }
}