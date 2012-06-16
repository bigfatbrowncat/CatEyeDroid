PROJ = CatEye @ dalvik, android, arm

JARPATH = out/dalvik_android_arm/jar

MAKE_DALVIK = make -f dalvik_android_arm.mk

DEP_JARS = ../CatEyeB.Core/$(JARPATH)/Core.jar \
           ../CatEyeB.Procedures/$(JARPATH)/Procedures.jar \
           ../CatEyeB.Ui/$(JARPATH)/Ui.jar \
           $(JARPATH)/Ui.Android.jar
           
all: droid
	
clean:
	@echo "[TODO] Implement cleaning"

../CatEyeB.Core/$(JARPATH)/Core.jar:
	$(MAKE_DALVIK) -C ../CatEyeB.Core

../CatEyeB.Procedures/$(JARPATH)/Procedures.jar:
	$(MAKE_DALVIK) -C ../CatEyeB.Procedures

../CatEyeB.Ui/$(JARPATH)/Ui.jar:
	$(MAKE_DALVIK) -C ../CatEyeB.Ui

$(JARPATH)/Ui.Android.jar:
	$(MAKE_DALVIK) -C .

jars: $(DEP_JARS)

droid: jars extract_dep_jars

include ../target_dalvik_android_arm/makefiles/dep_copy.mk

.SILENT: