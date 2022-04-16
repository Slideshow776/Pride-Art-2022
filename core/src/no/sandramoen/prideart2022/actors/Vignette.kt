package no.sandramoen.prideart2022.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import no.sandramoen.prideart2022.utils.BaseActor

class Vignette(stage: Stage) : BaseActor(0f, 0f, stage) {
    init {
        loadImage("vignette")
        setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        touchable = Touchable.disabled
    }
}
