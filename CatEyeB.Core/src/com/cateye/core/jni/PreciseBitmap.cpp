//#include <com/cateye/core/jni/PreciseBitmap.h>
#include <jni.h>
#include <bitmaps.h>

#define DEBUG_INFO	//printf("%d\n", __LINE__);fflush(stdout);
//#define DEBUG_INFO __android_log_print(ANDROID_LOG_INFO, "CorePreciseBitmap", "At line %d", __LINE__);

#ifndef NULL
#define NULL					0
#endif

#define NATIVE_OUT_OF_MEMORY	"Out of memory during native image allocation"
#define INVALID_IMAGE_DATA		"Invalid image data"

extern "C" JNIEXPORT void JNICALL Java_com_cateye_core_jni_PreciseBitmap_alloc
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
	PreciseBitmap pbmp;
	int res = PreciseBitmap_Init(pbmp, width, height);

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

extern "C" JNIEXPORT void JNICALL Java_com_cateye_core_jni_PreciseBitmap_free
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
	PreciseBitmap pbmp;
	pbmp.r = (float*)env->GetLongField(obj, r_id);
	pbmp.g = (float*)env->GetLongField(obj, g_id);
	pbmp.b = (float*)env->GetLongField(obj, b_id);
	pbmp.width = env->GetIntField(obj, width_id);
	pbmp.height = env->GetIntField(obj, height_id);

	int res = PreciseBitmap_Free(pbmp);

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

extern "C" JNIEXPORT jobject JNICALL Java_com_cateye_core_jni_PreciseBitmap_clone
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
	PreciseBitmap src;
	src.r = (float*)env->GetLongField(obj, r_id);
	src.g = (float*)env->GetLongField(obj, g_id);
	src.b = (float*)env->GetLongField(obj, b_id);
	src.width = env->GetIntField(obj, width_id);
	src.height = env->GetIntField(obj, height_id);

	PreciseBitmap dest;

	int res = PreciseBitmap_Copy(src, dest);

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

