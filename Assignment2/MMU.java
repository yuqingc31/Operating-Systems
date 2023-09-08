/**
* Interface for Memory Management Unit.
* The memory management unit should maintain the concept of a page table.
* As pages are read and written to, this changes the pages loaded into the
* the limited number of frames. The MMU keeps records, which will be used
* to analyse the performance of different replacement stratergies implemented
* for the MMU.
*/

public interface MMU {
    public void readMemory(int page_number);
    public void writeMemory(int page_number);
    
    public void setDebug();
    public void resetDebug();
    
    public int getTotalDiskReads();
    public int getTotalDiskWrites();
    public int getTotalPageFaults();
}