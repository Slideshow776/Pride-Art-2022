package no.sandramoen.prideart2022.actors.characters.player

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame

class Hair(player: Player) : BaseActor(0f, 0f, player.stage) {
    lateinit var hair0IdleAnimation: Animation<TextureAtlas.AtlasRegion>
    lateinit var hair0RunWESAnimation: Animation<TextureAtlas.AtlasRegion>
    lateinit var hair0RunWENAnimation: Animation<TextureAtlas.AtlasRegion>
    lateinit var hair0RunNAnimation: Animation<TextureAtlas.AtlasRegion>
    lateinit var hair0RunSAnimation: Animation<TextureAtlas.AtlasRegion>
    lateinit var hair0DeathAnimation: Animation<TextureAtlas.AtlasRegion>
    lateinit var hair0SmileAnimation: Animation<TextureAtlas.AtlasRegion>

    private val styles = Array<Int>()
    private var stylesIndex: Int = 0

    private val colors = Array<Color>()
    private var colorIndex: Int = 0

    var activeColor: Color = Color(0.910f, 0.757f, 0.439f, 1f) // blonde
    var activeStyle = 0 // hair

    init {
        setStyles()
        loadAnimation(activeStyle)

        setColors()
        color = activeColor
    }

    fun toggleColor() {
        val color = getNextColor()
        addAction(Actions.color(color, .25f))
        activeColor = color
    }

    fun toggleStyle() {
        val style = getNextStyle()
        if (style == 1) {
            isVisible = false
        } else {
            isVisible = true
            loadAnimation(style)
        }
        activeStyle = style
    }

    private fun setStyles() {
        styles.add(0)
        styles.add(1)
        stylesIndex = styles.size - 1
    }

    private fun getNextStyle(): Int {
        val int = styles[stylesIndex]
        if (stylesIndex < styles.size - 1) stylesIndex++
        else stylesIndex = 0
        return int
    }

    private fun setColors() {
        colors.add(Color(0.302f, 0.169f, 0.196f, 1f))   // brown
        colors.add(Color(0.310f, 0.561f, 0.729f, 1f))   // blue
        colors.add(Color(0.459f, 0.141f, 0.220f, 1f))   // dark red
        colors.add(Color(0.647f, 0.188f, 0.188f, 1f))   // red
        colors.add(Color(0.812f, 0.341f, 0.235f, 1f))   // orange
        colors.add(Color(0.875f, 0.518f, 0.647f, 1f))   // light pink
        colors.add(Color(0.635f, 0.243f, 0.549f, 1f))   // pink
        colors.add(Color(0.478f, 0.212f, 0.482f, 1f))   // purple
        colors.add(Color(0.659f, 0.792f, 0.345f, 1f))   // light green
        colors.add(Color(0.275f, 0.510f, 0.196f, 1f))   // green
        colors.add(Color(0.098f, 0.200f, 0.176f, 1f))   // dark green
        colors.add(Color(0.922f, 0.929f, 0.914f, 1f))   // white
        colors.add(Color(0.082f, 0.114f, 0.157f, 1f))   // black
        colors.add(Color(0.910f, 0.757f, 0.439f, 1f))   // blonde
    }

    private fun getNextColor(): Color {
        val color = colors[colorIndex]
        if (colorIndex < colors.size - 1) colorIndex++
        else colorIndex = 0
        return color
    }

    private fun loadAnimation(number: Int) {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/hair/hair $number/idle1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/hair/hair $number/idle1"))
        hair0IdleAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/hair/hair $number/runWES1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/hair/hair $number/runWES2"))
        hair0RunWESAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/hair/hair $number/runWEN1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/hair/hair $number/runWEN2"))
        hair0RunWENAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/hair/hair $number/runN1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/hair/hair $number/runN2"))
        hair0RunNAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/hair/hair $number/runS1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/hair/hair $number/runS2"))
        hair0RunSAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/hair/hair $number/death1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/hair/hair $number/death2"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/hair/hair $number/death3"))
        hair0DeathAnimation = Animation(.2f, animationImages)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/hair/hair $number/smile1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/hair/hair $number/smile2"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/hair/hair $number/smile3"))
        hair0SmileAnimation = Animation(.2f, animationImages)
        animationImages.clear()
    }
}
