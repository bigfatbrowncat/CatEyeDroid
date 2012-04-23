################### Main part ###################

CC = gcc $(COMMON_CFLAGS) $(CFLAGS) $(CDEFINES)
CPP = g++ $(COMMON_CPPFLAGS) $(CPPFLAGS) $(CPPDEFINES)
COMMON_CPPFLAGS += $(COMMON_CFLAGS)
CPPFLAGS += $(CFLAGS)
CPPDEFINES += $(CDEFINES)

OSTYPE := $(shell uname)

TARGET_BIN = $(TARGET)/bin
TARGET_OBJ = $(TARGET)/obj
TARGET_LIB = $(TARGET)/lib
TARGET_GEN = $(TARGET)/gen

STATIC_LIB = $(LIBNAME).a
SHARED_LIB = $(LIBNAME).dll
SHARED_LINK = $(LIBNAME).dll.a

OBJECT_FILES_WITH_PATH = $(addprefix $(TARGET_OBJ)/,$(addsuffix .o,$(OBJECTS)))
HEADERS_WITH_PATH = $(shell cd $(INCLUDE); find . -name \*.h)

all: shared_lib static_lib

clean:
	@echo "[$(PROJ)] Removing static library..."
	rm -f $(TARGET_LIB)/$(STATIC_LIB)
	rm -f $(TARGET_LIB)/$(SHARED_LINK)

	@echo "[$(PROJ)] Removing shared library..."
	rm -f $(TARGET_BIN)/$(SHARED_LIB)

	@echo "[$(PROJ)] Removing object files..."
	rm -f $(OBJECT_FILES_WITH_PATH)

	@echo "[$(PROJ)] Removing empty directories..."
	find $(TARGET) -depth -empty -type d -exec rmdir {} \;

shared_lib: $(TARGET_BIN)/$(SHARED_LIB)
static_lib: $(TARGET_LIB)/$(STATIC_LIB)

################### Folders ###################

ENSURE_BIN = if [ ! -d "$(TARGET_BIN)" ]; then mkdir -p "$(TARGET_BIN)"; fi
ENSURE_GEN = if [ ! -d "$(TARGET_GEN)" ]; then mkdir -p "$(TARGET_GEN)"; fi
ENSURE_LIB = if [ ! -d "$(TARGET_LIB)" ]; then mkdir -p "$(TARGET_LIB)"; fi
ENSURE_OBJ = if [ ! -d "$(TARGET_OBJ)" ]; then mkdir -p "$(TARGET_OBJ)"; fi

################### Objects ###################

$(OBJECT_FILES_WITH_PATH) : $(TARGET_OBJ)/%.o : $(SOURCE)/%.cpp $(HEADERS) 
	@echo "[$(PROJ)] Compiling $@ ..."
	$(ENSURE_OBJ)
	if [ ! -d $(dir $@) ]; then mkdir -p $(dir $@); fi
	$(CPP) -c $< -o $@ -I$(INCLUDE)

################### Targets ###################

$(TARGET_BIN)/$(SHARED_LIB): $(OBJECT_FILES_WITH_PATH) 
	@echo "[$(PROJ)] Building shared library $@ ..."
	$(ENSURE_BIN)
	$(ENSURE_LIB)
	$(CPP) -shared $(OBJECT_FILES_WITH_PATH) $(LIBS) -o $@ -Wl,--add-stdcall-alias -Wl,--out-implib=$(TARGET_LIB)/$(SHARED_LINK)

$(TARGET_LIB)/$(STATIC_LIB): $(OBJECT_FILES_WITH_PATH)
	@echo "[$(PROJ)] Building static library $@ ..."
	$(ENSURE_LIB)
	ar crv $@ $(OBJECT_FILES_WITH_PATH)
	ranlib $@

############### Dependent libs ################

.PHONY: all static_lib shared_lib clean
.SILENT: