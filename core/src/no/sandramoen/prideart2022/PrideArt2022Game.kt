package no.sandramoen.prideart2022

import no.sandramoen.prideart2022.screens.gameplay.Level2
import no.sandramoen.prideart2022.screens.gameplay.Level3
import no.sandramoen.prideart2022.screens.gameplay.Level4
import no.sandramoen.prideart2022.screens.gameplay.Level5
import no.sandramoen.prideart2022.screens.shell.SplashScreen
import no.sandramoen.prideart2022.utils.BaseGame

class PrideArt2022Game(appLocale: String) : BaseGame(appLocale) {
    override fun create() {
        super.create()
        // setActiveScreen(MenuScreen())
        // setActiveScreen(OptionsScreen())
        setActiveScreen(SplashScreen())
        // setActiveScreen(Level1())
        // setActiveScreen(Level2())
        // setActiveScreen(Level3())
        // setActiveScreen(Level4())
        // setActiveScreen(Level5())
        // setActiveScreen(SaturnScreen())
        // setActiveScreen(LightspeedScreen())
    }
}
