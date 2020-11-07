package waigoma.genshinreminder.timer

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Timer(dateStart: String, dateEnd: String) {
    private val format: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    private var diffTime: ArrayList<String> = ArrayList()

    init {
        try {
            val date1: Date = format.parse(dateStart)
            val date2: Date = format.parse(dateEnd)

            val diff: Long = date1.time - date2.time

            val diffSeconds: Long = diff / 1000 % 60
            val diffMinutes: Long = diff / (60 * 1000) % 60
            val diffHours: Long = diff / (60 * 60 * 1000) % 24
            val diffDays: Long = diff / (24 * 60 * 60 * 1000)

            diffTime = arrayListOf(diffDays.toString(), diffHours.toString(), diffMinutes.toString(), diffSeconds.toString())
        }catch (e: Exception){
            e.stackTrace
            println("Timerでエラー")
        }
    }

    fun calcTimer(): ArrayList<String>{
        return diffTime
    }
}