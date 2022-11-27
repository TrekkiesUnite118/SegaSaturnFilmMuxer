package sega.film;

import java.util.ArrayList;
import java.util.List;

public class STABChunk {
    
    private String stabString = "STAB";
    private int length;
    private int framerateFrequency;
    private int numOfEntries;
    List<STABEntry> entries = new ArrayList<>();
    
    
    /**
     * The getter for stabString.
     *
     * @return the stabString.
     */
    public String getStabString() {
        return stabString;
    }
    /**
     * The setter for stabString.
     *
     * @param stabString the stabString to set.
     */
    public void setStabString(String stabString) {
        this.stabString = stabString;
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
     * The getter for framerateFrequency.
     *
     * @return the framerateFrequency.
     */
    public int getFramerateFrequency() {
        return framerateFrequency;
    }
    /**
     * The setter for framerateFrequency.
     *
     * @param framerateFrequency the framerateFrequency to set.
     */
    public void setFramerateFrequency(int framerateFrequency) {
        this.framerateFrequency = framerateFrequency;
    }
    /**
     * The getter for numOfEntries.
     *
     * @return the numOfEntries.
     */
    public int getNumOfEntries() {
        return numOfEntries;
    }
    /**
     * The setter for numOfEntries.
     *
     * @param numOfEntries the numOfEntries to set.
     */
    public void setNumOfEntries(int numOfEntries) {
        this.numOfEntries = numOfEntries;
    }
    /**
     * The getter for entries.
     *
     * @return the entries.
     */
    public List<STABEntry> getEntries() {
        return entries;
    }
    /**
     * The setter for entries.
     *
     * @param entries the entries to set.
     */
    public void setEntries(List<STABEntry> entries) {
        this.entries = entries;
    }
    
    

}
