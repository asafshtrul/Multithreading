/**
 * Asaf Shtrul
 * ID: 203378039
 */
/**
 *This is the main class of the application.
 * This class contains a main method that starts the search process according to the given command lines.
 * 
 * Usage of the main method from command line goes as follows:
		> java DiskSearcher <filename-pattern> <file-extension> <root directory> <destination directory>
			<# of searchers> <# of copiers>
 */


import java.io.File;

import java.io.IOError;

public class DiskSearcher extends java.lang.Object {
    private static int DIRECTORY_QUEUE_CAPACITY = 50;
    private static int RESULTS_QUEUE_CAPACITY = 50;
    
    public DiskSearcher(){
    }

    public static void main(String[] args) {
    	// check first if the input is valid.
    	int check = args.length;
        if (check <= 5) {
            System.err.println("Invalid number of arguments");
            return;
        }
        File root, dest;
        String pattern = args[0];
        String extension = args[1];
        
        try {
        	root = new File(args[2]);
        } catch (IOError e) {
            System.err.println("can't resolve source path: " + args[2]);
            //
            e.printStackTrace();
            return;
        }

        try {
        	dest = new File(args[3]);
            if (!dest.exists()) {
            	dest.mkdir();
            }
        } catch (IOError e) {
            System.err.println("can't resolve source path: " + args[3]);
            //
            e.printStackTrace();
            return;
        }

        // we will get the number of copies & searchers
        int numS = Integer.parseInt(args[4]);
        int numC = Integer.parseInt(args[5]);
        SynchronizedQueue<File> directoryQueue = new SynchronizedQueue<File>(DIRECTORY_QUEUE_CAPACITY);
        SynchronizedQueue<File> resultsQueue = new SynchronizedQueue<File>(RESULTS_QUEUE_CAPACITY);
        
        Scouter scouter = new Scouter(directoryQueue, root);
        Thread scout = new Thread(scouter);
        scout.start();

        Searcher[] searchers = new Searcher[numS];
        Thread[] searchersThreads = new Thread[numS];

        for (int i = 0; i < numS; i++) {
        	
            searchers[i] = new Searcher(pattern, extension , directoryQueue, resultsQueue);
            searchersThreads[i] = new Thread(searchers[i]);
            searchersThreads[i].start();
        }

        Copier[] copiers = new Copier[numC];
        Thread[] copiersThreads = new Thread[numC];

        for (int i = 0; i < numC; i++) {
            copiers[i] = new Copier(dest, resultsQueue);
            copiersThreads[i] = new Thread(copiers[i]);
            copiersThreads[i].start();
        }

        // Waiting for all the threads
        try {
            for (int i = 0; i < searchers.length; i++) {
                searchersThreads[i].join();
            }

            for (int i = 0; i < copiers.length; i++) {
                copiersThreads[i].join();
            }
        } catch (InterruptedException e) {
            System.err.println("interrupt occured while closing threads");
        }
        System.out.println("process ended successfully");
    }
}
