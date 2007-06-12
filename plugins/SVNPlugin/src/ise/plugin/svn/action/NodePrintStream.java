package ise.plugin.svn.action;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

public class NodePrintStream extends PrintStream {

    private NodeActor actor;
    public static String LS = System.getProperty("line.separator");

    public NodePrintStream(NodeActor na) {
        super(new ByteArrayOutputStream());
        actor = na;
    }

    public void print(String s) {
        actor.print(s);
    }

    public void println(String s) {
        actor.print(s + LS);
    }

    public void close() {
        actor.close();
    }
}
