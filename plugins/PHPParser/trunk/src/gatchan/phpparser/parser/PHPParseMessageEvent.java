package gatchan.phpparser.parser;

/**
 * The PHPParseErrorEvent.
 *
 * @author Matthieu Casanova
 */
public class PHPParseMessageEvent {

  public static int MESSAGE_SHORT_OPEN_TAG = 1;
  public static int MESSAGE_SWITCH_ENDSWITCH_TAG = 2;
  public static int MESSAGE_IF_ENDIF_TAG = 3;
  public static int MESSAGE_FOR_ENDFOR_TAG = 4;
  public static int MESSAGE_WHILE_ENDWHILE_TAG = 5;
  public static int MESSAGE_FOREACH_ENDFOREACH_TAG = 6;

  private int messageClass;
  private int level;

  private final String path;
  private int beginLine;
  private int beginColumn;
  private int endLine;
  private int endColumn;

  private int sourceStart, sourceEnd;

  private String message;

  public PHPParseMessageEvent(int level,
                              int messageClass,
                              String path,
                              String message,
                              int sourceStart,
                              int sourceEnd,
                              int beginLine,
                              int endLine,
                              int beginColumn,
                              int endColumn) {
    this.level = level;
    this.messageClass = messageClass;
    this.path = path;
    this.beginLine = beginLine;
    this.message = message;
    this.beginColumn = beginColumn;
    this.endLine = endLine;
    this.endColumn = endColumn;
    this.sourceStart = sourceStart;
    this.sourceEnd = sourceEnd;
  }

  /**
   * @deprecated 
   * @param level
   * @param path
   * @param message
   * @param sourceStart
   * @param sourceEnd
   * @param beginLine
   * @param endLine
   * @param beginColumn
   * @param endColumn
   */
  public PHPParseMessageEvent(int level,
                              String path,
                              String message,
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

  public String getMessage() {
    return message;
  }

  public String getPath() {
    return path;
  }

  public int getMessageClass() {
    return messageClass;
  }
}
