# Introduction
* This is our school assignment for Java practicing. This project includes reading, saving, playing and editing Wavfile. (May contain some mistakes.)  
* For more information (PPT written in traditional chinese) -> https://drive.google.com/file/d/1QaUfj6Lz13TAlaMf__OLEFVOOADTupjH/view?usp=sharing  

# How to run - Windows
* Make sure that `Java` is installed and `JavaFx` is added into your PATH.
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

# Reference
* We use `FFT.java` and `Complex.java` from (neat and understandable)  
  1. https://introcs.cs.princeton.edu/java/97data/FFT.java.html  
  2. https://introcs.cs.princeton.edu/java/97data/Complex.java.html  
