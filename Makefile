# Makefile for compiling Java files

# Define the Java compiler
JAVAC = javac

# Define flags for the Java compiler (Java 1.8)
JAVACFLAGS = -source 1.8 -target 1.8

# Define the source directory
SRC_DIR = src

# Define the output directory
OUT_DIR = bin

# Define the list of Java source files
SOURCES = $(wildcard $(SRCDIR)/*.java)

# Define the list of class files (generated from source files)
CLASSES = $(SOURCES:$(SRC_DIR)/%.java=$(OUT_DIR)/%.class)

# Default target (compiling all Java files)
all: $(CLASSES)

# Rule to compile a Java source file to a class file
$(OUT_DIR)/%.class: $(SRC_DIR)/%.java
	@mkdir -p $(OUT_DIR)
	$(JAVAC) $(JAVACFLAGS) -d $(OUT_DIR) $<

# Clean up generated class files
clean:
	rm -rf $(OUTDIR)
