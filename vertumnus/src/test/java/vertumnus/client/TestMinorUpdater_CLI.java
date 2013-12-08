package vertumnus.client;

public class TestMinorUpdater_CLI extends TestUpdater_CLI {

	@Override
	protected String getExpectedVersion() {
		return "1.1.0";
	}
	
	@Override
	protected String getLine() {
		return "minor";
	}
}
