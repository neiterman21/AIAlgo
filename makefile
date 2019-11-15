CC = javac
classes = ex1.java CPT.java Bvar.java
ex1.class: $(classes)
	$(CC) *.java

.PHONY: run

run:
	java ex1
