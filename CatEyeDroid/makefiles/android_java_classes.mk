################### Main part ###################

TARGET_BIN_CLASSES = $(TARGET)/bin/classes

JAVA_FILES := $(shell cd $(SOURCE); find . -name \*.java) 
JAVA_GEN_FILES := $(shell cd $(GEN); find . -name \*.java)
CLASS_FILES = $(addprefix $(TARGET_BIN_CLASSES)/,$(addsuffix .class,$(basename $(JAVA_FILES))))
CLASS_GEN_FILES = $(addprefix $(TARGET_BIN_CLASSES)/,$(addsuffix .class,$(basename $(JAVA_GEN_FILES))))

all: classes

clean:
	@echo "[$(PROJ)] Removing Java classes..."
	rm -f $(foreach NAME, $(basename $(CLASS_FILES)), $(shell echo $(NAME)'\$$'\*.class))
	rm -f $(foreach NAME, $(basename $(CLASS_GEN_FILES)), $(shell echo $(NAME)'\$$'\*.class))
	rm -f $(CLASS_FILES)
	rm -f $(CLASS_GEN_FILES) 
	
	@echo "[$(PROJ)] Removing empty directories..."
	find $(TARGET) -depth -empty -type d -exec rmdir {} \;

################### Folders ###################

ENSURE_BIN_CLASSES = if [ ! -d "$(TARGET_BIN_CLASSES)" ]; then mkdir -p "$(TARGET_BIN_CLASSES)"; fi

################ Java classes #################

classes: $(CLASS_FILES) $(CLASS_GEN_FILES)

$(TARGET_BIN_CLASSES)/%.class : $(GEN)/%.java
	@echo "[$(PROJ)] Compiling generated java files $@ ..."
	$(ENSURE_BIN_CLASSES)
	"$(JAVA_HOME)/bin/javac" -g -sourcepath "$(GEN)" -classpath "$(EXTERNAL_JARS);$(CUSTOM_JARS);$(TARGET_BIN_CLASSES)" -d "$(TARGET_BIN_CLASSES)" $<

$(TARGET_BIN_CLASSES)/%.class : $(SOURCE)/%.java $(CLASS_GEN_FILES)
	@echo "[$(PROJ)] Compiling java class $@ ..."
	$(ENSURE_BIN_CLASSES)
	"$(JAVA_HOME)/bin/javac" -g -sourcepath "$(SOURCE)" -classpath "$(EXTERNAL_JARS);$(CUSTOM_JARS);$(TARGET_BIN_CLASSES)" -d "$(TARGET_BIN_CLASSES)" $<

.PHONY: all classes clean #deplibs 
.SILENT: