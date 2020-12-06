package world.gregs.game.playground.ai.iaus.bot.record

enum class PlayerRecords(override val defaultValue: Any) : Record {
    Skill(1),
    ChoppingMomentum(0.1),
    Bag(mutableMapOf<String, Int>());
}