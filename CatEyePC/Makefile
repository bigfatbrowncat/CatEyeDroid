all: bitmaps raw jpeg8c

clean:
	make -C ../bitmaps clean
	make -C ../libraw clean
	make -C ../jpeg-8c clean

bitmaps:
	make -C ../bitmaps

raw:
	make -C ../libraw

jpeg8c:
	make -C ../jpeg-8c

.PHONY: all bitmaps raw jpeg8c clean 
.SILENT: