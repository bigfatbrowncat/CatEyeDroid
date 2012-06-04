#include <com/cateye/procedures/limiter/LimiterStageOperationProcessor.h>
#include <colorlib.h>
#include <bitmaps.h>
#include <jni.h>
#include <math.h>

#define DEBUG_INFO printf("%d\n", __LINE__);fflush(stdout);

JNIEXPORT void JNICALL Java_com_cateye_procedures_limiter_LimiterStageOperationProcessor_process
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

	// Getting the brightness
	jclass operationClass = env->GetObjectClass(params);
	jfieldID powerId;
	powerId = env->GetFieldID(operationClass, "power", "D");

	double power = env->GetDoubleField(params, powerId);

	int pixels_per_percent = bmp.width * bmp.height / 100 + 1;		// 1 added for zero exclusion

	double a = 1.0 / (power * power) + 4;

	for (int i = 0; i < bmp.width * bmp.height; i++)
	{
		double r = 2 * (bmp.r[i] - 1) / a;
		double g = 2 * (bmp.g[i] - 1) / a;
		double b = 2 * (bmp.b[i] - 1) / a;
		r = r - sqrt(r * r + 1 / (power * power * a)) + 1;
		g = g - sqrt(g * g + 1 / (power * power * a)) + 1;
		b = b - sqrt(b * b + 1 / (power * power * a)) + 1;

		bmp.r[i] = r;
		bmp.g[i] = g;
		bmp.b[i] = b;

		// Reporting progress
		/*if (i % pixels_per_percent == 0 && progress_reporter != NULL)
		{
			bool user_answer = (*progress_reporter)((float)i / pixels_per_percent);

			// Checking if the operation canceled
			if (!user_answer)
			{
				return BRIGHTNESS_OPERATION_RESULT_CANCELLED_BY_CALLBACK;
			}
		}*/
	}
}
