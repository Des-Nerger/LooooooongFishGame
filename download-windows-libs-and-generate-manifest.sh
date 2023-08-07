#!/bin/sh
libs=libs-windows
gdxVer=1.10.0
spineVer=3.8.55.1
lwjglVer=2.9.3
repo=https://repo1.maven.org/maven2
mkdir -p $libs && cd $libs && wget --wait=1 \
	$repo/com/badlogicgames/gdx/gdx-platform/$gdxVer/gdx-platform-$gdxVer-natives-desktop.jar \
	$repo/com/badlogicgames/gdx/gdx-backend-lwjgl/$gdxVer/gdx-backend-lwjgl-$gdxVer.jar \
	$repo/com/badlogicgames/gdx/gdx/$gdxVer/gdx-$gdxVer.jar \
	$repo/com/esotericsoftware/spine/spine-libgdx/$spineVer/spine-libgdx-$spineVer.jar \
	$repo/org/lwjgl/lwjgl/lwjgl/$lwjglVer/lwjgl-$lwjglVer.jar \
	$repo/org/lwjgl/lwjgl/lwjgl-platform/$lwjglVer/lwjgl-platform-$lwjglVer-natives-windows.jar && \
		cd .. && mkdir -p META-INF && cat >META-INF/MANIFEST.MF <<- ᐸ/heredocᐳ
			Manifest-Version: 1.0
			Class-Path: $libs/gdx-$gdxVer.jar $libs/gdx-backend-lwjgl-$gdxVer.jar $libs/gdx-platform-$gdxVer-natives-desktop.jar $libs/lwjgl-$lwjglVer.jar $libs/lwjgl-platform-$lwjglVer-natives-windows.jar $libs/spine-libgdx-$spineVer.jar .
			Main-Class: LooooooongFishGame
		ᐸ/heredocᐳ
			

