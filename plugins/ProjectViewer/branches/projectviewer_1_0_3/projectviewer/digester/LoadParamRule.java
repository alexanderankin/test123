/*
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
package projectviewer.digester;

import java.util.*;
import org.apache.commons.digester.*;
import org.xml.sax.Attributes;
import projectviewer.FileView;


/**
 * A rule to load a parameter for a {@Link FileView}.
 *
 * @see FileView#setInitParameter(String name, String value).
 */
public class LoadParamRule extends Rule
{

   /**
    * Create a new <code>LoadParamRule</code>.
    */
   public LoadParamRule(Digester aDigester)
   {
      super(aDigester);
   }

   /**
    * Process the beginning of an element.
    */
   public void begin(Attributes atts) throws Exception
   {
      super.begin(atts);
      FileView view = (FileView) digester.peek();
      view.setInitParameter(atts.getValue("name"), atts.getValue("value"));
   }

}
