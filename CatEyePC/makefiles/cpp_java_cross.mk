################### Main part ###################

CC = gcc $(COMMON_CFLAGS) $(CFLAGS) $(CDEFINES)
CPP = g++ $(COMMON_CPPFLAGS) $(CPPFLAGS) $(CPPDEFINES)
COMMON_CPPFLAGS += $(COMMON_CFLAGS)
CPPFLAGS += $(CFLAGS)
CPPDEFINES += $(CDEFINES)

OSTYPE := $(shell uname)

JNI_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32"  

TARGET_BIN = $(TARGET)/bin
TARGET_OBJ = $(TARGET)/obj
TARGET_LIB = $(TARGET)/lib
TARGET_GEN = $(TARGET)/gen

EXTERNAL_JARS = $(shell find ../ExternalLibs/lib -name \*.jar -printf "%p;")

JAVA_FILES := $(shell cd $(SOURCE); find . -name \*.java)
CLASS_FILES = $(addprefix $(TARGET_BIN)/,$(addsuffix .class,$(basename $(JAVA_FILES))))
                   
JAVA_JNI_HEADERS = $(addprefix $(TARGET_GEN)/,$(addsuffix .h,$(subst .,/,$(JAVA_JNI_CLASSES))))
JAVA_JNI_JAVA = $(addprefix $(SOURCE)/,$(addsuffix .java,$(subst .,/,$(JAVA_JNI_CLASSES))))
JAVA_JNI_CPP = $(addprefix $(SOURCE)/,$(addsuffix .cpp,$(subst .,/,$(JAVA_JNI_CLASSES))))
JAVA_JNI_OBJ = $(addprefix $(TARGET_OBJ)/,$(addsuffix .o,$(subst .,/,$(JAVA_JNI_CLASSES))))

all: shared_lib classes $(JAVA_JNI_HEADERS)

clean:
	@echo "[$(PROJ)] Removing Java classes..."
	rm -f $(foreach NAME, $(basename $(CLASS_FILES)), $(shell echo $(NAME)'\$$'\*.class))
	rm -f $(CLASS_FILES) 
	
	@echo "[$(PROJ)] Removing JNI headers..."
	rm -f $(JAVA_JNI_HEADERS)
	
	@echo "[$(PROJ)] Removing JNI objects..."
	rm -f $(JAVA_JNI_OBJ)
	
	@echo "[$(PROJ)] Removing native library..."
	rm -f $(TARGET_BIN)/$(NATIVE_LIB)

	@echo "[$(PROJ)] Removing empty directories..."
	find $(TARGET) -depth -empty -type d -exec rmdir {} \;

shared_lib: $(TARGET_BIN)/$(NATIVE_LIB)

################### Folders ###################

ENSURE_BIN = if [ ! -d "$(TARGET_BIN)" ]; then mkdir -p "$(TARGET_BIN)"; fi
ENSURE_GEN = if [ ! -d "$(TARGET_GEN)" ]; then mkdir -p "$(TARGET_GEN)"; fi
ENSURE_OBJ = if [ ! -d "$(TARGET_OBJ)" ]; then mkdir -p "$(TARGET_OBJ)"; fi

################ Java classes #################

classes: $(CLASS_FILES)

$(TARGET_BIN)/%.class : $(SOURCE)/%.java
	@echo "[$(PROJ)] Compiling java class $@ ..."
	$(ENSURE_BIN)
	#@echo "Custom jars: $(CUSTOM_JARS)"
	"$(JAVA_HOME)/bin/javac" -sourcepath "$(SOURCE)" -classpath "$(EXTERNAL_JARS);$(CUSTOM_JARS);$(TARGET_BIN)" -d "$(TARGET_BIN)" $<

################# JNI Headers #################

$(JAVA_JNI_HEADERS) : $(TARGET_GEN)/%.h : $(TARGET_BIN)/%.class
	@echo "[$(PROJ)] Generating $@ ..."
	$(ENSURE_GEN)
	if [ ! -d "$(dir $@)" ]; then mkdir -p "$(dir $@)"; fi
	"$(JAVA_HOME)/bin/javah" -classpath "$(EXTERNAL_JARS);$(CUSTOM_JARS);$(TARGET_BIN)" -o $@ $(subst /,.,$(basename $(patsubst $(TARGET_GEN)/%, %, $@)))

################### Objects ###################

$(JAVA_JNI_OBJ) : $(TARGET_OBJ)/%.o : $(SOURCE)/%.cpp $(TARGET_GEN)/%.h 
	@echo "[$(PROJ)] Compiling $@ ..."
	if [ ! -d $(dir $@) ]; then mkdir -p $(dir $@); fi
	$(CPP) -c $< -o $@ -I"$(TARGET_GEN)" $(JNI_INCLUDES) $(CUSTOM_INCLUDES)

################### Targets ###################

$(TARGET_BIN)/$(NATIVE_LIB): $(JAVA_JNI_OBJ) 
	@echo "[$(PROJ)] Building native part: $@ ..."
	$(ENSURE_BIN)
	$(CPP) -shared $(JAVA_JNI_OBJ) $(LIBS) -o $@ -Wl,--add-stdcall-alias

############### Dependent libs ################

#deplibs: $(TARGET_BIN)/bitmaps.dll

#$(TARGET_BIN)/bitmaps.dll: ../bitmaps/Makefile
#	@echo [$(PROJ)] $@ needed. Making it ...
#	make --directory=../bitmaps	

.PHONY: all classes shared_lib clean #deplibs 
.SILENT: