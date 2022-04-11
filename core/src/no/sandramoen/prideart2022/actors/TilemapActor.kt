package no.sandramoen.prideart2022.actors

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import no.sandramoen.prideart2022.utils.BaseActor

class TilemapActor(private val tiledMap: TiledMap?, stage: Stage) : Actor() {
    companion object {
        const val unitScale = .15f
        const val zIndex = 0
    }

    private var tiledMapRenderer: OrthoCachedTiledMapRenderer

    init {
        println("-------------------------------- $zIndex")
        val tileWidth = tiledMap!!.properties.get("tilewidth") as Int
        val tileHeight = tiledMap.properties.get("tileheight") as Int
        val numTilesHorizontal = tiledMap.properties.get("width") as Int
        val numTilesVertical = tiledMap.properties.get("height") as Int
        val mapWidth = tileWidth * numTilesHorizontal
        val mapHeight = tileHeight * numTilesVertical

        BaseActor.setWorldBounds(mapWidth.toFloat() * unitScale, mapHeight.toFloat() * unitScale)
        tiledMapRenderer = OrthoCachedTiledMapRenderer(tiledMap, unitScale)
        tiledMapRenderer.setBlending(true)
        stage.addActor(this)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        tiledMapRenderer.setView(stage.camera as OrthographicCamera)
        tiledMapRenderer.render()
    }

    fun getRectangleList(propertyName: String): Array<MapObject> {
        val list = Array<MapObject>()
        for (layer in tiledMap!!.layers) {
            for (obj in layer.objects) {
                if (obj !is RectangleMapObject)
                    continue

                if (obj.name == propertyName)
                    list.add(obj)
            }
        }
        return list
    }

    fun getTileList(propertyName: String): ArrayList<MapObject> {
        val list = ArrayList<MapObject>()

        for (layer: MapLayer in tiledMap!!.layers) {
            for (obj: MapObject in layer.objects) {
                if (obj !is TiledMapTileMapObject)
                    continue

                val props = obj.properties

                // Default MapProperties are stored within associated Tile object
                // Instance-specific overrides are stored in MapObject

                val tmtmo: TiledMapTileMapObject = obj
                val t = tmtmo.tile
                val defaultProps = t.properties

                if (defaultProps.containsKey("name") && defaultProps.get("name") == propertyName)
                    list.add(obj)

                // get list of default property keys
                val propertyKeys = defaultProps.keys

                // iterate over keys; copy default values into props if needed
                while (propertyKeys.hasNext()) {
                    val key = propertyKeys.next()

                    // check if value already exists; if not, create property with default value
                    if (props.containsKey(key)) {
                        continue
                    } else {
                        val value = defaultProps.get(key)
                        props.put(key, value)
                    }
                }
            }
        }
        return list
    }
}
