/*
 *  ColorTabs.java - Part of the BufferTabs plugin for jEdit.
 *  Copyright (C) 2003 Chris Samuels
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */


package buffertabs;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import gnu.regexp.RE;
import gnu.regexp.REException;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;

import org.gjt.sp.util.Log;


/**
 *  An add-on to BufferTabs to allow coloured backgrounds on tabs
 *
 * @author     Chris Samuels
 * @created    24 February 2003
 */
public class ColorTabs
{
   private static int VERSION_THREE_JEDIT = 3;
   private boolean colourHighlight = true;
   private static ColorTabs colourTabs = null;
   private boolean colourText = false;
   private boolean colourTitles = false;
   private boolean colourVariation = true;
   private Vector colours;
   private Hashtable coloursAssigned = new Hashtable();
   private final static int darkRange = 150;
   private final static float darkRatio = ((float) darkRange / 254);
   private final static int highestColour = 230;
   private final static int jnd = 4;
   private Object lock = new Object();
   private final static int lowestColour = 150;
   private boolean muteColours = true;
   private final static int muteRange = highestColour - lowestColour;
   private final static float muteRatio = ((float) muteRange / 254);
   private Random rnd = null;
   private static boolean useColours = false;


   /**
    *  Singleton class
    */
   private ColorTabs() { }



   /**
    *  Creates colours suitable for reading text labels. Uniformly moves the
    *  colour range to a darker range.
    *
    * @param  colour
    * @return
    */
   Color alterColourDarken(Color colour)
   {
      if (colour == null)
      {
         return Color.black;
      }

      int r = colour.getRed();
      int g = colour.getGreen();
      int b = colour.getBlue();

      r = (int) (lowestColour - (r * darkRatio));
      g = (int) (lowestColour - (g * darkRatio));
      b = (int) (lowestColour - (b * darkRatio));

      if (colourVariation)
      {
         r -= rnd.nextInt(5) * jnd;
         g -= rnd.nextInt(5) * jnd;
         b -= rnd.nextInt(5) * jnd;
         r = r / jnd * jnd;
         g = g / jnd * jnd;
         b = b / jnd * jnd;

      }

      r = Math.max(0, Math.min(r, lowestColour));
      g = Math.max(0, Math.min(g, lowestColour));
      b = Math.max(0, Math.min(b, lowestColour));

      return new Color(r, g, b);
   }


   /**
    *  Creates colours suitable for highlighting an active tab. Boosts the
    *  brightness and lowers saturation to achieve this.
    *
    * @param  colour
    * @return
    */
   Color alterColourHighlight(Color colour)
   {
      if (colour == null)
      {
         return Color.lightGray;
      }
      int r = colour.getRed();
      int g = colour.getGreen();
      int b = colour.getBlue();

      float[] hsb = Color.RGBtoHSB(r, g, b, null);

      float s = hsb[1];
      float v = hsb[2];

      s *= 0.6;
      s = Math.max(0.0f, Math.min(s, 1f));

      v *= 1.6;
      v = Math.max(0.0f, Math.min(v, 0.8f));

      return Color.getHSBColor(hsb[0], s, v);
   }


   /**
    *  Creates colours suitable for backgrounds. Uniformly moves the colour
    *  range to a lighter paler range.
    *
    *@param  colour
    *@return
    */
   Color alterColourMute(Color colour)
   {
      if (colour == null)
      {
         return Color.gray;
      }

      int r = colour.getRed();
      int g = colour.getGreen();
      int b = colour.getBlue();

      r = (int) (lowestColour + (r * muteRatio));
      g = (int) (lowestColour + (g * muteRatio));
      b = (int) (lowestColour + (b * muteRatio));

      if (colourVariation)
      {
         r += rnd.nextInt(5) * jnd;
         g += rnd.nextInt(5) * jnd;
         b += rnd.nextInt(5) * jnd;
         r = r / jnd * jnd;
         g = g / jnd * jnd;
         b = b / jnd * jnd;

      }

      r = Math.max(lowestColour, Math.min(r, highestColour));
      g = Math.max(lowestColour, Math.min(g, highestColour));
      b = Math.max(lowestColour, Math.min(b, highestColour));

      return new Color(r, g, b);
   }


   /**
    *  Gets the defaultColourFor attribute of the ColorTabs class
    *
    * @param  name
    * @return       The defaultColourFor value
    */
   public Color getDefaultColourFor(String name)
   {
      synchronized (lock)
      {
         if (colours == null)
         {
            loadColours();
         }

         if (coloursAssigned.containsKey(name))
         {
            return (Color) coloursAssigned.get(name);
         }

         for (int i = 0; i < colours.size(); i++)
         {
            ColorEntry entry = (ColorEntry) colours.elementAt(i);
            if (entry.re.isMatch(name))
            {
               Color newColour = null;
               if (muteColours)
               {
                  if (colourTitles)
                  {
                     newColour = alterColourDarken(entry.colour);
                  }
                  else
                  {
                     newColour = alterColourMute(entry.colour);
                  }
               }
               else
               {
                  newColour = entry.colour;
               }

               coloursAssigned.put(name, newColour);
               return newColour;
            }
         }

         return null;
      }
   }


   /**
    * Returns access to the Singleton ColorTabs class
    *
    * @return    Singleton ColorTabs
    */
   public static ColorTabs instance()
   {
      if (colourTabs == null)
      {
         colourTabs = new ColorTabs();
      }
      return colourTabs;
   }


