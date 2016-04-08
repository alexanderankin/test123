/*
 * ServicesDatatypeLibraryFactory.java - loads datatype libraries from services
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2016 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.msg.PluginUpdate;
import org.gjt.sp.util.Log;
import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;

/**
 * Loads relax-ng datatype libraries from jEdit services.
 *
 * <p>Implements jing-trang pluggable datatypes DatatypeLibraryFactory,
 * finding datatype libraries from jEdit services.</p>
 *
 * <p>To add your own datatype library, add a service for the
 * org.relaxng.datatype.DatatypeLibraryFactory class:
 * <pre>
 * &lt;SERVICE CLASS="org.relaxng.datatype.DatatypeLibraryFactory" NAME="xsd-datatype-factory">
 *      xml.parser.XSDDatatypeLibraryFactoryCreator.createXSDFactory();
 * &lt;/SERVICE>
 * </pre>
 * </p>
 */
public class ServicesDatatypeLibraryFactory implements DatatypeLibraryFactory {
	private List<DatatypeLibraryFactory> factories;
	private Map<String, DatatypeLibrary> libraries;
	
    public ServicesDatatypeLibraryFactory() {
    	factories = new ArrayList<DatatypeLibraryFactory>();
		libraries = new HashMap<String, DatatypeLibrary>();
	}
    
    public void loadFactories(){
    	factories.clear();
    	libraries.clear();
    	for(String name: ServiceManager.getServiceNames(DatatypeLibraryFactory.class)){
    		DatatypeLibraryFactory f = ServiceManager.getService(DatatypeLibraryFactory.class, name);
    		Log.log(Log.DEBUG, ServicesDatatypeLibraryFactory.class, "Found Datatype factory " + name + ": "+f.getClass());
    		factories.add(f);
    	}
    }
    
    @Override
	public DatatypeLibrary createDatatypeLibrary(String namespaceURI) {
    	DatatypeLibrary library = libraries.get(namespaceURI);
    	if(library == null){
	    	for(DatatypeLibraryFactory factory: factories){
	    		library = factory.createDatatypeLibrary(namespaceURI);
	    		if(library != null){
	    			libraries.put(namespaceURI, library);
	    			break;
	    		}
	    	}
    	}
    	return library;
    }
    
    
	/**
	 * add to EditBus
	 */
	public void start(){
		loadFactories();
		EditBus.addToBus(this);
	}

	/**
	 * clear, remove from EditBus, forget singleton
	 */
	public void stop(){
    INSTANCE = null;
		EditBus.removeFromBus(this);
		factories.clear();
		libraries.clear();
	}
	
	@EBHandler
	public void handlePluginUpdate(PluginUpdate message) {
		if(message.getWhat() == PluginUpdate.LOADED
			|| message.getWhat() == PluginUpdate.UNLOADED) {
				loadFactories();
		}
	}

	public static ServicesDatatypeLibraryFactory instance(){
		return INSTANCE;
	}

	private static ServicesDatatypeLibraryFactory INSTANCE = new ServicesDatatypeLibraryFactory();
}