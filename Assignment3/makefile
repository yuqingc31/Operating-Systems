# compiler
JAVAC = javac
# Java source file
JAVA_FILES = Assignment3.java

all: compile

# Define the arguments you want to pass to the Java program
ARGS = ""

# compile server
compile:
	$(JAVAC) $(JAVA_FILES)

# # run the program
# run: compile
# 	java Assignment3

# clean the program
clean:
	rm -rf $(OUT_DIR)

.PHONY: all compile run clean