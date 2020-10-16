#!/bin/bash
javac --module-path /home/fumchin/Documents/javafx/javafx-sdk-11.0.2/lib/ --add-modules javafx.controls,javafx.fxml,javafx.media ./src/*.java -d ./bin/
cd ./bin/
java --module-path /home/fumchin/Documents/javafx/javafx-sdk-11.0.2/lib/ --add-modules javafx.controls,javafx.fxml,javafx.media Player