package gatchan.phpparser.project.itemfinder;

import javax.swing.*;

/**
 * @author Matthieu Casanova
 */
public interface PHPItem {
  int CLASS = 0;
  int METHOD = 1;
  int FIELD = 2;
  int INTERFACE = 3;

  int getItemType();

  String getName();

  int getSourceStart();

  int getBeginLine();

  int getBeginColumn();

  String getPath();

  Icon getIcon();
}
