package sidekick.antlr4;

import org.gjt.sp.jedit.EditPlugin;

// QUESTION: add an action to allow the user to generate files from the current grammar file?
// The antlr-4.4-complete.jar is all that is necessary, and it's a dependency of this plugin
// via the Antlr plugin.

public class AntlrSideKickPlugin extends EditPlugin {
    public static final String NAME = "sidekick.antlr4";
    public void start() {
    }

    public void stop() {
    }

}