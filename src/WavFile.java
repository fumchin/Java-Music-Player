//WavFile
// there are various chunk in wavfile , we get the most important chunk in the file
// 1 main chunk
// RIFF chunk -> know its type (wav here)

// 2 subchunks
// Fmt chunk -> describe the format of the sound information
// data chunk -> size of sound information and raw sound data

// output -> signal(Object ArrayList<double>) between -1 and 1
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.nio.ByteBuffer;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.*;
import java.io.File;

public class WavFile {

    private static Riff riff = new Riff();
    private static Fmt fmt = new Fmt();
    private static Data data = new Data();
    private static InputStream input = null;
    private static String fileName;

    private static ArrayList<Double>[] signal; // normalize between -1~1

    public static int getSampleRate() {
        return fmt.getSampleRate();
    }

    public static String getFileName() {
        return fileName;
    }

    public static int getNumChannels() {
        return fmt.getNumChannels();
    }

    public static ArrayList<Double>[] getSignal() {
        return signal;
    }

    public static int getBitsPerSample() {
        return fmt.getBitsPerSample();
    }

    public static void read(String fileNameInput) throws IOException {
        try {
            fileName = fileNameInput;
            byte[] buffer_four = new byte[4];
            byte[] buffer_two = new byte[2];
            byte[] buffer_signal;

            // input = new FileInputStream("Tim_Henson_VS_Ichika_Nito.wav");
            // input = new FileInputStream("C_major.wav");
            input = new FileInputStream(fileName);
            // Riff
            // find riff chunk
            do{
                input.read(buffer_four);
                riff.setChunkID(buffer_four);
            }while(riff.getChunkID().compareTo("RIFF") != 0);
            
            input.read(buffer_four);
            riff.setChunkDataSize(buffer_four);
            input.read(buffer_four);
            riff.setRiffType(buffer_four);

            // Format
            // find format chunk
            // the space after fmt is essential!!!
            do{
                input.read(buffer_four);
                fmt.setSubchunkID(buffer_four);
            }while(fmt.getSubchunkID().compareTo("fmt ") != 0);
            input.read(buffer_four);
            fmt.setFmtSubchunkSize(buffer_four);
            input.read(buffer_two);
            fmt.setAudioFormat(buffer_two);
            input.read(buffer_two);
            fmt.setNumChannels(buffer_two);
            input.read(buffer_four);
            fmt.setSampleRate(buffer_four);
            input.read(buffer_four);
            fmt.setByteRate(buffer_four);
            input.read(buffer_two);
            fmt.setBlockAlign(buffer_two);
            input.read(buffer_two);
            fmt.setBitsPerSample(buffer_two);

            // Data
            // we check "ta" instead of "data", in order to prevent the ignorance caused by shifting
            do{
                input.read(buffer_two);
                data.setDataSubchunk(buffer_two);
            }while(data.getDataSubchunk().compareTo("ta") != 0);
            input.read(buffer_four);
            data.setDataSubchunkSize(buffer_four);

            // get real data
            buffer_signal = new byte[fmt.getBitsPerSample() / (fmt.getNumChannels() * 4)];
            signal = new ArrayList[fmt.getNumChannels()]; // new with numbers of channel
            for (int i = 0; i < fmt.getNumChannels(); i++) {
                signal[i] = new ArrayList<Double>();
                signal[i].ensureCapacity((int) data.getSubchunk2Size());
            }
            double temp;
            int k;
            int count = 0;
            // double normalizeConstant;
            // if (fmt.getBitsPerSample() == 8) {
            //     // if bitsPerSample = 8 => unsigned
            //     normalizeConstant = Math.pow(2, fmt.getBitsPerSample());
            // } else {
            //     // if bitsPerSample = 16 or 32 => signed
            //     normalizeConstant = Math.pow(2, fmt.getBitsPerSample() - 1);
            // }
            // read hex datat from wav file
            while (count < (data.getSubchunk2Size() / fmt.getBlockAlign())) {
                for (int i = 0; i < fmt.getNumChannels(); i++) {
                    input.read(buffer_signal);
                    k = 0;
                    temp = 0;
                    if (fmt.getBitsPerSample() != 8) {
                        for (int j = 0; j < buffer_signal.length; j++) {
                            if (j == buffer_signal.length - 1) {
                                temp += (Integer.valueOf(buffer_signal[j])) * Math.pow(fmt.getBitsPerSample(), k);
                            } else {
                                temp += (Integer.valueOf(buffer_signal[j]) & 0xFF)
                                        * Math.pow(fmt.getBitsPerSample(), k);
                            }
                            k += 2;
                        }
                    } else {
                        for (int j = 0; j < buffer_signal.length; j++) {
                            temp += (Integer.valueOf(buffer_signal[j]) & 0xFF) * Math.pow(fmt.getBitsPerSample(), k);
                            k += 2;
                        }
                    }

                    // temp = (temp / normalizeConstant);
                    signal[i].add(Double.valueOf(temp));
                }
                count++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                input.close();
            }
        }

    }

    public static void saveAsWav(ArrayList<Double>[] input) {
        // file chooser

        Stage stage = new Stage();
        File file1=new File(".");
        String path=file1.getAbsolutePath();
        path=file1.getPath();   
        // class Note {
        //     private String noteID;
        //     private long noteChunkSize;
        //     private String noteContent;
        
        //     public Note() {
        //         noteChunkSize = 0;
        //     }
        
        //     public void setNoteID(String fromDataID) {
        //         noteID = new String(fromDataID);
        //         System.out.println("note ID:\t" + noteID);
        //     }
        
        //     public void setNoteChunkSize(byte[] noteChunkSize_read) {
        //         int k = 0;
        //         noteChunkSize = 0;
        //         for (int i = 0; i < noteChunkSize_read.length; i++) {
        //             noteChunkSize += (Integer.valueOf(noteChunkSize_read[i]) & 0xFF) * Math.pow(16, k);
        //             k += 2;
        //         }
        //         System.out.println("note chunk size:\t" + noteChunkSize);
        //     }
        
        //     public void setNoteContent(byte[] noteContent_read) {
        //         char[] noteContent_char = new char[(int) getNoteChunkSize()];
        //         for (int i = 0; i < noteContent_read.length; i++) {
        //             noteContent_char[i] = (char) (int) Integer.valueOf(noteContent_read[i]);
        //         }
        //         noteContent = new String(noteContent_read);
        //         // System.out.println("note content:\t" + noteContent);
        //     }
        
        //     public String getNoteId() {
        //         return noteID;
        //     }
        
        //     public long getNoteChunkSize() {
        //         return noteChunkSize;
        //     }
        // }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("save");
        fileChooser.setInitialDirectory(new File(path));
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("WAV file", "*.wav");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showSaveDialog(stage);
        try {
            // declare sourcedataline to stream in
            int bufferSize = 2200;
            byte[] data_write;
            AudioFormat audioFormat = new AudioFormat(fmt.getSampleRate(), fmt.getBitsPerSample(), fmt.getNumChannels(),
                    true, true);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int sampleCount = 0;
            int index = 0;
            byte[] buffer = new byte[bufferSize];
            while (sampleCount < input[0].size()) {
                while (index < bufferSize) {
                    for (int channel = 0; channel < fmt.getNumChannels(); channel++) {
                        // int temp = (int) (input[channel].get(sampleCount) * (double)
                        // normalizeConstant);
                        int temp = input[channel].get(sampleCount).intValue();
                        data_write = ByteBuffer.allocate(4).putInt(temp).array();
                        buffer[index] = data_write[2];
                        buffer[index + 1] = data_write[3];
                        index += fmt.getNumChannels();
                    }
                    sampleCount++;
                    if (sampleCount >= input[0].size()) {
                        break;
                    }
                }
                index = 0;
                byteArrayOutputStream.write(buffer, 0, bufferSize);
                buffer = new byte[bufferSize];
            }
            byte audioBytes[] = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(audioBytes);
            AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, audioFormat,
                    input[0].size());
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
        } catch (IOException e) {
            System.out.println(e.getStackTrace());

        }

    }

}

class Riff {
    private String chunkID;
    private long chunkSize;
    private String format;

    public Riff() {
        chunkSize = 0;
    }

    public void setChunkID(byte[] chunkID_read) {
        char[] chunkID_char = new char[4];
        for (int i = 0; i < chunkID_read.length; i++) {
            // System.out.println(ChunkID_read[i]);
            chunkID_char[i] = (char) (int) Integer.valueOf(chunkID_read[i]);
        }
        chunkID = new String(chunkID_char);
    }

    public void setChunkDataSize(byte[] chunkSize_read) {
        int k = 0;
        chunkSize = 0;
        for (int i = 0; i < chunkSize_read.length; i++) {
            chunkSize += (long) (Integer.valueOf(chunkSize_read[i]) & 0xFF) * Math.pow(16, k);
            k += 2;
        }
    }

    public void setRiffType(byte[] format_read) {
        char[] format_char = new char[4];
        for (int i = 0; i < format_read.length; i++) {
            format_char[i] = (char) (int) Integer.valueOf(format_read[i]);
        }
        format = new String(format_char);
    }

    public String getChunkID() {
        return chunkID;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public String getFormat() {
        return format;
    }

}

class Fmt {
    private String fmtSubchunkID;
    private long fmtSubchunkSize;
    private int audioFormat; // 1->PCM
    private int numChannels; // channel
    private int sampleRate;
    private long byteRate;
    private int blockAlign;
    private int bitsPerSample;

    public Fmt() {
        fmtSubchunkSize = 0;
        audioFormat = 0;
        numChannels = 0;
        sampleRate = 0;
        byteRate = 0;
        blockAlign = 0;
        bitsPerSample = 0;
    }

    public void setSubchunkID(byte[] subchunk_read) {
        char[] subchunk_char = new char[4];
        for (int i = 0; i < subchunk_read.length; i++) {
            subchunk_char[i] = (char) (int) Integer.valueOf(subchunk_read[i]);
        }
        fmtSubchunkID = new String(subchunk_char);
    }

    public void setFmtSubchunkSize(byte[] subchunk1Size_read) {
        int k = 0;
        fmtSubchunkSize = 0;
        for (int i = 0; i < subchunk1Size_read.length; i++) {
            fmtSubchunkSize += (long) (Integer.valueOf(subchunk1Size_read[i]) & 0xFF) * Math.pow(16, k);
            k += 2;
        }
    }

    public void setAudioFormat(byte[] audioFormat_read) {
        int k = 0;
        audioFormat = 0;
        for (int i = 0; i < audioFormat_read.length; i++) {
            audioFormat += (Integer.valueOf(audioFormat_read[i]) & 0xFF) * Math.pow(16, k);
            k += 2;
        }
    }

    public void setNumChannels(byte[] numChannels_read) {
        int k = 0;
        numChannels = 0;
        for (int i = 0; i < numChannels_read.length; i++) {
            numChannels += (Integer.valueOf(numChannels_read[i]) & 0xFF) * Math.pow(16, k);
            k += 2;
        }
    }

    public void setSampleRate(byte[] sampleRate_read) {
        int k = 0;
        sampleRate = 0;
        for (int i = 0; i < sampleRate_read.length; i++) {
            sampleRate += (Integer.valueOf(sampleRate_read[i]) & 0xFF) * Math.pow(16, k);
            k += 2;
        }
    }

    public void setByteRate(byte[] byteRate_read) {
        int k = 0;
        byteRate = 0;
        for (int i = 0; i < byteRate_read.length; i++) {
            byteRate += (long) (Integer.valueOf(byteRate_read[i]) & 0xFF) * Math.pow(16, k);
            k += 2;
        }
    }

    public void setBlockAlign(byte[] blockAlign_read) {
        int k = 0;
        blockAlign = 0;
        for (int i = 0; i < blockAlign_read.length; i++) {
            blockAlign += (Integer.valueOf(blockAlign_read[i]) & 0xFF) * Math.pow(16, k);
            k += 2;
        }
    }

    public void setBitsPerSample(byte[] bitsPerSample_read) {
        int k = 0;
        bitsPerSample = 0;
        for (int i = 0; i < bitsPerSample_read.length; i++) {
            bitsPerSample += (Integer.valueOf(bitsPerSample_read[i]) & 0xFF) * Math.pow(16, k);
            k += 2;
        }
    }

    public String getSubchunkID() {
        return fmtSubchunkID;
    }

    public long getSubchunk1Size() {
        return fmtSubchunkSize;
    }

    public int getAudioFomat() {
        return audioFormat;
    }

    public int getNumChannels() {
        return numChannels;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public long getByteRate() {
        return byteRate;
    }

    public int getBlockAlign() {
        return blockAlign;
    }

    public int getBitsPerSample() {
        return bitsPerSample;
    }
}

class Data {
    private String dataSubchunkID;
    private long dataSubchunkSize;

    public Data() {
        dataSubchunkSize = 0;
    }

    public void setDataSubchunk(byte[] dataSubchunk_read) {
        char[] dataSubchunk_char = new char[2];
        for (int i = 0; i < dataSubchunk_read.length; i++) {
            dataSubchunk_char[i] = (char) (int) Integer.valueOf(dataSubchunk_read[i]);
        }
        dataSubchunkID = new String(dataSubchunk_char);
    }

    public void setDataSubchunkSize(byte[] subchunk2Size_read) {
        int k = 0;
        dataSubchunkSize = 0;
        for (int i = 0; i < subchunk2Size_read.length; i++) {
            dataSubchunkSize += (Integer.valueOf(subchunk2Size_read[i]) & 0xFF) * Math.pow(16, k);
            k += 2;
        }
    }

    public String getDataSubchunk() {
        return dataSubchunkID;
    }

    public long getSubchunk2Size() {
        return dataSubchunkSize;
    }
}
