package sega.cvid;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Packet;
import org.jcodec.api.UnsupportedFormatException;
import org.jcodec.common.AudioCodecMeta;
import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.Format;
import org.jcodec.common.JCodecUtil;
import org.jcodec.common.SeekableDemuxerTrack;
import org.jcodec.common.VideoCodecMeta;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.containers.mp4.MP4Util;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;
import sega.film.FILMUtility;
import sega.film.FILMfile;
import sega.film.STABChunk;
import sega.film.STABEntry;

import static org.jcodec.common.Format.MOV;

public class MovieToSaturn {
    
    private static SeekableDemuxerTrack videoTrack;
    private static DemuxerTrack audioTrack;
    private static FILMfile film;
    private static STABChunk stab;
    
    private static byte[] combinedAudioBuffer;
    
    private static int AUDIO_SAMPLE_INFO_1 = 0xFFFFFFFF;
    private static int AUDIO_SAMPLE_INFO_2 = 0X00000001;
    
    private static int audioBitRate = 0;
    private static int videoBitRate = 0;
    private static int totalBitrate = 0;
    private static int maxVideoBitratePerSecond = 0;
    
    private static int audioChunkIntroSize = 0;
    private static int audioChunkSize1 = 0;
    private static int audioChunkSize2 = 0;
    private static int totalAudioSize = 0;

    private static int filmOffset = 0;
    private static int audioOffset = 0;
    private static int stabTableEntries = 0;

    private static boolean hasAudio = false;
    private static boolean swapToBigEndian = false;
    private static boolean firstChunk = true;
    private static boolean size1 = true;
    private static int remainingAudioTime = 0;
    private static int remainingAudioBytes = 0;
    private static List<Packet> framePackets = new ArrayList<>();
    private static List<Packet> audioPackets = new ArrayList<>();
    
    private static String statsMessage = "";
    
    public static void movieToSaturn(File fileToConvert, boolean enableChromaKey, byte red, byte blue, byte green) throws IOException, UnsupportedFormatException {

        //Initialize Variables
        film = new FILMfile();
        stab = new STABChunk();
        hasAudio = false;
        swapToBigEndian = false;
        size1 = true;
        firstChunk = true;
        filmOffset = 0;
        audioOffset = 0;
        stabTableEntries = 0;
        framePackets = new ArrayList<>();
        audioPackets = new ArrayList<>();
        audioChunkIntroSize = 0;
        audioChunkSize1 = 0;
        audioChunkSize2 = 0;
        remainingAudioBytes= 0;
        remainingAudioTime = 0;
        totalAudioSize = 0;
        maxVideoBitratePerSecond = 0;
        
        //Set Chroma Key values
        if(enableChromaKey) {
            byte chromaEnable = (byte) 0x80;
            film.getHeader().setChromaKeyEnable(chromaEnable);
        }
        
        film.getHeader().setChromaKeyRed(red);
        film.getHeader().setChromaKeyGreen(green);
        film.getHeader().setChromaKeyBlue(blue);
        
        //Open file and parse out streams
        String filePath = fileToConvert.getPath();
        String fileName = fileToConvert.getName();
                
        SeekableByteChannel _in = NIOUtils.readableChannel(fileToConvert);
        
        ByteBuffer header = ByteBuffer.allocate(65536);
        _in.read(header);
        MP4Util.parseFullMovieChannel(_in);
        String audioCodec = null;
        String videoCodec = null;
        
        header.flip();
        Format detectFormat = JCodecUtil.detectFormatBuffer(header);
        if (detectFormat == null) {
            throw new UnsupportedFormatException("Could not detect the format of the input video.");
        }
        
        long videoDuration = 0;
        
        //If file is a MOV file, start parsing, otherwise throw an exception.
        if (MOV == detectFormat) {
            
            //Get out the video track data.
            MP4Demuxer d1 = MP4Demuxer.createMP4Demuxer(_in);
            videoTrack = (SeekableDemuxerTrack) d1.getVideoTrack();
            VideoCodecMeta videoMeta = videoTrack.getMeta().getVideoCodecMeta();
            videoCodec = d1.getMovie().getVideoTrack().getStsd().getBoxes().get(0).getFourcc();
            videoDuration = d1.getMovie().getVideoTrack().getDuration();
            
            //If not Cinepak throw exception.
            if(!videoCodec.toUpperCase().trim().equals("CVID")) {
                throw new UnsupportedFormatException("Only Cinepak is supported! (CVID)");
            }

            //Set video header info.
            film.getHeader().setHeight(videoMeta.getSize().getHeight());
            film.getHeader().setWidth(videoMeta.getSize().getWidth());
            film.getHeader().setAudioChannels((byte) 0);
            film.getHeader().setAudioResolution((byte) 0);
            film.getHeader().setSampleRate((short) 0);
            
            Packet p = videoTrack.nextFrame();
            stab.setFramerateFrequency(p.getTimescale());
            
            //Get each frame and calculate total video duration for bitrate calculations later.
            long totalVidDuration = 0;
            while(p != null) {

                totalVidDuration += p.getDuration();
                framePackets.add(p);
                p = videoTrack.nextFrame();
            }

            //If we have audio tracks, parse and process them.
            if(d1.getAudioTracks().size() > 0) {
                
                hasAudio = true;
                audioTrack = d1.getAudioTracks().get(0);
                
                AudioCodecMeta audioMeta = audioTrack.getMeta().getAudioCodecMeta();
                audioCodec = d1.getMovie().getAudioTracks().get(0).getStsd().getBoxes().get(0).getFourcc();
                
                //If audio isn't 8-bit or 16-bit PCM, throw an exception.
                if(!audioCodec.toUpperCase().trim().equals("TWOS") 
                        && !audioCodec.toUpperCase().trim().equals("SOWT")
                        && !audioCodec.toUpperCase().trim().equals("RAW")) {
                    throw new UnsupportedFormatException("Audio must be 16-bit PCM or 8-bit PCM (twos, sowt, or raw)");
                }
                
                //If little endian we need to swap the bits later.
                swapToBigEndian = false;
                if(audioCodec.toUpperCase().trim().equals("SOWT")) {
                    swapToBigEndian = true;
                }
                
                //Set Audio values.
                film.getHeader().setAudioChannels((byte) audioMeta.getChannelCount());
                int audioResolution = audioMeta.getSampleSize() * 8;
                film.getHeader().setAudioResolution((byte) audioResolution);
                Integer intSample = audioMeta.getSampleRate();
                film.getHeader().setSampleRate(intSample.shortValue());

                //Parse and create combined audio track data.
                createCombinedAudioBuffer(d1.getAudioTracks().get(0));
                
                /*
                 * Ok this is really jank looking but part of this comes from AVIToSaturn's old source code.
                 * It calculates audio chunk sizes that are approximately the same as what MovieToSaturn created.
                 * 
                 * The idea is you want the first chunk to be 1/2 a second of data, and the remaining chunks to be 1/4 a second.
                 * However they also need to be aligned to 8-byte boundaries.
                 */
                
                audioChunkIntroSize = ((((audioMeta.getChannelCount() * audioMeta.getSampleSize()) * (intSample / 2)) >>1) & ~3) * 2;
                
                audioChunkSize1 = ((((audioMeta.getChannelCount() * audioMeta.getSampleSize()) * (intSample / 4)) >> 1) & ~3) * 2;
                
                audioChunkSize2 = audioMeta.getSampleSize() * ((intSample/ ( 4 / audioMeta.getChannelCount()) - audioChunkSize1) + audioChunkSize1 );
                
                audioBitRate = ((audioMeta.getChannelCount() * audioMeta.getSampleSize()) * intSample) / 1024;
                
                //If not divisible by 8 we need to adjust it.
                
                int remainder = audioChunkSize2 % 8;
                if(remainder != 0) {
                    audioChunkSize2 += (8 - remainder);
                }
                                
                //Set remaining audio bytes to total size of audio.
                remainingAudioBytes = combinedAudioBuffer.length;
            }
            
            //Start building stab chunks
            STABEntry stabEntry = new STABEntry();
            
            ByteBuffer sampleInfo1 = ByteBuffer.allocate(4);
            ByteBuffer sampleInfo2 = ByteBuffer.allocate(4);
            
            //If we have audio, the first chunk needs to be the first audio chunk.
            if(hasAudio) {
                processAudioChunk();
            }
            
            int totalCvidDataSize = 0;
            int totalCvidDataSizePerSecond = 0;
            int durationCounter = 0;
            
            //Proccess each CVID Frame.
            for(int i = 0; i < framePackets.size(); i++) {
                
                stabEntry = new STABEntry();
                sampleInfo1 = ByteBuffer.allocate(4);
                sampleInfo2 = ByteBuffer.allocate(4);
                
                int pts = (int) framePackets.get(i).getPts();
                int duration = (int) framePackets.get(i).getDuration();
                
                //Need to set the flag if it's not a keyframe.
                if(!framePackets.get(i).isKeyFrame()) {
                    pts |= 1 << 31;
                }
                
                sampleInfo1.putInt(pts);
                sampleInfo2.putInt(duration);
                
                //Process the CVID frames to adjust for 8-byte boundary sizes.
                CvidHeader cvidData = CvidDataProcessor.parse(framePackets.get(i).data.array());
                
                //Start adding up bitrate.
                totalCvidDataSize += cvidData.actualSize;
                totalCvidDataSizePerSecond +=  cvidData.actualSize;
                durationCounter += duration;
                //If it's been 1 second calculate the bitrate for the last second and see if it's the biggest we've seen.
                if(durationCounter >= stab.getFramerateFrequency()) {
                    if(totalCvidDataSizePerSecond > maxVideoBitratePerSecond) {
                        maxVideoBitratePerSecond = totalCvidDataSizePerSecond;
                    }
                    totalCvidDataSizePerSecond = 0;
                    durationCounter = 0;
                }
                
                //Set stabe Entry values.
                stabEntry.setLength(cvidData.actualSize);
                stabEntry.setOffset(filmOffset);
                stabEntry.setSampleInfo1(sampleInfo1.array());
                stabEntry.setSampleInfo2(sampleInfo2.array());

                //Add Stab entry and cvid chunk to film file and increment.
                stab.addEntry(stabEntry);
                film.getChunks().add(cvidData.toByteArray());
                filmOffset += cvidData.actualSize;
                stabTableEntries++;
                //Calculate remaining audio time.
                remainingAudioTime -= duration;
                //This again comes from AviToSaturn and calculates the correct frame to interleave the next audio chunk.
                if (hasAudio && remainingAudioBytes > 0 && remainingAudioTime <= stab.getFramerateFrequency() * (3/8)) {
                    processAudioChunk();
                }
            }
            
            //If when we're all done we still have some audio process it.
            while(remainingAudioBytes > 0) {
                processAudioChunk();
            }
            
            //Calculate bitrates.
            videoBitRate = (int) (totalCvidDataSize / ( videoDuration/ 1000)) / 1024;
            
            totalBitrate = videoBitRate + audioBitRate;
            maxVideoBitratePerSecond = maxVideoBitratePerSecond / 1024;
            
            //Set status message.
            statsMessage = new String ("<html>Video Bitrate:             " + videoBitRate + "KB/s <br />" 
                                            + "Max Video Bitrate Bitrate: " + maxVideoBitratePerSecond + "KB/s <br />"
                                            + "Audio Bitrate:             " + audioBitRate + "KB/s <br />" 
                                            +"Total Average Bitrate:      " + totalBitrate + "KB/s </html>");
            
        
            //Finish up creating film file.
            
            stab.setNumOfEntries(stabTableEntries);
            stab.setLength((stabTableEntries + 1)* 16);
            film.getHeader().setHeaderSize(stab.getLength() + 48);
            film.getHeader().setStab(stab);
            
            String pieces[] =  fileName.split("\\.(?=[^\\.]+$)");
            
            //Write new file.
            
            String newName = pieces[0].concat(".CPK");
            String output = filePath.replace(fileName, newName);
            
            FILMUtility.reconstruct(film, output);
            
                            
        } else {
            throw new UnsupportedFormatException("Container format is not supported by JCodec");
        } 
        
    }
    
