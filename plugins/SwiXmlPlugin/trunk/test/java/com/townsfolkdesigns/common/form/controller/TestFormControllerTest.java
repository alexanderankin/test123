/*
 * Copyright (c) 2008 Eric Berry <elberry@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/**
 * 
 */
package com.townsfolkdesigns.common.form.controller;

import javax.swing.JTextField;

import junit.framework.TestCase;

import org.swixml.SwingEngine;

/**
 * @author elberry
 *
 */
public class TestFormControllerTest extends TestCase {
	
	public void testBackingObjectCreation() {
		TestFormController controller = new TestFormController();
		controller.createBackingObject();
		TestForm testForm = controller.getBackingObject();
		assertNotNull(testForm);
		assertEquals(0, testForm.getBirthDay());
		assertEquals(0, testForm.getBirthMonth());
		assertEquals(0, testForm.getBirthYear());
	}
	
	public void testBackingObjectBinding() {
		TestFormController controller = new TestFormController();
		SwingEngine swingEngine = controller.getSwingEngine();
		((JTextField)swingEngine.find("firstName")).setText("Eric");
		((JTextField)swingEngine.find("lastName")).setText("Cartman");
		((JTextField)swingEngine.find("phoneNumber")).setText("555-123-4567");
		((JTextField)swingEngine.find("birthDay")).setText("4");
		((JTextField)swingEngine.find("birthMonth")).setText("2");
		((JTextField)swingEngine.find("birthYear")).setText("1997");
		controller.getSubmitAction().actionPerformed(null);
	}

}
