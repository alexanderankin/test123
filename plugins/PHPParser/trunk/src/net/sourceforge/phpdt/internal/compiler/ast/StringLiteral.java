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
    super(token.sourceStart,token.sourceEnd);
    source = token.image;
  }

  /**
   * Create a new StringLiteral
   * @param token the token
   * @param s sourcestart
   * @param e sourceend
   * @deprecated
   */
  public StringLiteral(final String token, final int s, final int e) {
    super(s, e);
    source = token;
  }

  /**
   * Create a new StringLiteral
   * @param token the token
   * @param s sourcestart
   * @param e sourceend
   * @deprecated
   */
  public StringLiteral(final String token,
                       final int s,
                       final int e,
                       final AbstractVariable[] variablesInside) {
    super(s, e);
    source = token;
    this.variablesInside = variablesInside;
  }

  /**
   * Create a new StringLiteral
   * @param token the token
   * @param s sourcestart
   * @param e sourceend
   * @deprecated
   */
  public StringLiteral(final char[] token, final int s, final int e) {
    this(new String(token),s, e);
  }

  public StringLiteral(final int s, final int e) {
    super(s, e);
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
