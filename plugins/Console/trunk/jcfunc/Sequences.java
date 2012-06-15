/*
 * Sequences.java - subsets of control functions, patterns and methods to work with them 
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

import java.util.Map;
import java.util.EnumMap;
import java.lang.String;
import java.lang.Cloneable;
import java.lang.StringBuilder;

/**
   Class <code>Sequencies</code> stores subsets of control functions, patterns
   and methods to work with them.
   see <a href="http://www.ecma-international.org/publications/standards/Ecma-048.htm">
   "Standart ECMA-48 Control functions for coded characters sets"</a> 
 */
public class Sequences
{
	//{{{ C0 enum
	/** Control function of C0-set */
	public enum C0 {
		/** \x00  */
		NUL,

		/** \x01  */
		SOH,

		/** \x02  */
		STX,

		/** \x03  */
		ETX,

		/** \x04  */
		EOT,

		/** \x05  */
		ENQ,

		/** \x06  */
		ACK,

		/** \x07  */
		BEL,

		/** \x08  */
		BS,

		/** \x09  */
		HT,

		/** \x0A  */
		LF,

		/** \x0B  */
		VT,

		/** \x0C  */
		FF,

		/** \x0D  */
		CR,

		/** \x0E  */
		LS1,

		/** \x0F  */
		LS0,

		/** \x10  */
		DLE,

		/** \x11  */
		DC1,

		/** \x12  */
		DC2,

		/** \x13  */
		DC3,

		/** \x14  */
		DC4,

		/** \x15  */
		NAK,

		/** \x16  */
		SYN,

		/** \x17  */
		ETB,

		/** \x18  */
		CAN,

		/** \x19  */
		EM,

		/** \x1A  */
		SUB,

		/** \x1B  */
		ESC,

		/** \x1C  */
		IS4,

		/** \x1D  */
		IS3,

		/** \x1E  */
		IS2,

		/** \x1F  */
		IS1
	}
	//}}}
	
	//{{{ C1 enum
	/** Control function of C1-set */
	public enum C1 {
		/** \x42 or \x82 */
		BPH,

		/** \x43 or \x83 */
		NBH,

		/** \x45 or \x85 */
		NEL,

		/** \x46 or \x86 */
		SSA,

		/** \x47 or \x87 */
		ESA,

		/** \x48 or \x88 */
		HTS,

		/** \x49 or \x89 */
		HTJ,

		/** \x4A or \x8A */
		VTS,

		/** \x4B or \x8B */
		PLD,

		/** \x4C or \x8C */
		PLU,

		/** \x4D or \x8D */
		RI,

		/** \x4E or \x8E */
		SS2,

		/** \x4F or \x8F */
		SS3,

		/** \x50 or \x90 */
		DCS,

		/** \x51 or \x91 */
		PU1,

		/** \x52 or \x92 */
		PU2,

		/** \x53 or \x93 */
		STS,

		/** \x54 or \x94 */
		CCH,

		/** \x55 or \x95 */
		MW,

		/** \x56 or \x96 */
		SPA,

		/** \x57 or \x97 */
		EPA,

		/** \x58 or \x98 */
		SOS,

		/** \x5A or \x9A */
		SCI,

		/** \x5B or \x9B */
		CSI,

		/** \x5C or \x9C */
		ST,

		/** \x5D or \x9D */
		OSC,

		/** \x5E or \x9E */
		PM,

		/** \x5F or \x9F */
		APC
	}
	//}}}
	
	//{{{ Sym enum
	/** Additional symbols */
	public enum Sym {
		/** ' ' */
		SPCE("\\x20"),
		
		/** '@' */
		  AT("\\x40"),
		
		/** 'A' */
		A_UP("\\x41"),
		
		/** 'B' */
		B_UP("\\x42"),
		
		/** 'C' */
		C_UP("\\x43"),
		
		/** 'D' */
		D_UP("\\x44"),
		
		/** 'E' */
		E_UP("\\x45"),
		
		/** 'F' */
		F_UP("\\x46"),
		
		/** 'G' */
		G_UP("\\x47"),
		
		/** 'H' */
		H_UP("\\x48"),
		
		/** 'I' */
		I_UP("\\x49"),
		
		/** 'J' */
		J_UP("\\x4A"),
		
		/** 'K' */
		K_UP("\\x4B"),
		
		/** 'L' */
		L_UP("\\x4C"),
		
		/** 'M' */
		M_UP("\\x4D"),
		
		/** 'N' */
		N_UP("\\x4E"),
		
		/** 'O' */
		O_UP("\\x4F"),
		
		/** 'P' */
		P_UP("\\x50"),
		
		/** 'Q' */
		Q_UP("\\x51"),
		
		/** 'R' */
		R_UP("\\x52"),
		
		/** 'S' */
		S_UP("\\x53"),
		
		/** 'T' */
		T_UP("\\x54"),
		
		/** 'U' */
		U_UP("\\x55"),
		
		/** 'V' */
		V_UP("\\x56"),
		
		/** 'W' */
		W_UP("\\x57"),
		
		/** 'X' */
		X_UP("\\x58"),
		
		/** 'Y' */
		Y_UP("\\x59"),
		
		/** 'Z' */
		Z_UP("\\x5A"),
		
		/** '[' */
		QBRR("\\x5B"),
		
		/** '\' */
		BKSL("\\x5C"),
		
		/** ']' */
		QBRL("\\x5D"),
		
		/** '^' */
		CRET("\\x5E"),
		
		/** '_' */
		UNLN("\\x5F"),
		
		/** '`' */
		INAP("\\x60"),
		
		/** 'a' */
		A_DN("\\x61"),
		
		/** 'b' */
		B_DN("\\x62"),
		
		/** 'c' */
		C_DN("\\x63"),
		
		/** 'd' */
		D_DN("\\x64"),
		
		/** 'e' */
		E_DN("\\x65"),
		
		/** 'f' */
		F_DN("\\x66"),
		
		/** 'g' */
		G_DN("\\x67"),
		
		/** 'h' */
		H_DN("\\x68"),
		
		/** 'i' */
		I_DN("\\x69"),
		
		/** 'j' */
		J_DN("\\x6A"),
		
		/** 'k' */
		K_DN("\\x6B"),
		
		/** 'l' */
		L_DN("\\x6C"),
		
		/** 'm' */
		M_DN("\\x6D"),
		
		/** 'n' */
		N_DN("\\x6E"),
		
		/** 'o' */
		O_DN("\\x6F"),
		
		/** '|' */
		STRK("\\x7C"),
		
		/** '}' */
		CBRR("\\x7D"),
		
		/** '~' */
		TWDL("\\x7E");
		
		private String symbol;
		
		private Sym(String sym)
		{
			symbol = sym;
		}
		
		public String toString()
		{
			return symbol;
		}
	}
	//}}}
	
	//{{{ data members
	/** Control functions are reprsented in 7-bit code */
	public static final int MODE_7BIT  = 0;
	/** Control functions are reprsented in 8-bit code */
	public static final int MODE_8BIT  = 1;
	
	private static final String Pn  = "[0-9:]*";
	private static final String Pn2 = String.format("%s;%s", Pn, Pn);
	private static final String Pna = String.format("%s(?:;%s)*", Pn, Pn);
	//}}}
	
	//{{{ getC0() method
	private static String[] getC0(C0 key)
	{
		switch (key) {
		    case NUL: return  new String[] {"\\x00", "\\x00"}; 
		    case SOH: return  new String[] {"\\x01", "\\x01"};       
		    case STX: return  new String[] {"\\x02", "\\x02"};
		    case ETX: return  new String[] {"\\x03", "\\x03"};
		    case EOT: return  new String[] {"\\x04", "\\x04"};
		    case ENQ: return  new String[] {"\\x05", "\\x05"};        
		    case ACK: return  new String[] {"\\x06", "\\x06"};
		    case BEL: return  new String[] {"\\x07", "\\x07"};
		    case BS : return  new String[] {"\\x08", "\\x08"};
		    case HT : return  new String[] {"\\x09", "\\x09"};
		    case LF : return  new String[] {"\\x0A", "\\x0A"};
		    case VT : return  new String[] {"\\x0B", "\\x0B"};
		    case FF : return  new String[] {"\\x0C", "\\x0C"};
		    case CR : return  new String[] {"\\x0D", "\\x0D"};
		    case LS1: return  new String[] {"\\x0E", "\\x0E"};
		    case LS0: return  new String[] {"\\x0F", "\\x0F"};
		    case DLE: return  new String[] {"\\x10", "\\x10"};
		    case DC1: return  new String[] {"\\x11", "\\x11"};
		    case DC2: return  new String[] {"\\x12", "\\x12"};
		    case DC3: return  new String[] {"\\x13", "\\x13"};
		    case DC4: return  new String[] {"\\x14", "\\x14"};
		    case NAK: return  new String[] {"\\x15", "\\x15"};
		    case SYN: return  new String[] {"\\x16", "\\x16"};
		    case ETB: return  new String[] {"\\x17", "\\x17"};
		    case CAN: return  new String[] {"\\x18", "\\x18"};
		    case EM : return  new String[] {"\\x19", "\\x19"};
		    case SUB: return  new String[] {"\\x1A", "\\x1A"};
		    case ESC: return  new String[] {"\\x1B", "\\x1B"};
		    case IS4: return  new String[] {"\\x1C", "\\x1C"};
		    case IS3: return  new String[] {"\\x1D", "\\x1D"};
		    case IS2: return  new String[] {"\\x1E", "\\x1E"};
		    case IS1: return  new String[] {"\\x1F", "\\x1F"};
		  default:
			 return null;
		}
	} //}}}
	
	//{{{ getC1() method
	private static String[] getC1(C1 key, String esc)
	{
		switch (key) {
		    case BPH: return  new String[] {esc + "\\x42", "\\x82"};
		    case NBH: return  new String[] {esc + "\\x43", "\\x83"};
		    case NEL: return  new String[] {esc + "\\x45", "\\x85"};
		    case SSA: return  new String[] {esc + "\\x46", "\\x86"};
		    case ESA: return  new String[] {esc + "\\x47", "\\x87"};
		    case HTS: return  new String[] {esc + "\\x48", "\\x88"};
		    case HTJ: return  new String[] {esc + "\\x49", "\\x89"};
		    case VTS: return  new String[] {esc + "\\x4A", "\\x8A"};
		    case PLD: return  new String[] {esc + "\\x4B", "\\x8B"};
		    case PLU: return  new String[] {esc + "\\x4C", "\\x8C"};
		    case RI : return  new String[] {esc + "\\x4D", "\\x8D"};
		    case SS2: return  new String[] {esc + "\\x4E", "\\x8E"};
		    case SS3: return  new String[] {esc + "\\x4F", "\\x8F"};
		    case DCS: return  new String[] {esc + "\\x50", "\\x90"};
		    case PU1: return  new String[] {esc + "\\x51", "\\x91"};
		    case PU2: return  new String[] {esc + "\\x52", "\\x92"};
		    case STS: return  new String[] {esc + "\\x53", "\\x93"};
		    case CCH: return  new String[] {esc + "\\x54", "\\x94"};
		    case MW : return  new String[] {esc + "\\x55", "\\x95"};
		    case SPA: return  new String[] {esc + "\\x56", "\\x96"};
		    case EPA: return  new String[] {esc + "\\x57", "\\x97"};
		    case SOS: return  new String[] {esc + "\\x58", "\\x98"};
		    case SCI: return  new String[] {esc + "\\x5A", "\\x9A"};
		    case CSI: return  new String[] {esc + "\\x5B", "\\x9B"};
		    case ST : return  new String[] {esc + "\\x5C", "\\x9C"};
		    case OSC: return  new String[] {esc + "\\x5D", "\\x9D"};
		    case PM : return  new String[] {esc + "\\x5E", "\\x9E"};
		    case APC: return  new String[] {esc + "\\x5F", "\\x9F"};
		  default:
		  	  return null;
		}
	} //}}}
	
	//{{{ getRecordByCF() method
	private static Record getRecordByCF(CF func)
	{
		String   ESC = getC0(C0.ESC)[0];
		String[] CSI = getC1(C1.CSI, ESC);
		String   str = ""; 
		
		switch (func) {
		    //{{{ defines functions without parameters 
		    case ACK : return  new Record( getC0(C0.ACK) );
		    case APC : return  new Record( getC1(C1.APC, ESC) );
		    case BEL : return  new Record( getC0(C0.BEL) );
		    case BPH : return  new Record( getC1(C1.BPH, ESC) );
		    case BS  : return  new Record( getC0(C0.BS ) );
		    case CAN : return  new Record( getC0(C0.CAN) );
		    case CCH : return  new Record( getC1(C1.CCH, ESC) );
		    case CR  : return  new Record( getC0(C0.CR ) );
		    case CSI : return  new Record( getC1(C1.CSI, ESC) );
		    case DC1 : return  new Record( getC0(C0.DC1) );
		    case DC2 : return  new Record( getC0(C0.DC2) );
		    case DC3 : return  new Record( getC0(C0.DC3) );
		    case DC4 : return  new Record( getC0(C0.DC4) );
		    case DCS : return  new Record( getC1(C1.DCS, ESC) );
		    case DLE : return  new Record( getC0(C0.DLE) );
		    case EM  : return  new Record( getC0(C0.EM ) );
		    case ENQ : return  new Record( getC0(C0.ENQ) );
		    case EOT : return  new Record( getC0(C0.EOT) );
		    case EPA : return  new Record( getC1(C1.EPA, ESC) );
		    case ESA : return  new Record( getC1(C1.ESA, ESC) );
		    case ESC : return  new Record( getC0(C0.ESC) );
		    case ETB : return  new Record( getC0(C0.ETB) );
		    case ETX : return  new Record( getC0(C0.ETX) );
		    case FF  : return  new Record( getC0(C0.FF ) );
		    case HT  : return  new Record( getC0(C0.HT ) );
		    case HTJ : return  new Record( getC1(C1.HTJ, ESC) );
		    case HTS : return  new Record( getC1(C1.HTS, ESC) );
		    case IS1 : return  new Record( getC0(C0.IS1) );
		    case IS2 : return  new Record( getC0(C0.IS2) );
		    case IS3 : return  new Record( getC0(C0.IS3) );
		    case IS4 : return  new Record( getC0(C0.IS4) );
		    case LF  : return  new Record( getC0(C0.LF ) );
		    case LS0 : return  new Record( null, getC0(C0.LS0)[1] );
		    case LS1 : return  new Record( null, getC0(C0.LS0)[1] );
		    case MW  : return  new Record( getC1(C1.MW , ESC) );
		    case NAK : return  new Record( getC0(C0.NAK) );
		    case NBH : return  new Record( getC1(C1.NBH, ESC) );
		    case NEL : return  new Record( getC1(C1.NEL, ESC) );
		    case NUL : return  new Record( getC0(C0.NUL) );
		    case OSC : return  new Record( getC1(C1.OSC, ESC) );
		    case PLD : return  new Record( getC1(C1.PLD, ESC) );
		    case PLU : return  new Record( getC1(C1.PLU, ESC) );
		    case PM  : return  new Record( getC1(C1.PM , ESC) );
		    case PU1 : return  new Record( getC1(C1.PU1, ESC) );
		    case PU2 : return  new Record( getC1(C1.PU2, ESC) );
		    case RI  : return  new Record( getC1(C1.RI , ESC) );
		    case SCI : return  new Record( getC1(C1.SCI, ESC) );
		    case SI  : return  new Record( getC0(C0.LS0)[0], null );
		    case SO  : return  new Record( getC0(C0.LS1)[0], null );
		    case SOH : return  new Record( getC0(C0.SOH) );
		    case SOS : return  new Record( getC1(C1.SOS, ESC) );
		    case SPA : return  new Record( getC1(C1.SPA, ESC) );
		    case SS2 : return  new Record( getC1(C1.SS2, ESC) );
		    case SS3 : return  new Record( getC1(C1.SS3, ESC) );
		    case SSA : return  new Record( getC1(C1.SSA, ESC) );
		    case ST  : return  new Record( getC1(C1.ST , ESC) );
		    case STS : return  new Record( getC1(C1.STS, ESC) );
		    case STX : return  new Record( getC0(C0.STX) );
		    case SUB : return  new Record( getC0(C0.SUB) );
		    case SYN : return  new Record( getC0(C0.SYN) );
		    case VT  : return  new Record( getC0(C0.VT ) );
		    case VTS : return  new Record( getC1(C1.VTS, ESC) );
		    //}}}
		    //{{{ defines functions with any parameters
	    	    case CBT : str = Pn  + Sym.Z_UP;		return  new Record(CSI[0] + str, CSI[1] + str);      
		    case CHA : str = Pn  + Sym.G_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case CHT : str = Pn  + Sym.I_UP;		return  new Record(CSI[0] + str, CSI[1] + str);                   
		    case CMD : str = ESC + Sym.D_DN;		return  new Record(str, str);
		    case CNL : str = Pn  + Sym.E_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case CPL : str = Pn  + Sym.F_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case CPR : str = Pn2 + Sym.R_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case CTC : str = Pna + Sym.W_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case CUB : str = Pn  + Sym.D_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case CUD : str = Pn  + Sym.B_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case CUF : str = Pn  + Sym.C_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case CUP : str = Pn2 + Sym.H_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case CUU : str = Pn  + Sym.A_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case CVT : str = Pn  + Sym.Y_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case DA  : str = Pn  + Sym.C_DN;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case DAQ : str = Pna + Sym.O_DN;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case DCH : str = Pn  + Sym.P_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case DL  : str = Pn  + Sym.M_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case DMI : str = ESC + Sym.INAP;		return  new Record(str, str);
		    case DSR : str = Pn  + Sym.N_DN;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case DTA : str = Pn2 + Sym.SPCE + Sym.T_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case EA  : str = Pn  + Sym.O_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case ECH : str = Pn  + Sym.X_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case ED  : str = Pn  + Sym.J_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case EF  : str = Pn  + Sym.N_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case EL  : str = Pn  + Sym.K_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case EMI : str = ESC + Sym.B_DN;		return  new Record(str, str);
		    case FNK : str = Pn  + Sym.SPCE + Sym.W_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case FNT : str = Pn2 + Sym.SPCE + Sym.D_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case GCC : str = Pn  + Sym.SPCE + Sym.UNLN; return  new Record(CSI[0] + str, CSI[1] + str);
		    case GSM : str = Pn2 + Sym.SPCE + Sym.B_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case GSS : str = Pn  + Sym.SPCE + Sym.C_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case HPA : str = Pn  + Sym.INAP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case HPB : str = Pn  + Sym.J_DN;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case HPR : str = Pn  + Sym.A_DN;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case HVP : str = Pn2 + Sym.F_DN;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case ICH : str = Pn  + Sym.AT;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case IDCS: str = Pn  + Sym.SPCE + Sym.O_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case IGS : str = Pn  + Sym.SPCE + Sym.M_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case IL  : str = Pn  + Sym.L_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case INT : str = ESC + Sym.A_DN;		return  new Record(str, str);
		    case JFY : str = Pna + Sym.SPCE + Sym.F_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case LS1R: str = ESC + Sym.TWDL;		return  new Record(str, str);
		    case LS2 : str = ESC + Sym.N_DN;		return  new Record(str, str);
		    case LS2R: str = ESC + Sym.CBRR;		return  new Record(str, str);
		    case LS3 : str = ESC + Sym.O_DN;		return  new Record(str, str);
		    case LS3R: str = ESC + Sym.STRK;		return  new Record(str, str);
		    case MC  : str = Pn  + Sym.I_DN;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case NP  : str = Pn  + Sym.U_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case PEC : str = Pn  + Sym.SPCE + Sym.Z_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case PFS : str = Pn  + Sym.SPCE + Sym.J_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case PP  : str = Pn  + Sym.V_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case PPA : str = Pn  + Sym.SPCE + Sym.P_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case PPB : str = Pn  + Sym.SPCE + Sym.R_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case PPR : str = Pn  + Sym.SPCE + Sym.Q_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case PTX : str = Pn  + Sym.BKSL;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case QUAD: str = Pna + Sym.SPCE + Sym.H_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case REP : str = Pn  + Sym.B_DN;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case RIS : str = ESC + Sym.C_DN;		return  new Record(str, str);
		    case RM  : str = Pna + Sym.L_DN;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case SACS: str = Pn  + Sym.SPCE + Sym.BKSL; return  new Record(CSI[0] + str, CSI[1] + str);
		    case SAPV: str = Pna + Sym.SPCE + Sym.QBRL; return  new Record(CSI[0] + str, CSI[1] + str);
		    case SCO : str = Pn  + Sym.SPCE + Sym.E_DN; return  new Record(CSI[0] + str, CSI[1] + str);
		    case SCP : str = Pn2 + Sym.SPCE + Sym.K_DN; return  new Record(CSI[0] + str, CSI[1] + str);
		    case SCS : str = Pn  + Sym.SPCE + Sym.G_DN; return  new Record(CSI[0] + str, CSI[1] + str);
		    case SD  : str = Pn  + Sym.T_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case SDS : str = Pn  + Sym.QBRL;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case SEE : str = Pn  + Sym.Q_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case SEF : str = Pn2 + Sym.SPCE + Sym.Y_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case SGR : str = Pna + Sym.M_DN;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case SHS : str = Pn  + Sym.SPCE + Sym.K_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case SIMD: str = Pn  + Sym.CRET;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case SL  : str = Pn  + Sym.SPCE + Sym.AT;	return  new Record(CSI[0] + str, CSI[1] + str);
		    case SLH : str = Pn  + Sym.SPCE + Sym.U_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case SLL : str = Pn  + Sym.SPCE + Sym.V_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case SLS : str = Pn  + Sym.SPCE + Sym.H_DN; return  new Record(CSI[0] + str, CSI[1] + str);
		    case SM  : str = Pna + Sym.H_DN;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case SPD : str = Pn2 + Sym.SPCE + Sym.S_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case SPH : str = Pn  + Sym.SPCE + Sym.I_DN; return  new Record(CSI[0] + str, CSI[1] + str);
		    case SPI : str = Pn2 + Sym.SPCE + Sym.G_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case SPL : str = Pn  + Sym.SPCE + Sym.J_DN; return  new Record(CSI[0] + str, CSI[1] + str);
		    case SPQR: str = Pn  + Sym.SPCE + Sym.X_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case SR  : str = Pn  + Sym.SPCE + Sym.A_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case SRCS: str = Pn  + Sym.SPCE + Sym.F_DN; return  new Record(CSI[0] + str, CSI[1] + str);
		    case SRS : str = Pn  + Sym.QBRR;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case SSU : str = Pn  + Sym.SPCE + Sym.I_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case SSW : str = Pn  + Sym.SPCE + Sym.QBRR; return  new Record(CSI[0] + str, CSI[1] + str);
		    case STAB: str = Pn  + Sym.SPCE + Sym.CRET; return  new Record(CSI[0] + str, CSI[1] + str);
		    case SU  : str = Pn  + Sym.S_UP;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case SVS : str = Pn  + Sym.SPCE + Sym.L_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case TAC : str = Pn  + Sym.SPCE + Sym.B_DN; return  new Record(CSI[0] + str, CSI[1] + str);
		    case TALE: str = Pn  + Sym.SPCE + Sym.A_DN; return  new Record(CSI[0] + str, CSI[1] + str);
		    case TATE: str = Pn  + Sym.SPCE + Sym.INAP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case TBC : str = Pn  + Sym.G_DN;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case TCC : str = Pn2 + Sym.SPCE + Sym.C_DN; return  new Record(CSI[0] + str, CSI[1] + str);
		    case TSR : str = Pn  + Sym.SPCE + Sym.D_DN; return  new Record(CSI[0] + str, CSI[1] + str);
		    case TSS : str = Pn  + Sym.SPCE + Sym.E_UP; return  new Record(CSI[0] + str, CSI[1] + str);
		    case VPA : str = Pn  + Sym.D_DN;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case VPB : str = Pn  + Sym.K_DN;		return  new Record(CSI[0] + str, CSI[1] + str);
		    case VPR : str = Pn  + Sym.E_DN;		return  new Record(CSI[0] + str, CSI[1] + str);
		    //}}}
		}
		
		return null;
	} //}}}
	
	//{{{ generateFullWorkingSet() method
	/** Returns map of Records. Map contains information about ALL control functions. */
	public static EnumMap<CF, Record> generateFullWorkingSet()
	{
		return generateWorkingSet(CF.values());
	} //}}}
	
	//{{{ generateWorkingSet() method
	/**
	   Returns map of Records. Map contains information about SELECTED control functions only.
	   If array of control functions is empty - returned Map is empty too.
	   @param args array of control functions
	 */
	public static EnumMap<CF, Record> generateWorkingSet(CF... args)
	{
		EnumMap<CF, Record> retws = new EnumMap<CF, Record>(CF.ACK.getDeclaringClass());
		
		for (CF element: args)
			retws.put( element, getRecordByCF(element) );
		
		return retws;
	} //}}}
	
	//{{{ getCFPattern() method
	/**
	   Returns control function's RegExp-pattern.
	   @param cmd control function
	   @param mode mode of control function's repersentation, MODE_7BIT and MODE_8BIT
	 */
	public static String getCFPattern(CF cmd, int mode)
	{
		if (mode == MODE_8BIT) {
			return getRecordByCF(cmd).pattern8;
			
		} else {
			return getRecordByCF(cmd).pattern7;
			
		}
	} //}}}
	
	//{{{ getCommonCFPattern() method
	/**
	   Returns common control function's RegExp-pattern, which matches any sequence.
	   @param mode mode of control function's repersentation, MODE_7BIT or MODE_8BIT
	   @param exceptLF_CR enables skipping following symbols: Line Feed and Carriage Return
	 */
	public static String getCommonCFPattern(int mode, boolean exceptLF_CR)
	{
		if (exceptLF_CR) {
			return getCommonCFPattern(mode, new C0[]{C0.LF, C0.CR}, null, null);
		} else {
			return getCommonCFPattern(mode, null, null, null);
		}
	}
	
