# This make include file provides copying of dependancies
# Parameters:
#    PROJ - project name
#    DEP_JARS - dependency jarfiles list (space separated)
#    DEP_CLASSPATHS - dependency classpaths (space separated)
# Targets:
#    extract_dep_jars - extracts external jars into classpath
#    copy_dep_classpaths - copies external classes into classpath
#    move_dlls_to_bin

CLASSPATH = bin/classes
FIND_DLLS = cd $(CLASSPATH); find . -name \*.dll | awk '{ sub(/.\//,"") }; 1'

ENSURE_CLASSES = if [ ! -d "$(CLASSPATH)" ]; then mkdir -p "$(CLASSPATH)"; fi

PWD = $(shell pwd)

extract_dep_jars:
	$(ENSURE_CLASSES)
	$(foreach jarname, $(DEP_JARS), \
	  echo "[$(PROJ)] Extracting $(jarname)"; \
	  ( cd $(CLASSPATH); \
	    "$(JAVA_HOME)/bin/jar" xf $(PWD)/$(jarname); ); )

copy_dep_classpaths:
	$(ENSURE_CLASSES)
	
	$(foreach clspath, $(DEP_CLASSPATHS), \
	  echo "[$(PROJ)] Importing classes from $(clspath)"; \
	  (cd $(clspath); \
	  cp -Rfu . $(PWD)/$(CLASSPATH);); \
	)
