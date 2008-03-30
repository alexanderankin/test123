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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

public class PluginHome {
	public static final String MODE_CONFIG_FILE_SUFIX="-CandyFolds.properties";
	private static File FILE;

	private static File getFile() {
		if(FILE!=null)
			return FILE;
		// dont use the jedit home settings api because is tied to the class name :(... Recommend to make EBPlugin.getPluginHome(String) public or use the plugin's name (not only class name)... 
		String jEditSettingsDir=jEdit.getSettingsDirectory();
		if(jEditSettingsDir==null)
			jEditSettingsDir=jEdit.getJEditHome();
		if(jEditSettingsDir==null) {
			Log.log(Log.ERROR, PluginHome.class, "Couldn't get plugin home directory; using home.");
			jEditSettingsDir=System.getProperty("user.home");
		}
		FILE=new File(jEditSettingsDir+File.separator+"plugins"+File.separator+"CandyFolds");
		if(!FILE.exists()){
			FILE.mkdirs();
			initializeDistribModeConfigFiles();
		}
		return FILE;
	}

	public static File getModeConfigFile(String modeConfigName) {
		return new File(getFile(), modeConfigName+MODE_CONFIG_FILE_SUFIX);
	}

	private static InputStream getDistribModeConfig(String modeConfigName) {
		return Config.class.getClassLoader().getResourceAsStream(
		         "CandyFolds-properties/"+
		         modeConfigName+MODE_CONFIG_FILE_SUFIX);
	}

	static final String[] distribModeConfigNames={"java"};

	private static void initializeDistribModeConfigFiles() {
		Log.log(Log.NOTICE, PluginHome.class, "Initializing distrib config files.");
		for(String modeConfigName: distribModeConfigNames) {
			File modeConfigFile=getModeConfigFile(modeConfigName);
			if(modeConfigFile.exists())
				continue;
			InputStream is=getDistribModeConfig(modeConfigName);
			if(is==null)
				throw new AssertionError("distribution configuration for mode not found: "+modeConfigName);
			try {
				is=new BufferedInputStream(is);
				modeConfigFile.createNewFile();
				OutputStream os=new BufferedOutputStream(new FileOutputStream(modeConfigFile));
				for(int r; (r=is.read())!=-1; )
					os.write(r);
				is.close();
				os.close();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
