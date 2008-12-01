package gatchan.phpparser.project;

import javax.swing.*;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * The project list.
 * It could be used as ListModel and ComboBoxModel
 *
 * @author Matthieu Casanova
 */
public class ProjectList extends AbstractListModel implements MutableComboBoxModel {
  private final List objects;
  private Object selectedObject;

  ProjectList(List list) {
    objects = list;
  }

  /**
   * Returns the project for the given file.
   * If it doesn't exists We will create it
   *
   * @param file the property file of the project
   * @return the project
   */
  public Project getProject(File file) throws FileNotFoundException, InvalidProjectPropertiesException {
    for (int i = 0; i < objects.size(); i++) {
      final Project project = (Project) objects.get(i);
      if (project.getFile().equals(file)) {
        return project;
      }
    }
    return new Project(file);
  }

  public void setSelectedItem(Object anObject) {
    if ((selectedObject != null && !selectedObject.equals(anObject)) ||
        selectedObject == null && anObject != null) {
      selectedObject = anObject;
      fireContentsChanged(this, -1, -1);
    }
  }

  public Object getSelectedItem() {
    return selectedObject;
  }

  public int getSize() {
    return objects.size();
  }

  public Object getElementAt(int index) {
    if (index >= 0 && index < objects.size())
      return objects.get(index);
    else
      return null;
  }

  public void addElement(Object anObject) {
    if (!objects.contains(anObject)) {
      objects.add(anObject);
      fireIntervalAdded(this, objects.size() - 1, objects.size() - 1);
      if (objects.size() == 1 && selectedObject == null && anObject != null) {
        setSelectedItem(anObject);
      }
    }
  }

  public void insertElementAt(Object anObject, int index) {
    objects.add(index, anObject);
    fireIntervalAdded(this, index, index);
  }

  public void removeElementAt(int index) {
    if (getElementAt(index) == selectedObject) {
      if (index == 0) {
        setSelectedItem(getSize() == 1 ? null : getElementAt(index + 1));
      } else {
        setSelectedItem(getElementAt(index - 1));
      }
    }

    objects.remove(index);

    fireIntervalRemoved(this, index, index);
  }

  public void removeElement(Object anObject) {
    final int index = objects.indexOf(anObject);
    if (index != -1) {
      removeElementAt(index);
    }
  }
}
