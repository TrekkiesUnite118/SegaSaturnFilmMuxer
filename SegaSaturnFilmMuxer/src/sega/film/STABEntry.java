package sega.film;

public class STABEntry {

    
    private int offset;
    private int length;
    private byte[] sampleInfo1 = new byte[4];
    private byte[] sampleInfo2 = new byte[4];
    
    /**
     * The getter for offset.
     *
     * @return the offset.
     */
    public int getOffset() {
        return offset;
    }
    /**
     * The setter for offset.
     *
     * @param offset the offset to set.
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }
    /**
     * The getter for length.
     *
     * @return the length.
     */
    public int getLength() {
        return length;
    }
    /**
     * The setter for length.
     *
     * @param length the length to set.
     */
    public void setLength(int length) {
        this.length = length;
    }
    /**
     * The getter for sampleInfo1.
     *
     * @return the sampleInfo1.
     */
    public byte[] getSampleInfo1() {
        return sampleInfo1;
    }
    /**
     * The setter for sampleInfo1.
     *
     * @param sampleInfo1 the sampleInfo1 to set.
     */
    public void setSampleInfo1(byte[] sampleInfo1) {
        this.sampleInfo1 = sampleInfo1;
    }
    /**
     * The getter for sampleInfo2.
     *
     * @return the sampleInfo2.
     */
    public byte[] getSampleInfo2() {
        return sampleInfo2;
    }
    /**
     * The setter for sampleInfo2.
     *
     * @param sampleInfo2 the sampleInfo2 to set.
     */
    public void setSampleInfo2(byte[] sampleInfo2) {
        this.sampleInfo2 = sampleInfo2;
    }
}
