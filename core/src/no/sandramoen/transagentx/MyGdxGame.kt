package no.sandramoen.transagentx

import no.sandramoen.transagentx.screens.shell.MenuScreen
import no.sandramoen.transagentx.utils.BaseGame

class MyGdxGame(appLocale: String) : BaseGame(appLocale) {
    override fun create() {
        super.create()
        // setActiveScreen(SandraSplashScreen())
        // setActiveScreen(SkeiveSpillutviklereSplashScreen())
        setActiveScreen(MenuScreen())
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
