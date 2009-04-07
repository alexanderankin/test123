package nested.manager ;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NestedReader {
	
	File file ;
	private static final Pattern rx = Pattern.compile( "^([^,]*),([^,]*),([^,]*)$" ) ;
	
	public NestedReader( File home ){
		if( !home.exists() ){
			home.mkdirs() ;
		}
		file = new File( home , "settings.txt" ) ;
	}
	
	public TreeMap<String,NestedObject> getMap(){
		TreeMap<String,NestedObject> map = new TreeMap<String,NestedObject>() ;
		if( !file.exists() ) return map ;
		BufferedReader reader ; 
		try{
			reader = new BufferedReader( new FileReader( file ) ) ;
		} catch( Exception e ){
			e.printStackTrace() ;
			return map ;
		}
		String line ;
		Matcher m ;
		
		for(;;){
			try{
				line = reader.readLine( ) ;
			} catch( Exception e ){
				break ;
			}
			if( line == null ) break ;
			
			m = rx.matcher( line ) ;
			if( !m.matches( ) ){
				continue ;
			}
			String mode = m.group(1).replaceAll( " ", "" ) ;
			String submode = m.group(2).replaceAll( " ", "" ) ;
			String colorString = m.group(3).replaceAll( " ", "" ) ;
			Color color = null ;
			try{
				color = Color.decode( colorString ) ;
			} catch( Exception e ){}
			map.put( mode + "-" + submode, new NestedObject( mode, submode, color ) ) ;
		}
		return map ;
	}
	
}

