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

/**
 * main executable entry point for the application. This does little more than provide a CLI and bootstrap up the
 * actual application.
 * 
 * @author robert
 */
public class Main {
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
                    Main instance = new Main();
                    instance.execute(Paths.get(cmd.getOptionValue('f')));
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
     * Run the process. Yes, there is a race condition in that the file may go away between checking that it
     * exists and is usable and when we start to try reading it.
     * 
     * @param path the path to the file, assumed non-null and to refer to a file.
     */
    private void execute(final Path path) {
        // TODO Auto-generated method stub
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
