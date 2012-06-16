PROJ = CatEyeB.Core

JARNAME = Core.jar

all: classes jar 
clean: classes_clean jar_clean

include ../target_dalvik_android_arm/makefiles/java_classes.mk

.SILENT: