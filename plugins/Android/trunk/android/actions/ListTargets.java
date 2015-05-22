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

    
    /**
     * The 'android list targets' command produces output with several sections like this:
     * ----------
     * id: 1 or "android-19"
     *      Name: Android 4.4.2
     *      Type: Platform
     *      API level: 19
     *      Revision: 4
     *      Skins: HVGA, QVGA, WQVGA400, WQVGA432, WSVGA, WVGA800 (default), WVGA854, WXGA720, WXGA800, WXGA800-7in
     *  Tag/ABIs : default/armeabi-v7a, default/x86
     *  
     * This parses the output, finds those sections with "Type: Platform" and lists the name
     * of that platform.
     */
    class Runner extends SwingWorker<String, Object> {
        @Override
        public String doInBackground() {
            try {
                String sdkPath = jEdit.getProperty( "android.sdk.path", "" );
                if ( !sdkPath.isEmpty() ) {
                    sdkPath += "/tools/";
                }
                Process p = Runtime.getRuntime().exec( sdkPath + "android list targets" );
                BufferedReader in = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
                StringBuilder targetList = new StringBuilder( "<html><h3>Android Target List</h3>" );
                while ( true ) {
                    String line = in.readLine();
                    if ( line == null ) {
                        break;
                    }
                    if ( line.startsWith( "---" ) ) {
                        String id = in.readLine();
                        if ( id == null ) {
                            break;
                        }
                        System.out.println("+++++ id: " + id);
                        String name = in.readLine();
                        if ( name == null ) {
                            break;
                        }
                        System.out.println( "+++++ name: " + name );
                        String type = in.readLine();
                        if ( type == null ) {
                            break;
                        }
                        System.out.println( "+++++ type: " + type );
                        if ( type != null && "type: platform".equals( type.toLowerCase().trim() ) ) {
                            targetList.append( name.split( ":" )[1].trim() + "\n" );
                        }
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