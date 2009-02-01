package nested.manager ;

import java.util.TreeMap ;
import java.io.* ;
import org.gjt.sp.util.SyntaxUtilities ;
import java.util.Collection ;

public class NestedWriter {
	
	private File file ;
	
	public NestedWriter( ) {
		File home = nested.Plugin.getHome() ;
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
		
		Collection<NestedObject> objects = map.values( ) ;
		for( NestedObject o : objects ){
			out.write( o.getMode() ) ; 
			out.write( "," ) ;
			out.write( o.getSubMode() ) ;
			out.write( "," ) ;
			out.write( SyntaxUtilities.getColorHexString(o.getColor()) ) ;
			out.write( "\n" ) ;
		}
		out.flush( ) ;
		out.close( ) ;
	}
	
}
