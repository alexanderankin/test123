/*
 * paramSM.java - returned values for CF.SM
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2012, Artem Bryantsev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
 
package jcfunc.parameters;

/**
   Enum <code>paramSM</code> contains returned values for control function CF.SM.
 */
public enum paramSM
{
	//{{{ enum's values
	/** GUARDED AREA TRANSFER MODE */
	GATM(1),
	
	/** KEYBOARD ACTION MODE */
	KAM(2),
	
	/** CONTROL REPRESENTATION MODE */
	CRM(3),
	
	/** INSERTION REPLACEMENT MODE */
	IRM(4),
	
	/** STATUS REPORT TRANSFER MODE */
	SRTM(5),
	
	/** ERASURE MODE */
	ERM(6),
	
	/** LINE EDITING MODE */
	VEM(7),
	
	/** BI-DIRECTIONAL SUPPORT MODE */
	BDSM(8),
	
	/** DEVICE COMPONENT SELECT MODE */
	DCSM(9),
	
	/** CHARACTER EDITING MODE */
	HEM(10),
	
	/** @deprecated POSITIONING UNIT MODE */
	PUM(11),
	
	/** SEND/RECEIVE MODE */
	SRM(12),
	
	/** FORMAT EFFECTOR ACTION MODE */
	FEAM(13),
	
	/** FORMAT EFFECTOR TRANSFER MODE */
	FETM(14),
	
	/** MULTIPLE AREA TRANSFER MODE */
	MATM(15),
	
	/** TRANSFER TERMINATION MODE */
	TTM(16),
	
	/** SELECTED AREA TRANSFER MODE */
	SATM(17),
	
	/** TABULATION STOP MODE */
	TSM(18),
	
	/** [eliminated] EDITING BOUNDARY MODE */
	EBM(19),
	
	/** [eliminated] LINE FEED/ NEW LINE MODE */
	LFNL(20),
	
	/** GRAPHIC RENDITION COMBINATION MODE */
	GRCM(21),
	
	/** @deprecated ZERO DEFAULT MODE */
	ZDM(22),

	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramSM(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
         */
	public static int getIntValue(paramSM val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramSM getEnumValue(int val)
	{
		for ( paramSM element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
