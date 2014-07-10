/*
 * CF.java - definitions of all control functions
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
 
package jcfunc;

//{{{ CF enum
/**
   Definitions (acronyms) of all control functions.
   
   see <a href="http://www.ecma-international.org/publications/standards/Ecma-048.htm">
   "Standart ECMA-48 Control functions for coded characters sets"</a> 
 */
public enum CF {
	/** Acknowledge */
	ACK,
	
	/** Application program command */
	APC,
	
	/** Bell */
	BEL,
	
	/**  Break permitted here */
	BPH,
	
	/** Backspace */
	BS,
	
	/** Cansel */
	CAN,
	
	/** Cursor backward tabulation */
	CBT,
	
	/** Cancel character */
	CCH,
	
	/** Cursor character absolute */
	CHA,
	
	/** Cursor forward tabulation */
	CHT,
	
	/** Coding method delimiter */
	CMD,
	
	/** Cursor next line */
	CNL,
	
	/** Cursor preceding line */
	CPL,
	
	/** Active position report */
	CPR,
	
	/** Carriage return */
	CR,
	
	/** Control sequence introducer */
	CSI,
	
	/** Cursor tabulation control */
	CTC,
	
	/** Cursor left */
	CUB,
	
	/** Cursor down */
	CUD,
	
	/** Cursor right */
	CUF,
	
	/** Cursor position */
	CUP,
	
	/** Cursor up */
	CUU,
	
	/** Cursor line tabulation */
	CVT,
	
	/** Device attributes */
	DA ,
	
	/** Define area qualification */
	DAQ,
	
	/** Delete character */
	DCH,
	
	/** Device control string */
	DCS,
	
	/** Device control one */
	DC1,
	
	/** Device control two */
	DC2,
	
	/** Device control three */
	DC3,
	
	/** Device control four */
	DC4,
	
	/** Delete line */
	DL ,
	
	/** Data link escape */
	DLE,
	
	/** Disable manual input */
	DMI,
	
	/** Device status report */
	DSR,
	
	/** Dimension text area */
	DTA,
	
	/** Erase in area */
	EA ,
	
	/** Erase character */
	ECH,
	
	/** Erase in page */
	ED ,
	
	/** Erase in field */
	EF ,
	
	/** Erase in line */
	EL ,
	
	/** End of medium */
	EM ,
	
	/** Enable manual input */
	EMI,
	
	/** Enquiry */
	ENQ,
	
	/** End of transmission */
	EOT,
	
	/** End of guarded area */
	EPA,
	
	/** End of selected area */
	ESA,
	
	/** Escape */
	ESC,
	
	/** End of transmission block */
	ETB,
	
	/** End of text */
	ETX,
	
	/** Form feed */
	FF ,
	
	/** Function key */
	FNK,
	
	/** Font selection */
	FNT,
	
	/** Graphic character combination */
	GCC,
	
	/** Graphic size modification */
	GSM,
	
	/** Graphic size selection */
	GSS,
	
	/** Character position absolute */
	HPA,
	
	/** Character position backward */
	HPB,
	
	/** Character position forward */
	HPR,
	
	/** Character tabulation */
	HT ,
	
	/** Character tabulation with justification */
	HTJ,
	
	/** Character tabulation set */
	HTS,
	
	/** Character and line position */
	HVP,
	
	/** Insert character */
	ICH,
	
	/** Identify device control string */
	IDCS,
	
	/** Identify graphic subrepertoire */
	IGS,
	
	/** Insert line */
	IL ,
	
	/** Interrupt */
	INT,
	
	/** Information separator one (us - unit separator) */
	IS1,
	
	/** Information separator two (rs - record separator) */
	IS2,
	
	/** Information separator three (gs - group separator) */
	IS3,
	
	/** Information separator four (fs - file separator) */
	IS4,
	
	/** Justify */
	JFY,
	
	/** Line feed */
	LF ,
	
	/** Locking-shift zero */
	LS0,
	
	/** Locking-shift one */
	LS1,
	
	/** Locking-shift one right */
	LS1R,
	
