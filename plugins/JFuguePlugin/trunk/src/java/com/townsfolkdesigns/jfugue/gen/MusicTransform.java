/**
 * 
 */
package com.townsfolkdesigns.jfugue.gen;

import java.io.Serializable;

/**
 * @author elberry
 * 
 */
public class MusicTransform implements Serializable {

	/**
    * 
    */
	private static final long serialVersionUID = 6971970633057471544L;

	private boolean active;

	private String source;

	private String target;

	/**
    * 
    */
	public MusicTransform() {
	}

	public MusicTransform(String source, String target) {
		setSource(source);
		setTarget(target);
	}

	/**
    * @return the source
    */
	public String getSource() {
		return source;
	}

	/**
    * @return the target
    */
	public String getTarget() {
		return target;
	}

	/**
    * @return the active
    */
	public boolean isActive() {
		return active;
	}

	/**
    * @param active
    *           the active to set
    */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
    * @param source
    *           the source to set
    */
	public void setSource(String transform) {
		this.source = transform;
	}

	/**
    * @param target
    *           the target to set
    */
	public void setTarget(String target) {
		this.target = target;
	}

}
