/*
 * Copyright (c) 2019, Dale Anson
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package sidekick.markdown;

import javax.swing.Icon;
import javax.swing.text.Position;
import sidekick.Asset;
import sidekick.util.Location;
import sidekick.util.SideKickElement;

/**
 * Nodes for the sidekick tree, this is a pretty plain implementation of Asset.
 */
public class Node extends Asset implements Comparable, SideKickElement {

    private int level = 0;
    private Icon icon = null;
    private Location startLocation = new Location();
    private Location endLocation = new Location();
    private Position startPosition = new Position() {
        public int getOffset() {
            return 0;
        }
    };
    private Position endPosition = new Position() {
        public int getOffset() {
            return 0;
        }
    };
    
    public Node() {
        super("");
    }
    
    public Node(String name) {
        super(name);
    }
    
    /**
     * The 'level' is used to create the tree hierarchy, so level 3 headers fall under
     * level 2 headers, for example.
     */
    public void setLevel(int level) {
        this.level = level;   
    }
    
    public int getLevel() {
        return level;   
    }

    public void setStartLocation(Location start) {
        startLocation = start;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setEndLocation(Location end) {
        endLocation = end;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Position p) {
        startPosition = p;
        start = p;
    }
    
    public Position getEndPosition() {
        return endPosition;
    }

    public void setEndPosition( Position p ) {
        endPosition = p;
        end = p;
    }
    
    public void setIcon(Icon icon) {
        this.icon = icon;   
    }

    public Icon getIcon() {
        return icon;   
    }
    
    public String toString() {
        return name; //getName() + '|' + getStartLocation() + '|' + getEndLocation() + '|' + getStartPosition().getOffset() + '|' + getEndPosition().getOffset();   
    }
    
    public String getShortString() {
        return name;   
    }
    
    public String getLongString() {
        return name + ": " + getStartLocation() + ":" + getEndLocation();   
    }
    
    public int compareTo(Object o) {
        return toString().compareToIgnoreCase(o.toString());
    }
}
