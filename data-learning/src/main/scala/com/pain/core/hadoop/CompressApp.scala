package com.pain.core.hadoop

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io.IOUtils
import org.apache.hadoop.io.compress.{BZip2Codec, CompressionCodec, CompressionOutputStream}
import org.apache.hadoop.util.ReflectionUtils

import java.io.{File, FileInputStream, FileOutputStream}

object CompressApp {

    def main(args: Array[String]): Unit = {
        val filepath = "big/trackinfo.txt"
        // println(classOf[BZip2Codec].getCanonicalName)
        println("=== begin compress")
        compress(filepath, classOf[BZip2Codec].getCanonicalName)
        println("=== complete compress")
    }

    def compress(filePath: String, method: String): Unit = {
        val inputStream = new FileInputStream(new File(filePath))
        val clazz: Class[_] = Class.forName(method)
        val codec: CompressionCodec =  ReflectionUtils.newInstance(clazz, new Configuration()).asInstanceOf[CompressionCodec]
        val outputStream = new FileOutputStream(new File(filePath + codec.getDefaultExtension))
        val codecOutputStream: CompressionOutputStream = codec.createOutputStream(outputStream)

        IOUtils.copyBytes(inputStream, codecOutputStream, 1024 * 1024 * 4)
        codecOutputStream.close()
        outputStream.close()
        inputStream.close()
    }
}
