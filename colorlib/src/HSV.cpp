#include "colorlib.h"

#define max(a,b) ((a)>(b)?(a):(b))
#define min(a,b) ((a)<(b)?(a):(b))

HSV::operator RGB()
{
	double hmod = h;
	while (hmod < 0) hmod += 1;

	int Hi = (int)(hmod / (1.0 / 6)) % 6;
	double f = h / (1.0 / 6) - (int)(h / (1.0 / 6));
	double p = v * (1 - s);
	double q = v * (1 - f * s);
	double t = v * (1 - (1 - f) * s);

	RGB res;
	switch (Hi)
	{
	case 0: res.r = v; res.g = t; res.b = p; break;
	case 1: res.r = q; res.g = v; res.b = p; break;
	case 2: res.r = p; res.g = v; res.b = t; break;
	case 3: res.r = p; res.g = q; res.b = v; break;
	case 4: res.r = t; res.g = p; res.b = v; break;
	case 5: res.r = v; res.g = p; res.b = q; break;
	}

	return res;
}
