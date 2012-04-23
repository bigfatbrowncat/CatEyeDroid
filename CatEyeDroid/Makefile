all: droid core

droid: core
	make -f Classes.mk
	
core:
	make -C ../CatEyeB.Core -f Classes.mk all
	
clean:
	make -f Classes.mk clean
	make -C ../CatEyeB.Core -f Classes.mk clean