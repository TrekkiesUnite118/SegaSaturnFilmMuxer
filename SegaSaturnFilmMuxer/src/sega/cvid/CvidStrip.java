package sega.cvid;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CvidStrip {

    byte flags;
    int size;
    int padding;
    short height;
    short width;
    List<CvidChunk> chunks = new ArrayList<>();
    
    
 public byte[] toByteArray() {
        
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.put(flags);
        
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        sizeBuffer.putInt(size);
        byte[] tempSize = sizeBuffer.array();
        
        byte[] sizeArray = Arrays.copyOfRange(tempSize, 1, 4);
        bb.put(sizeArray);
        bb.putInt(padding);
        bb.putShort(height);
        bb.putShort(width);
        
        for(int i = 0; i < chunks.size(); i++) {
            bb.put(chunks.get(i).toByteArray());
        }
        
        return bb.array();
    }
    
    
    /**
     * The getter for flags.
     *
     * @return the flags.
     */
    public byte getFlags() {
        return flags;
    }
    /**
     * The setter for flags.
     *
     * @param flags the flags to set.
     */
    public void setFlags(byte flags) {
        this.flags = flags;
    }
    /**
     * The getter for size.
     *
     * @return the size.
     */
    public int getSize() {
        return size;
    }
    /**
     * The setter for size.
     *
     * @param size the size to set.
     */
    public void setSize(int size) {
        this.size = size;
    }
    /**
     * The getter for padding.
     *
     * @return the padding.
     */
    public int getPadding() {
        return padding;
    }
    /**
     * The setter for padding.
     *
     * @param padding the padding to set.
     */
    public void setPadding(int padding) {
        this.padding = padding;
    }
    /**
     * The getter for height.
     *
     * @return the height.
     */
    public short getHeight() {
        return height;
    }
    /**
     * The setter for height.
     *
     * @param height the height to set.
     */
    public void setHeight(short height) {
        this.height = height;
    }
    /**
     * The getter for width.
     *
     * @return the width.
     */
    public short getWidth() {
        return width;
    }
    /**
     * The setter for width.
     *
     * @param width the width to set.
     */
    public void setWidth(short width) {
        this.width = width;
    }
    /**
     * The getter for chunks.
     *
     * @return the chunks.
     */
    public List<CvidChunk> getChunks() {
        return chunks;
    }
    /**
     * The setter for chunks.
     *
     * @param chunks the chunks to set.
     */
    public void setChunks(List<CvidChunk> chunks) {
        this.chunks = chunks;
    }
}
