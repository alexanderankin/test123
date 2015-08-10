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

import com.thaiopensource.relaxng.input.dtd.DtdInputFormat;
import com.thaiopensource.relaxng.input.dtd.Converter;
import com.thaiopensource.relaxng.input.dtd.ResolverEntityManager;

import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.input.InputFormat;
import com.thaiopensource.relaxng.output.common.ErrorReporter;
import com.thaiopensource.relaxng.translate.util.AbsoluteUriParam;
import com.thaiopensource.relaxng.translate.util.AbstractParam;
import com.thaiopensource.relaxng.translate.util.InvalidParamValueException;
import com.thaiopensource.relaxng.translate.util.InvalidParamsException;
import com.thaiopensource.relaxng.translate.util.NCNameParam;
import com.thaiopensource.relaxng.translate.util.NmtokenParam;
import com.thaiopensource.relaxng.translate.util.Param;
import com.thaiopensource.relaxng.translate.util.ParamFactory;
import com.thaiopensource.relaxng.translate.util.ParamProcessor;
import com.thaiopensource.resolver.Resolver;
import com.thaiopensource.resolver.Input;
import com.thaiopensource.resolver.Identifier;
import com.thaiopensource.resolver.ResolverException;
import com.thaiopensource.util.Localizer;
import com.thaiopensource.xml.dtd.om.Dtd;
import com.thaiopensource.xml.dtd.parse.DtdParserImpl;
import com.thaiopensource.xml.dtd.parse.ParseException;
import com.thaiopensource.xml.em.UriEntityManager;
import com.thaiopensource.xml.em.OpenEntity;
import com.thaiopensource.xml.util.Naming;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;


/* copy of http://jing-trang.googlecode.com/svn/tags/V20091111/mod/convert-from-dtd/src/main/com/thaiopensource/relaxng/input/dtd/DtdInputFormat.java
 * It is necessary in order to use an EntityResolverWrapper to resolve the input if it's an open buffer
 */
public class BufferDtdInputFormat extends DtdInputFormat {
  static private class NamespaceDeclParamFactory implements ParamFactory {
    private final Map<String, String> prefixMap;

    NamespaceDeclParamFactory(Map<String, String> prefixMap) {
      this.prefixMap = prefixMap;
    }

    public Param createParam(String name) {
      if (!name.startsWith("xmlns:"))
        return null;
      final String prefix = name.substring(6);
      if (!Naming.isNcname(prefix))
        return null;
      return new AbsoluteUriParam() {
        public void setAbsoluteUri(String uri) {
          prefixMap.put(prefix, uri);
        }
      };
    }
  }

  static private abstract class DeclPatternParam extends AbstractParam {
    private final Localizer localizer;

    DeclPatternParam(Localizer localizer) {
      this.localizer = localizer;
    }

    public void set(String value) throws InvalidParamValueException {
      if (value.indexOf('%') < 0)
        throw new InvalidParamValueException(localizer.message("no_percent"));
      if (value.lastIndexOf('%') != value.indexOf('%'))
        throw new InvalidParamValueException(localizer.message("multiple_percent"));
      if (!Naming.isNcname(value.replace('%', 'x')))
        throw new InvalidParamValueException(localizer.message("not_ncname_with_percent"));
      setDeclPattern(value);
    }

    abstract void setDeclPattern(String pattern);
  }

  public SchemaCollection load(String uri, String[] params, String outputFormat, ErrorHandler eh, Resolver resolver)
          throws InvalidParamsException, IOException, SAXException {
    final ErrorReporter er = new ErrorReporter(eh, DtdInputFormat.class);
    final BufferDtdConverter.Options options = new BufferDtdConverter.Options();
    if ("xsd".equals(outputFormat)) {
      options.inlineAttlistDecls = true;
      options.generateStart = false;
    }
    ParamProcessor pp = new ParamProcessor();
    pp.declare("inline-attlist",
               new AbstractParam() {
                 public void set(boolean value) {
                   options.inlineAttlistDecls = value;
                 }
               });
    pp.declare("xmlns",
               new AbsoluteUriParam() {
                 public void set(String value) throws InvalidParamValueException {
                   if (value.equals(""))
                    setAbsoluteUri(value);
                   else
                    super.set(value);
                 }

                 protected void setAbsoluteUri(String value) {
                   options.defaultNamespace = value;
                 }
               });
    pp.declare("any-name",
               new NCNameParam() {
                 protected void setNCName(String value) {
                   options.anyName = value;
                 }
               });
    pp.declare("strict-any",
               new AbstractParam() {
                 public void set(boolean value) {
                   options.strictAny = value;
                 }
               });
    pp.declare("annotation-prefix",
               new NCNameParam() {
                 protected void setNCName(String value) {
                   options.annotationPrefix = value;
                 }
               });
    pp.declare("colon-replacement",
               new NmtokenParam() {
                 protected void setNmtoken(String value) {
                   options.colonReplacement = value;
                 }
               });
    pp.declare("generate-start",
               new AbstractParam() {
                 public void set(boolean value) {
                   options.generateStart = value;
                 }
               });
    pp.declare("element-define",
               new DeclPatternParam(er.getLocalizer()) {
                 void setDeclPattern(String pattern) {
                   options.elementDeclPattern = pattern;
                 }
               });
    pp.declare("attlist-define",
               new DeclPatternParam(er.getLocalizer()) {
                 void setDeclPattern(String pattern) {
                   options.attlistDeclPattern = pattern;
                 }
               });
    pp.setParamFactory(new NamespaceDeclParamFactory(options.prefixMap));
    pp.process(params, eh);
    try {
    	// ELL: replaced UriEntityManager with ResolverEntityManager and called em.open()
      ResolverEntityManager em = new ResolverEntityManager(resolver);
      Dtd dtd = new DtdParserImpl().parse(em.open(uri), em);
        // end ELL
      try {
        return new BufferDtdConverter(dtd, er, options).convert();
      }
      catch (ErrorReporter.WrappedSAXException e) {
        throw e.getException();
      }
      	  
    }
    catch (ParseException e) {
      throw new SAXParseException(e.getMessageBody(), null, e.getLocation(), e.getLineNumber(), e.getColumnNumber());
    }
  }
}
