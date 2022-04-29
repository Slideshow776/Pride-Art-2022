package no.sandramoen.prideart2022.actors.characters.player

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array
import no.sandramoen.prideart2022.utils.BaseActor
import no.sandramoen.prideart2022.utils.BaseGame

class Skin(player: Player) : BaseActor(0f, 0f, player.stage) {
    lateinit var runWESAnimation: Animation<TextureAtlas.AtlasRegion>
    lateinit var runWENAnimation: Animation<TextureAtlas.AtlasRegion>
    lateinit var runNAnimation: Animation<TextureAtlas.AtlasRegion>
    lateinit var runSAnimation: Animation<TextureAtlas.AtlasRegion>
    lateinit var idleAnimation: Animation<TextureAtlas.AtlasRegion>
    lateinit var deathAnimation: Animation<TextureAtlas.AtlasRegion>
    lateinit var smileAnimation: Animation<TextureAtlas.AtlasRegion>

    private val colors = Array<Color>()
    private var colorIndex: Int = 0

    var activeColor: Color = Color(0.906f, 0.835f, 0.702f, 1f) // blonde, TODO

    init {
        setColors()
        loadAnimation()
        color = activeColor
    }

    fun toggleColor() {
        val color = getNextColor()
        addAction(Actions.color(color, .25f))
        activeColor = color
    }

    private fun setColors() {
        colors.add(Color(0.906f, 0.835f, 0.702f, 1f)) // light
        colors.add(Color(0.843f, 0.710f, 0.580f, 1f)) // darker light
        colors.add(Color(0.753f, 0.580f, 0.451f, 1f)) // darkest light
        colors.add(Color(0.753f, 0.580f, 0.451f, 1f)) // darkest light
        colors.add(Color(0.678f, 0.467f, 0.341f, 1f)) // light dark
        colors.add(Color(0.478f, 0.282f, 0.255f, 1f)) // darker dark
        colors.add(Color(0.302f, 0.169f, 0.196f, 1f)) // darkest dark
    }

    private fun getNextColor(): Color {
        val color = colors[colorIndex]
        if (colorIndex < colors.size - 1) colorIndex++
        else colorIndex = 0
        return color
    }

    private fun loadAnimation() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()

        for (i in 1..20)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("player/skin/idle1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/skin/idle2"))
        idleAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/skin/runWES1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/skin/runWES2"))
        runWESAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/skin/runWEN1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/skin/runWEN2"))
        runWENAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/skin/runN1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/skin/runN2"))
        runNAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/skin/runS1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/skin/runS2"))
        runSAnimation = Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/skin/death1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/skin/death2"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/skin/death3"))
        deathAnimation = Animation(.2f, animationImages)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/skin/smile1"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/skin/smile2"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player/skin/smile3"))
        smileAnimation = Animation(.2f, animationImages)
        animationImages.clear()

        setAnimation(idleAnimation)
    }
}
