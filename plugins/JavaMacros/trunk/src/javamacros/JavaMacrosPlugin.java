package javamacros;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.Macros;

public class JavaMacrosPlugin extends EditPlugin {

    public static final String NAME = JavaMacrosPlugin.class.getName();
    public static final String OPTION_PREFIX = "options." + NAME + ".";

    @Override
    public void start() {
        super.start();
        Macros.registerHandler(new JavaMacroHandler());
    }

}