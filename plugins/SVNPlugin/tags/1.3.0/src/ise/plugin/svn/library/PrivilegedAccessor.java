/*
Copyright (c) 2007, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ise.plugin.svn.library;

import java.lang.reflect.*;

/**
 * a.k.a. The "ObjectMolester" <p>
 *
 * This class is used to access a method or field of an object no matter what
 * the access modifier of the method or field. The syntax for accessing fields
 * and methods is out of the ordinary because this class uses reflection to peel
 * away protection. <p>
 *
 * Here is an example of using this to access a private member. <code>resolveName</code>
 * is a private method of <code>Class</code>. <pre>
 * Class c = Class.class;
 * System.out.println(
 *      PrivilegedAccessor.invokeMethod( c,
 *                                       "resolveName",
 *                                       "/ise/library/PrivilegedAccessor" ) );
 * </pre>
 *
 * @author    Charlie Hubbard (chubbard@iss.net)
 * @author    Prashant Dhokte (pdhokte@iss.net)
 * @author    Dale Anson (danson@germane-software.com)
 * @version   $Revision: 1.5 $
 */

public class PrivilegedAccessor {

    /**
     * Gets the value of the named field and returns it as an object.
     *
     * @param instance                    the object instance
     * @param fieldName                   the name of the field
     * @return                            an object representing the value of
     *      the field
     * @exception IllegalAccessException  Description of Exception
     * @exception NoSuchFieldException    Description of Exception
     */
    public static Object getValue( Object instance, String fieldName )
    throws IllegalAccessException, NoSuchFieldException {
        Field field = getField( instance.getClass(), fieldName );
        field.setAccessible( true );
        return field.get( instance );
    }

    /**
     * Sets the value of the named field.
     *
     * @param instance                    the object instance
     * @param fieldName                   the name of the field
     * @param value                       the value to set for the field
     * @exception IllegalAccessException  Description of Exception
     * @exception NoSuchFieldException    Description of Exception
     */
    public static void setValue( Object instance, String fieldName, Object value ) throws IllegalAccessException, NoSuchFieldException {
        Field field = getField( instance.getClass(), fieldName );
        field.setAccessible( true );
        field.set( instance, value );
    }

    /**
     * Gets the value of the named static field and returns it as an object.
     *
     * @param fieldName                   the name of the field
     * @param c
     * @return                            an object representing the value of
     *      the field
     * @exception IllegalAccessException  Description of Exception
     * @exception NoSuchFieldException    Description of Exception
     */
    public static Object getStaticValue( Class c, String fieldName )
    throws IllegalAccessException, NoSuchFieldException {
        Field field = getField( c, fieldName );
        field.setAccessible( true );
        return field.get( null );
    }

    /**
     * Calls a method on the given object instance with the given argument.
     *
     * @param instance                       the object instance
     * @param methodName                     the name of the method to invoke
     * @param arg                            the argument to pass to the method
     * @return                               Description of the Returned Value
     * @exception NoSuchMethodException      Description of Exception
     * @exception IllegalAccessException     Description of Exception
     * @exception InvocationTargetException  Description of Exception
     * @see
     *      PrivilegedAccessor#invokeMethod(Object,String,Object[])
     */
    public static Object invokeMethod( Object instance, String methodName, Object arg ) throws NoSuchMethodException,
        IllegalAccessException, InvocationTargetException {
        Object[] args = new Object[ 1 ];
        args[ 0 ] = arg;
        return invokeMethod( instance, methodName, args );
    }

    /**
     * Calls a method on the given object instance with the given arguments.
     * REQUIRES Java 1.5+
     *
     * @param instance                       the object instance
     * @param methodName                     the name of the method to invoke
     * @param args                           an array of objects to pass as
     *      arguments
     * @return                               Description of the Returned Value
     * @exception NoSuchMethodException      Description of Exception
     * @exception IllegalAccessException     Description of Exception
     * @exception InvocationTargetException  Description of Exception
     * @see
     *      PrivilegedAccessor#invokeMethod(Object,String,Object)
     */
    public static Object invokeMethod( Object instance, String methodName, Object[] args ) throws NoSuchMethodException,
        IllegalAccessException, InvocationTargetException {
        //Class[] classTypes = getClassArray( args );
        //return getMethod( instance, methodName, classTypes ).invoke( instance, args );
        if ( args == null )
            args = new Object[] {};
        Class[] classTypes = getClassArray( args );
        Class c = instance.getClass();
        Method[] methods = c.getDeclaredMethods();
        while ( c != null ) {
            for ( int i = 0; i < methods.length; i++ ) {
                Method method = methods[ i ];
                Class[] paramTypes = method.getParameterTypes();
                if ( method.getName().equals( methodName ) && compare( paramTypes, args ) ) {
                    method.setAccessible( true );
                    return method.invoke( instance, args );
                }
            }
            methods = getSuperclassMethods( c );
            c = c.getSuperclass();
        }
        StringBuffer sb = new StringBuffer();
        sb.append( "No method named " ).append( methodName ).append( " found in " ).append( instance.getClass().getName() ).append( " with parameters (" );
        for ( int x = 0; x < classTypes.length; x++ ) {
            sb.append( classTypes[ x ].getName() );
            if ( x < classTypes.length - 1 )
                sb.append( ", " );
        }
        sb.append( ")" );
        throw new NoSuchMethodException( sb.toString() );

    }

