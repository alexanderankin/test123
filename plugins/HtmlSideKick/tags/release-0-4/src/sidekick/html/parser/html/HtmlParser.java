/* Generated By:JavaCC: Do not edit this line. HtmlParser.java */
package sidekick.html.parser.html;

public class HtmlParser implements HtmlParserConstants {

  static String NL = System.getProperty("line.separator");

  public void setLineSeparator(String ls) {
    NL = ls;
  }

  private static String getTokenText(Token first, Token cur) {
    Token t;
    StringBuffer sb = new StringBuffer();

    for (t=first; t != cur.next; t = t.next) {
      if (t.specialToken != null) {
        Token tt=t.specialToken;
        while (tt.specialToken != null)
          tt = tt.specialToken;
        for (; tt != null; tt = tt.next)
          sb.append(tt.image);
      };
      sb.append(t.image);
    };
    return sb.toString();
  }

  public static void main(String[] args) throws ParseException {
    HtmlParser parser = new HtmlParser(System.in);
    HtmlDocument doc = parser.HtmlDocument();
    doc.accept(new HtmlDumper(System.out));
    System.exit(0);
  }

  final public HtmlDocument HtmlDocument() throws ParseException {
  HtmlDocument.ElementSequence s;
    s = ElementSequence();
    jj_consume_token(0);
    {if (true) return new HtmlDocument(s);}
    throw new Error("Missing return statement in function");
  }

