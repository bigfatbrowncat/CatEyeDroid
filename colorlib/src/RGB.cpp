#include "colorlib.h"

#include <math.h>

#define max(a,b) ((a)>(b)?(a):(b))
#define min(a,b) ((a)<(b)?(a):(b))

RGB::operator HSV()
{
	HSV res;
	double rgb_max = max(r, max(g, b));
	double rgb_min = min(r, min(g, b));

	// Light
	res.v = rgb_max;

	if (rgb_max - rgb_min < 0.0000001)
	{
		res.h = 0;	// Here it's undefined
		res.s = 0;	// Here it's undefined
	}
	else
	{
		// Hue
		if (r > g && r > b)
		{
			res.h = 1.0/6 * (g - b) / (rgb_max - rgb_min);
			if (b > g)
				res.h += 1;
		}
		else if (g > b && g > r)
		{
			res.h = 1.0/6 * (b - r) / (rgb_max - rgb_min) + 2.0/6;
		}
		else if (b > g && b > r)
		{
			res.h = 1.0/6 * (r - g) / (rgb_max - rgb_min) + 4.0/6;
		}
		else
		{
			res.h = 0;	// Here it's undefined
		}

		// Saturation
		if (res.v == 0)
		{
			res.s = 0; // Here it's undefined
		}
		else
		{
			res.s = 1 - rgb_min / rgb_max;
		}

	}

	return res;
}
