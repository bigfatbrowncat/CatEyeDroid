PROJ = CatEyeB.Ui

MAKE_AVIAN = make -f avian_win32_i386.mk

JARPATH = out/avian_win32_i386/jar

DEP_JARS = ../CatEyeB.Core/$(JARPATH)/Core.jar \
           ../CatEyeB.Procedures/$(JARPATH)/Procedures.jar

JARNAME = Ui.jar

all: classes jar
clean: classes_clean jar_clean

../CatEyeB.Core/$(JARPATH)/Core.jar:
	$(MAKE_AVIAN) -C ../CatEyeB.Core

../CatEyeB.Procedures/$(JARPATH)/Procedures.jar:
	$(MAKE_AVIAN) -C ../CatEyeB.Procedures

include ../target_avian_win32_i386/makefiles/java_classes.mk

.SILENT: