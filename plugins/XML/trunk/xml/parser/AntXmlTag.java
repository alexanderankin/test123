
package xml.parser;

import java.util.*;
import javax.swing.text.Position;
import org.xml.sax.Attributes;

public class AntXmlTag extends XmlTag {

    String realName = null;

    public AntXmlTag(String name, String namespace, Position start, Attributes attributes) {
        super(name, namespace, start, attributes);
        String idName = null;
        String idValue = null;

        Map<String, String> attrs = new LinkedHashMap<String, String>();

        for (int i = 0; i < attributes.getLength(); i++) {
            String aname = attributes.getQName(i);
            String value = attributes.getValue(i);
            attrs.put(aname, value);

            if (attributes.getLocalName(i).equalsIgnoreCase("id") || attributes.getType(i).equals("ID")) {
                idName = aname;
                idValue = value;
            }
        }

        StringBuffer buf = new StringBuffer();
        buf.append(name).append(' ');
        String realName = attrs.get("name");
        if (realName != null) {
            buf.append("name=\"");
            buf.append(realName);
            buf.append("\" ");
        }
        for (String key : attrs.keySet()) {
            if ("name".equals(key)) {
                continue;
            }
            String value = attrs.get(key);
            buf.append(key);
            buf.append("=\"");
            buf.append(value);
            buf.append("\" ");
        }

        attributeString = buf.toString();

        if (idName == null) {
            idAttributeString = name;
        } else {
            idAttributeString = name + ' ' + idName + "=\"" + idValue + '"';
        }
    }
}
