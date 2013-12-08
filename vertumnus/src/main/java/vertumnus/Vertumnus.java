package vertumnus;

import uk.co.flamingpenguin.jewel.cli.CliFactory;
import vertumnus.client.Updater;
import vertumnus.client.VertumnusCLI;

/**
 * Vertumnus Main Class: Call Updater
 */
public class Vertumnus {
	public static final String VERSION = "1.0.0";
	public static boolean debug = false;
	private static StringBuilder sb;

	public static void main(String[] args) {
		try {
			sb = new StringBuilder(); // important, must be executed in this method
			System.out.println("VERTUMNUS " + VERSION + " - Live Update");
			VertumnusCLI cli = CliFactory.parseArguments(VertumnusCLI.class, args);
			Updater u = new Updater();
			u.setDirectory(cli.getDirectory());
			if ("bugfix".equalsIgnoreCase(cli.getLine())) {
				u.setKeepMinorVersion(true);
			} else if ("minor".equalsIgnoreCase(cli.getLine())) {
				u.setKeepMajorVersion(true);
			}
			u.setGUI(cli.isGui());
			String filename = null, nv = null;
			if (cli.getStage().contains("1")) {
				nv = u.getNextVersion(cli.getModule(), cli.getVersion());
				if (nv == null) {
					exit(-2); return;
				}
				sb.append("NV=" + nv + ";");
				System.out.println("Update to " + nv); // TODO NLS
			}
			if (nv != null && cli.getStage().contains("2")) {
				filename = u.load();
				sb.append("dn=" + filename + ";");
			}
			if (filename != null && cli.getStage().contains("3")) {
				u.install(cli.getInstallFolder());
				sb.append("installed;");
			}
		} catch (Exception e) {
			e.printStackTrace();
			sb.append("ERR=" + e.getClass().getSimpleName() + ":" + e.getMessage() + ";");
			exit(-1); return;
		}
	}
	
	private static void exit(int code) {
		sb.append("exit=" + code + ";");
		if (!debug) {
			System.exit(-2);
		}
	}

	public static String getResult() {
		return sb.toString();
	}
}
