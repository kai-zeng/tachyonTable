import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;

public class ReadAndOpen extends AbstractBenchmark {

	/*
	 * The size of the large file that will be created in bytes. We'll be
	 * creating a single file of this size, and a bunch of files whose total
	 * size equals this size, so we'll be using 2*TOTAL_FILE_SIZE space on the
	 * disk.
	 */
	public static final int TOTAL_FILE_SIZE = (int) (2 * Math.pow(2, 20));

	/*
	 * The size of each split file we will be creating.
	 */
	public static final int SPLIT_FILE_SIZE = (int) (8 * Math.pow(2, 10));

	/*
	 * The number of split files we will be creating.
	 */
	public static final int SPLIT_FILE_NUM = TOTAL_FILE_SIZE / SPLIT_FILE_SIZE;

	/*
	 * The array of bytes we'll be writing to each file. To avoid making the
	 * array too large, we keep it SPLIT_FILE_SIZE bytes.
	 */
	public static final byte[] WRITE_BYTES = new byte[SPLIT_FILE_SIZE];

	/*
	 * Deletes all the large and small files from the root directories.
	 */
	public static void clearDirs() throws IOException {
		for (String dir : TestUtils.dirs) {
			for (Path p : Files.newDirectoryStream(Paths.get(dir), "*.txt")) {
				Files.delete(p);
			}
		}
	}

	/*
	 * Here we clear the root directories and create the large and split files.
	 * We create one large file of size TOTAL_FILE_SIZE and SPLIT_FILE_NUM files
	 * of size SPLIT_FILE_SIZE.
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		clearDirs();
		for (String dir : TestUtils.dirs) {
			OutputStream out = Files.newOutputStream(TestUtils
					.largeFilePath(dir));
			for (int i = 0; i < SPLIT_FILE_NUM; ++i) {
				out.write(WRITE_BYTES, 0, SPLIT_FILE_SIZE);
			}
			out.close();
			for (int i = 0; i < SPLIT_FILE_NUM; ++i) {
				out = Files.newOutputStream(TestUtils.smallFilePath(dir, i));
				out.write(WRITE_BYTES, 0, SPLIT_FILE_SIZE);
				out.close();
			}
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		clearDirs();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/*
	 * Tests opening and reading the large file, making sure the contents match.
	 */
	public void testOpenAndReadLarge(String dir) throws InterruptedException, IOException {
		byte[] readBytes = new byte[WRITE_BYTES.length];
		InputStream in = Files.newInputStream(TestUtils.largeFilePath(dir));
		int ret = in.read(readBytes);
		while (ret != -1) {
			assertArrayEquals(WRITE_BYTES, readBytes);
			ret = in.read(readBytes);
		}
		in.close();
	}
	
	@Test
	public void testOpenAndReadLargeRD() throws InterruptedException, IOException {
		testOpenAndReadLarge(TestUtils.dirs[TestUtils.RD_IND]);
	}
	
	@Test
	public void testOpenAndReadLargeHD() throws InterruptedException, IOException {
		testOpenAndReadLarge(TestUtils.dirs[TestUtils.HD_IND]);
	}

	/*
	 * Tests opening and reading each of the small files, making sure the
	 * contents match.
	 */
	public void testOpenAndReadSmalls(String dir) throws IOException {
		byte[] readBytes = new byte[WRITE_BYTES.length];
		for (int i = 0; i < SPLIT_FILE_NUM; ++i) {
			InputStream in = Files.newInputStream(TestUtils.smallFilePath(dir, i));
			int ret = in.read(readBytes);
			while (ret != -1) {
				assertArrayEquals(WRITE_BYTES, readBytes);
				ret = in.read(readBytes);
			}
			in.close();
		}
	}
	
	@Test
	public void testOpenAndReadSmallsRD() throws InterruptedException, IOException {
		testOpenAndReadSmalls(TestUtils.dirs[TestUtils.RD_IND]);
	}
	
	@Test
	public void testOpenAndReadSmallsHD() throws InterruptedException, IOException {
		testOpenAndReadSmalls(TestUtils.dirs[TestUtils.HD_IND]);
	}

	/*
	 * The fraction of bytes we'll be reading in the fractional read tests.
	 */
	public static final double fractionalRead = 0.50;

	/*
	 * The random number generator used in tests.
	 */
	public static Random rnd = new Random();

	/*
	 * Returns a random offset in the large file for where to start reading.
	 * This is guaranteed to leave at least TOTAL_FILE_SIZE * fractionalRead
	 * bytes in the file.
	 */
	public static int getLargeFileOffset() {
		return rnd.nextInt((int) (TOTAL_FILE_SIZE * (1 - fractionalRead)));
	}

	/*
	 * Tests reading a fraction of the large file from a random offset.
	 */
	public void testReadFractionLargeFile(String dir) throws IOException {
		int offset = getLargeFileOffset();
		byte[] readBytes = new byte[(int) (TOTAL_FILE_SIZE * fractionalRead)];
		byte[] expected = new byte[readBytes.length];
		InputStream in = Files.newInputStream(TestUtils.largeFilePath(dir));
		assertEquals(offset, in.skip(offset));
		assertNotEquals(-1, in.read(readBytes));
		assertArrayEquals(expected, readBytes);
		in.close();
	}
	
	@Test
	public void testReadFractionLargeFileRD() throws InterruptedException, IOException {
		testReadFractionLargeFile(TestUtils.dirs[TestUtils.RD_IND]);
	}
	
	@Test
	public void testReadFractionLargeFileHD() throws InterruptedException, IOException {
		testReadFractionLargeFile(TestUtils.dirs[TestUtils.HD_IND]);
	}
	
	/*
	 * Tests reading a fraction of the small files from a random offset. We will
	 * be reading the same amount as testReadFractionLargeFile, but from the
	 * small files instead of the large ones.
	 */
	public void testReadFractionSmallFiles(String dir) throws IOException {
		int offset = getLargeFileOffset();
		byte[] readBytes = new byte[(int) (TOTAL_FILE_SIZE * fractionalRead)];
		int numReadBytes = 0;
		byte[] expected = new byte[readBytes.length];
		// We have to figure out exactly which small files to read and where
		while (numReadBytes != readBytes.length) {
			// We'll be reading from position offset+numReadBytes mapped onto
			// some small file
			int smallFileIndex = (offset + numReadBytes) / SPLIT_FILE_SIZE;
			int smallFileOffset = (offset + numReadBytes) % SPLIT_FILE_SIZE;
			InputStream in = Files.newInputStream(TestUtils
					.smallFilePath(dir, smallFileIndex));
			in.skip(smallFileOffset);
			// We either read to the end of the file or the number of remaining
			// bytes to get to readBytes.length
			int bytesToRead = Math.min(SPLIT_FILE_SIZE - smallFileOffset,
					readBytes.length - numReadBytes);
			assertEquals(in.read(readBytes, numReadBytes, bytesToRead),
					bytesToRead);
			numReadBytes += bytesToRead;
			in.close();
		}
		assertArrayEquals(expected, readBytes);
	}
	
	@Test
	public void testReadFractionSmallFilesRD() throws InterruptedException, IOException {
		testReadFractionSmallFiles(TestUtils.dirs[TestUtils.RD_IND]);
	}
	
	@Test
	public void testReadFractionSmallFilesHD() throws InterruptedException, IOException {
		testReadFractionSmallFiles(TestUtils.dirs[TestUtils.HD_IND]);
	}
}
