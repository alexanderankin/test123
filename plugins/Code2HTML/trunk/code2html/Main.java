package code2html; 

import java.io.*  ;
import org.gjt.sp.util.* ;
import org.gjt.sp.jedit.* ;
import java.net.Socket ;
import java.net.InetAddress ;

public class Main {
	
	public static void main( String[] args ){
		String portFile = "/home/romain/.jedit/server" ;

		for( int i=0; i<args.length; i++){
			System.out.println( "args["+i+"] = "+  args[i] + "\n" ) ;
		}
		
		if( new File(portFile).exists()) {
			try {
				BufferedReader in = new BufferedReader(new FileReader(portFile));
				String check = in.readLine();
				if(!check.equals("b"))
					throw new Exception("Wrong port file format");

				int port = Integer.parseInt(in.readLine());
				int key = Integer.parseInt(in.readLine());

				Socket socket = new Socket(InetAddress.getByName("127.0.0.1"),port);
				DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());
				out.writeInt(key);

				try{
					out.writeUTF( makeScript( args ) );
				} catch( Exception e ){}

				Log.log(Log.DEBUG,Main.class,"Waiting for server");
				// block until its closed
				try {
					socket.getInputStream().read();
				} catch(Exception e) { }

				in.close();
				out.close();

				System.exit(0);
			}
			catch(Exception e) {}
	}
	
}

public static String makeScript( String[] args ) throws Exception {
	
	File temp = File.createTempFile("highlight", ".bsh");
  
  // Write to temp file
  BufferedWriter out = new BufferedWriter(new FileWriter(temp));
  out.write( "View v = new View( null , new View.ViewConfig() ) ; \n" ) ;
 	out.write( "code2html.Server.highlight( v, \"/tmp/test2.R\", \"/tmp/test2.html\", \"htmlcss\" ) ;\n" ) ;
	out.close( ) ;
	
	StringBuffer buf = new StringBuffer() ;
	buf.append( "v = new View( null , new View.ViewConfig() ) ;" ) ; buf.append( SEP ) ; 
	buf.append( "BeanShell.runScript(v,\"") 
			.append(StandardUtilities.charsToEscapes( temp.getAbsolutePath() ) )
			.append("\",null, true );\n"); 
	buf.append( SEP ) ; 
	buf.append( "socket.close()" ) ; buf.append( SEP ) ;	  
	return buf.toString( ) ;   
}

private static final String SEP = "\n" ;

}


