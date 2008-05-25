/*
 * $Revision$
 * $Date$
 *
 * NOTE: THIS CLASS is a COPY of the original work from fest-swing@1.01a1
 * Created on Jul 31, 2007
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Copyright @2007 the original author or authors.
 */
package org.fest.swing.finder;

import java.awt.Dialog;
import javax.swing.JDialog;
import java.util.concurrent.TimeUnit;

import org.fest.swing.core.Robot;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.core.ComponentMatcher;
import org.fest.swing.core.GenericTypeMatcher;

/**
 * NOTE: THIS CLASS is a COPY of the original work from fest-swing@1.01a1
 *
 * Understands a finder for <code>{@link Dialog}</code>s. This class cannot be used directly, please see 
 * <code>{@link WindowFinder}</code>.
 *
 * @author Yvonne Wang 
 * @author Alex Ruiz
 */
public final class DialogByTitleFinder extends WindowFinderTemplate<Dialog> {
	private String windowTitle;
	
  DialogByTitleFinder(String dialogTitle) {
    super("name", Dialog.class);
	windowTitle=dialogTitle;
  }

  DialogByTitleFinder(Class<? extends Dialog> dialogType) {
    super(dialogType);
  }

  /**
   * Sets the timeout for this finder. The window to search should be found within the given time period. 
   * @param timeout the number of milliseconds before stopping the search.
   * @return this finder.
   */
  @Override public DialogByTitleFinder withTimeout(long timeout) {
    return (DialogByTitleFinder)super.withTimeout(timeout);
  }

  /**
   * Sets the timeout for this finder. The window to search should be found within the given time period.
   * @param timeout the period of time the search should be performed.
   * @param unit the time unit for <code>timeout</code>.
   * @return this finder.
   */
  @Override public DialogByTitleFinder withTimeout(long timeout, TimeUnit unit) {
    return (DialogByTitleFinder)super.withTimeout(timeout, unit);
  }


  /**
   * Finds a <code>{@link Dialog}</code> by name or type.
   * @param robot contains the underlying finding to delegate the search to.
   * @return a <code>DialogFixture</code> managing the found <code>Dialog</code>.
   * @throws org.fest.swing.exception.WaitTimedOutError if a <code>Dialog</code> could not be found.
   */
  public DialogFixture using(Robot robot) {
    return new DialogFixture(robot, findComponentWith(robot));
  }

  protected String componentDisplayName() {
    return "dialog";
  }
  
  protected ComponentMatcher nameMatcher() {
	  System.err.println("HELLOOOOOOO");
	  return new GenericTypeMatcher<JDialog>(){
		  protected boolean isMatching(JDialog d){
			  System.out.println("IsMatching("+d.getTitle()+")");
			  return d.getTitle().equals(windowTitle);
		  }
	  };
  }
  
	public static DialogByTitleFinder findByTitle(String title){
		return new DialogByTitleFinder(title);
	}
}