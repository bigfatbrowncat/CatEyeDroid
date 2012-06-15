PROJ = CatEyeB.Ui.Swt

DEP_JARS = ../external/jar/classpath.jar \
           ../external/jar/swt.jar \
           ../CatEyeB.Core/out/jar/Core.jar \
           ../CatEyeB.Procedures/out/jar/Procedures.jar \
           ../CatEyeB.Ui/out/jar/Ui.jar

JARNAME = Ui.Swt.jar

all: classes jar
clean: classes_clean jar_clean

../CatEyeB.Core/out/jar/Core.jar:
	$(MAKE) -C ../CatEyeB.Core

../CatEyeB.Procedures/out/jar/Procedures.jar:
	$(MAKE) -C ../CatEyeB.Procedures

../CatEyeB.Ui/out/jar/Ui.jar:
	$(MAKE) -C ../CatEyeB.Ui

include ../target_avian_win32_i386/makefiles/java_classes.mk

.SILENT: