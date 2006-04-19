/*
 * ModeUtilities.java
 * Copyright (c) 1998, 1999, 2000, 2001, 2002 Slava Pestov
 * Copyright (c) 2002 Andre Kaplan
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


package code2html;

import java.io.*;
import java.net.URL;
import java.util.Vector;

import com.microstar.xml.*;

import org.gjt.sp.util.Log;

import code2html.syntax.ParserRuleSet;
import code2html.syntax.TokenMarker;
import code2html.syntax.XModeHandler;


public class ModeUtilities
{
    private static Vector modes = new Vector(50);


    private ModeUtilities() {}


    //{{{ loadMode() method
    /**
     * Loads an XML-defined edit mode from the specified reader.
     * @param mode The edit mode
     */
    /* package-private */ static void loadMode(Mode mode)
    {
        Object fileName = mode.getProperty("file");

        Log.log(Log.NOTICE,ModeUtilities.class,"Loading edit mode " + fileName);

        XmlParser parser = new XmlParser();
        XModeHandler xmh = new XModeHandler(parser,mode.getName(),fileName.toString());
        parser.setHandler(xmh);
        try
        {
            Reader grammar;
            if(fileName instanceof URL)
            {
                grammar = new BufferedReader(
                    new InputStreamReader(
                    ((URL)fileName).openStream()));
            }
            else
            {
                grammar = new BufferedReader(new FileReader(
                    (String)fileName));
            }

            parser.parse(null, null, grammar);
        }
        catch (Throwable e)
        {
            Log.log(Log.ERROR, ModeUtilities.class, e);

            if (e instanceof XmlException)
            {
                XmlException xe = (XmlException) e;
                int line = xe.getLine();
                String message = xe.getMessage();

                Object[] args = { fileName, new Integer(line), message };
                Log.log(
                    Log.ERROR, ModeUtilities.class,
                    Main.getProperty("xmode-error" + ".message", args)
                );
            }

            // give it an empty token marker to avoid problems
            TokenMarker marker = new TokenMarker();
            marker.addRuleSet("MAIN",new ParserRuleSet("MAIN",mode));
            mode.setTokenMarker(marker);
        }
    } //}}}


    //{{{ addMode() method
    /**
     * Do not call this method. It is only public so that classes
     * in the org.gjt.sp.jedit.syntax package can access it.
     * @param mode The edit mode
     */
    public static void addMode(Mode mode)
    {
        Log.log(Log.DEBUG,ModeUtilities.class,"Adding edit mode "
            + mode.getName());

        modes.addElement(mode);
    } //}}}


    //{{{ getMode() method
    /**
     * Returns the edit mode with the specified name.
     * @param name The edit mode
     */
    public static Mode getMode(String name)
    {
        for(int i = 0; i < modes.size(); i++)
        {
            Mode mode = (Mode)modes.elementAt(i);
            if(mode.getName().equals(name))
                return mode;
        }
        return null;
    } //}}}


    //{{{ getModes() method
    /**
     * Returns an array of installed edit modes.
     */
    public static Mode[] getModes()
    {
        Mode[] array = new Mode[modes.size()];
        modes.copyInto(array);
        return array;
    } //}}}
}

