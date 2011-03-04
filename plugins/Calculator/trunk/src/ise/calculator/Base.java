/*
This file, unless otherwise noted, is wholly the work of Dale Anson,
danson@grafidog.com. The complete contents of this file is hereby 
released into the public domain, with no rights reserved. For questions, 
concerns, or comments, please email the author.
*/

package ise.calculator;

/**
 * Definitions for numeric bases and modes. A 'base' is also known as a 'radix',
 * the calculator does base 16, 10, 8, and 2. Also definitions for modes, the
 * calculator uses 4 modes, big decimal, float, big int, and int.
 */
public class Base {

    // bases
    public static final int BASE_2 = 2;
    public static final int BASE_8 = 8;
    public static final int BASE_10 = 10;
    public static final int BASE_16 = 16;

    // modes
    public static final int INT = 32;
    public static final int BIGINT = 64;
    public static final int FLOAT = 128;
    public static final int BIGDECIMAL = 256;
}
