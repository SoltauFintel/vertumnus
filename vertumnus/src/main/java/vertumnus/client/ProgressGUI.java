package vertumnus.client;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

/**
 * Window with progress bar
 */
public class ProgressGUI {
	private String title = "Download";
	private String info = "";
	private int lastPercent = 0;
	private JFrame window;
	private JProgressBar progressBar;
	
	/**
	 * Call this method before show().
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Call this method before show().
	 * @param text
	 */
	public void setInfo(String text) {
		info = text;
	}

	/**
	 * Show window
	 */
	public void show() {
		window = new JFrame(title);
		// How can I hide the close window button??
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.setResizable(false);
		Container content = window.getContentPane();
		lastPercent = 0;
		progressBar = new JProgressBar();
		progressBar.setValue(lastPercent);
		progressBar.setStringPainted(true); // show percent also as text
		Border border = BorderFactory.createTitledBorder(info);
		progressBar.setBorder(border);
		content.add(progressBar, BorderLayout.NORTH);
		window.setSize(600, 70);
		window.setLocationRelativeTo(null); // center on screen
		window.setVisible(true);
	}
	
	/**
	 * Hide window
	 */
	public void hide() {
		if (window != null) {
			window.setVisible(false);
			window.dispose();
			window = null;
		}
	}
	
	/**
	 * Update progress bar
	 * @param percent 0..100
	 */
	public void setPercent(int percent) {
		if (progressBar != null && window != null) {
			if (percent < 0) {
				percent = 0;
			}
			if (percent > 100) {
				percent = 100;
			}
			if (percent != lastPercent) {
				progressBar.setValue(percent);
				lastPercent = percent;
			}
		}
	}
}
