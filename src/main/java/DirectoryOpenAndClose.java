import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;

public class DirectoryOpenAndClose extends AbstractBenchmark {

	/*
	 * The size of the files to be creating. We'll be creating a lot of these,
	 * so make sure they don't exceed the size of the ramdisk.
	 */
	public static final int FILE_SIZE = (int) (8 * Math.pow(2, 10));

	/*
	 * The bytes to write to each file.
	 */
	public static final byte[] WRITE_BYTES = new byte[FILE_SIZE];

	/*
	 * The number of subdirectories to create. Subdirectory i will have 10^i
	 * files.
	 */
	public static final int SUBDIRS = 4;

	public static String getDirPath(int subdir) {
		return Paths.get(TestUtils.ROOT, "dir-" + String.valueOf(subdir))
				.toString();
	}

	/*
	 * Returns the path of the file in the specified subdirectory and index.
	 */
	public static Path getFilePath(int subdir, int ind) {
		return Paths.get(getDirPath(subdir), String.valueOf(ind) + ".txt");
	}

	/*
	 * Creates a file in the specified subdirectory and index and writes to it.
	 */
	public static void createFile(int subdir, int ind) throws IOException {
		OutputStream out = Files.newOutputStream(getFilePath(subdir, ind));
		out.write(WRITE_BYTES);
		out.close();
	}

	public static void clearRoot() throws IOException {
		for (Path dir : Files.newDirectoryStream(Paths.get(TestUtils.ROOT),
				"dir-*")) {
			for (Path file : Files.newDirectoryStream(dir)) {
				Files.delete(file);
			}
			Files.delete(dir);
		}
	}

	/*
	 * Clears the root directory and creates all the files for opening and
	 * closing.
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		clearRoot();
		for (int i = 0; i < SUBDIRS; ++i) {
			new File(getDirPath(i)).mkdirs();
			int numFiles = (int) Math.pow(10, i);
			for (int j = 0; j < numFiles; ++j) {
				createFile(i, j);
			}
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		clearRoot();
	}

	/*
	 * Clears the root directory
	 */
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/*
	 * Tests opening and closing files in subdirectory n.
	 */
	public void testGeneral(int subdir) throws IOException {
		int numFiles = (int) Math.pow(10, subdir);
		for (int i = 0; i < numFiles; ++i) {
			InputStream in = Files.newInputStream(getFilePath(subdir, i));
			assertNotEquals(0, in.available());
			in.close();
		}
	}

	@Test
	public void testZero() throws IOException {
		testGeneral(0);
	}

	@Test
	public void testOne() throws IOException {
		testGeneral(1);
	}

	@Test
	public void testTwo() throws IOException {
		testGeneral(2);
	}

	@Test
	public void testThree() throws IOException {
		testGeneral(2);
	}

}
