import static org.junit.Assert.*;

import java.io.IOException;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;

import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

@BenchmarkOptions(callgc = false, benchmarkRounds = 1, warmupRounds = 0)
public class OpenAndReadTest extends AbstractBenchmark {
  private static int[] numFilesArr = {10, 100, 1000, 10000, 100000, 200000,
                                      300000, 400000, 800000, 1000000,
                                      2000000, 4000000};

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    for (int numFiles : numFilesArr) {
      TestUtils.createDir(numFiles);
    }
  }

  /*
   * Tests opening and reading files numFiles files in one directory.
   */
  public void testGeneral(int numFiles) throws IOException {
    File testDir = new File(TestUtils.dirs[TestUtils.RD_IND], String.valueOf(numFiles));
    byte[] readBytes = new byte[TestUtils.FILE_SIZE];
    int off = 0;
    for (int i = 0; i < numFiles; ++i) {
      File f = new File(testDir, String.valueOf(i));
      RandomAccessFile raf = new RandomAccessFile(f, "r");
      off += raf.read(readBytes, 0, readBytes.length - off);
      raf.close();
    }
  }

  @Test
  public void testTen() throws IOException {
    testGeneral(10);
  }

  @Test
  public void testHundred() throws IOException {
    testGeneral(100);
  }

  @Test
  public void testThousand() throws IOException {
    testGeneral(1000);
  }

  @Test
  public void testTenThousand() throws IOException {
    testGeneral(10000);
  }

  @Test
  public void testHundredThousand() throws IOException {
    testGeneral(100000);
  }

  @Test
  public void testTwoHundredThousand() throws IOException {
    testGeneral(200000);
  }

  @Test
  public void testThreeHundredThousand() throws IOException {
    testGeneral(300000);
  }

  @Test
  public void testFourHundredThousand() throws IOException {
    testGeneral(400000);
  }

  @Test
  public void testEightHundredThousand() throws IOException {
    testGeneral(800000);
  }

  @Test
  public void testOneMillion() throws IOException {
    testGeneral(1000000);
  }

  @Test
  public void testTwoMillion() throws IOException {
    testGeneral(2000000);
  }

  @Test
  public void testFourMillion() throws IOException {
    testGeneral(4000000);
  }
}
