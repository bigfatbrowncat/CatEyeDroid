# This make include file provides java classes build procedure
# Parameters:
#    BUILD_CLASSPATHS - dependency classpath (no semicolon in the end!)
#    SOURCE - java source path
# Targets:
#    classes - build classes
#    classes_clean - clean classes

CLASSPATH = out/dalvik_android_arm/classes
JARPATH = out/dalvik_android_arm/jar

SOURCE = src
SOURCEGEN = gen

JAVA_FILES := $(shell cd $(SOURCE); find . -name \*.java | awk '{ sub(/.\//,"") }; 1'))
JAVA_GEN_FILES := $(shell \
                      if [ -d "$(SOURCEGEN)" ]; \
                      then \
                          cd $(SOURCEGEN); \
                          find . -name \*.java | awk '{ sub(/.\//,"") }; 1'; \
                      fi; \
                  )

CLASS_FILES = $(addprefix $(CLASSPATH)/,$(addsuffix .class,$(basename $(JAVA_FILES))))
CLASS_GEN_FILES = $(addprefix $(CLASSPATH)/,$(addsuffix .class,$(basename $(JAVA_GEN_FILES))))

BUILD_CLASSPATHS = $(shell echo "$(DEP_CLASSPATHS);$(DEP_JARS)" | awk 'gsub(/ +/, ";"); 1';)

classes_clean:
	@echo "[$(PROJ)] Removing Java classes"
	rm -f $(foreach NAME, $(basename $(CLASS_FILES)), $(shell echo $(NAME)'\$$'\*.class))
	rm -f $(CLASS_FILES) 
	
	@echo "[$(PROJ)] Removing empty directories..."
	find . -depth -empty -type d -exec rmdir {} \;

jar_clean:
	@echo "[$(PROJ)] Removing jar"
	rm -f $(JARPATH)/$(JARNAME)

	@echo "[$(PROJ)] Removing empty directories..."
	find . -depth -empty -type d -exec rmdir {} \;

################### Folders ###################

ENSURE_CLASSES = if [ ! -d "$(CLASSPATH)" ]; then mkdir -p "$(CLASSPATH)"; fi
ENSURE_JAR = if [ ! -d "$(JARPATH)" ]; then mkdir -p "$(JARPATH)"; fi

################ Java classes #################

classes: $(CLASS_FILES) $(CLASS_GEN_FILES)

$(CLASSPATH)/%.class : $(SOURCE)/%.java $(DEP_JARS)
	@echo "[$(PROJ)] Compiling java class $@"
	$(ENSURE_CLASSES)
	"$(JAVA_HOME)/bin/javac" -g -sourcepath "$(SOURCE);$(SOURCEGEN)" -classpath "$(BUILD_CLASSPATHS);$(CLASSPATH)" -d "$(CLASSPATH)" $<

$(CLASSPATH)/%.class : $(SOURCEGEN)/%.java $(DEP_JARS)
	@echo "[$(PROJ)] Compiling java class $@"
	$(ENSURE_CLASSES)
	"$(JAVA_HOME)/bin/javac" -g -sourcepath "$(SOURCE);$(SOURCEGEN)" -classpath "$(BUILD_CLASSPATHS);$(CLASSPATH)" -d "$(CLASSPATH)" $<

jar:
	@echo "[$(PROJ)] Creating jar file $(JARPATH)/$(JARNAME)"
	$(ENSURE_JAR)
	"$(JAVA_HOME)/bin/jar" cf $(JARPATH)/$(JARNAME) -C $(CLASSPATH) .

.PHONY: classes classes_clean jar jar_clean