	/**
	   Returns common control function's RegExp-pattern, which matches any sequence.
	   @param mode mode of control function's repersentation, MODE_7BIT or MODE_8BIT
	   @param exclC0Set array of excluded C0-functions
	   @param exclC1Set array of excluded C1-functions
	   @param exclSymSet array of excluded symbols (from CSI-functions)
	 */
	public static String getCommonCFPattern(int mode, C0[] exclC0Set, C1[] exclC1Set, Sym[] exclSymSet)
	{
		StringBuilder retStr = new StringBuilder();
		String ESC = "";
		int index = (mode == MODE_7BIT) ? 0 : 1;
		
		retStr.append("(?:");
		
		// CSI
		retStr.append( mode == MODE_7BIT ?
		       			String.format("(?:%s%s|%s)", getC0(C0.ESC)[index], getC1(C1.CSI, ESC)[index], getC0(C0.ESC)[index] ) :
		       			String.format("(?:%s|%s)"  , 			   getC1(C1.CSI, ESC)[index], getC0(C0.ESC)[index] )
		     ).append( Pna
		     ).append( String.format("%s?[%s-%s%s%s%s", Sym.SPCE, Sym.AT, Sym.O_DN, Sym.STRK, Sym.CBRR, Sym.TWDL)
		     );
		     
		if (exclSymSet != null && exclSymSet.length > 0) {
			retStr.append("&&[^");
			
			for ( Sym element: exclSymSet ) {
				retStr.append(element);
			}
			
			retStr.append("]");
		}
		retStr.append("])|");
		
		// C0
		retStr.append( String.format("[%s-%s", getC0(C0.NUL)[index], getC0(C0.IS1)[index]) );
		
		if (exclC0Set != null && exclC0Set.length > 0) {
			retStr.append("&&[^");
			
			for ( C0 element: exclC0Set ) {
				retStr.append( getC0(element)[index] );
			}
			
			retStr.append("]");
		}
		retStr.append("]|");
		
		// C1
		retStr.append( mode == MODE_7BIT ?
		       			String.format("%s[%s-%s", getC0(C0.ESC)[index], getC1(C1.BPH, ESC)[index], getC1(C1.APC, ESC)[index] ) :
		       			String.format("[%s-%s"  , 			getC1(C1.BPH, ESC)[index], getC1(C1.APC, ESC)[index] )
		     );
		       			
		if (exclC1Set != null && exclC1Set.length > 0) {
			retStr.append("&&[^");
			
			for ( C1 element: exclC1Set ) {
				retStr.append( getC1(element, ESC)[index] );
			}
			
			retStr.append("]");
		}
		retStr.append("]");
		
		return retStr.toString();
	} //}}}
	
	//{{{ inner Record class
	/**
	   Class <code>Record</code> stores RegExp-patterns.
	 */
	public static class Record implements Cloneable
	{
		/** 7-bit mode's pattern */
		public String pattern7;
		/** 8-bit mode's pattern */
		public String pattern8;
		
		/**
		   Constructor.
		   @param str7 7-bit mode's pattern
		   @param str8 8-bit mode's pattern
		 */
		public Record(String str7, String str8)
		{
			pattern7 = str7;
			pattern8 = str8;
		}
		
		/**
		   Constructor.
		   @param strs contains 7- and 8-bit mode's patterns
		 */
		public Record(String[] strs)
		{
			pattern7 = strs[0];
			pattern8 = strs[1];
		}
		
		/** Creates copy of current instance. */
		public Record clone()
		{
			return new Record(pattern7, pattern8); 
		}
		
		/** Returns string represetation of current instance. */
		public String toString()
		{
			return String.format("%-35s : %-35s", pattern7, pattern8);
		}
	}//}}}
}

