/*
Copyright (c) 2005, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
this list of conditions and the following disclaimer in the documentation 
and/or other materials provided with the distribution.
* Neither the name of the <ORGANIZATION> nor the names of its contributors 
may be used to endorse or promote products derived from this software without 
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR 
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package beauty.parsers.java;

/**
 * Class to describe modifiers.  This class was originally an inner class of 
 * Java1.5.jj. I've moved it here for easier access.
 */
public final class ModifierSet {
    /* Definitions of the bits in the modifiers field.  */

    /* original values in ModifierSet */
    /*
    public static final int PUBLIC = 0x0001;
    public static final int PROTECTED = 0x0002;
    public static final int PRIVATE = 0x0004;
    public static final int ABSTRACT = 0x0008;
    public static final int STATIC = 0x0010;
    public static final int FINAL = 0x0020;
    public static final int SYNCHRONIZED = 0x0040;
    public static final int NATIVE = 0x0080;
    public static final int TRANSIENT = 0x0100;
    public static final int VOLATILE = 0x0200;
    public static final int STRICTFP = 0x1000;
    */

    /**
     * new values based on JVM spec, see <i>The JavaTM Virtual Machine 
     * Specification, Second edition</i>, tables 4.1, 4.4, 4.5, and 4.7.
     * These values also align with those in JBrowse's MutableModifier
     * class.  
     * Note: should change from STRICTFP to STRICT per the JVM spec.
     * Note: Even though the new syntax for Java 1.5 is covered in the Third
     * edition of the Specification, these remain the same.
     */
    public static final int PUBLIC = 0x0001 ;       // Declared public; may be accessed from outside its package.
    public static final int PRIVATE = 0x0002 ;      // Declared private; usable only within the defining class.
    public static final int PROTECTED = 0x0004 ;    // Declared protected; may be accessed within subclasses.
    public static final int STATIC = 0x0008 ;       // Declared static.
    public static final int FINAL = 0x0010 ;        // Declared final; no further assignment after initialization.
    public static final int SYNCHRONIZED = 0x0020;  // Declared synchronized; invocation is wrapped in a monitor lock.
    public static final int VOLATILE = 0x0040 ;     // Declared volatile; cannot be cached.
    public static final int TRANSIENT = 0x0080 ;    // Declared transient; not written or read by a persistent object manager.
    public static final int NATIVE = 0x0100;        // Declared native; implemented in a language other than Java.
    public static final int ABSTRACT = 0x0400;      // Declared abstract; no implementation is provided.
    public static final int STRICTFP = 0x0800;      // Declared strictfp; floating-point mode is FP-strict

	/**
	 * This method may be used to sort by visibility/accessibility.    
	 * @param modifiers the modifiers as an integer value.
	 * @return an integer value representing the visibility/accessibily "ranking"
	 * of the modifier, "public" accessibility is 1, "package" accessibility is 2,
	 * "protected" accessibility is 3, and "private" accessibility is 4.
	 */
    public static int visibilityRank( int modifiers ) {
        if ( isPublic( modifiers ) ) {
            return 1;
        }
        if ( isPackage( modifiers ) ) {
            return 2;
        }
        if ( isProtected( modifiers ) ) {
            return 3;
        }
        return 4;
    }

	/**
	 * A set of accessors that indicate whether the specified modifier
	 * is in the set.
	 */
    public static boolean isPublic( int modifiers ) {
        return ( modifiers & PUBLIC ) != 0;
    }

    public static boolean isProtected( int modifiers ) {
        return ( modifiers & PROTECTED ) != 0;
    }

    public static boolean isPrivate( int modifiers ) {
        return ( modifiers & PRIVATE ) != 0;
    }

    // package visible modifier, that is, not public, protected, nor private
    public static boolean isPackage( int modifiers ) {
        return !isPublic( modifiers ) && !isProtected( modifiers ) && !isPrivate( modifiers );
    }

    public static boolean isStatic( int modifiers ) {
        return ( modifiers & STATIC ) != 0;
    }

    public static boolean isAbstract( int modifiers ) {
        return ( modifiers & ABSTRACT ) != 0;
    }

    public static boolean isFinal( int modifiers ) {
        return ( modifiers & FINAL ) != 0;
    }

    public static boolean isNative( int modifiers ) {
        return ( modifiers & NATIVE ) != 0;
    }

    public static boolean isStrictfp( int modifiers ) {
        return ( modifiers & STRICTFP ) != 0;
    }

    public static boolean isSynchronized( int modifiers ) {
        return ( modifiers & SYNCHRONIZED ) != 0;
    }

    public static boolean isTransient( int modifiers ) {
        return ( modifiers & TRANSIENT ) != 0;
    }

    public static boolean isVolatile( int modifiers ) {
        return ( modifiers & VOLATILE ) != 0;
    }

    /**
     * Removes the given modifier.
     */
    static int removeModifier( int modifiers, int mod ) {
        return modifiers & ~mod;
    }


	/**
     * This method creates a string representation of the modifiers.  This method
     * handles "visibility" modifiers (public, private, etc), followed by other
     * modifiers (abstract, final, etc).
	 * @param modifiers modifiers as an integer value
	 * @return a string representation of the modifier value.
	 */
    public static String toString( int modifiers ) {
        StringBuffer sb = new StringBuffer();
        if ( ModifierSet.isPublic( modifiers ) ) {
            sb.append( "public " );
        }
        else if ( ModifierSet.isProtected( modifiers ) ) {
            sb.append( "protected " );
        }
        else if ( ModifierSet.isPrivate( modifiers ) ) {
            sb.append( "private " );
        }
        if ( ModifierSet.isStatic( modifiers ) ) {
            sb.append( "static " );
        }
        sb.append( modifiersAsString( modifiers ) );
        return sb.toString().trim();
    }

	/**
     * This method creates a string representation of the modifiers.  This method
     * does not handle "visibility" modifiers (public, private, etc), it only deals
     * with the other modifiers (abstract, final, etc).
	 * @param modifiers modifiers as an integer value
	 * @return a string representation of the modifier value.
	 */
    public static String modifiersAsString( int modifiers ) {
        if ( modifiers == 0 ) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        if ( ModifierSet.isAbstract( modifiers ) ) {
            sb.append( "abstract " );
        }
        if ( ModifierSet.isFinal( modifiers ) ) {
            sb.append( "final " );
        }
        if ( ModifierSet.isSynchronized( modifiers ) ) {
            sb.append( "synchronized " );
        }
        if ( ModifierSet.isNative( modifiers ) ) {
            sb.append( "native " );
        }
        if ( ModifierSet.isStrictfp( modifiers ) ) {
            sb.append( "strictfp " );
        }
        if ( ModifierSet.isTransient( modifiers ) ) {
            sb.append( "transient " );
        }
        if ( ModifierSet.isVolatile( modifiers ) ) {
            sb.append( "volatile " );
        }
        return sb.toString().trim();
    }
}


