package vertumnus.client;

import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Vertumnus Command Line Interface
 */
public interface VertumnusCLI {

	/** Ist-Version */
	@Option(shortName="v")
	String getVersion();

	/** Modulname */
	@Option(shortName="m")
	String getModule();

	/** Verzeichnis */
	@Option(shortName="d")
	String getDirectory();

	/** Linie (Major, Minor, ...) */
	@Option(shortName="l")
	String getLine();

	/** enthält "1", "2", "3" oder Kombination (z.B. "132") */
	@Option(shortName="s", defaultValue="123")
	String getStage();
	
	@Option(shortName="i", defaultValue="")
	String getInstallFolder();
}
