//#include <com/cateye/core/jni/PreviewBitmap.h>
#include <jni.h>
#include <bitmaps.h>

#define NULL					0
#define NATIVE_OUT_OF_MEMORY	"Out of memory during native image allocation"
#define INVALID_IMAGE_DATA		"Invalid image data"

JNIEXPORT void JNICALL Java_com_cateye_core_jni_PreviewBitmap_alloc
	(JNIEnv * env, jobject obj, jint width, jint height)
{
	// Getting the class
	jclass cls = env->GetObjectClass(obj);

	jclass exception_cls;

	// Getting field ids
	jfieldID r_id, g_id, b_id, width_id, height_id;
	r_id = env->GetFieldID(cls, "r", "J");
	g_id = env->GetFieldID(cls, "g", "J");
	b_id = env->GetFieldID(cls, "b", "J");
	width_id = env->GetFieldID(cls, "width", "I");
	height_id = env->GetFieldID(cls, "height", "I");

	// Creating the bitmap
	PreviewBitmap pbmp;
	int res = PreviewBitmap_Init(pbmp, width, height);

	switch (res)
	{
	case BITMAP_RESULT_OUT_OF_MEMORY:
		// Getting the com/cateye/core/NativeHeapAllocationException class
		exception_cls = env->FindClass("com/cateye/core/NativeHeapAllocationException");
		env->ThrowNew(exception_cls, NATIVE_OUT_OF_MEMORY);
		break;

	case BITMAP_RESULT_OK:
		// Setting field values
		env->SetIntField(obj, width_id, width);
		env->SetIntField(obj, height_id, height);
		env->SetLongField(obj, r_id, (jlong)(pbmp.r));
		env->SetLongField(obj, g_id, (jlong)(pbmp.g));
		env->SetLongField(obj, b_id, (jlong)(pbmp.b));
		break;
	}


}

JNIEXPORT void JNICALL Java_com_cateye_core_jni_PreviewBitmap_free
	(JNIEnv * env, jobject obj)
{
	// Getting the class
	jclass cls = env->GetObjectClass(obj);

	jclass exception_cls;

	// Getting field ids
	jfieldID r_id, g_id, b_id, width_id, height_id;
	r_id = env->GetFieldID(cls, "r", "J");
	g_id = env->GetFieldID(cls, "g", "J");
	b_id = env->GetFieldID(cls, "b", "J");
	width_id = env->GetFieldID(cls, "width", "I");
	height_id = env->GetFieldID(cls, "height", "I");

	// Getting the bitmap from JVM
	PreviewBitmap pbmp;
	pbmp.r = (Int8*)env->GetLongField(obj, r_id);
	pbmp.g = (Int8*)env->GetLongField(obj, g_id);
	pbmp.b = (Int8*)env->GetLongField(obj, b_id);
	pbmp.width = env->GetIntField(obj, width_id);
	pbmp.height = env->GetIntField(obj, height_id);

	int res = PreviewBitmap_Free(pbmp);

	switch (res)
	{
	case BITMAP_RESULT_INCORRECT_DATA:
		// Getting the com/cateye/core/InvalidDataException class
		exception_cls = env->FindClass("com/cateye/core/InvalidDataException");
		env->ThrowNew(exception_cls, INVALID_IMAGE_DATA);
		break;

	case BITMAP_RESULT_OK:
		// Setting field values
		env->SetIntField(obj, width_id, pbmp.width);
		env->SetIntField(obj, height_id, pbmp.height);
		env->SetLongField(obj, r_id, (jlong)(pbmp.r));
		env->SetLongField(obj, g_id, (jlong)(pbmp.g));
		env->SetLongField(obj, b_id, (jlong)(pbmp.b));
		break;
	}

}

JNIEXPORT jobject JNICALL Java_com_cateye_core_jni_PreviewBitmap_clone
	(JNIEnv * env, jobject obj)
{
	// Getting the class
	jclass cls = env->GetObjectClass(obj);

	jclass exception_cls;

	// Getting field ids
	jfieldID r_id, g_id, b_id, width_id, height_id;
	r_id = env->GetFieldID(cls, "r", "J");
	g_id = env->GetFieldID(cls, "g", "J");
	b_id = env->GetFieldID(cls, "b", "J");
	width_id = env->GetFieldID(cls, "width", "I");
	height_id = env->GetFieldID(cls, "height", "I");

	// Getting the bitmap from JVM
	PreviewBitmap src;
	src.r = (Int8*)env->GetLongField(obj, r_id);
	src.g = (Int8*)env->GetLongField(obj, g_id);
	src.b = (Int8*)env->GetLongField(obj, b_id);
	src.width = env->GetIntField(obj, width_id);
	src.height = env->GetIntField(obj, height_id);

	PreviewBitmap dest;

	int res = PreviewBitmap_Copy(src, dest);

	switch (res)
	{
	case BITMAP_RESULT_INCORRECT_DATA:
		// Getting the com/cateye/core/InvalidDataException class
		exception_cls = env->FindClass("com/cateye/core/InvalidDataException");
		env->ThrowNew(exception_cls, INVALID_IMAGE_DATA);
		return NULL;

	case BITMAP_RESULT_OUT_OF_MEMORY:
		// Getting the OutOfMemoryException class
		exception_cls = env->FindClass("com/cateye/core/NativeHeapAllocationException");
		env->ThrowNew(exception_cls, NATIVE_OUT_OF_MEMORY);
		return NULL;

	case BITMAP_RESULT_OK:
		jobject ret_obj = env->AllocObject(cls);

		// Setting field values
		env->SetIntField(ret_obj, width_id, dest.width);
		env->SetIntField(ret_obj, height_id, dest.height);
		env->SetLongField(ret_obj, r_id, (jlong)(dest.r));
		env->SetLongField(ret_obj, g_id, (jlong)(dest.g));
		env->SetLongField(ret_obj, b_id, (jlong)(dest.b));

		return ret_obj;
	}
}
