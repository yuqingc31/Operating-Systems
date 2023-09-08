import java.util.LinkedList;

public class FIFO implements MMU{
    private int numFrames;
    private boolean debugMode;
    private int totalDiskWrites;
    private int totalPageFaults;
    private LinkedList<Integer> frameQueue;
    private LinkedList<Integer> dirtyPages;

    public FIFO(int frames){
        numFrames = frames;
        debugMode = false;
        totalDiskWrites = 0;
        totalPageFaults = 0;
        frameQueue = new LinkedList<>();
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

    public void readMemory(int page_number){

        if (frameQueue.contains(page_number)){
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
        }

    }

    public void writeMemory(int page_number){
        
        if(frameQueue.contains(page_number)){
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

    private void handlePageFault(int page_number){
        totalPageFaults++;

        if (debugMode) {
            System.out.println("Page fault        " + page_number);
        }

        // if the queue is full:
        if (frameQueue.size() >= numFrames){
            // if the page that is preparing to add into the frame queue is in the dirty page queue, disk write +1
            if (dirtyPages.contains(frameQueue.getLast())){
                if (debugMode){
                    System.out.println("Disk write        " + frameQueue.getLast());
                }
                totalDiskWrites++;
                // remove it from the dirty page queue
                dirtyPages.remove(frameQueue.getLast());
            }else{
                // if it is not a dirty page, discard
                if(debugMode){
                    System.out.println("Discard           " + frameQueue.getLast());
                }
            }

            // remove the last page in frame queue
            int removedPage = frameQueue.removeLast();
        }

        // after removing the page, add it to the frame queue
        // or if the frame queue is not full, add it to the frame queue also
        frameQueue.addFirst(page_number);
    }
}