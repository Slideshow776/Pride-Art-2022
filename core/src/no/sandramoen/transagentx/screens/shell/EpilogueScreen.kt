package no.sandramoen.transagentx.screens.shell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.TypingLabel
import no.sandramoen.transagentx.utils.BaseActor
import no.sandramoen.transagentx.utils.BaseGame
import no.sandramoen.transagentx.utils.BaseScreen

class EpilogueScreen : BaseScreen() {
    override fun initialize() {
        val skeiveSpillutviklere = BaseActor(0f, 0f, uiStage)
        skeiveSpillutviklere.loadImage("skeiveSpillutviklere")
        skeiveSpillutviklere.color.a = 0f
        skeiveSpillutviklere.setSize(Gdx.graphics.width * .5f, Gdx.graphics.height * .5f)
        skeiveSpillutviklere.centerAtPosition(Gdx.graphics.width * .5f, Gdx.graphics.height * .5f)

        val label = TypingLabel(
            BaseGame.myBundle!!.get("epilogue0"),
            BaseGame.smallLabelStyle
        )
        label.setAlignment(Align.center)
        label.wrap = true
        label.addAction(Actions.sequence(
            Actions.delay(11f),
            Actions.run {
                label.restart()
                label.setText(BaseGame.myBundle!!.get("epilogue1"))
            },
            Actions.delay(10f),
            Actions.run {
                label.addAction(Actions.fadeOut(1f))
                skeiveSpillutviklere.addAction(Actions.fadeIn(2f))
            },
            Actions.delay(3f),
            Actions.run { skeiveSpillutviklere.addAction(Actions.fadeOut(2f)) },
            Actions.delay(3f),
            Actions.run { BaseGame.setActiveScreen(MenuScreen()) }
        ))
        uiTable.add(label).grow().width(Gdx.graphics.width * .98f)

        val saturn = BaseActor(-5f, -10f, mainStage)
        saturn.loadImage("saturn")
        saturn.color.a = 0f
        saturn.addAction(Actions.fadeIn(1f))
        saturn.setScale(0f)
        saturn.addAction(Actions.scaleTo(1f, 1f, 27f))
    }

    override fun update(dt: Float) {}
}
