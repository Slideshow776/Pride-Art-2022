package no.sandramoen.prideart2022

import no.sandramoen.prideart2022.screens.gameplay.Level1
import no.sandramoen.prideart2022.screens.shell.EpilogueScreen
import no.sandramoen.prideart2022.screens.shell.MenuScreen
import no.sandramoen.prideart2022.screens.shell.SandraSplashScreen
import no.sandramoen.prideart2022.screens.shell.SkeiveSpillutviklereSplashScreen
import no.sandramoen.prideart2022.utils.BaseGame

class PrideArt2022Game(appLocale: String) : BaseGame(appLocale) {
    override fun create() {
        super.create()
        setActiveScreen(SandraSplashScreen())
        // setActiveScreen(SkeiveSpillutviklereSplashScreen())
        // setActiveScreen(MenuScreen())
        // setActiveScreen(OptionsScreen())
        // setActiveScreen(Level1())
        // setActiveScreen(Level2())
        // setActiveScreen(Level3())
        // setActiveScreen(Level4())
        // setActiveScreen(Level5())
        // setActiveScreen(SaturnScreen())
        // setActiveScreen(LightspeedScreen())
        // setActiveScreen(EpilogueScreen())
    }
}
