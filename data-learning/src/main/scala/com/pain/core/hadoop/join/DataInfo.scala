package com.pain.core.hadoop.join

import org.apache.hadoop.io.Writable

import java.io.{DataInput, DataOutput}

class DataInfo(var flag: String, var data: String) extends Writable {

    def this() = this("", "")

    override def write(dataOutput: DataOutput): Unit = {
        dataOutput.writeUTF(flag)
        dataOutput.writeUTF(data)
    }

    override def readFields(dataInput: DataInput): Unit = {
        this.flag = dataInput readUTF()
        this.data = dataInput.readUTF()
    }
}
