import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * Utilities and values shared between the different benchmarks.
 */
public class TestUtils {

  /*
   * The directories where we'll be running the benchmarks. Each one should be
   * a different storage medium. They should be changed depending on where the
   * tests are run.
   */
  public static final String[] dirs = { "/mnt/ramdisk",
                                        "/root/harddisk" };

  /*
   * The index of the ramdisk path.
   */
  public static final int RD_IND = 0;

  /*
   * The index of the hard disk path.
   */
  public static final int HD_IND = 1;

  /*
   * Returns the path of the large file.
   */
  public static Path largeFilePath(String root) {
    return Paths.get(root, "l0.txt");
  }

  /*
   * Returns the path of the small file with the given index.
   */
  public static Path smallFilePath(String root, int fileInd) {
    return Paths.get(root, "s" + String.valueOf(fileInd) + ".txt");
  }

  // The total size of the file to create in each directory
  public static final int FILE_SIZE = 268435456; // 256MB

  // If the directory doesn't exist, create one with a file of size FILE_SIZE
  // split into numFiles files
  public static void createDir(int numFiles) throws IOException {
    File testDir = new File(TestUtils.dirs[TestUtils.RD_IND], String.valueOf(numFiles));
    if (testDir.exists()) {
      return;
    }
    testDir.mkdirs();
    final int splitFileSize = FILE_SIZE / numFiles;
    byte[] writeBytes = new byte[splitFileSize];
    for (int i = 0; i < numFiles; ++i) {
      File f = new File(testDir, String.valueOf(i));
      RandomAccessFile raf = new RandomAccessFile(f, "rw");
      raf.write(writeBytes);
      raf.close();
    }
  }
}
