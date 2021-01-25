# Introduction
* NCTU Java　Programming Course final project

# Function
1. read your .wav music/audio files (support 8,16,32 bitsPerSample at any sampleRate)
2. read and store all sample points in .wav files into `ArrrayList<Double>[]` (check out WavFile.java)
3. save your edits in .wav format
4. preview your edits in real time
5. some basic functions：speed and volumn control
6. `graphic equalizer` that can edit frequency domain by implementing Hanning Windows, FFT(STFT), Filters and iFFT
7. `chord identification` (basic) that can analyze chord by implementing FFT(STFT), energy analysis and music theory
8. mp4 player, recorder......

# Group Members
+ [fumchin](https://github.com/fumchin)
+ [nctu0513325](https://github.com/nctu0513325)

# What we use
* Java 11  
* JavaFx 11

# How to run
## Ubuntu
1. install Java 11
```shell
$ sudo apt-get update
$ sudo apt-get install oracle-java11-installer-local
```
2. download and unzip [JavaFX Linux SDK - v11.0.2](https://gluonhq.com/products/javafx/)
3. open the terminal add javafx to PATH_TO_FX (path/to/ is where you locate your javafx folder)
```shell
$ export PATH_TO_FX=path/to/javafx-sdk-11.0.2/lib
``` 
or write it in `~/.bashrc` directly so you don't have to `export` every single time you open a new terminal
``` shell
$ echo 'export PATH_TO_FX=path/to/javafx-sdk-11.0.2/lib' >> ~/.bashrc 
$ source ~/.bashrc
```
4. run the program
```shell
$ cd path/to/Java-Music-Player
$ # run
$ ./run.sh

$ # you will only use the following commands if you are going to edit the code
$ # compile
$ ./compile.sh
$ # compile and run!!!
$ ./compile_and_run.sh
```

## Windows
1. download and install Java 11
2. download javaFX 11 and add it to environment variables (PATH_TO_FX), you can check [this](https://openjfx.io/openjfx-docs/#install-javafx) out
3. open windows powershell
```shell
$ cd path/to/Java-Music-Player
```
4. open `command_for_Windows.txt` and copy the text inside, pasting on the powershell.
```shell
$ #run
$ cd ./bin/
$ java --module-path $env:PATH_TO_FX --add-modules=javafx.controls,javafx.fxml,javafx.media Player
$ cd ..
```
or if you want to edit the code and compile it
```shell
$ # compile
$ javac --module-path $env:PATH_TO_FX --add-modules=javafx.controls,javafx.fxml,javafx.media  ./src/*.java -d ./bin/
``` 


# 最近在幹麻
* 重新review WavFile.java, 由`static`改成`non-static`，除了16 bits per sample, 將8 bits, 32 bits ......等等也納入考慮，目前可以讀取並播放8, 16 及32 bits per sample的檔案，存檔還須修正。
* 之前因為很趕所以fft, ifft的轉換並沒有用得很精細，最近在fft之前加了hanning window並在ifft後利用overlap重組訊號，以減少spectral leakage的產生。
* 優化一下程式碼，寫太趕有點亂哈哈，而且原先fft, ifft一跑下去很容易當機發燙，最近在把他修好一點。

# 之後要幹麻
* 拍點偵測：本來在學期間想做出來，但是失敗了qqqq，想花點時間把他做好，畢竟相信對和弦辨識會有一定幫助。
* 和弦辨識：一開始做這個本來是想做好和弦辨識就號，但做到一半跑去做EQ了。之後想要把和弦辨識做好，先從一般的方法開始，有機會考慮能夠加入deep learning吧，但不知道有沒有時間。
* 效果器：本身蠻愛彈吉他的，希望能寫出效果器(delay, reverb, distrotion......)然後把吉他接到電腦上去做即時的轉換，感覺會蠻好玩的。
* 調音器、即時和弦辨識......之類的，希望推甄有上不用考試就可以好好玩這個了哈哈哈。

# Reference
* We use `FFT.java` and `Complex.java` from (neat and understandable)  
  1. https://introcs.cs.princeton.edu/java/97data/FFT.java.html  
  2. https://introcs.cs.princeton.edu/java/97data/Complex.java.html  
