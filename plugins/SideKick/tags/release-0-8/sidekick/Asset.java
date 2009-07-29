/*
 * Asset.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2005 Slava Pestov
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

package sidekick;

import javax.swing.text.Position;

/**
 * A block of code within a file.  Assets correspond to nodes in the 
 * Structure Browser and folds in the SideKick folding mode.
 */
public abstract class Asset implements IAsset
{
        //{{{ Instance variables
        protected String name;
        protected Position start, end;
        //}}}

        //{{{ Asset constructor
        public Asset(String name)
        {
                this.name = name;
        } //}}}

        //{{{ getName() method
        /**
         * Returns the name of the Asset.
         */
        public String getName()
        {
                return name;
        } //}}}

        //{{{ getStart() method
        /**
         * Returns the starting position.
         */
        public Position getStart()
        {
                return start;
        } //}}}

        //{{{ getEnd() method
        /**
         * Returns the end position.
         */
        public Position getEnd()
        {
                return end;
        } //}}}
	

        //{{{ setName() method
        /**
         * Set the name of the asset
         */
        public void setName(String name)
        {
                this.name = name;
        } //}}}
        
        //{{{ setStart() method
        /**
         * Set the start position
         */
        public void setStart(Position start)
        {
                this.start = start;
        } //}}}
        
        //{{{ setEnd() method
        /**
         * Set the end position
         */
        public void setEnd(Position end)
        {
                this.end = end;
        } //}}}
}
