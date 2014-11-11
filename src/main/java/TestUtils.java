import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * Utilities and values shared between the different benchmarks.
 */
public class TestUtils {
	/*
	 * The root directory of the ramdisk on the file system. This should be
	 * changed depending on where the ramdisk is located.
	 */
	public static final String ROOT = "/Volumes/ramdisk";

	/*
	 * Returns the path of the large file.
	 */
	public static Path largeFilePath() {
		return Paths.get(ROOT, "l0.txt");
	}

	/*
	 * Returns the path of the small file with the given index.
	 */
	public static Path smallFilePath(int ind) {
		return Paths.get(ROOT, "s" + String.valueOf(ind) + ".txt");
	}
}
