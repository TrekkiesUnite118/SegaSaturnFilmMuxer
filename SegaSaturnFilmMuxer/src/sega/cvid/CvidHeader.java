package sega.cvid;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CvidHeader {

    byte flag;
    int size;
    short width;
    short height;
    short numOfStrips;
    List<CvidStrip> strips = new ArrayList<>();
    int actualSize;
    
    public byte[] toByteArray() {
        
        ByteBuffer bb = ByteBuffer.allocate(actualSize);
        bb.put(flag);
        
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        sizeBuffer.putInt(size);
        byte[] tempSize = sizeBuffer.array();
        
        byte[] sizeArray = Arrays.copyOfRange(tempSize, 1, 4);
        bb.put(sizeArray);
        bb.putShort(width);
        bb.putShort(height);
        bb.putShort(numOfStrips);
        byte[] padding = new byte[2];
        bb.put(padding);
        
        for(int i = 0; i < strips.size(); i++) {
            bb.put(strips.get(i).toByteArray());
        }
        
        return bb.array();
    }
    
    /**
     * The getter for flag.
     *
     * @return the flag.
     */
    public byte getFlag() {
        return flag;
    }
    /**
     * The setter for flag.
     *
     * @param flag the flag to set.
     */
    public void setFlag(byte flag) {
        this.flag = flag;
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
     * The getter for numOfStrips.
     *
     * @return the numOfStrips.
     */
    public short getNumOfStrips() {
        return numOfStrips;
    }
    /**
     * The setter for numOfStrips.
     *
     * @param numOfStrips the numOfStrips to set.
     */
    public void setNumOfStrips(short numOfStrips) {
        this.numOfStrips = numOfStrips;
    }
    /**
     * The getter for strips.
     *
     * @return the strips.
     */
    public List<CvidStrip> getStrips() {
        return strips;
    }
    /**
     * The setter for strips.
     *
     * @param strips the strips to set.
     */
    public void setStrips(List<CvidStrip> strips) {
        this.strips = strips;
    }
    /**
     * The getter for actualSize.
     *
     * @return the actualSize.
     */
    public int getActualSize() {
        return actualSize;
    }
    /**
     * The setter for actualSize.
     *
     * @param actualSize the actualSize to set.
     */
    public void setActualSize(int actualSize) {
        this.actualSize = actualSize;
    }
    
    
    
}
