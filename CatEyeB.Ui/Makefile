PROJ = CatEyeB.Ui

DEP_JARS = ../CatEyeB.Core/out/jar/Core.jar \
           ../CatEyeB.Procedures/out/jar/Procedures.jar

JARNAME = Ui.jar

all: classes jar
clean: classes_clean jar_clean

../CatEyeB.Core/out/jar/Core.jar:
	$(MAKE) -C ../CatEyeB.Core

../CatEyeB.Procedures/out/jar/Procedures.jar:
	$(MAKE) -C ../CatEyeB.Procedures

include ../target_avian_win32_i386/makefiles/java_classes.mk

.SILENT: