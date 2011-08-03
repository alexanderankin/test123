/**
* Copyright (C) 1998,1999,200,2001,2002,2003 Jean-Yves Mengant
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

package org.jymc.jpydebug.utils;

import java.awt.* ;
import javax.swing.* ;
import javax.swing.border.* ;

/**


  define SWING publics general generics
  basics stuffs as setting application LookAndFeel

  and defining misc static final stuff used by
  all swings panels

  @author Jean-Yves MENGANT
 
*/
public class Swing {


  public static final int  BEVELRAISED  = -1 ;
  public static final int  BEVELLOWERED = -2 ;

  // defining available look and feel
  public final static int METAL   = 0 ;
  public final static int MOTIF   = 1 ;
  public final static int WINDOWS = 2 ;
  public final static int MAC     = 3 ;

  // Define some widely used formats
  public final static Font   BASICFONT      = new Font("Dialog",Font.PLAIN,12) ;
  public final static Font   BOLDBASICFONT  = new Font("Dialog",Font.BOLD,12) ;
  public final static Font   BASICFONTCOUR8 = new Font("Courier",Font.PLAIN,8) ;
  public final static Font   BOLDFONTCOUR8  = new Font("Courier",Font.BOLD,8) ;
  public final static Font   BOLDFONTTIM10   = new Font("TimesRoman",Font.BOLD,10) ;
  public final static Font   BASICFONTTIM12  = new Font("TimesRoman",Font.PLAIN,12) ;
  public final static Font   BOLDFONTTIM12  = new Font("TimesRoman",Font.BOLD,12) ;
  public final static Font   BOLDFONTCOUR12 = new Font("Courier",Font.BOLD,12) ;
  public final static Font   BOLDFONTCOUR14 = new Font("Courier",Font.BOLD,14) ;
  public final static Font   BOLDFONTCOUR16 = new Font("Courier",Font.BOLD,16) ;
  public final static Color  BLUE             = Color.blue  ;
  public final static Color  BLACK            = Color.black ;
  public final static Color  WHITE            = Color.white ;
  public final static Color  RED              = Color.red   ;
  public final static Color  MAGENTA          = Color.magenta ;
  public final static Color  GREEN            = Color.green ;
  public final static Color  LIGHTGRAY        = Color.lightGray ;
  public final static Color  DARKGRAY         = Color.darkGray ;
  public final static Color  GRAY             = Color.gray ;
  public final static Color  YELLOW           = Color.yellow ;
  public final static Color  CONTROL          = UIManager.getColor("control") ;

  public final static SwingTextEnv BOXSTANDARD =
       new SwingTextEnv ( BOLDBASICFONT , CONTROL , BLUE ) ;

  public final static SwingTextEnv BOXBOLDGRAY =
       new SwingTextEnv ( BOLDFONTTIM12 , CONTROL , DARKGRAY ) ;

  public final static SwingTextEnv BOXLIGHTTITLEBOLD10 =
       new SwingTextEnv ( BOLDFONTTIM10 ,  CONTROL , BLACK ) ;

  public final static SwingTextEnv BOXLIGHTTITLE12 =
       new SwingTextEnv ( BASICFONTTIM12 ,  CONTROL , BLACK ) ;

  public final static SwingTextEnv BOXTITLE =
       new SwingTextEnv ( BOLDFONTCOUR12 ,  CONTROL , BLACK ) ;

  public final static SwingTextEnv FIELDSTANDARD =
       new SwingTextEnv ( BASICFONT ,  CONTROL , BLUE ) ;

  public final static SwingTextEnv FIELDBIG =
       new SwingTextEnv ( BOLDFONTCOUR14 ,  CONTROL , BLUE ) ;

  public final static SwingTextEnv TABLETITLE =
       new SwingTextEnv ( BOLDFONTCOUR16 ,  CONTROL , BLUE ) ;

  public final static SwingTextEnv FTPTITLE =
       new SwingTextEnv ( BOLDFONTCOUR16 ,  CONTROL , DARKGRAY ) ;



