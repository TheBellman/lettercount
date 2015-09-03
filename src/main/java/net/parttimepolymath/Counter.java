package net.parttimepolymath;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jcip.annotations.NotThreadSafe;

/**
 * the core controller class. It takes responsibility for launching threads to do the count, and accumulating the results. This class is
 * not threadsafe - simultaneous invocations of execute() will garble the results.
 * 
 * @author robert
 */
@NotThreadSafe
public final class Counter {
    /**
     * Class logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Counter.class);
    /**
     * the number of sub threads to launch. this will become a parameter later.
     */
    private static final int NTHREADS = 4;

    /**
     * the path being processed by this instance.
     */
    private final Path inputPath;

    /**
     * the current set of counts. By using a tree map we get sorting for free.
     */
    private final Map<Character, Integer> totalCount = new TreeMap<>();

    /**
     * semaphore used to prevent simultaneous read and write to the total count.
     */
    private final Lock totalLock = new ReentrantLock(true);

    /**
     * primary constructor.
     * 
     * @param path specification of the file to process. We assume this is a good, non-null value that points to a readable file.
     */
    public Counter(final Path path) {
        inputPath = path;
    }

    /**
     * write a report of the current counts to the specified output stream.
     * 
     * @param out the output stream to write to.
     * @throws IOException if unable to open the file for some reason.
     */
    public void report(final PrintStream out) throws IOException {
        out.println(String.format("Letter count for %s:", inputPath.toAbsolutePath().toString()));
        out.println(String.format("File size = %d", Files.size(inputPath)));
        try {
            totalLock.lockInterruptibly();
            for (Entry<Character, Integer> item : totalCount.entrySet()) {
                out.println(String.format("%c = %d", item.getKey(), item.getValue()));
            }
        } catch (InterruptedException e) {
            System.err.println("Totals lock forcibly interrupted");
        } finally {
            totalLock.unlock();
        }
    }

    /**
     * perform the actual count.
     * 
     * @throws IOException if we get file reads.
     */
    public void execute() throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(NTHREADS);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath.toFile()), "UTF8"))) {
            List<Future<Map<Character, MutableInt>>> results = new ArrayList<>();

            // read the file in the main thread and throw the segments to sub-threads for counting.
            int lastRead = 0;
            do {
                CounterWorker cw = new CounterWorker();
                lastRead = br.read(cw.getBuffer());
                if (lastRead > 0) {
                    results.add(executorService.submit(cw));
                }

            } while (lastRead == CounterWorker.BUFSIZ);

            accumulate(results);

        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Serious failure in execution", e);
        } finally {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                    LOGGER.warn("Results inaccurate - not all counter threads completed");
                }
            } catch (InterruptedException ie) {
                // silently swallow the exception.
            }
        }
    }

    /**
     * accumulate the provided sub-results into the main result.
     * 
     * @param results the sub-results to accumulate
     * @throws ExecutionException if we fail to get a result from the threads.
     * @throws InterruptedException if we are interrupted waiting for a thread to finish.
     */
    private void accumulate(final List<Future<Map<Character, MutableInt>>> results) throws InterruptedException, ExecutionException {
        try {
            totalLock.lock();

            totalCount.clear();

            for (Future<Map<Character, MutableInt>> result : results) {
                for (Entry<Character, MutableInt> item : result.get().entrySet()) {
                    Integer count = totalCount.get(item.getKey());
                    if (count == null) {
                        totalCount.put(item.getKey(), item.getValue().toInteger());
                    } else {
                        totalCount.put(item.getKey(), item.getValue().toInteger() + count);
                    }
                }
            }

        } finally {
            totalLock.unlock();
        }
    }

}
