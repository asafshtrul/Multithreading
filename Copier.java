/**
 * Asaf Shtrul
 * ID: 203378039
 */
/**
 * This class reads a file from the results queue
 *  (the queue of files that contains the output of the searchers), 
 *  and copies it into the specified destination directory.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Copier extends java.lang.Object implements java.lang.Runnable {
	private static int COPY_BUFFER_SIZE = 4096;
    private File dest;
    private SynchronizedQueue<File> res;


    /**
	 * Constructor. Initializes the worker with a destination directory and a queue of files to copy. 
	 * @param:destination - Destination directory, resultsQueue - Queue of files found, to be copied.
	 */
    public Copier(java.io.File destination, SynchronizedQueue<java.io.File> resultsQueue) {
    	//check if the destination path is a folder.
        if (!destination.isDirectory()) {
            throw new IllegalArgumentException("the destination isn't a folder");
        }
        this.res = resultsQueue;
        this.dest = destination;
    }

    @Override
    /**
     * Runs the copier thread. Thread will fetch files from queue and copy them,
     * one after each other, to the destination directory.
     * When the queue has no more files, the thread finishes. 
     */
    public void run() {
    	// dequeue the first item from the queue.
        File file = res.dequeue();
       
        byte[] buffer = new byte[COPY_BUFFER_SIZE];
        while (file != null) {
            try {
                FileInputStream srcstream = new FileInputStream(file);
                int len = srcstream.read(buffer);
                File tempfile = new File(dest, file.getName());
                FileOutputStream deststream = new FileOutputStream(tempfile);
                //
                while (len > 0) {
                	deststream.write(buffer, 0, len);
                	len = srcstream.read(buffer);
                }
                deststream.close();
                srcstream.close();
                file = res.dequeue();
                
            } catch (IOException e) {
                System.err.println("IOException");
            }
        }
    }
}
