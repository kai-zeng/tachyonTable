import static org.junit.Assert.*;

import java.io.IOException;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

@BenchmarkOptions(callgc = false, benchmarkRounds = 3, warmupRounds = 0)
public class FileChannelTest extends AbstractBenchmark {
  private static int[] numFilesArr = {10, 100, 1000, 10000};
  private static final int bytesAtATime = 1000;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    for (int numFiles : numFilesArr) {
      TestUtils.createDir(numFiles);
    }
  }

  /*
   * Tests opening and reading files numFiles files in one directory, bytesAtATime bytes at a time.
   */
  public void testGeneralNoMap(int numFiles) throws IOException {
    File testDir = new File(TestUtils.dirs[TestUtils.RD_IND], String.valueOf(numFiles));
    byte[] readBytes = new byte[TestUtils.FILE_SIZE];
    int off = 0;
    for (int i = 0; i < numFiles; ++i) {
      File f = new File(testDir, String.valueOf(i));
      FileChannel channel = FileChannel.open(f.toPath(), StandardOpenOption.READ);
      int endOff = (int) (off + channel.size());
      while (off < endOff) {
        off += channel.read(ByteBuffer.wrap(readBytes, off, Math.min(bytesAtATime, endOff - off)));
      }
      channel.close();
      //System.out.println(i);
    }
  }

  /*
   * Tests opening and reading files numFiles files in one directory, one byte at a time, with a MappedByteBuffer.
   */
  public void testGeneralMap(int numFiles) throws IOException {
    File testDir = new File(TestUtils.dirs[TestUtils.RD_IND], String.valueOf(numFiles));
    byte[] readBytes = new byte[TestUtils.FILE_SIZE];
    int off = 0;
    for (int i = 0; i < numFiles; ++i) {
      File f = new File(testDir, String.valueOf(i));
      FileChannel channel = FileChannel.open(f.toPath(), StandardOpenOption.READ);
      MappedByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
      int endOff = (int) (off + channel.size());
      while (off < endOff) {
        int bytesToRead = Math.min(bytesAtATime, endOff - off);
        buf.get(readBytes, off, bytesToRead);
        off += bytesToRead;
      }
      channel.close();
      //System.out.println(i);
      channel.close();
    }
  }

  @Test
  public void testTenNoMap() throws IOException {
    testGeneralNoMap(10);
  }

  @Test
  public void testTenMap() throws IOException {
    testGeneralMap(10);
  }

  @Test
  public void testHundredNoMap() throws IOException {
    testGeneralNoMap(100);
  }

  @Test
  public void testHundredMap() throws IOException {
    testGeneralMap(100);
  }

  @Test
  public void testThousandMap() throws IOException {
    testGeneralMap(1000);
  }

  @Test
  public void testThousandNoMap() throws IOException {
    testGeneralNoMap(1000);
  }

  @Test
  public void testTenThousandMap() throws IOException {
    testGeneralMap(10000);
  }

  @Test
  public void testTenThousandNoMap() throws IOException {
    testGeneralNoMap(10000);
  }
}
