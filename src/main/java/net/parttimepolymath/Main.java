package net.parttimepolymath;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * main executable entry point for the application. This does little more than provide a CLI and bootstrap up the
 * actual application.
 * 
 * @author robert
 */
public class Main {
    /**
     * default number of threads.
     */
    private static final int NTHREADS = 4;

    /**
     * application properties loaded from the classpath.
     */
    private static final Properties PROPERTIES = loadProperties();

    /**
     * The main entry point, invoked from the command line. This method parses the runtime
     * invocation and responds appropriately.
     * 
     * @param args
     */
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("?", "help", false, "print this message");
        options.addOption("v", "version", false, "print version");
        options.addOption("f", "file", true, "specify the input file");
        options.addOption("n", "threads", true, "optionally specify the number of worker threads (defaults to 4)");

        // Yes, this is ugly. sue me.
        CommandLineParser parser = new PosixParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption('?')) {
                doHelp(options);
            } else if (cmd.hasOption('v')) {
                doVersion();
            } else {
                if (cmd.hasOption('f') && validateFile(cmd.getOptionValue('f'))) {

                    int nThreads = parseThreads(cmd);

                    Counter instance = new Counter(Paths.get(cmd.getOptionValue('f')), nThreads);
                    instance.execute();
                    instance.report(System.out);
                } else {
                    doHelp(options);
                }
            }
        } catch (ParseException ex) {
            doHelp(options);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * work out if the thread count has been added to the command
     * 
     * @param cmd the command line we are parsing.
     * @return a value greater than 0.
     */
    private static int parseThreads(final CommandLine cmd) {
        if (!cmd.hasOption('n')) {
            return NTHREADS;
        }
        try {
            Integer result = NumberUtils.createInteger(cmd.getOptionValue('n'));
            if (result == null || result < 1) {
                return NTHREADS;
            }
            return result;
        } catch (NumberFormatException nfe) {
            return NTHREADS;
        }
    }

    /**
     * validate that the path identifies a valid file.
     * 
     * @param pathName a path assumed to point to a readable file, and assumed non blank.
     * @return true if the path identifies a valid file, false otherwise.
     */
    private static boolean validateFile(final String pathName) {
        Path path = Paths.get(pathName);
        return Files.isReadable(path) && Files.isRegularFile(path);
    }

    /**
     * report the application version to standard out.
     */
    private static void doVersion() {
        String name = PROPERTIES.getProperty("project.name");
        String version = PROPERTIES.getProperty("project.version");
        System.out.println(String.format("%s [%s]", name, version));
    }

    /**
     * print command line options to standard out.
     * 
     * @param options the command line options.
     */
    private static void doHelp(final Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(PROPERTIES.getProperty("project.name"), options);
    }

    /**
     * load properties from the class path.
     * 
     * @return a Properties object, which should be non-null unless theres an exception.
     */
    private static Properties loadProperties() {
        Properties props = new Properties();
        try {
            InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("project.properties");
            props.load(in);
        } catch (IOException ioe) {
            System.err.println("Failed to read application properties");
        }
        return props;
    }
}
