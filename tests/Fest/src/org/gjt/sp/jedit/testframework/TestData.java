/*
 * TestData.java
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

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * ensures test_data is passed as test parameter (env. property)
 * and returns it as a file.
 * 
 */
public class TestData extends ExternalResource {

	private File testData;
	
	public File get(){
		return testData;
	}
	

	@Override
	protected void before() throws Throwable {
        testData = new File(System.getProperty("test_data")).getCanonicalFile();
        assertTrue(testData.exists());
	}
}
