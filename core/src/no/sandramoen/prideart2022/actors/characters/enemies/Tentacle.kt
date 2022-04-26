package no.sandramoen.prideart2022.actors.characters.enemies

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.utils.BaseActor

class Tentacle(x: Float, y: Float, stage: Stage, bossKG: BossKG) : BaseActor(x, y, stage) {
    init {
        loadImage("enemies/tentacle0")
        setOrigin(Align.center)
        rotation = -45f
        zIndex = bossKG.zIndex - 1
        debug = true
    }
}
