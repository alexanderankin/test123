package gatchan.phpparser.project.itemfinder;

import javax.swing.*;

/**
 * @author Matthieu Casanova
 */
public interface PHPItem {
  int CLASS = 1;
  int METHOD = 2;
  int FIELD = 4;
  int INTERFACE = 8;

  int getItemType();

  String getName();
  
  String getNameLowerCase();

  int getSourceStart();

  int getBeginLine();

  int getBeginColumn();

  String getPath();

  Icon getIcon();
}
