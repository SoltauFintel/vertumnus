package vertumnus.client;

public class TestBugfixUpdater_CLI extends TestUpdater_CLI {

	@Override
	protected String getExpectedVersion() {
		return "1.0.4";
	}
	
	@Override
	protected String getLine() {
		return "bugfix";
	}
}
