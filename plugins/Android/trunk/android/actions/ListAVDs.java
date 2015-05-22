package android.actions;

import java.io.*;
import javax.swing.SwingWorker;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

// list Android AVDs
// assumes "android" is in your path
public class ListAVDs implements Command {

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
                String sdkPath = jEdit.getProperty("android.sdk.path", "");
                if (!sdkPath.isEmpty()) {
                    sdkPath += "/tools/";   
                }
                Process p = Runtime.getRuntime().exec( sdkPath + "android list avd" );
                BufferedReader in = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
                StringBuilder avdList = new StringBuilder( "<html><h3>Android AVD List</h3>" );
                while ( true ) {
                    String line = in.readLine();
                    if ( line == null ) {
                        break;
                    }
                    line = line.trim();
                    if ( line.startsWith( "Name: " ) ) {
                        String name = line.substring( "Name: ".length() );
                        avdList.append( name ).append( "<br>" );
                    }
                }
                return avdList.toString();
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
                    Util.showMessage( view, jEdit.getProperty("android.Available_AVDs", "Available AVDs"), answer );
                }
            } catch ( Exception e ) {
                Util.showError( view, jEdit.getProperty( "android.Error", "Error" ), e.getMessage() );
            }
        }
    }
}

