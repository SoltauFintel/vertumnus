package vertumnus.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import vertumnus.client.version.TestCompareVersion;
import vertumnus.client.version.TestWhatVersion;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestCompareVersion.class,
	TestWhatVersion.class,
	TestUpdater.class,
	TestMinorUpdater.class,
	TestUpdater_CLI.class,
	TestMinorUpdater_CLI.class
} )
public class AllTests {
}
