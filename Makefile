SOURCES := $(wildcard *.java)
CLASSES := $(patsubst %.java,%.class,$(SOURCES))

all: $(CLASSES) LooooooongFishGame.jar

LooooooongFishGame.jar: META-INF/MANIFEST.MF
	zip -0 -r - $< >$@

%.class: %.java
	@#    --release 8
	javac --release 10 -cp 'libs-linux64/*:.' $<

.PHONY: clean fmt run

clean:
	rm -v *.class LooooooongFishGame.jar

fmt: $(SOURCES)
	java -jar LooooooongFishGames-java-format-*-all-deps.jar -i $^

run: $(CLASSES)
	java --add-opens java.base/java.lang=ALL-UNNAMED -cp 'libs-linux64/*:.' LooooooongFishGame || true

