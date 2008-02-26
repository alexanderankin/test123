/*
 * jedit.cpp - jEdit Launcher for Windows (TM).
 *
 * Native launcher.
 *
 * Copyright 2004, 2005 Ollie Rutherfurd <oliver@jedit.org>
 *
 * $Id: jedit.cpp 71 2005-01-10 20:26:43Z orutherfurd $
 */


#include "jeditlib.h"

// jedit.exe can be build as either a "windows app"
// or a console app.  Comment this out to compile
// as a console app.
#define BUILD_WIN32_APP

#define J_LOG_SOURCE "jedit.exe"

const char* USAGE = 
"Usage: jedit.exe [<options>] [<files>]\n"
"    -newplainview: Client instance opens a new plain view\n"
"    -newview: Client instance opens a new view (default)\n"
"    -run=<script>: Run the specified BeanShell script\n"
"    -server: Read/write server info from/to $HOME/.jedit/server (default)\n"
"    -server=<name>: Read/write server info from/to $HOME/.jedit/<name>\n"
"    -settings=<path>: Load user-specific settings from <path>\n"
"    -wait: Wait until the user closes the specified buffer in the server\n"
"     instance. Does nothing if passed to the initial jEdit instance.\n";

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
	char** files = 0;
	int nfiles = 0;

	// log arguments
	for(int i=1; i < argc; i++)
		log_to_file(J_LOG_FILE, J_LOG_SOURCE, "argv[%d]: %s\n", i, argv[i]);

	if(argc > 1  && (strcmp(argv[1], "/h") == 0 ||
					 strcmp(argv[1], "-h") == 0 ||
					 strcmp(argv[1], "/?") == 0 ||
					 strcmp(argv[1], "--help") == 0))
	{
#ifdef BUILD_WIN32_APP
		MessageBox(NULL, USAGE, "jEdit Usage", MB_OK);
#else
		printf(USAGE);
#endif
		return 0;
	}

	// get Java and jEdit settings
	LaunchConfig config;
	OptList* opts = parse_args(argc,(const char**)argv);

	// expand any globs given, if any filenames given
	if(argc > 1) {
		// find out how many files match
		nfiles = -1 * expand_globs((const char**)&argv[opts->len+1], 
								   argc-(opts->len+1), 
								   files, 
								   nfiles);
		if(nfiles) {
			log_to_file(J_LOG_FILE, J_LOG_SOURCE, "nfiles: %d\n", nfiles);
			int sz = (nfiles) * sizeof(char*);
			files = (char**)malloc(sz);
			if(!files) {
				log_to_file(J_LOG_FILE, J_LOG_SOURCE, "out of memory\n");
				// display a MsgBox if winapp?
				printf("ERROR: out of memory\n");
				return 1;
			}
			expand_globs((const char**)&argv[opts->len+1], argc-(opts->len+1), files, nfiles);
		}
	}

	// get java path, jedit path, etc...
	if(get_launch_config(&config)) {
		printf(FAILED_TO_GET_SETTINGS);
		return 1;
	}

	return open_files_in_jedit(config.java,
								config.java_opts,
								config.jedit_jar,
								config.jedit_opts,
								config.working_dir,
								opts,
								(const char**)files,
								nfiles);

}


// :deepIndent=true:folding=indent:tabSize=4:
