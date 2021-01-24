#!/bin/bash
javac --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml,javafx.media ./src/*.java -d ./bin/
cd ./bin/
java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml,javafx.media Player
cd ..