package world.gregs.game.playground.mdp

typealias ProbableState = Pair<Double, State>

internal val ProbableState.probability: Double
    get() {return first}
internal val ProbableState.state: State
    get() {return second}