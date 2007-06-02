#include <stdio.h>
#include <jni.h>
#include "gdb_jni_GdbProcess.h"

static pid_t pid;

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    start
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_gdb_jni_GdbProcess_start
  (JNIEnv *, jobject, jstring gdb, jstring prog, jstring args, jstring wdir, jstring env);
{
	int    i;
	char   buf[BUF_SIZE];
	
	pid = fork();
	if (pid < 0) {
		fprintf(stderr, "Error: fork() failed\n");
		exit(-1);
	}
	if (pid != 0) {
		// Parent process - communicates with the JNI
		return;
	}
	
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    end
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_gdb_jni_GdbProcess_end
  (JNIEnv *, jobject)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    pauseProgram
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_gdb_jni_GdbProcess_pauseProgram
  (JNIEnv *, jobject)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    getGdbErrorStream
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_gdb_jni_GdbProcess_getGdbErrorStream
  (JNIEnv *, jobject)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    getGdbOutputStream
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_gdb_jni_GdbProcess_getGdbOutputStream
  (JNIEnv *, jobject)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    getGdbInputStream
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_gdb_jni_GdbProcess_getGdbInputStream
  (JNIEnv *, jobject)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    getProgramErrorStream
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_gdb_jni_GdbProcess_getProgramErrorStream
  (JNIEnv *, jobject)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    getProgramOutputStream
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_gdb_jni_GdbProcess_getProgramOutputStream
  (JNIEnv *, jobject)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    getProgramInputStream
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_gdb_jni_GdbProcess_getProgramInputStream
  (JNIEnv *, jobject)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    isReady
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_gdb_jni_GdbProcess_isReady
  (JNIEnv *, jobject, jint)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    readLine
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_gdb_jni_GdbProcess_readLine
  (JNIEnv *, jobject, jint)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    write
 * Signature: (ILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_gdb_jni_GdbProcess_write
  (JNIEnv *, jobject, jint, jstring)
{
}


