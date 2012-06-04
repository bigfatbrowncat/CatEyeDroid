#ifndef COLOR_LIB_H
#define COLOR_LIB_H

struct HSV;
struct RGB;

struct RGB
{
	RGB() {}
	RGB(double r, double g, double b) : r(r), g(g), b(b) {}
	double r, g, b;
	operator HSV();
};

struct HSV
{
	HSV() {}
	HSV(double h, double s, double v) : h(h), s(s), v(v) {}
	double h, s, v;
	operator RGB();
};

#endif
