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
 *  An add-on to BufferTabs to allow colored backgrounds on tabs
 *
 * @author     Chris Samuels
 * @created    24 February 2003
 */
public class ColorTabs
{
   private static final int JND = 4;

   private static final int DARKEN_LOWEST_COLOR = 0;
   private static final int DARKEN_HIGHEST_COLOR = 150;
   private static final int DARKEN_RANGE = DARKEN_HIGHEST_COLOR - DARKEN_LOWEST_COLOR;
   private static final float DARKEN_RATIO = ((float) DARKEN_RANGE / 254);

   private static final int MUTE_LOWEST_COLOR = 150;
   private static final int MUTE_HIGHEST_COLOR = 230;
   private static final int MUTE_RANGE = MUTE_HIGHEST_COLOR - MUTE_LOWEST_COLOR;
   private static final float MUTE_RATIO = ((float) MUTE_RANGE / 254);

   private static ColorTabs colorTabs = null;

   private boolean enabled = false;
   private boolean selectedColorized = true;
   private boolean colorTitles = false;
   private boolean colorVariation = true;
   private Vector colors;
   private Hashtable colorsAssigned = new Hashtable();
   private Object lock = new Object();
   private boolean muteColors = true;
   private Random rnd = null;


   /**
    *  Singleton class
    */
   private ColorTabs() { }


   public boolean isEnabled() {
      return this.enabled;
   }


   public boolean isSelectedColorized() {
      return this.selectedColorized;
   }


   /**
    *  Creates colors suitable for reading text labels. Uniformly moves the
    *  color range to a darker range.
    *
    * @param  color
    * @return
    */
   Color alterColorDarken(Color color)
   {
      if (color == null)
      {
         return Color.black;
      }

      int r = color.getRed();
      int g = color.getGreen();
      int b = color.getBlue();

      r = (int) (DARKEN_HIGHEST_COLOR - (r * DARKEN_RATIO));
      g = (int) (DARKEN_HIGHEST_COLOR - (g * DARKEN_RATIO));
      b = (int) (DARKEN_HIGHEST_COLOR - (b * DARKEN_RATIO));

      if (colorVariation)
      {
         r -= rnd.nextInt(5) * JND;
         g -= rnd.nextInt(5) * JND;
         b -= rnd.nextInt(5) * JND;
         r = r / JND * JND;
         g = g / JND * JND;
         b = b / JND * JND;

      }

      r = Math.max(DARKEN_LOWEST_COLOR, Math.min(r, DARKEN_HIGHEST_COLOR));
      g = Math.max(DARKEN_LOWEST_COLOR, Math.min(g, DARKEN_HIGHEST_COLOR));
      b = Math.max(DARKEN_LOWEST_COLOR, Math.min(b, DARKEN_HIGHEST_COLOR));

      return new Color(r, g, b);
   }


   /**
    *  Creates colors suitable for highlighting an active tab. Boosts the
    *  brightness and lowers saturation to achieve this.
    *
    * @param  color
    * @return
    */
   Color alterColorHighlight(Color color)
   {
      if (color == null)
      {
         return Color.lightGray;
      }
      int r = color.getRed();
      int g = color.getGreen();
      int b = color.getBlue();

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
    *  Creates colors suitable for backgrounds. Uniformly moves the color
    *  range to a lighter paler range.
    *
    *@param  color
    *@return
    */
   Color alterColorMute(Color color)
   {
      if (color == null)
      {
         return Color.gray;
      }

      int r = color.getRed();
      int g = color.getGreen();
      int b = color.getBlue();

      r = (int) (MUTE_LOWEST_COLOR + (r * MUTE_RATIO));
      g = (int) (MUTE_LOWEST_COLOR + (g * MUTE_RATIO));
      b = (int) (MUTE_LOWEST_COLOR + (b * MUTE_RATIO));

      if (colorVariation)
      {
         r += rnd.nextInt(5) * JND;
         g += rnd.nextInt(5) * JND;
         b += rnd.nextInt(5) * JND;
         r = r / JND * JND;
         g = g / JND * JND;
         b = b / JND * JND;

      }

      r = Math.max(MUTE_LOWEST_COLOR, Math.min(r, MUTE_HIGHEST_COLOR));
      g = Math.max(MUTE_LOWEST_COLOR, Math.min(g, MUTE_HIGHEST_COLOR));
      b = Math.max(MUTE_LOWEST_COLOR, Math.min(b, MUTE_HIGHEST_COLOR));

      return new Color(r, g, b);
   }


   /**
    *  Gets the defaultColorFor attribute of the ColorTabs class
    *
    * @param  name
    * @return       The defaultColorFor value
    */
   public Color getDefaultColorFor(String name)
   {
      synchronized (lock)
      {
         if (colors == null)
         {
            loadColors();
         }

         if (colorsAssigned.containsKey(name))
         {
            return (Color) colorsAssigned.get(name);
         }

         for (int i = 0; i < colors.size(); i++)
         {
            ColorEntry entry = (ColorEntry) colors.elementAt(i);
            if (entry.re.isMatch(name))
            {
               Color newColor = null;
               if (muteColors)
               {
                  if (colorTitles)
                  {
                     newColor = alterColorDarken(entry.color);
                  }
                  else
                  {
                     newColor = alterColorMute(entry.color);
                  }
               }
               else
               {
                  newColor = entry.color;
               }

               colorsAssigned.put(name, newColor);
               return newColor;
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
      if (colorTabs == null)
      {
         colorTabs = new ColorTabs();
      }
      return colorTabs;
   }


   /**
    *  Load the colors from 'File system browser' color options
    */
   private void loadColors()
   {
      synchronized (lock)
      {
         colors = new Vector();

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
               colors.addElement(new ColorEntry(
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

      if (this.enabled != jEdit.getBooleanProperty("buffertabs.color-tabs"))
      {
         this.enabled = !this.enabled;

         //Turn off all color features
         if (!this.enabled)
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

      if (this.enabled)
      {
         if (muteColors != jEdit.getBooleanProperty("buffertabs.color-mute"))
         {
            muteColors = !muteColors;
         }

         if (colorVariation != jEdit.getBooleanProperty("buffertabs.color-variation"))
         {
            colorVariation = !colorVariation;
         }

         if (colorTitles != jEdit.getBooleanProperty("buffertabs.color-foreground"))
         {
            colorTitles = !colorTitles;
         }

         if (this.selectedColorized != jEdit.getBooleanProperty("buffertabs.color-selected"))
         {
            this.selectedColorized = !this.selectedColorized;

            //Turn off all colorhighlight
            if (!this.selectedColorized)
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

         loadColors();
         colorsAssigned.clear();
         //Set seed so color variation are 'mostly' consistent during a session
         rnd = new java.util.Random(20020212);
      }

   }


   /**
    * Sets the color of the given tab
    *
    * @param  index   The index of the buffer to change
    * @param  parent  The instance of the BufferTabs class to use
    */
   public void setColor(BufferTabs parent, int index)
   {
      if (this.enabled)
      {
         Buffer buffer = (Buffer) parent.getBuffers().elementAt(index);
         String name = buffer.getName();

         try
         {
            if (!colorTitles)
            {
               Color color = getDefaultColorFor(name);
               parent.setBackgroundAt(index, color);
               parent.setForegroundAt(index, null);

            }
            else
            {
               Color color = getDefaultColorFor(name);
               parent.setForegroundAt(index, color);
               parent.setBackgroundAt(index, null);

            }

         }
         catch (java.lang.NullPointerException npe)
         {
            Log.log(Log.ERROR, ColorTabs.class, "setColor: " + npe.toString());
            // Log.log( Log.ERROR, ColorTabs.class, npe );
            // Log.flushStream();

         }
         updateHighlight(parent, index);
      }
   }


   /**
    *  Force the Look and Feel to use the given color as it 'selected' color.
    *  TODO:This may cause side-effects with other tab panes.
    *
    *@param  parent
    *@param  index
    */
   void updateHighlight(BufferTabs parent, int index)
   {
      if (this.enabled && this.selectedColorized)
      {

         // System.out.println("CES: updateHighlight  index="+index +"   selectedIndex="+parent.getSelectedIndex());
         if (index == parent.getSelectedIndex())
         {
            Buffer buffer = (Buffer) parent.getBuffers().elementAt(index);
            String name = buffer.getName();
            // System.out.println( "CES: name=" + name );
            Color color = getDefaultColorFor(name);

            try
            {
               parent.getUI().uninstallUI(parent);
               UIManager.getDefaults().put("TabbedPane.selected", new ColorUIResource(alterColorHighlight(color)));
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
    * Class to store color match data
    *
    * @author     Chris Samuels
    * @created    24 February 2003
    */
   static class ColorEntry
   {
      Color color;
      RE re;


      ColorEntry(RE re, Color color)
      {
         this.re = re;
         this.color = color;
      }
   }

}

