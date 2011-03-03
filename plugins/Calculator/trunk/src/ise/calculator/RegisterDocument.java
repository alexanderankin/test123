/*
This file, unless otherwise noted, is wholly the work of Dale Anson,
danson@grafidog.com. The complete contents of this file is hereby 
released into the public domain, with no rights reserved. For questions, 
concerns, or comments, please email the author.
*/

package ise.calculator;

import java.awt.Toolkit;
import javax.swing.text.*;

/**
 * Extends PlainDocument to create a document for the calculator registers,
 * only allows numbers to be displayed.
 *
 * @author Dale Anson, July 2003
 */
public class RegisterDocument extends PlainDocument {

   private int radix = Base.BASE_10;     
   private int mode = Base.FLOAT;        

   public RegisterDocument() {
      this( Base.BASE_10, Base.FLOAT );
   }

   public RegisterDocument( int base, int mode ) {
      radix = base;
      this.mode = mode;
   }

   /**
    * Beeps if invalid value entered.
    * @param pos the starting offset >= 0
    * @param text the string to insert; does nothing with null/empty strings
    * @param attr the attributes for the inserted content
    * @exception BadLocationException when the given insert position is not a 
    * valid position within the document
    */
   public void insertString( int pos, String text, AttributeSet attr ) throws BadLocationException {
      if ( text == null || text.length() == 0 )
         return ;
      // chs (change sign) can only go in 2 places, at the front or just after
      // the E in float mode, base 10
      if ( text.equals( "chs" ) ) {
         if ( mode == Base.FLOAT && radix == Base.BASE_10 ) {
            // check for E, might want to change sign on the exponent
            String maybe_E = getText( pos - 1, 1 );
            if ( maybe_E.equals( "E" ) ) {
               if ( getText( pos, 1 ).equals( "-" ) )
                  remove( pos, 1 );
               else
                  insertString( pos, "-", attr );
               return ;
            }
         }
         // otherwise, change sign on the whole number
         if ( getText( 0, 1 ).equals( "-" ) )
            remove( 0, 1 );
         else
            insertString( 0, "-", attr );
         return ;
      }
      if ( text.equals( "NaN" ) || text.equals( "Infinity" ) || text.startsWith( "Error" ) ) {
         remove( 0, getLength() );
         ///insertString( 0, text, attr );
         return ;
      }
      if ( isNumber( text ) ) {
         super.insertString( pos, text.toUpperCase(), attr );
      }
      else {
         Toolkit.getDefaultToolkit().beep();
         //throw new BadLocationException( "NaN: " + text, pos );
      }
   }

   /**
    * Check that the given string is a number or a portion of a number.   
    */
   private boolean isNumber( String s ) {
      s = s.toUpperCase();
      int index = 0;
      if ( s.startsWith( "-" ) )
         index = 1;
      for ( int i = index; i < s.length(); i++ ) {
         char c = s.charAt( i );
         if ( mode == Base.FLOAT || mode == Base.BIGDECIMAL ) {
            if ( ( c >= '0' && c <= '9' ) || c == '.' || c == '-' ) {
               continue;
            }
            else if ( c == 'E' ) {
               try {
                  // E can't be at the start, and only one E in the middle
                  if ( getLength() == 0 && s.startsWith("E") )
                     return false;
                  String text = getText( 0, getLength() );
                  if ( text.indexOf( 'E' ) > 0 )
                     return false;
                  continue;
               }
               catch ( Exception e ) {
                  e.printStackTrace();
               }
            }
            return false;
         }
         else {
            switch ( radix ) {
               case Base.BASE_2:
                  if ( c == '0' || c == '1' )
                     break;
                  return false;
               case Base.BASE_8:
                  if ( c >= '0' && c <= '7' )
                     break;
                  return false;
               case Base.BASE_16:
                  if ( ( c >= '0' && c <= '9' ) || ( c >= 'A' && c <= 'F' ) )
                     break;
                  return false;
               case Base.BASE_10:
                  if ( c >= '0' && c <= '9' )
                     break;
                  return false;
            }
         }
      }
      return true;
   }
}
