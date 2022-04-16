package no.sandramoen.prideart2022.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.utils.Align
import no.sandramoen.prideart2022.utils.BaseGame

class ControllerMessage : Table() {
    private var label: Label
    private var image: Image

    init {
        color.a = 0f

        image = initializeImage()
        label = Label("", BaseGame.smallLabelStyle)

        add(image).row()
        add(label)
    }

    fun showConnected() {
        changeImageTo("controllerConnected")
        setLabel(BaseGame.myBundle!!.get("controllerConnected"), Color(0.659f, 0.792f, 0.345f, 1f)) // green
        fadeInAndOut()
    }

    fun showDisConnected() {
        changeImageTo("controllerDisconnected")
        setLabel(BaseGame.myBundle!!.get("controllerDisconnected"), Color(0.812f, 0.341f, 0.235f, 1f)) // red
        fadeIn()
    }

    fun showNoControllerFound() {
        changeImageTo("controllerDisconnected")
        setLabel(BaseGame.myBundle!!.get("noControllerFound"), Color(0.812f, 0.341f, 0.235f, 1f)) // red
        fadeInAndOut()
    }

    fun fadeIn() = addAction(Actions.fadeIn(.5f))

    fun fadeOut() = addAction(
        Actions.after(
            Actions.sequence(
                Actions.delay(2f),
                Actions.fadeOut(.5f)
            )
        )
    )

    private fun setLabel(text: String, color: Color) {
        label.setText(text)
        label.color = color
    }

    private fun fadeInAndOut() {
        addAction(
            Actions.sequence(
                Actions.fadeIn(.5f),
                Actions.delay(2f),
                Actions.fadeOut(.5f)
            )
        )
    }

    private fun changeImageTo(region: String) {
        image.drawable = SpriteDrawable(Sprite(BaseGame.textureAtlas!!.findRegion(region)))
    }

    private fun initializeImage(): Image {
        val image = Image(BaseGame.textureAtlas!!.findRegion("controllerConnected"))
        image.setScale(4f)
        image.setOrigin(Align.bottom)
        return image
    }
}
