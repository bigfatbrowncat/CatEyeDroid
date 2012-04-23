PROJ = raw

OSTYPE := $(shell uname)
COMMON_CFLAGS = -static-libgcc
COMMON_CPPFLAGS = $(COMMON_CFLAGS) -static-libstdc++ -fopenmp

CFLAGS = -O3
CPPFLAGS = -O3

DEFINES = -DLIBRAW_LIBRARY_BUILD

CC = gcc $(COMMON_CFLAGS) $(CFLAGS) $(THREADFLAG) $(DEFINES)
CPP = g++ $(COMMON_CPPFLAGS) $(CPPFLAGS) $(THREADFLAG) $(DEFINES)

INCLUDES = 

SOURCE = src
PUBLIC_HEADERS = libraw
PRIVATE_HEADERS = internal

TARGET = ../CatEyePC

TARGET_BIN = $(TARGET)/bin
TARGET_OBJ = $(TARGET)/obj
TARGET_LIB = $(TARGET)/lib

OBJECT_FILES = dcraw_common.o \
               dcraw_fileio.o \
               demosaic_packs.o \
               libraw_cxx.o \
               libraw_datastream.o \
               libraw_c_api.o

PRIVATE_HEADER_FILES = defines.h libraw_bytebuffer.h var_defines.h \
                       libraw_internal_funcs.h 
PUBLIC_HEADER_FILES = libraw_alloc.h libraw_const.h \
                      libraw_datastream.h libraw_internal.h \
                      libraw_types.h libraw_version.h libraw.h

OBJECT_FILES_WITH_PATH = $(addprefix $(TARGET_OBJ)/,$(OBJECT_FILES))
PUBLIC_HEADER_FILES_WITH_PATH = $(addprefix $(PUBLIC_HEADERS)/,$(PUBLIC_HEADER_FILES))
PRIVATE_HEADER_FILES_WITH_PATH = $(addprefix $(PRIVATE_HEADERS)/,$(PRIVATE_HEADER_FILES))

LIBNAME = raw

LIB_STATIC_NAME = $(LIBNAME).a

ifneq (,$(findstring MINGW, $(OSTYPE)))			# OS type is MinGW 32
	# In Windows we need to link to libws2_32
	LIBS = -lws2_32 -lm
	LIB_SHARED_NAME = $(LIBNAME).dll
else											# any other case assumed as a POSIX system
	LIBS = -lm
	THREADFLAG = -pthread
	LIB_SHARED_NAME = lib$(LIBNAME).so
endif


SHARED_LIB = $(LIB_SHARED_NAME)
STATIC_LIB = $(LIB_STATIC_NAME)
SHARED_LINK = $(LIB_SHARED_NAME).a

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

static_lib: $(TARGET_LIB)/$(STATIC_LIB)
shared_lib: $(TARGET_BIN)/$(SHARED_LIB)

################### Folders ###################

ENSURE_BIN = if [ ! -d "$(TARGET_BIN)" ]; then mkdir -p "$(TARGET_BIN)"; fi
ENSURE_LIB = if [ ! -d "$(TARGET_LIB)" ]; then mkdir -p "$(TARGET_LIB)"; fi
ENSURE_OBJ = if [ ! -d "$(TARGET_OBJ)" ]; then mkdir -p "$(TARGET_OBJ)"; fi

################### Objects ###################

$(OBJECT_FILES_WITH_PATH) : $(TARGET_OBJ)/%.o : $(SOURCE)/%.cpp $(PUBLIC_HEADER_FILES_WITH_PATH) $(PRIVATE_HEADER_FILES_WITH_PATH)
	@echo "[$(PROJ)] Compiling $@ ..."
	$(ENSURE_OBJ)
	$(CPP) -c $< -o $@ $(INCLUDES) -I"$(PUBLIC_HEADERS)" -I"$(PRIVATE_HEADERS)"

################### Targets ###################

$(TARGET_BIN)/$(SHARED_LIB): $(OBJECT_FILES_WITH_PATH)
	@echo "[$(PROJ)] Building shared library $@ ..."
	$(ENSURE_BIN)
	$(ENSURE_LIB)
	$(CPP) -shared $(OBJECT_FILES_WITH_PATH) $(LIBS) -o $@ -Wl,--out-implib=$(TARGET_LIB)/$(SHARED_LINK)

$(TARGET_LIB)/$(STATIC_LIB): $(OBJECT_FILES_WITH_PATH)
	@echo "[$(PROJ)] Building static library $@ ..."
	$(ENSURE_LIB)
	ar crv $@ $(OBJECT_FILES_WITH_PATH)
	ranlib $@

.PHONY: all shared_lib static_lib
.SILENT: