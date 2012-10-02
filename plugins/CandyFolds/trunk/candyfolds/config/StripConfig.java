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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Stroke;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;

import javax.swing.text.Segment;

import org.gjt.sp.util.Log;

public final class StripConfig {
	static final Logger L=Logger.getLogger(StripConfig.class.getName());
	static { L.setLevel(Level.ALL); }

	public static final Color DEFAULT_COLOR=new Color(181, 181, 181, 255); // WARNING: using transparency slows drawing (noticeable on my computer 2ghz pentium + radeon 9700...)
	private String name="";
	private Color color=DEFAULT_COLOR;
	public final Regex regex=new Regex();
	/*
	private float strokeWidthFactor=1;

	public final FontMetricsInfo fontMetricsInfo=new FontMetricsInfo();

	public final class FontMetricsInfo{
		private FontMetrics fontMetrics;
		private Stroke stroke;

		public void reset(){
			fontMetrics=null;
		}

		public Stroke getStroke(FontMetrics fontMetrics){
			if(this.fontMetrics == fontMetrics)
				return stroke;
			this.fontMetrics = fontMetrics;
			setupStroke();
			return stroke;
		}

		private void setupStroke(){
			if(strokeWidthFactor==0){
				stroke=null;
				return;
			}
			int spaceWidth = fontMetrics.charWidth(' ');
			float strokeWidth=spaceWidth/2.3f * strokeWidthFactor;
			stroke=new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
		}
	}
	*/

	StripConfig() {
	}

	public String getName(){
		return name;
	}

	public void setName(String name) {
		if(name==null)
			name="";
		this.name=name.trim();
	}

	public void setColor(Color color) {
		if(color==null)
			throw new NullPointerException();
		this.color=color;
	}

	public Color getColor() {
		return color;
	}

	/*
	public void setStrokeWidthFactor(float strokeWidthFactor){
		if(strokeWidthFactor<0)
			strokeWidthFactor=0;
		this.strokeWidthFactor=strokeWidthFactor;
		fontMetricsInfo.reset();
	}

	public float getStrokeWidthFactor() {
		return strokeWidthFactor;
	}
	*/

	boolean load(Properties ps, ModeConfig mc, StringBuilder sb, int i) {
		String iS=String.valueOf(i);
		return load(ps, mc.getPropertyNameB(sb, iS).
						append(".color").toString(),
						mc.getPropertyNameB(sb, iS).
						append(".regex").toString(),
						mc.getPropertyNameB(sb, iS).
						append(".name").toString()/*,
						mc.getPropertyNameB(sb, iS).
						append(".strokeWidthFactor").toString()*/
					  );
	}

	boolean load(Properties ps,
					 String colorPropName,
					 String regexPropName,
					 String namePropName/*,
					 String strokeWidthFactorPropName*/) {
		String colorS=ps.getProperty(colorPropName);
		if(colorS==null)
			return false;
		Color color=decodeColor(colorS);
		if(color==null)
			return false;
		setColor(color);
		setName(ps.getProperty(namePropName));
		regex.setValue(ps.getProperty(regexPropName));
		//setStrokeWidthFactor(decodeStrokeWidthFactor(ps.getProperty(strokeWidthFactorPropName)));
		return true;
	}

	void store(Properties ps, ModeConfig mc, StringBuilder sb, int i) {
		String iS=String.valueOf(i);
		store(ps,
				mc.getPropertyNameB(sb, iS).
				append(".color").toString(),
				mc.getPropertyNameB(sb, iS).
				append(".regex").toString(),
				mc.getPropertyNameB(sb, iS).
				append(".name").toString()/*,
				mc.getPropertyNameB(sb, iS).
				append(".strokeWidthFactor").toString()*/
			  );
	}

	void store(Properties ps,
				  String colorPropName,
				  String regexPropName,
				  String namePropName/*,
				  String strokeWidthFactorPropName*/) {
		ps.setProperty(colorPropName, encodeColor(new StringBuilder(), color));
		String regexValue=regex.getValue();
		if(regexValue!=null)
			ps.setProperty(regexPropName, regexValue);
		ps.setProperty(namePropName, name);
		//ps.setProperty(strokeWidthFactorPropName, Float.valueOf(strokeWidthFactor).toString());
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

	/*
	static String encodeStrokeWidthFactor(float strokeWidthFactor){
		if(strokeWidthFactor<0)
			return "0";
		else
			return Float.toString(strokeWidthFactor);
	}

	static float decodeStrokeWidthFactor(String strokeWidthFactorS){
		if(strokeWidthFactorS==null)
			return 1;
		try{
			return Float.valueOf(strokeWidthFactorS);
		}catch(NumberFormatException ex){
			return 1;
		}
	}
	*/

}