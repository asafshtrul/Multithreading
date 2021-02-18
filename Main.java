/** 
 * Asaf Shtrul
 * ID: 203378039
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        List<String> lines = getLinesFromFile();
        System.out.println("Number of lines found: " + lines.size());
        System.out.println("Starting to process");

        long startTimeWithoutThreads = System.currentTimeMillis();
        workWithoutThreads(lines);
        long elapsedTimeWithoutThreads = (System.currentTimeMillis() - startTimeWithoutThreads);
        System.out.println("Execution time: " + elapsedTimeWithoutThreads);


        long startTimeWithThreads = System.currentTimeMillis();
        workWithThreads(lines);
        long elapsedTimeWithThreads = (System.currentTimeMillis() - startTimeWithThreads);
        System.out.println("Execution time: " + elapsedTimeWithThreads);

    }

    private static void workWithThreads(List<String> lines) {
        //Your code:
        //Get the number of available cores
        //Assuming X is the number of cores - Partition the data into x data sets
        //Create a fixed thread pool of size X
        //Submit X workers to the thread pool
		//Wait for termination
    	
    	//Get the number of available cores
    	int x = Runtime.getRuntime().availableProcessors();
    	//distribution the rows to the existing processors
    	int NumOfPartitions = lines.size() / x;
    	
    	//Create a fixed thread pool of size x
    	ExecutorService threadPool = Executors.newFixedThreadPool(x);
    	
        int end = NumOfPartitions, start;
        //Submit x workers to the thread pool using the submit method
        for (int i = 0; i < x; i++) {
            List<String> subLines = lines.subList(0, end);
            threadPool.submit(new Worker(subLines));
            start = end;
            end += NumOfPartitions;
        }
        //Wait until the executor service finishes by using the shutdown and awaitTermination 
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(1, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void workWithoutThreads(List<String> lines) {
        Worker worker = new Worker(lines);
        worker.run();
    }

    private static List<String> getLinesFromFile() {
        //Your code:
        //Read the shakespeare file provided from C:\Temp\Shakespeare.txt
        //and return an ArrayList<String> that contains each line read from the file.
        List<String> sLines = new ArrayList<>();
        File file = new File("C:\\Temp\\Shakespeare.txt");
        FileReader fReader;
        BufferedReader bReader = null;
        
        try {
        	fReader = new FileReader(file);
        	bReader = new BufferedReader(fReader);
            int index = 0;
            String line;
            
            while ((line = bReader.readLine()) != null) {
            	sLines.add(index, line);
                index++;
            }
        } catch (IOException e) {
            System.err.print("Error: " + e.getMessage());
        }
        return sLines;
    }
}

