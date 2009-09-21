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

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.swing.text.Segment;

public final class ModeConfig {
	public static final int MAX_FOLD_CONFIGS=20;
	public final Config config;
	public final String name;
	private final List<FoldConfig> foldConfigs=new ArrayList<FoldConfig>();
	public final List<FoldConfig> foldConfigsA=Collections.unmodifiableList(foldConfigs);
	private boolean enabled=true;

	ModeConfig(Config config, String name) {
		this.config=config;
		this.name=name;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		if(config.defaultModeConfig==this)
			return;
		this.enabled=enabled;
	}

	public FoldConfig addFoldConfig() {
		if(config.defaultModeConfig==this && !foldConfigs.isEmpty())
			return null;
		FoldConfig foldConfig=new FoldConfig();
		foldConfigs.add(foldConfig);
		return foldConfig;
	}

	public void removeFoldConfig(int index) {
		if(config.defaultModeConfig==this && foldConfigs.size()==1)
			return;
		foldConfigs.remove(index);
	}

	public boolean moveUp(int foldConfigIndex) {
		if(foldConfigIndex>0 && foldConfigIndex<foldConfigs.size()) {
			FoldConfig toMove=foldConfigs.remove(foldConfigIndex);
			foldConfigs.add(foldConfigIndex-1, toMove);
			return true;
		}
		return false;
	}

	public boolean moveDown(int foldConfigIndex) {
		if(foldConfigIndex>=0 && foldConfigIndex<foldConfigs.size()-1) {
			FoldConfig toMove=foldConfigs.remove(foldConfigIndex);
			foldConfigs.add(foldConfigIndex+1, toMove);
			return true;
		}
		return false;
	}

	public FoldConfig evalFoldConfig(Segment segment) {
		for(FoldConfig foldConfig: foldConfigs) {
			if(foldConfig.matches(segment))
				return foldConfig;
		}
		return config.defaultModeConfig.foldConfigs.get(0);
	}

	void store(Properties ps) {
		StringBuilder sb=new StringBuilder();
		ps.clear();
		ps.setProperty(getPropertyName(sb, "enabled"), String.valueOf(enabled));
		for(int i=0, size=foldConfigs.size(); i<size && i<MAX_FOLD_CONFIGS; i++) {
			foldConfigs.get(i).store(ps, this, sb, i);
		}
	}

	private String getPropertyName(StringBuilder sb, String firstName) {
		return getPropertyNameB(sb, firstName).toString();
	}

	StringBuilder getPropertyNameB(StringBuilder sb, String firstName) {
		return getPropertyPrefix(sb).append(firstName);
	}

	private StringBuilder getPropertyPrefix(StringBuilder sb) {
		sb.setLength(0);
		sb.append(name);
		sb.append(".");
		return sb;
	}

	void load(Properties ps) {
		try {
			StringBuilder sb=new StringBuilder();
			if(this!=config.defaultModeConfig)
				setEnabled(Boolean.valueOf(
				             ps.getProperty(getPropertyName(sb, "enabled"))));
			foldConfigs.clear();
			for(int i=0; i<MAX_FOLD_CONFIGS; i++) {
				FoldConfig foldConfig=new FoldConfig();
				if(foldConfig.load(ps, this, sb, i)) {
					foldConfigs.add(foldConfig);
				} else
					break;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
