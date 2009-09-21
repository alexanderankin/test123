/* % [{
% (C) Copyright 2008 Nicolas Carranza and individual contributors.
% See the CandyFolds-copyright.txt file in the CandyFolds distribution for a full
% listing of individual contributors.
%
% This file is part of CandyFolds.
%
% CandyFolds is free software: you can redistribute it and/or modify
% it under the terms of the GNU General Public License as published by
% the Free Software Foundation, either version 3 of the License,
% or (at your option) any later version.
%
% CandyFolds is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
% GNU General Public License for more details.
%
% You should have received a copy of the GNU General Public License
% along with CandyFolds.  If not, see <http://www.gnu.org/licenses/>.
% }] */
package candyfolds.config;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

public final class Config {
	public static final String DEFAULT_MODE_CONFIG_NAME="DEFAULT";
	public final ModeConfig defaultModeConfig=new ModeConfig(this, DEFAULT_MODE_CONFIG_NAME);
	private final Map<String, ModeConfig> modeNameToConfig=new HashMap<String, ModeConfig>();

	public Config() {
		load(defaultModeConfig);
		if(defaultModeConfig.foldConfigsA.isEmpty()) {
			FoldConfig defFoldConfig=defaultModeConfig.addFoldConfig();
			defFoldConfig.setName("all");
			store();
		}
	}

	public boolean usesDefaultModeConfig(String modeName) {
		if(modeName.equals(DEFAULT_MODE_CONFIG_NAME))
			return false;
		ModeConfig modeConfig=getModeConfig(modeName);
		if(modeConfig==defaultModeConfig)
			return true;
		return false;
	}
	
	public void setModeConfigToDefault(String modeName) {
		modeNameToConfig.put(modeName, defaultModeConfig);
	}

	public ModeConfig setModeConfigToNew(String modeName) {
		ModeConfig modeConfig=new ModeConfig(this, modeName);
		modeNameToConfig.put(modeName, modeConfig);
		return modeConfig;
	}

	public ModeConfig getModeConfig(String modeName) {
		ModeConfig modeConfig=modeNameToConfig.get(modeName);
		if(modeConfig==null) {
			if(modeName.equals(DEFAULT_MODE_CONFIG_NAME))
				modeConfig=defaultModeConfig;
			else {
				modeConfig=new ModeConfig(this, modeName);
				if(!load(modeConfig))
					modeConfig=defaultModeConfig;
			}
			modeNameToConfig.put(modeName, modeConfig);
		}
		return modeConfig;
	}
	
	private boolean load(ModeConfig modeConfig) {
		File file=PluginHome.getModeConfigFile(modeConfig.name);
		if(!file.exists())
			return false;
		Properties ps=new Properties();
		try {
			Reader r=new BufferedReader(new FileReader(file));
			ps.load(r);
			r.close();
			modeConfig.load(ps);
			return true;
		} catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public void store() {
		Properties ps=new Properties() { // this subclass stores the properties sorted
			              private final TreeSet<Object> sorter=new TreeSet<Object>();
			              @Override
			              public Enumeration<Object> keys() {
				              sorter.clear();
				              sorter.addAll(keySet());
				              return Collections.enumeration(sorter);
			              }
		              };
		for(Map.Entry<String, ModeConfig> entry: modeNameToConfig.entrySet())
			store(ps, entry.getKey(), entry.getValue());
		store(ps, DEFAULT_MODE_CONFIG_NAME, defaultModeConfig);
	}

	private void store(Properties ps,String modeName, ModeConfig modeConfig) {
		ps.clear();
		File file=PluginHome.getModeConfigFile(modeName);
		if(file.exists()) {
			//Log.log(Log.NOTICE, this, "Deleting file: "+file);
			file.delete();
		}
		if(modeConfig==defaultModeConfig && modeConfig.name!=modeName)
			return;
		try {
			//Log.log(Log.NOTICE, this, "Creating file: "+file);
			file.createNewFile();
			modeConfig.store(ps);
			Writer w=new BufferedWriter(new FileWriter(file));
			ps.store(w, null);
			w.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
