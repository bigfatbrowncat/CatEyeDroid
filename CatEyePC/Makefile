EXTERNAL = ../external
PLATFORM = windows
ARCH = i386

EMBEDDED_TARGET_NAME = CatEye

all: bitmaps raw jpeg8c core colorlib procedures ui avian_embed

clean:
	make -C ../colorlib clean
	make -C ../CatEyeB.Procedures clean
	make -C ../bitmaps clean
	make -C ../libraw clean
	make -C ../jpeg-8c clean
	make -C ../CatEyeB.Core clean
	rm -rf bin/embedded
	rm -rf bin/classes

colorlib:
	make -C ../colorlib
	
procedures: colorlib
	make -C ../CatEyeB.Procedures

bitmaps:
	make -C ../bitmaps

raw:
	make -C ../libraw

jpeg8c:
	make -C ../jpeg-8c
	
core: raw bitmaps procedures
	make -C ../CatEyeB.Core

ui: core
	make -C ../CatEyeB.Ui.Swt
	
avian_embed: ui
	# Here we implement the default Avian embedding procedure
	mkdir -p bin/embedded; \
	cd bin/embedded; \
	echo "Unpacking libavian.a"; \
	ar x ../../$(EXTERNAL)/lib/libavian.a; \
	cp ../../$(EXTERNAL)/jar/classpath.jar boot.jar; \
	echo "Moving dlls"; \
	cd ../classes/; \
	mv *.dll ../; \
	cd ../embedded/; \
	echo "Packing boot.jar"; \
	"$(JAVA_HOME)/bin/jar" uf boot.jar -C ../classes .; \
	echo "Converting boot.jar to object file"; \
	../../$(EXTERNAL)/bin/binaryToObject boot.jar boot.jar.o _binary_boot_jar_start _binary_boot_jar_end $(PLATFORM) $(ARCH); \
	g++ -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32" \
     -D_JNI_IMPLEMENTATION_ -c ../../src/embedded-main.cpp -o embedded-main.o; \
    dlltool -z $(EMBEDDED_TARGET_NAME).def *.o; \
    dlltool -d $(EMBEDDED_TARGET_NAME).def -e $(EMBEDDED_TARGET_NAME).exp; \
    g++ $(EMBEDDED_TARGET_NAME).exp *.o -L../../$(EXTERNAL)/lib -lmingwthrd -lm -lz -lws2_32 \
     -mwindows -mconsole -static-libgcc -static-libstdc++ -o $(EMBEDDED_TARGET_NAME).exe; \
    mv -f $(EMBEDDED_TARGET_NAME).exe ../
    #strip --strip-all $(EMBEDDED_TARGET_NAME).exe
	
.PHONY: all bitmaps raw jpeg8c core clean colorlib procedures ui avian_embed
.SILENT: