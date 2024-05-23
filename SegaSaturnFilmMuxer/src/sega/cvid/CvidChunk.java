package sega.cvid;

import java.nio.ByteBuffer;

public class CvidChunk {

    short chunkType;
    short chunkSize;
    byte[] data;
    
    public byte[] toByteArray() {
        
        ByteBuffer bb = ByteBuffer.allocate(chunkSize);
        bb.putShort(chunkType);
        bb.putShort(chunkSize);
        bb.put(data);
        
        return bb.array();
    }
    
    /**
     * The getter for chunkType.
     *
     * @return the chunkType.
     */
    public short getChunkType() {
        return chunkType;
    }
    /**
     * The setter for chunkType.
     *
     * @param chunkType the chunkType to set.
     */
    public void setChunkType(short chunkType) {
        this.chunkType = chunkType;
    }
    /**
     * The getter for chunkSize.
     *
     * @return the chunkSize.
     */
    public short getChunkSize() {
        return chunkSize;
    }
    /**
     * The setter for chunkSize.
     *
     * @param chunkSize the chunkSize to set.
     */
    public void setChunkSize(short chunkSize) {
        this.chunkSize = chunkSize;
    }
    /**
     * The getter for data.
     *
     * @return the data.
     */
    public byte[] getData() {
        return data;
    }
    /**
     * The setter for data.
     *
     * @param data the data to set.
     */
    public void setData(byte[] data) {
        this.data = data;
    }
    
}
