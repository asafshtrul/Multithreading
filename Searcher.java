/**
 * Asaf Shtrul
 * ID: 203378039
 */
/**
 * This class reads a directory from the directory queue and lists all files in this directory.
 * Then, it checks each file name for containing the given pattern and if it has the correct extension.
 * Files that contain the pattern and have the correct extension are enqueued to the results queue (to be copied).
 */

import java.io.File;

public class Searcher extends java.lang.Object implements java.lang.Runnable {
    private String Pattern, extension;
    private SynchronizedQueue<File> res;
    private SynchronizedQueue<File> dirqueue;
    
    /**
	 * Constructor. Initializes the searcher thread.
	 * @params:
	 * pattern - Pattern to look for
	 * extension - wanted extension
	 * directoryQueue - A queue with directories to search in (as listed by the scouter)
	 * resultsQueue - A queue for files found (to be copied by a copier) 
	 */
    public Searcher(java.lang.String pattern, java.lang.String extension, SynchronizedQueue<java.io.File> directoryQueue, SynchronizedQueue<java.io.File> resultsQueue) {
        this.Pattern = pattern;
        this.res = resultsQueue;
        this.dirqueue = directoryQueue;
        this.extension = extension;
    }

    /**
     * Runs the searcher thread. Thread will fetch a directory to search in from the directory queue, 
     * then search all files inside it (but will not recursively search subdirectories!). 
     * 
     * Files that a contain the pattern and have the wanted extension are enqueued to the results queue. 
     * This method begins by registering to the results queue as a producer and when finishes, it unregisters from it. 
     */
    @Override
    public void run() {
        File path;
        // increases the internal producers counter of the queue by one.
        res.registerProducer();
        while ((path = dirqueue.dequeue()) != null) {
        	// array of abstract pathnames denoting the files and directories in the directory & check the pattern.
            File[] PathFiles = path.listFiles();
            for (int i=0; i<PathFiles.length; i++) {
            	File currentfile = PathFiles[i];
            	// check the extension 
                if ((!currentfile.isDirectory() &&
                		currentfile.getName().endsWith(this.extension) && currentfile.getName().contains(Pattern))) {
                	res.enqueue(currentfile);
                }
            }
        }
        // Unregisters a producer from the queue.
        res.unregisterProducer();

    }
}