	/** Locking-shift two */
	LS2,
	
	/** Locking-shift two right */
	LS2R,
	
	/** Locking-shift three */
	LS3,
	
	/** Locking-shift three right */
	LS3R,
	
	/** Media copy */
	MC ,
	
	/** Message waiting */
	MW ,
	
	/** Negative acknowledge */
	NAK,
	
	/** No break here */
	NBH,
	
	/** Next line */
	NEL,
	
	/** Next page */
	NP ,
	
	/** Null */
	NUL,
	
	/** Operating system command */
	OSC,
	
	/** Presentation expand or contract */
	PEC,
	
	/** Page format selection */
	PFS,
	
	/** Partial line forward */
	PLD,
	
	/** Partial line backward */
	PLU,
	
	/** Privacy message */
	PM ,
	
	/** Preceding page */
	PP ,
	
	/** Page position absolute */
	PPA,
	
	/** Page position backward */
	PPB,
	
	/** Page position forward */
	PPR,
	
	/** Parallel texts */
	PTX,
	
	/** Private use one */
	PU1,
	
	/** Private use two */
	PU2,
	
	/** Quad */
	QUAD,
	
	/** Repeat */
	REP,
	
	/** Reverse line feed */
	RI ,
	
	/** Reset to initial state */
	RIS,
	
	/** Reset mode */
	RM ,
	
	/** Set additional character separation */
	SACS,
	
	/** Select alternative presentation variants */
	SAPV,
	
	/** Single character introducer */
	SCI,
	
	/** Select character orientation */
	SCO,
	
	/** Select character path */
	SCP,
	
	/** Set character spacing */
	SCS,
	
	/** Scroll down */
	SD ,
	
	/** Start directed string */
	SDS,
	
	/** Select editing extent */
	SEE,
	
	/** Sheet eject and feed */
	SEF,
	
	/** Select graphic rendition */
	SGR,
	
	/** Select character spacing */
	SHS,
	
	/** Shift-in */
	SI ,
	
	/** Select implicit movement direction */
	SIMD,
	
	/** Scroll left */
	SL ,
	
	/** Set line home */
	SLH,
	
	/** Set line limit */
	SLL,
	
	/** Set line spacing */
	SLS,
	
	/** Set mode */
	SM ,
	
	/** Shift-out */
	SO ,
	
	/** Start of heading */
	SOH,
	
	/** Start of string */
	SOS,
	
	/** Start of guarded area */
	SPA,
	
	/** Select presentation directions */
	SPD,
	
	/** Set page home */
	SPH,
	
	/** Spacing increment */
	SPI,
	
	/** Set page limit */
	SPL,
	
	/** Select print quality and rapidity */
	SPQR,
	
	/** Scroll right */
	SR ,
	
	/** Set reduced character separation */
	SRCS,
	
	/** Start reversed string */
	SRS,
	
	/** Start of selected area */
	SSA,
	
	/** Select size unit */
	SSU,
	
	/** Set space width */
	SSW,
	
	/** Single-shift two */
	SS2,
	
	/** Single-shift three */
	SS3,
	
	/** String terminator */
	ST ,
	
	/** Selective tabulation */
	STAB,
	
	/** Set transmit state */
	STS,
	
	/** Start of text */
	STX,
	
	/** Scroll up */
	SU ,
	
	/** Substitute */
	SUB,
	
	/** Select line spacing */
	SVS,
	
	/** Synchronous idle */
	SYN,
	
	/** Tabulation aligned centred */
	TAC,
	
	/** Tabulation aligned leading edge */
	TALE,
	
	/** Tabulation aligned trailing edge */
	TATE,
	
	/** Tabulation clear */
	TBC,
	
	/** Tabulation centred on character */
	TCC,
	
	/** Tabulation stop remove */
	TSR,
	
	/** Thin space specification */
	TSS,
	
	/** Line position absolute */
	VPA,
	
	/** Line position backward */
	VPB,
	
	/** Line position forward */
	VPR,
	
	/** Line tabulation */
	VT ,
	
	/** Line tabulation set */
	VTS
}
//}}}
	