    //private static Method[] getSuperclassMethods( Object instance ) {
    //    return getSuperclassMethods(instance.getClass());
    //}

    private static Method[] getSuperclassMethods(Class c) {
        return ( ( Class ) c.getGenericSuperclass() ).getDeclaredMethods();
    }

    /**
     * Converts the object array to an array of Classes.
     *
     * @param args  the object array to convert
     * @return      a Class array
     */
    private static Class[] getClassArray( Object[] args ) {
        Class[] classTypes = null;
        if ( args != null ) {
            classTypes = new Class[ args.length ];
            for ( int i = 0; i < args.length; i++ ) {
                if ( args[ i ] != null )
                    classTypes[ i ] = args[ i ].getClass();
            }
        }
        return classTypes;
    }

    /**
     * @param instance                   the object instance
     * @param methodName                 the
     * @param classTypes
     * @return                           The method value
     * @exception NoSuchMethodException  Description of Exception
     */
    public static Method getMethod( Object instance, String methodName, Class[] classTypes ) throws NoSuchMethodException {
        Method accessMethod = getMethod( instance.getClass(), methodName, classTypes );
        accessMethod.setAccessible( true );
        return accessMethod;
    }

    /**
     * Return the named field from the given class.
     *
     * @param thisClass
     * @param fieldName
     * @return                          The field value
     * @exception NoSuchFieldException  Description of Exception
     */
    public static Field getField( Class thisClass, String fieldName ) throws NoSuchFieldException {
        if ( thisClass == null )
            throw new NoSuchFieldException( "Invalid field : " + fieldName );
        try {
            return thisClass.getDeclaredField( fieldName );
        }
        catch ( NoSuchFieldException e ) {
            return getField( thisClass.getSuperclass(), fieldName );
        }
    }

    /**
     * Return the named method with a method signature matching classTypes from
     * the given class.
     *
     * @param thisClass
     * @param methodName
     * @param classTypes
     * @return                           The method value
     * @exception NoSuchMethodException  Description of Exception
     */
    @SuppressWarnings("unchecked")
    public static Method getMethod( Class thisClass, String methodName, Class[] classTypes ) throws NoSuchMethodException {
        if ( thisClass == null )
            throw new NoSuchMethodException( "Invalid method : " + methodName );
        try {
            return thisClass.getDeclaredMethod( methodName, classTypes );
        }
        catch ( NoSuchMethodException e ) {
            return getMethod( thisClass.getSuperclass(), methodName, classTypes );
        }
    }

    /**
     * Description of the Method
     *
     * @param c
     * @param args
     * @return      Description of the Returned Value
     */
    private static boolean compare( Class[] c, Object[] args ) {
        if ( c.length != args.length ) {
            return false;
        }
        if ( c.length == 0 )
            return true;
        for ( int i = 0; i < args.length; i++ ) {
            if ( !c[ i ].isInstance( args[ i ] ) ) {
                return false;
            }
        }
        return true;
    }


    /**
     * Creates a new instance of the named class initialized with the given
     * arguments.
     *
     * @param classname                      the name of the class to
     *      instantiate.
     * @param args                           the arguments to pass as parameters
     *      to the constructor of the class.
     * @return                               the instantiated object
     * @exception ClassNotFoundException     Description of Exception
     * @exception InstantiationException     Description of Exception
     * @exception NoSuchMethodException      Description of Exception
     * @exception IllegalAccessException     Description of Exception
     * @exception InvocationTargetException  Description of Exception
     */
    public static Object getNewInstance( String classname, Object[] args )
    throws ClassNotFoundException, InstantiationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if ( classname == null )
            throw new ClassNotFoundException();
        return getNewInstance( Class.forName( classname ), args );
    }

    /**
     * Gets the newInstance attribute of the PrivilegedAccessor class
     *
     * @param c
     * @param args
     * @return                               The newInstance value
     * @exception ClassNotFoundException     Description of Exception
     * @exception InstantiationException     Description of Exception
     * @exception NoSuchMethodException      Description of Exception
     * @exception IllegalAccessException     Description of Exception
     * @exception InvocationTargetException  Description of Exception
     */
    public static Object getNewInstance( Class c, Object[] args )
    throws ClassNotFoundException, InstantiationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if ( c == null )
            throw new ClassNotFoundException();
        if ( args == null )
            args = new Object[] {};

        Class[] classTypes = getClassArray( args );
        Constructor[] constructors = c.getConstructors();
        for ( int i = 0; i < constructors.length; i++ ) {
            Constructor constructor = constructors[ i ];
            Class[] paramTypes = constructor.getParameterTypes();
            if ( compare( paramTypes, args ) ) {
                return constructor.newInstance( args );
            }
        }
        StringBuffer sb = new StringBuffer();
        sb.append( "No constructor found for " ).append( c.getName() ).append( " with parameters (" );
        for ( int x = 0; x < classTypes.length; x++ ) {
            sb.append( classTypes[ x ].getName() );
            if ( x < classTypes.length - 1 )
                sb.append( ", " );
        }
        sb.append( ")" );
        throw new NoSuchMethodException( sb.toString() );
    }
}
