/*
 * HTMLEntity.java
 * Copyright (c) 2000 Andre Kaplan, list of entities borrowed from 
 * org.w3c.tidy.EntityTable by Andy Quick <ac.quick@sympatico.ca>
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
    public static String lookupEntity(short code) {
        return (String) codesToEntities.get(new Short(code));
    }

    public static short lookupCode(String entityName) {
        Short res = (Short) entitiesToCodes.get(entityName);
        if (res == null) {
            return 0;
        } else {
            return res.shortValue();
        }
    }

    private static Hashtable codesToEntities;
    private static Hashtable entitiesToCodes;

    private static void putEntity(Short code, String entityName) {
        codesToEntities.put(code, entityName);
        entitiesToCodes.put(entityName, code);
    }

    static {
        codesToEntities = new Hashtable();
        entitiesToCodes = new Hashtable();

        putEntity(new Short((short) 160),  "nbsp");
        putEntity(new Short((short) 161),  "iexcl");
        putEntity(new Short((short) 162),  "cent");
        putEntity(new Short((short) 163),  "pound");
        putEntity(new Short((short) 164),  "curren");
        putEntity(new Short((short) 165),  "yen");
        putEntity(new Short((short) 166),  "brvbar");
        putEntity(new Short((short) 167),  "sect");
        putEntity(new Short((short) 168),  "uml");
        putEntity(new Short((short) 169),  "copy");
        putEntity(new Short((short) 170),  "ordf");
        putEntity(new Short((short) 171),  "laquo");
        putEntity(new Short((short) 172),  "not");
        putEntity(new Short((short) 173),  "shy");
        putEntity(new Short((short) 174),  "reg");
        putEntity(new Short((short) 175),  "macr");
        putEntity(new Short((short) 176),  "deg");
        putEntity(new Short((short) 177),  "plusmn");
        putEntity(new Short((short) 178),  "sup2");
        putEntity(new Short((short) 179),  "sup3");
        putEntity(new Short((short) 180),  "acute");
        putEntity(new Short((short) 181),  "micro");
        putEntity(new Short((short) 182),  "para");
        putEntity(new Short((short) 183),  "middot");
        putEntity(new Short((short) 184),  "cedil");
        putEntity(new Short((short) 185),  "sup1");
        putEntity(new Short((short) 186),  "ordm");
        putEntity(new Short((short) 187),  "raquo");
        putEntity(new Short((short) 188),  "frac14");
        putEntity(new Short((short) 189),  "frac12");
        putEntity(new Short((short) 190),  "frac34");
        putEntity(new Short((short) 191),  "iquest");
        putEntity(new Short((short) 192),  "Agrave");
        putEntity(new Short((short) 193),  "Aacute");
        putEntity(new Short((short) 194),  "Acirc");
        putEntity(new Short((short) 195),  "Atilde");
        putEntity(new Short((short) 196),  "Auml");
        putEntity(new Short((short) 197),  "Aring");
        putEntity(new Short((short) 198),  "AElig");
        putEntity(new Short((short) 199),  "Ccedil");
        putEntity(new Short((short) 200),  "Egrave");
        putEntity(new Short((short) 201),  "Eacute");
        putEntity(new Short((short) 202),  "Ecirc");
        putEntity(new Short((short) 203),  "Euml");
        putEntity(new Short((short) 204),  "Igrave");
        putEntity(new Short((short) 205),  "Iacute");
        putEntity(new Short((short) 206),  "Icirc");
        putEntity(new Short((short) 207),  "Iuml");
        putEntity(new Short((short) 208),  "ETH");
        putEntity(new Short((short) 209),  "Ntilde");
        putEntity(new Short((short) 210),  "Ograve");
        putEntity(new Short((short) 211),  "Oacute");
        putEntity(new Short((short) 212),  "Ocirc");
        putEntity(new Short((short) 213),  "Otilde");
        putEntity(new Short((short) 214),  "Ouml");
        putEntity(new Short((short) 215),  "times");
        putEntity(new Short((short) 216),  "Oslash");
        putEntity(new Short((short) 217),  "Ugrave");
        putEntity(new Short((short) 218),  "Uacute");
        putEntity(new Short((short) 219),  "Ucirc");
        putEntity(new Short((short) 220),  "Uuml");
        putEntity(new Short((short) 221),  "Yacute");
        putEntity(new Short((short) 222),  "THORN");
        putEntity(new Short((short) 223),  "szlig");
        putEntity(new Short((short) 224),  "agrave");
        putEntity(new Short((short) 225),  "aacute");
        putEntity(new Short((short) 226),  "acirc");
        putEntity(new Short((short) 227),  "atilde");
        putEntity(new Short((short) 228),  "auml");
        putEntity(new Short((short) 229),  "aring");
        putEntity(new Short((short) 230),  "aelig");
        putEntity(new Short((short) 231),  "ccedil");
        putEntity(new Short((short) 232),  "egrave");
        putEntity(new Short((short) 233),  "eacute");
        putEntity(new Short((short) 234),  "ecirc");
        putEntity(new Short((short) 235),  "euml");
        putEntity(new Short((short) 236),  "igrave");
        putEntity(new Short((short) 237),  "iacute");
        putEntity(new Short((short) 238),  "icirc");
        putEntity(new Short((short) 239),  "iuml");
        putEntity(new Short((short) 240),  "eth");
        putEntity(new Short((short) 241),  "ntilde");
        putEntity(new Short((short) 242),  "ograve");
        putEntity(new Short((short) 243),  "oacute");
        putEntity(new Short((short) 244),  "ocirc");
        putEntity(new Short((short) 245),  "otilde");
        putEntity(new Short((short) 246),  "ouml");
        putEntity(new Short((short) 247),  "divide");
        putEntity(new Short((short) 248),  "oslash");
        putEntity(new Short((short) 249),  "ugrave");
        putEntity(new Short((short) 250),  "uacute");
        putEntity(new Short((short) 251),  "ucirc");
        putEntity(new Short((short) 252),  "uuml");
        putEntity(new Short((short) 253),  "yacute");
        putEntity(new Short((short) 254),  "thorn");
        putEntity(new Short((short) 255),  "yuml");
        putEntity(new Short((short) 402),  "fnof");
        putEntity(new Short((short) 913),  "Alpha");
        putEntity(new Short((short) 914),  "Beta");
        putEntity(new Short((short) 915),  "Gamma");
        putEntity(new Short((short) 916),  "Delta");
        putEntity(new Short((short) 917),  "Epsilon");
        putEntity(new Short((short) 918),  "Zeta");
        putEntity(new Short((short) 919),  "Eta");
        putEntity(new Short((short) 920),  "Theta");
        putEntity(new Short((short) 921),  "Iota");
        putEntity(new Short((short) 922),  "Kappa");
        putEntity(new Short((short) 923),  "Lambda");
        putEntity(new Short((short) 924),  "Mu");
        putEntity(new Short((short) 925),  "Nu");
        putEntity(new Short((short) 926),  "Xi");
        putEntity(new Short((short) 927),  "Omicron");
        putEntity(new Short((short) 928),  "Pi");
        putEntity(new Short((short) 929),  "Rho");
        putEntity(new Short((short) 931),  "Sigma");
        putEntity(new Short((short) 932),  "Tau");
        putEntity(new Short((short) 933),  "Upsilon");
        putEntity(new Short((short) 934),  "Phi");
        putEntity(new Short((short) 935),  "Chi");
        putEntity(new Short((short) 936),  "Psi");
        putEntity(new Short((short) 937),  "Omega");
        putEntity(new Short((short) 945),  "alpha");
        putEntity(new Short((short) 946),  "beta");
        putEntity(new Short((short) 947),  "gamma");
        putEntity(new Short((short) 948),  "delta");
        putEntity(new Short((short) 949),  "epsilon");
        putEntity(new Short((short) 950),  "zeta");
        putEntity(new Short((short) 951),  "eta");
        putEntity(new Short((short) 952),  "theta");
        putEntity(new Short((short) 953),  "iota");
        putEntity(new Short((short) 954),  "kappa");
        putEntity(new Short((short) 955),  "lambda");
        putEntity(new Short((short) 956),  "mu");
        putEntity(new Short((short) 957),  "nu");
        putEntity(new Short((short) 958),  "xi");
        putEntity(new Short((short) 959),  "omicron");
        putEntity(new Short((short) 960),  "pi");
        putEntity(new Short((short) 961),  "rho");
        putEntity(new Short((short) 962),  "sigmaf");
        putEntity(new Short((short) 963),  "sigma");
        putEntity(new Short((short) 964),  "tau");
        putEntity(new Short((short) 965),  "upsilon");
        putEntity(new Short((short) 966),  "phi");
        putEntity(new Short((short) 967),  "chi");
        putEntity(new Short((short) 968),  "psi");
        putEntity(new Short((short) 969),  "omega");
        putEntity(new Short((short) 977),  "thetasym");
        putEntity(new Short((short) 978),  "upsih");
        putEntity(new Short((short) 982),  "piv");
        putEntity(new Short((short) 8226), "bull");
        putEntity(new Short((short) 8230), "hellip");
        putEntity(new Short((short) 8242), "prime");
        putEntity(new Short((short) 8243), "Prime");
        putEntity(new Short((short) 8254), "oline");
        putEntity(new Short((short) 8260), "frasl");
        putEntity(new Short((short) 8472), "weierp");
        putEntity(new Short((short) 8465), "image");
        putEntity(new Short((short) 8476), "real");
        putEntity(new Short((short) 8482), "trade");
        putEntity(new Short((short) 8501), "alefsym");
        putEntity(new Short((short) 8592), "larr");
        putEntity(new Short((short) 8593), "uarr");
        putEntity(new Short((short) 8594), "rarr");
        putEntity(new Short((short) 8595), "darr");
        putEntity(new Short((short) 8596), "harr");
        putEntity(new Short((short) 8629), "crarr");
        putEntity(new Short((short) 8656), "lArr");
        putEntity(new Short((short) 8657), "uArr");
        putEntity(new Short((short) 8658), "rArr");
        putEntity(new Short((short) 8659), "dArr");
        putEntity(new Short((short) 8660), "hArr");
        putEntity(new Short((short) 8704), "forall");
        putEntity(new Short((short) 8706), "part");
        putEntity(new Short((short) 8707), "exist");
        putEntity(new Short((short) 8709), "empty");
        putEntity(new Short((short) 8711), "nabla");
        putEntity(new Short((short) 8712), "isin");
        putEntity(new Short((short) 8713), "notin");
        putEntity(new Short((short) 8715), "ni");
        putEntity(new Short((short) 8719), "prod");
        putEntity(new Short((short) 8721), "sum");
        putEntity(new Short((short) 8722), "minus");
        putEntity(new Short((short) 8727), "lowast");
        putEntity(new Short((short) 8730), "radic");
        putEntity(new Short((short) 8733), "prop");
        putEntity(new Short((short) 8734), "infin");
        putEntity(new Short((short) 8736), "ang");
        putEntity(new Short((short) 8743), "and");
        putEntity(new Short((short) 8744), "or");
        putEntity(new Short((short) 8745), "cap");
        putEntity(new Short((short) 8746), "cup");
        putEntity(new Short((short) 8747), "int");
        putEntity(new Short((short) 8756), "there4");
        putEntity(new Short((short) 8764), "sim");
        putEntity(new Short((short) 8773), "cong");
        putEntity(new Short((short) 8776), "asymp");
        putEntity(new Short((short) 8800), "ne");
        putEntity(new Short((short) 8801), "equiv");
        putEntity(new Short((short) 8804), "le");
        putEntity(new Short((short) 8805), "ge");
        putEntity(new Short((short) 8834), "sub");
        putEntity(new Short((short) 8835), "sup");
        putEntity(new Short((short) 8836), "nsub");
        putEntity(new Short((short) 8838), "sube");
        putEntity(new Short((short) 8839), "supe");
        putEntity(new Short((short) 8853), "oplus");
        putEntity(new Short((short) 8855), "otimes");
        putEntity(new Short((short) 8869), "perp");
        putEntity(new Short((short) 8901), "sdot");
        putEntity(new Short((short) 8968), "lceil");
        putEntity(new Short((short) 8969), "rceil");
        putEntity(new Short((short) 8970), "lfloor");
        putEntity(new Short((short) 8971), "rfloor");
        putEntity(new Short((short) 9001), "lang");
        putEntity(new Short((short) 9002), "rang");
        putEntity(new Short((short) 9674), "loz");
        putEntity(new Short((short) 9824), "spades");
        putEntity(new Short((short) 9827), "clubs");
        putEntity(new Short((short) 9829), "hearts");
        putEntity(new Short((short) 9830), "diams");
        putEntity(new Short((short) 34),   "quot");
        putEntity(new Short((short) 38),   "amp");
        putEntity(new Short((short) 60),   "lt");
        putEntity(new Short((short) 62),   "gt");
        putEntity(new Short((short) 338),  "OElig");
        putEntity(new Short((short) 339),  "oelig");
        putEntity(new Short((short) 352),  "Scaron");
        putEntity(new Short((short) 353),  "scaron");
        putEntity(new Short((short) 376),  "Yuml");
        putEntity(new Short((short) 710),  "circ");
        putEntity(new Short((short) 732),  "tilde");
        putEntity(new Short((short) 8194), "ensp");
        putEntity(new Short((short) 8195), "emsp");
        putEntity(new Short((short) 8201), "thinsp");
        putEntity(new Short((short) 8204), "zwnj");
        putEntity(new Short((short) 8205), "zwj");
        putEntity(new Short((short) 8206), "lrm");
        putEntity(new Short((short) 8207), "rlm");
        putEntity(new Short((short) 8211), "ndash");
        putEntity(new Short((short) 8212), "mdash");
        putEntity(new Short((short) 8216), "lsquo");
        putEntity(new Short((short) 8217), "rsquo");
        putEntity(new Short((short) 8218), "sbquo");
        putEntity(new Short((short) 8220), "ldquo");
        putEntity(new Short((short) 8221), "rdquo");
        putEntity(new Short((short) 8222), "bdquo");
        putEntity(new Short((short) 8224), "dagger");
        putEntity(new Short((short) 8225), "Dagger");
        putEntity(new Short((short) 8240), "permil");
        putEntity(new Short((short) 8249), "lsaquo");
        putEntity(new Short((short) 8250), "rsaquo");
        putEntity(new Short((short) 8364), "euro");
    }
}
