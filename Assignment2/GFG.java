// Java program to demonstrate optimal page
// replacement algorithm.
import java.io.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

class GFG {

	// Function to check whether a page exists
	// in a frame or not
	static boolean search(int key, int[] fr)
	{
		for (int i = 0; i < fr.length; i++)
			if (fr[i] == key)
				return true;
		return false;
	}

	// Function to find the frame that will not be used
	// recently in future after given index in pg[0..pn-1]
	static int predict(int pg[], int[] fr, int pn,
					int index)
	{
		// Store the index of pages which are going
		// to be used recently in future
		int res = -1, farthest = index;
		for (int i = 0; i < fr.length; i++) {
			int j;
			for (j = index; j < pn; j++) {
				if (fr[i] == pg[j]) {
					if (j > farthest) {
						farthest = j;
						res = i;
					}
					break;
				}
			}

			// If a page is never referenced in future,
			// return it.
			if (j == pn)
				return i;
		}

		// If all of the frames were not in future,
		// return any of them, we return 0. Otherwise
		// we return res.
		return (res == -1) ? 0 : res;
	}

	static void optimalPage(int pg[], int pn, int fn)
	{
		// Create an array for given number of
		// frames and initialize it as empty.
		int[] fr = new int[fn];

		// Traverse through page reference array
		// and check for miss and hit.
		int hit = 0;
		int index = 0;
		for (int i = 0; i < pn; i++) {

			// Page found in a frame : HIT
			if (search(pg[i], fr)) {
				hit++;
				continue;
			}

			// Page not found in a frame : MISS

			// If there is space available in frames.
			if (index < fn)
				fr[index++] = pg[i];

			// Find the page to be replaced.
			else {
				int j = predict(pg, fr, pn, i + 1);
				fr[j] = pg[i];
			}
		}
        
		// System.out.println("No. of hits = " + hit);
        // int misses = pn - hit;
        // System.out.println("No. of misses = " + misses);
        double hit_rate = (double) hit / pn;
        System.out.println(hit_rate);
	}

	// driver function
	public static void main(String[] args)
	{
        //java GFG gcc.trace 100 

        int page_offset = 12;
        int frames;
        BufferedReader input = null;

        try {
            input = new BufferedReader(new FileReader(args[0]));
        }
        catch (java.io.FileNotFoundException e) {
            System.out.println("Input '" + args[0] + "' could not be found");
            System.exit(-1);
        }
        // frames = Integer.parseInt(args[1]);

        String traceLine;
        String[] traceCmd;
        long logical_address;
        int page_number;
        LinkedList<Integer> page_queue = new LinkedList<>();

        try {
            // input file: trace files
            traceLine = input.readLine();
            // when traceLine is not null, continue
            while (traceLine != null) {
                // split the command to two parts 
                traceCmd = traceLine.split(" ");
                
                // the front part is the address
                logical_address = Long.parseLong(traceCmd[0],16);
                // System.out.println(logical_address);
                // get the page number from the address
                page_number = (int) (logical_address >>> page_offset);

                // add the page number to the queue
                page_queue.addFirst(page_number);

                traceLine = input.readLine();
            }
        }
        catch (java.io.IOException e) {
            System.out.println("Error reading input file");
            System.exit(-1);
        }
		catch (NumberFormatException e) {
			System.out.println("Memory address strange on line ");
            System.exit(-1);
		}

        int[] pg = new int[page_queue.size()];
		int index = 0;

        for (Integer page : page_queue) {
            pg[index++] = page;
        }

		int pn = 1000000;
        for(int i = 1; i <=800; i++){
            optimalPage(pg, pn, i);
        }
	}
}

