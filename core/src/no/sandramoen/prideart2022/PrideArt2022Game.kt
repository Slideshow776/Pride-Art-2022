package no.sandramoen.prideart2022

import no.sandramoen.prideart2022.screens.gameplay.*
import no.sandramoen.prideart2022.screens.shell.MenuScreen
import no.sandramoen.prideart2022.screens.shell.SplashScreen
import no.sandramoen.prideart2022.screens.shell.intro.LightspeedScreen
import no.sandramoen.prideart2022.utils.BaseGame

class PrideArt2022Game(appLocale: String) : BaseGame(appLocale) {
    override fun create() {
        super.create()
        // setActiveScreen(SplashScreen())
        // setActiveScreen(MenuScreen())
        // setActiveScreen(OptionsScreen())
        setActiveScreen(Level1())
        // setActiveScreen(Level2())
        // setActiveScreen(Level3())
        // setActiveScreen(Level4())
        // setActiveScreen(Level5())
        // setActiveScreen(SaturnScreen())
        // setActiveScreen(LightspeedScreen())
    }
}
