PROJ = CatEyeB.Ui

DEP_JARS = ../CatEyeB.Core/$(JARPATH)/Core.jar \
           ../CatEyeB.Procedures/$(JARPATH)/Procedures.jar

SOURCE = src

JARNAME = Ui.jar

all: classes jar 
clean: classes_clean jar_clean

include ../target_dalvik_android_arm/makefiles/java_classes.mk

.SILENT: