package net.parttimepolymath;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

public class CounterTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

    @Test
    public void test() throws IOException {
        File testFile = tempFolder.newFile();
        IOUtils.copy(this.getClass().getResourceAsStream("/33437.txt.utf-8.txt"), new FileWriter(testFile));

        Counter instance = new Counter(testFile.toPath(), 4);
        instance.execute();
        instance.report(System.out);

        String sysLog = systemOutRule.getLogWithNormalizedLineSeparator();
        assertTrue(sysLog.startsWith("Letter count for "));
        assertTrue(sysLog.contains("k = 3057\n"));
        assertTrue(sysLog.endsWith("z = 292\n"));
    }

}
