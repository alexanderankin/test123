/*
 * Member.java - Ruby file structure member
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
package org.jedit.ruby;

import java.util.*;

/**
 * Ruby file structure member
 * @author robmckinnon at users.sourceforge.net
 */
public abstract class Member implements Comparable<Member> {

    private static final Member[] EMPTY_MEMBER_ARRAY = new Member[0];
    private String receiverName;
    private int parentCount;
    private List<Member> parentPath;

    private Member parentMember;
    private List<Member> childMembers;

    private String namespace;
    private String name;
    private String shortName;
    private int startOffset;
    private int endOffset;

    public Member(String name, int startOffset) {
        this(name, startOffset, startOffset);
    }

    public Member(String name, int startOffset, int endOffset) {
        parentCount = -1;
        this.name = name;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.shortName = (new StringTokenizer(name, " (")).nextToken();
    }

    public int compareTo(Member member) {
        return getFullName().compareTo(member.getFullName());
    }

    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }

    public abstract void accept(Member.Visitor visitor);

    public void visitChildren(Member.Visitor visitor) {
        if (hasChildMembers()) {
            for (Member member : getChildMembersAsList()) {
                member.accept(visitor);
            }
        }
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setReceiver(String receiverName) {
        this.receiverName = receiverName;
        if(name.startsWith(receiverName)) {
            name = name.substring(name.indexOf('.') + 1);
        }
    }

    /**
     * Returns member name including any
     * namespace or receiver prefix.
     */
    public String getFullName() {
        if(namespace == null) {
            if(receiverName == null) {
                return name;
            } else {
                return receiverName + '.' + name;
            }
        } else {
            return namespace + name;
        }
    }

    /**
     * Returns member name excluding
     * any namespace or receiver prefix.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns member name excluding
     * any namespace and excluding
     * any parameter list or class
     * extended from.
     */
    public String getShortName() {
        return shortName;
    }

    public String getLowerCaseName() {
        return name;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    /**
     * Returns true if supplied object
     * is a member with the same display name
     * and parent member as this member.
     */
    public boolean equals(Object obj) {
        boolean equal = false;
        if(obj instanceof Member) {
            Member member = ((Member) obj);
            boolean displayNamesEqual = getFullName().equals(member.getFullName());

            if (displayNamesEqual) {
               if (hasParentMember()) {
                   equal = parentMember.equals(member.getParentMember());
               } else {
                   equal = true;
               }
            }
        }
        return equal;
    }

    public int hashCode() {
        int code = getFullName().hashCode();
        if (hasParentMember()) {
            code += getParentMember().hashCode();
        }
        return code;
    }

    public String toString() {
        return getFullName();
    }

    public boolean hasChildMembers() {
        return childMembers != null;
    }

    public Member[] getChildMembers() {
        if(hasChildMembers()) {
            return childMembers.toArray(EMPTY_MEMBER_ARRAY);
        } else {
            return null;
        }
    }

    public List<Member> getChildMembersAsList() {
        return childMembers;
    }

    public void addChildMember(Member member) {
        if(childMembers == null) {
            childMembers = new ArrayList<Member>();
        }
        childMembers.add(member);
        member.setParentMember(this);
    }

    public boolean hasParentMember() {
        return parentMember != null && !(parentMember instanceof Member.Root);
    }

    public Member getTopMostParent() {
        if(hasParentMember()) {
            return getTopMostParentOrSelf(getParentMember());
        } else {
            return null;
        }
    }

    private Member getTopMostParentOrSelf(Member member) {
        if (member.hasParentMember()) {
            return getTopMostParentOrSelf(member.getParentMember());
        } else {
            return member;
        }
    }

    /**
     * Returns list of member parent hierarchy
     * starting with top most parent, ending
     * with the member itself.
     *
     * If this member has no parent, the list only
     * contains this member.
     */ 
    public List<Member> getMemberPath() {
        if (parentPath == null) {
            List<Member> path = new ArrayList<Member>();
            Member current = this;
            path.add(current);

            while(current.hasParentMember()) {
                current = current.getParentMember();
                path.add(current);
            }

            Collections.reverse(path);
            parentPath = path;
        }

        return parentPath;
    }

    public int getParentCount() {
        if (parentCount == -1) {
            parentCount = getMemberPath().size() - 1;
        }

        return parentCount;
    }

    public Member getParentMember() {
        return parentMember;
    }

    public void setParentMember(Member parentMember) {
        this.parentMember = parentMember;
    }

    public static interface Visitor {
        public void handleModule(Module module);
        public void handleClass(Class classMember);
        public void handleMethod(Method method);
        public void handleWarning(Warning warning);
        public void handleError(Error warning);
    }

    public static class VisitorAdapter implements Visitor {
        public void handleModule(Module module) {
        }

        public void handleClass(Class classMember) {
        }

        public void handleMethod(Method method) {
        }

        public void handleWarning(Warning warning) {
        }

        public void handleError(Error warning) {
        }
    }

    public static abstract class ParentMember extends Member {
        protected ParentMember(String name, int startOffset) {
            super(name, startOffset);
        }

        public Set<Method> getMethods() {
            final Set<Method> methods = new HashSet<Method>();

            visitChildren(new VisitorAdapter() {
                public void handleMethod(Method method) {
                    methods.add(method);
                }
            });

            return methods;
        }
    }

    public static class Module extends ParentMember {
        public Module(String name, int startOffset) {
            super(name, startOffset);
        }

        public void accept(Visitor visitor) {
            visitor.handleModule(this);
        }
    }

	public static class Class extends ParentMember {
        public Class(String name, int startOffset) {
            super(name, startOffset);
        }

        public void accept(Visitor visitor) {
            visitor.handleClass(this);
        }

    }

	public static class Method extends Member {
        private String filePath;
        private String fileName;

        public Method(String name, String filePath, String fileName, int startOffset) {
            super(name, startOffset);
            this.filePath = filePath;
            this.fileName = fileName;
        }

        public void accept(Visitor visitor) {
            visitor.handleMethod(this);
        }

        public String getFilePath() {
            return filePath;
        }

        public String getFileName() {
            return fileName;
        }

        public int compareTo(Member member) {
            int comparison = super.compareTo(member);
            if(comparison == 0 && member instanceof Method) {
                Method method = (Method)member;
                comparison = fileName.compareTo(method.fileName);
            }
            return comparison;
        }
    }

    public static class Root extends Member {
        public Root(int endOffset) {
            super("root", 0, endOffset);
        }

        public void accept(Visitor visitor) {
        }
    }

    public static class Warning extends Member {
        public Warning(String message, int startOffset, int endOffset) {
            super(message, startOffset, endOffset);
        }

        public void accept(Visitor visitor) {
            visitor.handleWarning(this);
        }
    }

    public static class Error extends Member {
        public Error(String message, int startOffset, int endOffset) {
            super(message, startOffset, endOffset);
        }

        public void accept(Visitor visitor) {
            visitor.handleError(this);
        }
    }
}