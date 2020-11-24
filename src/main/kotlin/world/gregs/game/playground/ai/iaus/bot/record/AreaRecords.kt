package world.gregs.game.playground.ai.iaus.bot.record

enum class AreaRecords(override val defaultValue: Any) : Record {
    HasDeposits(false),
    HasAxes(false),
    HasTrees(false);
}