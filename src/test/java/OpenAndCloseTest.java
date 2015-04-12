import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;

public class OpenAndCloseTest extends AbstractBenchmark {

  private static int[] numFilesArr = {10, 100, 1000, 10000, 100000, 200000, 300000, 400000, 1000000};

  @BeforeClass
  public static void setupBeforeClass() throws IOException {
    for (int numFiles : numFilesArr) {
      File testDir = new File(TestUtils.dirs[TestUtils.RD_IND], String.valueOf(numFiles));
      if (testDir.exists()) {
        continue;
      }
      testDir.mkdirs();
      for (int i = 0; i < numFiles; ++i) {
        File f = new File(testDir, String.valueOf(i));
        f.createNewFile();
      }
    }
  }

  /*
   * Tests opening and closing files n files in one directory.
   */
  public void testGeneral(int numFiles) throws IOException {
    File testDir = new File(TestUtils.dirs[TestUtils.RD_IND], String.valueOf(numFiles));
    for (int i = 0; i < numFiles; ++i) {
      File f = new File(testDir, String.valueOf(i));
      FileChannel fc = FileChannel.open(f.toPath(), StandardOpenOption.READ);
      fc.close();
    }
  }

  @Test
  public void testTen() throws IOException {
    testGeneral(numFilesArr[0]);
  }

  @Test
  public void testHundred() throws IOException {
    testGeneral(numFilesArr[1]);
  }

  @Test
  public void testThousand() throws IOException {
    testGeneral(numFilesArr[2]);
  }

  @Test
  public void testTenThousand() throws IOException {
    testGeneral(numFilesArr[3]);
  }

  @Test
  public void testHundredThousand() throws IOException {
    testGeneral(numFilesArr[4]);
  }

  @Test
  public void testTwoHundredThousand() throws IOException {
    testGeneral(numFilesArr[5]);
  }

  @Test
  public void testThreeHundredThousand() throws IOException {
    testGeneral(numFilesArr[6]);
  }

//  @Test
//  public void testFourHundredThousand() throws IOException {
//    testGeneral(numFilesArr[6]);
//  }

//  @Test
//  public void testMillion() throws IOException {
//    testGeneral(numFilesArr[6]);
//  }
}
