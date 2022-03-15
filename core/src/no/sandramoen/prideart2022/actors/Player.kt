package no.sandramoen.prideart2022.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.prideart2022.utils.BaseActor

class Player(stage: Stage) : BaseActor(0f, 0f, stage) {
    init {
        loadImage("ghost")
        setSize(4f, 4f)
        centerAtPosition(100f, 100f)
        color = Color.CYAN
        debug = true

        setAcceleration(200f)
        setMaxSpeed(25f)
        setDeceleration(200f)

        zoomCamera(.5f)
    }

    override fun act(dt: Float) {
        super.act(dt)

        controls()
        applyPhysics(dt)

        boundToWorld()
        alignCamera(lerp = .1f)
    }

    private fun controls() {
        if (Gdx.input.isKeyPressed(Keys.W)) {
            accelerateAtAngle(90f)
        }
        if (Gdx.input.isKeyPressed(Keys.A)) {
            accelerateAtAngle(180f)
        }
        if (Gdx.input.isKeyPressed(Keys.S)) {
            accelerateAtAngle(270f)
        }
        if (Gdx.input.isKeyPressed(Keys.D)) {
            accelerateAtAngle(0f)
        }
    }
}
