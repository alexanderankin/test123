/*
Copyright (c) 2001-2003 Thai Open Source Software Center Ltd
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

    Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.

    Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in
    the documentation and/or other materials provided with the
    distribution.

    Neither the name of the Thai Open Source Software Center Ltd nor
    the names of its contributors may be used to endorse or promote
    products derived from this software without specific prior written
    permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

_________ END NOTICE ___________

modifications Copyright (c) 2010 Eric Le Lay, under the same licence terms
*/
package xml.translate;

import com.thaiopensource.relaxng.input.parse.sax.SAXParseInputFormat;

import com.thaiopensource.relaxng.input.parse.ParseInputFormat;
import com.thaiopensource.relaxng.input.parse.ElementAnnotationBuilderImpl;
import com.thaiopensource.relaxng.input.parse.CommentListImpl;
import com.thaiopensource.relaxng.input.parse.AnnotationsImpl;
import com.thaiopensource.relaxng.parse.Parseable;
import com.thaiopensource.relaxng.parse.sax.SAXParseable;
import com.thaiopensource.relaxng.edit.Pattern;
import com.thaiopensource.relaxng.edit.NameClass;
import com.thaiopensource.relaxng.edit.SourceLocation;
import com.thaiopensource.resolver.xml.sax.SAXResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.sax.SAXSource;

import java.io.IOException;
import org.gjt.sp.util.Log;

/* copy of http://jing-trang.googlecode.com/svn/tags/V20091111/mod/rng-schema/src/main/com/thaiopensource/relaxng/input/parse/sax/SAXParseInputFormat.java
 * some modifications where made to resolve the input first, so that live contents of an open buffer is used.
 */
public class BufferSAXParseInputFormat extends SAXParseInputFormat {
  public BufferSAXParseInputFormat() {
    super();
  }

  public Parseable<Pattern, NameClass, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl>
  	  makeParseable(InputSource in, SAXResolver resolver, ErrorHandler eh)
  	  throws SAXException
  {
  	  // ELL : resolve the input first
 	 InputSource resolved = null;
 	 try {
 	 	  resolved = resolver.open(in);
 	 } catch(IOException ioe) {
 	 	 Log.log(Log.ERROR,this,"unable to resolve "+in.getSystemId());
 	 	 Log.log(Log.ERROR,this,ioe);
 	 	 resolved = in;
 	 }
 	 // ELL : end mods
 	 	 
    return super.makeParseable(resolved,resolver,eh);
  }
}
