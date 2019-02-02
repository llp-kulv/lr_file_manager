package com.fm.lingrui.base.utils

import android.os.Environment
import android.text.TextUtils
import android.util.Log
import com.fm.lingrui.base.BuildConfig
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.*

class Flog {
    companion object {
        private val isDebug: Boolean = BuildConfig.DEBUG;
        private var PACKAGE_NAME = BuildConfig.APPLICATION_ID
        private val TAG: String = PACKAGE_NAME
        private var logStream: PrintStream? = null
        private val LOG_ENTRY_FORMAT = "[%tF %tT][%s][%s]%s"
        private var initialized = false
        private val isNeedCaller = true
        private val isAllCaller = false
        private val singleTag = false
        private val LOG_LEVEL_WARN = 8
        private val LOG_LEVEL_INFO = 4
        private val LOG_LEVEL_DEBUG = 2
        private val FILE_LOG_LEVEL = if (isDebug) 2 else 32
        /*
         * 当前日志日志文件名
         */
        private var LOG_FILE_NAME = "LRFileManager.log"

        /**
         * 每个分块最大5M
         */
        private val LOG_SIZE = (5 * 1024 * 1024).toLong()

        fun d(msg: String) {
            d(TAG, msg);
        }

        fun e(msg: String) {
            e(TAG, msg);
        }

        fun w(msg: String) {
            w(TAG, msg);
        }

        fun i(msg: String) {
            i(TAG, msg);
        }


        fun d(tag: String, msg: String) {
            if (!isDebug) {
                return
            }

            Log.d(tag, msg)

            writePre(tag, "D")
        }


        fun e(tag: String, msg: String) {
            if (!isDebug) {
                return
            }

            Log.e(tag, msg)

            writePre(tag, "E")
        }

        fun w(tag: String, msg: String) {
            if (!isDebug) {
                return
            }

            Log.w(tag, msg)

            writePre(tag, "w")
        }

        fun i(tag: String, msg: String) {
            if (!isDebug) {
                return
            }

            Log.i(tag, msg)

            writePre(tag, "I")
        }

        fun trace(info: String, level: String) {
            if (!isDebug) {
                return
            }

            val totalInfo = info + " " + Log.getStackTraceString(Throwable())
            e("<<<-------start------")
            e(totalInfo)
            e("-------end-------->>>")

            var tempTag = TAG
            if (!singleTag) {
                tempTag =
                    Thread.currentThread().name + (if (isNeedCaller) "[" + getCaller() + "]" else "") + ":" + tempTag
            }

            if (FILE_LOG_LEVEL <= LOG_LEVEL_WARN) {
                write(level, tempTag, totalInfo, null)
            }
        }

        private fun writePre(tag: String, message: String) {
            if (!isDebug) {
                return
            }

            var newTag: String = tag;
            if (!singleTag) {
                newTag = Thread.currentThread().name + (if (isNeedCaller) "[" + getCaller() + "]" else "") + ":" + tag
            }
            if (FILE_LOG_LEVEL <= LOG_LEVEL_INFO) {
                write("I", newTag, message, null)
            }
        }

        private fun write(level: String, tag: String, msg: String, error: Throwable?) {
            if (!initialized) {
                init()
            }
            if (logStream == null || logStream!!.checkError()) {
                initialized = false
                return
            }
            val now = Date()

            logStream!!.printf(LOG_ENTRY_FORMAT, now, now, level, tag, " : $msg")
            logStream!!.println()

            error!!.printStackTrace(logStream) // NOSONAR
            logStream!!.println()
        }

        @Synchronized
        private fun init() {
            if (initialized) {
                return
            }
            try {
                val cacheRoot = getSDCacheFile() // 改到应用目录

                if (cacheRoot != null) {
                    val logFile = File(cacheRoot, LOG_FILE_NAME)
                    logFile.createNewFile()
                    if (logStream != null) {
                        logStream!!.close()
                    }
                    logStream = PrintStream(FileOutputStream(logFile, true), true)
                    initialized = true
                }
            } catch (e: Exception) {
                e("catch root error$e")
            }
        }

        private fun getSDCacheFile(): File? {
            // 为什么要重复代码。1.log不确定能不能取到context 2.log时机不确定
            if (isSdCardAvailable()) {
                val dataDir = File(File(Environment.getExternalStorageDirectory(), "Android"), "data")
                val appCacheDir = File(File(dataDir, PACKAGE_NAME), "cache")
                if (!appCacheDir.exists()) {
                    if (!appCacheDir.mkdirs()) {
                        return null
                    }
                }
                return appCacheDir
            }
            return null
        }

        private fun isSdCardAvailable(): Boolean {
            val file = Environment.getExternalStorageDirectory()
            return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED && file != null && file.exists()
        }

        // about save to local
        private fun getCaller(): String {
            val stack = Throwable().stackTrace
            val result = StringBuffer()
            for (i in stack.indices) {
                val ste = stack[i]

                val className = ste.className
                if (TextUtils.isEmpty(className) || !className.contains(PACKAGE_NAME) || className.contains(TAG)) {
                    continue
                }
                result.append(className.replace(PACKAGE_NAME + ".", "")).append(".").append(ste.methodName).append("(")
                    .append(ste.lineNumber).append(")")
                if (!isAllCaller) {
                    break
                } else {
                    result.append("]<-")
                }
            }
            return result.toString()
        }

        /**
         * 根据文件大小手动切分日志文件 默认为大于5M
         */
        @Synchronized
        fun commitByFileSize() {
            if (!isDebug) {
                return
            }

            try {
                val cacheRoot = getSDCacheFile() // 改到应用目录

                if (cacheRoot != null) {
                    val logFile = File(cacheRoot, LOG_FILE_NAME)
                    if (logFile.length() > LOG_SIZE) {
                        val backfile = File(
                            cacheRoot,
                            TAG + "_" + SimpleDateFormat("yyyyMMdd-HHmmss").format(Date()) + ".log"
                        )
                        logFile.renameTo(backfile)
                        logFile.delete()
                        logFile.createNewFile()
                        if (logStream != null) {
                            logStream!!.close()
                        }
                        logStream = PrintStream(FileOutputStream(logFile, true), true)
                    }
                }
            } catch (e: IOException) {
                e("Create back log file & init log stream failed$e")
            }

        }
    }
}