package nested.manager ;

import java.io.File ;
import java.io.BufferedReader ;
import java.io.FileReader ;

import java.util.TreeMap ;

import java.util.regex.* ;
import java.awt.Color ;

public class NestedReader {
	
	File file ;
	private static final Pattern rx = Pattern.compile( "^([^,]*),([^,]*),([^,]*)$" ) ;
	
	public NestedReader(){
		file = new File( nested.Plugin.getHome(), "settings.txt" ) ;
	}
	
	public TreeMap<String,NestedObject> getMap(){
		TreeMap<String,NestedObject> map = new TreeMap<String,NestedObject>() ;
		BufferedReader reader ; 
		try{
			reader = new BufferedReader( new FileReader( file ) ) ;
		} catch( Exception e ){
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
			String colorString = m.group(1).replaceAll( " ", "" ) ;
			Color color = null ;
			try{
				color = Color.decode( colorString ) ;
			} catch( Exception e ){}
			map.put( mode + "-" + submode, new NestedObject( mode, submode, color ) ) ;
		}
		return map ;
	}
	
}

