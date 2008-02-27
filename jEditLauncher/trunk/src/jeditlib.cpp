/*
 * jeditlib.cpp - jEdit Launcher for Windows (TM).
 *
 * Library functions.
 *
 * Copyright 2004, 2005 Ollie Rutherfurd <oliver@jedit.org>
 *
 * $Id: jeditlib.cpp 81 2005-07-20 14:40:20Z orutherfurd $
 */

#include "jeditlib.h"

#define DIFF_SCRIPT_LEN (2048+(MAX_PATH*2))
#define J_LOG_SOURCE "jeditlib"

/**
 * Returns "true" if value is true otherwise "false";
 */
const char*
int2bool(const int value);

int
file_exists(const char* filename);

int
has_opt(const OptList* opts,
        const char* name);

const char*
get_opt_value(const OptList* opts,
              const char* name);

char*
create_server_script(const int wait,
                     const int restore,
                     const int new_view,
                     const int new_plain_view,
                     const char * script_file,
                     const char * working_dir,
                     const char ** filenames,
                     unsigned int nfiles);

// use same locations as jEditLauncher for easier transition
static const char* REG_SETTINGS_PATH        = "Software\\www.jedit.org\\jEditLauncher\\4.0";
static const char* SETTING_JAVA             = "Java Executable";
static const char* SETTING_JAVA_OPTS        = "Java Options";
static const char* SETTING_JEDIT_OPTS   = "jEdit Options";
static const char* SETTING_JEDIT_JAR        = "jEdit Target";
static const char* SETTING_WORKING_DIR  = "jEdit Working Directory";

static const char* DIFF_SCRIPT = ""
"// get current view, or open a new one\n"
//"View v = jEdit.getActiveView();\n"
"View v = jEdit.getLastView();\n"
"if(v == null) v = jEdit.newView(v);\n"
"// check that JDiff is installed\n"
"if(jEdit.getPlugin(\"jdiff.JDiffPlugin\") == null){\n"
"   Macros.error(v, \"You must have the JDiff plugin installed to use this feature.\");\n"
"   return;\n"
"}\n"
"// disable, if enabled\n"
"if(jdiff.DualDiff.isEnabledFor(v))\n"
"   jdiff.DualDiff.toggleFor(v);\n"
"// unsplit all edit panes, then split vertically\n"
"while(v.getEditPanes().length > 1)\n"
"   v.unsplit();\n"
"v.splitVertically();\n"
"// enable diff\n"
"jdiff.DualDiff.toggleFor(v);\n"
"// open buffers and set 1 and 2 in the vertical splits\n"
"openDiffFiles(vw){\n"
"   v = vw;\n"
"   run(){\n"
"       b1 = jEdit.openFile(v, \"%s\");\n"
"       b2 = jEdit.openFile(v, \"%s\");\n"
"       VFSManager.waitForRequests();\n"
"       editPanes = v.getEditPanes();\n"
"       editPanes[0].setBuffer(b1); \n"
"       editPanes[1].setBuffer(b2);\n"
"   }\n"
"   return this;\n"
"}\n"
"// open and display the files\n"
"SwingUtilities.invokeLater(openDiffFiles(v));\n";


static const char* FOCUS_SCRIPT = ""
"View v = jEdit.getActiveView();\n"
"if(v == null) v = jEdit.getLastView();\n"
"if(v == null) v = jEdit.newView(v);\n"
"int state = v.getExtendedState();\n"
"if((state & v.ICONIFIED) == v.ICONIFIED){ state ^= v.ICONIFIED; v.setExtendedState(state); }\n"
"v.setVisible(true);\n"
"v.getTextArea().requestFocus();\n";


/**
 * Private helper function for expand_globs - adds "filename" to "files".
 *
 * @param filename file to copy to "files".
 * @param files list of files to copy "filename" to.
 * @param nfiles size of list of files
 * @param count number of files in "files".
 *
 * @return ADDFILE_OK if filename appended to files, ADDFILE_OOM if unable
 *         to copy file (out of memory), and ADDFILE_OVERFLOW if files
 *         isn't long enough to fit filename.
 */
int
add_file_to_list(const char* filename,
                 char** files,
                 const int nfiles,
                 const int count);

/**
 * Private constants for private
 * helper function "add_file_to_list".
 */
#define ADDFILE_OK      1
#define ADDFILE_OOM     0
#define ADDFILE_OVERFLOW    -1


size_t
get_home(char* dest, const size_t ndest)
{
    char* path;
    size_t path_len;

    if(_dupenv_s(&path,&path_len,"USERPROFILE") == 0)
    {
        if(path_len <= ndest)
        {
            strncpy_s(dest,ndest,path,_TRUNCATE);
            path_len = 0;
        }
        free(path);
        return path_len;
    }

    char* drive;
    size_t drive_len;

    if(_dupenv_s(&drive,&drive_len,"HOMEDRIVE") == 0
    && _dupenv_s(&path,&path_len,"HOMEPATH") == 0)
    {
        size_t len = drive_len + path_len;
        if(len <= ndest)
        {
            strncpy_s(dest,3,drive,_TRUNCATE);
            strncat_s(dest,ndest-3,path,_TRUNCATE);
            len = 0;
        }
        free(path);
        free(drive);
        return len;
    }

    if(_dupenv_s(&path,&path_len,"HOME") == 0)
    {
        if(path_len <= ndest)
        {
            strncpy_s(dest,ndest,path,_TRUNCATE);
            path_len = 0;
        }
        free(path);
        return path_len;
    }

    return -1;
}


int
get_launch_config(LaunchConfig* config)
{
    int error = 0;
    log_to_file(J_LOG_FILE, J_LOG_SOURCE, "getting launch config...\n");
    if(get_java_path(config->java))
        error = 1;
    log_to_file(J_LOG_FILE, J_LOG_SOURCE, "java: '%s'\n", config->java, error);
    get_java_opts(config->java_opts);
    log_to_file(J_LOG_FILE, J_LOG_SOURCE, "java_opts: '%s'\n", config->java_opts);
    if(get_jedit_jar(config->jedit_jar))
        error = 1;
    log_to_file(J_LOG_FILE, J_LOG_SOURCE, "jedit_jar: '%s'\n", config->jedit_jar);
    get_jedit_opts(config->jedit_opts);
    log_to_file(J_LOG_FILE, J_LOG_SOURCE, "jedit_opts: '%s'\n", config->jedit_opts);
    get_working_dir(config->working_dir);
    log_to_file(J_LOG_FILE, J_LOG_SOURCE, "working_dir: '%s'\n", config->working_dir);
    log_to_file(J_LOG_FILE, J_LOG_SOURCE, "errors getting config: %d\n", error);
    return error;
}


// XXX add an "ndest" param for the size of output buffer
int
get_server_file(char* dest,
                const char* settings_dir,
                const char* filename)
{
    dest[0] = 0;
    char tmp[MAX_PATH] = {'\0'};
    char home[MAX_PATH] = {'\0'};

    // XXX need a path_concat function
    if(settings_dir)
    {
        strncpy(dest,settings_dir,MAX_PATH);
    }
    else
    {
        if(get_home(home,MAX_PATH))
            // XXX print an error
            return 1;
        strncpy(dest,home,MAX_PATH);
        strcat(dest,"\\.jedit");
    }

    strcat(dest,"\\");
    if(filename)
        strcat(dest,filename);
    else
        strcat(dest,"server");

    return 0;
}


int
get_edit_server_info(const char* server_file,
                     EditServerInfo * edit_server)
{
    if(!server_file)
    {
        log_to_file(J_LOG_FILE, J_LOG_SOURCE,"server_file is NULL\n");
        return 1;
    }
    log_to_file(J_LOG_FILE, J_LOG_SOURCE, "getting server info from '%s'\n",
                server_file);
    char first_line[64] = {'\0'};
    FILE* f;
    f = fopen(server_file, "r");
    if(0 == f)
    {
        log_to_file(J_LOG_FILE, J_LOG_SOURCE, "failed to open server file\n");
        return 1;
    }
    fscanf(f, "%s", first_line);
    fscanf(f, "%u", &edit_server->port);
    fscanf(f, "%u", &edit_server->key);
    fclose(f);
    log_to_file(J_LOG_FILE, J_LOG_SOURCE, "edit server port: %d, key: %d\n",
                edit_server->port, edit_server->key);
    return 0;
}

int
diff_files_in_jedit(const char* java,
                    const char* java_opts,
                    const char* jedit_jar,
                    const char* jedit_opts,
                    const char* working_dir,
                    const char* file1,
                    const char* file2)
{
    char escaped1[MAX_PATH] = {'\0'};
    char escaped2[MAX_PATH] = {'\0'};
    char script[DIFF_SCRIPT_LEN] = {'\0'};
    char server_file[MAX_PATH] = {'\0'};
    char* files[] = {0,0};
    EditServerInfo edit_server;

    escape_filename(file1, escaped1);
    escape_filename(file2, escaped2);

    sprintf(script, DIFF_SCRIPT, escaped1, escaped2);

    // XXX handle errors!
    // XXX pass additional args
    get_server_file(server_file,NULL,NULL);
    get_edit_server_info(server_file,&edit_server);

    log_to_file(J_LOG_FILE, J_LOG_SOURCE, "sending diff script...\n");
    if(send_script(edit_server.port, edit_server.key, script, 0)) /* XXX */
    {
        log_to_file(J_LOG_FILE, J_LOG_SOURCE, "sending diff script failed.\n");

        files[0] = (char*)file1;
        files[1] = (char*)file2;
        log_to_file(J_LOG_FILE, J_LOG_SOURCE, "attempting to launch jEdit...\n");
        if(open_files_in_jedit(java,
                               java_opts,
                               jedit_jar,
                               jedit_opts,
                               working_dir,
                               NULL,                    // XXX pass options
                               (const char**)files,
                               2))
            return 1;   // failed

        // make 10 attempts to find the server file
        // and send the diff script to the server
        //
        // XXX  THIS IS PRETTY CRAPPY.
        //      MIGHT BE BETTER TO GENERATE A TEMP.bsh FILE
        //      AND USE -run=TEMP.bsh INSTEAD.
        for(int i=0; i < 10; i++)
        {
            log_to_file(J_LOG_FILE, J_LOG_SOURCE, "waiting (%d) for server file...\n", i);
            Sleep(1500);    // XXX win32 specific
            if(!get_edit_server_info(server_file,&edit_server))
            {
                log_to_file(J_LOG_FILE, J_LOG_SOURCE, "sending diff script...\n");
                if(send_script(edit_server.port, edit_server.key, script, 0)) /* XXX */
                {
                    log_to_file(J_LOG_FILE, J_LOG_SOURCE, "sending diff script failed.\n");
                    return 1;
                }
                if(send_script(edit_server.port, edit_server.key, FOCUS_SCRIPT, 0)) /* XXX */
                {
                    log_to_file(J_LOG_FILE, J_LOG_SOURCE, "sending focus script faile.\n");
                    return 1;
                }
                return 0;
            }
        }
        log_to_file(J_LOG_FILE, J_LOG_SOURCE, "never found server file.\n");
        return 1;
    }
    else
    {
        log_to_file(J_LOG_FILE, J_LOG_SOURCE, "sending focus script...\n");
        return send_script(edit_server.port, edit_server.key, FOCUS_SCRIPT, 0); /* XXX */
    }
}

int
open_files_in_jedit(const char* java,
                    const char* java_opts,
                    const char* jedit_jar,
                    const char* jedit_opts,
                    const char* working_dir,
                    const OptList* opts,
                    const char** files,
                    const int nfiles)
{
    // XXX check for "server"
    char server_file[MAX_PATH] = {'\0'};
    int started = 0;
    EditServerInfo edit_server;
    int wait = has_opt(opts,"wait");                    // XXX const
    int new_plain_view = has_opt(opts,"newplainview");  // XXX const
    int new_view = has_opt(opts,"newview");         // XXX const
    int restore = has_opt(opts,"reuseview");            // XXX const
    const char* script_file = get_opt_value(opts,"run");    // XXX const
    const char* settings = get_opt_value(opts,"settings");
    const char* server = get_opt_value(opts,"server");
    char tmp[MAX_PATH] = {'\0'};
    if(script_file)
    {
        _fullpath(tmp, script_file, MAX_PATH);
        printf("tmp=%s\n", tmp);    // XXX
        script_file = tmp;
    }

    // XXX handle errors
    get_server_file(server_file,settings,server);

    if(!get_edit_server_info(server_file, &edit_server))
    {

        char* script = create_server_script(wait,
                                            restore,
                                            new_view,
                                            new_plain_view,
                                            script_file,
                                            working_dir,
                                            files,
                                            nfiles);
        // XXX handle NULL script

        log_to_file(J_LOG_FILE, J_LOG_SOURCE, "sending open script...\n");
        int error = send_script(edit_server.port, edit_server.key, script, wait); /* XXX */
        log_to_file(J_LOG_FILE, J_LOG_SOURCE, "send_script returned: %d\n", error);
        if(!error)
            started = 1;

    }

    // Either didn't find server file, or found server file
    // but wasn't able to connect to EditServer.
    if(0 == started)
    {
        log_to_file(J_LOG_FILE, J_LOG_SOURCE, "attempting to launch\n");
        return launch_jedit(java,
                            java_opts,
                            jedit_jar,
                            jedit_opts,
                            working_dir,
                            opts,
                            files,
                            nfiles);
    }

    return (started != 1);
}

int
expand_globs(const char** globs,
             const int nglobs,
             char** files,
             int nfiles)
{

    WIN32_FIND_DATA ffd;
    HANDLE hFind;

    int overflow = 0;
    int count = 0;
    char filename[MAX_PATH] = {'\0'};
    char directory[MAX_PATH] = {'\0'};
    int slash = -1;
    char cwd[MAX_PATH] = {'\0'};
    char path[MAX_PATH] = {'\0'};

    _getcwd(cwd,MAX_PATH);
    if(cwd[strlen(cwd)-1] != '\\')
        strcat(cwd,"\\");

    for(int i=0; i < nglobs; i++)
    {
        slash = -1;
        ZeroMemory(directory,MAX_PATH);

        // XXX use _splitpath
        // extract directory portion of path, forward- or backslash found
        for(int j=(int)strlen(globs[i])-1; j >= 0; j--)
        {
            if(globs[i][j] == '\\' || globs[i][j] == '/')
            {
                slash = j;
                strncpy(directory, globs[i], j+1);
                log_to_file(J_LOG_FILE, J_LOG_SOURCE, "directory for %s is %s\n",
                            globs[i], directory);
                break;
            }
        }

        hFind = FindFirstFile(globs[i], &ffd);
        if (hFind == INVALID_HANDLE_VALUE)
        {
            // assume the problem is that the file doesn't
            // exist, so just add to the list

            // XXX refactor this into a function
            if(strlen(directory))
                strcat(filename,directory);
            else
                strcat(filename,cwd);
            strcat(filename,globs[i]);
            _fullpath(path,filename,MAX_PATH);

            switch(add_file_to_list(path, files, nfiles, count))
            {
                case ADDFILE_OK:            // added
                    count++;
                    files++;
                    break;
                case ADDFILE_OOM:           // out of memory
                    // Not doing anything special to signal malloc failure.
                    // As long as we're not overwriting RAM that's not ours,
                    // since it means bad stuff's already going on.
                    goto DONE;
                case ADDFILE_OVERFLOW:      // list full
                    overflow++;
                    break;
            }

            continue;
        }
        else
        {
            if((ffd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) == FILE_ATTRIBUTE_DIRECTORY)
                goto FIND_FILES;

            ZeroMemory(filename,MAX_PATH);
            // add either given directory or current working directory
            if(strlen(directory))
                strcat(filename,directory);
            else
                strcat(filename,cwd);
            // concat directory and filename
            strcat(filename,ffd.cFileName);
            // absolute path
            _fullpath(path,filename,MAX_PATH);
            switch(add_file_to_list(path, files, nfiles, count))
            {
                case ADDFILE_OK:            // added
                    count++;
                    files++;
                    break;
                case ADDFILE_OOM:           // out of memory
                    // Not doing anything special to signal malloc failure.
                    // As long as we're not overwriting RAM that's not ours,
                    // since it means bad stuff's already going on.
                    goto DONE;
                case ADDFILE_OVERFLOW:      // list full
                    overflow++;
                    break;
            }

FIND_FILES:
            while(FindNextFile(hFind, &ffd))
            {
                if((ffd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) == FILE_ATTRIBUTE_DIRECTORY)
                    continue;

                // clear out filename -- for previous
                ZeroMemory(filename,MAX_PATH);
                // add either given directory or current working directory
                if(strlen(directory))
                    strcat(filename,directory);
                else
                    strcat(filename,cwd);
                // concat directory and filename
                strcat(filename,ffd.cFileName);
                // absolute path
                _fullpath(path,filename,MAX_PATH);
                // add to list
                switch(add_file_to_list(path, files, nfiles, count))
                {
                    case ADDFILE_OK:            // added
                        count++;
                        files++;
                        break;
                    case ADDFILE_OOM:           // out of memory
                        // Not doing anything special to signal malloc failure.
                        // As long as we're not overwriting RAM that's not ours,
                        // since it means bad stuff's already going on.
                        goto DONE;
                    case ADDFILE_OVERFLOW:      // list full
                        overflow++;
                        break;
                }
            }
            FindClose(hFind);
        }
    }

DONE:
    if(overflow)
        return -(overflow + count);
    return count;
}


int
add_file_to_list(const char* filename,
                 char** files,
                 const int nfiles,
                 const int count)
{
    char* tmp = NULL;

    log_to_file(J_LOG_FILE, J_LOG_SOURCE, "found: %s\n", filename);

    // if we've overflowed the list,
    // don't even bother copying
    if(count >= nfiles)
    {
        return ADDFILE_OVERFLOW;        // overflow
    }

    tmp = (char*)malloc(strlen(filename)+1);
    if(!filename)
    {
        return ADDFILE_OOM;
    }
    *files = strcpy(tmp, filename);
    log_to_file(J_LOG_FILE, J_LOG_SOURCE, "added: %s\n", tmp);
    return ADDFILE_OK;
}

char*
create_server_script(const int wait,
                     const int restore,
                     const int new_view,
                     const int new_plain_view,
                     const char * script_file,
                     const char * working_dir,
                     const char ** filenames,
                     unsigned int nfiles)
{
    char* script = (char*)malloc((1024*2) + MAX_PATH * 2 * nfiles);
    char escaped_name[MAX_PATH*2] = {'\0'};
    char buffer[250] = {'\0'};
    // XXX log an error
    if(!script)
    {
        log_to_file(J_LOG_FILE, J_LOG_SOURCE, "unable to allocate script in 'create_server_script\n");
        return script;
    }
    else
        script[0] = '\0';
    strcat(script, "parent = \"");
    if(working_dir)
    {
        escape_filename(working_dir, escaped_name);
        strncat(script, escaped_name, MAX_PATH*2);
    }
    strcat(script, "\";\n");

    sprintf(buffer, "args = new String[%d];\n", nfiles);
    strcat(script, buffer);
    for(unsigned int i=0; i < nfiles; i++)
    {
        sprintf(buffer, "args[%d] = \"", i);
        strcat(script, buffer);
        escape_filename(filenames[i], escaped_name);
        strncat(script, escaped_name, MAX_PATH*2);
        strcat(script, "\";\n");
    }

    strcat(script, "view = jEdit.getLastView();\n");
    sprintf(buffer, "buffer = EditServer.handleClient(%s, %s, %s, parent, args);\n",
                    int2bool(restore),
                    int2bool(new_view),
                    int2bool(new_plain_view));
    strcat(script, buffer);
    sprintf(buffer, "if(buffer != null && %s){\n", int2bool(wait));
    strcat(script, buffer);
    strcat(script, "    buffer.setWaitSocket(socket);\n");
    strcat(script, "    doNotCloseSocket = true;\n");
    strcat(script, "}\n");
    sprintf(buffer, "if(view != jEdit.getLastView() && %s){\n", int2bool(wait));
    strcat(script, buffer);
    strcat(script, "  jEdit.getLastView().setWaitSocket(socket);\n");
    strcat(script, "  doNotCloseSocket = true;\n");
    strcat(script, "}\n");
    strcat(script, "if(doNotCloseSocket == void)\n");
    strcat(script, "  socket.close();\n");

    // XXX need to make script_file an absolute path
    if(script_file)
    {
        printf("adding script: %s\n", script_file); // XXX
        escape_filename(script_file, escaped_name);
        printf("escaped_name: %s\n", escaped_name);     // XXX
        strcat(script, "BeanShell.runScript(view,\"");
        strcat(script, escaped_name);
        strcat(script, "\", null, this.namespace);\n");
    }

    strcat(script, "int state = view.getExtendedState();\n");
    strcat(script, "if((state & view.ICONIFIED) == view.ICONIFIED){ state ^= view.ICONIFIED; view.setExtendedState(state); }\n");
    strcat(script, "view.setVisible(true);\n");
    strcat(script, "view.getTextArea().requestFocus();\n");

    return script;
}

const char*
int2bool(const int value)
{
    return (value ? "true" : "false");
}

// XXX error handling
// XXX no buffer overrun
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
                        unsigned int ncmd)
{
    int error = 0;

    // quote java path if containing spaces, but don't double-quote
    if(strchr(java,' ') && java[0] != '"' && java[strlen(java)-1] != '"')
    {
        if(file_exists(java) != ERROR_SUCCESS)
        {
            log_to_file(J_LOG_FILE, J_LOG_SOURCE,
                        "java '%s' doesn't exist.\n", java);
            error = 2;
        }
        strcat(cmd, "\"");
        strcat(cmd, java);
        strcat(cmd, "\"");
    }
    else
        strcat(cmd, java);
    strcat(cmd, " ");

    if(java_opts && strlen(java_opts))
    {
        strcat(cmd, java_opts);
        strcat(cmd, " ");
    }

    // don't include -jar if in opts
    if(!strspn(java_opts,"-jar"))
        strcat(cmd, "-jar ");

    // quote jedit.jar path if containing spaces, but don't double-quote
    if(strchr(jedit_jar, ' ') && jedit_jar[0] != '"' && jedit_jar[strlen(jedit_jar)-1] != '"')
    {
        if(file_exists(jedit_jar) != ERROR_SUCCESS)
        {
            log_to_file(J_LOG_FILE, J_LOG_SOURCE,
                        "jedit.jar '%s' doesn't exist.\n", jedit_jar);
            error = 2;
        }
        strcat(cmd, "\"");
        strcat(cmd, jedit_jar);
        strcat(cmd, "\"");
    }
    else
        strcat(cmd, jedit_jar);
    strcat(cmd, " ");

    // add jedit options, if given
    if(jedit_opts && strlen(jedit_opts))
    {
        strcat(cmd, jedit_opts);
        strcat(cmd, " ");
    }

    // add command-line options, if given
    for(int i = 0; options != NULL && i < options->len; i++)
    {
        Opt* opt = &(options->opts[i]);
        strcat(cmd, "-");
        strcat(cmd, opt->name);
        if(opt->value && opt->value[0])
        {
            strcat(cmd, "=");
            if(strchr(opt->value, ' '))
            {
                strcat(cmd, "\"");
                strcat(cmd, opt->value);
                strcat(cmd, "\"");
            }
            strcat(cmd, opt->value);
        }
        strcat(cmd, " ");
    }

    // add files to open
    for(unsigned int i=0; i < nfiles; i++)
    {
        if(i > 0)
            strcat(cmd, " ");

        if(strchr(filenames[i], ' '))
        {
            strcat(cmd, "\"");
            strcat(cmd, filenames[i]);
            strcat(cmd, "\"");
        }
        else
            strcat(cmd, filenames[i]);
    }

    return error;
}

