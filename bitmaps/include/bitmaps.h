#ifndef BITMAPS_H_
#define BITMAPS_H_

#ifdef WIN32
	#ifdef BUILDING_LIBBITMAP
		#define LIBBITMAP __declspec(dllexport)
	#else
		#define LIBBITMAP __declspec(dllimport)
	#endif
#else
	#define LIBBITMAP
#endif

typedef unsigned char Int8;

struct PreciseBitmap
{
	int width;
	int height;
	float* r;
	float* g;
	float* b;
};

struct PreviewBitmap
{
	int width;
	int height;
	Int8* r;
	Int8* g;
	Int8* b;
};

#define BITMAP_RESULT_OK					0
#define BITMAP_RESULT_OUT_OF_MEMORY			1
#define BITMAP_RESULT_INCORRECT_DATA		2

extern "C"
{
	// PreciseBitmap management
	LIBBITMAP int PreciseBitmap_Init(PreciseBitmap& bmp, int width, int height);
	LIBBITMAP int PreciseBitmap_Copy(PreciseBitmap& src, PreciseBitmap& res);
	LIBBITMAP int PreciseBitmap_Free(PreciseBitmap& fb);

	// PreviewBitmap management
	LIBBITMAP int PreviewBitmap_Init(PreviewBitmap& bmp, int width, int height);
	LIBBITMAP int PreviewBitmap_Copy(PreviewBitmap& src, PreviewBitmap& res);
	LIBBITMAP int PreviewBitmap_Free(PreviewBitmap& fb);

	// Converters
	LIBBITMAP int PreviewBitmap_FromPreciseBitmap(PreciseBitmap* src, PreviewBitmap* res);
}

#endif /* BITMAPS_H_ */