    public static String getStatsMessage() {
        return statsMessage;
    }
    
    /**
     * Combines the audio chunks into one large buffer. Also swaps endianness.
     * 
     * @param demux Track to demux
     * @throws IOException
     */
    private static void createCombinedAudioBuffer(DemuxerTrack demux) throws IOException {
        Packet ap = demux.nextFrame();
        while(ap != null) {
            audioPackets.add(ap);
            totalAudioSize += ap.getData().array().length;
            ap = demux.nextFrame();
        }
        ByteBuffer buffer = ByteBuffer.allocate(totalAudioSize);
                
        for(int i = 0; i < audioPackets.size(); i++) {
            buffer.put((audioPackets.get(i).getData().array()));
        }

        byte[] combinedAudio = buffer.array();

        int audioSampleSize = film.getHeader().getAudioResolution() / 8;
        
        //Swap to big Endian.
        if(swapToBigEndian) {
            if(audioSampleSize == 2) {

                byte[] sample = new byte[2];

                ByteBuffer audioBuffer = ByteBuffer.allocate(totalAudioSize);
                for(int i = 0; i < totalAudioSize; i += 2) {
                    sample[1] = combinedAudio[i];
                    sample[0] = combinedAudio[i + 1];
                    
                    audioBuffer.put(sample);
                }
                combinedAudioBuffer = audioBuffer.array();
            }
        } else {
            combinedAudioBuffer = combinedAudio;
        }
         
        
    }
    
