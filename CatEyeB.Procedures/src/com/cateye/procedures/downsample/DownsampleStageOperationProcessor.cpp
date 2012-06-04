#include <com/cateye/procedures/downsample/DownsampleStageOperationProcessor.h>
#include <colorlib.h>
#include <bitmaps.h>
#include <jni.h>
#include <math.h>
#include <mem.h>

#include <vector>

using namespace std;

#define DEBUG_INFO printf("%d\n", __LINE__);fflush(stdout);

JNIEXPORT void JNICALL Java_com_cateye_procedures_downsample_DownsampleStageOperationProcessor_process
  (JNIEnv * env, jobject obj, jobject params, jobject bitmap, jobject listener)
{
	// Getting the class
	jclass cls = env->GetObjectClass(bitmap);

	// Getting field ids
	jfieldID r_id, g_id, b_id, width_id, height_id;
	r_id = env->GetFieldID(cls, "r", "J");
	g_id = env->GetFieldID(cls, "g", "J");
	b_id = env->GetFieldID(cls, "b", "J");
	width_id = env->GetFieldID(cls, "width", "I");
	height_id = env->GetFieldID(cls, "height", "I");

	// Getting the bitmap from JVM
	PreciseBitmap bmp;
	bmp.r = (float*)env->GetLongField(bitmap, r_id);
	bmp.g = (float*)env->GetLongField(bitmap, g_id);
	bmp.b = (float*)env->GetLongField(bitmap, b_id);
	bmp.width = env->GetIntField(bitmap, width_id);
	bmp.height = env->GetIntField(bitmap, height_id);

	// Getting the stage operation parameters
	jclass operationClass = env->GetObjectClass(params);
	jfieldID rateId;

	rateId = env->GetFieldID(operationClass, "rate", "I");
	int rate = (int)env->GetIntField(params, rateId);

	PreciseBitmap newBmp;
	PreciseBitmap_Init(newBmp, bmp.width / rate, bmp.height / rate);

	for (int i = 0; i < bmp.width; i++)
	for (int j = 0; j < bmp.height; j++)
	{
		int inx = (j / rate) * newBmp.width + i / rate;
		if (i / rate < newBmp.width && j / rate < newBmp.height)
		{
			newBmp.r[inx] += 1.0 / rate / rate * bmp.r[j * bmp.width + i];
			newBmp.g[inx] += 1.0 / rate / rate * bmp.g[j * bmp.width + i];
			newBmp.b[inx] += 1.0 / rate / rate * bmp.b[j * bmp.width + i];
		}
	}

	// Setting the new bitmap data
	env->SetIntField(bitmap, width_id, (jint)(newBmp.width));
	env->SetIntField(bitmap, height_id, (jint)(newBmp.height));
	env->SetLongField(bitmap, r_id, (jlong)(newBmp.r));
	env->SetLongField(bitmap, g_id, (jlong)(newBmp.g));
	env->SetLongField(bitmap, b_id, (jlong)(newBmp.b));

	PreciseBitmap_Free(bmp);
}
