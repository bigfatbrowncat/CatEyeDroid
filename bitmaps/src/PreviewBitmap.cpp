#include "bitmaps.h"

#include <string.h>
#include <new>

int PreviewBitmap_Init(PreviewBitmap& bmp, int width, int height)
{
	try
	{
		bmp.width = width;
		bmp.height = height;

		bmp.r = new Int8[width * height];
		bmp.g = new Int8[width * height];
		bmp.b = new Int8[width * height];

		return BITMAP_RESULT_OK;
	}
	catch (std::bad_alloc& ex)
	{
		if (bmp.r != NULL) delete [] bmp.r;
		if (bmp.g != NULL) delete [] bmp.g;
		if (bmp.b != NULL) delete [] bmp.b;

		return BITMAP_RESULT_OUT_OF_MEMORY;
	}
}

int PreviewBitmap_Copy(PreviewBitmap& src, PreviewBitmap& res)
{
	int init_res = PreviewBitmap_Init(res, src.width, src.height);
	if (init_res != BITMAP_RESULT_OK)
		return init_res;
	else
	{
		memcpy(res.r, src.r, src.width * src.height * sizeof(Int8));
		memcpy(res.g, src.g, src.width * src.height * sizeof(Int8));
		memcpy(res.b, src.b, src.width * src.height * sizeof(Int8));
		return BITMAP_RESULT_OK;
	}

}

int PreviewBitmap_Free(PreviewBitmap& fb)
{
	try
	{
		delete [] fb.r;
		delete [] fb.g;
		delete [] fb.b;
		return BITMAP_RESULT_OK;
	}
	catch (std::exception&)
	{
		return BITMAP_RESULT_INCORRECT_DATA;
	}
}

int PreviewBitmap_FromPreciseBitmap(PreciseBitmap& src, PreviewBitmap& res)
{
	int init_res = PreviewBitmap_Init(res, src.width, src.height);
	if (init_res != BITMAP_RESULT_OK)
		return init_res;
	else
	{
		for (int k = 0; k < src.width * src.height; k++)
		{
			res.r[k] = (Int8)(src.r[k] * 255);
			res.g[k] = (Int8)(src.g[k] * 255);
			res.b[k] = (Int8)(src.b[k] * 255);
		}
		return BITMAP_RESULT_OK;
	}
}
