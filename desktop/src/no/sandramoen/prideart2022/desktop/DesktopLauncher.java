package no.sandramoen.prideart2022.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.awt.Dimension;

import no.sandramoen.prideart2022.PrideArt2022Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		config.width = (int) (dimension.width * .9f);
		float screenRation = .461538461f;
		config.height = (int) (config.width * screenRation);

		config.vSyncEnabled = true;
		config.useGL30 = true;
		config.title = "Pride Art 2022";
		config.resizable = true;
		// config.fullscreen = true;

		new LwjglApplication(new PrideArt2022Game(), config);
	}
}
