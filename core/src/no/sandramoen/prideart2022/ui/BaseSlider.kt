package no.sandramoen.prideart2022.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.GameUtils

class BaseSlider(value: String, labelText: String) : Table() {
    val slider = sliderInit(value)
    var stepSize = .1f
    val label = labelInit(labelText)

    init {
        val containerWidth = Gdx.graphics.width * .07f
        val containerHeight = Gdx.graphics.height * .02f
        val containerScaleX = 4f
        val containerScaleY = 4.5f

        val container = Container(slider)
        container.isTransform = true
        container.width = containerWidth * containerScaleX
        container.height = containerHeight * containerScaleY
        container.setOrigin(container.width / 2, container.height / 2)
        container.setScale(containerScaleX, containerScaleY)

        setContainerHoverColor(container, label)

        add(container).width(container.width).height(container.height)
        add(label).width(Gdx.graphics.width * .1f).padLeft(Gdx.graphics.width * .02f)
            .padBottom(Gdx.graphics.height * .015f)
        /*debug = true*/
    }

    private fun sliderInit(value: String): Slider {
        stepSize = .1f
        val slider = Slider(0f, 1f, stepSize, false, BaseGame.skin)
        when (value) {
            "sound" -> slider.value = BaseGame.soundVolume
            "music" -> slider.value = BaseGame.musicVolume
            "voice" -> slider.value = BaseGame.voiceVolume
            else -> Gdx.app.error(
                javaClass.simpleName,
                "Error, value could not be appropriated => $value"
            )
        }
        slider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                BaseGame.musicVolume = slider.value
                when (value) {
                    "sound" -> BaseGame.soundVolume = slider.value
                    "music" -> GameUtils.setMusicVolume(slider.value)
                    "voice" -> BaseGame.voiceVolume = slider.value
                }
                BaseGame.click1Sound!!.play(BaseGame.musicVolume)
                GameUtils.saveGameState()
            }
        })
        return slider
    }

    private fun labelInit(labelText: String): Label {
        val label = Label("$labelText", BaseGame.smallLabelStyle)
        label.setFontScale(1.2f)
        GameUtils.addWidgetEnterExitEffect(label)
        return label
    }

    private fun setContainerHoverColor(container: Container<Slider>, label: Label) {
        container.addListener(object : ClickListener() {
            override fun enter(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                fromActor: Actor?
            ) {
                label.color = BaseGame.lightPink
                BaseGame.hoverOverEnterSound!!.play(BaseGame.soundVolume)
                super.enter(event, x, y, pointer, fromActor)
            }

            override fun exit(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                toActor: Actor?
            ) {
                label.color = Color.WHITE
                super.exit(event, x, y, pointer, toActor)
            }
        })
    }
}
