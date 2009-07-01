package com.addictedtor.infonode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowManager;

public class PerspectiveTools {

	private static final String FLOATING = "floating" ;
	
	private static HashMap<String,String> directions; 
	static{
		directions = new HashMap<String, String>(); 
		directions.put( "left", "Left" ) ;
		directions.put( "right", "Right" ) ;
		directions.put( "up", "Up" ) ;
		directions.put( "bottom", "Down" ) ;
	}
	
	public static void createDefaultPerspective(File file){

		String[] dockables = DockableWindowManager.getRegisteredDockableWindows() ;
		HashMap<String,Vector<String>> data = new HashMap<String,Vector<String>>() ;
		
		for( String dockable: dockables ){
			String position = jEdit.getProperty( dockable + ".dock-position" , FLOATING ) ;
			if( ! position.equals(FLOATING) ){
				if( !data.containsKey(position)){
					data.put( position, new Vector<String>() ) ;
				}
				data.get(position).add( dockable ) ;
			}
		}
		
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(file)));
			
			out.write( "<RootWindow>" ) ;
			
			Iterator<String> iterator = directions.keySet().iterator() ;
			while( iterator.hasNext()){
				String key = iterator.next() ;
				writeWindowBar( out, directions.get(key), data.get( key) ) ;
			}
			out.write( "  <JEditViewView title=\"frame0\" />\n" ) ;
			out.write( "</RootWindow>" ) ;
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private static void writeWindowBar(Writer out, String d, Vector<String> vector) throws IOException {
		if( vector == null || vector.size() == 0){
			out.write( "  <WindowBar direction=\""+ d + "\" />\n" ); 
		} else{
			out.write( "  <WindowBar direction=\""+ d + "\" >\n" );
			for( String s: vector ){
				out.write( "    <JEditDockableView title=\"" + s + "\" />\n") ;
			}
			out.write( "  </WindowBar>\n" );
		}
	}

}
