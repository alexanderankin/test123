TextAutocomplete Development
============================

This plugin uses jEdit's `build-support` for building. We will now describe how
to build the plugin and then how to import it into IntelliJ as a project.

Retrieve all the sources
------------------------

Create the root directory for the development:

    mkdir /tmp/jedit
    cd /tmp/jedit

Create a symlink called `jEdit` to a directory that containd `jedit.jar` (required by `build-support`);
this depends on where you have installed jEdit, on my machine it is:

    jedit$ ln -s /Applications/MyTools/editors/jEdit.app/Contents/Resources/Java/ jEdit


Check out `build-support`:

    jedit$ svn co https://jedit-plugins.svn.sourceforge.net/svnroot/jedit/build-support/trunk/ build-support

Create a directory for the plugin with build properties pointing to `build-support` and JUnit:

    jedit$ mkdir plugins
    jedit$ cd plugins
    plugins$ touch build.properties

The content of `plugins/build.properties` should be (adjust as needed):

    # Properties needed for builds:
    build.support=../../build-support
    junit.jar=${user.home}/.m2/repository/junit/junit/4.11/junit-4.11.jar

Check out the plugin itself:

    plugins$ svn co https://jedit.svn.sourceforge.net/svnroot/jedit/plugins/TextAutocomplete/trunk TextAutocomplete

Finally, [download source codes](http://www.jedit.org/index.php?page=download) of the latest jEdit release and extract
them into a directory, for example `/tmp/jedit/jEdit5.0.0` - this is not needed for builidng but useful for
working with the project in an IDE.

You should end up with structur like this:

    /tmp/jedit/
        build-support/
        jEdit/ -> /path/to/directory/containing/jedit.jar/
        jEdit5.0.0/
        plugins/
            TextAutocomplete/
            build.properties

Build
------

Building is now as easy as:

    TextAutocomplete$ ant build

which will build `TextAutocomplete.jar` in the parent `plugins/` directory.

Import the project into an IDE
------------------------------

To import the project into an IDE such as IntelliJ:

1. Create a new project, with sources at the location of the TextAutocomplete/ directory
2. Add `jedit.jar` (f.ex. the jEdit/jedit.jar used when building) to the project's libraries
3. Set the location of the source codes of `jedit.jar` to the source directory (jEdit5.0.0 above)
