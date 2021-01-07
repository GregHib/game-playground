package world.gregs.game.playground.flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

object Flows {
    /*
        Questions:
        Relation of flows and data - Will everything need to be wrapped in objects? Could we pass an enum and store the data elsewhere? other solutions with methods? (instructions data is just (Player, Used (optional), Target (entity, tile or interface), a few extra params)
        Mutable flows - Wrap flows so that they don't have to be manually reassigned each time something is added to them? i.e mutableFlow.onCompletion {} vs player.flow = player.flow.onCompletion {}
        Reusable flows - Will an entity object just be filled with flow variables? or is it a bunch of high-level functions to obtain the same data in multiple places? i.e val playersNearby vs player.nearbyPlayers()
        How to get all values on subscription? - SharedFlow


        Splitting a flow into two or breaking out/being processed by two collections
        Where should all these flows be stored?
        Where should subscriptions be added?
        How can it all be combined into a single flow which anyone can subscribe too pub-sub style?
        How can we pass data without wrapping everything in objects?
        How to receive all previously emitted events on subscribe?
        How to cancel out two opposing events? i.e entered and exited chunk/region/area
        Buffers vs State handling vs ? for keeping a value until
     */

    object Idle : Action
    data class Move(val fromRegion: Int, val toRegion: Int) : Action
    data class EnteredRegion(val id: Int) : Action
    data class LeftRegion(val id: Int) : Action
    interface Action

    fun Flow<Any>.launch(dispatcher: CoroutineDispatcher = Dispatchers.Default) = CoroutineScope(dispatcher).launch {
        collect() // tail-call
    }

    class MutableFlowList() {
        val map = mutableMapOf<StateFlow<Int>, Job>()
        val shared = MutableSharedFlow<Int>(extraBufferCapacity = 0xffff)

        fun add(flow: StateFlow<Int>) {
            val job = flow.onEach {
                this.shared.emit(it)
            }.launch()
            map[flow] = job
        }

        fun remove(flow: StateFlow<Int>) {
            map[flow]?.cancel()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {

        runBlocking {
            val list = MutableFlowList()
            list.shared.onEach {
                println("Emitted $it")
            }.launch()

            delay(10)
            val one = MutableStateFlow(-1)
            list.add(one)
            val two = MutableStateFlow(-1)
            list.add(two)
            one.emit(1)
            two.emit(2)
            list.remove(two)
            one.emit(3)
            two.emit(4)
        }
        val flow = MutableStateFlow<Action>(value = Idle)
        val regionChanges = flow
            .filterIsInstance<Move>()
            .filter { it.fromRegion != it.toRegion }


        val entered = regionChanges.transform {
            emit(EnteredRegion(it.toRegion))
        }
        val exited = regionChanges.transform {
            emit(LeftRegion(it.fromRegion))
        }

        val regionEvents = flowOf(entered, exited).flattenMerge()

        launch {
            regionEvents.collect {
                println("Region Event $it")
            }
        }

        launch {
            flow.emit(Move(1, 2))
        }

    }
}