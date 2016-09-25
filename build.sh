#!/bin/bash

[ ! -d "bin" ] && mkdir "bin"
find src -name "*.java" | xargs javac -XDignore.symbol.file -d bin

jar cvfm agent.jar resource/MANIFEST.MF -C bin . > /dev/null


