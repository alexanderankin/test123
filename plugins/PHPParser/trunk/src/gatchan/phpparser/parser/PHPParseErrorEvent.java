package gatchan.phpparser.parser;

import org.gjt.sp.util.Log;

/**
 * The PHPParseErrorEvent.
 *
 * @author Matthieu Casanova
 */
public class PHPParseErrorEvent {

  private int level;

  private final String path;
  private int beginLine;
  private int beginColumn;
  private int endLine;
  private int endColumn;

  private int sourceStart, sourceEnd;

  private String message;
  private String tokenGot;

  private String expectedToken;

  public PHPParseErrorEvent(int level,
                            String path,
                            String message,
                            String expectedToken,
                            String tokenGot,
                            int sourceStart,
                            int sourceEnd,
                            int beginLine,
                            int endLine,
                            int beginColumn,
                            int endColumn) {
    this.level = level;
    this.path = path;
    this.beginLine = beginLine;
    this.message = message;
    this.beginColumn = beginColumn;
    this.endLine = endLine;
    this.endColumn = endColumn;
    this.sourceStart = sourceStart;
    this.sourceEnd = sourceEnd;
    this.tokenGot = tokenGot;
    this.expectedToken = expectedToken;
  }

  public int getLevel() {
    return level;
  }

  public int getBeginLine() {
    return beginLine;
  }

  public int getBeginColumn() {
    return beginColumn;
  }

  public int getEndLine() {
    return endLine;
  }

  public int getEndColumn() {
    return endColumn;
  }

  public int getSourceStart() {
    return sourceStart;
  }

  public int getSourceEnd() {
    return sourceEnd;
  }

  public String getTokenGot() {
    return tokenGot;
  }

  public String getExpectedToken() {
    return expectedToken;
  }

  public String getMessage() {
    return message;
  }

  public String getPath() {
    return path;
  }

  public String toString() {
    return "PHPParseErrorEvent{" +
            "level=" + level +
            ", path='" + path + "', beginLine=" + beginLine +
            ", beginColumn=" + beginColumn +
            ", endLine=" + endLine +
            ", endColumn=" + endColumn +
            ", sourceStart=" + sourceStart +
            ", sourceEnd=" + sourceEnd +
            ", message='" + message + "', tokenGot='" + tokenGot + "', expectedToken='" + expectedToken + "'}";
  }
}
