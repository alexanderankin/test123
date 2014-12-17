package sidekick.antlr4;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;

import org.antlr.v4.Tool;

public class AntlrSideKickPlugin extends EditPlugin {
    public static final String NAME = "sidekick.antlr4";
    public void start() {
    }

    public void stop() {
    }

    // Generate antlr files from the current file in the buffer. 
    public static void generateFiles(Buffer buffer) {
        if ("antlr4".equals(buffer.getMode().getName())) {
            String[] args = new String[]{buffer.getPath()};
            Tool antlr = new Tool(args);
            antlr.processGrammarsOnCommandLine();
        } 
    }
}