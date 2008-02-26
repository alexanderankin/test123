/*
 * jeditext.cpp - jEdit Launcher for Windows (TM).
 *
 * Library functions.
 *
 * Copyright 2004, 2005 Ollie Rutherfurd <oliver@jedit.org>
 *
 * $Id: jeditlib.h 78 2005-07-15 20:24:58Z orutherfurd $
 */

#ifndef __JEDITLIB_H__
#define __JEDITLIB_H__

#define WIN32_LEAN_AND_MEAN

#include <direct.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <string.h>
#include <time.h>
#include <windows.h>
#include <winsock.h>

#define J_LOG_FILE "jeditlauncher.log"

#define OPT_NAME_LEN 50
#define OPT_VALUE_LEN MAX_PATH

const char FAILED_TO_GET_SETTINGS[] =
"ERROR: Didn't find jEdit configuration settings.\n"
"       See README.html provided with this program\n"
"       for information on required settings.\n"
"\n"
"Also, see 'jeditlauncher.log' in your HOME\n"
"directory for additional troubleshooting output\n";

/**
 * Command-line option name/value pair.
 */
typedef struct{
    char name[OPT_NAME_LEN];
    char value[OPT_VALUE_LEN];
} Opt;


/**
 * List of zero or more command-line options.
 */
typedef struct{
    Opt* opts;      /** array of Opts */
    int len;        /** number of Opts */
} OptList;


/**
 * EditServer information.
 */
typedef struct
{
    unsigned int port;
    unsigned int key;
} EditServerInfo;

/**
 * Struct contains all required information
 * to launch a new instance of jEdit.
 */
typedef struct{
    char java[MAX_PATH];
    char java_opts[MAX_PATH];
    char jedit_jar[MAX_PATH];
    char jedit_opts[MAX_PATH];
    char working_dir[MAX_PATH];
} LaunchConfig;


/**
 * Extracts command-line options.
 *
 * Options are formatted:
 *
 *  -name[=value]
 *
 * where name is required and value is optional.
 *
 * This function assumes it's passed the same
 * values as passed to main() -- it skips the
 * first element in "argv".
 *
 * @return An allocated OptList*.  It is caller's resposibility to free
 *         this and all Opt's in the list.  Unless unable to allocate memory,
 *         OptList will always be non-null.
 */
OptList*
parse_args(int argc,
           const char** argv);



/**
 * Populates the given struct with configuration
 * info.  See get_java_path and friends for details
 * on how they're determined -- this is just a wrapper
 * since this has to be done all over the place.
 *
 * If either java_path or jedit_jar is not found,
 * then the function fails.
 *
 * @param config struct to fill
 *
 * @return 0 on success, non-zero on error.
 */
int
get_launch_config(LaunchConfig* config);

/**
 * Sends a script to EditServer on localhost.
 *
 * @param port port number EditServer is running on
 * @param key authentication key for EditServer
 * @param script the script to run.
 * @param wait for a response or socket to be closed before returning
 *
 * @return 0 on success, non-zero on error.
 */
int send_script(unsigned int port, unsigned int key, const char* script, int wait);

/**
 * Convert "\" in original to "\\" in escaped
 *
 * @param original the string to escape
 * @param escaped the escaped string.  escaped must be long
 *                enough to accomodate the original string.
 *
 * @return The length of the escaped string.
 */
size_t escape_filename(const char* original, char* escaped);

// XXX document
int
create_launch_command(const char * java,
                        const char * java_opts,
                        const char * jedit_jar,
                        const char * jedit_opts,
                        const char * working_dir,
                        const OptList * options,
                        const char ** filenames,
                        unsigned int nfiles,
                        char* cmd,
                        unsigned int ncmd);

/**
 * Launches an instance of jEdit.
 *
 * @param java path to java executable.
 * @param java_opts java options, or null.
 * @param jedit_jar path to jEdit.jar
 * @param jedit_opts jEdit options, or null.
 * @param working_dir working directory for new process
 * @param options options given on command-line
 * @param files list of files to open, or null
 * @param nfiles number of files to open.
 *
 * @return 0 on success, non-zero on error.
 */
int
launch_jedit(const char * java,
             const char * java_opts,
             const char * jedit_jar,
             const char * jedit_opts,
             const char * working_dir,
             const OptList * options,
             const char ** filenames,
             unsigned int nfiles);

/**
 * Get path to user's home directory.
 *
 * @param dest buffer to copy path to
 * @param ndest size of destination buffer
 *
 * @return 0 on success, size of buffer if buffer too small, -1 on other errors.
 */
size_t
get_home(char* dest,
         const int ndest);

/**
 * Copies path to server file into "server_file",
 * which should be MAX_PATH or longer.
 *
 * @param server_file string to copy path to
 * @param settings_dir location of settings dir, or NULL for default ("~/.jedit")
 * @param filename server filename, or NULL for default ("server")
 *
 * @return 0 on success, non-zero on failure.
 */
int
get_server_file(char* server_file,
                const char* settings_dir,
                const char* filename);

/**
 * Reads EditServerInfo from server_file.
 *
 * @param server_file path to server file.
 * @param edit_server struct to populate from server file.
 *
 * @return 0 on success, non-zero on error.
 */
int
get_edit_server_info(const char* server_file,
                     EditServerInfo * edit_server);

/**
 * Files "files" list with files found from "globs".
 *
 * It is the caller's responsibility to free entries added to "files".
 *
 * @param globs zero or more glob patterns (or filenames).
 * @param nglobs number of glob patterns (or filenames)
 * @param files list to fill with found filenames
 * @param nfiles size of "files" list.
 *
 * @return number of files found.  If negative, the number
 *         of files found couldn't fit into the given
 *         list.  Increase the size of "files" an re-call.
 */
int
expand_globs(const char** globs,
             const int nglobs,
             char** files,
             int nfiles);


/**
 * Diffs files in jEdit using JDiffPlugin.
 *
 * @param java path to java executable.
 * @param java_opts java options, or null.
 * @param jedit_jar path to jEdit.jar
 * @param jedit_opts jEdit options, or null.
 * @param working_dir working directory for new process
 * @param file1 file to open in left split
 * @param file2 file to open in right split
 *
 * @return 0 on success, non-zero on error.
 */
int
diff_files_in_jedit(const char* java,
                    const char* java_opts,
                    const char* jedit_jar,
                    const char* jedit_opts,
                    const char* working_dir,
                    const char* file1,
                    const char* file2);


/**
 * Opens zero or more files in jEdit.
 *
 * If jEdit is not running, it is started,
 * otherwise any files are opened in the running
 * instance.
 *
 * @param java path to java executable.
 * @param java_opts java options, or null.
 * @param jedit_jar path to jEdit.jar
 * @param jedit_opts jEdit options, or null.
 * @param working_dir working directory for new process
 * @param opts options given on command line
 * @param files list of files to open, or null.  Note that files are treated
 *              as relative the the given working directory, if not absolute.
 * @param nfiles length of files (number of files to open).
 *
 * @return 0 on success, non-zero on error.
 */
int
open_files_in_jedit(const char* java,
                    const char* java_opts,
                    const char* jedit_jar,
                    const char* jedit_opts,
                    const char* working_dir,
                    const OptList* opts,
                    const char** files,
                    const int nfiles);

/**
 * Reads a registry value from jEditLaucher settings key.
 *
 * @param name registry value name
 * @param value buffer to copy value
 * @param nvalue size of value buffer
 *
 * @return 0 on success, non-zero on error
 */
int read_registry_string(const char* name,
                         char* value,
                         const int nvalue);

/**
 * Writes a registry value to the jEditLauncher settings key.
 *
 * @param name value name
 * @param value what to save
 */
int write_registry_string(const char* name,
                            const char* value);

/**
 * Returns "Java Executable" value from jEditLaucher's
 * registry settings.
 *
 * If unable to read setting from the registry,
 * try to use %JAVA_HOME% env. variable.
 */
int
get_java_path(char* java);

int
set_java_path(const char*);

/**
 * Returns "Java Options" value from jEditLaucher's
 * registry settings.
 *
 * If unable to read setting from the registry,
 * try to use %JAVA_OPTS% env. variable.
 */
int
get_java_opts(char* java_opts);

int
set_java_opts(const char*);

/**
 * If %JEDIT_JAR% env. variable set, use that
 * otherwise try to read "jEdit Target" from
 * jEditLaucher's registry settings.
 */
int
get_jedit_jar(char* jedit_jar);

int
set_jedit_jar(const char*);

/**
 * If %JEDIT_OPTS% env. variable set, use that
 * otherwise try to read "jEdit Options" from
 * jEditLaucher's registry settings.
 */
int
get_jedit_opts(char* jedit_opts);

int
set_jedit_opts(const char*);

/**
 * Returns "jEdit Working Directory" value from jEditLaucher's
 * registry settings.
 */
int
get_working_dir(char* working_dir);

int
set_working_dir(const char* working_dir);

/**
 * printf-like log function that appends output to a log file.
 *
 * The implentation is pretty hacky -- it opens and closes the
 * file every time.  Need to figure out how to tote around a
 * single global reference.
 *
 * @param filename log filename
 * @param format printf-like format string
 * @param ... insertions for format string
 *
 * @return 0 on success, non-zero on error
 */
int
log_to_file(const char* filename,
            const char* source,
            const char* format,
            ...);
/**
 * If given strip is wrapped with double quotes,
 * remove them.
 */
char*
strip_quotes(char*);

#endif