// XXX handle quoted paths, args, etc...
// XXX if working dir not given, determine from jedit.jar
int
launch_jedit(const char * java,
             const char * java_opts,
             const char * jedit_jar,
             const char * jedit_opts,
             const char * working_dir,
             const OptList * options,
             const char ** filenames,
             unsigned int nfiles)
{
    char cmd[MAX_PATH*10] = {'\0'};
    int error = 0;
    char* wdir = (char*)working_dir;

    create_launch_command(java, java_opts,
                            jedit_jar, jedit_opts,
                            working_dir, options,
                            filenames, nfiles,
                            cmd,
                            MAX_PATH*10);

    // handle empty or non-existing working directory
    if(!working_dir || strlen(working_dir) == 0)
    {
        log_to_file(J_LOG_FILE, J_LOG_SOURCE,
                    "working_dir not given, using current.\n");
        wdir = NULL;
    }
    else if(file_exists(working_dir) != ERROR_SUCCESS)
    {
        log_to_file(J_LOG_FILE, J_LOG_SOURCE,
                    "working_dir '%s' doesn't exist, using current.\n", working_dir);
        wdir = NULL;
    }

    log_to_file(J_LOG_FILE, J_LOG_SOURCE, "cmd:\n%s\n", cmd);

    STARTUPINFO si;
    PROCESS_INFORMATION pi;

    ZeroMemory(&si, sizeof(si));
    si.cb = sizeof(si);
    if(!CreateProcess(NULL,         // mo module name (use command line)
                      cmd,          // command line
                      NULL,         // process handle not inheritable
                      NULL,         // thread handle not inheritable
                      FALSE,        // no handle inheritance
                      0,            // no creation flags
                      NULL,         // parent's env
                      wdir,         // working dir for new process
                      &si,          // pointer to STARTUPINFO structure
                      &pi)          // pointer to PROCESS_INFORMATION structure
       )
    {
        error = GetLastError();
        log_to_file(J_LOG_FILE, J_LOG_SOURCE, "CREATE PROCESS FAILED: %d.\n", error);
        return 1;
    }
    else
    {
        CloseHandle(pi.hProcess);
        CloseHandle(pi.hThread);
    }
    return NOERROR;
}


size_t
escape_filename(const char* original,
                char* escaped)
{
    char* p = escaped;
    // XXX probably want to ensure we don't overrun
    for(size_t i=0; i < strlen(original) && i < MAX_PATH; i++)
    {
        if(original[i] == '\\')
        {
            *p = '\\';
            p++;
            *p = '\\';
            p++;
        }
        else
        {
            *p = original[i];
            p++;
        }
    }
    *p = NULL;// NULL-terminate
    return strlen(escaped);
}


int
send_script(unsigned int port,
            unsigned int key,
            const char* script,
            int wait)
{
    int err = 0;
    WORD wVersionRequested;
    WSADATA wsaData;
    SOCKET sock;

    wVersionRequested = MAKEWORD(2,2);
    err = WSAStartup(wVersionRequested, &wsaData);
    if(err)
    {
        log_to_file(J_LOG_FILE, J_LOG_SOURCE, "WSAStartup err: %d\n", err);
        return 1;
    }

    sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    sockaddr_in sock_info;

    sock_info.sin_family = AF_INET;
    sock_info.sin_addr.s_addr = inet_addr("127.0.0.1");  // localhost
    sock_info.sin_port = htons(port);

    unsigned long ulKey = htonl(key);  // key, prepped for send
    unsigned short usLen = htons((u_short)strlen(script));  // size of script

    err = connect(sock, (SOCKADDR*)&sock_info, sizeof(sock_info));
    if(err != 0)
    {
        log_to_file(J_LOG_FILE, J_LOG_SOURCE, "connect failed: result=%d, Error=%d\n",
                    err, GetLastError());
        WSACleanup();
        return err;
    }

    // XXX ensure everything gets sent
    int sent = 0;

    log_to_file(J_LOG_FILE, J_LOG_SOURCE, "sending script: %s\n", script);

    // send key
    sent = send(sock, (const char*)&ulKey, sizeof(unsigned long), 0);
    // send script len
    sent = send(sock, (const char*)&usLen, sizeof(unsigned short), 0);
    // send script
    sent = send(sock, script, (int)strlen(script), 0);

    // XXX clean up
    if(wait)
    {
        char read_buffer[255] = {'\0'};
        int r = recv(sock, read_buffer, 255, 0);
    }

    closesocket(sock);
    WSACleanup();

    return 0;
}

