/*
 * jdiff.cpp - jEdit Launcher for Windows (TM).
 *
 * Program to diff two files in jEdit using JDiffPlugin.
 *
 * Copyright 2004, 2005 Ollie Rutherfurd <oliver@jedit.org>
 *
 * $Id: jdiff.cpp 74 2005-05-02 19:55:40Z orutherfurd $
 */

#include "jeditlib.h"

#define J_LOG_SOURCE "jdiff.exe"

// jedit.exe can be build as either a "windows app"
// or a console app.  Comment this out to compile
// as a console app.
#define BUILD_WIN32_APP

const char* USAGE = 
"Usage: jdiff.exe [-h] file1 file2\n";


#ifdef BUILD_WIN32_APP
int 
WINAPI WinMain(HINSTANCE hInstance,
			   HINSTANCE hPrevInstance,
			   LPSTR lpCmdLine,
			   int nCmdShow)
#else
int
main(int argc,
	 char** argv)
#endif
{
	#ifdef BUILD_WIN32_APP
	// avoid having to parse lpCmdLine if
	// compiling as windows app
	int argc = __argc;
	char** argv = __argv;
	#endif
	LaunchConfig config;
	char file1[MAX_PATH] = {'\0'};
	char file2[MAX_PATH] = {'\0'};

	for(int i=1; i < argc; i++)
		log_to_file(J_LOG_FILE, J_LOG_SOURCE, "argv[%d]: %s\n", i, argv[i]);

	// XXX a wait option would be nice
	if(argc > 1  && (strcmp(argv[1], "/h") == 0 ||
					 strcmp(argv[1], "-h") == 0 ||
					 strcmp(argv[1], "/?") == 0 ||
					 strcmp(argv[1], "--help") == 0))
	{
#ifdef BUILD_WIN32_APP
		MessageBox(NULL, USAGE, "jDiff Usage", MB_OK);
#else
		printf(USAGE);
#endif
		exit(0);
	}
	else if(argc != 3)
	{
#ifdef BUILD_WIN32_APP
		MessageBox(NULL, USAGE, "jDiff Usage", MB_OK);
#else
		printf(USAGE);
#endif
		exit(2);
	}

	if(get_launch_config(&config))
	{
		printf(FAILED_TO_GET_SETTINGS);
		return 1;
	}

	_fullpath(file1,argv[1],MAX_PATH);
	_fullpath(file2,argv[2],MAX_PATH);

	return diff_files_in_jedit(config.java,
							   config.java_opts,
							   config.jedit_jar,
							   config.jedit_opts,
							   config.working_dir,
							   file1,
							   file2);

}

// :deepIndent=true:folding=indent:tabSize=4:
