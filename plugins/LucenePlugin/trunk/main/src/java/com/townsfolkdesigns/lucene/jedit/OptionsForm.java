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
package com.townsfolkdesigns.lucene.jedit;

import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JList;

import org.apache.commons.lang.StringUtils;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.browser.VFSFileChooserDialog;
import org.gjt.sp.util.Log;

import com.townsfolkdesigns.lucene.jedit.manager.OptionsManager;
import com.townsfolkdesigns.swixml.form.SimpleForm;

/**
 * This class acts like a form binder. It doesn't actually store anything, but
 * using the setters/getters sets and gets the values from or in the gui.
 * 
 * @author elberry
 * 
 */
public class OptionsForm extends SimpleForm {

	private DirectoryListModel directoryListModel = new DirectoryListModel();
	private OptionsManager optionsManager;

	public OptionsForm() {
		super("/luceneplugin/OptionsPane.xml");
		JList directoryList = (JList) getIdMap().get("directoryList");
		directoryList.setModel(directoryListModel);
		setOptionsManager(OptionsManager.getInstance());
	}

	public void addDirectory() {
		View view = jEdit.getActiveView();
		Buffer currentBuffer = view.getBuffer();
		VFSFileChooserDialog fileChooser = new VFSFileChooserDialog(view, currentBuffer.getDirectory(),
		      VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false);
		String[] selectedFiles = fileChooser.getSelectedFiles();
		if (selectedFiles != null) {
			for (String filePath : selectedFiles) {
				getDirectories().add(filePath);
			}
		}
		setDirectories(getDirectories());
	}

	public void cancel() {
		init();
	}

	public List<String> getDirectories() {
		return directoryListModel.getDirectoryList();
	}

	public String getFileNameRegex() {
		String fieldValue = getTextFieldValue("fileNameRegex");
		return fieldValue;
	}

	public long getIndexInterval() {
		String fieldValue = getTextFieldValue("indexInterval");
		long indexInterval = 0;
		if (StringUtils.isNotBlank(fieldValue)) {
			try {
				indexInterval = Long.valueOf(fieldValue);
			} catch (Exception e) {
				Log.log(Log.ERROR, this, "Index Inverval value is not a number.", e);
			}
		}
		return indexInterval;
	}

	/**
	 * @return the optionsManager
	 */
	public OptionsManager getOptionsManager() {
		return optionsManager;
	}

	public void init() {
		optionsManager.load();
		setDirectories(optionsManager.getDirectories());
		setAutomaticallyIndexProjectFiles(optionsManager.isAutomaticallyIndexProjects());
		setFileNameRegex(optionsManager.getFileNameRegex());
		setIndexInterval(optionsManager.getIndexInterval());
	}

	public boolean isAutomaticallyIndexProjectFiles() {
		return getCheckBoxValue("indexProjects");
	}

	public void removeDirectory() {
		JList directoryList = (JList) getIdMap().get("directoryList");
		Object[] selectedDirectories = directoryList.getSelectedValues();
		List<String> directories = getDirectories();
		for (Object selectedDirectory : selectedDirectories) {
			directories.remove(selectedDirectory.toString());
		}
		setDirectories(directories);
	}

	public void save() {
		optionsManager.setAutomaticallyIndexProjects(isAutomaticallyIndexProjectFiles());
		optionsManager.setDirectories(getDirectories());
		optionsManager.setFileNameRegex(getFileNameRegex());
		optionsManager.setIndexInterval(getIndexInterval());
		optionsManager.save();
	}

	public void setAutomaticallyIndexProjectFiles(boolean automaticallyIndexProjectFiles) {
		setCheckBoxValue("indexProjects", automaticallyIndexProjectFiles);
	}

	public void setDirectories(List<String> directories) {
		directoryListModel.setDirectoryList(directories);
	}

	public void setFileNameRegex(String fileNameRegex) {
		setTextFieldValue("fileNameRegex", fileNameRegex);
	}

	public void setIndexInterval(long indexInterval) {
		setTextFieldValue("indexInterval", String.valueOf(indexInterval));
	}

	/**
	 * @param optionsManager
	 *           the optionsManager to set
	 */
	public void setOptionsManager(OptionsManager optionsManager) {
		this.optionsManager = optionsManager;
	}

	private class DirectoryListModel extends AbstractListModel {

		private static final long serialVersionUID = -6720547100306422626L;

		private List<String> directoryList;

		private DirectoryListModel() {
			setDirectoryList(new LinkedList<String>());
		}

		public List<String> getDirectoryList() {
			return directoryList;
		}

		public Object getElementAt(int arg0) {
			return directoryList.get(arg0);
		}

		public int getSize() {
			return directoryList.size();
		}

		public void setDirectoryList(List<String> directoryList) {
			this.directoryList = directoryList;
			fireContentsChanged(this, 0, directoryList.size());
		}
	}
}
