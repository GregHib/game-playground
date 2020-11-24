package world.gregs.game.playground.ai.iaus.bot.record

enum class PlayerRecords(override val defaultValue: Any) : Record {
    Skill(0),
    ChoppingMomentum(0.1),
    HasAxe(false),
    Logs(0);
}