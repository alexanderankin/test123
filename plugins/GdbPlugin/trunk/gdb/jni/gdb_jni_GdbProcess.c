#include <stdio.h>
#include <jni.h>
#include <string.h>
#include <unistd.h>
#include "gdb_jni_GdbProcess.h"

static pid_t pid;

static char *copyJString(JNIEnv *env, jstring js)
{
	char *s = (*env)->GetStringUTFChars(env, js, NULL);
	char *copy = strdup(s);
	(*env)->ReleaseStringUTFChars(env, js, s);
	return copy;
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    start
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_gdb_jni_GdbProcess_start
(JNIEnv *env, jobject obj, jstring gdb, jstring prog, jstring args,
 jstring wdir, jstring envStr)
{
	pid = fork();
	if (pid < 0) {
		fprintf(stderr, "Error: fork() failed\n");
		return;
	}
	if (pid != 0) {
		// Parent process - communicates with the JNI
		return;
	}
	// Prepare the command-line
	char *argv[4];
	argv[0] = copyJString(env, gdb);
	argv[1] = "--interpreter=mi";
	argv[2] = copyJString(env, prog);
	argv[3] = 0;
	// Set the working directory
	chdir(copyJString(env, wdir));
	// Adjust the environment
	char *envCopy = copyJString(env, envStr);
	char *s = strtok(envCopy, ",");
	while (s) {
		putenv(s);
		s = strtok(0, ",");
	}
	execv(argv[0], argv);
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    end
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_gdb_jni_GdbProcess_end
(JNIEnv *env, jobject obj)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    pauseProgram
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_gdb_jni_GdbProcess_pauseProgram
(JNIEnv *env, jobject obj)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    getGdbErrorStream
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_gdb_jni_GdbProcess_getGdbErrorStream
(JNIEnv *env, jobject obj)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    getGdbOutputStream
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_gdb_jni_GdbProcess_getGdbOutputStream
(JNIEnv *env, jobject obj)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    getGdbInputStream
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_gdb_jni_GdbProcess_getGdbInputStream
(JNIEnv *env, jobject obj)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    getProgramErrorStream
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_gdb_jni_GdbProcess_getProgramErrorStream
(JNIEnv *env, jobject obj)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    getProgramOutputStream
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_gdb_jni_GdbProcess_getProgramOutputStream
(JNIEnv *env, jobject obj)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    getProgramInputStream
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_gdb_jni_GdbProcess_getProgramInputStream
(JNIEnv *env, jobject obj)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    isReady
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_gdb_jni_GdbProcess_isReady
(JNIEnv *env, jobject obj, jint stream)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    readLine
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_gdb_jni_GdbProcess_readLine
(JNIEnv *env, jobject obj, jint stream)
{
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    write
 * Signature: (ILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_gdb_jni_GdbProcess_write
(JNIEnv *env, jobject obj, jint stream, jstring s)
{
}


