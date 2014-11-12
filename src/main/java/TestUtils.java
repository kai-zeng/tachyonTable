import java.io.IOException;
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
	public static final String[] dirs = { "/Volumes/ramdisk",
			"/Users/manu/harddisk" };

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
}
