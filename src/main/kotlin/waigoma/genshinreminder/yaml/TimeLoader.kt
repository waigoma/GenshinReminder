package waigoma.genshinreminder.yaml

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.nodes.Tag
import waigoma.genshinreminder.app.GenshinReminderApp
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.LinkedHashMap

class TimeLoader {
    companion object {
        private val ymlFile = System.getProperty("user.dir") + File.separator + "GenshinReminder" + File.separator + "data.yml"
        private val yml: Yaml = Yaml()
        private lateinit var tm: TimeManager

        fun loadStart(loadFile: InputStream, tm: TimeManager) {
            this.tm = tm
            val ymlMap: LinkedHashMap<String, Objects> = yml.load(loadFile)
            for (key in ymlMap.keys) {
                val doc = yml.dump(ymlMap[key])
                val map: LinkedHashMap<String, Objects> = yml.load(doc)
                if (!loadYaml(key, map)) println("$key は使用できません。")
            }

        }

        private fun loadYaml(type: String, map: LinkedHashMap<String, Objects>): Boolean{
            when(type){
                "every" -> {
                    for (key in map.keys){
                        val timer: Int = key.replace("day", "").toInt()
                        val nextYml: LinkedHashMap<String, Objects> = yml.load(yml.dump(map[key]))

                        for (nextKey in nextYml.keys){
                            when(nextKey){
                                "nextTime" -> {
                                    tm.registerEvery(key, TimeEveryTemplate(timer, nextYml[nextKey].toString()))
                                }
                            }
                        }
                    }
                    return true
                }

                "custom" -> {
                    for (key: String in map.keys){
                        val nextYml: LinkedHashMap<String, Objects> = yml.load(yml.dump(map[key]))
                        var timer = 0
                        var active = false
                        var nextTime = ""

                        for (nextKey in nextYml.keys){
                            when(nextKey){
                                "timer" -> {
                                    timer = nextYml[nextKey].toString().toInt()
                                }

                                "active" -> {
                                    active = nextYml[nextKey].toString().toBoolean()
                                }

                                "nextTime" -> {
                                    nextTime = nextYml[nextKey].toString()
                                }
                            }
                        }
                        tm.registerCustom(TimeCustomTemplate(timer, active, nextTime))
                    }
                    return true
                }
            }
            return false
        }

        fun saveYml(){
            try {
                val fileWriter = FileWriter(File(ymlFile))
                fileWriter.write(yml.dumpAs(GenshinReminderApp.timeManager.ymlString(), Tag.MAP, DumperOptions.FlowStyle.BLOCK))
                fileWriter.close()
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
}