package sega.cvid;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class CvidDataProcessor {
    
    /**
     * Parses and processes a chunk of CVID Data to make the necessary adjustments to align everything
     * to 8-byte boundaries.
     * 
     * @param data
     * @return
     */
    public static CvidHeader parse(byte[] data) {
        CvidHeader cvidHeader = new CvidHeader();
        int offset = 0;
        ByteBuffer bb = ByteBuffer.wrap(data, offset, 1);
        bb.order(ByteOrder.BIG_ENDIAN);
        cvidHeader.setFlag(bb.get());
        offset++;
        bb = ByteBuffer.wrap(data, offset, 3);
        byte[] tempArray = bb.array();
        
        byte[] sizeArray = new byte[4];
        sizeArray[1] = tempArray[0];
        sizeArray[2] = tempArray[1];
        sizeArray[3] = tempArray[2];
        
        bb = ByteBuffer.wrap(sizeArray, 0, 4);
        cvidHeader.setSize(bb.getInt());
        offset+=3;
        bb = ByteBuffer.wrap(data, offset, 2);
        cvidHeader.setWidth(bb.getShort());
        offset+=2;
        bb = ByteBuffer.wrap(data, offset, 2);
        cvidHeader.setHeight(bb.getShort());
        offset+=2;
        bb = ByteBuffer.wrap(data, offset, 2);
        cvidHeader.setNumOfStrips(bb.getShort());
        offset+=2;
        
        for(int i = 0; i < cvidHeader.getNumOfStrips(); i++) {
            CvidStrip strip = new CvidStrip();
            bb = ByteBuffer.wrap(data, offset, 1);
            strip.setFlags(bb.get());
            offset++;
            
            tempArray = Arrays.copyOfRange(data, offset, offset+3);
            sizeArray = new byte[4];
            sizeArray[1] = tempArray[0];
            sizeArray[2] = tempArray[1];
            sizeArray[3] = tempArray[2];
                        
            ByteBuffer sizeBuffer = ByteBuffer.wrap(sizeArray);
            int testSize = sizeBuffer.getInt();
            
            strip.setSize(testSize);
            offset+=3;
            bb = ByteBuffer.wrap(data, offset, 4);
            strip.setPadding(bb.getInt());
            offset+=4;
            bb = ByteBuffer.wrap(data, offset, 2);
            strip.setHeight(bb.getShort());
            offset+=2;
            bb = ByteBuffer.wrap(data, offset, 2);
            strip.setWidth(bb.getShort());
            offset+=2;            
            
            int bytesRemaining = strip.getSize() - 22;
            while(bytesRemaining > 0) {

                CvidChunk chunk = new CvidChunk();
                bb = ByteBuffer.wrap(data, offset, 2);
                chunk.setChunkType(bb.getShort());
                offset+=2;            
                bb = ByteBuffer.wrap(data, offset, 2);
                                
                chunk.setChunkSize(bb.getShort());
                offset+=2;
                
                int chunkSize = Short.toUnsignedInt(chunk.getChunkSize());
                
                
                byte[] chunkArray = Arrays.copyOfRange(data, offset, offset + chunkSize - 4);

                offset += chunkSize - 4;
                
                
                chunk.setData(chunkArray);
                
                int remainder = (chunkSize % 4);
                if(remainder != 0) {
                    chunkSize += remainder;
                    chunk.setChunkSize((short) chunkSize);
                }
                strip.getChunks().add(chunk);
                bytesRemaining -= chunkSize;
            }
            
            int newStripSize = 12;
            for(CvidChunk c : strip.getChunks()) {
                newStripSize += Short.toUnsignedInt(c.getChunkSize());
            }
            strip.setSize(newStripSize);
            cvidHeader.getStrips().add(strip);
        }
        
        int newDataSize = 12;
        for(CvidStrip s : cvidHeader.getStrips()) {
            newDataSize += s.getSize();
        }
        cvidHeader.setSize(newDataSize - 8);
        cvidHeader.setActualSize(newDataSize);
        return cvidHeader;
    }
    
      
}
