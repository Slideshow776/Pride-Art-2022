package no.sandramoen.prideart2022

import no.sandramoen.sandbox.screens.LevelScreen
import no.sandramoen.prideart2022.utils.BaseGame

class PrideArt2022Game() : BaseGame() {
    override fun create() {
        super.create()
        setActiveScreen(LevelScreen())
    }
}
