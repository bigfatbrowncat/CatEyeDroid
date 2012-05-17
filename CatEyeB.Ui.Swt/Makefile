EXTERNAL = ../external
TARGET = ../CatEyePC

all: extractswt classes

include Classes.mk

extractswt:
	if [ ! -d "$(TARGET)/bin/classes" ]; then mkdir -p "$(TARGET)/bin/classes"; fi
	
	cd $(TARGET)/bin/classes; \
	"$(JAVA_HOME)/bin/jar" xvf ../../$(EXTERNAL)/jar/swt.jar 
