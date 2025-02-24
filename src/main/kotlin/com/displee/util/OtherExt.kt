package com.displee.util

import com.displee.cache.CacheLibrary
import com.displee.cache.index.Index.Companion.WHIRLPOOL_SIZE
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.OutputStream

private val CRC_TABLE = IntArray(256) {
    var crc = it
    for (i_84_ in 0..7) {
        crc = if (crc and 0x1 == 1) {
            crc ushr 1 xor 0x12477cdf.inv()
        } else {
            crc ushr 1
        }
    }
    crc
}

fun ByteArray.generateCrc(offset: Int = 0, length: Int = size): Int {
    var crc = -1
    for (i in offset until length) {
        crc = crc ushr 8 xor CRC_TABLE[crc xor this[i].toInt() and 0xff]
    }
    crc = crc xor -0x1
    return crc
}

fun ByteArray.generateWhirlpool(whirlpool: Whirlpool, offset: Int = 0, length: Int = size): ByteArray {
    val source: ByteArray
    if (offset > 0) {
        source = ByteArray(length)
        System.arraycopy(this, offset, source, 0, length)
    } else {
        source = this
    }
    whirlpool.NESSIEinit()
    whirlpool.NESSIEadd(source, (length * 8).toLong())
    val digest = ByteArray(WHIRLPOOL_SIZE)
    whirlpool.NESSIEfinalize(digest, 0)
    return digest
}

fun InputStream.writeTo(to: OutputStream): Long {
    val buf = ByteArray(0x1000)
    var total: Long = 0
    while (true) {
        val r = read(buf)
        if (r == -1) {
            break
        }
        to.write(buf, 0, r)
        total += r.toLong()
    }
    return total
}

fun String.hashCode317(): Int {
    val upperCaseString = toUpperCase()
    var hash = 0
    for (element in upperCaseString) {
        hash = hash * 61 + element.toInt() - 32
    }
    return hash
}

val log = LoggerFactory.getLogger("displeeLog")

fun CacheLibrary.checkFor229() {
    val origin = this
    if (!origin.is317() && !origin.isRS3() && !origin.osrs229Plus && origin.indexCount <= 23 && origin.exists(2)) {
        val rev = origin.index(2).revision
        if (rev >= 300 && rev >= 4652) { // as of rev 229 2025 feb 18th.
            val msg = "WARNING: Auto-detected that this cache is OSRS rev229+ (based on index count ${origin.indexCount} and index2.revision=$rev. If so, you need to set osrs229Plus as TRUE when initilizing the library : `CacheLibrary.create(path, osrs229Plus = true`. If you have cache exceptions after packing, this is probably why. Format is being written as pre-229."
            origin.listener?.notify(99.9, msg) // no listener may be attached
            log.warn("{}", msg) // logger may be muted
            System.err.println(msg)
        }
    }
}