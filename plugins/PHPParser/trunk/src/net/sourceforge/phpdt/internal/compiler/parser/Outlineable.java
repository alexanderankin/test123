package net.sourceforge.phpdt.internal.compiler.parser;

/**
 * Here is an interface that object that can be in the outline view must implement.
 * @author Matthieu Casanova
 */
public interface Outlineable {

  /**
   * This will return the image for the outline of the object.
   * @return an image
   */
//  ImageDescriptor getImage();

  Object getParent();


 // Position getPosition();
}
