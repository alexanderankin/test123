
package sidekick.java.node;

import java.util.*;


// an extension of TigerNode for a Class
public class ClassNode extends TigerNode {

    private String typeParams = null;

    public ClassNode( String name, int modifiers ) {
        super( name, modifiers );
    }

    public int getOrdinal() {
        return CLASS;
    }

    public void setTypeParams( String p ) {
        typeParams = p;
    }

    public String getTypeParams() {
        return typeParams == null ? "" : typeParams;
    }


    public void setExtendsList( List list ) {
        if ( list == null )
            return ;
        for ( Iterator it = list.iterator(); it.hasNext(); ) {
            Type t = ( Type ) it.next();
            ExtendsNode en = new ExtendsNode( t );

            addChild( en );
        }
    }


    public void setImplementsList( List list ) {
        if ( list == null )
            return ;
        for ( Iterator it = list.iterator(); it.hasNext(); ) {
            Type t = ( Type ) it.next();
            ImplementsNode in = new ImplementsNode( t );

            addChild( in );
        }
    }
    

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( super.toString() );
        if ( getTypeParams() != null )
            sb.append( getTypeParams() );

        StringBuffer extendsList = new StringBuffer();
        StringBuffer implementsList = new StringBuffer();
        List children = getChildren();
        if ( children != null ) {
            for ( Iterator it = children.iterator(); it.hasNext(); ) {
                TigerNode child = ( TigerNode ) it.next();
                if ( child instanceof ExtendsNode )
                    extendsList.append( child.getName() );
                else if ( child instanceof ImplementsNode ) {
                    if ( implementsList.length() > 0 )
                        implementsList.append( ", " );
                    implementsList.append( child.getName() );
                }
            }
        }
        if ( extendsList.length() > 0 )
            sb.append( " extends " ).append( extendsList.toString() );
        if ( implementsList.length() > 0 )
            sb.append( " implements " ).append( implementsList.toString() );
        return sb.toString();
    }
}


