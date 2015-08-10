/*
 * EntityMgrFixerConfiguration.java - work-around for XERCESJ-1205
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * copyright (C) 2011 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml.parser;

import java.io.IOException;

import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.dtd.DTDGrammar;
import org.apache.xerces.impl.dtd.XMLDTDDescription;
import org.apache.xerces.impl.dtd.XMLEntityDecl;
import org.apache.xerces.parsers.XIncludeAwareParserConfiguration;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.gjt.sp.util.Log;

/**
 * fix for bug #3393297 - "XMLPlugin doesn't find DTD upon second parse".
 * work-around for Xerces bug [XERCESJ-1205]
 * "Entity resolution does not work with DTD grammar caching resolved". code is
 * brittle (depends on the implementation in XIncludeAwareParserConfiguration
 * and the like).
 * 
 * @see http://sourceforge.net/tracker/?func=detail&aid=3393297&group_id=588&atid=565475
 * @see https://issues.apache.org/jira/browse/XERCESJ-1205
 * @author kerik-sf
 * @version $Id$
 * */
class EntityMgrFixerConfiguration extends XIncludeAwareParserConfiguration {
	protected EntityMgrFixer fEntityMgrFixer;

	private class EntityMgrFixer implements XMLComponent, XMLDocumentFilter {
		// instance variables

		// for XMLDocumentFilter
		protected XMLDocumentHandler fDocumentHandler;
		protected XMLDocumentSource fDocumentSource;

		protected XMLLocator fDocLocation;

		/**
		 * save locator for future use and forward.
		 * */
		@Override
		public void startDocument(XMLLocator locator, String encoding,
				NamespaceContext namespaceContext, Augmentations augs)
				throws XNIException {

			// save XMLLocator for future use
			fDocLocation = locator;

			if (fDocumentHandler != null) {
				fDocumentHandler.startDocument(locator, encoding,
						namespaceContext, augs);
			}
		}

		/**
		 * copy external entities from a cached grammar to the entity manager.
		 * relies on being called when the grammar has been loaded (see
		 * XMLDTDValidator, line 769)
		 **/
		@Override
		public void doctypeDecl(String rootElement, String publicId,
				String systemId, Augmentations augs) throws XNIException {
			if (fDocumentHandler != null) {
				fDocumentHandler.doctypeDecl(rootElement, publicId, systemId,
						augs);
			}
			if (fValidationManager.isCachedDTD()) {
				// duplicates code from XMLDTDValidator to retrieve the grammar
				String eid = null;
				try {
					eid = XMLEntityManager.expandSystemId(systemId,
							fDocLocation.getExpandedSystemId(), false);
				} catch (java.io.IOException e) {
				}
				XMLDTDDescription grammarDesc = new XMLDTDDescription(publicId,
						systemId, fDocLocation.getExpandedSystemId(), eid,
						rootElement);
				DTDGrammar grammar = (DTDGrammar) fGrammarPool
						.retrieveGrammar(grammarDesc);
				// grammar is cached so it should be retrieved
				assert (grammar != null);

				// now copy to the entityManager
				((EntityMgrFixerConfiguration.MyEntityManager) fEntityManager)
						.copyEntitiesFromDTD(grammar);
			}
		}

		/* {{{ forward-only */
		@Override
		public void xmlDecl(String version, String encoding, String standalone,
				Augmentations augs) throws XNIException {
			if (fDocumentHandler != null) {
				fDocumentHandler.xmlDecl(version, encoding, standalone, augs);
			}
		}

		@Override
		public void comment(XMLString text, Augmentations augs)
				throws XNIException {
			if (fDocumentHandler != null) {
				fDocumentHandler.comment(text, augs);
			}
		}

		@Override
		public void processingInstruction(String target, XMLString data,
				Augmentations augs) throws XNIException {
			if (fDocumentHandler != null) {
				fDocumentHandler.processingInstruction(target, data, augs);
			}
		}

		@Override
		public void startElement(QName element, XMLAttributes attributes,
				Augmentations augs) throws XNIException {
			if (fDocumentHandler != null) {
				fDocumentHandler.startElement(element, attributes, augs);
			}
		}

		@Override
		public void emptyElement(QName element, XMLAttributes attributes,
				Augmentations augs) throws XNIException {
			if (fDocumentHandler != null) {
				fDocumentHandler.emptyElement(element, attributes, augs);
			}
		}

		@Override
		public void startGeneralEntity(String name,
				XMLResourceIdentifier identifier, String encoding,
				Augmentations augs) throws XNIException {
			if (fDocumentHandler != null) {
				fDocumentHandler.startGeneralEntity(name, identifier, encoding,
						augs);
			}
		}

		@Override
		public void textDecl(String version, String encoding, Augmentations augs)
				throws XNIException {
			if (fDocumentHandler != null) {
				fDocumentHandler.textDecl(version, encoding, augs);
			}
		}

