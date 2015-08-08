/*
Copyright (c) 2006, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
this list of conditions and the following disclaimer in the documentation 
and/or other materials provided with the distribution.
* Neither the name of the <ORGANIZATION> nor the names of its contributors 
may be used to endorse or promote products derived from this software without 
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR 
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package sidekick.util;

import sidekick.Asset;
import javax.swing.Icon;

/**
 * danson: simple concrete implementation of an Asset that wraps some sort of
 * SideKickElement.  A SideKickElement provides both Location and Position
 * information.
 */
public class SideKickAsset extends Asset {
    
    private SideKickElement element = null;
    private String name = "";
    private String longString = null;
    
    public SideKickAsset(SideKickElement element) {
        super(element.toString());    
        this.element = element;
        this.name = element.toString();
    }
    
    public SideKickElement getElement() {
        return element;   
    }
    
	public Icon getIcon() { 
        return null; 
    }

    public String getShortString() { 
        return name; 
    }
    
    public String toString() {
        return name;   
    }

	public String getLongString() {
        return longString == null ? name : longString;
    }
    
    public void setLongString(String s) {
        longString = s;   
    }
}