   /**
    *  Load the colours from 'File system browser' colour options
    */
   private void loadColours()
   {
      synchronized (lock)
      {
         colours = new Vector();

         if (!jEdit.getBooleanProperty("vfs.browser.colorize"))
         {
            return;
         }

         String glob;
         int i = 0;
         while ((glob = jEdit.getProperty("vfs.browser.colors." + i + ".glob")) != null)
         {
            try
            {
               colours.addElement(new ColorEntry(
                     new RE(MiscUtilities.globToRE(glob)),
                     jEdit.getColorProperty(
                     "vfs.browser.colors." + i + ".color",
                     Color.black)));
               i++;
            }
            catch (REException e)
            {
               Log.log(Log.ERROR, ColorTabs.class, "Invalid regular expression: " + glob);
               //Log.log( Log.ERROR, ColorTabs.class, e );
               //Log.flushStream();
            }
         }
      }
   }



   /**
    * Check for changes to properties
    *
    * @param  parent
    */
   public void propertiesChanged(BufferTabs parent)
   {

      if (useColours != jEdit.getBooleanProperty("buffertabs.colourtabs"))
      {
         useColours = !useColours;

         //Turn off all colour features
         if (!useColours)
         {
            try
            {

               for (int index = parent.getTabCount() - 1; index >= 0; index--)
               {
                  parent.setBackgroundAt(index, null);
                  parent.setForegroundAt(index, null);
               }

            }
            catch (java.lang.NullPointerException npe)
            {
               Log.log(Log.ERROR, ColorTabs.class, "propertiesChanged: 1 " + npe.toString());
               //Log.log( Log.ERROR, ColorTabs.class, npe );
               //Log.flushStream();
            }

            try
            {

               parent.getUI().uninstallUI(parent);

               UIManager.getDefaults().put("TabbedPane.selected", null);

               parent.getUI().installUI(parent);
            }
            catch (java.lang.NullPointerException npe)
            {
               Log.log(Log.ERROR, ColorTabs.class, "propertiesChanged: 2 " + npe.toString());
               //Log.log( Log.ERROR, ColorTabs.class, npe );
               //Log.flushStream();
            }
         }

      }

      if (useColours)
      {
         if (muteColours != jEdit.getBooleanProperty("buffertabs.colourmute"))
         {
            muteColours = !muteColours;
         }

         if (colourText != jEdit.getBooleanProperty("buffertabs.colourizetext"))
         {
            colourText = !colourText;
         }

         if (colourVariation != jEdit.getBooleanProperty("buffertabs.colourvariation"))
         {
            colourVariation = !colourVariation;
         }

         if (colourTitles != jEdit.getBooleanProperty("buffertabs.colourizetext"))
         {
            colourTitles = !colourTitles;
         }

         if (colourHighlight != jEdit.getBooleanProperty("buffertabs.colourhighlight"))
         {
            colourHighlight = !colourHighlight;

            //Turn off all colourhighlight
            if (!colourHighlight)
            {
               try
               {
                  parent.getUI().uninstallUI(parent);
                  UIManager.getDefaults().put("TabbedPane.selected", null);
                  parent.getUI().installUI(parent);
               }
               catch (Exception e)
               {
                  Log.log(Log.ERROR, ColorTabs.class, "propertiesChanged: 3 " + e.toString());
                  //Log.log( Log.ERROR, ColorTabs.class, e );
                  //Log.flushStream();
               }
            }
         }

         loadColours();
         coloursAssigned.clear();
         rnd = new java.util.Random(20020212);                   //Set seed so colour variation are 'mostly' consistent during a session
      }

   }


   /**
    * Sets the colour of the given tab
    *
    * @param  index   The index of the buffer to change
    * @param  parent  The instance of the BufferTabs class to use
    */
   public void setColour(BufferTabs parent, int index)
   {
      if (useColours)
      {
         Buffer buffer = (Buffer) parent.getBuffers().elementAt(index);
         String name = buffer.getName();

         try
         {
            if (!colourTitles)
            {
               Color color = getDefaultColourFor(name);
               parent.setBackgroundAt(index, color);
               parent.setForegroundAt(index, null);

            }
            else
            {
               Color color = getDefaultColourFor(name);
               parent.setForegroundAt(index, color);
               parent.setBackgroundAt(index, null);

            }

         }
         catch (java.lang.NullPointerException npe)
         {
            Log.log(Log.ERROR, ColorTabs.class, "setColour: " + npe.toString());
            // Log.log( Log.ERROR, ColorTabs.class, npe );
            // Log.flushStream();

         }
         updateHighlight(parent, index);
      }
   }


   /**
    *  Force the Look and Feel to use the given colour as it 'selected' colour.
    *  TODO:This may cause side-effects with other tab panes.
    *
    *@param  parent
    *@param  index
    */
   void updateHighlight(BufferTabs parent, int index)
   {
      if (useColours && colourHighlight)
      {

         // System.out.println("CES: updateHighlight  index="+index +"   selectedIndex="+parent.getSelectedIndex());
         if (index == parent.getSelectedIndex())
         {
            Buffer buffer = (Buffer) parent.getBuffers().elementAt(index);
            String name = buffer.getName();
            // System.out.println( "CES: name=" + name );
            Color colour = getDefaultColourFor(name);

            try
            {
               parent.getUI().uninstallUI(parent);
               UIManager.getDefaults().put("TabbedPane.selected", new ColorUIResource(alterColourHighlight(colour)));
               parent.getUI().installUI(parent);
            }
            catch (Exception e)
            {
               Log.log(Log.ERROR, ColorTabs.class, "updateHighlight: " + e.toString());
               // Log.log( Log.ERROR, ColorTabs.class, e );
               // Log.flushStream();
            }

         }
      }
   }


   /**
    * Class to store colour match data
    *
    * @author     Chris Samuels
    * @created    24 February 2003
    */
   static class ColorEntry
   {
      Color colour;
      RE re;


      ColorEntry(RE re, Color colour)
      {
         this.re = re;
         this.colour = colour;
      }
   }

}

