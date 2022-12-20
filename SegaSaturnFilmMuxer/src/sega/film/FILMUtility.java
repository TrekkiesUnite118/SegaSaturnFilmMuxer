package sega.film;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
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
}