int
get_java_path(char* java)
{
    const char* java_home = 0;
    if(read_registry_string(SETTING_JAVA,java,MAX_PATH))
    {
        // use %JAVA_HOME% if not read from registry
        java_home = getenv("JAVA_HOME");
        if(!java_home)
            return 1;

        strcpy(java, java_home);
        if(java[strlen(java)] != '\\')
            strcat(java, "\\");
        strcat(java,"bin\\javaw.exe");
    }

    return (java == 0);
}

int
set_java_path(const char* java)
{
    return write_registry_string(SETTING_JAVA,java);
}

int
get_java_opts(char* java_opts)
{
    const char* opts = 0;
    if(read_registry_string(SETTING_JAVA_OPTS,
                            java_opts,
                            MAX_PATH))
    {
        // use env JAVA_OPTS if not read
        opts = getenv("JAVA_OPTS");
        if(opts)
        {
            strcpy(java_opts, opts);
            return 0;
        }
        return 1;
    }
    return 0;
}

int
set_java_opts(const char* java_opts)
{
    return write_registry_string(SETTING_JAVA_OPTS,java_opts);
}

int
get_jedit_jar(char* jedit_jar)
{
    const char* jar = getenv("JEDIT_JAR");
    if(jar)
    {
        strcpy(jedit_jar, jar);
        return 0;
    }

    return read_registry_string(SETTING_JEDIT_JAR,
                                jedit_jar,
                                MAX_PATH);
}

int
set_jedit_jar(const char* jedit_jar)
{
    return write_registry_string(SETTING_JEDIT_JAR,jedit_jar);
}

int
get_jedit_opts(char* jedit_opts)
{
    const char* opts = getenv("JEDIT_OPTS");
    if(opts)
    {
        strcpy(jedit_opts, opts);
        return 0;
    }

    return read_registry_string(SETTING_JEDIT_OPTS,
                                jedit_opts,
                                MAX_PATH);
}

int
set_jedit_opts(const char* jedit_opts)
{
    return write_registry_string(SETTING_JEDIT_OPTS,jedit_opts);
}

int
get_working_dir(char* working_dir)
{
    return read_registry_string(SETTING_WORKING_DIR,
                                working_dir,
                                MAX_PATH);
}

int
set_working_dir(const char* working_dir)
{
    return write_registry_string(SETTING_WORKING_DIR,working_dir);
}

int read_registry_string(const char* name,
                         char* value,
                         const int nvalue)
{
    HKEY hKey;
    DWORD dwLen = nvalue;
    value[0] = 0;

    if(RegOpenKeyEx(HKEY_CURRENT_USER, REG_SETTINGS_PATH, 0,
                    KEY_READ, &hKey) == ERROR_SUCCESS)
    {
        if(ERROR_SUCCESS != RegQueryValueEx(hKey, name, 0,
                                            NULL, (BYTE*)value, &dwLen))
            value[0] = 0;
        else
            value[dwLen] = 0;
        RegCloseKey(hKey);
    }
    else
        return 1;

    return (value[0] == 0);
}

int write_registry_string(const char* name,
                            const char* value)
{
    HKEY hKey;
    int r = 1;
    r = RegCreateKeyEx(HKEY_CURRENT_USER, REG_SETTINGS_PATH, 0,
                        NULL, REG_OPTION_NON_VOLATILE,
                        KEY_WRITE | KEY_SET_VALUE, NULL,
                        &hKey, NULL);
    if(r == ERROR_SUCCESS)
    {
        r = RegSetValueEx(hKey,name,0,REG_SZ,(const BYTE*)value,(DWORD)strlen(value));
        if(r == ERROR_SUCCESS)
            r = 0;
        else
            log_to_file(J_LOG_FILE,J_LOG_SOURCE,"Error setting registry value (%s=%s): %d\n", name,value,r);
        RegCloseKey(hKey);
    }
    else
        log_to_file(J_LOG_FILE,J_LOG_SOURCE,"Error opening registry key: %d\n", r);
    return r;
}

