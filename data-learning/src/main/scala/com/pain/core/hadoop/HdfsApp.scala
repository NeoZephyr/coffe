package com.pain.core.hadoop

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{BlockLocation, FSDataInputStream, FSDataOutputStream, FileStatus, FileSystem, LocatedFileStatus, Path, RemoteIterator}
import org.apache.hadoop.io.IOUtils
import org.apache.hadoop.util.Progressable

import java.io.{BufferedInputStream, FileInputStream}
import java.net.URI

object HdfsApp {

    val HDFS_URL = "hdfs://cdp:8020"

    def main(args: Array[String]): Unit = {
        val configuration = new Configuration()
        val fileSystem: FileSystem = FileSystem.get(new URI(HDFS_URL), configuration, "ubuntu")

        // mkdir(fileSystem, "/hdfs_test")
        createFile(fileSystem, "/hello/hadoop.txt", "hadoop 棒极了！")
        // readFile(fileSystem, "/hdfs_test/hadoop.txt")
        // rename(fileSystem, "/hdfs_test/hadoop.txt", "/hdfs_test/hdfs.txt")
        // copyFromLocal(fileSystem, "input/words.txt", "/hdfs_test/hadoop.txt")
        // deleteFile(fileSystem, "/hdfs_test/hadoop.txt")
        // copyToLocal(fileSystem, "/hdfs_test/hdfs.txt", "input/hdfs.txt")
        // copyFromLocalWithProcess(fileSystem, "big/kafka_2.12-2.4.0.tgz", "/hdfs_test/kafka.tgz")
        // listFile(fileSystem, "/hdfs_test")
        // listFileStatus(fileSystem, "/hdfs_test")
        fileSystem.close()
    }

    def mkdir(fileSystem: FileSystem, dirPath: String): Unit = {
        val ok: Boolean = fileSystem.mkdirs(new Path(dirPath))
        println(s"create ${dirPath} success: ${ok}")
    }

    def createFile(fileSystem: FileSystem, filePath: String, content: String): Unit = {
        // default replication: 3
        val stream: FSDataOutputStream = fileSystem.create(new Path(filePath))
        stream.write(content.getBytes())
        stream.flush()
        stream.close()
    }

    def readFile(fileSystem: FileSystem, filePath: String): Unit = {
        val stream: FSDataInputStream = fileSystem.open(new Path(filePath))
        IOUtils.copyBytes(stream, System.out, 1024)
        stream.close()
    }

    def rename(fileSystem: FileSystem, srcFilePath: String, destFilePath: String): Unit = {
        val src = new Path(srcFilePath)
        val dest = new Path(destFilePath)
        fileSystem.rename(src, dest)
    }

    def copyFromLocal(fileSystem: FileSystem, localFilePath: String, hadoopFilePath: String): Unit = {
        val src = new Path(localFilePath)
        val dest = new Path(hadoopFilePath)
        fileSystem.copyFromLocalFile(src, dest)
    }

    def deleteFile(fileSystem: FileSystem, filePath: String): Unit = {
        fileSystem.delete(new Path(filePath), true)
    }

    def copyToLocal(fileSystem: FileSystem, hadoopFilePath: String, localFilePath: String): Unit = {
        val src = new Path(hadoopFilePath)
        val dest = new Path(localFilePath)
        fileSystem.copyToLocalFile(src, dest)
    }

    def copyFromLocalWithProcess(fileSystem: FileSystem, localFilePath: String, hadoopFilePath: String): Unit = {
        val inputStream = new BufferedInputStream(new FileInputStream(localFilePath))
        val outputStream: FSDataOutputStream = fileSystem.create(new Path(hadoopFilePath), new Progressable {
            override def progress(): Unit = {
                print(".")
            }
        })
        IOUtils.copyBytes(inputStream, outputStream, 4096)
        println()
        println("complete")
    }

    def listFile(fileSystem: FileSystem, filePath: String): Unit = {
        val iterator: RemoteIterator[LocatedFileStatus] = fileSystem.listFiles(new Path(filePath), true)

        while (iterator.hasNext) {
            val fileStatus: LocatedFileStatus = iterator.next()
            val directory: Boolean = fileStatus.isDirectory
            val len: Long = fileStatus.getLen
            val replication: Short = fileStatus.getReplication
            val path: String = fileStatus.getPath.toString
            println(s"类型：${if (directory) "文件夹" else "文件"}\t路径：${path}\t副本数：${replication}\t文件大小：${len}")
            println("blockInfo:")

            val blockLocations: Array[BlockLocation] = fileStatus.getBlockLocations

            for (blockLocation <- blockLocations) {
                println(blockLocation.getHosts.mkString(", "))
            }
        }
    }

    def listFileStatus(fileSystem: FileSystem, filePath: String): Unit = {
        val fileStatuses: Array[FileStatus] = fileSystem.listStatus(new Path(filePath))
        for (fileStatus <- fileStatuses) {
            val directory: Boolean = fileStatus.isDirectory
            val len: Long = fileStatus.getLen
            val replication: Short = fileStatus.getReplication
            val path: String = fileStatus.getPath.toString
            println(s"类型：${if (directory) "文件夹" else "文件"}\t路径：${path}\t副本数：${replication}\t文件大小：${len}")

            val blockLocations: Array[BlockLocation] = fileSystem.getFileBlockLocations(fileStatus, 0, fileStatus.getLen)

            for (blockLocation <- blockLocations) {
                println(s"name: ${blockLocation.getNames.mkString(", ")}, offset: ${blockLocation.getOffset}, length: ${blockLocation.getLength}, hosts: ${blockLocation.getHosts.mkString(", ")}")
            }
        }
    }
}