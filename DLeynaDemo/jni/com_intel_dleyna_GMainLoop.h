/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_intel_dleyna_GMainLoop */

#ifndef _Included_com_intel_dleyna_GMainLoop
#define _Included_com_intel_dleyna_GMainLoop
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_intel_dleyna_GMainLoop
 * Method:    allocNative
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GMainLoop_allocNative
  (JNIEnv *, jobject);

/*
 * Class:     com_intel_dleyna_GMainLoop
 * Method:    freeNative
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GMainLoop_freeNative
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_intel_dleyna_GMainLoop
 * Method:    idleAddNative
 * Signature: (JLjava/lang/Runnable;)V
 */
JNIEXPORT void JNICALL Java_com_intel_dleyna_GMainLoop_idleAddNative
  (JNIEnv *, jobject, jlong, jobject);

#ifdef __cplusplus
}
#endif
#endif
