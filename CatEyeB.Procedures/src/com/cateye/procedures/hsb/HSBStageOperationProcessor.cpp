#include <com/cateye/procedures/hsb/HSBStageOperationProcessor.h>
#include <colorlib.h>
#include <bitmaps.h>
#include <jni.h>
#include <math.h>

#define DEBUG_INFO printf("%d\n", __LINE__);fflush(stdout);

JNIEXPORT void JNICALL Java_com_cateye_procedures_hsb_HSBStageOperationProcessor_process
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
	jfieldID brightnessId, hueId, saturationId, saturationCompressionPowerId;

	brightnessId = env->GetFieldID(operationClass, "brightness", "D");
	hueId = env->GetFieldID(operationClass, "hue", "D");
	saturationId = env->GetFieldID(operationClass, "saturation", "D");
	saturationCompressionPowerId = env->GetFieldID(operationClass, "saturationCompressionPower", "D");

	double brightness = env->GetDoubleField(params, brightnessId);
	double hue = env->GetDoubleField(params, hueId);
	double saturation = env->GetDoubleField(params, saturationId);
	double saturationCompressionPower = env->GetDoubleField(params, saturationCompressionPowerId);

	double a = 1.0 / (saturationCompressionPower * saturationCompressionPower) + 4;

	int pixels_per_percent = bmp.width * bmp.height / 100 + 1;		// 1 added for zero exclusion

	for (int i = 0; i < bmp.width * bmp.height; i++)
	{
		RGB rgb(bmp.r[i], bmp.g[i], bmp.b[i]);
		HSV hsv(rgb);

		// Multiplying Value
		hsv.v *= brightness;

		// Adding Hue
		hsv.h += hue;

		// Multiplying Saturation
		hsv.s = hsv.s * saturation;
		// Compressing Saturation
		double s = 2 * (hsv.s - 1) / a;
		hsv.s = s - sqrt(s * s + 1 / (saturationCompressionPower * saturationCompressionPower * a)) + 1;

		rgb = (RGB)hsv;
		bmp.r[i] = rgb.r;
		bmp.g[i] = rgb.g;
		bmp.b[i] = rgb.b;


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
