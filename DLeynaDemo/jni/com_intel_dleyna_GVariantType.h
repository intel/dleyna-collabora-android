/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_intel_dleyna_GVariantType */

#ifndef _Included_com_intel_dleyna_GVariantType
#define _Included_com_intel_dleyna_GVariantType
#ifdef __cplusplus
extern "C" {
#endif
#undef com_intel_dleyna_GVariantType_BOOLEAN
#define com_intel_dleyna_GVariantType_BOOLEAN 98L
#undef com_intel_dleyna_GVariantType_UINT32
#define com_intel_dleyna_GVariantType_UINT32 117L
#undef com_intel_dleyna_GVariantType_INT64
#define com_intel_dleyna_GVariantType_INT64 120L
#undef com_intel_dleyna_GVariantType_DOUBLE
#define com_intel_dleyna_GVariantType_DOUBLE 100L
#undef com_intel_dleyna_GVariantType_STRING
#define com_intel_dleyna_GVariantType_STRING 115L
#undef com_intel_dleyna_GVariantType_OBJECT_PATH
#define com_intel_dleyna_GVariantType_OBJECT_PATH 111L
#undef com_intel_dleyna_GVariantType_ARRAY
#define com_intel_dleyna_GVariantType_ARRAY 97L
#undef com_intel_dleyna_GVariantType_TUPLE
#define com_intel_dleyna_GVariantType_TUPLE 114L
/*
 * Class:     com_intel_dleyna_GVariantType
 * Method:    free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_intel_dleyna_GVariantType_free
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_intel_dleyna_GVariantType
 * Method:    newBasicNative
 * Signature: (C)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariantType_newBasicNative
  (JNIEnv *, jclass, jchar);

/*
 * Class:     com_intel_dleyna_GVariantType
 * Method:    newArrayNative
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariantType_newArrayNative
  (JNIEnv *, jclass, jlong);

#ifdef __cplusplus
}
#endif
#endif
