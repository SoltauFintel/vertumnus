package vertumnus.client;

public class TestMinorUpdater extends TestUpdater {

	@Override
	protected Updater getUpdater() {
		Updater u = super.getUpdater();
		u.setKeepMajorVersion(true);
		return u;
	}

	@Override
	protected String getExpectedVersion() {
		return "1.1.0";
	}
	
	@Override
	protected long getStage3Size() {
		return 9033;
	}
}
