package no.sandramoen.prideart2022.actors.characters.lost

import com.badlogic.gdx.scenes.scene2d.Stage

class Lost2(x: Float, y: Float, stage: Stage) : BaseLost(x, y, stage) {
    init {
        initializeIdleAnimation(2)
    }
}
