package vertumnus.maker;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestFTPUpload.class,
	TestZip.class,
	TestUploader.class
} )
public class AllTests {
}
