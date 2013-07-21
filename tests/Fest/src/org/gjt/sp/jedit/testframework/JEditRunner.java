/*
 * JEditRunner.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2013 Eric Le Lay
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.gjt.sp.jedit.testframework;

import java.util.ArrayList;
import java.util.List;

import org.junit.rules.MethodRule;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 *	Runs a new instance of jEdit per tested class.
 *  Closes all buffers and windows between tests, in an attempt to
 *  sanitize the test environment a bit. 
 *  Usage: add {@link RunWith}(JEditPerTestRunner) to your test class
 *  @see TestData if you make use of a test_data directory in your plugin
 *  (see XSLT as an example).
 **/
public class JEditRunner extends BlockJUnit4ClassRunner {

	public JEditRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}
	
	
	@Override
	protected List<MethodRule> rules(Object target) {
		// hooks the RunJEditMethodRule to be run before/after each test
		List<MethodRule> myRules = new ArrayList<MethodRule>(super.rules(target));
		myRules.add(new RunJEditMethodRule());
		return myRules;
	}

	@Override
	protected Statement withBeforeClasses(Statement statement) {
		final Statement s = super.withBeforeClasses(statement);
		return new Statement() {
			
			@Override
			public void evaluate() throws Throwable {
				// tarts a new jEdit instance
				TestUtils.setupNewjEdit();
				TestUtils.tearDownExistingjEdit();
				// before running the tests
				s.evaluate();
			}
		};
	}
	
	@Override
	protected Statement withAfterClasses(Statement statement) {
		final Statement s = super.withAfterClasses(statement);
		return new Statement() {
			
			@Override
			public void evaluate() throws Throwable {
				// run tests
				s.evaluate();
				// and then quit the jEdit instance
				TestUtils.tearDownNewjEdit();
			}
		};
	}
}
