package no.sandramoen.prideart2022.screens.shell

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.rafaskoberg.gdx.typinglabel.TypingLabel
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame
import no.sandramoen.prideart2022.utils.BaseScreen

class EpilogueScreen : BaseScreen() {
    override fun initialize() {
        val label = TypingLabel(
            BaseGame.myBundle!!.get("epilogue0"),
            BaseGame.smallLabelStyle
        )
        label.setAlignment(Align.center)
        label.addAction(Actions.sequence(
            Actions.delay(11f),
            Actions.run {
                label.restart()
                label.setText(BaseGame.myBundle!!.get("epilogue1"))
            },
            Actions.delay(10f),
            Actions.run { BaseGame.setActiveScreen(MenuScreen()) }
        ))
        uiTable.add(label).grow()

        val saturn = BaseActor(-5f, -10f, mainStage)
        saturn.loadImage("saturn")
        saturn.color.a = 0f
        saturn.addAction(Actions.fadeIn(1f))
        saturn.setScale(0f)
        saturn.addAction(Actions.scaleTo(1f, 1f, 18f))
    }

    override fun update(dt: Float) {}
}
