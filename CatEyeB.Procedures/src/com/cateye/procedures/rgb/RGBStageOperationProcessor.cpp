//TODO Make this include work under Android
//#include <com/cateye/procedures/rgb/RGBStageOperationProcessor.h>
#include <colorlib.h>
#include <bitmaps.h>
#include <jni.h>
#include <math.h>

#define DEBUG_INFO printf("%d\n", __LINE__);fflush(stdout);

JNIEXPORT void JNICALL Java_com_cateye_procedures_rgb_RGBStageOperationProcessor_process
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
	jfieldID rId, gId, bId;

	rId = env->GetFieldID(operationClass, "r", "D");
	gId = env->GetFieldID(operationClass, "g", "D");
	bId = env->GetFieldID(operationClass, "b", "D");

	double r = env->GetDoubleField(params, rId);
	double g = env->GetDoubleField(params, gId);
	double b = env->GetDoubleField(params, bId);


	int pixels_per_percent = bmp.width * bmp.height / 100 + 1;		// 1 added for zero exclusion
	double light0 = sqrt(r*r + g*g + b*b);

	for (int i = 0; i < bmp.width * bmp.height; i++)
	{
		double light = sqrt(bmp.r[i] * bmp.r[i] + bmp.g[i] * bmp.g[i] + bmp.b[i] * bmp.b[i]);

		bmp.r[i] = bmp.r[i] * r;// / light0 * light;
		bmp.g[i] = bmp.g[i] * g;// / light0 * light;
		bmp.b[i] = bmp.b[i] * b;// / light0 * light;


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
