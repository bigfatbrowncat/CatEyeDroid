# This make include file provides copying of dependancies
# Parameters:
#    PROJ - project name
#    DEP_JARS - dependency jarfiles list (space separated)
#    DEP_CLASSPATHS - dependency classpaths (space separated)
# Targets:
#    extract_dep_jars - extracts external jars into classpath
#    copy_dep_classpaths - copies external classes into classpath
#    move_dlls_to_bin

CLASSPATH = out/avian_win32_i386/classes
BIN = out/avian_win32_i386/bin
FIND_DLLS = cd $(CLASSPATH); find . -name \*.dll | awk '{ sub(/.\//,"") }; 1'

ENSURE_CLASSES = if [ ! -d "$(CLASSPATH)" ]; then mkdir -p "$(CLASSPATH)"; fi
ENSURE_BIN = if [ ! -d "$(BIN)" ]; then mkdir -p "$(BIN)"; fi
PWD = $(shell pwd)

extract_dep_jars_no_move_dlls:
	$(ENSURE_CLASSES)
	$(foreach jarname, $(DEP_JARS), \
	  echo "[$(PROJ)] Extracting $(jarname)"; \
	  ( cd $(CLASSPATH); \
	    "$(JAVA_HOME)/bin/jar" xf $(PWD)/$(jarname); ); )

extract_dep_jars: extract_dep_jars_no_move_dlls
	$(foreach dllname, $(shell $(FIND_DLLS)), \
	  echo "[$(PROJ)] Moving $(dllname) to $(BIN)"; \
	  $(ENSURE_BIN); \
	  ( cd $(CLASSPATH); \
	    mv -f $(dllname) $(PWD)/$(BIN)/; ); )

copy_dep_classpaths:
	$(ENSURE_CLASSES)
	
	$(foreach clspath, $(DEP_CLASSPATHS), \
	  echo "[$(PROJ)] Importing classes from $(clspath)"; \
	  (cd $(clspath); \
	  cp -Rfu . $(PWD)/$(CLASSPATH);); \
	)
#cp -f -u `find . -name \*.class | awk '{ sub(/.\//,"") }; 1'` $(clspath)
	