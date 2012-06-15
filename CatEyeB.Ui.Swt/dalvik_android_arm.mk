PROJ = CatEyeB.Ui.Swt

JARNAME = Ui.Swt.jar

all: classes jar 
clean: classes_clean jar_clean

include ../target_dalvik_android_arm/makefiles/java_classes.mk

.SILENT: