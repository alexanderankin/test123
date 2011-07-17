package xml.cache;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import static org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;

import static xml.Debug.*;
import xml.PathUtilities;

/**
 * caching policy :
 *    - a file-based CacheEntry is valid until opened in jEdit
 *    - a buffer-based CacheEntry is invalidated on change of the buffer
 *    - a CacheEntry is discarded once all buffers requiring it are closed
 *      (similar to reference-counting)
 * cache cleaners :
 *	  - listen to Buffer modifications and invalidates entries related to them
 *    - listen to Buffer open and invalidates file based CacheEntry
 *    - listen to Buffer close and invalidate buffer based CacheEntry 
 *      and invalidate cache entries no more required
 */
public final class Cache extends BufferAdapter
{
	private static Cache instance;
	
	private Set<CacheEntry> entries;
	
	private Cache(){
		entries = new HashSet<CacheEntry>();
	}
	
	public static Cache instance()
	{
		if(instance == null)instance = new Cache();
		return instance;
	}
	
	/**
	 * @param	path	path of cached resource (file:/ urls are equivalent to paths)
	 * @param	key	unambiguously distinguish schema from completionInfo from...
	 */
	public CacheEntry put(String path, Object key, Object value){
		if(DEBUG_CACHE)Log.log(Log.DEBUG,Cache.class,"put("+path+","+key+","+(value == null ? null : value.getClass())+")");
		String npath = PathUtilities.urlToPath(path);
		if(npath != path){
			if(DEBUG_CACHE)Log.log(Log.DEBUG,Cache.class," really putting "+npath);
			path = npath;
		}
		CacheEntry en = new CacheEntry(path,key,value);
		// TODO: not sure about overwritting - not overwritting for now
		if(entries.contains(en)){
			if(DEBUG_CACHE)Log.log(Log.DEBUG,Cache.class," already in cache");
		}else{
			if(DEBUG_CACHE)Log.log(Log.DEBUG,Cache.class," not in cache");
			entries.add(en);
		}
		return en;
	}
	
	/**
	 * @param	path	path of cached resource (file:/ urls are equivalent to paths)
	 * @param	key		unambiguously distinguish schema from completionInfo from...
	 */
	public CacheEntry get(String path, Object key){
		if(DEBUG_CACHE)Log.log(Log.DEBUG,Cache.class,"get("+path+","+key+")");
		String npath = PathUtilities.urlToPath(path);
		if(npath != path){
			if(DEBUG_CACHE)Log.log(Log.DEBUG,Cache.class," really getting "+npath);
			path = npath;
		}
		for(Iterator<CacheEntry> it=entries.iterator();it.hasNext();){
			CacheEntry en = it.next();
			if(en.getPath().equals(path)
				&& en.getKey().equals(key)){
				if(DEBUG_CACHE)Log.log(Log.DEBUG,Cache.class," found");
				return en;
			}
		}
		if(DEBUG_CACHE)Log.log(Log.DEBUG,Cache.class," not found");
		return null;
	}
	
	/**
	 * invalidate cache entries corresponding to (not requested by) this buffer
	 */
	public void contentInserted(JEditBuffer buffer, int startLine, int offset, int numLines, int length){
		handleBufferContentChanged((Buffer)buffer);
	}

	/**
	 * invalidate cache entries corresponding to (not requested by) this buffer
	 */
	public void contentRemoved(JEditBuffer buffer, int startLine, int offset, int numLines, int length){
		handleBufferContentChanged((Buffer)buffer);
	}

	/**
	 * invalidate cache entries corresponding to (not requested by) this buffer
	 */
	public void transactionComplete(JEditBuffer buffer){
		handleBufferContentChanged((Buffer)buffer);
	}

	@EBHandler
	public void handleBufferUpdate(BufferUpdate message)
	{
		if(BufferUpdate.CLOSED.equals(message.getWhat())){
			if(DEBUG_CACHE)Log.log(Log.DEBUG,Cache.class,"buffer closed");
			invalidateCacheEntriesFromPath(message.getBuffer().getPath());
			invalidateCacheEntriesRequiredByBuffer(message.getBuffer());
			message.getBuffer().removeBufferListener(this);
		}else if(BufferUpdate.LOADED.equals(message.getWhat())){
			if(DEBUG_CACHE)Log.log(Log.DEBUG,Cache.class,"buffer opened");
			invalidateCacheEntriesFromPath(message.getBuffer().getPath());
			message.getBuffer().addBufferListener(this);
		}
	}
	
	public void handleBufferContentChanged(Buffer buffer)
	{
		invalidateCacheEntriesFromPath(buffer.getPath());
	}
	
	private void invalidateCacheEntriesFromPath(String path){
		if(DEBUG_CACHE)Log.log(Log.DEBUG,Cache.class,"invalidateCacheEntriesFromPath("+path+")");
		List<CacheEntry> toRemove = new ArrayList<CacheEntry>();
		for(Iterator<CacheEntry> it=entries.iterator();it.hasNext();){
			CacheEntry en = it.next();
			if(en.getPath().equals(path)){
				if(DEBUG_CACHE)Log.log(Log.DEBUG,Cache.class,"invalidating "+en);
				it.remove();
				toRemove.addAll(en.getRelated());
			}
		}
		removeRelated(new HashSet<CacheEntry>(),toRemove);
	}
	
	private void invalidateCacheEntriesRequiredByBuffer(Buffer b){
		List<CacheEntry> toRemove = new ArrayList<CacheEntry>();
		for(Iterator<CacheEntry> it=entries.iterator();it.hasNext();){
			CacheEntry en = it.next();
			Set<Buffer> reqB = en.getRequestingBuffers();
			if(reqB.contains(b)){
				reqB.remove(b);
				if(reqB.isEmpty()){
					if(DEBUG_CACHE)Log.log(Log.DEBUG,Cache.class,"invalidating "+en);
					it.remove();
					toRemove.addAll(en.getRelated());
				}
			}
		}
		removeRelated(new HashSet<CacheEntry>(), toRemove);
	}
	
	/**
	 * recursively remove related CacheEntries
	 * @param	removed	avoid infinite recursion when related are recursive
	 */
	private void removeRelated(Set<CacheEntry> removed,List<CacheEntry> toRemove){
		for(CacheEntry ce : toRemove){
			if(!removed.contains(ce)){
				if(DEBUG_CACHE)Log.log(Log.DEBUG,Cache.class,"invalidating related "+ce);
				entries.remove(ce);
				removed.add(ce);
				removeRelated(removed, ce.getRelated());
			}
		}
	}
	
	/**
	 * add to EditBus
	 */
	public void start(){
		EditBus.addToBus(this);
	}

	/**
	 * clear, remove from EditBus, forget singleton
	 */
	public void stop(){
		EditBus.removeFromBus(this);
		for(Buffer b:jEdit.getBuffers()){
			b.removeBufferListener(this);
		}
		entries.clear();
		instance = null;
	}
	
	/**
	 * remove all cache entries
	 */
	public void clear(){
		entries.clear();
	}
}
