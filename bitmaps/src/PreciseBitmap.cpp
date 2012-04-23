#include "bitmaps.h"

#include <string.h>
#include <new>

int PreciseBitmap_Init(PreciseBitmap& bmp, int width, int height)
{
	try
	{
		bmp.width = width;
		bmp.height = height;

		bmp.r = 0; bmp.g = 0; bmp.b = 0;

		bmp.r = new float[width * height];
		bmp.g = new float[width * height];
		bmp.b = new float[width * height];

		return BITMAP_RESULT_OK;
	}
	catch (std::bad_alloc&)
	{
		if (bmp.r != NULL) delete [] bmp.r;
		if (bmp.g != NULL) delete [] bmp.g;
		if (bmp.b != NULL) delete [] bmp.b;

		return BITMAP_RESULT_OUT_OF_MEMORY;
	}

}

int PreciseBitmap_Copy(PreciseBitmap& src, PreciseBitmap& res)
{
	if (src.r == NULL || src.g == NULL || src.g == NULL)
	{
		return BITMAP_RESULT_INCORRECT_DATA;
	}

	int init_res = PreciseBitmap_Init(res, src.width, src.height);
	if (init_res != BITMAP_RESULT_OK)
		return init_res;
	else
	{
		memcpy(res.r, src.r, src.width * src.height * sizeof(float));
		memcpy(res.g, src.g, src.width * src.height * sizeof(float));
		memcpy(res.b, src.b, src.width * src.height * sizeof(float));
		return BITMAP_RESULT_OK;
	}
}

int PreciseBitmap_Free(PreciseBitmap& fb)
{
	if (fb.r == NULL || fb.g == NULL || fb.g == NULL)
	{
		return BITMAP_RESULT_INCORRECT_DATA;
	}
	else
	{
		delete [] fb.r;
		fb.r = NULL;
		delete [] fb.g;
		fb.g = NULL;
		delete [] fb.b;
		fb.b = NULL;
		return BITMAP_RESULT_OK;
	}
}
