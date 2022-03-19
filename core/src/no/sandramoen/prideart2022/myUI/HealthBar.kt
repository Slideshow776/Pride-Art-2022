package no.sandramoen.prideart2022.myUI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Array
import no.sandramoen.prideart2022.utils.BaseGame

class HealthBar : Table() {
    private var healths: Array<Image>
    private var numHealths = -1

    var padding = -1f

    init {
        val health1 = Image(BaseGame.textureAtlas!!.findRegion("heart"))
        val health2 = Image(BaseGame.textureAtlas!!.findRegion("heart"))
        val health3 = Image(BaseGame.textureAtlas!!.findRegion("heart"))

        healths = Array<Image>()
        healths.add(health1)
        healths.add(health2)
        healths.add(health3)
        numHealths = healths.size
        for (i in 0 until healths.size) {
            healths[i].color.a = 0f
            healths[i].addAction(
                Actions.sequence(
                    Actions.delay(i / 2f), // bigger number yields faster animations
                    Actions.fadeIn(.5f)
                )
            )
        }
        val healthWidth = Gdx.graphics.width * .03f

        add(health1).width(healthWidth).height(healthWidth).padRight(Gdx.graphics.width * .02f)
        add(health2).width(healthWidth).height(healthWidth).padRight(Gdx.graphics.width * .02f)
        add(health3).width(healthWidth).height(healthWidth)
    }

    fun subtractHealth() {
        healths[numHealths-1].addAction(Actions.fadeOut(.5f))
        numHealths--
    }
}