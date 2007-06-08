/**
 * 
 */
package com.townsfolkdesigns.jfugue.gen;

import java.io.Serializable;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;

/**
 * @author elberry
 *
 */
public class LSysMusicGenerator extends AbstractMusicGenerator implements Serializable {	
	

	/**
    * 
    */
   private static final long serialVersionUID = -9195899792977648100L;

	/**
	 * 
	 */
	public LSysMusicGenerator() {
		setMusicTransforms(new LinkedList<MusicTransform>());
	}

	/* (non-Javadoc)
	 * @see com.townsfolkdesigns.jfugue.gen.MusicGenerator#generate(java.lang.String, int)
	 */
	public String generate(String axiom, int iterations) {
		String generatedMusic = axiom;
		for(int iteration = 0; iteration < iterations; iteration++) {
			for(MusicTransform musicTransform : getMusicTransforms()) {
				generatedMusic = StringUtils.replace(generatedMusic, musicTransform.getSource(), musicTransform.getTarget());
			}
		}
		return generatedMusic;
	}

	/* (non-Javadoc)
	 * @see com.townsfolkdesigns.jfugue.gen.MusicGenerator#getName()
	 */
	public String getName() {
		return "Lindenmayer System";
	}
}
