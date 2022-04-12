package no.sandramoen.prideart2022.myUI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame

class ExperienceBar(x: Float, y: Float, stage: Stage) : BaseActor(0f, 0f, stage) {
    private var progress: BaseActor
    private var label: Label

    private var level = 1
    private var nextLevel = 10f
    private var currentXP = 0f
    private var constant = 2f
    private val ratio = 1.14f

    init {
        loadImage("whitePixel")
        color = Color.BLACK
        setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height * .05f)
        setPosition(x, y - height)

        progress = BaseActor(0f, 0f, stage)
        progress.loadImage("whitePixel")
        progress.color = Color.PINK
        progress.setSize(0f,height)
        addActor(progress)

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
            constant += ratio
            nextLevel += constant
            currentXP = restXP
            percent = currentXP / nextLevel
            label.setText("Level ${++level}")
            BaseGame.playerLevelUpSound!!.play(BaseGame.soundVolume)
        }

        // println("current XP: $currentXP, next level: $nextLevel, constant: $constant")
        progress.addAction(Actions.sizeTo(width * percent, height, .25f))
    }
}
