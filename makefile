CC = javac
classes = ex1.java CPT.java Bvar.java parser.java
ex1.class: $(classes)
	$(CC) *.java

.PHONY: run clean

run:
	java ex1

clean:
	rm *.class
