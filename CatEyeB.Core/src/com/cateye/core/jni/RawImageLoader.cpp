#include <math.h>
#include <stdio.h>

#include <bitmaps.h>
#include <libraw.h>
#include <jpeglib.h>
#include <jni.h>
#include <stdio.h>
#include <malloc.h>

#ifdef DROID
#include <android/log.h>
#endif

#define DEBUG_INFO
/*
#ifdef DROID
  #define DEBUG_INFO __android_log_print(ANDROID_LOG_INFO, "RawImageLoader", "At line %d", __LINE__);
#else
  #define DEBUG_INFO printf("RawImageLoader at line %d\n", __LINE__);
#endif
*/

#define RAWPROCESSOR_OPEN_BUFFER			1024 * 1024 * 64		// 64Mb

// Java class and field names
#define JCLASS_EXCEPTION_LIBRAWEXCEPTION								"com/cateye/core/jni/exceptions/LibRawException"
#define JCLASS_EXCEPTION_LIBRAWEXCEPTION_CREATE_SPECIFIC				"createSpecificException"
#define JCLASS_EXCEPTION_LIBRAWEXCEPTION_CREATE_SPECIFIC_SIGNATURE		"(I)Lcom/cateye/core/jni/exceptions/LibRawException;"

void throwLibRawExceptionInJava(JNIEnv * env, int libraw_code)
{
	jclass exception_cls;
	jmethodID exception_create_specific_mtd;
	jobject exception;

	if (libraw_code != LIBRAW_SUCCESS)
	{
    	DEBUG_INFO printf("libraw error %d occured\n", libraw_code);

    	DEBUG_INFO exception_cls = env->FindClass(JCLASS_EXCEPTION_LIBRAWEXCEPTION);
    	if (!env->ExceptionCheck())
    	{
    		DEBUG_INFO exception_create_specific_mtd = env->GetMethodID(exception_cls,
    		                                                            JCLASS_EXCEPTION_LIBRAWEXCEPTION_CREATE_SPECIFIC,
    		                                                            JCLASS_EXCEPTION_LIBRAWEXCEPTION_CREATE_SPECIFIC_SIGNATURE);
        	if (!env->ExceptionCheck())
        	{
        		DEBUG_INFO exception = env->CallStaticObjectMethod(exception_cls, exception_create_specific_mtd, libraw_code);
            	if (!env->ExceptionCheck())
            	{
            		DEBUG_INFO env->Throw((jthrowable)exception);
            	}
            	else
            	{
            		printf("Can't call the createSpecific method in LibRawException class\n");
                	DEBUG_INFO
            	}
        	}
        	else
        	{
        		printf("Can't reach the createSpecific method in LibRawException class\n");
            	DEBUG_INFO
        	}
    	}
    	else
    	{
    		printf("Can't reach LibRawException class\n");
        	DEBUG_INFO
    	}
    	DEBUG_INFO
	}
}

void decode_jpeg(PreviewBitmap& res, unsigned char* buffer, unsigned long size)
{
	struct jpeg_decompress_struct cinfo;
	struct jpeg_error_mgr jerr;
	char *image;

	/*Initialize, open the JPEG and query the parameters */
	cinfo.err = jpeg_std_error(&jerr);
	jpeg_create_decompress(&cinfo);

	jpeg_mem_src(&cinfo, buffer, size);

	jpeg_read_header(&cinfo, TRUE);
	jpeg_start_decompress(&cinfo);

	/* allocate data and read the image as RGBRGBRGBRGB */
	image = (char*)malloc(cinfo.output_width * cinfo.output_height * cinfo.num_components);
	for(int i = 0; i < cinfo.output_height; i++)
	{
		char *ptr = image + i * 3 * cinfo.output_width;
		jpeg_read_scanlines(&cinfo, &ptr, 1);
	}

	jpeg_finish_decompress(&cinfo);
	jpeg_destroy_decompress(&cinfo);

	// Decompositing
	PreviewBitmap_Init(res, cinfo.output_width, cinfo.output_height);

	res.width = cinfo.output_width;
	res.height = cinfo.output_height;
	for (int i = 0; i < cinfo.output_width * cinfo.output_height; i++)
	{
		res.r[i] = image[3 * i];
		res.g[i] = image[3 * i + 1];
		res.b[i] = image[3 * i + 2];
	}

	free(image);
}

void decode_plain(PreviewBitmap& res, int width, int height, unsigned char* buffer, unsigned long size)
{
	// Decompositing
	PreviewBitmap_Init(res, width, height);
	for (int i = 0; i < width * height; i++)
	{
		res.r[i] = buffer[3 * i];
		res.g[i] = buffer[3 * i + 1];
		res.b[i] = buffer[3 * i + 2];
	}
}

bool decode_precise(PreciseBitmap& res, libraw_processed_image_t *image)
{
    PreciseBitmap_Init(res, image->width, image->height);

    if (image->bits == 8)
    {
    	for (int i = 0; i < res.width * res.height; i++)
    	{
    		res.r[i] = ((float)image->data[3 * i]) / 255;
    		res.g[i] = ((float)image->data[3 * i + 1]) / 255;
    		res.b[i] = ((float)image->data[3 * i + 2]) / 255;
    	}
    	return true;
    }
    else if (image->bits == 16)
    {
    	unsigned short* pus = (unsigned short*)image->data;
    	for (int i = 0; i < res.width * res.height; i++)
    	{
    		res.r[i] = ((float)pus[3 * i]) / 65535;
    		res.g[i] = ((float)pus[3 * i + 1]) / 65535;
    		res.b[i] = ((float)pus[3 * i + 2]) / 65535;
    	}
    	return true;
    }
    else
    {
    	PreciseBitmap_Free(res);
    	return false;
    }

}


extern "C" JNIEXPORT jobject JNICALL Java_com_cateye_core_jni_RawImageLoader_loadImageDescriptionFromFile
  (JNIEnv * env, jobject, jstring filename)
{
	// Getting the classes
	jclass imageDescription_class = env->FindClass("Lcom/cateye/core/jni/RawImageDescription;");
	jclass previewBitmap_class = env->FindClass("Lcom/cateye/core/jni/PreviewBitmap;");
//	jclass date_class = env->FindClass("Ljava/util/Date;");

	// Getting the methods
	jmethodID previewBitmap_init = env->GetMethodID(previewBitmap_class, "<init>", "()V");
	jmethodID imageDescription_init = env->GetMethodID(imageDescription_class, "<init>", "()V");
//	jmethodID date_init = env->GetMethodID(date_class, "<init>", "(J)V");

	// Getting the field ids
	jfieldID thumbnail_id = env->GetFieldID(imageDescription_class, "thumbnail", "Lcom/cateye/core/IPreviewBitmap;"),
	         flip_id = env->GetFieldID(imageDescription_class, "flip", "I"),
	         isoSpeed_id = env->GetFieldID(imageDescription_class, "isoSpeed", "F"),
	         shutter_id = env->GetFieldID(imageDescription_class, "shutter", "F"),
	         aperture_id = env->GetFieldID(imageDescription_class, "aperture", "F"),
	         focalLength_id = env->GetFieldID(imageDescription_class, "focalLength", "F"),
	         timeStamp_id = env->GetFieldID(imageDescription_class, "timeStamp", "J"),
	         shotOrder_id = env->GetFieldID(imageDescription_class, "shotOrder", "I"),
	         description_id = env->GetFieldID(imageDescription_class, "description", "Ljava/lang/String;"),
	         artist_id = env->GetFieldID(imageDescription_class, "artist", "Ljava/lang/String;"),
	         cameraMaker_id = env->GetFieldID(imageDescription_class, "cameraMaker", "Ljava/lang/String;"),
	         cameraModel_id = env->GetFieldID(imageDescription_class, "cameraModel", "Ljava/lang/String;");

	jobject previewBitmap;
	jobject timestamp = NULL;
	jobject imageDescription;


    const char* fn;
    long time_con;
    int ret;

    LibRaw* RawProcessor = NULL;
    libraw_processed_image_t *image = NULL;

	DEBUG_INFO    fn = env->GetStringUTFChars(filename, NULL);
    if (fn == NULL) {
        printf("Error: NULL string!\n");
		return NULL;
    }

	DEBUG_INFO	RawProcessor = new LibRaw();

	DEBUG_INFO ret = RawProcessor->open_file(fn, RAWPROCESSOR_OPEN_BUFFER);
	if (ret != LIBRAW_SUCCESS)
	{
		goto end;
	}

	DEBUG_INFO if ((ret = RawProcessor->unpack_thumb()) != LIBRAW_SUCCESS)
	{
		goto end;
	}

	DEBUG_INFO imageDescription = env->NewObject(imageDescription_class, imageDescription_init);

	// Setting fields
	printf("[Native] flip = %d\n", RawProcessor->imgdata.sizes.flip);
	DEBUG_INFO env->SetIntField(imageDescription, flip_id, RawProcessor->imgdata.sizes.flip);	// 0 - no rotation; 3 - 180-deg rotation; 5 - 90-deg counterclockwise, 6 - 90-deg clockwise
	printf("[Native] iso speed = %f\n", RawProcessor->imgdata.other.iso_speed);
	env->SetFloatField(imageDescription, isoSpeed_id, RawProcessor->imgdata.other.iso_speed);
	printf("[Native] shutter = %f\n", RawProcessor->imgdata.other.shutter);
	env->SetFloatField(imageDescription, shutter_id, RawProcessor->imgdata.other.shutter);
	printf("[Native] aperture = %f\n", RawProcessor->imgdata.other.aperture);
	env->SetFloatField(imageDescription, aperture_id, RawProcessor->imgdata.other.aperture);
	printf("[Native] focal length = %f\n", RawProcessor->imgdata.other.focal_len);
	env->SetFloatField(imageDescription, focalLength_id, RawProcessor->imgdata.other.focal_len);
	printf("[Native] shot order = %d\n", RawProcessor->imgdata.other.shot_order);
	env->SetIntField(imageDescription, shotOrder_id, RawProcessor->imgdata.other.shot_order);

	DEBUG_INFO time_con = RawProcessor->imgdata.other.timestamp;
	printf("[Native] timestamp = %d\n", time_con);
	//timestamp = env->NewObject(date_class, date_init, time_con);
	//env->SetObjectField(imageDescription, timeStamp_id, timestamp);
	DEBUG_INFO env->SetLongField(imageDescription, timeStamp_id, time_con);


	printf("[Native] description = %s\n", RawProcessor->imgdata.other.desc);
	env->SetObjectField(imageDescription, description_id, env->NewStringUTF(RawProcessor->imgdata.other.desc));
	printf("[Native] artist = %s\n", RawProcessor->imgdata.other.artist);
	env->SetObjectField(imageDescription, artist_id, env->NewStringUTF(RawProcessor->imgdata.other.artist));
	printf("[Native] camera maker = %s\n", RawProcessor->imgdata.idata.make);
	env->SetObjectField(imageDescription, cameraMaker_id, env->NewStringUTF(RawProcessor->imgdata.idata.make));
	printf("[Native] camera model = %s\n", RawProcessor->imgdata.idata.model);
	env->SetObjectField(imageDescription, cameraModel_id, env->NewStringUTF(RawProcessor->imgdata.idata.model));

	DEBUG_INFO
	fflush(stdout);

	DEBUG_INFO previewBitmap = env->NewObject(previewBitmap_class, previewBitmap_init);

	// Getting bitmap field ids
	jfieldID r_id, g_id, b_id, width_id, height_id;
	r_id = env->GetFieldID(previewBitmap_class, "r", "J");
	g_id = env->GetFieldID(previewBitmap_class, "g", "J");
	b_id = env->GetFieldID(previewBitmap_class, "b", "J");
	width_id = env->GetFieldID(previewBitmap_class, "width", "I");
	height_id = env->GetFieldID(previewBitmap_class, "height", "I");

	PreviewBitmap thumb;

	// Extracting and saving the thumbnail picture

	DEBUG_INFO image = RawProcessor->dcraw_make_mem_thumb(&ret);
	if (image == 0)
	{
		goto end;
	}

	printf("[Native] Image type: %s\n", (image->type == LIBRAW_IMAGE_JPEG) ? "jpeg" : "bitmap");

	if (image->type == LIBRAW_IMAGE_JPEG)
	{
		decode_jpeg(thumb, image->data, image->data_size);
	}
	else
	{
		decode_plain(thumb, image->width, image->height, image->data, image->data_size);
	}
	DEBUG_INFO
	// Setting field values
	env->SetIntField(previewBitmap, width_id, thumb.width);
	env->SetIntField(previewBitmap, height_id, thumb.height);
	env->SetLongField(previewBitmap, r_id, (jlong)(thumb.r));
	env->SetLongField(previewBitmap, g_id, (jlong)(thumb.g));
	env->SetLongField(previewBitmap, b_id, (jlong)(thumb.b));

	env->SetObjectField(imageDescription, thumbnail_id, previewBitmap);

	DEBUG_INFO

	RawProcessor->recycle();   // just for show this call...
	                           // use it if you want to load something else


end:
	DEBUG_INFO if (image != NULL) LibRaw::dcraw_clear_mem(image);
	DEBUG_INFO if (RawProcessor != NULL) delete RawProcessor;
	DEBUG_INFO env->ReleaseStringUTFChars(filename, fn);
	DEBUG_INFO if (ret != LIBRAW_SUCCESS)
	{
		DEBUG_INFO throwLibRawExceptionInJava(env, ret);
	}
	DEBUG_INFO return imageDescription;
}

struct JNIObjectContext
{
	JNIObjectContext(JNIEnv * env, jobject obj): env(env), obj(obj) {}
	JNIEnv * env;
	jobject obj;
};

int my_raw_processing_callback(void *d, enum LibRaw_progress p, int iteration, int expected)
{
	JNIObjectContext* oc = (JNIObjectContext*)d;
	jclass cls = oc->env->GetObjectClass(oc->obj);
	jmethodID raiseProgress_id = oc->env->GetMethodID(cls, "raiseProgress", "(F)Z");

	double logp = log((double)p)/log(2.0);
	double logmax = log((double)LIBRAW_PROGRESS_STRETCH)/log(2.0);

	float progress = (float)(logp / logmax);

#ifdef DROID
	__android_log_print(ANDROID_LOG_INFO, "RawImageLoader_my_raw_processing_callback", "progress = %d, iteration = %d, expected = %d, progress = %.1f", p, iteration, expected, progress);
	__android_log_print(ANDROID_LOG_INFO, "RawImageLoader_my_raw_processing_callback", "progress = %.1f", progress * 100);
#else
	printf("RawImageLoader_my_raw_processing_callback: progress = %d, iteration = %d, expected = %d, progress = %.1f\n", p, iteration, expected, progress);
	fflush(stdout);
#endif

	if (oc->env->CallBooleanMethod(oc->obj, raiseProgress_id, progress))
	{
		return 0;	// Continue
	}
    else
    {
    	return 1;	// Cancel
    }

}

