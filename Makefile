.PHONY: all clean grammar

all: grammar

clean:
	rm -rf api/src/main/java/de/sortsys/api/ql/generated

grammar: QL.g4
	mkdir -p api/src/main/java/de/sortsys/api/ql/generated
	antlr4 $< -Dlanguage=Java -o api/src/main/java/de/sortsys/api/ql/generated -no-listener -visitor -package de.sortsys.api.ql.generated

