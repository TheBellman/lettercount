package net.parttimepolymath;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.SystemUtils;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

public class MainTest {
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

    private final static String USAGE = "usage: LetterCount\n" + " -?,--help         print this message\n"
            + " -f,--file <arg>   specify the input file\n" + " -v,--version      print version\n";

    @After
    public void tearDown() throws Exception {
        systemOutRule.clearLog();
    }

    @Test
    public void testVersion() {
        String[] args = {"-v"};
        Main.main(args);
        assertEquals("LetterCount [0.0.1-SNAPSHOT]\n", systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    public void testHelp() {
        String[] args = {"-?"};
        Main.main(args);
        assertEquals(USAGE, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    public void testBadCli() {
        String[] args = {"-z"};
        Main.main(args);
        assertEquals(USAGE, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    public void testNoArgs() {
        String[] args = {};
        Main.main(args);
        assertEquals(USAGE, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    public void testNoFileArg() {
        String[] args = {"-f"};
        Main.main(args);
        assertEquals(USAGE, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    public void testBadFile() {
        String[] args = {"-f /this/does/notexist"};
        Main.main(args);
        assertEquals(USAGE, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    public void testNotFile() {
        String[] args = {"-f " + SystemUtils.getJavaIoTmpDir().getAbsolutePath()};
        Main.main(args);
        assertEquals(USAGE, systemOutRule.getLogWithNormalizedLineSeparator());
    }

}
