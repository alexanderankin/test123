package xml.cache;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import org.gjt.sp.jedit.Buffer;

/**
 * While they may be some ordering between related CacheEntries (eg schema A imports
 * schema B), this is not recorded in the CacheEntry : one only says that A and B are
 * related and if either A or B changes, both are invalidated.
 * RequestingBuffer is really a reference-counting algorithm. A CacheEntry is 
 * evicted when no buffer requests it anymore.
 */
public class CacheEntry{
	private final String path;
	private final Object key;
	private final Object value;
	private List<CacheEntry> related;
	private Set<Buffer> requestingBuffers;
	
	public CacheEntry(String path, Object key, Object value){
		if(path == null) throw new IllegalArgumentException("path may not be null");
		if(key == null)  throw new IllegalArgumentException("key may not be null");

		this.path = path;
		this.key = key;
		this.value = value;
		this.related = new ArrayList<CacheEntry>();
		this.requestingBuffers = new HashSet<Buffer>();
	}
	
	public Set<Buffer> getRequestingBuffers(){
		return requestingBuffers;
	}
	
	public void addRequestingBuffer(Buffer b){
		addRequestingBufferReq(new HashSet<CacheEntry>(),b);
	}
	
	private void addRequestingBufferReq(Set<CacheEntry> visited, Buffer b){
		requestingBuffers.add(b);
		visited.add(this);
		for(CacheEntry en:related){
			if(!visited.contains(en)){
				en.addRequestingBufferReq(visited,b);
			}
		}
	}
	
	public List<CacheEntry> getRelated(){
		return related;
	}
	
	public Object getCachedItem(){
		return value;
	}
	
	public Object getKey(){
		return key;
	}
	
	public String getPath(){
		return path;
	}
	
	@Override
	public boolean equals(Object other){
		if(other == this)return true;
		if(other == null || !(other instanceof CacheEntry))return false;
		CacheEntry o = (CacheEntry)other;
		return o.path ==path && o.key == key;
	}
	
	@Override
	public int hashCode(){
		return 53 + path.hashCode() + key.hashCode()*2013;
	}
	
	@Override
	public String toString(){
		return "CacheEntry["+path.toString()+","+key.toString()+"="+(value==null ? null : value.getClass())+"]";
	}
}
