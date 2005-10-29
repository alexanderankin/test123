/*
 * ParentMember.java - 
 *
 * Copyright 2005 Robert McKinnon
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
package org.jedit.ruby.ast;

import java.util.Set;
import java.util.HashSet;

/**
 * @author robmckinnon at users.sourceforge.net
 */
public abstract class ParentMember extends Member {

    protected ParentMember(String name) {
        super(name);
    }

    public final Set<Method> getMethods() {
        final Set<Method> methods = new HashSet<Method>();

        visitChildren(new MemberVisitorAdapter() {
            public void handleMethod(Method method) {
                methods.add(method);
            }
        });

        return methods;
    }
}
