/**
* MMU using enchanced second chance replacement strategy
*/

public class EscMMU implements MMU {
    class Page {
        private int pageAddr;
        private boolean useBit;
        private boolean dirtyBit;
        private Page next;
        private Page prev;

        public Page(int pageAddr) {
            this.pageAddr = pageAddr;
            this.useBit = true; // page is use when it's created
        }

    }

    class PageList{
        private int size;
        private Page pointer;

        public PageList() {
            this.size = 0;
            this.pointer = null;
        }

        // print out all the pages in the list
        public void printPages(){
            if (pointer == null){
                System.out.println("empty list");
            } else {
                Page curr = pointer;
                System.out.println("printing from pointer:");
                System.out.println("address\tuseBit\tdirtyBit");
                do {
                    System.out.println(curr.pageAddr + "\t" + curr.useBit + "\t" + curr.dirtyBit);
                    curr = curr.next;   
                } while (curr != pointer);
            }
        }

        // add new page to the list
        public void addPage(int address){
            Page newPage = new Page(address);
            if (pointer == null){ // list is empty
                pointer = newPage;
                pointer.next = pointer; // pointing to itself
                pointer.prev = pointer; // pointing to itself
            } else { // add node before to the current pointer
                Page tmp = pointer.prev;

                newPage.next = pointer;
                newPage.prev = tmp;

                pointer.prev = newPage;
                tmp.next = newPage;
            }
            this.size++;
        }

        // delete pointer node
        public Page deletePage (){
            if (pointer == null){
                System.out.println("list is empty");
                return null;
            } else if (pointer.next == pointer && pointer.prev == pointer){
                // there is only 1 page in the list
                Page tmp = pointer;
                pointer = null;
                return tmp;
            } else {
                // delete the node that the pointer is pointing at
                Page prevPage = pointer.prev;
                Page nextPage = pointer.next;
                
                prevPage.next = nextPage;
                nextPage.prev = prevPage;

                Page tmp = pointer;
                pointer = nextPage; // place the pointer to the page next to it

                this.size--;
                return tmp;
            }
        }

        // search page
        public Page search (int address){
            if (pointer == null){
                return null;
            }

            Page current = pointer;
            do {
                if (current.pageAddr == address){
                    return current;
                }
                current = current.next;
            } while (current != pointer);

            // System.out.println("node not found");
            return null;
        }
    }



    private boolean debugMode;
    private int totalDiskWrites;
    private int totalPageFaults;
    private int maxFrame;
    private PageList pageList;
    // private int totalDiskReads;

    public EscMMU(int frames) {
        this.maxFrame = frames;
        this.debugMode = false;
        this.totalDiskWrites = 0;
        // this.totalDiskReads = 0;
        this.pageList = new PageList();
    }
    
    public void setDebug() {
        this.debugMode = true;
    }
    
    public void resetDebug() {
        this.debugMode = false;
    }
    
    public void readMemory(int page_number) {
        Page newPage = pageList.search(page_number);

        if (newPage!=null){ // page is found
            newPage.useBit = true;
            if (debugMode){
                System.out.println("reading           " + page_number);
            }
        } else {
            // handle page fault
            // System.out.println("page is not found");
            handlePageFault(page_number);
            if (debugMode){
                System.out.println("reading           " + page_number);
            }
        }
    }
    
    public void writeMemory(int page_number) {
        Page newPage = pageList.search(page_number);

        if (newPage != null){ // page is found
            newPage.useBit = true;
            newPage.dirtyBit = true;

            if (debugMode){
                System.out.println("writing           " + page_number);
            }
        } else{
            // handle page fault
            // System.out.println("page is not found");
            newPage = handlePageFault(page_number);
            newPage.dirtyBit = true;
            if (debugMode){
                System.out.println("writing           " + page_number);
            }
        }
    }

    private Page handlePageFault (int page_number){
        totalPageFaults++;

        if (debugMode){
            System.out.println("Page fault        " + page_number);
        }

        if (this.pageList.size >= maxFrame){// need to discard some pages

            // iterate the pointer through the list
            Page current = pageList.pointer;
            while (current.useBit){
                current.useBit = false;
                pageList.pointer = current.next;
                current = pageList.pointer;
            } 
            // pointer is now pointing at the page to be discarded

            // check if the page is dirty and needs to write to disk
            if (current.dirtyBit){
                if (debugMode){
                    System.out.println("Disk write        " + current.pageAddr);
                }
                totalDiskWrites++;
            }

            // discard the page and point to the next page
            Page deletedPage = pageList.deletePage();
            if (debugMode){
                System.out.println("Discard           " + deletedPage.pageAddr);
            }
        } 

        // add a new page before the pointer
        pageList.addPage(page_number);
        return pageList.search(page_number);
    }
    
    
    public int getTotalDiskReads() {
        return totalPageFaults;
    }
    
    public int getTotalDiskWrites() {
        return totalDiskWrites;
    }
    
    public int getTotalPageFaults() {
        return totalPageFaults;
    }

    // public static void main(String[] args) {
    //     EscMMU myEsc = new EscMMU(4);
    //     // EscMMU.PageList myPageList = myEsc.new PageList();

    //     // myEsc.pageList.addPage(1);
    //     // myEsc.pageList.addPage(2);
    //     // myEsc.pageList.printPages();

    //     myEsc.writeMemory(0);
    //     myEsc.readMemory(1);
    //     myEsc.readMemory(2);
    //     myEsc.readMemory(3);
    //     myEsc.writeMemory(2);
    //     myEsc.readMemory(4);
    //     myEsc.readMemory(3);
    //     myEsc.writeMemory(3);
    //     myEsc.readMemory(0);
    //     myEsc.readMemory(1);
    //     myEsc.readMemory(5);
    //     myEsc.readMemory(2);

    //     myEsc.pageList.printPages();
    //     System.out.println("pointer:\t "+ myEsc.pageList.pointer.pageAddr);
    // }

}