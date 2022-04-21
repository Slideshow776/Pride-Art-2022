package no.sandramoen.prideart2022.screens.shell.intro

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.prideart2022.screens.gameplay.Level1
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.BaseScreen

class RikshospitaletScreen : BaseScreen() {
    /*private val camera = mainStage.camera as OrthographicCamera*/

    override fun initialize() {
        val earth = BaseActor(0f, 0f, uiStage)
        earth.loadImage("rikshospitalet")
        earth.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        /*camera.position.set(Vector3(earth.width * .5f, earth.height * .5f, 0f))*/

        BaseActor(0f, 0f, uiStage).addAction(
            Actions.sequence(
            Actions.delay(1f),
            Actions.run { BaseGame.setActiveScreen(Level1()) }
        ))
    }

    override fun update(dt: Float) {}

    override fun keyDown(keycode: Int): Boolean {
        BaseGame.setActiveScreen(Level1())
        return super.keyDown(keycode)
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        BaseGame.setActiveScreen(Level1())
        return super.buttonDown(controller, buttonCode)
    }
}
