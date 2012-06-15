# This make include file provides jni library and headers build procedure
# Parameters:
#    DEP_CLASSPATH - dependency classpath (no semicolon in the end!)
#    LIBS - external native libraries list
#    JNI_NATIVE_LIB - target jni library name
#    JNI_CPPFLAGS - c++ compiler flags for native library compilation
#    JNI_SOURCE - source folder for jni c++ files
#    JNI_CLASSES - java classes which have jni additions
#    INCLUDES - c++ include folders list
# Targets:
#    jni_headers - jni header files
#    jni_lib - jni native library

CPP = g++ $(JNI_CPPFLAGS)

OSTYPE := $(shell uname)

JNI_INCLUDE_FLAGS = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32"  
JNI_SOURCE = src
CLASSPATH = out/avian_win32_i386/classes
TARGET_OBJ = out/avian_win32_i386/obj
TARGET_LIB = out/avian_win32_i386/bin
TARGET_INCLUDE_JNI = out/avian_win32_i386/gen

CLASS_FILES = $(addprefix $(CLASSPATH)/,$(addsuffix .class,$(basename $(JAVA_FILES))))

JNI_CLASSES := $(shell cd $(JNI_SOURCE); find . -name \*.cpp | awk '{ sub(/.\//,""); gsub(/\//,"."); sub(/.cpp$$/, "") }; 1')
JAVA_JNI_HEADERS = $(addprefix $(TARGET_INCLUDE_JNI)/,$(addsuffix .h,$(subst .,/,$(JNI_CLASSES))))

JNI_SOURCES := $(shell cd $(JNI_SOURCE); find . -name \*.cpp | awk '{ sub(/.\//,"") }; 1')
JNI_OBJECTS = $(addprefix $(TARGET_OBJ)/,$(addsuffix .o,$(basename $(JNI_SOURCES))))

INCLUDE_FLAGS = $(addprefix -I,$(INCLUDES))
EXTLIBS_FLAGS = $(addprefix -l,$(EXTLIBS))

jni_headers: $(JAVA_JNI_HEADERS)

jni_clean:
	@echo "[$(PROJ)] Removing JNI headers..."
	rm -f $(JAVA_JNI_HEADERS)
	
	@echo "[$(PROJ)] Removing JNI objects..."
	rm -f $(JAVA_JNI_OBJ)
	
	@echo "[$(PROJ)] Removing native library..."
	rm -f $(TARGET_LIB)/$(JNI_NATIVE_LIB)

	@echo "[$(PROJ)] Removing empty directories..."
	find . -depth -empty -type d -exec rmdir {} \;

jni_lib: $(TARGET_LIB)/$(JNI_NATIVE_LIB)

################### Folders ###################

ENSURE_LIB = if [ ! -d "$(TARGET_LIB)" ]; then mkdir -p "$(TARGET_LIB)"; fi
ENSURE_CLASSES = if [ ! -d "$(CLASSPATH)" ]; then mkdir -p "$(CLASSPATH)"; fi
ENSURE_INCLUDE_JNI = if [ ! -d "$(TARGET_INCLUDE_JNI)" ]; then mkdir -p "$(TARGET_INCLUDE_JNI)"; fi
ENSURE_OBJ = if [ ! -d "$(TARGET_OBJ)" ]; then mkdir -p "$(TARGET_OBJ)"; fi

################# JNI Headers #################

$(JAVA_JNI_HEADERS) : $(TARGET_INCLUDE_JNI)/%.h : $(CLASSPATH)/%.class
	@echo "[$(PROJ)] Generating $@ ..."
	$(ENSURE_INCLUDE_JNI)
	if [ ! -d "$(dir $@)" ]; then mkdir -p "$(dir $@)"; fi
	"$(JAVA_HOME)/bin/javah" -classpath "$(DEP_CLASSPATH);$(CLASSPATH)" -o $@ $(subst /,.,$(basename $(patsubst $(TARGET_INCLUDE_JNI)/%, %, $@)))

################### Objects ###################

$(JNI_OBJECTS) : $(TARGET_OBJ)/%.o : $(JNI_SOURCE)/%.cpp $(TARGET_INCLUDE_JNI)/%.h 
	@echo "[$(PROJ)] Compiling $@ ..."
	$(ENSURE_OBJ)
	if [ ! -d $(dir $@) ]; then mkdir -p $(dir $@); fi
	$(CPP) -c $< -o $@ -I"$(TARGET_INCLUDE_JNI)" $(JNI_INCLUDE_FLAGS) $(INCLUDE_FLAGS)

################### Targets ###################

$(TARGET_LIB)/$(JNI_NATIVE_LIB): $(JNI_OBJECTS) $(LIBS)
	@echo "[$(PROJ)] Building native part: $@ ..."
	$(ENSURE_LIB)
	$(CPP) -o $@ -shared $(JNI_OBJECTS) $(LIBS) $(EXTLIBS_FLAGS) -Wl,--add-stdcall-alias
	#$(CPP) -shared -Wl,--add-stdcall-alias $(JNI_OBJECTS) $(LIBS) $(EXTLIBS_FLAGS) -o $@

.PHONY: jni_headers jni_clean jni_lib
#.SILENT: