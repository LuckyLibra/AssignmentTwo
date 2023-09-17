JAVAC=javac
JAVAFLAGS = -source 8 -target 8
sources = $(wildcard *.java)
classes = $(sources:.java=.class)

all: program

program: $(classes)

out: output

clean: rm -f *.class

%.class: %.java
	$(JAVAC) $(JAVAFLAGS) $<

.PHONY: all program clean jar