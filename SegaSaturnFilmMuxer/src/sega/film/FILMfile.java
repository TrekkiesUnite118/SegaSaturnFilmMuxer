package sega.film;

import java.util.ArrayList;
import java.util.List;

public class FILMfile {
    
    FILMHeader header = new FILMHeader();
    STABChunk stab = new STABChunk();
    List<byte[]> chunks = new ArrayList<>();
    /**
     * The getter for header.
     *
     * @return the header.
     */
    public FILMHeader getHeader() {
        return header;
    }
    /**
     * The setter for header.
     *
     * @param header the header to set.
     */
    public void setHeader(FILMHeader header) {
        this.header = header;
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
    /**
     * The getter for chunks.
     *
     * @return the chunks.
     */
    public List<byte[]> getChunks() {
        return chunks;
    }
    /**
     * The setter for chunks.
     *
     * @param chunks the chunks to set.
     */
    public void setChunks(List<byte[]> chunks) {
        this.chunks = chunks;
    }
    
    

}
