package vertumnus.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import vertumnus.client.version.TestCompareVersion;
import vertumnus.client.version.TestWhatVersion;

// It's possible that you must run Preparation before running this test suite.
// It's normal that the error message "File doesn't exist or is not downloadable: .../directory-does-not-exist.xml"
// is displayed in the console. Only the test result is relevant.
@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestCompareVersion.class,
	TestWhatVersion.class,
	TestUpdater.class,
	TestMinorUpdater.class,
	TestBugfixUpdater.class,
	TestUpdater_CLI.class,
	TestMinorUpdater_CLI.class,
	TestBugfixUpdater_CLI.class
} )
public class AllTests {
}
