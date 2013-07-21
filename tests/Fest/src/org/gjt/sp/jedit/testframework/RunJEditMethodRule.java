/*
 * RunJEditMethodRule.java
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

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * To be run between each test.
 * initialize the Fest robot before tests
 * does some cleanup after tests (close windows, close buffers)
 **/
public class RunJEditMethodRule implements MethodRule {

    public Statement apply(final Statement base, final FrameworkMethod method, Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
            	System.err.println("testing "+method.getName());
                TestUtils.setupExistingjEdit();

                try {
                    base.evaluate();
                } finally {
                	TestUtils.tearDownExistingjEdit();
                }
            }
        };
    }

}
