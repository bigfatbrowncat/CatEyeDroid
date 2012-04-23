all: bitmaps raw jpeg8c core

clean:
	make -C ../bitmaps clean
	make -C ../libraw clean
	make -C ../jpeg-8c clean
	make -C ../CatEyeB.Core clean

bitmaps:
	make -C ../bitmaps

raw:
	make -C ../libraw

jpeg8c:
	make -C ../jpeg-8c
	
core: bitmaps
	make -C ../CatEyeB.Core

.PHONY: all bitmaps raw jpeg8c core clean 
.SILENT: