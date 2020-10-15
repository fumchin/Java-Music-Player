//WavFile
//RIFF chunk -> know its type (wav here)
//Fmt chunk -> describe the format of the sound information
//data chunk -> size of sound information and raw sound data
//output -> signal(Object ArrayList<double>) between -1 and 1
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
    private static Note note = new Note();
    private static InputStream input = null;
    private static String fileName;

    private static ArrayList<Double>[] signal; // normalize between -1~1
    private static ArrayList<Double>[] signal_dB; // change to dB

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
            byte[] buffer_note;
            byte[] buffer_signal;

            // input = new FileInputStream("Tim_Henson_VS_Ichika_Nito.wav");
            // input = new FileInputStream("C_major.wav");
            input = new FileInputStream(fileName);
            // Riff
            input.read(buffer_four);
            riff.setChunkID(buffer_four);
            input.read(buffer_four);
            riff.setChunSize(buffer_four);
            input.read(buffer_four);
            riff.setFormat(buffer_four);

            // Format
            input.read(buffer_four);
            fmt.setSubchunk(buffer_four);
            input.read(buffer_four);
            fmt.setSubchunk1Size(buffer_four);
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
            input.read(buffer_four);
            data.setDataSubchunk(buffer_four);
            // might be data chunk or note chunk, need to check
            if (data.getDataSubchunk().compareTo("data") == 0) {
                input.read(buffer_four);
                data.setSubchunk2Size(buffer_four);
            } else {
                note.setNoteID(data.getDataSubchunk());
                input.read(buffer_four);
                note.setNoteChunkSize(buffer_four);
                buffer_note = new byte[(int) note.getNoteChunkSize()];
                input.read(buffer_note);
                note.setNoteContent(buffer_note);
                input.read(buffer_four);
                data.setDataSubchunk(buffer_four);
                input.read(buffer_four);
                data.setSubchunk2Size(buffer_four);
            }
            // get real data
            buffer_signal = new byte[fmt.getBitsPerSample() / (fmt.getNumChannels() * 4)];
            signal = new ArrayList[fmt.getNumChannels()]; // new with numbers of channel
            signal_dB = new ArrayList[fmt.getNumChannels()]; // new with numbers of channel
            for (int i = 0; i < fmt.getNumChannels(); i++) {
                signal[i] = new ArrayList<Double>();
                signal_dB[i] = new ArrayList<Double>();
            }
            double temp;
            int k;
            int count = 0;
            double normalizeConstant;
            if (fmt.getBitsPerSample() == 8) {
                // if bitsPerSample = 8 => unsigned
                normalizeConstant = Math.pow(2, fmt.getBitsPerSample());
            } else {
                // if bitsPerSample = 16 or 32 => signed
                normalizeConstant = Math.pow(2, fmt.getBitsPerSample() - 1);
            }
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
        // ChunkID = new char[4];
        chunkSize = 0;
        // format = new char[4];
    }

    public void setChunkID(byte[] chunkID_read) {
        char[] chunkID_char = new char[4];
        for (int i = 0; i < chunkID_read.length; i++) {
            // System.out.println(ChunkID_read[i]);
            chunkID_char[i] = (char) (int) Integer.valueOf(chunkID_read[i]);
        }
        chunkID = new String(chunkID_char);
        System.out.println("chunk ID:\t" + chunkID);
    }

    public void setChunSize(byte[] chunkSize_read) {
        int k = 0;
        chunkSize = 0;
        for (int i = 0; i < chunkSize_read.length; i++) {
            chunkSize += (long) (Integer.valueOf(chunkSize_read[i]) & 0xFF) * Math.pow(16, k);
            k += 2;
        }
        System.out.println("chunk size:\t" + chunkSize);
    }

    public void setFormat(byte[] format_read) {
        char[] format_char = new char[4];
        for (int i = 0; i < format_read.length; i++) {
            format_char[i] = (char) (int) Integer.valueOf(format_read[i]);
        }
        format = new String(format_char);
        System.out.println("format:\t" + format);
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
    private String subchunk;
    private long subchunk1Size;
    private int audioFormat; // 1->PCM
    private int numChannels; // channel
    private int sampleRate;
    private long byteRate;
    private int blockAlign;
    private int bitsPerSample;

    public Fmt() {
        subchunk1Size = 0;
        audioFormat = 0;
        numChannels = 0;
        sampleRate = 0;
        byteRate = 0;
        blockAlign = 0;
        bitsPerSample = 0;
    }

    public void setSubchunk(byte[] subchunk_read) {
        char[] subchunk_char = new char[4];
        for (int i = 0; i < subchunk_read.length; i++) {
            subchunk_char[i] = (char) (int) Integer.valueOf(subchunk_read[i]);
        }
        subchunk = new String(subchunk_char);
        System.out.println("subchunk:\t" + subchunk);
    }

    public void setSubchunk1Size(byte[] subchunk1Size_read) {
        int k = 0;
        subchunk1Size = 0;
        for (int i = 0; i < subchunk1Size_read.length; i++) {
            subchunk1Size += (long) (Integer.valueOf(subchunk1Size_read[i]) & 0xFF) * Math.pow(16, k);
            k += 2;
        }
        System.out.println("subchunk1 size:\t" + subchunk1Size);
    }

    public void setAudioFormat(byte[] audioFormat_read) {
        int k = 0;
        audioFormat = 0;
        for (int i = 0; i < audioFormat_read.length; i++) {
            audioFormat += (Integer.valueOf(audioFormat_read[i]) & 0xFF) * Math.pow(16, k);
            k += 2;
        }
        System.out.println("audio format:\t" + audioFormat);
    }

    public void setNumChannels(byte[] numChannels_read) {
        int k = 0;
        numChannels = 0;
        for (int i = 0; i < numChannels_read.length; i++) {
            numChannels += (Integer.valueOf(numChannels_read[i]) & 0xFF) * Math.pow(16, k);
            k += 2;
        }
        System.out.println("num channels:\t" + numChannels);
    }

    public void setSampleRate(byte[] sampleRate_read) {
        int k = 0;
        sampleRate = 0;
        for (int i = 0; i < sampleRate_read.length; i++) {
            sampleRate += (Integer.valueOf(sampleRate_read[i]) & 0xFF) * Math.pow(16, k);
            k += 2;
        }
        System.out.println("sample rate:\t" + sampleRate);
    }

    public void setByteRate(byte[] byteRate_read) {
        int k = 0;
        byteRate = 0;
        for (int i = 0; i < byteRate_read.length; i++) {
            byteRate += (long) (Integer.valueOf(byteRate_read[i]) & 0xFF) * Math.pow(16, k);
            k += 2;
        }
        System.out.println("byteRate:\t" + byteRate);
    }

    public void setBlockAlign(byte[] blockAlign_read) {
        int k = 0;
        blockAlign = 0;
        for (int i = 0; i < blockAlign_read.length; i++) {
            blockAlign += (Integer.valueOf(blockAlign_read[i]) & 0xFF) * Math.pow(16, k);
            k += 2;
        }
        System.out.println("block align:\t" + blockAlign);
    }

    public void setBitsPerSample(byte[] bitsPerSample_read) {
        int k = 0;
        bitsPerSample = 0;
        for (int i = 0; i < bitsPerSample_read.length; i++) {
            bitsPerSample += (Integer.valueOf(bitsPerSample_read[i]) & 0xFF) * Math.pow(16, k);
            k += 2;
        }
        System.out.println("bit per sample:\t" + bitsPerSample);
    }

    public String getSubchunk() {
        return subchunk;
    }

    public long getSubchunk1Size() {
        return subchunk1Size;
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
    private String dataSubchunk;
    private long subchunk2Size;

    public Data() {
        subchunk2Size = 0;
    }

    public void setDataSubchunk(byte[] dataSubchunk_read) {
        char[] dataSubchunk_char = new char[4];
        for (int i = 0; i < dataSubchunk_read.length; i++) {
            dataSubchunk_char[i] = (char) (int) Integer.valueOf(dataSubchunk_read[i]);
        }
        dataSubchunk = new String(dataSubchunk_char);
        System.out.println("data subchunk:\t" + dataSubchunk);
    }

    public void setSubchunk2Size(byte[] subchunk2Size_read) {
        int k = 0;
        subchunk2Size = 0;
        for (int i = 0; i < subchunk2Size_read.length; i++) {
            subchunk2Size += (Integer.valueOf(subchunk2Size_read[i]) & 0xFF) * Math.pow(16, k);
            k += 2;
        }
        System.out.println("subchunk2 size:\t" + subchunk2Size);
    }

    public String getDataSubchunk() {
        return dataSubchunk;
    }

    public long getSubchunk2Size() {
        return subchunk2Size;
    }
}

class Note {
    private String noteID;
    private long noteChunkSize;
    private String noteContent;

    public Note() {
        noteChunkSize = 0;
    }

    public void setNoteID(String fromDataID) {
        noteID = new String(fromDataID);
        System.out.println("note ID:\t" + noteID);
    }

    public void setNoteChunkSize(byte[] noteChunkSize_read) {
        int k = 0;
        noteChunkSize = 0;
        for (int i = 0; i < noteChunkSize_read.length; i++) {
            noteChunkSize += (Integer.valueOf(noteChunkSize_read[i]) & 0xFF) * Math.pow(16, k);
            k += 2;
        }
        System.out.println("note chunk size:\t" + noteChunkSize);
    }

    public void setNoteContent(byte[] noteContent_read) {
        char[] noteContent_char = new char[(int) getNoteChunkSize()];
        for (int i = 0; i < noteContent_read.length; i++) {
            noteContent_char[i] = (char) (int) Integer.valueOf(noteContent_read[i]);
        }
        noteContent = new String(noteContent_read);
        // System.out.println("note content:\t" + noteContent);
    }

    public String getNoteId() {
        return noteID;
    }

    public long getNoteChunkSize() {
        return noteChunkSize;
    }
}