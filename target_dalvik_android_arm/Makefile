PROJ = CatEye @ dalvik, android, arm

JARPATH = out/dalvik_android_arm/jar

MAKE_DALVIK = make -f dalvik_android_arm.mk

DEP_JARS = $(JARPATH)/Ui.Android.jar

all: droid
	
clean:
	@echo "[TODO] Implement cleaning"

$(JARPATH)/Ui.Android.jar:
	$(MAKE_DALVIK) -C .

jars: $(DEP_JARS)

droid: jars extract_dep_jars

include ../target_dalvik_android_arm/makefiles/dep_copy.mk

.SILENT: