################### Main part ###################

TARGET_BIN_CLASSES = $(TARGET)/bin/classes

JAVA_FILES := $(shell cd $(SOURCE); find . -name \*.java) 
CLASS_FILES = $(addprefix $(TARGET_BIN_CLASSES)/,$(addsuffix .class,$(basename $(JAVA_FILES))))

all: classes

clean:
	@echo "[$(PROJ)] Removing Java classes..."
	rm -f $(foreach NAME, $(basename $(CLASS_FILES)), $(shell echo $(NAME)'\$$'\*.class))
	rm -f $(CLASS_FILES)
	
	@echo "[$(PROJ)] Removing empty directories..."
	find $(TARGET) -depth -empty -type d -exec rmdir {} \;

################### Folders ###################

ENSURE_BIN_CLASSES = if [ ! -d "$(TARGET_BIN_CLASSES)" ]; then mkdir -p "$(TARGET_BIN_CLASSES)"; fi

################ Java classes #################

classes: $(CLASS_FILES)

$(TARGET_BIN_CLASSES)/%.class : $(SOURCE)/%.java
	@echo "[$(PROJ)] Compiling generated java files $@ ..."
	$(ENSURE_BIN_CLASSES)
	"$(JAVA_HOME)/bin/javac" -g -sourcepath "$(SOURCE)" -classpath "$(EXTERNAL_JARS);$(CUSTOM_JARS);$(TARGET_BIN_CLASSES)" -d "$(TARGET_BIN_CLASSES)" $<

.PHONY: all classes clean #deplibs 
.SILENT: