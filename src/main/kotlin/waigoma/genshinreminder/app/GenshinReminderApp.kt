package waigoma.genshinreminder.app

import tornadofx.*
import waigoma.genshinreminder.view.GenshinReminderView
import waigoma.genshinreminder.yaml.TimeLoader
import waigoma.genshinreminder.yaml.TimeManager
import java.util.jar.JarFile
import org.apache.commons.io.FileUtils;
import java.io.*


class GenshinReminderApp: App(GenshinReminderView::class){
    companion object{
        val timeManager = TimeManager()
    }
    init {
        val dirPath = System.getProperty("user.dir") + File.separator + "GenshinReminder"
        val file = File(dirPath)

        try {
            if (!file.exists()) {
                file.mkdir()
                saveResource("data", file)
            }
        }catch (e: IOException){
            e.printStackTrace()
        }
        val ymlFile = FileInputStream(dirPath + File.separator + "data.yml")
        TimeLoader.loadStart(ymlFile, timeManager)
    }

    private fun saveResource(copyResource: String, directory: File){
        val jarFile = File(GenshinReminderApp::class.java.protectionDomain.codeSource.location.path)
        if (jarFile.isFile) {
            // JARで実行する場合
            val jar = JarFile(jarFile)
            val entries = jar.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                if (entry.name.startsWith("$copyResource/") && !entry.isDirectory) {
                    val dest = File(directory, entry.name.substring(copyResource.length + 1))
                    val parent = dest.parentFile
                    parent?.mkdirs()
                    val out = FileOutputStream(dest)
                    val `in` = jar.getInputStream(entry)
                    try {
                        val buffer = ByteArray(8 * 1024)
                        var s = 0
                        while (`in`.read(buffer).also { s = it } > 0) {
                            out.write(buffer, 0, s)
                        }
                    } finally {
                        `in`.close()
                        out.close()
                    }
                }
            }
            jar.close()
        } else {
            // IDEで実行する場合
            val resource = File(GenshinReminderApp::class.java.classLoader.getResource(copyResource)!!.toURI())
            FileUtils.copyDirectory(resource, directory)
        }
    }
}

fun main(){
    launch<GenshinReminderApp>()
}