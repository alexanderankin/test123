package gatchan.phpparser.project.itemfinder;

import javax.swing.*;

/**
 * @author Matthieu Casanova
 */
public interface PHPItem {
  int CLASS = 0;
  int METHOD = 1;

  int getItemType();

  String getName();

  int getSourceStart();

  String getPath();

  Icon getIcon();
}
