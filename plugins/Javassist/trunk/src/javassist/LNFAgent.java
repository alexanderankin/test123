package javassist;

import java.io.*;
import java.lang.instrument.*;
import java.security.ProtectionDomain;

/**
 * Simple java agent to fix the <code>provideErrorFeedback</code> in javax.swing.LookAndFeel.
 * This adds a line to that method to check the System property "allowBeep" before it 
 * proceeds with beeping. If "allowBeep" is anything other than "true", the method returns
 * immediately without providing any error feedback.
 */
public final class LNFAgent implements ClassFileTransformer {

    private static final String CLASS_TO_PATCH = "javax/swing/LookAndFeel";

    public static void premain( String agentArgument, final Instrumentation instrumentation ) {
        LNFAgent agent = null;

        try {
            agent = new LNFAgent();
        } catch ( Exception e ) {
            System.out.println("LNFAgent not installed.");
            return;
        }
        instrumentation.addTransformer( agent );
        
        // signal the LookAndFeel plugin that this agent is installed. Since this agent loads
        // into the JVM well before the LookAndFeel plugin, setting a system property is an
        // acceptable way to do this.
        System.setProperty("LNFAgentInstalled", "true");
    }

    @Override
    public byte[] transform( final ClassLoader loader, String className, final Class classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer ) throws IllegalClassFormatException {
        byte[] result = null;

        if ( className.equals( CLASS_TO_PATCH ) ) {
            try {
                CtClass ctClass = ClassPool.getDefault().makeClass( new DataInputStream( new ByteArrayInputStream( classfileBuffer ) ) );
                CtMethod ctMethod = ctClass.getDeclaredMethod( "provideErrorFeedback" );
                ctMethod.insertBefore( "if (!\"true\".equals(System.getProperty(\"allowBeep\"))) return;" );
                result = ctClass.toBytecode();
            } catch ( Exception e ) {
                System.err.println("Unable to patch javax.swing.LookAndFeel to disable beeps.");
            }
        }
        return result;
    }
}