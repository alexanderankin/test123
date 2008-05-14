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
package com.townsfolkdesigns.lucene.jedit.manager;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gjt.sp.util.Log;

import com.townsfolkdesigns.lucene.jedit.LucenePlugin;

public class OptionsManager {

	private transient static OptionsManager instance = new OptionsManager();

	private boolean automaticallyIndexProjects;
	private List<String> directories;
	private String fileNameRegex;
	private long indexInterval;
	private transient File settingsFile;

	private OptionsManager() {
		setDefaults();
		// try to load from storage.
		load();
	}

	public static OptionsManager getInstance() {
		return instance;
	}

	public synchronized void clear() {
		setDefaults();
		save();
	}

	public synchronized List<String> getDirectories() {
		return directories;
	}

	public synchronized String getFileNameRegex() {
		return fileNameRegex;
	}

	public synchronized long getIndexInterval() {
		return indexInterval;
	}

	/**
	 * @return the settingsFile
	 */
	public synchronized File getSettingsFile() {
		return settingsFile;
	}

	public synchronized boolean isAutomaticallyIndexProjects() {
		return automaticallyIndexProjects;
	}

	public synchronized void load() {
		File settingsFile = getSettingsFile();
		Map<String, Object> settingsMap = new HashMap<String, Object>();
		FileInputStream fis = null;
		XMLDecoder decoder = null;
		try {
			fis = new FileInputStream(settingsFile);
			decoder = new XMLDecoder(fis);
			settingsMap = (Map<String, Object>) decoder.readObject();
		} catch (Exception e) {
			Log.log(Log.ERROR, this, "Error closing ObjectOutputStream", e);
		} finally {
			if (decoder != null) {
				try {
					decoder.close();
				} catch (Exception e) {
					Log.log(Log.ERROR, this, "Error closing XMLEncoder", e);
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					Log.log(Log.ERROR, this, "Error closing ObjectOutputStream", e);
				}
			}
		}
		Field field = null;
		for (String fieldName : settingsMap.keySet()) {
			try {
				field = getClass().getDeclaredField(fieldName);
				if (field != null) {
					field.set(this, settingsMap.get(fieldName));
				} else {
					Log.log(Log.WARNING, this, "The given field \"" + fieldName
					      + "\" was not found, are you loading settings from a new version?");
				}
			} catch (Exception e) {
				Log.log(Log.ERROR, this, "Error loading value for field: " + field.getName(), e);
			}
		}
	}

	public synchronized void save() {
		File settingsFile = getSettingsFile();
		Map<String, Object> settingsMap = new HashMap<String, Object>();
		Field[] fields = getClass().getDeclaredFields();
		for (Field field : fields) {
			if (!Modifier.isTransient(field.getModifiers())) {
				try {
					settingsMap.put(field.getName(), field.get(this));
				} catch (Exception e) {
					Log.log(Log.ERROR, this, "Error getting value for field: " + field.getName(), e);
				}
			}
		}
		FileOutputStream fos = null;
		XMLEncoder encoder = null;
		try {
			fos = new FileOutputStream(settingsFile, false);
			encoder = new XMLEncoder(fos);
			encoder.writeObject(settingsMap);
			encoder.flush();
		} catch (Exception e) {
			Log.log(Log.ERROR, this, "Error storing serialized settings to file: " + settingsFile.getPath(), e);
		} finally {
			if (encoder != null) {
				try {
					encoder.close();
				} catch (Exception e) {
					Log.log(Log.ERROR, this, "Error closing XMLEncoder", e);
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					Log.log(Log.ERROR, this, "Error closing ObjectOutputStream", e);
				}
			}
		}
	}

	public synchronized void setAutomaticallyIndexProjects(boolean automaticallyIndexProjects) {
		this.automaticallyIndexProjects = automaticallyIndexProjects;
	}

	public synchronized void setDirectories(List<String> directories) {
		this.directories = directories;
	}

	public synchronized void setFileNameRegex(String fileNameRegex) {
		this.fileNameRegex = fileNameRegex;
	}

	public synchronized void setIndexInterval(long indexInterval) {
		this.indexInterval = indexInterval;
	}

	private void setDefaults() {
		// default values.
		setAutomaticallyIndexProjects(true);
		setDirectories(new LinkedList<String>());
		setFileNameRegex(".*");
		setIndexInterval(5 * 60 * 1000); // 5 minutes.
		File pluginHome = new LucenePlugin().getPluginHome();
		File settingsFile = new File(pluginHome, "settings.xml");
		setSettingsFile(settingsFile);
	}

	void setSettingsFile(File settingsFile) {
		this.settingsFile = settingsFile;
	}
}
