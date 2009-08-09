package com.addictedtor.nativ ;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.ServiceManager ;

import org.gjt.sp.jedit.msg.PluginUpdate; 

/**
 * @author Romain Francois <francoisromain@free.fr>
 */
public class NativePlugin extends EBPlugin {

    public static final String NAME = "NativePlugin";

    public NativePlugin() {}

    /**
     * Start method of the plugin. This is where the magic takes place
     */
    @Override
    public void start() {
    }

    /**
     * Stop method of the plugin. Placeholder at the moment.
     */
    @Override
    public void stop() {
    }

    public void handleMessage(EBMessage message) {
    	if( message instanceof PluginUpdate){
    		PluginUpdate pu = (PluginUpdate)message ;
    		if( pu.getWhat() == PluginUpdate.LOADED ){
    			
    			NativeLibraryPathProvider service = 
    				(NativeLibraryPathProvider) ServiceManager.getService( 
    					"com.addictedtor.nativ.NativeLibraryPathProvider", pu.getPluginJAR().getPlugin().getClassName()
    					); 
    			if( service != null ){
    				String[] libraries = service.getNativeLibraries() ;
    				for( String lib: libraries ){
    					try{
    						System.load( lib );
    					} catch( Exception e){
    						e.printStackTrace(); 
    					}
    				}
    			}
    		}
    	}
    }

}

