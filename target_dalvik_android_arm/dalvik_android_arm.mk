PROJ = CatEyeB.Ui.Android

ANDROID_SDK_PATH = c:\android-sdk

JARPATH = out/dalvik_android_arm/jar

MAKE_DALVIK = make -f dalvik_android_arm.mk

DEP_JARS = $(ANDROID_SDK_PATH)/platforms/android-10/android.jar \
           $(ANDROID_SDK_PATH)/tools/support/annotations.jar \
           ../CatEyeB.Core/$(JARPATH)/Core.jar \
           ../CatEyeB.Procedures/$(JARPATH)/Procedures.jar \
           ../CatEyeB.Ui/$(JARPATH)/Ui.jar

JARNAME = Ui.Android.jar

all: classes jar 
clean: classes_clean jar_clean

../CatEyeB.Core/$(JARPATH)/Core.jar:
	$(MAKE_DALVIK) -C ../CatEyeB.Core

../CatEyeB.Procedures/$(JARPATH)/Procedures.jar:
	$(MAKE_DALVIK) -C ../CatEyeB.Procedures

../CatEyeB.Ui/$(JARPATH)/Ui.jar:
	$(MAKE_DALVIK) -C ../CatEyeB.Ui

include ../target_dalvik_android_arm/makefiles/java_classes.mk

.SILENT: