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
import projectviewer.*;
import projectviewer.views.defaultview.DefaultView;


/**
 * A rule to create a {@link FileView}.
 */
public class CreateFileViewRule extends ObjectCreateRule {

   /**
    * Create a new <code>CreateFileViewRule</code>.
    */
   public CreateFileViewRule(Digester aDigester) {
      super(aDigester, DefaultView.class.getName(), "type");
   }

   /**
    * Process the beginning of an element.
    */
   public void begin(Attributes atts) throws Exception {
      super.begin(atts);
      FileView view = (FileView) digester.peek();
      view.setName(atts.getValue("name"));
      LayeredRules layeredRules = new LayeredRules(digester.getRules());
      digester.setRules(layeredRules);
      view.initDigester(digester);
   }

   /**
    * Process the end of an element.
    */
   public void end() throws Exception {
      LayeredRules layeredRules = (LayeredRules) digester.getRules();
      for (Iterator i = layeredRules.rules().iterator(); i.hasNext();) {
         ((Rule) i.next()).finish();
      }
      digester.setRules(layeredRules.getParent());
      super.end();
   }

}
