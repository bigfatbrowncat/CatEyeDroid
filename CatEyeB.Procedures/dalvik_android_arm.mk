PROJ = CatEyeB.Procedures

DEP_JARS = ../CatEyeB.Core/$(JARPATH)/Core.jar

JARNAME = Procedures.jar

all: classes jar 
clean: classes_clean jar_clean

include ../target_dalvik_android_arm/makefiles/java_classes.mk

.SILENT: