EXTERNAL = ../external
TARGET = ../CatEyePC

all: extractswt classes

include Classes.mk

extractswt:
	if [ ! -d "$(TARGET)/bin/classes" ]; then mkdir -p "$(TARGET)/bin/classes"; fi
	
	echo "Unpacking swt.jar"
	cd $(TARGET)/bin/classes; \
	"$(JAVA_HOME)/bin/jar" xf ../../$(EXTERNAL)/jar/swt.jar 
