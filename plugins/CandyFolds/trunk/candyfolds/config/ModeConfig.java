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

	public static final int MAX_STRIP_CONFIGS=25;
	public static final float DEFAULT_DRAW_THIN_STRIPES_FACTOR=0.6f;
	public final Config config;
	public final String name;
	private final List<StripConfig> stripConfigs=new ArrayList<StripConfig>();
	public final List<StripConfig> stripConfigsA=Collections.unmodifiableList(stripConfigs);
	private boolean enabled=true;
	private boolean useIgnoreLineRegex;
	public final Regex ignoreLineRegex=new Regex();
	private boolean showStripOn0Indent;
	private boolean drawThinStripes;
	private float drawThinStripesFactor=DEFAULT_DRAW_THIN_STRIPES_FACTOR;
	private boolean useThinStripesPixelSize=false;

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

	public boolean getUseIgnoreLineRegex() {
		return useIgnoreLineRegex;
	}

	public void setUseIgnoreLineRegex(boolean useIgnoreLineRegex) {
		this.useIgnoreLineRegex=useIgnoreLineRegex;
	}

	public boolean getShowStripOn0Indent() {
		return showStripOn0Indent;
	}

	public void setShowStripOn0Indent(boolean showStripOn0Indent) {
		this.showStripOn0Indent=showStripOn0Indent;
	}

	public void setDrawThinStripes(boolean drawThinStripes) {
		this.drawThinStripes=drawThinStripes;
	}

	public boolean getDrawThinStripes() {
		return drawThinStripes;
	}

	public float getDrawThinStripesFactor() {
		return drawThinStripesFactor;
	}

	public void setDrawThinStripesFactor(float v) {
		this.drawThinStripesFactor=v;
	}

	public void setUseThinStripesPixelSize(boolean useThinStripesPixelSize) {
		this.useThinStripesPixelSize=useThinStripesPixelSize;
	}

	public boolean getUseThinStripesPixelSize() {
		return useThinStripesPixelSize;
	}

	public StripConfig addStripConfig() {
		if((config.defaultModeConfig==this && !stripConfigs.isEmpty())
				|| stripConfigs.size()==MAX_STRIP_CONFIGS)
			return null;
		StripConfig stripConfig=new StripConfig();
		stripConfigs.add(stripConfig);
		return stripConfig;
	}

	public void removeStripConfig(int index) {
		if(config.defaultModeConfig==this && stripConfigs.size()==1)
			return;
		stripConfigs.remove(index);
	}

	public boolean moveUp(int stripConfigIndex) {
		if(stripConfigIndex>0 && stripConfigIndex<stripConfigs.size()) {
			StripConfig toMove=stripConfigs.remove(stripConfigIndex);
			stripConfigs.add(stripConfigIndex-1, toMove);
			return true;
		}
		return false;
	}

	public boolean moveDown(int stripConfigIndex) {
		if(stripConfigIndex>=0 && stripConfigIndex<stripConfigs.size()-1) {
			StripConfig toMove=stripConfigs.remove(stripConfigIndex);
			stripConfigs.add(stripConfigIndex+1, toMove);
			return true;
		}
		return false;
	}

	public StripConfig evalStripConfig(Segment segment) {
		for(StripConfig stripConfig: stripConfigs) {
			if(stripConfig.regex.matches(segment))
				return stripConfig;
		}
		return config.defaultModeConfig.stripConfigs.get(0);
	}

	void store(Properties ps) {
		StringBuilder sb=new StringBuilder();
		ps.clear();
		ps.setProperty(getPropertyName(sb, "enabled"), String.valueOf(enabled));
		if(useIgnoreLineRegex) {
			String ignoreLineRegexValue=ignoreLineRegex.getValue();
			if(ignoreLineRegexValue!=null && ignoreLineRegexValue.length()>0)
				ps.setProperty(getPropertyName(sb, "ignoreLineRegex"), ignoreLineRegexValue);
		} else
			ps.remove(getPropertyName(sb, "ignoreLineRegex"));
		ps.setProperty(getPropertyName(sb, "showStripOn0Indent"), String.valueOf(showStripOn0Indent));
		ps.setProperty(getPropertyName(sb, "drawThinStripes"), String.valueOf(drawThinStripes));
		if(drawThinStripesFactor!=DEFAULT_DRAW_THIN_STRIPES_FACTOR)
			ps.setProperty(getPropertyName(sb, "drawThinStripesFactor"), String.valueOf(drawThinStripesFactor));
		else
			ps.remove(getPropertyName(sb, "drawThinStripesFactor"));
		ps.setProperty(getPropertyName(sb, "useThinStripesPixelSize"), String.valueOf(useThinStripesPixelSize));
		for(int i=0, size=stripConfigs.size(); i<size && i<MAX_STRIP_CONFIGS; i++) {
			stripConfigs.get(i).store(ps, this, sb, i);
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
							   ps.getProperty(getPropertyName(sb, "enabled"))
						   ));
			setShowStripOn0Indent(Boolean.valueOf(
									  ps.getProperty(getPropertyName(sb, "showStripOn0Indent"))
								  ));
			setDrawThinStripes(Boolean.valueOf(
								   ps.getProperty(getPropertyName(sb, "drawThinStripes"))
							   ));
			String drawThinStripesFactorValue=ps.getProperty(getPropertyName(sb, "drawThinStripesFactor"));
			if(drawThinStripesFactorValue!=null) {
				try {
					setDrawThinStripesFactor(Float.valueOf(
												 drawThinStripesFactorValue
											 ));
				}	catch(NumberFormatException ex) {}
			}
			setUseThinStripesPixelSize(Boolean.valueOf(
										   ps.getProperty(getPropertyName(sb, "useThinStripesPixelSize"))
									   ));
			//v support deprecated feature 'bigLines' with its ignoreLineRegexValue equivalent:
			String ignoreLineRegexValue=null;
			if(Boolean.valueOf(
						ps.getProperty(getPropertyName(sb, "bigLines"))))
				ignoreLineRegexValue="\\s*\\S\\s*$";
			//^
			if(ignoreLineRegexValue==null)
				ignoreLineRegexValue=ps.getProperty(getPropertyName(sb, "ignoreLineRegex"));
			if(ignoreLineRegexValue!=null) {
				useIgnoreLineRegex=true;
				ignoreLineRegex.setValue(ignoreLineRegexValue);
			}
			stripConfigs.clear();
			for(int i=0; i<MAX_STRIP_CONFIGS; i++) {
				StripConfig stripConfig=new StripConfig();
				if(stripConfig.load(ps, this, sb, i)) {
					stripConfigs.add(stripConfig);
				} else
					break;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}