extern "C" JNIEXPORT jobject JNICALL Java_com_cateye_core_jni_RawImageLoader_loadPreciseBitmapFromFile
  (JNIEnv * env, jobject obj, jstring filename)
{
#ifdef DROID
	__android_log_write(ANDROID_LOG_INFO, "RawImageLoader_loadPreciseBitmapFromFile", "Entering loadPreciseBitmapFromFile");
#endif
	jclass cls = env->GetObjectClass(obj);
	DEBUG_INFO
	jfieldID divide_by_2_id = env->GetFieldID(cls, "divideBy2", "Z");
	DEBUG_INFO
	bool divide_by_2 = env->GetBooleanField(obj, divide_by_2_id);
	DEBUG_INFO

	// Creating Java PreciseBitmap object
#ifdef DROID
	__android_log_write(ANDROID_LOG_INFO, "RawImageLoader_loadPreciseBitmapFromFile", "Creating Java PreciseBitmap object");
#endif
	DEBUG_INFO
	jclass preciseBitmap_class = env->FindClass("com/cateye/core/jni/PreciseBitmap");
#ifdef DROID
	__android_log_print(ANDROID_LOG_INFO, "RawImageLoader_loadPreciseBitmapFromFile", "preciseBitmap_class = %d", preciseBitmap_class);
#endif
	DEBUG_INFO
	jmethodID preciseBitmap_init = env->GetMethodID(preciseBitmap_class, "<init>", "()V");
#ifdef DROID
	__android_log_print(ANDROID_LOG_INFO, "RawImageLoader_loadPreciseBitmapFromFile", "preciseBitmap_init = %d", preciseBitmap_init);
#endif

	DEBUG_INFO
	jobject preciseBitmap = env->NewObject(preciseBitmap_class, preciseBitmap_init);
	DEBUG_INFO

	JNIObjectContext* oc = new JNIObjectContext(env, obj);

    int ret;

    LibRaw* RawProcessor = NULL;
    libraw_processed_image_t *image = NULL;
    PreciseBitmap pbmp;

	DEBUG_INFO

#ifdef WIN32
	const wchar_t* fnw = (wchar_t*)env->GetStringChars(filename, NULL);
    if (fnw == NULL)
    {
        printf("Error: NULL string!\n");
		return NULL;
    }

    FILE* wfile = _wfopen(fnw, L"rb");

    env->ReleaseStringChars(filename, (jchar*)fnw);

    void* fb = NULL;

    if (wfile != NULL)
    {
    	// Checking the size
        fseek(wfile, 0L, SEEK_END);
        long size = ftell(wfile);

        fseek(wfile, 0L, SEEK_SET);

        // Reading the file
        fb = malloc(size);

        fread(fb, 1, size, wfile);

        fclose(wfile);

#else
   	DEBUG_INFO
    const char* fn = env->GetStringUTFChars(filename, NULL);
#endif

    	DEBUG_INFO

    	RawProcessor = new LibRaw();

    	DEBUG_INFO

    	RawProcessor->imgdata.params.gamm[0] = RawProcessor->imgdata.params.gamm[1] =
    										   RawProcessor->imgdata.params.no_auto_bright = 1;
    	DEBUG_INFO
    	RawProcessor->imgdata.params.output_bps = 16;
    	//RawProcessor->imgdata.params.highlight  = 9;
    	RawProcessor->imgdata.params.threshold  = (float)200;
    	DEBUG_INFO

    	if (divide_by_2)
    	{
    		RawProcessor->imgdata.params.half_size         = 1;
    		RawProcessor->imgdata.params.four_color_rgb    = 1;
    	}

    	RawProcessor->set_progress_handler(my_raw_processing_callback, oc);

#ifdef WIN32
    	DEBUG_INFO
    	ret = RawProcessor->open_buffer(fb, size);
#else
    	DEBUG_INFO
    	ret = RawProcessor->open_file(fn, RAWPROCESSOR_OPEN_BUFFER);
#endif

    	if (ret != LIBRAW_SUCCESS)
    	{
    		goto end;
    	}

    	DEBUG_INFO
    	ret = RawProcessor->unpack();
    	if (ret != LIBRAW_SUCCESS)
    	{
    		goto end;
    	}
    	DEBUG_INFO
    	ret = RawProcessor->dcraw_process();
    	if (ret != LIBRAW_SUCCESS)
    	{
    		goto end;
    	}
    	DEBUG_INFO

    	image = RawProcessor->dcraw_make_mem_image(&ret);

    	if (image == NULL)
        {
    		goto end;
        }

    	DEBUG_INFO
        decode_precise(pbmp, image);
    	DEBUG_INFO

    	// Getting bitmap field ids
    	jfieldID r_id, g_id, b_id, width_id, height_id;
    	r_id = env->GetFieldID(preciseBitmap_class, "r", "J");
    	g_id = env->GetFieldID(preciseBitmap_class, "g", "J");
    	b_id = env->GetFieldID(preciseBitmap_class, "b", "J");
    	width_id = env->GetFieldID(preciseBitmap_class, "width", "I");
    	height_id = env->GetFieldID(preciseBitmap_class, "height", "I");

    	DEBUG_INFO

    	// Setting field values
    	env->SetIntField(preciseBitmap, width_id, pbmp.width);
    	env->SetIntField(preciseBitmap, height_id, pbmp.height);
    	env->SetLongField(preciseBitmap, r_id, (jlong)(pbmp.r));
    	env->SetLongField(preciseBitmap, g_id, (jlong)(pbmp.g));
    	env->SetLongField(preciseBitmap, b_id, (jlong)(pbmp.b));

    	DEBUG_INFO

    	RawProcessor->recycle();   // just for show this call...
    	                           // use it if you want to load something else
    	DEBUG_INFO

#ifdef WIN32
    }
#endif

end:

#ifdef WIN32
	if (fb != NULL) free(fb);
#else
    env->ReleaseStringUTFChars(filename, fn);
#endif
	delete oc;
	DEBUG_INFO
	if (image != NULL) LibRaw::dcraw_clear_mem(image);
	DEBUG_INFO
	if (RawProcessor != NULL) delete RawProcessor;
	DEBUG_INFO
	if (ret != LIBRAW_SUCCESS)
	{
		DEBUG_INFO
		throwLibRawExceptionInJava(env, ret);
	}
	DEBUG_INFO
#ifdef DROID
	__android_log_write(ANDROID_LOG_INFO, "RawImageLoader_loadPreciseBitmapFromFile", "Leaving loadPreciseBitmapFromFile");
#endif
	return preciseBitmap;
}
