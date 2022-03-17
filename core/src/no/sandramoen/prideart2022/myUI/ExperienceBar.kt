package no.sandramoen.prideart2022.myUI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import kotlin.math.ceil

class ExperienceBar(x: Float, y: Float, stage: Stage) : BaseActor(0f, 0f, stage) {
    private var increment: BaseActor
    private var label: Label

    private var level = 1
    private var nextLevel = 10f
    private var currentXP = 0f
    private var constant = 2f
    private val ration = 1.14f

    init {
        loadImage("whitePixel")
        color = Color.BLACK
        setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height * .05f)
        setPosition(x, y - height)

        increment = BaseActor(0f, 0f, stage)
        increment.loadImage("whitePixel")
        increment.color = Color.PINK
        increment.setSize(0f,height)
        addActor(increment)

        label = Label("Level $level", BaseGame.smallLabelStyle)
        label.setFontScale(.5f)
        label.setPosition(width - label.prefWidth * 1.2f, 0f)
        addActor(label)
    }

    fun increment(number: Int) {
        currentXP += number
        nearArithmeticProgression()
    }

    private fun nearArithmeticProgression() {
        var percent = currentXP / nextLevel
        if (percent >= 1) {
            val restXP = (percent - 1) * nextLevel
            nextLevel += constant * ration
            currentXP = restXP
            percent = currentXP / nextLevel
            label.setText("Level ${++level}")
            BaseGame.playerLevelUpSound!!.play(BaseGame.soundVolume)
        }

        // println("current XP: $currentXP, next level: $nextLevel")
        increment.setSize(width * percent, height)
    }
}
