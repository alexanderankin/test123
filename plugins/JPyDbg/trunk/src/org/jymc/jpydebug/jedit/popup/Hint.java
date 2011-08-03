/*
 * Hint.java
 * Copyright (c) 2001, 2002 CodeAid team
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


package org.jymc.jpydebug.jedit.popup;

import java.awt.Dimension;
import java.awt.Point;
import org.jymc.jpydebug.*;



public interface Hint
{
    public Dimension getSize();


    public void setMember(PythonSyntaxTreeNode node);


    public void show(int x, int y);


    public void show(Point pt);


    public void hide();
    
    public boolean isVisible();
}

