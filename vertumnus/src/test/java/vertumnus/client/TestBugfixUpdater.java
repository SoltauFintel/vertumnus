package vertumnus.client;

public class TestBugfixUpdater extends TestUpdater {

	@Override
	protected Updater getUpdater() {
		Updater u = super.getUpdater();
		u.setKeepMinorVersion(true);
		return u;
	}
	
	@Override
	protected String getExpectedVersion() {
		return "1.0.4";
	}
	
	@Override
	protected long getStage3Size() {
		return 9061;
	}
}
