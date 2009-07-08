
package code2html; 

import org.gjt.sp.jedit.View ;
import org.gjt.sp.jedit.jEdit ;
import org.gjt.sp.jedit.Buffer ;
import org.gjt.sp.jedit.ServiceManager ;

import code2html.services.ExporterProvider ;
import code2html.generic.GenericExporter ;

public class Server {

	public static void highlight( View v, String infile, String outfile, String exporter ){
			Buffer buf = jEdit.openFile( v, infile ) ;                                                     
 			GenericExporter ex = ( (ExporterProvider)ServiceManager.getService( "code2html.services.ExporterProvider" , exporter ) )
				.getExporter( buf, v.getTextArea().getPainter().getStyles(), null ) ;
 			ex.getDocumentBuffer().save( v, outfile ) ;
	}
	
}
