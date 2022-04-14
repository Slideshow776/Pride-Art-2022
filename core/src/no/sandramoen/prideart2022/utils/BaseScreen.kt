package no.sandramoen.prideart2022.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import no.sandramoen.prideart2022.actors.TilemapActor
import no.sandramoen.prideart2022.utils.GameUtils.Companion.initShaderProgram

abstract class BaseScreen : Screen, InputProcessor, ControllerListener {
    protected var mainStage: Stage
    protected var uiStage: Stage
    protected var uiTable: Table
    var dtModifier = 1f

    init {
        mainStage = Stage()
        mainStage.viewport = ExtendViewport(100f, 100f)

        uiTable = Table()
        uiTable.setFillParent(true)
        uiStage = Stage()
        uiStage.addActor(uiTable)
        uiStage.viewport = ScreenViewport()
        initialize()
    }

    abstract fun initialize()
    abstract fun update(dt: Float)

    override fun render(dt: Float) {
        uiStage.act(dt)
        mainStage.act(dt * dtModifier)
        update(dt * dtModifier)

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
        Controllers.addListener(this)
    }

    override fun hide() {
        val im: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer
        im.removeProcessor(this)
        im.removeProcessor(uiStage)
        im.removeProcessor(mainStage)
        Controllers.removeListener(this)
    }

    override fun resize(width: Int, height: Int) {
        mainStage.viewport.update(width, height)
        uiStage.viewport.update(width, height, true)
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

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean { return false }
    override fun buttonUp(controller: Controller?, buttonCode: Int): Boolean  { return false }
    override fun axisMoved(controller: Controller?, axisCode: Int, value: Float): Boolean  { return false }
    override fun connected(controller: Controller?) {}
    override fun disconnected(controller: Controller?) {}
}
