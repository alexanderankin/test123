package gatchan.phpparser;

import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.Log;

/**
 * The PHP Parser plugin.
 *
 * @author Matthieu Casanova
 */
public final class PHPParserPlugin extends EBPlugin {

    public void handleMessage(EBMessage message) {
        if (message instanceof BufferUpdate) {
            final BufferUpdate bufferUpdate = (BufferUpdate) message;
            final Object what = bufferUpdate.getWhat();
            if (what == BufferUpdate.LOADED) {
                Buffer buffer = bufferUpdate.getBuffer();
                if ("php".equals(buffer.getMode().getName())) {
                    buffer.setProperty("sidekick.parser", "PHPParser");
                }
            } else if (what == BufferUpdate.PROPERTIES_CHANGED) {
                Buffer buffer = bufferUpdate.getBuffer();
                if ("php".equals(buffer.getMode().getName())) {
                    buffer.setProperty("sidekick.parser", "PHPParser");
                } else if ("PHPParser".equals(buffer.getProperty("sidekick.parser"))) {
                    buffer.setProperty("sidekick.parser", null);
                }
            }
        }
    }
/*
  private ErrorList errorList;

  private PHPErrorSource errorSource;
  */
    /**
     * The instance of the plugin for a static access.
     */
    //private static PHPParserPlugin instance;
    /*
 private boolean parseOnLoad;
 private boolean parseOnSave;
 private boolean activateParser;
        */
    /**
     * The regexp defining files that need to be parsed.
     */
    // private RE filesToParseRE;


    /**
     * Initialize the plugin.
     * When starting this plugin will add an Highlighter on each text area
     */
/*  public void start() {
    instance = this;
    errorSource = new PHPErrorSource();
    ErrorSource.registerErrorSource(errorSource);
    propertiesChanged();
  }    */


    /**
     * uninitialize the plugin.
     * we will remove the Highlighter on each text area
     */
    /* public void stop() {
      // ErrorSource.unregisterErrorSource(errorSource);
       instance = null;
       errorSource = null;
       errorList = null;
       EditBus.removeFromBus(this);
     }     */

    /**
     * Get the instance of the parser. This method is called in the actions.xml
     *
     * @return the instance of the parser
     */
    /* public static PHPParserPlugin getInstance() {
       return instance;
     }    */

    /* public void handleMessage(final EBMessage message) {
       if (message instanceof BufferUpdate) {
         handleBufferUpdateMessage((BufferUpdate) message);
       } else if (message instanceof PropertiesChanged) {
         propertiesChanged();

       }
     }    */

    /* private void handleBufferUpdateMessage(final BufferUpdate message) {
       final Object what = message.getWhat();
       final Buffer buffer = message.getBuffer();
       final String path = buffer.getPath();
       if (activateParser) {
         if (parseOnLoad && what == BufferUpdate.LOADED) {
           parseIfPaternMatch(path, buffer, message.getView());
         } else if (parseOnSave && what == BufferUpdate.SAVING) {
           parseIfPaternMatch(path, buffer, message.getView());
         } else if (what == BufferUpdate.CLOSED) {
           Log.log(Log.DEBUG, PHPParserPlugin.class, "Buffer closed : " + path);
        //   errorSource.removeFileErrors(path);
         }
       }
     }  */

    /**
     * parse a buffer if the path match the patern of files to parse
     *
     * @param path   the path of the file to parse
     * @param buffer the buffer
     * @param view   the jEdit's view
     */
    /* private void parseIfPaternMatch(final String path, final Buffer buffer, final View view) {
       if (filesToParseRE.isMatch(path)) {
         Log.log(Log.DEBUG, PHPParserPlugin.class, "Parsing launched by load or save on : " + path);
         final String text = buffer.getText(0, buffer.getLength());
         parse(path, text, view);
       }
     }     */

    /**
     * Parse a php string.
     *
     * @param path the path of the file
     * @param text the text to parse
     * @param view the jEdit's view
     */
/*  private void parse(final String path, final String text, final View view) {
    try {
      Log.log(Log.DEBUG, PHPParserPlugin.class, "Parsing " + path);
      final PHPParser parser = new PHPParser();
      parser.setPath(path);
      errorSource.removeFileErrors(path);
      parser.addParserListener(errorSource);
      parser.parseInfo(null, text);
      if (errorSource.getErrorCount() != 0 && errorList == null) {
        errorList = new ErrorList(view);
      }
    } catch (ParseException e) {
      Log.log(Log.ERROR, this, e);
      errorSource.addError(ErrorSource.ERROR,
              path,
              e.currentToken.beginLine - 1,
              e.currentToken.beginColumn,
              e.currentToken.endColumn,
              "Unhandled error please report the bug (with the trace in the activity log");
    }
  }  */

    /**
     * This is the entry point for the actions.xml that will parse the buffer.
     *
     * @param view   the view
     * @param buffer the buffer
     */
/*  public void parseBuffer(final View view, final Buffer buffer) {
    final String path = buffer.getPath();
    Log.log(Log.DEBUG, PHPParserPlugin.class, "Parsing launched by user request : " + path);
    final String text = buffer.getText(0, buffer.getLength());
    parse(path, text, view);
  }  */ 
}
