package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * @author Matthieu Casanova
 */
public class TryStatement extends Statement {
  private Block block;

  private List catchs;

  public TryStatement(Block block,
                      List catchs,
                      int sourceStart,
                      int sourceEnd,
                      int beginLine,
                      int endLine,
                      int beginColumn,
                      int endColumn) {
    super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
    this.block = block;
    this.catchs = catchs;
  }

  public String toString(int tab) {
    return null; //todo implement toString of catch
  }

  public void getOutsideVariable(List list) {
    block.getOutsideVariable(list);
    for (int i = 0; i < list.size(); i++) {
      Catch catched =  (Catch) list.get(i);
      catched.getOutsideVariable(list);
    }
  }

  public void getModifiedVariable(List list) {
    block.getModifiedVariable(list);
    for (int i = 0; i < list.size(); i++) {
      Catch catched =  (Catch) list.get(i);
      catched.getModifiedVariable(list);
    }
  }

  public void getUsedVariable(List list) {
    block.getUsedVariable(list);
    for (int i = 0; i < list.size(); i++) {
      Catch catched =  (Catch) list.get(i);
      catched.getUsedVariable(list);
    }
  }
}
