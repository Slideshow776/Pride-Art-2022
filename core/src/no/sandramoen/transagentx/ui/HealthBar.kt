package no.sandramoen.transagentx.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Array
import no.sandramoen.transagentx.utils.BaseGame

class HealthBar : Table() {
    private var healths: Array<Image>
    private var healthRemaining = -1

    init {
        val health1 = Image(BaseGame.textureAtlas!!.findRegion("heart"))
        val health2 = Image(BaseGame.textureAtlas!!.findRegion("heart"))
        val health3 = Image(BaseGame.textureAtlas!!.findRegion("heart"))
        val health4 = Image(BaseGame.textureAtlas!!.findRegion("heart"))

        healths = Array()
        healths.add(health1)
        healths.add(health2)
        healths.add(health3)
        healths.add(health4)
        healthRemaining = healths.size
        for (i in 0 until healths.size)
            healths[i].color.a = 0f
        addAction(Actions.sequence(
            Actions.delay(.75f),
            Actions.run {
                for (i in 0 until healths.size) {
                    healths[i].color.a = 0f
                    healths[i].addAction(
                        Actions.sequence(
                            Actions.delay(i / 2f),
                            Actions.run { BaseGame.healthUpSound!!.play(BaseGame.soundVolume) },
                            Actions.fadeIn(.5f)
                        )
                    )
                }
            }
        ))

        val healthWidth = Gdx.graphics.width * .03f

        add(health1).width(healthWidth).height(healthWidth).padRight(Gdx.graphics.width * .02f)
        add(health2).width(healthWidth).height(healthWidth).padRight(Gdx.graphics.width * .02f)
        add(health3).width(healthWidth).height(healthWidth).padRight(Gdx.graphics.width * .02f)
        add(health4).width(healthWidth).height(healthWidth)
    }

    fun subtractHealth() {
        if (healthRemaining < 0) {
            Gdx.app.error(
                javaClass.simpleName,
                "Error: cannot subtract health, health is: $healthRemaining"
            )
        } else {
            healths[healthRemaining - 1].addAction(Actions.color(Color.BLACK, .5f))
            healthRemaining--
        }
    }

    fun addHealth(): Boolean {
        if (healthRemaining == healths.size) {
            return false
        } else {
            healths[healthRemaining].addAction(Actions.color(Color.WHITE, .5f))
            healthRemaining++
            return true
        }
    }
}
