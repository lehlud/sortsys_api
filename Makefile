.PHONY: all clean grammar

all: grammar

clean:
	rm -rf src/generated

grammar: QL.g4
	mkdir -p src/generated
	antlr4 QL.g4 -Dlanguage=JavaScript -o src/generated -no-listener -visitor



