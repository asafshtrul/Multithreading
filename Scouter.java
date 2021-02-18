/**
 * Asaf Shtrul
 * ID: 203378039
 */

/**
 * This class is responsible for listing all directories that exist under the given root directory.
 * It enqueues all directories into the directory queue.
 * There is always only one scouter thread in the system.
 */


import java.io.File;

public class Scouter extends java.lang.Object implements java.lang.Runnable {
    private SynchronizedQueue<File> directory_queue;
    private File root;
    
    /**
	 * Construnctor. Initializes the scouter with a queue for the directories
	 * to be searched and a root directory to start from.
	 * @params:
	 * directoryQueue - A queue for directories to be searched
	 * root - Root directory to start from
	 */	
    public Scouter(SynchronizedQueue<java.io.File> directoryQueue, java.io.File root) {
        this.root = root;
        this.directory_queue = directoryQueue;
    }

    
    /**
     * Starts the scouter thread. Lists directories under root directory and adds them to queue, 
     * then lists directories in the next level and enqueues them and so on. 
     * This method begins by registering to the directory queue as a producer and when finishes, it unregisters from it. 
     */
    @Override
    public void run() {
    	// increases the internal producers counter of the queue by one.
        this.directory_queue.registerProducer();
        directory_queue.enqueue(this.root);
        try {
        	addPath(this.root);
        } catch (IllegalArgumentException e) {
        	System.err.println(e.toString());
        	//e.printStackTrace();
        }
        // Unregisters a producer from the queue.
        this.directory_queue.unregisterProducer();
    }
    
    // Adding the file path to the queue
    public void addPath(File path) throws IllegalArgumentException {
        File[] PathFiles;
        if (path.isDirectory() == true) {
        	PathFiles = path.listFiles();
            for (int i=0; i<PathFiles.length; i++) {
            	File currentfile = PathFiles[i];
                if (currentfile.isDirectory()) {
                	directory_queue.enqueue(currentfile);
                	addPath(currentfile);
                }
            }
        } else {
            throw new IllegalArgumentException("Can't resolve the directory path " + path);
        }
    }

}
