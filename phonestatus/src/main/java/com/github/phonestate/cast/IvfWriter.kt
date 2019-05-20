/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.phonestate.cast

import java.io.IOException
import java.io.OutputStream

/**
 * Writes an IVF file.
 *
 * IVF format is a simple container format for VP8 encoded frames defined at
 * http://wiki.multimedia.cx/index.php?title=IVF.
 */

class IvfWriter
/**
 * Initializes the IVF file writer.
 *
 * Timebase fraction is in format scale/rate, e.g. 1/1000
 * Timestamp values supplied while writing frames should be in accordance
 * with this timebase value.
 *
 * @param filename   name of the IVF file
 * @param width      frame width
 * @param height     frame height
 * @param scale      timebase scale (or numerator of the timebase fraction)
 * @param rate       timebase rate (or denominator of the timebase fraction)
 */
@Throws(IOException::class)
@JvmOverloads constructor(//private RandomAccessFile mOutputFile;
    private val mOutputStream: OutputStream,
    private val mWidth: Int, private val mHeight: Int,
    private val mScale: Int = 1, private val mRate: Int = 1000000
) {
    private var mFrameCount: Int = 0

    init {
        mFrameCount = 0
        //mOutputFile.setLength(0);
        //mOutputFile.seek(HEADER_END);  // Skip the header for now, as framecount is unknown
    }//mOutputFile = new RandomAccessFile(filename, "rw");

    /**
     * Finalizes the IVF header and closes the file.
     */
    @Throws(IOException::class)
    fun close() {
        // Write header now
        //mOutputFile.seek(0);
        //mOutputFile.write(makeIvfHeader(mFrameCount, mWidth, mHeight, mScale, mRate));
        //mOutputFile.close();
        mOutputStream.close()
    }


    @Throws(IOException::class)
    fun writeHeader() {
        mOutputStream.write(makeIvfHeader(mFrameCount, mWidth, mHeight, mScale, mRate))
    }

    /**
     * Writes a single encoded VP8 frame with its frame header.
     *
     * @param frame     actual contents of the encoded frame data
     * @param timeStamp timestamp of the frame (in accordance to specified timebase)
     */
    @Throws(IOException::class)
    fun writeFrame(frame: ByteArray, timeStamp: Long) {
        mOutputStream.write(makeIvfFrameHeader(frame.size, timeStamp))
        mOutputStream.write(frame)
        mFrameCount++
    }

    companion object {
        private val HEADER_END: Byte = 32

        /**
         * Makes a 32 byte file header for IVF format.
         *
         * Timebase fraction is in format scale/rate, e.g. 1/1000
         *
         * @param frameCount total number of frames file contains
         * @param width      frame width
         * @param height     frame height
         * @param scale      timebase scale (or numerator of the timebase fraction)
         * @param rate       timebase rate (or denominator of the timebase fraction)
         */
        fun makeIvfHeader(frameCount: Int, width: Int, height: Int, scale: Int, rate: Int): ByteArray {
            val ivfHeader = ByteArray(32)
            ivfHeader[0] = 'D'.toByte()
            ivfHeader[1] = 'K'.toByte()
            ivfHeader[2] = 'I'.toByte()
            ivfHeader[3] = 'F'.toByte()
            lay16Bits(ivfHeader, 4, 0)  // version
            lay16Bits(ivfHeader, 6, 32)  // header size
            ivfHeader[8] = 'V'.toByte()  // fourcc
            ivfHeader[9] = 'P'.toByte()
            ivfHeader[10] = '8'.toByte()
            ivfHeader[11] = '0'.toByte()
            lay16Bits(ivfHeader, 12, width)
            lay16Bits(ivfHeader, 14, height)
            lay32Bits(ivfHeader, 16, rate)  // scale/rate
            lay32Bits(ivfHeader, 20, scale)
            lay32Bits(ivfHeader, 24, frameCount)
            lay32Bits(ivfHeader, 28, 0)  // unused
            return ivfHeader
        }

        /**
         * Makes a 12 byte header for an encoded frame.
         *
         * @param size      frame size
         * @param timestamp presentation timestamp of the frame
         */
        private fun makeIvfFrameHeader(size: Int, timestamp: Long): ByteArray {
            val frameHeader = ByteArray(12)
            lay32Bits(frameHeader, 0, size)
            lay64bits(frameHeader, 4, timestamp)
            return frameHeader
        }


        /**
         * Lays least significant 16 bits of an int into 2 items of a byte array.
         *
         * Note that ordering is little-endian.
         *
         * @param array     the array to be modified
         * @param index     index of the array to start laying down
         * @param value     the integer to use least significant 16 bits
         */
        private fun lay16Bits(array: ByteArray, index: Int, value: Int) {
            array[index] = value.toByte()
            array[index + 1] = (value shr 8).toByte()
        }

        /**
         * Lays an int into 4 items of a byte array.
         *
         * Note that ordering is little-endian.
         *
         * @param array     the array to be modified
         * @param index     index of the array to start laying down
         * @param value     the integer to use
         */
        private fun lay32Bits(array: ByteArray, index: Int, value: Int) {
            for (i in 0..3) {
                array[index + i] = (value shr i * 8).toByte()
            }
        }

        /**
         * Lays a long int into 8 items of a byte array.
         *
         * Note that ordering is little-endian.
         *
         * @param array     the array to be modified
         * @param index     index of the array to start laying down
         * @param value     the integer to use
         */
        private fun lay64bits(array: ByteArray, index: Int, value: Long) {
            for (i in 0..7) {
                array[index + i] = (value shr i * 8).toByte()
            }
        }
    }
}
/**
 * Initializes the IVF file writer with a microsecond timebase.
 *
 * Microsecond timebase is default for OMX thus stagefright.
 *
 * @param filename   name of the IVF file
 * @param width      frame width
 * @param height     frame height
 */
