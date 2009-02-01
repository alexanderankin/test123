package nested.manager ;

import java.util.TreeMap ;
import java.io.* ;
import org.gjt.sp.util.SyntaxUtilities ;
import org.gjt.sp.jedit.jEdit ;
import java.util.Collection ;

public class NestedWriter {
	
	private File file ;
	
	public NestedWriter( File home ) {
		if( ! home.exists() ){
			home.mkdir() ;
		}
		file = new File( home , "settings.txt" ) ;
	}
	
	public void saveMap( TreeMap<String,NestedObject> map ){
		if( map == null || map.size( ) == 0 ) return ; 
		PrintWriter out = null ; 
		try{ 
			out = new PrintWriter(new BufferedWriter(new FileWriter( file )));
		} catch( Exception e ){
			return ; 
		}
		
		for( NestedObject o : map.values( ) ){
			String hex = jEdit.getProperty( "view.bgColor" ) ; 
			try{
				hex = SyntaxUtilities.getColorHexString( o.getColor( ) ) ; 
			} catch( Exception e ){}
			
			out.write( o.getMode() ) ; 
			out.write( "," ) ;
			out.write( o.getSubMode() ) ;
			out.write( "," ) ;
			out.write( hex ) ;
			out.write( "\n" ) ;
		}
		out.flush( ) ;
		out.close( ) ;
	}
	
}
