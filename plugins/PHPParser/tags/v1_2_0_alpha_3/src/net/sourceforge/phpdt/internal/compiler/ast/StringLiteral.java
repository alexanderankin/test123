/*******************************************************************************
 * Copyright (c) 2000, 2001, 2002 International Business Machines Corp. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v0.5
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v05.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

import gatchan.phpparser.parser.Token;
import gatchan.phpparser.parser.Token;

public final class StringLiteral extends Literal {
  private String source;

  private AbstractVariable[] variablesInside;

  public StringLiteral(final Token token) {
    super(token.sourceStart, token.sourceEnd, token.beginLine,token.endLine,token.beginColumn,token.endColumn);
    source = token.image;
  }

  public StringLiteral(final String source, final int sourceStart, final int sourceEnd, final int beginLine, final int endLine, final int beginColumn, final int endColumn) {
    super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
    this.source = source;
  }

  /**
   * Return the expression as String.
   * @return the expression
   */
  public String toStringExpression() {
    return source;
  }

  /**
   * @deprecated - use field instead
   */
  public int sourceEnd() {
    return sourceEnd;
  }

  /**
   * @deprecated - use field instead
   */
  public int sourceStart() {
    return sourceStart;
  }

  public void getUsedVariable(final List list) {
    if (variablesInside != null) {
      for (int i = 0; i < variablesInside.length; i++) {
        variablesInside[i].getUsedVariable(list);
      }
    }
  }
}
