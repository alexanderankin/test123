/*
 * ProcyonVFS.java
 * Copyright (c) 2014 Tim Blackler
 *
 * jEdit edit mode settings:
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */


package javainsight;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import javainsight.buildtools.ChainedIOException;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.Log;

import com.strobel.assembler.InputTypeLoader;
import com.strobel.assembler.metadata.DeobfuscationUtilities;
import com.strobel.assembler.metadata.MetadataSystem;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import com.strobel.decompiler.languages.Languages;
import com.strobel.decompiler.languages.TypeDecompilationResults;
import com.strobel.decompiler.languages.java.JavaFormattingOptions;


public class ProcyonVFS extends ByteCodeVFS
{
    public static final String PROTOCOL = "procyon";

    public ProcyonVFS() {
        super(PROTOCOL);
    }


    /**
     * Creates an input stream. This method is called from the I/O
     * thread.
     * @param session the VFS session
     * @param path The path
     * @param ignoreErrors If true, file not found errors should be
     * ignored
     * @param comp The component that will parent error dialog boxes
     * @exception IOException If an I/O error occurs
     */
    @Override
    public InputStream _createInputStream(Object session,
        String path, boolean ignoreErrors, Component comp)
        throws IOException
    {
        String pathToClass = path;
        if (path.startsWith(PROTOCOL + ':')) {
            pathToClass = pathToClass.substring(PROTOCOL.length() + 1);
        }

        VFS vfs = VFSManager.getVFSForPath(pathToClass);

        if (pathToClass.endsWith(".marks")) {
            return null;
        }


        try {
            // Get the VFS path
            String vfsPath = vfs.getParentOfPath(pathToClass);

            Log.log(Log.DEBUG, this, 
                "className=" + pathToClass
                + " vfsPath=" + vfsPath
                + " pathToClass=" + pathToClass);

            final DecompilationOptions options = this.decompilerOptions();
            
            ByteArrayOutputStream baOut = new ByteArrayOutputStream();
            OutputStreamWriter output = new OutputStreamWriter(baOut,
            		options.getSettings().isUnicodeOutputEnabled() ? Charset.forName("UTF-8")
                            : Charset.defaultCharset());

            final MetadataSystem metadataSystem = new MetadataSystem(options.getSettings().getTypeLoader());
            metadataSystem.setEagerMethodLoadingEnabled(jEdit.getBooleanProperty("javainsight.procyon.disableforeach", false));         
            
            decompile(pathToClass, options, metadataSystem, output);
            output.close();
            
            return new BufferedInputStream(new ByteArrayInputStream(baOut.toByteArray()));
        } catch (IOException ioex) {
            throw ioex;
        } catch (Throwable t) {
            throw new ChainedIOException("An error occured while decompiling " + pathToClass, t);
        }
    }


    private void decompile(String className, DecompilationOptions options, MetadataSystem metadataSystem , Writer decompilerOutput) throws IOException {

        final TypeReference type;
        final DecompilerSettings settings = options.getSettings();

        type = metadataSystem.lookupType(className);
        final TypeDefinition resolvedType;

        if (type == null || (resolvedType = type.resolve()) == null) {
            Log.log(Log.ERROR, this, "Failed to load class " + className + ".\n");
            return;
        }

        DeobfuscationUtilities.processType(resolvedType);
  
        final PlainTextOutput output = new PlainTextOutput(decompilerOutput);;

        output.setUnicodeOutputEnabled(settings.isUnicodeOutputEnabled());

        @SuppressWarnings("unused")
		final TypeDecompilationResults results = settings.getLanguage().decompileType(resolvedType, output, options);

        decompilerOutput.flush();
    }

    private DecompilationOptions decompilerOptions() {
        final DecompilerSettings settings = new DecompilerSettings();

        settings.setFlattenSwitchBlocks(jEdit.getBooleanProperty("javainsight.procyon.flattenswitch", false));
        settings.setForceExplicitImports(jEdit.getBooleanProperty("javainsight.procyon.explicitimport", false));
        settings.setForceExplicitTypeArguments(jEdit.getBooleanProperty("javainsight.procyon.explicittypes", false));
        settings.setRetainRedundantCasts(jEdit.getBooleanProperty("javainsight.procyon.retaincasts", false));
        settings.setShowSyntheticMembers(jEdit.getBooleanProperty("javainsight.procyon.showsynth", false));
        settings.setExcludeNestedTypes(jEdit.getBooleanProperty("javainsight.procyon.excludenested", false));
        settings.setRetainPointlessSwitches(jEdit.getBooleanProperty("javainsight.procyon.retainswitches", false));
        settings.setUnicodeOutputEnabled(false);
        settings.setMergeVariables(jEdit.getBooleanProperty("javainsight.procyon.merge", false));
        settings.setShowDebugLineNumbers(false);
        settings.setSimplifyMemberReferences(jEdit.getBooleanProperty("javainsight.procyon.simplifymember", false));
        settings.setDisableForEachTransforms(jEdit.getBooleanProperty("javainsight.procyon.disableforeach", false));
        settings.setTypeLoader(new InputTypeLoader());
        settings.setLanguage(Languages.java());

        DecompilationOptions decompilationOptions = new DecompilationOptions();

        decompilationOptions.setSettings(settings);
        decompilationOptions.setFullDecompilation(true);

        if (settings.getFormattingOptions() == null) {
            settings.setFormattingOptions(JavaFormattingOptions.createDefault());
        }
        
        return decompilationOptions;
    }
    
}
