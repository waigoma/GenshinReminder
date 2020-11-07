package waigoma.genshinreminder.yaml

import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

class TimeManager {
    private var every: HashMap<String, TimeEveryTemplate> = HashMap()
    private var custom: ArrayList<TimeCustomTemplate> = ArrayList()
    private var yamlMap: LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> = LinkedHashMap()

    fun registerEvery(key: String, tet: TimeEveryTemplate){
        every[key] = tet
    }

    fun getEvery(key: String): TimeEveryTemplate?{
        return every[key]
    }

    fun registerCustom(tct: TimeCustomTemplate){
        custom.add(tct)
    }

    fun getCustom(num: Int): TimeCustomTemplate{
        return custom[num]
    }

    fun ymlString(): LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> {
        for (key in listOf("every", "custom")){
            val ymlMap = LinkedHashMap<String, LinkedHashMap<String, String>>()
            when(key){
                "every" -> {
                    for (day in every.keys){
                        val lhashMap = LinkedHashMap<String, String>()
                        lhashMap["nextTime"] = every[day]!!.nextTime

                        ymlMap[day] = lhashMap
                    }
                }
                "custom" -> {
                    var num = 1
                    for (number in custom){
                        val lhashMap = LinkedHashMap<String, String>()
                        lhashMap["timer"] = number.timer.toString()
                        lhashMap["registerTime"] = number.registerTime
                        lhashMap["nextTime"] = number.nextTime

                        ymlMap[num.toString()] = lhashMap
                        num++
                    }
                }
            }

            yamlMap[key] = ymlMap
        }
        return yamlMap
    }
}