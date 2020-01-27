package world.gregs.game.playground.reactive

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

@Suppress("RemoveRedundantBackticks")
internal class AsynchronousFlows {

    @Test
    fun `Blocking sequences`() {
        fun foo() = sequence {
            for (i in 1..3) {
                Thread.sleep(300)
                yield(i)
            }
        }
        foo().forEach { value -> println(value) }
    }

    @Test
    fun `Sequences sequences`() {
        suspend fun foo() = sequence {
            for (i in 1..3) {
                Thread.sleep(300)
                yield(i)
            }
        }
        runBlocking {
            foo().forEach { value -> println(value) }
        }
    }

    @Test
    fun `Flows`() {
        fun foo() = flow<Int> {
            for (i in 1..3) {
                delay(100)
                emit(i)
            }
        }
        runBlocking {
            launch {
                for (k in 1..3) {
                    println("I'm not blocked $k")
                    delay(100)
                }
            }

            foo().collect { value -> println(value) }
        }
    }

    @Test
    fun `Cold flows`() {
        fun foo() = flow {
            println("Flow started")
            for (i in 1..3) {
                delay(100)
                emit(i)
            }
        }

        runBlocking {
            println("Calling foo")
            val flow = foo()
            println("Calling collect...")
            flow.collect { value -> println(value) }
            println("Calling collect again...")
            flow.collect { value -> println(value) }
        }
    }

    @Test
    fun `Flow cancellation`() {
        fun foo() = flow {
            for (i in 1..3) {
                delay(100)
                emit(i)
            }
        }

        runBlocking {
            withTimeoutOrNull(2500) {
                foo().collect { value -> println(value) }
            }
            println("Done")
        }
    }

    @Test
    fun `Flow builders`() = runBlocking {
        (1..3).asFlow().collect { value -> println(value) }
    }

    @Test
    fun `Intermediate flow operators`() = runBlocking {
        (1..3).asFlow()
            .map { request ->
                delay(100)
                "response $request"
            }
            .collect { value -> println(value) }
    }

    @Test
    fun `Transform operator`() {
        runBlocking {
            (1..3).asFlow()
                .transform { request ->
                    emit("Making request $request")
                    delay(100)
                    emit("response $request")
                }
                .collect { response -> println(response) }
        }
    }

    @Test
    fun `Size limiting operators`() {
        fun numbers() = flow {
            try {
                emit(1)
                emit(2)
                println("This line will not execute")
                emit(3)
            } finally {
                println("Finally in numbers")
            }
        }
        runBlocking {
            numbers()
                .take(2)
                .collect { value -> println(value) }
        }
    }

    @Test
    fun `Terminal operators`() = runBlocking {
        val sum = (1..5).asFlow()
            .map { it * it }
            .reduce { a, b -> a + b }
        println(sum)
    }

    @Test
    fun `Flow on operator`() = runBlocking {
        fun foo() = flow {
            for (i in 1..3) {
                Thread.sleep(100)
                println("[${Thread.currentThread().name}] Emitting $i")
                emit(i)
            }
        }.flowOn(Dispatchers.Default)

        foo()
            .collect { value ->
                println("[${Thread.currentThread().name}] Collected $value")
            }
    }

    @Test
    fun `Buffering`() = runBlocking {
        fun foo() = flow {
            for (i in 1..3) {
                delay(100)
                emit(i)
            }
        }

        val time = measureTimeMillis {
            foo()
                .buffer()
                .collect { value ->
                    delay(300)
                    println(value)
                }
        }
        println("Collected in $time ms")
    }

    @Test
    fun `Conflation`() = runBlocking {
        fun foo() = flow {
            for (i in 1..3) {
                delay(100)
                emit(i)
            }
        }

        val time = measureTimeMillis {
            foo()
                .conflate()
                .collect { value ->
                    delay(300)
                    println(value)
                }
        }
        println("Collected in $time ms")
    }

    @Test
    fun `Collect latest`() = runBlocking {
        fun foo() = flow {
            for (i in 1..3) {
                delay(100)
                emit(i)
            }
        }

        val time = measureTimeMillis {
            foo()
                .collectLatest { value ->
                    println("Collecting $value")
                    delay(300)
                    println(value)
                }
        }
        println("Collected in $time ms")
    }

    @Test
    fun `Zip`() = runBlocking {
        val nums = (1..3).asFlow()
        val strs = flowOf("one", "two", "three")
        nums
            .zip(strs) { a, b -> "$a -> $b" }
            .collect { println(it) }
    }

    @Test
    fun `Zip delayed`() = runBlocking {
        val nums = (1..3).asFlow().onEach { delay(300) }
        val strs = flowOf("one", "two", "three").onEach { delay(400) }
        val startTime = System.currentTimeMillis()
        nums
            .zip(strs) { a, b -> "$a -> $b" }
            .collect { value ->
                println("$value at ${System.currentTimeMillis() - startTime} ms from start")
            }
    }

    @Test
    fun `Combine`() = runBlocking {
        val nums = (1..3).asFlow().onEach { delay(300) }
        val strs = flowOf("one", "two", "three").onEach { delay(400) }
        val startTime = System.currentTimeMillis()
        nums
            .combine(strs) { a, b -> "$a -> $b" }
            .collect { value ->
                println("$value at ${System.currentTimeMillis() - startTime} ms from start")
            }
    }

    @Test
    fun `Flap map concat`() = runBlocking {
        fun requestFlow(i: Int) = flow {
            emit("$i: First")
            delay(50)
            emit("$i: Second")
        }

        val startTime = System.currentTimeMillis()
        (1..3).asFlow().onEach { delay(100) }
            .flatMapConcat { requestFlow(it) }
            .collect { value ->
                println("$value at ${System.currentTimeMillis() - startTime} ms from start")
            }
    }

    @Test
    fun `Exception transparency`() = runBlocking {
        fun foo() = flow {
            for (i in 1..3) {
                println("Emitting $i")
                emit(i) // emit next value
            }
        }
            .map { value ->
                check(value <= 1) { "Crashed on $value" }
                "string $value"
            }
        foo()
            .catch { e -> emit("Caught $e") }
            .collect { value -> println(value) }
    }

    @Test
    fun `Catching declaratively`() = runBlocking {
        fun foo() = flow {
            for (i in 1..3) {
                println("Emitting $i")
                emit(i)
            }
        }
        foo()
            .onEach { value ->
                check(value <= 1) { "Collected $value" }
                println(value)
            }
            .catch { e -> println("Caught $e") }
            .collect()
    }
}