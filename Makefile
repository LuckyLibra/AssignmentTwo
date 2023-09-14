JAVAC=javac
sources = $(wildcard *.java)
classes = $(sources:.java=.class)

all: program

program: $(classes)

out: output

clean: rm -f *.class

%.class: %.java
	$(JAVAC) $<

.PHONY: all program clean jar