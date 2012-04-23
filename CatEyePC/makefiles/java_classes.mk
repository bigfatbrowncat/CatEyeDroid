################### Main part ###################

TARGET_BIN = $(TARGET)/bin

EXTERNAL_JARS = $(shell find ../ExternalLibs/lib -name \*.jar -printf "%p;")

JAVA_FILES := $(shell cd $(SOURCE); find . -name \*.java)
CLASS_FILES = $(addprefix $(TARGET_BIN)/,$(addsuffix .class,$(basename $(JAVA_FILES))))
                   
all: classes

clean:
	@echo "[$(PROJ)] Removing Java classes..."
	rm -f $(foreach NAME, $(basename $(CLASS_FILES)), $(shell echo $(NAME)'\$$'\*.class))
	rm -f $(CLASS_FILES) 
	
	@echo "[$(PROJ)] Removing empty directories..."
	find $(TARGET) -depth -empty -type d -exec rmdir {} \;

################### Folders ###################

ENSURE_BIN = if [ ! -d "$(TARGET_BIN)" ]; then mkdir -p "$(TARGET_BIN)"; fi

################ Java classes #################

classes: $(CLASS_FILES)

$(TARGET_BIN)/%.class : $(SOURCE)/%.java
	@echo "[$(PROJ)] Compiling java class $@ ..."
	$(ENSURE_BIN)
	#@echo "Custom jars: $(CUSTOM_JARS)"
	"$(JAVA_HOME)/bin/javac" -g -sourcepath "$(SOURCE)" -classpath "$(EXTERNAL_JARS);$(CUSTOM_JARS);$(TARGET_BIN)" -d "$(TARGET_BIN)" $<

.PHONY: all classes clean #deplibs 
.SILENT: