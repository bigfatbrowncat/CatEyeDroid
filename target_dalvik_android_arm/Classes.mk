PROJ = CatEyeDroid

ANDROID_SDK_PATH = c:\android-sdk

EXTERNAL_JARS = $(ANDROID_SDK_PATH)/platforms/android-10/android.jar;$(ANDROID_SDK_PATH)/tools/support/annotations.jar
SOURCE = src
GEN = gen
TARGET = .

include $(TARGET)/makefiles/android_java_classes.mk