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
package com.townsfolkdesigns.swixml.form;

import com.townsfolkdesigns.swixml.form.SimpleForm;

/**
 * @author elberry
 * 
 */
public class TestForm extends SimpleForm {

	protected int birthDay;
	protected int birthMonth;
	protected int birthYear;
	protected String firstName;
	protected String lastName;
	protected boolean newsWanted;
	protected String phoneNumber;

	public TestForm() {
		super("/forms/TestForm.xml");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.townsfolkdesigns.common.ui.FormController#cancel()
	 */
	public void cancel() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.townsfolkdesigns.common.ui.FormController#init()
	 */
	public void init() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.townsfolkdesigns.common.ui.FormController#save()
	 */
	public void save() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void cancelAction() {
		super.cancelAction();
	}

	@Override
	protected void submitAction() {
		super.submitAction();
	}

}
