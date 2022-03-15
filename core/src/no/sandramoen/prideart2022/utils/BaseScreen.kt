package no.sandramoen.prideart2022.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport

abstract class BaseScreen : Screen, InputProcessor {
    protected var mainStage: Stage
    protected var uiStage: Stage
    protected var uiTable: Table

    init {
        uiTable = Table()
        uiTable.setFillParent(true)
        uiStage = Stage()
        uiStage.addActor(uiTable)
        uiStage.viewport = ScreenViewport()

        mainStage = Stage()
        mainStage.viewport = ExtendViewport(100f, 100f)

        initialize()
    }

    abstract fun initialize()
    abstract fun update(dt: Float)

    override fun render(dt: Float) {
        uiStage.act(dt)
        mainStage.act(dt)
        update(dt)

        Gdx.gl.glClearColor(1f, 0.8f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        mainStage.viewport.apply()
        mainStage.draw()

        uiStage.viewport.apply()
        uiStage.draw()
    }

    override fun show() {
        val im: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer
        im.addProcessor(this)
        im.addProcessor(uiStage)
        im.addProcessor(mainStage)
    }

    override fun hide() {
        val im: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer
        im.removeProcessor(this)
        im.removeProcessor(uiStage)
        im.removeProcessor(mainStage)
    }

    override fun resize(width: Int, height: Int) {
        mainStage.viewport.update(width, height)
    }

    override fun pause() {}
    override fun resume() {}
    override fun dispose() {}

    override fun keyDown(keycode: Int): Boolean { return false }
    override fun keyUp(keycode: Int): Boolean { return false }
    override fun keyTyped(character: Char): Boolean { return false }
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean { return false }
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean { return false }
    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean { return false }
    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean { return false }
    override fun scrolled(amountX: Float, amountY: Float): Boolean { return false }
}