		@Override
		public void endGeneralEntity(String name, Augmentations augs)
				throws XNIException {
			if (fDocumentHandler != null) {
				fDocumentHandler.endGeneralEntity(name, augs);
			}
		}

		@Override
		public void characters(XMLString text, Augmentations augs)
				throws XNIException {
			if (fDocumentHandler != null) {
				fDocumentHandler.characters(text, augs);
			}
		}

		@Override
		public void ignorableWhitespace(XMLString text, Augmentations augs)
				throws XNIException {
			if (fDocumentHandler != null) {
				fDocumentHandler.ignorableWhitespace(text, augs);
			}
		}

		@Override
		public void endElement(QName element, Augmentations augs)
				throws XNIException {
			if (fDocumentHandler != null) {
				fDocumentHandler.endElement(element, augs);
			}
		}

		@Override
		public void startCDATA(Augmentations augs) throws XNIException {
			if (fDocumentHandler != null) {
				fDocumentHandler.startCDATA(augs);
			}
		}

		@Override
		public void endCDATA(Augmentations augs) throws XNIException {
			if (fDocumentHandler != null) {
				fDocumentHandler.endCDATA(augs);
			}
		}

		@Override
		public void endDocument(Augmentations augs) throws XNIException {
			if (fDocumentHandler != null) {
				fDocumentHandler.endDocument(augs);
			}
		}

		/* }}} */

		/* {{{ piping */
		@Override
		public void setDocumentSource(XMLDocumentSource source) {
			fDocumentSource = source;
		}

		@Override
		public XMLDocumentSource getDocumentSource() {
			return fDocumentSource;
		}

		@Override
		public void setDocumentHandler(XMLDocumentHandler handler) {
			fDocumentHandler = handler;
		}

		@Override
		public XMLDocumentHandler getDocumentHandler() {
			return fDocumentHandler;
		}

		/* }}} */
		/* {{{ unimplemented stubs */
		@Override
		public void reset(XMLComponentManager componentManager)
				throws XMLConfigurationException {
		}

		@Override
		public String[] getRecognizedFeatures() {
			return null;
		}

		@Override
		public void setFeature(String featureId, boolean state)
				throws XMLConfigurationException {
		}

		@Override
		public String[] getRecognizedProperties() {
			return null;
		}

		@Override
		public void setProperty(String propertyId, Object value)
				throws XMLConfigurationException {
		}

		@Override
		public Boolean getFeatureDefault(String featureId) {
			return null;
		}

		@Override
		public Object getPropertyDefault(String propertyId) {
			return null;
		}
		/* }}} */

	}

	/**
	 * add a method to copy external entities from a cached grammar to
	 * XMLEntityManager. fInExternalSubset is what requires subclassing.
	 **/
	private static class MyEntityManager extends XMLEntityManager {

		private void copyEntitiesFromDTD(DTDGrammar grammar) {

			XMLEntityDecl entityDecl = new XMLEntityDecl();

			for (int i = 0; grammar.getEntityDecl(i, entityDecl); i++) {
				fInExternalSubset = entityDecl.inExternal;
				// see test_data/dtd/sample2.xml for an example why internal
				// entities should not be added
				if (entityDecl.inExternal) {
					if (entityDecl.publicId != null
							|| entityDecl.systemId != null) {
						try {
							addExternalEntity(entityDecl.name,
									entityDecl.publicId, entityDecl.systemId,
									entityDecl.baseSystemId);
						} catch (IOException e) {
							Log.log(Log.WARNING,
									EntityMgrFixerConfiguration.class,
									"error adding external entity from cached grammar ("
											+ entityDecl + ")", e);
						}
					} else {
						addInternalEntity(entityDecl.name, entityDecl.value);
					}
				}
			}
			fInExternalSubset = false;
		}
	}

	public EntityMgrFixerConfiguration(SymbolTable symbolTable,
			XMLGrammarPool cachedGrammarPool) {
		super(symbolTable, cachedGrammarPool);

		// override fEntityManager with my own
		// XXX: code is brittle, check with XML11Configuration() for any other
		// use of fEntityManager on new release
		fCommonComponents.remove(fEntityManager);
		fEntityManager = new MyEntityManager();
		fProperties.put(ENTITY_MANAGER, fEntityManager);
		addCommonComponent(fEntityManager);

		fErrorReporter.setDocumentLocator(fEntityManager.getEntityScanner());
	}

	@Override
	protected void configurePipeline() {
		super.configurePipeline();

		fEntityMgrFixer = new EntityMgrFixer();

		// insert the EntityMgrFixer in the end of the pipeline
		XMLDocumentSource prev = fLastComponent;
		fLastComponent = fEntityMgrFixer;

		XMLDocumentHandler next = prev.getDocumentHandler();
		prev.setDocumentHandler(fEntityMgrFixer);
		fEntityMgrFixer.setDocumentSource(prev);
		if (next != null) {
			fEntityMgrFixer.setDocumentHandler(next);
			next.setDocumentSource(fEntityMgrFixer);
		}

	}
}