  final public HtmlDocument.ElementSequence ElementSequence() throws ParseException {
  HtmlDocument.ElementSequence s = new HtmlDocument.ElementSequence();
  HtmlDocument.HtmlElement h;
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EOL:
      case COMMENT_START:
      case ENDTAG_START:
      case TAG_START:
      case DECL_START:
      case PCDATA:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      h = Element();
                  s.addElement(h);
    }
    {if (true) return s;}
    throw new Error("Missing return statement in function");
  }

  final public HtmlDocument.HtmlElement Element() throws ParseException {
  HtmlDocument.HtmlElement e;
  Token text;
    if (jj_2_1(2)) {
      e = Tag();
                            {if (true) return e;}
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ENDTAG_START:
        e = EndTag();
                            {if (true) return e;}
        break;
      case COMMENT_START:
        e = CommentTag();
                            {if (true) return e;}
        break;
      case DECL_START:
        e = DeclTag();
                            {if (true) return e;}
        break;
      default:
        jj_la1[1] = jj_gen;
        if (jj_2_2(2)) {
          e = ScriptBlock();
                             {if (true) return e;}
        } else if (jj_2_3(2)) {
          e = StyleBlock();
                             {if (true) return e;}
        } else if (jj_2_4(2)) {
          jj_consume_token(TAG_START);
          text = jj_consume_token(LST_ERROR);
                            {if (true) return new HtmlDocument.Text("<" + text.image);}
        } else {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case PCDATA:
            text = jj_consume_token(PCDATA);
                            {if (true) return new HtmlDocument.Text(text.image);}
            break;
          case EOL:
            jj_consume_token(EOL);
                            {if (true) return new HtmlDocument.Newline();}
            break;
          default:
            jj_la1[2] = jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
          }
        }
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public HtmlDocument.Attribute Attribute() throws ParseException {
  HtmlDocument.Attribute a;
  Token t1, t2=null;
    t1 = jj_consume_token(ATTR_NAME);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ATTR_EQ:
      jj_consume_token(ATTR_EQ);
      t2 = jj_consume_token(ATTR_VAL);
      break;
    default:
      jj_la1[3] = jj_gen;
      ;
    }
    if (t2 == null) {
      a = new HtmlDocument.Attribute(t1.image);
    }
    else {
      a = new HtmlDocument.Attribute(t1.image, t2.image);
    }

     {if (true) return a;}
    throw new Error("Missing return statement in function");
  }

  final public HtmlDocument.AttributeList AttributeList() throws ParseException {
  HtmlDocument.AttributeList alist = new HtmlDocument.AttributeList();
  HtmlDocument.Attribute a;
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ATTR_NAME:
        ;
        break;
      default:
        jj_la1[4] = jj_gen;
        break label_2;
      }
      a = Attribute();
                   alist.addAttribute(a);
    }
    {if (true) return alist;}
    throw new Error("Missing return statement in function");
  }

  final public HtmlDocument.HtmlElement Tag() throws ParseException {
  Token t, et;
  HtmlDocument.AttributeList alist;
  Token firstToken = getToken(1);
  Token st = null;
  boolean isJspTag = false;
    try {
      st = jj_consume_token(TAG_START);
      t = jj_consume_token(TAG_NAME);
      alist = AttributeList();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAG_END:
        et = jj_consume_token(TAG_END);
        break;
      case TAG_SLASHEND:
        et = jj_consume_token(TAG_SLASHEND);
        break;
      default:
        jj_la1[5] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
        String tag_start = "<";
        String tag_name = "";
        if (st.image.startsWith("<") && st.image.endsWith(":")) {
            isJspTag = true;
            tag_start = "<";
            tag_name = st.image.substring(1) + t.image;
        }
        else {
            tag_name = t.image;
        }
        if (st.image.startsWith("<%@")) {
            isJspTag = true;
        }
      HtmlDocument.Tag rtn_tag = new HtmlDocument.Tag(tag_start, tag_name, alist, et.image);
      if (et.kind == TAG_SLASHEND) {
          rtn_tag.setEmpty(true);
      }
      rtn_tag.setStartLocation(st.beginLine, st.beginColumn);
      rtn_tag.setEndLocation(et.endLine, et.endColumn);
      rtn_tag.setIsJspTag(isJspTag);
      {if (true) return rtn_tag;}
    } catch (ParseException ex) {
    token_source.SwitchTo(DEFAULT);
    String s = getTokenText(firstToken, getNextToken());
    {if (true) return new HtmlDocument.Text(s);}
    }
    throw new Error("Missing return statement in function");
  }

  final public void StyleBlockContents() throws ParseException {
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case BLOCK_EOL:
      case BLOCK_LBR:
      case BLOCK_WORD:
        ;
        break;
      default:
        jj_la1[6] = jj_gen;
        break label_3;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case BLOCK_EOL:
        jj_consume_token(BLOCK_EOL);
        break;
      case BLOCK_WORD:
        jj_consume_token(BLOCK_WORD);
        break;
      case BLOCK_LBR:
        jj_consume_token(BLOCK_LBR);
        break;
      default:
        jj_la1[7] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

  final public HtmlDocument.ElementSequence ScriptBlockContents() throws ParseException {
  HtmlDocument.ElementSequence e = new HtmlDocument.ElementSequence();
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case BLOCK_EOL:
      case BLOCK_LBR:
      case BLOCK_WORD:
        ;
        break;
      default:
        jj_la1[8] = jj_gen;
        break label_4;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case BLOCK_EOL:
        jj_consume_token(BLOCK_EOL);
        break;
      case BLOCK_WORD:
        jj_consume_token(BLOCK_WORD);
        break;
      case BLOCK_LBR:
        jj_consume_token(BLOCK_LBR);
        break;
      default:
        jj_la1[9] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    {if (true) return e;}
    throw new Error("Missing return statement in function");
  }

  final public HtmlDocument.HtmlElement ScriptBlock() throws ParseException {
  HtmlDocument.AttributeList alist;
  HtmlDocument.ElementSequence e;
  Token firstToken = getToken(1);
  Token st, et;
    try {
      st = jj_consume_token(TAG_START);
      jj_consume_token(TAG_SCRIPT);
      alist = AttributeList();
      jj_consume_token(TAG_END);
      token_source.SwitchTo(LexScript);
      e = ScriptBlockContents();
      et = jj_consume_token(SCRIPT_END);
        HtmlDocument.TagBlock b = new HtmlDocument.TagBlock("SCRIPT", alist, e);
        b.setStartLocation(st.beginLine, st.beginColumn);
        b.setEndLocation(et.endLine, et.endColumn);
        {if (true) return b;}
    } catch (ParseException ex) {
      ex.printStackTrace();
    token_source.SwitchTo(DEFAULT);
    String s = getTokenText(firstToken, getNextToken());
    {if (true) return new HtmlDocument.Text(s);}
    }
    throw new Error("Missing return statement in function");
  }

  final public HtmlDocument.HtmlElement StyleBlock() throws ParseException {
  HtmlDocument.AttributeList alist;
  Token firstToken = getToken(1);
  Token st, et;
    try {
      st = jj_consume_token(TAG_START);
      jj_consume_token(TAG_STYLE);
      alist = AttributeList();
      jj_consume_token(TAG_END);
      token_source.SwitchTo(LexStyle);
      StyleBlockContents();
      et = jj_consume_token(STYLE_END);
        HtmlDocument.TagBlock b = new HtmlDocument.TagBlock("STYLE", alist, null);
        b.setStartLocation(st.beginLine, st.beginColumn);
        b.setEndLocation(et.endLine, et.endColumn);
        {if (true) return b;}
    } catch (ParseException ex) {
    token_source.SwitchTo(DEFAULT);
    String s = getTokenText(firstToken, getNextToken());
    {if (true) return new HtmlDocument.Text(s);}
    }
    throw new Error("Missing return statement in function");
  }

  final public HtmlDocument.HtmlElement EndTag() throws ParseException {
  Token t;
  Token firstToken = getToken(1);
  Token st, et;
    try {
      st = jj_consume_token(ENDTAG_START);
      t = jj_consume_token(TAG_NAME);
      et = jj_consume_token(TAG_END);
        String tag_name = "";
        if (st.image.startsWith("</") && st.image.endsWith(":")) {
            tag_name = st.image.substring(2) + t.image;
        }
        else
            tag_name = t.image;
        HtmlDocument.EndTag b = new HtmlDocument.EndTag(tag_name);
        b.setStartLocation(st.beginLine, st.beginColumn);
        b.setEndLocation(et.endLine, et.endColumn);
        {if (true) return b;}
    } catch (ParseException ex) {
    token_source.SwitchTo(DEFAULT);
    String s = getTokenText(firstToken, getNextToken());
    {if (true) return new HtmlDocument.Text(s);}
    }
    throw new Error("Missing return statement in function");
  }

  final public HtmlDocument.Comment CommentTag() throws ParseException {
  Token t, comment_start, comment_end = null;
  StringBuffer s = new StringBuffer();
    comment_start = jj_consume_token(COMMENT_START);
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DASH:
      case COMMENT_EOL:
      case COMMENT_WORD:
        ;
        break;
      default:
        jj_la1[10] = jj_gen;
        break label_5;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DASH:
        t = jj_consume_token(DASH);
               s.append(t.image);
        break;
      case COMMENT_EOL:
        jj_consume_token(COMMENT_EOL);
                       s.append(NL);
        break;
      case COMMENT_WORD:
        t = jj_consume_token(COMMENT_WORD);
                         s.append(t.image);
        break;
      default:
        jj_la1[11] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 0:
      jj_consume_token(0);
      break;
    case COMMENT_END:
      comment_end = jj_consume_token(COMMENT_END);
      break;
    default:
      jj_la1[12] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return new HtmlDocument.Comment(comment_start.image + s.toString() + (comment_end == null ? "" : comment_end.image));}
    throw new Error("Missing return statement in function");
  }

  final public HtmlDocument.Comment DeclTag() throws ParseException {
  Token t;
    jj_consume_token(DECL_START);
    t = jj_consume_token(DECL_ANY);
    jj_consume_token(DECL_END);
    {if (true) return new HtmlDocument.Comment(t.image);}
    throw new Error("Missing return statement in function");
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

  final private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  final private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  final private boolean jj_3R_8() {
    if (jj_scan_token(TAG_START)) return true;
    if (jj_scan_token(TAG_STYLE)) return true;
    return false;
  }

  final private boolean jj_3_3() {
    if (jj_3R_8()) return true;
    return false;
  }

  final private boolean jj_3_2() {
    if (jj_3R_7()) return true;
    return false;
  }

  final private boolean jj_3R_6() {
    if (jj_scan_token(TAG_START)) return true;
    if (jj_scan_token(TAG_NAME)) return true;
    return false;
  }

  final private boolean jj_3_1() {
    if (jj_3R_6()) return true;
    return false;
  }

  final private boolean jj_3R_7() {
    if (jj_scan_token(TAG_START)) return true;
    if (jj_scan_token(TAG_SCRIPT)) return true;
    return false;
  }

  final private boolean jj_3_4() {
    if (jj_scan_token(TAG_START)) return true;
    if (jj_scan_token(LST_ERROR)) return true;
    return false;
  }

  public HtmlParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  public Token token, jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  public boolean lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[13];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_0();
      jj_la1_1();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {0x1f800,0xb000,0x10800,0x4000000,0x800000,0x3000000,0x0,0x0,0x0,0x0,0x0,0x0,0x1,};
   }
   private static void jj_la1_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x0,0x0,0x0,0x0,0x700,0x700,0x700,0x700,0xe,0xe,0x1,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[4];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  public HtmlParser(java.io.InputStream stream) {
     this(stream, null);
  }
  public HtmlParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new HtmlParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 13; i++) jj_la1[i] = -1;
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
    for (int i = 0; i < 13; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public HtmlParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new HtmlParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 13; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 13; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public HtmlParser(HtmlParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 13; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(HtmlParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 13; i++) jj_la1[i] = -1;
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
    boolean[] la1tokens = new boolean[43];
    for (int i = 0; i < 43; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 13; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 43; i++) {
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
    for (int i = 0; i < 4; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
            case 3: jj_3_4(); break;
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
