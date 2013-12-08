package vertumnus.client;

import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Vertumnus Command Line Interface
 */
public interface VertumnusCLI {

	/** Current version */
	@Option(shortName="v")
	String getVersion();

	/** Module name */
	@Option(shortName="m")
	String getModule();

	/** Directory */
	@Option(shortName="d")
	String getDirectory();

	/** Line (major, minor, ...) */
	@Option(shortName="l")
	String getLine();

	/** contains "1", "2", "3" or a combination (e.g. "132") */
	@Option(shortName="s", defaultValue="123")
	String getStage();
	
	@Option(shortName="i", defaultValue="")
	String getInstallFolder();
}
