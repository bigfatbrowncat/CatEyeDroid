EXTERNAL = ../external
TARGET = ../CatEyePC

all: classes extractswt

include Classes.mk

extractswt:
	cd $(TARGET)/bin/classes; \
	"$(JAVA_HOME)/bin/jar" xvf ../../$(EXTERNAL)/jar/swt.jar 
