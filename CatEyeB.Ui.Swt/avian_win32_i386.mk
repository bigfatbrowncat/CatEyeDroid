PROJ = CatEyeB.Ui.Swt

MAKE_AVIAN = make -f avian_win32_i386.mk

JARPATH = out/avian_win32_i386/jar

DEP_JARS = ../external/jar/classpath.jar \
           ../external/jar/swt.jar \
           ../CatEyeB.Core/$(JARPATH)/Core.jar \
           ../CatEyeB.Procedures/$(JARPATH)/Procedures.jar \
           ../CatEyeB.Ui/$(JARPATH)/Ui.jar

JARNAME = Ui.Swt.jar

all: classes jar
clean: classes_clean jar_clean

../CatEyeB.Core/$(JARPATH)/Core.jar:
	$(MAKE_AVIAN) -C ../CatEyeB.Core

../CatEyeB.Procedures/$(JARPATH)/Procedures.jar:
	$(MAKE_AVIAN) -C ../CatEyeB.Procedures

../CatEyeB.Ui/$(JARPATH)/Ui.jar:
	$(MAKE_AVIAN) -C ../CatEyeB.Ui

include ../target_avian_win32_i386/makefiles/java_classes.mk

.SILENT: