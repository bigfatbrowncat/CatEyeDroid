################### Main part ###################

TARGET_BIN = $(TARGET)/bin

EXTERNAL_JARS = #$(shell find ../ExternalLibs/lib -name \*.jar -printf "%p;")

JAVA_FILES := $(shell cd $(SOURCE); find . -name \*.java)
CLASS_FILES = $(addprefix $(TARGET_BIN)/classes/,$(addsuffix .class,$(basename $(JAVA_FILES))))
                   
clean:
	@echo "[$(PROJ)] Removing Java classes..."
	rm -f $(foreach NAME, $(basename $(CLASS_FILES)), $(shell echo $(NAME)'\$$'\*.class))
	rm -f $(CLASS_FILES) 
	
	@echo "[$(PROJ)] Removing empty directories..."
	find $(TARGET) -depth -empty -type d -exec rmdir {} \;

################### Folders ###################

ENSURE_CLASSES = if [ ! -d "$(TARGET_BIN)/classes" ]; then mkdir -p "$(TARGET_BIN)/classes"; fi

################ Java classes #################

classes: $(CLASS_FILES)

$(TARGET_BIN)/classes/%.class : $(SOURCE)/%.java
	@echo "[$(PROJ)] Compiling java class $@ ..."
	$(ENSURE_CLASSES)
	#@echo "Custom jars: $(CUSTOM_JARS)"
	"$(JAVA_HOME)/bin/javac" -g -sourcepath "$(SOURCE)" -classpath "$(EXTERNAL_JARS);$(CUSTOM_JARS);$(TARGET_BIN)/classes" -d "$(TARGET_BIN)/classes" $<

.PHONY: all classes clean #deplibs 
.SILENT: