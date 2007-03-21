/*
 * Copyright (c) 2007, Jeffrey Hoyt. All rights reserved.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 */
package de.hunsicker.jalopy.plugin.jedit;

import beauty.beautifiers.Beautifier;
import beauty.parsers.ParserException;
import beauty.parsers.java.*;
import de.hunsicker.jalopy.*;
import org.gjt.sp.util.*;
import java.io.*;
import java.util.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class JalopyBeautifier extends Beautifier
{
    private static JavaParser parser = null;

    /**
     * DOCUMENT ME!
     *
     * @param text DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ParserException DOCUMENT ME!
     */
    public String beautify( String text ) throws ParserException
    {
        try
        {
            /*
             *  get Beautifier
             */
            Jalopy newJalopy = JEditPlugin.getJalopyInstance(  );

            /*
             *  the beautifier only works on files, so I'm going to create a temp file
             *  beautify it, then read it back in.
             */
            Long time = new Long( System.currentTimeMillis(  ) );
            File f = File.createTempFile( time.toString(  ), ".java" );
            FileWriter out = new FileWriter( f );
            out.write( text );
            out.close(  );
            /*
             *  do the formatting
             */
            newJalopy.setInput( f );
            newJalopy.setOutput( f );
            newJalopy.format(  );

            /*
             *  read the text back in
             */
            int buffersize = 4 * 1024;
            char[] buffer = new char[buffersize];
            int bytesRead = 0;
            FileReader in = new FileReader( f );
            StringBuilder ret = new StringBuilder(  );
            bytesRead = in.read( buffer, 0, buffersize );

            while ( bytesRead != -1 )
            {
                ret.append( buffer, 0, bytesRead );
                bytesRead = in.read( buffer, 0, buffersize );
            }

            /*
             *  delete temp file and return
             */
            return ret.toString(  );
        }
        catch ( Exception e )
        {
            throw new ParserException( e );
        }
    }
}

