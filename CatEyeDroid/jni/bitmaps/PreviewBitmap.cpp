#include <bitmaps.h>
#include <string.h>
#include <new>

#ifndef NULL
#define NULL		0
#endif

int PreviewBitmap_Init(PreviewBitmap& bmp, int width, int height)
{
	try
	{
		bmp.width = width;
		bmp.height = height;

		bmp.r = new Int8[width * height];
		bmp.g = new Int8[width * height];
		bmp.b = new Int8[width * height];
	}
    catch (...) //catch (std:: bad_alloc) doesn't work here for some reason!!!
	{
		if (bmp.r != NULL) delete [] bmp.r;
		if (bmp.g != NULL) delete [] bmp.g;
		if (bmp.b != NULL) delete [] bmp.b;

		return BITMAP_RESULT_OUT_OF_MEMORY;
	}
	return BITMAP_RESULT_OK;
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
	}
    catch (...) //catch (std:: exception) doesn't work here for some reason!!!
	{
		return BITMAP_RESULT_INCORRECT_DATA;
	}
	return BITMAP_RESULT_OK;
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
