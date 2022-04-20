package no.sandramoen.prideart2022

import no.sandramoen.prideart2022.screens.gameplay.LevelScreen
import no.sandramoen.prideart2022.screens.shell.MenuScreen
import no.sandramoen.prideart2022.utils.BaseGame

class PrideArt2022Game(appLocale: String) : BaseGame(appLocale) {
    override fun create() {
        super.create()
        setActiveScreen(MenuScreen())
        // setActiveScreen(OptionsScreen())
        // setActiveScreen(SplashScreen())
        // setActiveScreen(LevelScreen())
        // setActiveScreen(IntroSaturnScreen())
        // setActiveScreen(IntroLightspeedScreen())
    }
}
