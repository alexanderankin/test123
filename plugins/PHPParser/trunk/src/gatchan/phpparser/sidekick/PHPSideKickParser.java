package gatchan.phpparser.sidekick;

import sidekick.SideKickParser;
import sidekick.SideKickParsedData;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;
import errorlist.DefaultErrorSource;
import errorlist.ErrorList;
import errorlist.ErrorSource;
import gatchan.phpparser.parser.PHPParser;
import gatchan.phpparser.parser.ParseException;
import gatchan.phpparser.parser.ParsingAbortedError;
import gatchan.phpparser.PHPParserPlugin;
import gatchan.phpparser.PHPErrorSource;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.text.Position;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.phpdt.internal.compiler.ast.PHPDocument;
import net.sourceforge.phpdt.internal.compiler.ast.AstNode;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import net.sourceforge.phpdt.internal.compiler.parser.OutlineableWithChildren;

/**
 * My sidekick implementation of the sidekick parser.
 *
 * @author Matthieu Casanova
 */
public class PHPSideKickParser extends SideKickParser {
  private PHPParser parser;

  public PHPSideKickParser(final String name) {
    super(name);
  }

  public SideKickParsedData parse(Buffer buffer, DefaultErrorSource errorSource) {
    final String path = buffer.getPath();
    try {
      if (parser != null && !parser.isStopped()) {
        Log.log(Log.ERROR, PHPSideKickParser.class, "The parser had not been stopped before asking a new parse !");
        stop();
      }
      parser = new PHPParser();
      parser.setPath(path);
      PHPErrorSource phpErrorSource = new PHPErrorSource(errorSource);
      errorSource.removeFileErrors(path);
      parser.addParserListener(phpErrorSource);
      try {
        parser.parseInfo(null, buffer.getText(0, buffer.getLength()));
      } catch (ParsingAbortedError parsingAbortedError) {
        return null;
      }
      PHPDocument phpDocument = parser.getPHPDocument();
      parser = null;
      SideKickParsedData data = new SideKickParsedData(buffer.getName());

      buildChildNodes(data.root, phpDocument, buffer);

      return data;


    } catch (ParseException e) {
      Log.log(Log.ERROR, this, e);
      errorSource.addError(ErrorSource.ERROR,
                           path,
                           e.currentToken.beginLine - 1,
                           e.currentToken.beginColumn,
                           e.currentToken.endColumn,
                           "Unhandled error please report the bug (with the trace in the activity log");
    }
    return null;
  }

  public void stop() {
    if (parser != null) {
      parser.stop();
    }
  }


  private void buildChildNodes(DefaultMutableTreeNode parent,
                               OutlineableWithChildren phpDocument,
                               Buffer buffer) {
    final List list = phpDocument.getList();
    for (int i = 0; i < list.size(); i++) {
      Outlineable o = (Outlineable) list.get(i);
      buildNode(parent, o, buffer);
    }
  }


  private void buildNode(DefaultMutableTreeNode parent,
                         Outlineable sourceNode,
                         Buffer buffer) {
    final AstNode astNode = ((AstNode)sourceNode);
    final Position position = buffer.createPosition(astNode.sourceStart);
    PHPAsset asset = new PHPAsset("php",astNode.toString(),position,buffer.createPosition(astNode.sourceEnd));
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(asset, true);
    if (sourceNode instanceof OutlineableWithChildren) {
      buildChildNodes(node, (OutlineableWithChildren) sourceNode, buffer);
    }
    parent.add(node);
  }


}
