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
package projectviewer;

import java.util.Enumeration;

/**
 * A walker that traverses the a project artifact tree.
 */
public class ArtifactTreeWalker {

   public final static int EVAL_CHILDREN = 0;
   public final static int SKIP_CHILDREN = 1;

   private ProjectArtifact artifact;
   private Evaluator eval;

   /**
    * Create a new <code>ArtifactTreeWalker</code>.
    */
   public ArtifactTreeWalker(Evaluator anEval) {
      eval = anEval;
   }

   /**
    * Set the root artifact to walk from.
    */
   public void setRootArtifact(ProjectArtifact anArtifact) {
      artifact = anArtifact;
   }

   /**
    * Start the walking.
    */
   public void walk() {
      walk(artifact);
   }

   /**
    * An interface for receiving node visits.
    */
   public static interface Evaluator {

      /**
       * Evaluate the given artifact node.  Returns {@link #EVAL_CHILDREN} if the
       * to proceed to evaluate this artifact's children (if any), or return
       * {@link #SKIP_CHILDREN} to skip the children evaluation of the given
       * children.  This value is ignored if the node is a leaf.
       */
      public int evaluate(ProjectArtifact node);

   }

   /**
    * Start walking from this artifact node.
    */
   private void walk(ProjectArtifact artifact) {
      int result = eval.evaluate(artifact);
      if (!artifact.isLeaf() && result == EVAL_CHILDREN) {
         Enumeration enum = artifact.children();
         while (enum.hasMoreElements())
            walk((ProjectArtifact) enum.nextElement());
      }
   }

}
