/*
 * HTMLEntity.java
 * Copyright (c) 2000 Andre Kaplan
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import java.util.Hashtable;

public class HTMLEntity
{
	public static String lookup(short code) {
		return (String) entities.get(new Short(code));
	}
	private static Hashtable entities;
	
	static {
		entities = new Hashtable();

		entities.put(new Short((short) 160),  "nbsp");
		entities.put(new Short((short) 161),  "iexcl");
		entities.put(new Short((short) 162),  "cent");
		entities.put(new Short((short) 163),  "pound");
		entities.put(new Short((short) 164),  "curren");
		entities.put(new Short((short) 165),  "yen");
		entities.put(new Short((short) 166),  "brvbar");
		entities.put(new Short((short) 167),  "sect");
		entities.put(new Short((short) 168),  "uml");
		entities.put(new Short((short) 169),  "copy");
		entities.put(new Short((short) 170),  "ordf");
		entities.put(new Short((short) 171),  "laquo");
		entities.put(new Short((short) 172),  "not");
		entities.put(new Short((short) 173),  "shy");
		entities.put(new Short((short) 174),  "reg");
		entities.put(new Short((short) 175),  "macr");
		entities.put(new Short((short) 176),  "deg");
		entities.put(new Short((short) 177),  "plusmn");
		entities.put(new Short((short) 178),  "sup2");
		entities.put(new Short((short) 179),  "sup3");
		entities.put(new Short((short) 180),  "acute");
		entities.put(new Short((short) 181),  "micro");
		entities.put(new Short((short) 182),  "para");
		entities.put(new Short((short) 183),  "middot");
		entities.put(new Short((short) 184),  "cedil");
		entities.put(new Short((short) 185),  "sup1");
		entities.put(new Short((short) 186),  "ordm");
		entities.put(new Short((short) 187),  "raquo");
		entities.put(new Short((short) 188),  "frac14");
		entities.put(new Short((short) 189),  "frac12");
		entities.put(new Short((short) 190),  "frac34");
		entities.put(new Short((short) 191),  "iquest");
		entities.put(new Short((short) 192),  "Agrave");
		entities.put(new Short((short) 193),  "Aacute");
		entities.put(new Short((short) 194),  "Acirc");
		entities.put(new Short((short) 195),  "Atilde");
		entities.put(new Short((short) 196),  "Auml");
		entities.put(new Short((short) 197),  "Aring");
		entities.put(new Short((short) 198),  "AElig");
		entities.put(new Short((short) 199),  "Ccedil");
		entities.put(new Short((short) 200),  "Egrave");
		entities.put(new Short((short) 201),  "Eacute");
		entities.put(new Short((short) 202),  "Ecirc");
		entities.put(new Short((short) 203),  "Euml");
		entities.put(new Short((short) 204),  "Igrave");
		entities.put(new Short((short) 205),  "Iacute");
		entities.put(new Short((short) 206),  "Icirc");
		entities.put(new Short((short) 207),  "Iuml");
		entities.put(new Short((short) 208),  "ETH");
		entities.put(new Short((short) 209),  "Ntilde");
		entities.put(new Short((short) 210),  "Ograve");
		entities.put(new Short((short) 211),  "Oacute");
		entities.put(new Short((short) 212),  "Ocirc");
		entities.put(new Short((short) 213),  "Otilde");
		entities.put(new Short((short) 214),  "Ouml");
		entities.put(new Short((short) 215),  "times");
		entities.put(new Short((short) 216),  "Oslash");
		entities.put(new Short((short) 217),  "Ugrave");
		entities.put(new Short((short) 218),  "Uacute");
		entities.put(new Short((short) 219),  "Ucirc");
		entities.put(new Short((short) 220),  "Uuml");
		entities.put(new Short((short) 221),  "Yacute");
		entities.put(new Short((short) 222),  "THORN");
		entities.put(new Short((short) 223),  "szlig");
		entities.put(new Short((short) 224),  "agrave");
		entities.put(new Short((short) 225),  "aacute");
		entities.put(new Short((short) 226),  "acirc");
		entities.put(new Short((short) 227),  "atilde");
		entities.put(new Short((short) 228),  "auml");
		entities.put(new Short((short) 229),  "aring");
		entities.put(new Short((short) 230),  "aelig");
		entities.put(new Short((short) 231),  "ccedil");
		entities.put(new Short((short) 232),  "egrave");
		entities.put(new Short((short) 233),  "eacute");
		entities.put(new Short((short) 234),  "ecirc");
		entities.put(new Short((short) 235),  "euml");
		entities.put(new Short((short) 236),  "igrave");
		entities.put(new Short((short) 237),  "iacute");
		entities.put(new Short((short) 238),  "icirc");
		entities.put(new Short((short) 239),  "iuml");
		entities.put(new Short((short) 240),  "eth");
		entities.put(new Short((short) 241),  "ntilde");
		entities.put(new Short((short) 242),  "ograve");
		entities.put(new Short((short) 243),  "oacute");
		entities.put(new Short((short) 244),  "ocirc");
		entities.put(new Short((short) 245),  "otilde");
		entities.put(new Short((short) 246),  "ouml");
		entities.put(new Short((short) 247),  "divide");
		entities.put(new Short((short) 248),  "oslash");
		entities.put(new Short((short) 249),  "ugrave");
		entities.put(new Short((short) 250),  "uacute");
		entities.put(new Short((short) 251),  "ucirc");
		entities.put(new Short((short) 252),  "uuml");
		entities.put(new Short((short) 253),  "yacute");
		entities.put(new Short((short) 254),  "thorn");
		entities.put(new Short((short) 255),  "yuml");
		entities.put(new Short((short) 402),  "fnof");
		entities.put(new Short((short) 913),  "Alpha");
		entities.put(new Short((short) 914),  "Beta");
		entities.put(new Short((short) 915),  "Gamma");
		entities.put(new Short((short) 916),  "Delta");
		entities.put(new Short((short) 917),  "Epsilon");
		entities.put(new Short((short) 918),  "Zeta");
		entities.put(new Short((short) 919),  "Eta");
		entities.put(new Short((short) 920),  "Theta");
		entities.put(new Short((short) 921),  "Iota");
		entities.put(new Short((short) 922),  "Kappa");
		entities.put(new Short((short) 923),  "Lambda");
		entities.put(new Short((short) 924),  "Mu");
		entities.put(new Short((short) 925),  "Nu");
		entities.put(new Short((short) 926),  "Xi");
		entities.put(new Short((short) 927),  "Omicron");
		entities.put(new Short((short) 928),  "Pi");
		entities.put(new Short((short) 929),  "Rho");
		entities.put(new Short((short) 931),  "Sigma");
		entities.put(new Short((short) 932),  "Tau");
		entities.put(new Short((short) 933),  "Upsilon");
		entities.put(new Short((short) 934),  "Phi");
		entities.put(new Short((short) 935),  "Chi");
		entities.put(new Short((short) 936),  "Psi");
		entities.put(new Short((short) 937),  "Omega");
		entities.put(new Short((short) 945),  "alpha");
		entities.put(new Short((short) 946),  "beta");
		entities.put(new Short((short) 947),  "gamma");
		entities.put(new Short((short) 948),  "delta");
		entities.put(new Short((short) 949),  "epsilon");
		entities.put(new Short((short) 950),  "zeta");
		entities.put(new Short((short) 951),  "eta");
		entities.put(new Short((short) 952),  "theta");
		entities.put(new Short((short) 953),  "iota");
		entities.put(new Short((short) 954),  "kappa");
		entities.put(new Short((short) 955),  "lambda");
		entities.put(new Short((short) 956),  "mu");
		entities.put(new Short((short) 957),  "nu");
		entities.put(new Short((short) 958),  "xi");
		entities.put(new Short((short) 959),  "omicron");
		entities.put(new Short((short) 960),  "pi");
		entities.put(new Short((short) 961),  "rho");
		entities.put(new Short((short) 962),  "sigmaf");
		entities.put(new Short((short) 963),  "sigma");
		entities.put(new Short((short) 964),  "tau");
		entities.put(new Short((short) 965),  "upsilon");
		entities.put(new Short((short) 966),  "phi");
		entities.put(new Short((short) 967),  "chi");
		entities.put(new Short((short) 968),  "psi");
		entities.put(new Short((short) 969),  "omega");
		entities.put(new Short((short) 977),  "thetasym");
		entities.put(new Short((short) 978),  "upsih");
		entities.put(new Short((short) 982),  "piv");
		entities.put(new Short((short) 8226), "bull");
		entities.put(new Short((short) 8230), "hellip");
		entities.put(new Short((short) 8242), "prime");
		entities.put(new Short((short) 8243), "Prime");
		entities.put(new Short((short) 8254), "oline");
		entities.put(new Short((short) 8260), "frasl");
		entities.put(new Short((short) 8472), "weierp");
		entities.put(new Short((short) 8465), "image");
		entities.put(new Short((short) 8476), "real");
		entities.put(new Short((short) 8482), "trade");
		entities.put(new Short((short) 8501), "alefsym");
		entities.put(new Short((short) 8592), "larr");
		entities.put(new Short((short) 8593), "uarr");
		entities.put(new Short((short) 8594), "rarr");
		entities.put(new Short((short) 8595), "darr");
		entities.put(new Short((short) 8596), "harr");
		entities.put(new Short((short) 8629), "crarr");
		entities.put(new Short((short) 8656), "lArr");
		entities.put(new Short((short) 8657), "uArr");
		entities.put(new Short((short) 8658), "rArr");
		entities.put(new Short((short) 8659), "dArr");
		entities.put(new Short((short) 8660), "hArr");
		entities.put(new Short((short) 8704), "forall");
		entities.put(new Short((short) 8706), "part");
		entities.put(new Short((short) 8707), "exist");
		entities.put(new Short((short) 8709), "empty");
		entities.put(new Short((short) 8711), "nabla");
		entities.put(new Short((short) 8712), "isin");
		entities.put(new Short((short) 8713), "notin");
		entities.put(new Short((short) 8715), "ni");
		entities.put(new Short((short) 8719), "prod");
		entities.put(new Short((short) 8721), "sum");
		entities.put(new Short((short) 8722), "minus");
		entities.put(new Short((short) 8727), "lowast");
		entities.put(new Short((short) 8730), "radic");
		entities.put(new Short((short) 8733), "prop");
		entities.put(new Short((short) 8734), "infin");
		entities.put(new Short((short) 8736), "ang");
		entities.put(new Short((short) 8743), "and");
		entities.put(new Short((short) 8744), "or");
		entities.put(new Short((short) 8745), "cap");
		entities.put(new Short((short) 8746), "cup");
		entities.put(new Short((short) 8747), "int");
		entities.put(new Short((short) 8756), "there4");
		entities.put(new Short((short) 8764), "sim");
		entities.put(new Short((short) 8773), "cong");
		entities.put(new Short((short) 8776), "asymp");
		entities.put(new Short((short) 8800), "ne");
		entities.put(new Short((short) 8801), "equiv");
		entities.put(new Short((short) 8804), "le");
		entities.put(new Short((short) 8805), "ge");
		entities.put(new Short((short) 8834), "sub");
		entities.put(new Short((short) 8835), "sup");
		entities.put(new Short((short) 8836), "nsub");
		entities.put(new Short((short) 8838), "sube");
		entities.put(new Short((short) 8839), "supe");
		entities.put(new Short((short) 8853), "oplus");
		entities.put(new Short((short) 8855), "otimes");
		entities.put(new Short((short) 8869), "perp");
		entities.put(new Short((short) 8901), "sdot");
		entities.put(new Short((short) 8968), "lceil");
		entities.put(new Short((short) 8969), "rceil");
		entities.put(new Short((short) 8970), "lfloor");
		entities.put(new Short((short) 8971), "rfloor");
		entities.put(new Short((short) 9001), "lang");
		entities.put(new Short((short) 9002), "rang");
		entities.put(new Short((short) 9674), "loz");
		entities.put(new Short((short) 9824), "spades");
		entities.put(new Short((short) 9827), "clubs");
		entities.put(new Short((short) 9829), "hearts");
		entities.put(new Short((short) 9830), "diams");
		entities.put(new Short((short) 34),   "quot");
		entities.put(new Short((short) 38),   "amp");
		entities.put(new Short((short) 60),   "lt");
		entities.put(new Short((short) 62),   "gt");
		entities.put(new Short((short) 338),  "OElig");
		entities.put(new Short((short) 339),  "oelig");
		entities.put(new Short((short) 352),  "Scaron");
		entities.put(new Short((short) 353),  "scaron");
		entities.put(new Short((short) 376),  "Yuml");
		entities.put(new Short((short) 710),  "circ");
		entities.put(new Short((short) 732),  "tilde");
		entities.put(new Short((short) 8194), "ensp");
		entities.put(new Short((short) 8195), "emsp");
		entities.put(new Short((short) 8201), "thinsp");
		entities.put(new Short((short) 8204), "zwnj");
		entities.put(new Short((short) 8205), "zwj");
		entities.put(new Short((short) 8206), "lrm");
		entities.put(new Short((short) 8207), "rlm");
		entities.put(new Short((short) 8211), "ndash");
		entities.put(new Short((short) 8212), "mdash");
		entities.put(new Short((short) 8216), "lsquo");
		entities.put(new Short((short) 8217), "rsquo");
		entities.put(new Short((short) 8218), "sbquo");
		entities.put(new Short((short) 8220), "ldquo");
		entities.put(new Short((short) 8221), "rdquo");
		entities.put(new Short((short) 8222), "bdquo");
		entities.put(new Short((short) 8224), "dagger");
		entities.put(new Short((short) 8225), "Dagger");
		entities.put(new Short((short) 8240), "permil");
		entities.put(new Short((short) 8249), "lsaquo");
		entities.put(new Short((short) 8250), "rsaquo");
		entities.put(new Short((short) 8364), "euro");
	}
}
