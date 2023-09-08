import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
* MMU using random selection replacement strategy
*/

public class RandMMU implements MMU {
    private boolean debugMode;
    private int totalDiskWrites;
    private int totalPageFaults;
    private Random random; // Random number generator
    private int numFrames;
    private int totalDiskReads;
    private LinkedList<Integer> frameQueue;
    private LinkedList<Integer> dirtyPages;

    public RandMMU(int frames) {
        //todo
        numFrames = frames;
        debugMode = false;
        totalDiskWrites = 0;
        totalPageFaults = 0;
        frameQueue = new LinkedList<>();
        random = new Random();
        dirtyPages = new LinkedList<>();
    }
    
    public void setDebug() {
        //todo
        debugMode = true;
    }
    
    public void resetDebug() {
        //todo
        debugMode = false;
    }
    
    public void readMemory(int page_number) {
        //todo
        if (frameQueue.contains(page_number)) {
            // if the current page is in the frame queue, do nothing
            if (debugMode){
                System.out.println("reading           " + page_number);
            }
        } else {
            // if the current page is not in the frame queue, handle page fault
            handlePageFault(page_number);
            if (debugMode){
                System.out.println("reading           " + page_number);
            }
            totalDiskReads++;
            
        }
    }
    
    public void writeMemory(int page_number) {
        //todo
        if (frameQueue.contains(page_number)) {
            // if the current page is in the frame queue, do nothing
            if (debugMode){
                System.out.println("writing           " + page_number);
            }
        } else {
            // if the current page is not in the frame queue, handle page fault
            handlePageFault(page_number);
            if (debugMode){
                System.out.println("writing           " + page_number);
            }
        }
        // if the current page is not in the dirty page queue, add it into the dirty page queue
        if(!dirtyPages.contains(page_number)){
            dirtyPages.addFirst(page_number);
        }
    }
    
    public int getTotalDiskReads() {
        //todo
        return totalPageFaults;
    }
    
    public int getTotalDiskWrites() {
        //todo
        return totalDiskWrites;
    }
    
    public int getTotalPageFaults() {
        return totalPageFaults;
    }

    private void handlePageFault(int page_number) {
        totalPageFaults++;

        if (debugMode) {
            System.out.println("Page fault        " + page_number);
        }

        // if the queue is full
        if (frameQueue.size() >= numFrames) {
            // get a random index
            int randomIndex = random.nextInt(frameQueue.size());

            // if the page that is preparing to add into the framequeue is in the dirty page queue, disk write +1
            if (dirtyPages.contains(frameQueue.get(randomIndex))){
                if (debugMode){
                    System.out.println("Disk write        " + frameQueue.get(randomIndex));
                }
                totalDiskWrites++;
                // remove it from the dirty page queue
                dirtyPages.remove(frameQueue.get(randomIndex));
            }else{
                if(debugMode){
                    System.out.println("Discard           " + frameQueue.get(randomIndex));
                }
            }

            // randomly remove a page from frame queue
            System.out.println("Random remove page:    " + frameQueue.get(randomIndex));
            frameQueue.remove(randomIndex);
        }

        // after removing the page, add it to the frame queue
        // or if the frame queue is not full, add it to the frame queue
        frameQueue.add(page_number);
    }
}