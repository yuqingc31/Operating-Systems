import java.io.BufferedReader;
import java.io.FileReader;

public class Memsim {
    public static void main(String[] args) {
        int page_offset = 12;			// page is 2^12 = 4KB
	
		int frames;
        BufferedReader input = null;
        MMU mmu = null;
        
        /* read parameters */
        //the file
        try {
           input = new BufferedReader(new FileReader(args[0]));
        }
        catch (java.io.FileNotFoundException e) {
            System.out.println("Input '" + args[0] + "' could not be found");
            System.out.println("Usage: java Memsim inputfile numberframes replacementmode debugmode");
            System.exit(-1);
        }
        
        //number of frames
        frames = Integer.parseInt(args[1]);
        
        //the replacement mode
        if (args[2].equals("rand"))
            mmu = new RandMMU(frames);
        else if (args[2].equals("lru"))
            mmu = new LruMMU(frames);
        else if (args[2].equals("clock"))
            mmu = new EscMMU(frames);
        else if (args[2].equals("fifo"))
            mmu = new FIFO(frames);
        else {
            System.out.println("Usage: java Memsim inputfile numberframes replacementmode debugmode");
            System.out.println("replacementmodes are [ rand | lru | esc ]");
            System.exit(-1);
        }
        
        //debug mode?
        if (args[3].equals("debug"))
            mmu.setDebug();
        else if (args[3].equals("quiet"))
            mmu.resetDebug();
        else {
            System.out.println("Usage: java Memsim inputfile numberframes replacementmode debugmode");
            System.out.println("debugmode are [ debug | quiet ]");
            System.exit(-1);
        }
        
        /* Process the traces from the file */
        String traceLine;
        String[] traceCmd;
        long logical_address;
        int page_number;
        int no_events = 0;
        
        try {
            traceLine = input.readLine();
            while (traceLine != null) {
                traceCmd = traceLine.split(" ");
                
                //convert from hexadecimal address from file, to appropriate page number
                logical_address = Long.parseLong(traceCmd[0],16);
                page_number = (int) (logical_address >>> page_offset);
                
                //process read or write
                if (traceCmd[1].equals("R")){
                    mmu.readMemory(page_number);
                }
                else if (traceCmd[1].equals("W")){
                    mmu.writeMemory(page_number);
                }
                else {
                    System.out.println("Badly formatted file. Error on line " + (no_events+1));
                    System.exit(-1);
                }
                
                no_events++;
                traceLine = input.readLine();
            }
        }
        catch (java.io.IOException e) {
            System.out.println("Error reading input file");
            System.exit(-1);
        }
		catch (NumberFormatException e) {
			System.out.println("Memory address strange on line " + (no_events+1));
            System.exit(-1);
		}
        
        /* Print results */
        System.out.println("total memory frames: " + frames);
        System.out.println("events in trace: " + no_events);
        System.out.println("total disk reads: " + mmu.getTotalDiskReads());
        System.out.println("total disk writes: " + mmu.getTotalDiskWrites());
        // System.out.println("page fault rate: " + ((double) mmu.getTotalPageFaults())/no_events);
        System.out.println("page fault rate: " + String.format("%.4f", ((double) mmu.getTotalPageFaults()) / no_events));
    }
    
}
