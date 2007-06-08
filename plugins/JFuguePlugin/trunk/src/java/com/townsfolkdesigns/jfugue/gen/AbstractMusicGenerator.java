/**
 * 
 */
package com.townsfolkdesigns.jfugue.gen;

import java.util.List;

/**
 * @author elberry
 *
 */
public abstract class AbstractMusicGenerator implements MusicGenerator {
	
	private List<MusicTransform> musicTransforms;

	/* (non-Javadoc)
    * @see com.townsfolkdesigns.jfugue.gen.MusicGenerator#addTransform(com.townsfolkdesigns.jfugue.gen.MusicTransform)
    */
   public void addTransform(MusicTransform musicTransform) {
	   getMusicTransforms().add(musicTransform);
   }

	/* (non-Javadoc)
    * @see com.townsfolkdesigns.jfugue.gen.MusicGenerator#removeTransform(com.townsfolkdesigns.jfugue.gen.MusicTransform)
    */
   public boolean removeTransform(MusicTransform musicTransform) {
   	 return getMusicTransforms().remove(musicTransform);
   }

	/**
    * @return the musicTransforms
    */
   protected List<MusicTransform> getMusicTransforms() {
   	return musicTransforms;
   }

	/**
    * @param musicTransforms the musicTransforms to set
    */
   protected void setMusicTransforms(List<MusicTransform> musicTransforms) {
   	this.musicTransforms = musicTransforms;
   }

}