int
log_to_file(const char* filename,
            const char* source,
            const char* format,
            ...)
{
    struct tm *newtime;
    time_t clock;
    char path[MAX_PATH] = {'\0'};
    char home[MAX_PATH] = {'\0'};
    char prefix[MAX_PATH] = {'\0'};
    // XXX const char home = NULL;
    if(format == NULL || strlen(format) == 0)
        return 0;
    va_list args;
    if(get_home(home,MAX_PATH))
        return 1;
    strcpy(path,home);
    if(path[strlen(path)-1] != '\\' && path[strlen(path)-1] != '/'){
        strcat(path,"\\");
    }
    strcat(path,filename);
    FILE* log = fopen(path,"a");
    if(!log)
        return 1;
    time(&clock);
    newtime = localtime(&clock);
    sprintf(prefix, "%s:%s", source, asctime(newtime));
    if(prefix[strlen(prefix)-1] == '\n')
        prefix[strlen(prefix)-1] = '\0';
    fprintf(log, "%s:", prefix);
    va_start(args, format);
    vfprintf(log, format, args);
    va_end(args);
    fclose(log);
    return 0;
}

OptList*
parse_args(int argc,
           const char** argv)
{
    int nopts = 0;
    int i;

    // do one pass through to count the number of args
    for(i=1; i < argc; i++)
    {
        const char* arg = argv[i];
        if(strcmp(arg, "--") == 0)  /* end of options */
            break;
        else if(arg[0] != '-')      /* not an option */
            break;
        else
        {
            nopts++;
        }
    }

    // allocate arg list
    OptList* opts = (OptList*)malloc(sizeof(OptList));
    if(!opts)
    {
        printf("ERROR: Out of Memory\n");
        log_to_file(J_LOG_FILE, J_LOG_SOURCE, "Out of memory (for OptList*)");
        return NULL;
    }

    // set number of options
    opts->len = nopts;
    // if no options, no need for second pass
    if(!nopts)
        return opts;

    // allocate args list to hold args
    opts->opts = (Opt*)malloc(sizeof(Opt)*nopts);
    if(!opts->opts)
    {
        free(opts);
        printf("ERROR: Out of Memory\n");
        log_to_file(J_LOG_FILE, J_LOG_SOURCE, "Out of memory (for opts->opts)");
        return NULL;
    }

    Opt* p = opts->opts;    // current arg pointer
    for(i=1; i < argc; i++)
    {
        const char* arg = argv[i];
        if(strcmp(arg, "--") == 0)  /* end of options */
            break;
        else if(arg[0] != '-')      /* not an option */
            break;
        else
        {
            // zero-out current arg
            p->name[0] = 0;
            p->value[0] = 0;
            // vars to hold copies of extracted portions
            char name[OPT_NAME_LEN] = {'\0'};
            char value[OPT_VALUE_LEN] = {'\0'};
            log_to_file(J_LOG_FILE, J_LOG_SOURCE,
                        "arg[%d], strlen(%s)=%d\n", i, arg, strlen(arg));   // XXX
            for(size_t j=1; j < strlen(arg); j++)
            {
                if(arg[j] == '=')
                {
                    // copy name (exclude "-" and "=")
                    strncpy(name, arg+1, j-1);
                    // copy value (exclude "=")
                    strncpy(value, arg+j+1, OPT_VALUE_LEN);
                    log_to_file(J_LOG_FILE, J_LOG_SOURCE,
                                "arg[%d]: %s, name=%s, value=%s\n", i, arg, name, value);   // XXX
                    strcpy(p->name,name);
                    strcpy(p->value,value);
                    log_to_file(J_LOG_FILE, J_LOG_SOURCE,
                                "arg[%d]: %s, name=%s, value=%s\n", i, arg, p->name, p->value); // XXX
                    goto NEXT;
                }
            }

            log_to_file(J_LOG_FILE, J_LOG_SOURCE,
                        "didn't find '=', so just using name: %s\n", arg+1);    // XXX
            strncpy(p->name, arg+1, OPT_NAME_LEN);

NEXT:

            p++;    // pointer to next arg
        }
    }

    return opts;
}

// XXX document
const char*
get_opt_value(const OptList* opts,
              const char* name)
{
    if(NULL == opts)
        return NULL;

    for(int i=0; i < opts->len; i++)
        if(strcmp(opts->opts[i].name,name) == 0)
            return (const char*)opts->opts[i].value;
    return NULL;
}

int
has_opt(const OptList* opts,
        const char* name)
{
    if(NULL == opts)
        return 0;
    for(int i=0; i < opts->len; i++)
        if(strcmp(opts->opts[i].name,name) == 0)
            return 1;
    return 0;
}

/**
 * Return 0 if a file exists, otherwise non-zero.
 */
int
file_exists(const char* filename)
{
    struct _stat buf;
    return _stat(filename, &buf);
}

char*
strip_quotes(char* s)
{
    if(s && s[0] == '"' && s[strlen(s)-1] == '"')
    {
        for(size_t i=1; i < strlen(s); i++)
            s[i-1]=s[i];
        s[strlen(s)-2] = 0;
    }
    return s;
}
