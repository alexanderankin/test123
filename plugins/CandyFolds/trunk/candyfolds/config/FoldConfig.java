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
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.text.Segment;
import org.gjt.sp.util.Log;

public final class FoldConfig {
	public static final Color DEFAULT_COLOR=new Color(0, 0, 128, 86); // WARNING: using transparency slows drawing (noticeable on my computer 2ghz pentium + radeon 9700...)
	private String name="";
	private Color color=DEFAULT_COLOR;
	private String regex;
	private Pattern pattern;
	private Matcher matcher;

	FoldConfig() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(name==null)
			name="";
		this.name=name.trim();
	}

	boolean matches(Segment segment) {
		if(matcher==null) {
			if(pattern==null)
				return true;
			else {
				matcher=pattern.matcher(segment);
				//Log.log(Log.NOTICE, this, "matcher setted");
			}
		} else
			matcher.reset();
		return matcher.matches();
	}

	public void setRegex(String regex)
	throws PatternSyntaxException {
		if(regex!=null) {
			regex=regex.trim();
			this.pattern=Pattern.compile(regex);
		} else
			this.pattern=null;
		this.regex=regex;
		matcher=null;
	}

	public String getRegex() {
		return regex;
	}

	public void setColor(Color color) {
		if(color==null)
			throw new NullPointerException();
		this.color=color;
	}

	public Color getColor() {
		return color;
	}

	boolean load(Properties ps, ModeConfig mc, StringBuilder sb, int i) {
		String colorPropName= mc.getPropertyNameB(sb, String.valueOf(i)).
		    append(".color").toString();
		String regexPropName=mc.getPropertyNameB(sb, String.valueOf(i)).
		    append(".regex").toString();
		String namePropName=mc.getPropertyNameB(sb, String.valueOf(i)).
		    append(".name").toString();
		return load(ps, colorPropName, regexPropName, namePropName);
	}

	boolean load(Properties ps,
	             String colorPropName,
	             String regexPropName,
	             String namePropName) {
		String colorS=ps.getProperty(colorPropName);
		if(colorS==null)
			return false;
		Color color=decodeColor(colorS);
		if(color==null)
			return false;
		setColor(color);
		setRegex(ps.getProperty(regexPropName));
		setName(ps.getProperty(namePropName));
		return true;
	}

	void store(Properties ps, ModeConfig mc, StringBuilder sb, int i) {
		store(ps, mc.getPropertyNameB(sb, String.valueOf(i)).
		      append(".color").toString(),
		      mc.getPropertyNameB(sb, String.valueOf(i)).
		      append(".regex").toString(),
		      mc.getPropertyNameB(sb, String.valueOf(i)).
		      append(".name").toString()
		     );
	}

	void store(Properties ps,
	           String colorPropName,
	           String regexPropName,
	           String namePropName) {
		ps.setProperty(colorPropName, encodeColor(new StringBuilder(), color));
		if(regex!=null)
			ps.setProperty(regexPropName, regex);
		ps.setProperty(namePropName, name);
	}

	static String encodeColor(StringBuilder sb, Color color) {
		sb.setLength(0);
		sb.append(color.getRed());
		sb.append(",");
		sb.append(color.getGreen());
		sb.append(",");
		sb.append(color.getBlue());
		if(color.getAlpha()!=255) {
			sb.append(",");
			sb.append(color.getAlpha());
		}
		return sb.toString();
	}

	static Color decodeColor(String colorS) {
		String[] colorComps=colorS.split(",");
		int[] colorCompVals=new int[colorComps.length];
		for(int i=colorComps.length; --i>=0;) {
			colorCompVals[i]=Integer.valueOf(colorComps[i].trim());
			if(colorCompVals[i]<0 || colorCompVals[i]>255)
				return null;
		}
		if(colorCompVals.length<3)
			return null;
		if(colorCompVals.length==3)
			return new Color(colorCompVals[0], colorCompVals[1], colorCompVals[2]);
		else
			return new Color(colorCompVals[0], colorCompVals[1], colorCompVals[2], colorCompVals[3]);
	}

}
