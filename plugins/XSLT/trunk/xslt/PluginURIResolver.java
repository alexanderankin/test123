package xslt;

import org.gjt.sp.util.Log;
import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import org.apache.xml.utils.*;
import org.xml.sax.*;

public class PluginURIResolver implements URIResolver
{

	public Source resolve(String href, String baseID) throws javax.xml.transform.TransformerException
	{
		Log.log(Log.DEBUG, this, "Resolving from " + href + " to " + baseID);
		href = SystemIDResolver.getAbsoluteURI(href, baseID);
		Source source = new SAXSource(new InputSource(href));
		return source;
	}

}
