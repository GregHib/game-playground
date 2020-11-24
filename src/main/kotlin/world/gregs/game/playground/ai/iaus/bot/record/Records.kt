package world.gregs.game.playground.ai.iaus.bot.record

class Records(private val map: MutableMap<Record, Any> = HashMap()) {

    constructor(vararg pairs: Pair<Record, Any>) : this(pairs.toMap().toMutableMap())

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any> get(record: Record): T = (map[record] ?: record.defaultValue) as T

    fun has(record: Record) = map.containsKey(record)

    operator fun set(record: Record, value: Any) {
        map[record] = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> remove(record: Record): T? = map.remove(record) as? T

    fun clear(record: Record) {
        map.remove(record)
    }

}

operator fun <T : Any> Recorder.get(record: Record): T = records[record]

fun Recorder.has(record: Record) = records.has(record)

operator fun Recorder.set(record: Record, value: Any) = records.set(record, value)

fun <T : Any> Recorder.remove(record: Record): T? = records.remove(record)

fun Recorder.clear(record: Record) = records.clear(record)

fun Recorder.getBoolean(record: Record) = get<Boolean>(record)

fun Recorder.getLong(record: Record) = get<Long>(record)

fun Recorder.getInt(record: Record) = get<Int>(record)

fun Recorder.getDouble(record: Record) = get<Double>(record)

fun Recorder.getString(record: Record) = get<String>(record)