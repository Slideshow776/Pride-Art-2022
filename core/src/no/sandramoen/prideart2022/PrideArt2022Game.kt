package no.sandramoen.prideart2022

import no.sandramoen.prideart2022.screens.gameplay.Level1
import no.sandramoen.prideart2022.screens.gameplay.Level2
import no.sandramoen.prideart2022.utils.BaseGame

class PrideArt2022Game(appLocale: String) : BaseGame(appLocale) {
    override fun create() {
        super.create()
        // setActiveScreen(MenuScreen())
        // setActiveScreen(OptionsScreen())
        // setActiveScreen(SplashScreen())
        // setActiveScreen(Level1())
        setActiveScreen(Level2())
        // setActiveScreen(IntroSaturnScreen())
        // setActiveScreen(IntroLightspeedScreen())
    }
}
