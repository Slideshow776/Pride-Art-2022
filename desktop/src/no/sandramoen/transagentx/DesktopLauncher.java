package no.sandramoen.transagentx;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import java.awt.Dimension;
import java.util.Locale;


// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Trans Agent X");
		config.setResizable(false);
		config.useVsync(true);
		config.setWindowIcon("images/excluded/icon_16x16.png", "images/excluded/icon_32x32.png");

		boolean isFullscreen = false;
		if (isFullscreen)
			config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		else
			setWindowedMode(.8f, config);

		new Lwjgl3Application(new MyGdxGame(getCountryCode()), config);
	}

	private static String getCountryCode() {
		String countryCode = Locale.getDefault().getCountry().toLowerCase(Locale.ROOT);
		System.out.println("Loading with country code: " + countryCode);
		return countryCode;
	}

	private static void setWindowedMode(float percentOfScreenSize, Lwjgl3ApplicationConfiguration config) {
		Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) (dimension.width * percentOfScreenSize);

		float aspectRatio = 16 / 9f;
		int height = (int) (width / aspectRatio);

		System.out.println("Window dimensions => width: " + width + ", height: " + height);
		config.setWindowedMode(width, height);
	}
}
