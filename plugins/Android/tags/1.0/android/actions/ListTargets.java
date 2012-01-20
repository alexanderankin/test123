package android.actions;

import java.io.*;
import javax.swing.SwingWorker;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

// list Android targets
// assumes "android" is in your path
public class ListTargets implements Command {

    private View view;

    public void execute( View view ) {
        this.view = view;
        Runner runner = new Runner();
        runner.execute();
    }

    class Runner extends SwingWorker<String, Object> {
        @Override
        public String doInBackground() {
            try {
                Process p = Runtime.getRuntime().exec( "android list targets" );
                BufferedReader in = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
                StringBuilder targetList = new StringBuilder( "<html><h3>Android Target List</h3>" );
                while ( true ) {
                    String line = in.readLine();
                    if ( line == null ) {
                        break;
                    }
                    if ( line.startsWith( "id:" ) ) {
                        String id = line.substring(4, line.indexOf( ' ', 4 ) );
                        String name = line.substring( line.indexOf( ' ', 4 ) + " or ".length() );
                        name = name.replaceAll( "\"", "" );
                        targetList.append( id ).append( " " ).append( name ).append( "<br>" );
                    }
                }
                return targetList.toString();
            } catch ( Exception e ) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        public void done() {
            try {
                String answer = get();
                if ( answer.startsWith( "Error" ) ) {
                    Util.showError( view, jEdit.getProperty( "android.Error", "Error" ), answer );
                } else {
                    Util.showMessage( view, jEdit.getProperty( "android.Android_Targets", "Android Targets" ), answer );
                }
            } catch ( Exception e ) {
                Util.showError( view, jEdit.getProperty( "android.Error", "Error" ), e.getMessage() );
            }
        }
    }
}