extern "C" JNIEXPORT jboolean JNICALL Java_com_cateye_core_jni_PreciseBitmap_getPixelsBGRIntoIntBuffer
	(JNIEnv * env, jobject obj, jintArray buf, jint x, jint y, jint screenWidth, jint screenHeight, jfloat brightness, jfloat scale, jobject cb)
{
	// Getting the class
	jclass cls = env->GetObjectClass(obj);
	DEBUG_INFO
	jclass exception_cls;

	DEBUG_INFO

	// Getting field ids
	jfieldID r_id, g_id, b_id, width_id, height_id;
	r_id = env->GetFieldID(cls, "r", "J");
	g_id = env->GetFieldID(cls, "g", "J");
	b_id = env->GetFieldID(cls, "b", "J");
	width_id = env->GetFieldID(cls, "width", "I");
	height_id = env->GetFieldID(cls, "height", "I");

	DEBUG_INFO

	// Getting the bitmap from JVM
	PreciseBitmap pbmp;
	pbmp.r = (float*)env->GetLongField(obj, r_id);
	pbmp.g = (float*)env->GetLongField(obj, g_id);
	pbmp.b = (float*)env->GetLongField(obj, b_id);
	pbmp.width = env->GetIntField(obj, width_id);
	pbmp.height = env->GetIntField(obj, height_id);

	DEBUG_INFO

	// Getting the callback
	jmethodID report_mtd = NULL;
	if (cb != NULL)
	{
		jclass cb_cls = env->GetObjectClass(cb);
		if (cb_cls != NULL)
		{
			report_mtd = env->GetMethodID(cb_cls, "report", "()Z");
		}
	}

	if (buf == NULL)
	{
		buf = env->NewIntArray(screenWidth * screenHeight);
	}
	jint* pixels = env->GetIntArrayElements(buf, 0);

	DEBUG_INFO

	bool result = true;

    for (int j = 0; j < screenHeight; j++)
    {
		for (int i = 0; i < screenWidth; i++)
		{
			int srcx = (int)((double)i / scale + x);
			int srcy = (int)((double)j / scale + y);

			if (srcx < 0 || srcy < 0 || srcx >= pbmp.width || srcy >= pbmp.height)
			{
				pixels[j * screenWidth + i] = 0;
			}
			else
			{
				int r, g, b;

				r = pbmp.r[srcy * pbmp.width + srcx] * brightness,
				g = pbmp.g[srcy * pbmp.width + srcx] * brightness,
				b = pbmp.b[srcy * pbmp.width + srcx] * brightness;

				if (r > 255) r = 255;
				if (g > 255) g = 255;
				if (b > 255) b = 255;

				pixels[j * screenWidth + i] = (0xff << 24) + (r << 16) + (g << 8) + b;
			}
		}
    	if (report_mtd != NULL)
    	{
    		if (env->CallBooleanMethod(cb, report_mtd) != true)
    		{
    			result = false;
    			break;
    		}
    	}
    }
	env->ReleaseIntArrayElements(buf, pixels, 0);

	DEBUG_INFO
    return result;
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_cateye_core_jni_PreciseBitmap_getPixelsRGBIntoByteBuffer
	(JNIEnv * env, jobject obj, jbyteArray buf, jint bytesPerLine, jint x, jint y, jint screenWidth, jint screenHeight, jfloat brightness, jfloat scale, jboolean antialiasing, jobject callback)
{
	// Getting the class
	jclass cls = env->GetObjectClass(obj);
	DEBUG_INFO
	jclass exception_cls;

	DEBUG_INFO

	// Getting field ids
	jfieldID r_id, g_id, b_id, width_id, height_id;
	r_id = env->GetFieldID(cls, "r", "J");
	g_id = env->GetFieldID(cls, "g", "J");
	b_id = env->GetFieldID(cls, "b", "J");
	width_id = env->GetFieldID(cls, "width", "I");
	height_id = env->GetFieldID(cls, "height", "I");

	DEBUG_INFO

	// Getting the bitmap from JVM
	PreciseBitmap pbmp;
	pbmp.r = (float*)env->GetLongField(obj, r_id);
	pbmp.g = (float*)env->GetLongField(obj, g_id);
	pbmp.b = (float*)env->GetLongField(obj, b_id);
	pbmp.width = env->GetIntField(obj, width_id);
	pbmp.height = env->GetIntField(obj, height_id);

	DEBUG_INFO

	// Getting the callback
	jmethodID report_mtd = NULL;
	if (callback != NULL)
	{
		jclass cb_cls = env->GetObjectClass(callback);
		if (cb_cls != NULL)
		{
			report_mtd = env->GetMethodID(cb_cls, "report", "()Z");
		}
	}

	jbyte* pixels = env->GetByteArrayElements(buf, 0);

	DEBUG_INFO

	bool result = true;

	if (scale > 1 || !antialiasing)
	{
		// This method is good for zooming in and it's quick,
		// but it doesn't do antialiasing, so we don't use it for zooming out

		int delta = 0;
		for (int j = 0; j < screenHeight; j++)
		{
			for (int i = 0; i < screenWidth; i++)
			{
				int srcx = (int)((double)i / scale + x);
				int srcy = (int)((double)j / scale + y);

				if (srcx < 1 || srcy < 1 || srcx >= pbmp.width - 1 || srcy >= pbmp.height - 1)
				{
					pixels[delta + i * 3 + 0] = 0;
					pixels[delta + i * 3 + 1] = 0;
					pixels[delta + i * 3 + 2] = 0;
				}
				else
				{
					float r, g, b;

					r = pbmp.r[srcy * pbmp.width + srcx] * brightness;
					g = pbmp.g[srcy * pbmp.width + srcx] * brightness;
					b = pbmp.b[srcy * pbmp.width + srcx] * brightness;


					if (r > 255) r = 255;
					if (g > 255) g = 255;
					if (b > 255) b = 255;

					pixels[delta + i * 3 + 0] = (int)b;
					pixels[delta + i * 3 + 1] = (int)g;
					pixels[delta + i * 3 + 2] = (int)r;
				}
			}
			delta += bytesPerLine;

	    	if (report_mtd != NULL)
	    	{
	    		if (env->CallBooleanMethod(callback, report_mtd) != true)
	    		{
	    			result = false;
	    			break;
	    		}
	    	}
		}
	}
	else
	{
		// Good antialiasing with pixels averaging. Couldn't be used with scale > 1

		float* ptmp = new float[bytesPerLine * screenHeight * 3]();
		int* pts = new int[screenWidth * screenHeight]();

		// Drawing
		int invs = (int)(1.0 / scale);
		for (int j = 0; j < pbmp.height; j++)
		{
			for (int i = 0; i < pbmp.width; i++)
			{
				double srcx = ((double)i - x) * scale;
				double srcy = ((double)j - y) * scale;
				int isx = (int)srcx;
				int isy = (int)srcy;

				if (isx < 0 || isy < 0 || isx >= screenWidth || isy >= screenHeight)
				{
					// Black here
				}
				else
				{
					double kx = srcx - isx;
					double ky = srcy - isy;

					double pdr = brightness * pbmp.r[j * pbmp.width + i];
					double pdg = brightness * pbmp.g[j * pbmp.width + i];
					double pdb = brightness * pbmp.b[j * pbmp.width + i];

					ptmp[bytesPerLine * isy + isx * 3 + 0] += pdb;
					ptmp[bytesPerLine * isy + isx * 3 + 1] += pdg;
					ptmp[bytesPerLine * isy + isx * 3 + 2] += pdr;
					pts[screenWidth * isy + isx] ++;

					/*
					 * We can use "pts" array with floats
					 * and replace the code above with this, commented out.
					 * It would make our antialiasing method perfect.
					 * It's not needed at runtime cause it's too slow.
					 * Maybe someday when the computers become strong as
					 * titans and quick as photons we will uncomment it...
					 *
					double q = (1 - kx) * (1 - ky);
					ptmp[bytesPerLine * isy + isx * 3 + 0] += pdb * q;
					ptmp[bytesPerLine * isy + isx * 3 + 1] += pdg * q;
					ptmp[bytesPerLine * isy + isx * 3 + 2] += pdr * q;
					pts[screenWidth * isy + isx] += q;

					q = (1 - kx) * ky;
					ptmp[bytesPerLine * (isy + 1) + isx * 3 + 0] += pdb * q;
					ptmp[bytesPerLine * (isy + 1) + isx * 3 + 1] += pdg * q;
					ptmp[bytesPerLine * (isy + 1) + isx * 3 + 2] += pdr * q;
					pts[screenWidth * (isy + 1) + isx] += q;

					q = kx * (1 - ky);
					ptmp[bytesPerLine * isy + (isx + 1) * 3 + 0] += pdb * q;
					ptmp[bytesPerLine * isy + (isx + 1) * 3 + 1] += pdg * q;
					ptmp[bytesPerLine * isy + (isx + 1) * 3 + 2] += pdr * q;
					pts[screenWidth * isy + (isx + 1)] += q;

					q = kx * ky;
					ptmp[bytesPerLine * (isy + 1) + (isx + 1) * 3 + 0] += pdb * q;
					ptmp[bytesPerLine * (isy + 1) + (isx + 1) * 3 + 1] += pdg * q;
					ptmp[bytesPerLine * (isy + 1) + (isx + 1) * 3 + 2] += pdr * q;
					pts[screenWidth * (isy + 1) + (isx + 1)] += q;

					*/
				}
			}

	    	if (report_mtd != NULL)
	    	{
	    		if (env->CallBooleanMethod(callback, report_mtd) != true)
	    		{
	    			result = false;
	    			break;
	    		}
	    	}
		}

		if (result)
		{
			// Printing it all out
			for (int j = 0; j < screenHeight; j++)
			{
				for (int i = 0; i < screenWidth; i++)
				{
					if (pts[screenWidth * j + i] > 0)
					{
						float b = ptmp[bytesPerLine * j + i * 3 + 0] / pts[screenWidth * j + i];
						float g = ptmp[bytesPerLine * j + i * 3 + 1] / pts[screenWidth * j + i];
						float r = ptmp[bytesPerLine * j + i * 3 + 2] / pts[screenWidth * j + i];

						if (b > 255) b = 255;
						if (g > 255) g = 255;
						if (r > 255) r = 255;

						pixels[bytesPerLine * j + i * 3 + 0] = (int)b;
						pixels[bytesPerLine * j + i * 3 + 1] = (int)g;
						pixels[bytesPerLine * j + i * 3 + 2] = (int)r;
					}
					else
					{
						pixels[bytesPerLine * j + i * 3 + 0] = 0;
						pixels[bytesPerLine * j + i * 3 + 1] = 0;
						pixels[bytesPerLine * j + i * 3 + 2] = 0;
					}
				}
				if (report_mtd != NULL)
				{
					if (env->CallBooleanMethod(callback, report_mtd) != true)
					{
						result = false;
						break;
					}
				}
			}
		}

		delete [] ptmp;
		delete [] pts;
	}
	env->ReleaseByteArrayElements(buf, pixels, 0);

	return result;
}
