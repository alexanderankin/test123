package gatchan.phpparser;

import errorlist.ErrorList;
import errorlist.ErrorSource;
import gatchan.phpparser.parser.PHPParser;
import gatchan.phpparser.parser.ParseException;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.util.Log;

/**
 * The PHP Parser plugin.
 * The parser was taken from phpeclipse <a href="http://www.phpeclipse.de" target="_blank">http://www.phpeclipse.de</a>
 *
 * @author Matthieu Casanova
 */
public final class PHPParserPlugin extends EBPlugin {

  private ErrorList errorList;

  private PHPErrorSource errorSource;

  /**
   * Initialize the plugin.
   * When starting this plugin will add an Highlighter on each text area
   */
  public void start() {
    errorSource = new PHPErrorSource();
    ErrorSource.registerErrorSource(errorSource);
  }


  /**
   * uninitialize the plugin.
   * we will remove the Highlighter on each text area
   */
  public void stop() {
    ErrorSource.unregisterErrorSource(errorSource);
    errorSource = null;
    EditBus.removeFromBus(this);
  }


  public void handleMessage(final EBMessage message) {
    if (message instanceof BufferUpdate) {
      handleBufferUpdateMessage((BufferUpdate) message);
    }
  }

  private void handleBufferUpdateMessage(final BufferUpdate message) {
    final Object what = message.getWhat();
    final Buffer buffer = message.getBuffer();
    final String path = buffer.getPath();
    if (what == BufferUpdate.SAVING) {
      if (path.toLowerCase().endsWith(".php")) {
        final String text = buffer.getText(0, buffer.getLength());
        try {
          final PHPParser parser = new PHPParser();
          parser.setPath(path);
          errorSource.removeFileErrors(path);
          parser.addParserListener(errorSource);
          parser.parseInfo(null, text);
          if (errorSource.getErrorCount() != 0 && errorList == null) {
            errorList = new ErrorList(message.getView());
          }
        } catch (ParseException e) {
          Log.log(Log.ERROR, this, e);
          errorSource.addError(ErrorSource.ERROR,
                               path,
                               e.currentToken.beginLine - 1,
                               e.currentToken.beginColumn,
                               e.currentToken.endColumn,
                               "Unhandled error please report the bug");
        }
      }
    } else if (what == BufferUpdate.CLOSED) {
      errorSource.removeFileErrors(path);
    }

  }
}
