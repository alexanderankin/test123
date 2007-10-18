package projects;

import java.util.Vector;

public interface ProjectViewerInterface {
	Vector<String> getProjects();
	Vector<String> getFiles(String project);
}
