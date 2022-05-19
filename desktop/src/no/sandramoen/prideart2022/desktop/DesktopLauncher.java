package no.sandramoen.prideart2022.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.awt.Dimension;

import no.sandramoen.prideart2022.PrideArt2022Game;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

        if (true) {
            config.width = dimension.width;
            config.height = dimension.height;
            config.fullscreen = true;
        } else { // debug
            config.width = (int) (dimension.width * .9f);
            float screenRation = .461538461f;
            config.height = (int) (config.width * screenRation);
        }

        config.vSyncEnabled = true;
        config.useGL30 = false;
        config.title = "Trans Agent X";
        config.resizable = true;
        config.addIcon("images/excluded/icon_32x32.png", Files.FileType.Internal);
        config.addIcon("images/excluded/icon_16x16.png", Files.FileType.Internal);

        new LwjglApplication(new PrideArt2022Game("no"), config);
    }
}
