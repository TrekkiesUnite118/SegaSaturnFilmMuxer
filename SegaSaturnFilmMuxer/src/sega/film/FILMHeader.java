package sega.film;

public class FILMHeader {
    
    private String filmString = "FILM";
    private int headerSize;
    private String version = "1.09";
    private String fsdcString = "FDSC";
    private int fsdcLength = 32;
    private String fourCC = "cvid";
    private int height;
    private int width;
    private byte bpp = 24;
    private byte audioChannels;
    private byte audioResolution;
    private byte compression;
    private short sampleRate;
    private short unknown;
    private byte chromaKeyEnable;
    private byte chromaKeyBlue;
    private byte chromaKeyGreen;
    private byte chromaKeyRed;
    private STABChunk stab = new STABChunk();
    
    /**
     * The getter for filmString.
     *
     * @return the filmString.
     */
    public String getFilmString() {
        return filmString;
    }
    /**
     * The setter for filmString.
     *
     * @param filmString the filmString to set.
     */
    public void setFilmString(String filmString) {
        this.filmString = filmString;
    }
    /**
     * The getter for version.
     *
     * @return the version.
     */
    public String getVersion() {
        return version;
    }
    /**
     * The setter for version.
     *
     * @param version the version to set.
     */
    public void setVersion(String version) {
        this.version = version;
    }
    /**
     * The getter for fsdcString.
     *
     * @return the fsdcString.
     */
    public String getFsdcString() {
        return fsdcString;
    }
    /**
     * The setter for fsdcString.
     *
     * @param fsdcString the fsdcString to set.
     */
    public void setFsdcString(String fsdcString) {
        this.fsdcString = fsdcString;
    }
    /**
     * The getter for headerSize.
     *
     * @return the headerSize.
     */
    public int getHeaderSize() {
        return headerSize;
    }
    /**
     * The setter for headerSize.
     *
     * @param headerSize the headerSize to set.
     */
    public void setHeaderSize(int headerSize) {
        this.headerSize = headerSize;
    }
    /**
     * The getter for fsdcLength.
     *
     * @return the fsdcLength.
     */
    public int getFsdcLength() {
        return fsdcLength;
    }
    /**
     * The setter for fsdcLength.
     *
     * @param fsdcLength the fsdcLength to set.
     */
    public void setFsdcLength(int fsdcLength) {
        this.fsdcLength = fsdcLength;
    }
    /**
     * The getter for fourCC.
     *
     * @return the fourCC.
     */
    public String getFourCC() {
        return fourCC;
    }
    /**
     * The setter for fourCC.
     *
     * @param fourCC the fourCC to set.
     */
    public void setFourCC(String fourCC) {
        this.fourCC = fourCC;
    }
    /**
     * The getter for height.
     *
     * @return the height.
     */
    public int getHeight() {
        return height;
    }
    /**
     * The setter for height.
     *
     * @param height the height to set.
     */
    public void setHeight(int height) {
        this.height = height;
    }
    /**
     * The getter for width.
     *
     * @return the width.
     */
    public int getWidth() {
        return width;
    }
    /**
     * The setter for width.
     *
     * @param width the width to set.
     */
    public void setWidth(int width) {
        this.width = width;
    }
    /**
     * The getter for bpp.
     *
     * @return the bpp.
     */
    public byte getBpp() {
        return bpp;
    }
    /**
     * The setter for bpp.
     *
     * @param bpp the bpp to set.
     */
    public void setBpp(byte bpp) {
        this.bpp = bpp;
    }
    /**
     * The getter for audioChannels.
     *
     * @return the audioChannels.
     */
    public byte getAudioChannels() {
        return audioChannels;
    }
    /**
     * The setter for audioChannels.
     *
     * @param audioChannels the audioChannels to set.
     */
    public void setAudioChannels(byte audioChannels) {
        this.audioChannels = audioChannels;
    }
    /**
     * The getter for audioResolution.
     *
     * @return the audioResolution.
     */
    public byte getAudioResolution() {
        return audioResolution;
    }
    /**
     * The setter for audioResolution.
     *
     * @param audioResolution the audioResolution to set.
     */
    public void setAudioResolution(byte audioResolution) {
        this.audioResolution = audioResolution;
    }
    /**
     * The getter for compression.
     *
     * @return the compression.
     */
    public byte getCompression() {
        return compression;
    }
    /**
     * The setter for compression.
     *
     * @param compression the compression to set.
     */
    public void setCompression(byte compression) {
        this.compression = compression;
    }
    /**
     * The getter for sampleRate.
     *
     * @return the sampleRate.
     */
    public short getSampleRate() {
        return sampleRate;
    }
    /**
     * The setter for sampleRate.
     *
     * @param sampleRate the sampleRate to set.
     */
    public void setSampleRate(short sampleRate) {
        this.sampleRate = sampleRate;
    }
    /**
     * The getter for unknown.
     *
     * @return the unknown.
     */
    public short getUnknown() {
        return unknown;
    }
    /**
     * The setter for unknown.
     *
     * @param unknown the unknown to set.
     */
    public void setUnknown(short unknown) {
        this.unknown = unknown;
    }
    /**
     * The getter for chromaKeyEnable.
     *
     * @return the chromaKeyEnable.
     */
    public byte getChromaKeyEnable() {
        return chromaKeyEnable;
    }
    /**
     * The setter for chromaKeyEnable.
     *
     * @param chromaKeyEnable the chromaKeyEnable to set.
     */
    public void setChromaKeyEnable(byte chromaKeyEnable) {
        this.chromaKeyEnable = chromaKeyEnable;
    }
    /**
     * The getter for chromaKeyBlue.
     *
     * @return the chromaKeyBlue.
     */
    public byte getChromaKeyBlue() {
        return chromaKeyBlue;
    }
    /**
     * The setter for chromaKeyBlue.
     *
     * @param chromaKeyBlue the chromaKeyBlue to set.
     */
    public void setChromaKeyBlue(byte chromaKeyBlue) {
        this.chromaKeyBlue = chromaKeyBlue;
    }
    /**
     * The getter for chromaKeyGreen.
     *
     * @return the chromaKeyGreen.
     */
    public byte getChromaKeyGreen() {
        return chromaKeyGreen;
    }
    /**
     * The setter for chromaKeyGreen.
     *
     * @param chromaKeyGreen the chromaKeyGreen to set.
     */
    public void setChromaKeyGreen(byte chromaKeyGreen) {
        this.chromaKeyGreen = chromaKeyGreen;
    }
    /**
     * The getter for chromaKeyRed.
     *
     * @return the chromaKeyRed.
     */
    public byte getChromaKeyRed() {
        return chromaKeyRed;
    }
    /**
     * The setter for chromaKeyRed.
     *
     * @param chromaKeyRed the chromaKeyRed to set.
     */
    public void setChromaKeyRed(byte chromaKeyRed) {
        this.chromaKeyRed = chromaKeyRed;
    }
    /**
     * The getter for stab.
     *
     * @return the stab.
     */
    public STABChunk getStab() {
        return stab;
    }
    /**
     * The setter for stab.
     *
     * @param stab the stab to set.
     */
    public void setStab(STABChunk stab) {
        this.stab = stab;
    }
    
    public void printHeader() {
        System.out.println(filmString);
        System.out.println(headerSize);
        System.out.println(version);
        System.out.println(fsdcString);
        System.out.println(fsdcLength);
        System.out.println(fourCC);
        System.out.println(height);
        System.out.println(width);
        System.out.println(bpp);
        System.out.println(audioChannels);
        System.out.println(audioResolution);
        System.out.println(compression);
        System.out.println(sampleRate & 0xffff);
    }


}