  private final static String _UI_SUN_ = "com.sun.java.swing.plaf." ;
  private final static String _UI_     = "javax.swing.plaf." ;
  private final static String[] _LOOKANDFEELS_ =
      { _UI_ +"metal.MetalLookAndFeel" ,
        _UI_SUN_ +"motif.MotifLookAndFeel" ,
        _UI_SUN_ +"windows.WindowsLookAndFeel" ,
        _UI_ +"mac.MacLookAndFeel"
      }  ;



   /**
     a synthetic way to build title borders
   */
   public static TitledBorder buildBorder( String title ,
                               int    titleJustification ,
                               int    titlePosition ,
                               SwingTextEnv textLayout  ,
                               int    thickness
                                   )
   {
   Border requestedBorder ;

     if ( thickness == BEVELRAISED )
       requestedBorder = BorderFactory.createRaisedBevelBorder() ;
     else if ( thickness == BEVELLOWERED )
       requestedBorder = Swing.buildStandardLoweredPanelBorder() ;
     else
       requestedBorder = new LineBorder( textLayout.get_foreGround() ,
                                         thickness) ;

     return(    new TitledBorder( requestedBorder,
                                  title ,
                                  titleJustification ,
                                  titlePosition ,
                                  textLayout.get_font()  ,
                                  textLayout.get_foreGround()
                                )
            ) ;
   }

   /**
     just return a 2 pitch Line border inside a loweredPanel
     ( This is a standard way to border big panels )
   */
   public static Border buildLoweredPanelBorder( Color color ,
                                                 int   thickness
                                                )
   {
      return (
        BorderFactory.createCompoundBorder(
          BorderFactory.createLoweredBevelBorder() ,
          BorderFactory.createLineBorder( color , thickness )
                                       )
             ) ;
   }

   public static Border buildStandardLoweredPanelBorder()
   {
     return Swing.buildLoweredPanelBorder(Color.lightGray,2) ;
   }

   /**
     just return a 2 pitch Line border inside a loweredPanel
     ( This is a standard way to border big panels )
   */
   public static Border buildRaisedPanelBorder( Color color ,
                                                int   thickness
                                               )
   {
      return (
        BorderFactory.createCompoundBorder(
          BorderFactory.createRaisedBevelBorder() ,
          BorderFactory.createLineBorder( color , thickness )
                                       )
             ) ;
  }

  private static int convertStrLookAndFeel( String strLookAndFeel )
  throws UnsupportedLookAndFeelException
  {
    if ( strLookAndFeel.equalsIgnoreCase("Windows") )
      return WINDOWS ;
    else if ( strLookAndFeel.equalsIgnoreCase("Metal") )
      return METAL ;
    else if ( strLookAndFeel.equalsIgnoreCase("Motif") )
      return MOTIF ;
    else if ( strLookAndFeel.equalsIgnoreCase("Mac") )
      return MAC ;
    else
      throw new UnsupportedLookAndFeelException(strLookAndFeel+" unsupported") ;
  }

  /**
    any look and Feel setting errors are reported thru non null
    String returned code

  */
  public static String setLookAndFeel( String strLookAndFeel ,
                                       Component component
                                     )
  {
    try {
      int lookAndFeel = convertStrLookAndFeel(strLookAndFeel) ;
       UIManager.setLookAndFeel( _LOOKANDFEELS_[lookAndFeel] ) ;
       if ( component != null )
         SwingUtilities.updateComponentTreeUI(component) ;
       return null ;
    }
    catch ( UnsupportedLookAndFeelException e )
    { return new String("Unsupported look and feel exception returned on :"+
                        strLookAndFeel
                       ) ;
    }
    catch ( IllegalAccessException f )
    { return new String("IllegalAccessException returned on :"+
                        strLookAndFeel
                       ) ;
    }
    catch ( InstantiationException g )
    { return new String("IllegalAccessException returned on :"+
                        strLookAndFeel
                       ) ;
    }
    catch ( ClassNotFoundException h )
    { return new String("ClassNotFound exception returned on :"+
                        strLookAndFeel
                       ) ;
    }
  }


  public Swing( Component component )
  {}

}
