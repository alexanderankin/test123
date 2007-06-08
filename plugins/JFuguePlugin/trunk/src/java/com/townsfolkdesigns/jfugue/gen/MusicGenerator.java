/**
 * 
 */
package com.townsfolkdesigns.jfugue.gen;

/**
 * @author elberry
 *
 */
public interface MusicGenerator {
	
	public void addTransform(MusicTransform musicTransform);
	
	public String generate(String axiom, int iterations);
	
	public String getName();
	
	public boolean removeTransform(MusicTransform musicTransform);

}
