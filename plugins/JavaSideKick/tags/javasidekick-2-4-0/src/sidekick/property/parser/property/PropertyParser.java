/* Generated By:JavaCC: Do not edit this line. PropertyParser.java */
package sidekick.property.parser.property;

import java.util.*;

import sidekick.util.Location;
public class PropertyParser implements PropertyParserConstants {

    // accumulates parse exceptions
    private List<ParseException> exceptions = new ArrayList<ParseException>();

    // for testing...
    public static void main(String args[]) throws ParseException {
        PropertyParser parser = new PropertyParser(System.in);
        parser.Properties();
    }

    /**
     * Utility to trim horizontal whitespace from the front of a string.
     * @param the string to trim
     * @return the trimmed string
     */
    public String trimFront(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }

        int index = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ' || s.charAt(i) == '\t')
                ++index;
            else
                break;
        }
        return s.substring(index);
    }

    /**
     * Setting the tab size makes the token locations more accurate.
     * @param size the size of the tabs in the current jEdit buffer
     */
    public void setTabSize(int size) {
        jj_input_stream.setTabSize(size);
    }

    /**
     * @return the current tab size that the parser is using
     */
    public int getTabSize() {
        return jj_input_stream.getTabSize(0);
    }

    /**
     * Creates a start location from the given token.
     * @param t a token
     * @return the start location of the token.
     */
    public Location createStartLocation(Token t) {
       if (t == null)
           return new Location(0, 0);
       return new Location(t.beginLine, t.beginColumn);
    }

    /**
     * Creates an end location from the given token.
     * @param t a token
     * @return the end location of the token.
     */
    public Location createEndLocation(Token t) {
       if (t == null)
           return new Location(0, 0);
       return new Location(t.endLine, t.endColumn);
    }

    /**
     * Add an exception to the list of exceptions. Rather than failing on
     * any exception, this parser will continue parsing and will accumulate
     * any/all exceptions.
     * @param pe a ParseException to accumulate
     */
    public void addException(ParseException pe) {
        if (pe != null)
            exceptions.add(pe);
    }

    /**
     * @return the list of accumulated ParseExceptions
     */
    public List<ParseException> getExceptions() {
        return exceptions;
    }

  final public List<Property> Properties() throws ParseException {
    List<Property> list = new ArrayList<Property>();
    Property p = null;
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMENT:
      case BARE_KEY:
      case KEY:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      if (jj_2_1(2)) {
        Comment();
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case BARE_KEY:
        case KEY:
          p = Property();
                if (p != null)
                    list.add(p);
          break;
        default:
          jj_la1[1] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
    }
    jj_consume_token(0);
        /* always sort the list by property name -- could make the caller do
        this and just return the properties in the original order */
        Collections.sort(list);
        {if (true) return list;}
    throw new Error("Missing return statement in function");
  }

/**
 * @return a single property from the property file.
 */
  final public Property Property() throws ParseException {
    Token key = null;
    Token value = null;
    try {
      if (jj_2_2(2)) {
        key = jj_consume_token(KEY);
              token_source.SwitchTo(ParseEquals);
        jj_consume_token(EQUALS);
              token_source.SwitchTo(ParseValue);
        value = jj_consume_token(VALUE);
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case BARE_KEY:
          key = jj_consume_token(BARE_KEY);
                token_source.SwitchTo(DEFAULT);
          break;
        default:
          jj_la1[2] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
        Property prop = new Property();
        prop.setStartLocation(createStartLocation(key));
        prop.setEndLocation(createEndLocation(value == null ? key : value));

        /* key -- need to trim as the regex production can capture whitespace,
        and by definition, the key cannot contain any whitespace characters */
        String out = key.image.trim();

        /* key can have escaped 'equals' characters, unescape them */
        out = out.replaceAll("\\\\=", "=");
        out = out.replaceAll("\\\\:", ":");
        out = out.replaceAll("\\\\ ", " ");
        out = out.replaceAll("\\\\\\t", "\t");
        prop.setKey(out);

        /* value -- need to combine multi-line values into a single line. Leading
        whitespace on continuation lines is discarded, as is leading whitespace
        at the start of the value. */
        out = value == null ? "" : value.image.replaceAll("\\\\(\\s)+", "");
        out = trimFront(out);
        prop.setValue(out);
        {if (true) return prop;}
    } catch (ParseException e) {
        addException(generateParseException());
    }
    throw new Error("Missing return statement in function");
  }

/* For completeness only.  This parser does nothing special with comments. */
  final public void Comment() throws ParseException {
    jj_consume_token(COMMENT);
  }

  final private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  final private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  final private boolean jj_3_2() {
    if (jj_scan_token(KEY)) return true;
    if (jj_scan_token(EQUALS)) return true;
    return false;
  }

  final private boolean jj_3_1() {
    if (jj_scan_token(4)) return true;
    return false;
  }

  public PropertyParserTokenManager token_source;
  JavaCharStream jj_input_stream;
  public Token token, jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  public boolean lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[3];
  static private int[] jj_la1_0;
  static {
      jj_la1_0();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {0x70,0x60,0x20,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[2];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  public PropertyParser(java.io.InputStream stream) {
     this(stream, null);
  }
  public PropertyParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new JavaCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new PropertyParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public PropertyParser(java.io.Reader stream) {
    jj_input_stream = new JavaCharStream(stream, 1, 1);
    token_source = new PropertyParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public PropertyParser(PropertyParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(PropertyParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  final private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }

  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  final public Token getToken(int index) {
    Token t = lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      boolean exists = false;
      for (java.util.Enumeration e = jj_expentries.elements(); e.hasMoreElements();) {
        int[] oldentry = (int[])(e.nextElement());
        if (oldentry.length == jj_expentry.length) {
          exists = true;
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              exists = false;
              break;
            }
          }
          if (exists) break;
        }
      }
      if (!exists) jj_expentries.addElement(jj_expentry);
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[10];
    for (int i = 0; i < 10; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 3; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 10; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

  final private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 2; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  final private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
