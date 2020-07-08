# Introduction
> this is our school assignment for Java practicing.  
> this project includes reading, saving, playing and editing Wavfile. May contain some mistakes.  

# How to run - Windows
* Make sure that `Java` is installed and `JavaFx` is added into your PATH.( adding path: https://openjfx.io/openjfx-docs/#install-javafx)
* Open powershell
* run  
  1. `javac --module-path $env:PATH_TO_FX --add-modules=javafx.controls,javafx.fxml,javafx.media  *.java `  
  2. `java --module-path $env:PATH_TO_FX --add-modules=javafx.controls,javafx.fxml,javafx.media Player` 
* Choose a Wave audio file you like and edit it

# Some cool fuctions
1. Graphic Equalizer  (use eq to modify the frquency)  
2. Chord Identify (can only find Major, minor and dim, and this function is not pretty accurate)  

# How it works
* for those interested in reading & saving Wavfile, check `WavFile.java`.  
  * https://github.com/fumchin/project/blob/master/WavFile.java
* for those interested in playing audio by sample points, check `playBySample() method in PlayerController.java`.  
  * https://github.com/fumchin/project/blob/master/PlayerController.java#L618
* for those interested in Equalizer, check `TenEQController.java`.
  * https://github.com/fumchin/project/blob/master/TenEQController.java
* for those interested in Chord Identify, check `FrequencyAnalysisController.java` and `ChordComposition.java`
  * https://github.com/fumchin/project/blob/master/FrequencyAnalysisController.java
  * https://github.com/fumchin/project/blob/master/ChordComposition.java