    /**
     * Processes gets and processes the next audio chunk. Then adds it to the Film File.
     * 
     * Normal PCM Data has the left and right samples interleaved. 
     * Sega FILM has all left samples first then all the right samples next. We need to convert to this format.
     * 
     * Finally 8-bit PCM is usually unsigned, Sega FILM expects it to be signed. So this will convert it.
     * 
     */
    private static void processAudioChunk() {
        int audioChunkSize = 0;
        
        //Determin which size to use.
        if(firstChunk) {
            audioChunkSize = audioChunkIntroSize;
        } else {
            if(size1) {
                audioChunkSize = audioChunkSize1;
                size1 = !size1;
            } else {
                audioChunkSize = audioChunkSize2;
                size1 = !size1;
            }
        }
        
        //More jank from AviToSaturn to deal with the last chunk not aligning to an 8-byte boundary.
        if(audioChunkSize > remainingAudioBytes) {
            audioChunkSize = remainingAudioBytes;
            int remainder = audioChunkSize % 8;
            if(remainder != 0) {
                
                audioChunkSize = (remainingAudioBytes + (8 - remainder)) & ~3;
            }
           
        }
    
        byte[] audioChunk = Arrays.copyOfRange(combinedAudioBuffer, audioOffset, audioOffset + audioChunkSize);
        int audioSampleSize = film.getHeader().getAudioResolution() / 8;
        int leftSize = 0;
        int rightSize = 0;
        
      //Need to deinterleave
        if(film.getHeader().getAudioChannels() == 2) {
        
            ByteBuffer leftAudio;
            ByteBuffer rightAudio;
            if(audioSampleSize == 2) {
                leftAudio = ByteBuffer.allocate((audioChunkSize / 2));
                rightAudio = ByteBuffer.allocate((audioChunkSize / 2));

                
                for(int i = 0; i < audioChunk.length; i += 4) {

                    leftAudio.put(leftSize, audioChunk[i]);
                    leftSize++;
                    leftAudio.put(leftSize, audioChunk[i + 1]);
                    leftSize++;
                    
                    rightAudio.put(rightSize, audioChunk[i + 2]);
                    rightSize++;
                    rightAudio.put(rightSize, audioChunk[i + 3]);
                    rightSize++;
                 
                }
                
                byte[] leftAudioArray = Arrays.copyOfRange(leftAudio.array(), 0, leftSize);
                byte[] rightAudioArray =  Arrays.copyOfRange(rightAudio.array(), 0, rightSize);
                
                leftAudio = ByteBuffer.wrap(leftAudioArray);
                rightAudio = ByteBuffer.wrap(rightAudioArray);
                
                
            } else {
                leftAudio = ByteBuffer.allocate((audioChunkSize / 2) + 1);
                rightAudio = ByteBuffer.allocate((audioChunkSize / 2) + 1);

                byte[] leftSample = new byte[1];
                byte[] rightSample = new byte[1];
                
                //Convert to signed and interleave
                for(int i = 0; i < audioChunkSize; i++) {
                    leftSample[0] = audioChunk[i];
                    byte lb = leftSample[0];
                    int lbVal = lb & 0xFF;
                    lbVal -= 128;
                    Integer lbInt = new Integer(lbVal);
                    leftAudio.put(lbInt.byteValue());
                    leftSize++;
                    
                    if(i + 1 < audioChunkSize) {
                        i++;
                        rightSample[0] = audioChunk[i];
                        byte rb = rightSample[0];
                        int rbVal = rb & 0xFF;
                        rbVal -= 128;
                        Integer rbInt = new Integer(rbVal);
                        rightAudio.put(rbInt.byteValue());
                        rightSize++;
                    }
                }
                byte[] leftAudioArray = Arrays.copyOfRange(leftAudio.array(), 0, leftSize);
                byte[] rightAudioArray =  Arrays.copyOfRange(rightAudio.array(), 0, rightSize);
                
                leftAudio = ByteBuffer.wrap(leftAudioArray);
                rightAudio = ByteBuffer.wrap(rightAudioArray);
                
            }
            ByteBuffer buffer = ByteBuffer.allocate(audioChunkSize );
            
            buffer.put(leftAudio.array());
            buffer.put(rightAudio.array());

            audioChunk = buffer.array();
        } else if (audioSampleSize == 1) {
            //Need to convert 8-bit to signed
            ByteBuffer buffer = ByteBuffer.allocate(audioChunkSize );

            byte[] sample = new byte[1];
            
            for(int i = 0; i < audioChunkSize; i ++) {
                sample[0] = audioChunk[i];
                byte sb = sample[0];
                int sbVal = sb & 0xFF;
                sbVal -= 128;
                Integer sbInt = new Integer(sbVal);
                buffer.put(sbInt.byteValue());
                
            }
            audioChunk = buffer.array();
        }
                
        //Increment Audio offset.
        audioOffset += audioChunk.length;
        
        //Create stab entries.
        ByteBuffer sampleInfo1 = ByteBuffer.allocate(4);
        ByteBuffer sampleInfo2 = ByteBuffer.allocate(4);
        sampleInfo1.putInt(AUDIO_SAMPLE_INFO_1);
        sampleInfo2.putInt(AUDIO_SAMPLE_INFO_2);
        
        STABEntry stabEntry = new STABEntry();
        
        stabEntry.setLength(audioChunk.length);
        stabEntry.setOffset(filmOffset);
        stabEntry.setSampleInfo1(sampleInfo1.array());
        stabEntry.setSampleInfo2(sampleInfo2.array());
        filmOffset += audioChunk.length;
        
        //Add audio chunk and entries to film file.
        film.getChunks().add(audioChunk);
        stab.addEntry(stabEntry);
        stabTableEntries++;
        
        //More jank from AviToSaturn to increment remainingAudioTime to properly calculate the next time we need to do an audio frame.
        if(firstChunk) {
            remainingAudioTime = (stab.getFramerateFrequency() / 2) - (3 * (stab.getFramerateFrequency() / 8));
            firstChunk = false;
        }else {
            remainingAudioTime += stab.getFramerateFrequency() / 4;
        }
        remainingAudioBytes -= audioChunk.length;
    }

}
