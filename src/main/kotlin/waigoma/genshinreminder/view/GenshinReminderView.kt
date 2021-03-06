package waigoma.genshinreminder.view

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.util.Duration
import tornadofx.*
import waigoma.genshinreminder.app.GenshinReminderApp
import waigoma.genshinreminder.timer.Timer
import waigoma.genshinreminder.yaml.TimeCustomTemplate
import waigoma.genshinreminder.yaml.TimeEveryTemplate
import waigoma.genshinreminder.yaml.TimeLoader
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*


class GenshinReminderView : View("Genshin Reminder") {
    private val prop = Properties()
    private val tm = GenshinReminderApp.timeManager

    private val propFile = System.getProperty("user.dir") + File.separator + "GenshinReminder" + File.separator + "checkbox.properties"

    private val format: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    private var nowTime: String = ""

    override val root: AnchorPane by fxml("/fxml/main_view.fxml")

    //everyday timer & checkbox
    private val edTimer: Label by fxid("ed_timer")
    private val edCheckbox1: CheckBox by fxid("ed_checkbox1")

    //every3day timer & checkbox
    private val rdTimer: Label by fxid("rd_timer")
    private val rdCheckbox1: CheckBox by fxid("rd_checkbox1")
    private val rdCheckbox2: CheckBox by fxid("rd_checkbox2")
    private val rdCheckbox3: CheckBox by fxid("rd_checkbox3")
    private val rdCheckbox4: CheckBox by fxid("rd_checkbox4")

    //custom button
    private val ctButton1: Button by fxid("ct_button1")
    private val ctSpinner1: Spinner<Int> by fxid("custom_spinner1")
    private val ctSpinnerValueFactory1: SpinnerValueFactory<Int> = SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 48)
    private val ctTimer1: Label by fxid("countdown1")
    private val ctLabelList = listOf(ctTimer1)

    private val currentTime: Label by fxid("currentTime")

    private val currentTimeline = Timeline(KeyFrame(Duration.seconds(1.0),  //時間経過をトリガにするのはTimelineクラスを使う
            {
                val calendar: Calendar = Calendar.getInstance()
                nowTime = format.format(calendar.time)
                currentTime.text = nowTime
            }
    ))

    private val everyTimeline = Timeline(KeyFrame(Duration.seconds(1.0),
            {
                everyDayTimeLine()
            }
    ))

    private val customTimeline = Timeline(KeyFrame(Duration.seconds(1.0),
            {
                customTimeLine()
            }
    ))

    init {
        prop.load(FileInputStream(propFile))

        val calendar: Calendar = Calendar.getInstance()
        nowTime = format.format(calendar.time)
        while (!everyDayTimeLine()) println("everydayTimeLine Loading...")
        while (!customTimeLine()) println("customTimeLine Loading...")

        currentTimeline.cycleCount = Timeline.INDEFINITE
        currentTimeline.play()

        everyTimeline.cycleCount = Timeline.INDEFINITE
        everyTimeline.play()

        customTimeline.cycleCount = Timeline.INDEFINITE
        customTimeline.play()

        edCheckbox1.isSelected = prop.getProperty("edCheckbox1").toBoolean()
        rdCheckbox1.isSelected = prop.getProperty("rdCheckbox1").toBoolean()
        rdCheckbox2.isSelected = prop.getProperty("rdCheckbox2").toBoolean()
        rdCheckbox3.isSelected = prop.getProperty("rdCheckbox3").toBoolean()
        rdCheckbox4.isSelected = prop.getProperty("rdCheckbox4").toBoolean()

        ctSpinner1.valueFactory = ctSpinnerValueFactory1
        ctSpinner1.editor.alignment = Pos.CENTER_RIGHT
        ctSpinner1.valueFactory.value = tm.getCustom()[0].timer

        edCheckbox1.action {
            prop.setProperty("edCheckbox1", edCheckbox1.isSelected.toString())
            saveProperties()
        }
        rdCheckbox1.action {
            prop.setProperty("rdCheckbox1", rdCheckbox1.isSelected.toString())
            saveProperties()
        }
        rdCheckbox2.action {
            prop.setProperty("rdCheckbox2", rdCheckbox2.isSelected.toString())
            saveProperties()
        }
        rdCheckbox3.action {
            prop.setProperty("rdCheckbox3", rdCheckbox3.isSelected.toString())
            saveProperties()
        }
        rdCheckbox4.action {
            prop.setProperty("rdCheckbox4", rdCheckbox4.isSelected.toString())
            saveProperties()
        }
        ctButton1.action {
            val ct1 = tm.getCustom()[0]
            if (!ct1.active) {
                ct1.timer = ctSpinner1.value
                val ctCalendar1: Calendar = Calendar.getInstance()
                ctCalendar1.add(Calendar.HOUR_OF_DAY, ct1.timer)
                ct1.nextTime = format.format(ctCalendar1.time)
                ct1.active = true
            } else {
                ctTimer1.text = "ストップされました。"
                ct1.active = false
            }
            TimeLoader.saveYml()
        }
        TimeLoader.saveYml()
    }

    private fun everyDayTimeLine(): Boolean{
        for (day in listOf("1day", "3day")) {
            val map = tm.getEvery(day)
            val timer = Timer(map!!.nextTime, nowTime)
            val calc = timer.calcTimer()
            var oneMore = false

            if (calc[3].toInt() < 0) {
                val calendar: Calendar = Calendar.getInstance()
                calendar.time = format.parse(map.nextTime)
                calendar.add(Calendar.DATE, map.timer)
                calendar.set(Calendar.HOUR_OF_DAY, 5)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                tm.registerEvery(day, TimeEveryTemplate(map.timer, format.format(calendar.time)))
                TimeLoader.saveYml()
                when(day) {
                    "1day" -> {
                        edCheckbox1.isSelected = false

                        prop.setProperty("edCheckbox1", edCheckbox1.isSelected.toString())
                        edTimer.text = "Loading..."
                        println("1day")
                    }
                    "3day" -> {
                        rdCheckbox1.isSelected = false
                        rdCheckbox2.isSelected = false
                        rdCheckbox3.isSelected = false
                        rdCheckbox4.isSelected = false

                        prop.setProperty("rdCheckbox1", rdCheckbox1.isSelected.toString())
                        prop.setProperty("rdCheckbox2", rdCheckbox2.isSelected.toString())
                        prop.setProperty("rdCheckbox3", rdCheckbox3.isSelected.toString())
                        prop.setProperty("rdCheckbox4", rdCheckbox4.isSelected.toString())
                        rdTimer.text = "Loading..."
                        println("3day")
                    }
                }
                saveProperties()
                oneMore = true
            }
            if (oneMore) return false

            when(day) {
                "1day" -> {
                    edTimer.text = calc[0] + "日" + calc[1] + "時間" + calc[2] + "分" + calc[3] + "秒"
                }
                "3day" -> {
                    rdTimer.text = calc[0] + "日" + calc[1] + "時間" + calc[2] + "分" + calc[3] + "秒"
                }
            }
        }
        return true
    }

    private fun customTimeLine(): Boolean{
        var customNumber = 0
        for (number in tm.getCustom()) {
            if (!number.active) continue
            val timer = Timer(number.nextTime, nowTime)
            val calc = timer.calcTimer()
            var oneMore = false

            if (calc[3].toInt() < 0) {
                number.active = false
                ctLabelList[customNumber].text = "タイマーが終了しました。"

                TimeLoader.saveYml()
                saveProperties()
                oneMore = true
            }
            if (oneMore) return false

            // ラベル
            ctLabelList[customNumber].text = calc[0] + "日" + calc[1] + "時間" + calc[2] + "分" + calc[3] + "秒"

            customNumber++
        }
        return true
    }

    private fun saveProperties(){
        prop.store(FileWriter(File(propFile)), "Comments")
    }
}