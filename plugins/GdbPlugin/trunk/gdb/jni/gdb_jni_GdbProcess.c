#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>
#include <fcntl.h>
#include "gdb_jni_GdbProcess.h"

static pid_t pid;

typedef struct {
	char *b;
	int size;
	int cnt;
} Buffer;
Buffer out, err;
int outPipe[2], errPipe[2], inPipe[2];

static char *copyJString(JNIEnv *env, jstring js)
{
	char *s = (*env)->GetStringUTFChars(env, js, NULL);
	char *copy = strdup(s);
	(*env)->ReleaseStringUTFChars(env, js, s);
	return copy;
}

static void initBuffer(Buffer *buf, int size)
{
	buf->b = malloc(sizeof(char) * size);
	buf->size = size;
	buf->cnt = 0;
}

static void setNonblocking(int fd, int nb)
{
    int flags;

    if (-1 == (flags = fcntl(fd, F_GETFL, 0)))
        flags = 0;
    if (nb)
    	flags |= O_NONBLOCK;
    else
    	flags &= ~O_NONBLOCK;
    fcntl(fd, F_SETFL, flags);
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
	// Setup standard I/O pipes
	pipe(outPipe);
	pipe(errPipe);
	pipe(inPipe);
	
	pid = fork();
	if (pid < 0) {
		fprintf(stderr, "Error: fork() failed\n");
		return;
	}
	if (pid != 0) {
		// Parent process - communicates with the JNI
		initBuffer(out, 1000);
		initBuffer(err, 1000);
		return;
	}
	// Child process
	// Replace standard I/O streams with pipes
	close(0);
	dup(inPipe[0]);
	close(1);
	dup(outPipe[1]);
	close(2);
	dup(errPipe[1]);
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
	fprintf(stderr, "Running: %s %s %s\n", argv[0], argv[1], argv[2]);
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
	signal(pid, 2);
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    getGdbErrorStream
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_gdb_jni_GdbProcess_getGdbErrorStream
(JNIEnv *env, jobject obj)
{
	return (jint)errPipe[0];
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    getGdbOutputStream
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_gdb_jni_GdbProcess_getGdbOutputStream
(JNIEnv *env, jobject obj)
{
	return (jint)outPipe[0];
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    getGdbInputStream
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_gdb_jni_GdbProcess_getGdbInputStream
(JNIEnv *env, jobject obj)
{
	return (jint)inPipe[1];
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    getProgramErrorStream
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_gdb_jni_GdbProcess_getProgramErrorStream
(JNIEnv *env, jobject obj)
{
	return (jint)errPipe[0];
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    getProgramOutputStream
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_gdb_jni_GdbProcess_getProgramOutputStream
(JNIEnv *env, jobject obj)
{
	return (jint)outPipe[0];
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    getProgramInputStream
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_gdb_jni_GdbProcess_getProgramInputStream
(JNIEnv *env, jobject obj)
{
	return (jint)inPipe[1];
}

static Buffer *getBuf(int stream)
{
	if (stream == outPipe[0])
		return out;
	return err;
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    isReady
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_gdb_jni_GdbProcess_isReady
(JNIEnv *env, jobject obj, jint stream)
{
	Buffer *buf = getBuf((int)stream);
	// first, read
	int toRead = buf->size - buf->cnt;
	if (toRead == 0) {
		// Enlarge buffer
		int newSize = buf->size * 2;
		char *newBuf = malloc(sizeof(char) * newSize);
		memcpy(newBuf, buf->b, buf->cnt);
		free(buf->b);
		buf->b = newBuf;
		buf->size = newSize;
		toRead = buf->size - buf->cnt;
	}
	setNonblocking((int)stream, 1);
	int ret = read((int)stream, buf->b + cnt, toRead);
	setNonblocking((int)stream, 0);
	if (ret > 0)
		buf->cnt += ret;
	return (buf->cnt > 0);
}

/*
 * Class:     gdb_jni_GdbProcess
 * Method:    readLine
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_gdb_jni_GdbProcess_readLine
(JNIEnv *env, jobject obj, jint stream)
{
	int i;
	for (i = 0; i < buf->cnt; i++)
		if (buf[i] == '\n') {
			break;
		}
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


