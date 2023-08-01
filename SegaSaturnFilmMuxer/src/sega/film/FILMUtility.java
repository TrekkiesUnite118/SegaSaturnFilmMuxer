package sega.film;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FILMUtility {
       
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    
    private static final Logger log = Logger.getLogger(FILMUtility.class.getName());
    
    public static void parse(String inputFile, FILMfile file) throws IOException {
        if(inputFile == null) {
            log.log(Level.WARNING, "Input File path is null, aborting parsing.");
        } else {
            File f = new File(inputFile);
            
            byte[] FILMBytes = Files.readAllBytes(f.toPath());
            
            ByteBuffer bb = ByteBuffer.wrap(FILMBytes, 4, 4);
            file.getHeader().setHeaderSize(bb.getInt());
            
            bb = ByteBuffer.wrap(FILMBytes, 28, 4);
            file.getHeader().setHeight(bb.getInt());
            
            bb = ByteBuffer.wrap(FILMBytes, 32, 4);
            file.getHeader().setWidth(bb.getInt());
            
            bb = ByteBuffer.wrap(FILMBytes, 36, 1);
            file.getHeader().setBpp(bb.get());
            
            bb = ByteBuffer.wrap(FILMBytes, 37, 1);
            file.getHeader().setAudioChannels(bb.get());
            
            bb = ByteBuffer.wrap(FILMBytes, 38, 1);
            file.getHeader().setAudioResolution(bb.get());
            
            bb = ByteBuffer.wrap(FILMBytes, 39, 1);
            file.getHeader().setCompression(bb.get());
            
            bb = ByteBuffer.wrap(FILMBytes, 40, 2);
            file.getHeader().setSampleRate(bb.getShort());
            
            bb = ByteBuffer.wrap(FILMBytes, 52, 4);
            file.getHeader().getStab().setLength(bb.getInt());
            
            bb = ByteBuffer.wrap(FILMBytes, 56, 4);
            file.getHeader().getStab().setFramerateFrequency(bb.getInt());
            
            bb = ByteBuffer.wrap(FILMBytes, 60, 4);
            int numEntries = bb.getInt();
            file.getHeader().getStab().setNumOfEntries(numEntries);
            
            
            int offsetStart = 64;
            List<STABEntry> stabEntries = new ArrayList<>();
            for(int i = 0; i < numEntries; i++) {
                
                STABEntry entry = new STABEntry();
                bb = ByteBuffer.wrap(FILMBytes, offsetStart, 4);
                entry.setOffset(bb.getInt());
                offsetStart+=4;
                
                bb = ByteBuffer.wrap(FILMBytes, offsetStart, 4);
                entry.setLength(bb.getInt());
                offsetStart+=4;
                
                byte[] sample1 = Arrays.copyOfRange(FILMBytes, offsetStart, offsetStart + 4);
                offsetStart+=4;
                byte[] sample2 = Arrays.copyOfRange(FILMBytes, offsetStart, offsetStart + 4);
                offsetStart+=4;
                
                entry.setSampleInfo1(sample1);
                entry.setSampleInfo2(sample2);
                stabEntries.add(entry);
            }
            
            file.getHeader().getStab().setEntries(stabEntries);
            
            for(int i = 0; i < numEntries; i++) {
                STABEntry entry = stabEntries.get(i);
                byte[] chunk = Arrays.copyOfRange(FILMBytes, offsetStart, offsetStart + entry.getLength());
                offsetStart += entry.getLength();
                file.getChunks().add(chunk);
            }
        }
    }
    
    public static void extractAudio(FILMfile source, String outputFilePath, boolean waveOut) throws IOException {
        List<STABEntry> sourceStabs = source.getHeader().getStab().getEntries();
                        
        boolean isADX = false;
        if(source.getHeader().getCompression() == 2) {
            isADX = true;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        for(int i = 0; i < sourceStabs.size(); i++) {
            if(isAudioChunk(sourceStabs.get(i))) {
                
                if(!isADX && source.getHeader().getAudioChannels() == 2) {
                    int length = source.getChunks().get(i).length;
                    int halfway = source.getChunks().get(i).length / 2;
                    byte[] leftData = Arrays.copyOfRange(source.getChunks().get(i), 0, halfway);
                    byte[] rightData = Arrays.copyOfRange(source.getChunks().get(i), halfway, length);

                    int iter = 0;
                    if(source.getHeader().getAudioResolution() == 16) {
                        while(iter < leftData.length) {
                            out.write(leftData[iter]);
                            out.write(leftData[iter+1]);

                            out.write(rightData[iter]);
                            out.write(rightData[iter+1]);
                            
                            iter+=2;
                        }   
                    } else {
                        if(source.getHeader().getAudioResolution() == 8) {
                            while(iter < leftData.length) {
                                out.write(leftData[iter]);
                                out.write(rightData[iter]);
                                iter++;
                            }   
                        }
                    }
                    
                    
                } else {
                    out.write(source.getChunks().get(i));
                }
                
            }
        }
        
        if(!isADX && source.getHeader().getAudioResolution() == 16 && waveOut) {
            ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
            
            byte[] fileBytes = out.toByteArray();
            ByteBuffer bb = ByteBuffer.allocate(4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            outBuffer.write(new String("RIFF").getBytes());
            bb.putInt(0, fileBytes.length + 44);
            outBuffer.write(bb.array());
            outBuffer.write(new String("WAVE").getBytes());
            outBuffer.write(new String("fmt ").getBytes());
            bb.putInt(0, 16);
            outBuffer.write(bb.array());
            bb = ByteBuffer.allocate(2);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putShort(0, (short) 1);
            outBuffer.write(bb.array());
            bb.putShort(0, (short) source.getHeader().getAudioChannels());
            outBuffer.write(bb.array());
            bb = ByteBuffer.allocate(4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putInt(0, source.getHeader().getSampleRate());
            outBuffer.write(bb.array());
            
            int sampleRate = source.getHeader().getSampleRate();
            int resolution = source.getHeader().getAudioResolution();
            int channels = source.getHeader().getAudioChannels();
            
            int waveHeaderValue_1 = ((sampleRate * resolution * channels) / 8 );
            short waveHeaderValue_2 = (short) ((resolution * channels) / 8 );
            
            bb.putInt(0, waveHeaderValue_1);
            outBuffer.write(bb.array());
            

            bb = ByteBuffer.allocate(2);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putShort(0, waveHeaderValue_2);
            outBuffer.write(bb.array());
            bb.putShort(0, (short) resolution);
            outBuffer.write(bb.array());
            outBuffer.write(new String("data").getBytes());
            bb = ByteBuffer.allocate(4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putInt(0, fileBytes.length);
            outBuffer.write(bb.array());

            outBuffer.write(swapByteOrder(fileBytes));
            
                 
            out = outBuffer;
        }
        
        String[] pieces = outputFilePath.split("\\.");
        
        if(pieces.length > 1) {
            if(isADX) {
                pieces[pieces.length - 1] = "ADX";
            } else if(waveOut && source.header.getAudioResolution() == 16){
                pieces[pieces.length - 1] = "WAV";
            } else {
                pieces[pieces.length - 1] = "PCM";
            }
            
            String outputPath = pieces[0];
            
            for (int i = 1; i < pieces.length; i++) {
                outputPath = outputPath.concat(".").concat(pieces[i]);
            }
            
            outputFilePath = outputPath;
        }
        
        Path path = Paths.get(outputFilePath);
        try {
            Files.write(path, out.toByteArray());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Caught IOException attempting to write bytes to file.", e);
            e.printStackTrace();
        }
    }
    
    public static FILMfile swapAudioFromADXFile(String adxFilePath, FILMfile dest) throws IOException {
        
        if(dest.getHeader().getCompression() == 2) {
            File f = new File(adxFilePath);
            
            byte[] ADXBytes = Files.readAllBytes(f.toPath());
                            
            List<STABEntry> destStabs = dest.getHeader().getStab().getEntries();
            
            List<Integer> audioStabs = new ArrayList<>();
            
            int adxOffset = 0;
            List<byte[]> newChunks = new ArrayList<>();
            for(int i = 0; i < destStabs.size(); i++) {
                if(isAudioChunk(destStabs.get(i))) {
                    audioStabs.add(i);
                    newChunks.add(Arrays.copyOfRange(ADXBytes, adxOffset, adxOffset + destStabs.get(i).getLength()));
                    
                    adxOffset += destStabs.get(i).getLength();
                    
                } else {
                    newChunks.add(dest.getChunks().get(i));
                }
            }
            
            dest.setChunks(newChunks);
            
            return dest;
        } else {
            //file doesn't use ADX, abort.
            return dest;
        }
        
    }
    
public static FILMfile swapAudioFromWAVFile(String wavFilePath, FILMfile dest) throws IOException {
    if(dest.getHeader().getCompression() == 0 && dest.header.getAudioResolution() == 16 ) {
        File f = new File(wavFilePath);
        byte[] wavBytes = Files.readAllBytes(f.toPath());
        
        byte[] PCMBytes = Arrays.copyOfRange(wavBytes, 44, wavBytes.length);
        
        return swapPCMData(dest, PCMBytes, false, false);
        
    }else {
        //Can't use 8 bit WAV file, Saturn doesn't support unsigned 8 bit for Cinepak, must but be signed.
        //WAV can't hold 8-bit signed PCM.
        return dest;
    }
}
    
 public static FILMfile swapAudioFromPCMFile(String pcmFilePath, FILMfile dest, boolean satFormat, boolean bigEndian) throws IOException {
        
        if(dest.getHeader().getCompression() == 0) {
            File f = new File(pcmFilePath);
            
            byte[] PCMBytes = Files.readAllBytes(f.toPath());
            
            return swapPCMData(dest, PCMBytes, satFormat, bigEndian);
        } else {
            //file doesn't use PCM, abort.
            return dest;
        }
        
    }
 
 
private static FILMfile swapPCMData(FILMfile dest, byte[] PCMBytes, boolean satFormat, boolean bigEndian) throws IOException {
    List<STABEntry> destStabs = dest.getHeader().getStab().getEntries();
    
    List<Integer> audioStabs = new ArrayList<>();
    
    int pcmOffset = 0;
    List<byte[]> newChunks = new ArrayList<>();
    for(int i = 0; i < destStabs.size(); i++) {
        if(isAudioChunk(destStabs.get(i))) {
            audioStabs.add(i);
            
            byte[] rawAudio = Arrays.copyOfRange(PCMBytes, pcmOffset, pcmOffset + destStabs.get(i).getLength());
            
            if(!satFormat) {
                
                if(dest.header.getAudioResolution() == 16 && !bigEndian) {
                    rawAudio = swapByteOrder(rawAudio);
                }
                
                if(dest.header.getAudioChannels() == 2) {

                    ByteArrayOutputStream merge = new ByteArrayOutputStream();
                    ByteArrayOutputStream left = new ByteArrayOutputStream();
                    ByteArrayOutputStream right = new ByteArrayOutputStream();
                    
                    int length = rawAudio.length;
                    int half = length / 2;
                    
                    int iter = 0;
                    if(dest.header.getAudioResolution() == 16) {
                        while (iter < length) {
                            left.write(rawAudio[iter]);  
                            left.write(rawAudio[iter + 1]); 
                            
                            right.write(rawAudio[iter + 2]);  
                            right.write(rawAudio[iter + 3]);  
                            iter +=4;
                        }
                    } else {
                        while (iter < length) {
                            left.write(rawAudio[iter]);
                            right.write(rawAudio[iter + 1]); 
                            iter+=2;
                        }
                        
                    }
                    
                    merge.write(left.toByteArray());                                            
                    merge.write(right.toByteArray());
                    
                    rawAudio = merge.toByteArray();
                    
                }
            }
            
            newChunks.add(rawAudio);
            
            pcmOffset += destStabs.get(i).getLength();
            
        } else {
            newChunks.add(dest.getChunks().get(i));
        }
    }
    
    dest.setChunks(newChunks);
    
    return dest;
}
    
public static FILMfile swapAudio(FILMfile source, FILMfile dest) throws IOException {
        
        List<STABEntry> sourceStabs = source.getHeader().getStab().getEntries();
        
        List<STABEntry> destStabs = dest.getHeader().getStab().getEntries();
        
        List<Integer> audioStabs = new ArrayList<>();
        boolean isADX = false;
        if(source.getHeader().getCompression() == 2) {
            isADX = true;
        }
        
        List<byte[]> sourceAudioChunks = new ArrayList<>();
        
        List<byte[]> sourceVideoChunks = new ArrayList<>();
        
        for(int i = 0; i < sourceStabs.size(); i++) {
            if(isAudioChunk(sourceStabs.get(i))) {
                audioStabs.add(i);
                sourceAudioChunks.add(source.getChunks().get(i));
            } else {
                sourceVideoChunks.add(source.getChunks().get(i));
            }
        }
        System.out.println("AUDIO SOURCE: ");
        System.out.println("Found " + sourceAudioChunks.size() + " Audio Chunks");
        System.out.println("Found " + sourceVideoChunks.size() + " Video Chunks");
        int vidSize = 0;
        for(byte[] chunk : sourceVideoChunks) {
           vidSize += chunk.length; 
        }
        System.out.println("Video Data Size " + vidSize);
        
        List<byte[]> destAudioChunks = new ArrayList<>();
        
        List<byte[]> destVideoChunks = new ArrayList<>();
        for(int i = 0; i < destStabs.size(); i++) {
            if(isAudioChunk(destStabs.get(i))) {
                destAudioChunks.add(dest.getChunks().get(i));
            } else {
                destVideoChunks.add(dest.getChunks().get(i));
            }
        }
        System.out.println("VIDEO SOURCE: ");
        System.out.println("Found " + destAudioChunks.size() + " Audio Chunks");
        System.out.println("Found " + destVideoChunks.size() + " Video Chunks");
        vidSize = 0;
        for(byte[] chunk : destVideoChunks) {
            vidSize += chunk.length; 
        }
        System.out.println("Video Data Size " + vidSize);
        
        List<byte[]> newChunks = new ArrayList<>();
        List<STABEntry> newStabs = new ArrayList<>();
        
        for(int i = 0; i < destStabs.size(); i++) {
            if(audioStabs.contains(i)) {
                newChunks.add(source.getChunks().get(i));
                newStabs.add(sourceStabs.get(i));
            } else if(!isAudioChunk(destStabs.get(i))) {
               newChunks.add(dest.getChunks().get(i));
               newStabs.add(destStabs.get(i));
            }
        }
        
        int offset = 0;
        for(int i = 0; i < newStabs.size(); i++) {
            newStabs.get(i).setOffset(offset);
            offset += newStabs.get(i).getLength();
        }
        int stabSize;
        if(isADX) {
            stabSize = newStabs.size() * 0x10;
            
            dest.getHeader().getStab().setNumOfEntries(newStabs.size());
            dest.getHeader().getStab().setLength(stabSize);
            dest.getHeader().setHeaderSize(stabSize + 0x40);
            dest.getHeader().setCompression(source.getHeader().getCompression());
        } else {
            stabSize = (newStabs.size() * 0x10) + 0x10;
            dest.getHeader().getStab().setNumOfEntries(newStabs.size());
            dest.getHeader().getStab().setLength(stabSize);
            dest.getHeader().setHeaderSize(stabSize + 0x30);
        }
        
        dest.getHeader().getStab().setEntries(newStabs);
        dest.setChunks(newChunks);
        
        System.out.println("OUTPUT:");
        System.out.println("Source Chunks: " + source.getChunks().size());
        System.out.println("New Chunks: " + newChunks.size());
        
        return dest;
        
    }
    
    public static void reconstruct(FILMfile film, String outputFilePath) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FILMHeader header = film.getHeader();
        
        out.write(header.getFilmString().getBytes());
        
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(0, header.getHeaderSize());
        out.write(bb.array());
        
        out.write(header.getVersion().getBytes());
        out.write(new byte[4]);
        out.write(header.getFsdcString().getBytes());
        
        bb.putInt(0, header.getFsdcLength());
        out.write(bb.array());
        out.write(header.getFourCC().getBytes());
        
        bb.putInt(0, header.getHeight());
        out.write(bb.array());
        bb.putInt(0, header.getWidth());
        out.write(bb.array());
        
        out.write(header.getBpp());
        
        out.write(header.getAudioChannels());
        
        out.write(header.getAudioResolution());
        
        out.write(header.getCompression());
        
        bb = ByteBuffer.allocate(2);
        bb.putShort(0, header.getSampleRate());
        out.write(bb.array());
        
        out.write(new byte[6]);
        
        STABChunk stab = header.getStab();
        
        out.write(stab.getStabString().getBytes());
        
        bb = ByteBuffer.allocate(4);
        bb.putInt(0, stab.getLength());
        out.write(bb.array());
        
        bb.putInt(0, stab.getFramerateFrequency());
        out.write(bb.array());
        
        bb.putInt(0, stab.getNumOfEntries());
        out.write(bb.array());
        
        for(int i = 0; i < stab.getEntries().size(); i++) {
            STABEntry entry = stab.getEntries().get(i);
            
            bb.putInt(0, entry.getOffset());
            out.write(bb.array());
            
            bb.putInt(0, entry.getLength());
            out.write(bb.array());
            out.write(entry.getSampleInfo1());
            out.write(entry.getSampleInfo2());
        }
        
        for(int i = 0; i < film.getChunks().size(); i++) {
            out.write(film.getChunks().get(i));
        }
        Path path = Paths.get(outputFilePath);
        try {
            Files.write(path, out.toByteArray());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Caught IOException attempting to write bytes to file.", e);
            e.printStackTrace();
        }
    }
    
    private static boolean isAudioChunk(STABEntry entry) {
        if(bytesToHex(entry.getSampleInfo1()).equals("FFFFFFFF")) {
            return true;
        }
        
        return false;
    }
    
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    private static byte[] swapByteOrder(byte[] value) {
        final int length = value.length;
        byte[] res = new byte[length];
        int i = 0;
        while(i < length) {
            if(i+1 >= length) {
                res[i] = value[i];
            }else {
                res[i] = value[i+1];
                res[i+1] = value[i];
            }
            
            i += 2;
        }
        return res;
    }
    
    private static byte[] convertSignedToUnsigned(byte[] value) {
        final int length = value.length;
        byte[] res = new byte[length];
        int i = 0;
        while(i < length) {
        
            int val = value[i] & 0x000000FF;
            res[i] = (byte) val;
            i++;
        }
        
        return res;
    }
    
}
