package xml.parser;

import java.util.List;
import java.util.ArrayList;

import org.apache.xerces.xni.grammars.*;
import org.apache.xerces.xni.*;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;

import xml.cache.Cache;
import xml.cache.CacheEntry;
import static xml.Debug.*;

/**
 * FIXME: thread safety
 */
public class CachedGrammarPool implements XMLGrammarPool {
	private Buffer buffer;
	
	public CachedGrammarPool(Buffer requestingBuffer){
		this.buffer = requestingBuffer;
	}
	
    /**
     * <p> retrieve the initial known set of grammars. this method is
     * called by a validator before the validation starts. the application 
     * can provide an initial set of grammars available to the current 
     * validation attempt. </p>
     * @param grammarType the type of the grammar, from the
     *  <code>org.apache.xerces.xni.grammars.Grammar</code> interface.
     * @return the set of grammars the validator may put in its "bucket"
     */
     public Grammar[] retrieveInitialGrammarSet(String grammarType){
     	 return new Grammar[0];
     }

    /** 
     * <p>return the final set of grammars that the validator ended up
     * with.  
     * This method is called after the
     * validation finishes. The application may then choose to cache some
     * of the returned grammars. </p>
     * @param grammarType the type of the grammars being returned;
     * @param grammars an array containing the set of grammars being
     *  returned; order is not significant.
     */
     public void cacheGrammars(String grammarType, Grammar[] grammars){
     	 if(DEBUG_CACHE)Log.log(Log.DEBUG,CachedGrammarPool.class,"cacheGrammars("+grammarType+")");
     	 
     	 Cache cache = Cache.instance();
     	 List<CacheEntry> myGrammars = new ArrayList<CacheEntry>(grammars.length);
     	 for(Grammar g: grammars){
     	 	 XMLGrammarDescription desc = g.getGrammarDescription();
			 String path = null;
			 try{
				 path = xml.Resolver.instance().resolveEntityToPath(
					 null,//name
					 desc.getPublicId(),
					 desc.getBaseSystemId(),
					 desc.getLiteralSystemId()
					 );
			 }catch(Exception e){
				 //not allowed to rethrow it (or as RuntimeException)
				 Log.log(Log.ERROR,CachedGrammarPool.class,"error caching "+desc,e);
			 }
     	 	 if(DEBUG_CACHE)Log.log(Log.DEBUG,CachedGrammarPool.class,"grammar="+desc.getExpandedSystemId());
     	 	 if(DEBUG_CACHE)Log.log(Log.DEBUG,CachedGrammarPool.class,"path="+path);
     	 	 // happens for DTDs inside the document, where systemId==publicId==null
     	 	 // and that we don't want to cache
     	 	 if(path!=null)myGrammars.add(cache.put(path, grammarType, g));
     	 }
     	 for(CacheEntry en: myGrammars){
     	 	 en.getRequestingBuffers().add(buffer);
     	 	 en.getRelated().addAll(myGrammars);
     	 	 en.getRelated().remove(en);
     	 }
     }

    /** 
     * <p> This method requests that the application retrieve a grammar
     * corresponding to the given GrammarIdentifier from its cache.
     * If it cannot do so it must return null; the parser will then
     * call the EntityResolver.  <strong>An application must not call its
     * EntityResolver itself from this method; this may result in infinite
     * recursions.</strong>
     * @param desc The description of the Grammar being requested.
     * @return the Grammar corresponding to this description or null if
     *  no such Grammar is known.
     */
     public Grammar retrieveGrammar(XMLGrammarDescription desc){
     	 if(DEBUG_CACHE)Log.log(Log.DEBUG,CachedGrammarPool.class,"retrieveGrammar("+desc.getGrammarType()+","+desc+")");

     	 // the validator firstly asks for a grammar without location information
     	 if(desc.getPublicId()==null && desc.getLiteralSystemId() == null)return null;
     	 
     	 // don't cache DTD, overwise I don't get the DTD events to construct the CompletionInfo from a DTD !
     	 //if(desc.getGrammarType() == desc.XML_DTD)return null;
     	 Cache cache = Cache.instance();
     	 String path = null;
     	 try{
     	 	 path = xml.Resolver.instance().resolveEntityToPath(
				 null,//name
				 desc.getPublicId(),
				 desc.getBaseSystemId(),
				 desc.getLiteralSystemId()
				 );
		 }catch(Exception e){
		 	 //not allowed to rethrow it (or as RuntimeException)
		 	 Log.log(Log.ERROR,CachedGrammarPool.class,"error retrieving "+desc);
		 	 Log.log(Log.ERROR,CachedGrammarPool.class,e);
		 }
		 CacheEntry en = cache.get(path, desc.getGrammarType());
		 if(en == null){
		 	 if(DEBUG_CACHE)Log.log(Log.DEBUG,CachedGrammarPool.class,path+" not found in cache");
		 	 return null;
		 }else{
		 	 if(DEBUG_CACHE)Log.log(Log.DEBUG,CachedGrammarPool.class,path+" found in cache");
		 	 en.getRequestingBuffers().add(buffer);
		 	 return (Grammar)en.getCachedItem();
		 }
     }

    /**
     * Causes the XMLGrammarPool not to store any grammars when
     * the cacheGrammars(String, Grammar[[]) method is called.
     */
     public void lockPool(){
     	 throw new UnsupportedOperationException();
     }

    /**
     * Allows the XMLGrammarPool to store grammars when its cacheGrammars(String, Grammar[])
     * method is called.  This is the default state of the object.
     */
     public void unlockPool(){
     	 throw new UnsupportedOperationException();
     }

    /**
     * Removes all grammars from the pool.
     */
     public void clear(){
		 throw new UnsupportedOperationException();
     }
	
}
