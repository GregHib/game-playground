package world.gregs.game.playground.ai.iaus

import javafx.scene.paint.Color
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.App
import tornadofx.View
import tornadofx.launch
import tornadofx.text
import world.gregs.game.playground.BooleanGrid
import world.gregs.game.playground.ai.iaus.bot.*
import world.gregs.game.playground.ai.iaus.bot.behaviour.SimpleBehaviour
import world.gregs.game.playground.ai.iaus.bot.record.*
import world.gregs.game.playground.ai.iaus.bot.record.AreaRecords.*
import world.gregs.game.playground.ai.iaus.bot.record.PlayerRecords.*
import world.gregs.game.playground.ai.iaus.world.*
import world.gregs.game.playground.ai.iaus.world.action.Action
import world.gregs.game.playground.ai.iaus.world.action.ActionsProvider
import world.gregs.game.playground.ai.iaus.world.action.area
import world.gregs.game.playground.ai.iaus.world.action.obj
import world.gregs.game.playground.chebyshev
import world.gregs.game.playground.spatial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.GridCanvas
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle
import kotlin.random.Random

class PlayerAIView : View("Player AI view") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
        private const val maxDistance = 100.0
        const val speed = 10L
        var debug = false
    }

    private val agent = Agent("player", 0, 0, Color.BLUE)
    private val world = Area("world", 0, 0, 32, 32, Color.TRANSPARENT)
    private val forest = Area("forest", 9, 9, 8, 8, Color.rgb(0, 100, 0, 0.25))
    private val shop = Area("axe shop", 19, 2, 8, 5, Color.rgb(100, 100, 0, 0.25))
    private val shed = Area("woodshed", 20, 9, 8, 5, Color.rgb(100, 50, 50, 0.25))
    private val areas: List<Area> = mutableListOf(world, forest, shop, shed)

    private val provider = ActionsProvider()

    init {
        setupWorld()

        setupActionHandlers()

        val distanceTo = consider { agent: Agent, obj: Coordinates ->
            chebyshev(agent.x, agent.y, obj.x, obj.y).scale(0.0, maxDistance).inverse()
        }

        val hasSkillToUse = considerBool { agent: Agent, obj: GameObject ->
            val skill: Int = agent[Skill]
            val required: Int = obj[Skill]
            skill >= required
        }

        val skillEfficiency = consider { agent: Agent, obj: GameObject ->
            obj.getDouble(Skill).scale(0.1, agent.getDouble(Skill))
        }

        val hasTooManyLogs = considerBool { agent: Agent, _: Any ->
            agent.bag.getOrDefault("logs", 0) >= 4
        }

        val hasAxe = considerBool { agent: Agent, _: Any ->
            agent.bag.getOrDefault("axe", 0) > 0
        }

        val hasNoAxe = considerBool { agent: Agent, _: Any ->
            agent.bag.getOrDefault("axe", 0) == 0
        }

        val choppingMomentum = consider { agent: Agent, _: Any ->
            agent.getDouble(ChoppingMomentum)
        }

        fun goToArea(predicate: (Area) -> Boolean, considerations: Set<(Agent, Any) -> Double>): SimpleBehaviour {
            return SimpleBehaviour(
                name = "go to",
                targets = { areas.filter(predicate) },
                considerations = considerations.toMutableSet().apply {
                    add(distanceTo)
                },
                weight = 0.8,
                action = Action.MoveToArea
            )
        }

        val idle = SimpleBehaviour(
            name = "idle",
            weight = 0.001,
            momentum = 1.0,
            action = Action.Idle
        )

        val findAxeArea = goToArea(
            predicate = { it[HasAxes] },
            considerations = setOf(
                hasNoAxe,
                considerBool { agent, _: Any ->
                    !agent.area.getBoolean(HasAxes)
                },
                choppingMomentum
            )
        )

        val pickupAxe = SimpleBehaviour(
            name = "pickup",
            targets = { agent -> agent.area.objects.filter { it.name.contains("axe", true) } },
            considerations = setOf(
                distanceTo,
                hasNoAxe,
                hasSkillToUse,
                skillEfficiency,
                choppingMomentum
            ),
            action = Action.Pickup
        )

        val findTreeArea = goToArea(
            predicate = { it[HasTrees] },
            considerations = setOf(
                hasAxe,
                considerBool { agent, _: Any ->
                    !agent.area.getBoolean(HasTrees)
                },
                choppingMomentum,
            )
        )

        val chopTree = SimpleBehaviour(
            name = "chop down tree",
            targets = { agent -> agent.area.objects.filter { it.name.contains("tree", true) && it.colour == Color.GREEN } },
            considerations = setOf(
                hasAxe,
                distanceTo,
                hasSkillToUse,
                skillEfficiency,
                choppingMomentum
            ),
            action = Action.Chop
        )

        val findShedArea = goToArea(
            predicate = { it[HasDeposits] },
            considerations = setOf(
                hasTooManyLogs,
                considerBool { agent, _: Any ->
                    !agent.area.getBoolean(HasDeposits)
                },
                choppingMomentum,
            )
        )

        val depositLogs = SimpleBehaviour(
            name = "deposit logs",
            targets = { agent -> agent.area.objects.filter { it.name.startsWith("store", true) } },
            considerations = mutableSetOf(
                hasTooManyLogs,
                distanceTo
            ),
            action = Action.DepositLogs
        )

        shop.behaviours.add(pickupAxe)
        forest.behaviours.add(chopTree)
        shed.behaviours.add(depositLogs)

        agent.reasoner.behaviours.apply {
            add(idle)
            add(findAxeArea)
            add(findTreeArea)
            add(findShedArea)
        }
    }

    private fun setupWorld() {
        forest.add(GameObject("small tree", 10, 13, Color.GREEN, Records(Skill to 1)))
        forest.add(GameObject("medium tree", 15, 14, Color.GREEN, Records(Skill to 5)))
        forest.add(GameObject("large tree", 14, 11, Color.GREEN, Records(Skill to 10)))
        forest.add(GameObject("giant tree", 11, 11, Color.GREEN, Records(Skill to 20)))
        shop.add(GameObject("small axe", 23, 3, Color.ORANGE, Records(Skill to 1)))
        shop.add(GameObject("medium axe", 20, 3, Color.ORANGE, Records(Skill to 10)))
        shop.add(GameObject("large axe", 25, 4, Color.ORANGE, Records(Skill to 15)))
        shed.add(GameObject("store", 22, 12, Color.BROWN))
        shed.add(GameObject("store", 25, 12, Color.BROWN))
        world.add(agent)

        forest.records[HasTrees] = true
        shop.records[HasAxes] = true
        shed.records[HasDeposits] = true
    }

    private fun setupActionHandlers() {
        provider.area<Agent>(Action.MoveToArea) { agent: Agent, area: Area ->
            agent.area.remove(agent)
            area.add(agent)
        }

        provider.obj<Agent>(Action.Pickup) { agent: Agent, obj: GameObject ->
            obj.area.actors.remove(obj)
            if (obj.name.contains("axe", true)) {
                agent.bag["axe"] = agent.bag.getOrDefault("axe", 0) + 1
                if (obj.area == shop) {
                    GlobalScope.launch {
                        delay(speed * 50)
                        obj.area.actors.add(obj)
                    }
                }
            }
        }

        provider.obj<Agent>(Action.DepositLogs) { agent, _ ->
            delay(speed * 10)
            agent.bag["logs"] = 0
        }

        provider.obj<Agent>(Action.Chop) { agent, target ->
            delay(speed * 10)
            target.colour = Color.BROWN
            agent.bag["logs"] = agent.bag.getOrDefault("logs", 0) + 1
            agent.records[Skill] = agent.getInt(Skill) + 1
            agent.records[ChoppingMomentum] = (agent.getDouble(ChoppingMomentum) + 0.01).coerceAtMost(1.0)
            if (Random.nextBoolean()) {
                agent.bag["axe"] = 0
            }
            GlobalScope.launch {
                delay(Random.nextLong(speed * 40, speed * 50))
                target.colour = Color.GREEN
            }
        }

        areas.forEach { area ->
            area.actors.forEach { actor ->
                provider.produce(actor)
            }
        }
    }

    private var mouseX = 0
    private var mouseY = 0

    private fun GridCanvas<Boolean, BooleanGrid>.text(s: String, row: Int, column: Int) {
        content.text(s) {
            this.x = row * 75.0
            this.y = boundary.height.toDouble() + boundsInLocal.height * (column + 1)
            strokeWidth = 1.0
            stroke = Color.WHITE
        }
    }

    private fun GridCanvas<Boolean, BooleanGrid>.text(s: String, column: Int) {
        content.text(s) {
            this.x = -boundsInLocal.width - 5
            this.y = boundsInLocal.height * (column + 1)
            strokeWidth = 1.0
            stroke = Color.WHITE
        }
    }

    suspend fun GridCanvas<Boolean, BooleanGrid>.reload() = withContext(Dispatchers.JavaFx) {
        reloadGrid()

        text("Bag:", 0)
        var index = 1
        agent.bag.forEach { (key, value) ->
            text("$key: $value", index++)
        }
        text("Skill: ${agent.getInt(Skill)}", 0, 0)
        val currentChoice = agent.reasoner.behaviours.current
        text("Behaviour: ${currentChoice?.behaviour?.name ?: "none"} ${currentChoice?.target?.name ?: ""}", 1, 0)
        text("State: ${agent.actorState}", 1, 1)

        content.text("X: $mouseX Y: $mouseY") {
            this.x = boundary.width.toDouble() - boundsInLocal.width
            this.y = boundary.height.toDouble() + boundsInLocal.height
            strokeWidth = 1.0
            stroke = Color.WHITE
        }
        areas.forEach {
            tile(it.x, it.y, it.width, it.height) {
                fill = it.colour
            }
            tileText(it.x, it.y + it.height - 1, it.x + it.width, it.y + it.height - 1, it.name) {
                strokeWidth = 1.0
                stroke = Color.BLACK
            }
        }
        areas.forEach {
            it.actors.forEach { actor ->
                tile(actor.x, actor.y) {
                    fill = actor.colour
                }
                tileText(actor.x, actor.y, actor.name.first().toUpperCase().toString()) {
                    strokeWidth = 2.0
                    stroke = Color.WHITE
                }
            }
        }
    }

    override val root = grid(
        32, 32,
        PADDING,
        PADDING
    ) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        content.prefWidth = boundary.width.toDouble()
        content.prefHeight = boundary.height.toDouble()

        var start = true
        GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                if (!start) {
                    continue
                }
                agent.reasoner.tick()
                reload()
                delay(speed)
            }
        }

        content.setOnMouseMoved {
            mouseX = it.gridX
            mouseY = it.gridY
        }
    }
}

fun Double.scale(min: Double, max: Double): Double {
    return (coerceIn(min, max) - min) / (max - min)
}

fun Double.inverse(): Double {
    return 1.0 - this
}

class PlayerAIApp : App(PlayerAIView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<PlayerAIApp>(*args)
}