# Introduction
* 109下學期Java期末專題
* 做音樂播放器

# 功能
1. 讀取Wav檔案，最好是44kHz、16 bits per sample的，不然可能會掛掉
2. 儲存修改好的wav檔案
3. 預覽播放，可以讀在修改當下聽修改的內容
4. 簡易的編輯功能：速度、音量等等
5. EQ(Equalizer)等化器，可以調頻率
6. 簡易的和弦辨識，還有很大的進步空間，但簡單的和弦是可以成功辨識的（吉他伴奏之類的）
7. 播放影片、錄音......

# 版本
* Java 11
* JavaFx 11

## Linux
  1. install java 11
  ```console
  sudo apt-get update
  sudo apt-get install oracle-java11-installer-local
  ```
# 最近在幹麻
* 之前因為很趕所以fft, ifft的轉換並沒有用得很精細，最近在fft之前加了hanning window並在ifft後利用overlap重組訊號，以減少spectral leakage的產生。
* 優化一下程式碼，寫太趕有點亂哈哈，而且原先fft, ifft一跑下去很容易當機發燙，最近在把他修好一點。
* 寫了shell script，好執行多了。

# 之後要幹麻
* 拍點偵測：本來在學期間想做出來，但是失敗了qqqq，想花點時間把他做好，畢竟相信對和弦辨識會有一定幫助。
* 和弦辨識：一開始做這個本來是想做好和弦辨識就號，但做到一半跑去做EQ了。之後想要把和弦辨識做好，先從一般的方法開始，有機會考慮能夠加入deep learning吧，但不知道有沒有時間。
* 效果器：本身蠻愛彈吉他的，希望能寫出效果器(delay, reverb, distrotion......)然後把吉他接到電腦上去做即時的轉換，感覺會蠻好玩的。
* 調音器、即時和弦辨識......之類的，希望推甄有上不用考試就可以好好玩這個了哈哈哈。

# Reference
* We use `FFT.java` and `Complex.java` from (neat and understandable)  
  1. https://introcs.cs.princeton.edu/java/97data/FFT.java.html  
  2. https://introcs.cs.princeton.edu/java/97data/Complex.java.html  
