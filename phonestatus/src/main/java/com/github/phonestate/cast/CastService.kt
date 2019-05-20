/*
 * Copyright (C) 2016 Jones Chi
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

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.display.VirtualDisplay
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.view.Surface
import java.io.*
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.UnknownHostException
import java.util.*

class CastService : Service() {
    private val TAG = "CastService"
    private val NT_ID_CASTING = 0
    private val mHandler = Handler(ServiceHandlerCallback())
    private val mMessenger = Messenger(mHandler)
    private val mClients = ArrayList<Messenger>()
    private var mBroadcastIntentFilter: IntentFilter? = null

    private var mMediaProjectionManager: MediaProjectionManager? = null
    private var mReceiverIp: String? = null
    private var mResultCode: Int = 0
    private var mResultData: Intent? = null
    private var mSelectedFormat: String? = null
    private var mSelectedWidth: Int = 0
    private var mSelectedHeight: Int = 0
    private var mSelectedDpi: Int = 0
    private var mSelectedBitrate: Int = 0
    //private boolean mMuxerStarted = false;
    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mInputSurface: Surface? = null
    //private MediaMuxer mMuxer;
    private var mVideoEncoder: MediaCodec? = null
    private var mVideoBufferInfo: MediaCodec.BufferInfo? = null
    //private int mTrackIndex = -1;
    private var mServerSocket: ServerSocket? = null
    private var mSocket: Socket? = null
    private var mSocketOutputStream: OutputStream? = null
    private var mIvfWriter: IvfWriter? = null
    private val mDrainHandler = Handler()
    private val mStartEncodingRunnable = Runnable {
        if (!startScreenCapture()) {
            Log.e(TAG, "Failed to start capturing screen")
        }
    }
    private val mDrainEncoderRunnable = Runnable { drainEncoder() }

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            Log.d(TAG, "Service receive broadcast action: " + action!!)
            if (action == null) {
                return
            }
            if (Common.ACTION_STOP_CAST == action) {
                stopScreenCapture()
                closeSocket(true)
                stopSelf()
            }
        }
    }

    private inner class ServiceHandlerCallback : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            Log.d(TAG, "Handler got event, what: " + msg.what)
            when (msg.what) {
                Common.MSG_REGISTER_CLIENT -> {
                    mClients.add(msg.replyTo)
                }
                Common.MSG_UNREGISTER_CLIENT -> {
                    mClients.remove(msg.replyTo)
                }
                Common.MSG_STOP_CAST -> {
                    stopScreenCapture()
                    closeSocket(true)
                    stopSelf()
                }
            }
            return false
        }
    }

    override fun onCreate() {
        super.onCreate()
        mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mBroadcastIntentFilter = IntentFilter()
        mBroadcastIntentFilter!!.addAction(Common.ACTION_STOP_CAST)
        registerReceiver(mBroadcastReceiver, mBroadcastIntentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Destroy service")
        stopScreenCapture()
        closeSocket(true)
        unregisterReceiver(mBroadcastReceiver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return Service.START_NOT_STICKY
        }
        mReceiverIp = intent.getStringExtra(Common.EXTRA_RECEIVER_IP)
        mResultCode = intent.getIntExtra(Common.EXTRA_RESULT_CODE, -1)
        mResultData = intent.getParcelableExtra(Common.EXTRA_RESULT_DATA)
        Log.d(TAG, "Remove IP: " + mReceiverIp!!)
        if (mReceiverIp == null) {
            return Service.START_NOT_STICKY
        }
        //if (mResultCode != Activity.RESULT_OK || mResultData == null) {
        //    Log.e(TAG, "Failed to start service, mResultCode: " + mResultCode + ", mResultData: " + mResultData);
        //    return START_NOT_STICKY;
        //}
        mSelectedWidth = intent.getIntExtra(Common.EXTRA_SCREEN_WIDTH, Common.DEFAULT_SCREEN_WIDTH)
        mSelectedHeight = intent.getIntExtra(Common.EXTRA_SCREEN_HEIGHT, Common.DEFAULT_SCREEN_HEIGHT)
        mSelectedDpi = intent.getIntExtra(Common.EXTRA_SCREEN_DPI, Common.DEFAULT_SCREEN_DPI)
        mSelectedBitrate = intent.getIntExtra(Common.EXTRA_VIDEO_BITRATE, Common.DEFAULT_VIDEO_BITRATE)
        mSelectedFormat = intent.getStringExtra(Common.EXTRA_VIDEO_FORMAT)
        if (mSelectedFormat == null) {
            mSelectedFormat = Common.DEFAULT_VIDEO_MIME_TYPE
        }
        if (mReceiverIp!!.length <= 0) {
            Log.d(TAG, "Start with listen mode")
            if (!createServerSocket()) {
                Log.e(TAG, "Failed to create socket to receiver, ip: " + mReceiverIp!!)
                return Service.START_NOT_STICKY
            }
        } else {
            Log.d(TAG, "Start with client mode")
            if (!createSocket()) {
                Log.e(TAG, "Failed to create socket to receiver, ip: " + mReceiverIp!!)
                return Service.START_NOT_STICKY
            }
            if (!startScreenCapture()) {
                Log.e(TAG, "Failed to start capture screen")
                return Service.START_NOT_STICKY
            }
        }
        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return mMessenger.binder
    }

    private fun showNotification() {
        val notificationIntent = Intent(Common.ACTION_STOP_CAST)
        val notificationPendingIntent =
            PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = Notification.Builder(this)
        /*  builder.setSmallIcon(R.mipmap.ic_launcher)
              .setDefaults(Notification.DEFAULT_ALL)
              .setOnlyAlertOnce(true)
              .setOngoing(true)
              .setContentTitle(getString(R.string.app_name))
              .setContentText(getString(R.string.casting_screen))
              .addAction(
                  android.R.drawable.ic_menu_close_clear_cancel,
                  getString(R.string.action_stop),
                  notificationPendingIntent
              )*/
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NT_ID_CASTING, builder.build())
    }

    private fun dismissNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NT_ID_CASTING)
    }

    private fun startScreenCapture(): Boolean {
        Log.d(TAG, "mResultCode: $mResultCode, mResultData: $mResultData")
        if (mResultCode != 0 && mResultData != null) {
            setUpMediaProjection()
            startRecording()
            showNotification()
            return true
        }
        return false
    }

    private fun setUpMediaProjection() {
        mMediaProjection = mMediaProjectionManager!!.getMediaProjection(mResultCode, mResultData!!)
    }

    private fun startRecording() {
        Log.d(TAG, "startRecording")
        prepareVideoEncoder()

        //try {
        //    mMuxer = new MediaMuxer("/sdcard/video.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        //} catch (IOException ioe) {
        //    throw new RuntimeException("MediaMuxer creation failed", ioe);
        //}

        // Start the video input.
        mVirtualDisplay = mMediaProjection!!.createVirtualDisplay(
            "Recording Display", mSelectedWidth,
            mSelectedHeight, mSelectedDpi, 0 /* flags */, mInputSurface, null, null/* handler */
        )/* callback */

        // Start the encoders
        drainEncoder()
    }

    private fun prepareVideoEncoder() {
        mVideoBufferInfo = MediaCodec.BufferInfo()
        val format = MediaFormat.createVideoFormat(mSelectedFormat, mSelectedWidth, mSelectedHeight)
        val frameRate = Common.DEFAULT_VIDEO_FPS

        // Set some required properties. The media codec may fail if these aren't defined.
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        format.setInteger(MediaFormat.KEY_BIT_RATE, mSelectedBitrate)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        format.setInteger(MediaFormat.KEY_CAPTURE_RATE, frameRate)
        format.setInteger(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, 1000000 / frameRate)
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1) // 1 seconds between I-frames

        // Create a MediaCodec encoder and configure it. Get a Surface we can use for recording into.
        try {
            mVideoEncoder = MediaCodec.createEncoderByType(mSelectedFormat!!)
            mVideoEncoder!!.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            mInputSurface = mVideoEncoder!!.createInputSurface()
            mVideoEncoder!!.start()
        } catch (e: IOException) {
            Log.e(TAG, "Failed to initial encoder, e: $e")
            releaseEncoders()
        }

    }

    private fun drainEncoder(): Boolean {
        mDrainHandler.removeCallbacks(mDrainEncoderRunnable)
        while (true) {
            val bufferIndex = mVideoEncoder!!.dequeueOutputBuffer(mVideoBufferInfo!!, 0)

            if (bufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // nothing available yet
                break
            } else if (bufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                //if (mTrackIndex >= 0) {
                //    throw new RuntimeException("format changed twice");
                //}
                //mTrackIndex = mMuxer.addTrack(mVideoEncoder.getOutputFormat());
                //if (!mMuxerStarted && mTrackIndex >= 0) {
                //    mMuxer.start();
                //    mMuxerStarted = true;
                //}
            } else if (bufferIndex < 0) {
                // not sure what's going on, ignore it
            } else {
                val encodedData = mVideoEncoder!!.getOutputBuffer(bufferIndex)
                    ?: throw RuntimeException("couldn't fetch buffer at index $bufferIndex")
// Fixes playability issues on certain h264 decoders including omxh264dec on raspberry pi
                // See http://stackoverflow.com/a/26684736/4683709 for explanation
                //if ((mVideoBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                //    mVideoBufferInfo.size = 0;
                //}

                //Log.d(TAG, "Video buffer offset: " + mVideoBufferInfo.offset + ", size: " + mVideoBufferInfo.size);
                if (mVideoBufferInfo!!.size != 0) {
                    encodedData.position(mVideoBufferInfo!!.offset)
                    encodedData.limit(mVideoBufferInfo!!.offset + mVideoBufferInfo!!.size)
                    if (mSocketOutputStream != null) {
                        try {
                            val b = ByteArray(encodedData.remaining())
                            encodedData.get(b)
                            if (mIvfWriter != null) {
                                mIvfWriter!!.writeFrame(b, mVideoBufferInfo!!.presentationTimeUs)
                            } else {
                                mSocketOutputStream!!.write(b)
                            }
                        } catch (e: IOException) {
                            Log.d(TAG, "Failed to write data to socket, stop casting")
                            e.printStackTrace()
                            stopScreenCapture()
                            return false
                        }

                    }
                    /*
                    if (mMuxerStarted) {
                        encodedData.position(mVideoBufferInfo.offset);
                        encodedData.limit(mVideoBufferInfo.offset + mVideoBufferInfo.size);
                        try {
                            if (mSocketOutputStream != null) {
                                byte[] b = new byte[encodedData.remaining()];
                                encodedData.get(b);
                                mSocketOutputStream.write(b);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mMuxer.writeSampleData(mTrackIndex, encodedData, mVideoBufferInfo);
                    } else {
                        // muxer not started
                    }
                    */
                }

                mVideoEncoder!!.releaseOutputBuffer(bufferIndex, false)

                if (mVideoBufferInfo!!.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    break
                }
            }
        }

        mDrainHandler.postDelayed(mDrainEncoderRunnable, 10)
        return true
    }

    private fun stopScreenCapture() {
        dismissNotification()
        releaseEncoders()
        closeSocket()
        if (mVirtualDisplay == null) {
            return
        }
        mVirtualDisplay!!.release()
        mVirtualDisplay = null
    }

    private fun releaseEncoders() {
        mDrainHandler.removeCallbacks(mDrainEncoderRunnable)
        /*
        if (mMuxer != null) {
            if (mMuxerStarted) {
                mMuxer.stop();
            }
            mMuxer.release();
            mMuxer = null;
            mMuxerStarted = false;
        }
        */
        if (mVideoEncoder != null) {
            mVideoEncoder!!.stop()
            mVideoEncoder!!.release()
            mVideoEncoder = null
        }
        if (mInputSurface != null) {
            mInputSurface!!.release()
            mInputSurface = null
        }
        if (mMediaProjection != null) {
            mMediaProjection!!.stop()
            mMediaProjection = null
        }
        if (mIvfWriter != null) {
            mIvfWriter = null
        }
        //mResultCode = 0;
        //mResultData = null;
        mVideoBufferInfo = null
        //mTrackIndex = -1;
    }

    private fun createServerSocket(): Boolean {
        val th = Thread(Runnable {
            try {
                mServerSocket = ServerSocket(Common.VIEWER_PORT)
                while (!Thread.currentThread().isInterrupted && !mServerSocket!!.isClosed) {
                    mSocket = mServerSocket!!.accept()
                    val commThread = CommunicationThread(mSocket)
                    Thread(commThread).start()
                }
            } catch (e: IOException) {
                Log.e(TAG, "Failed to create server socket or server socket error")
                e.printStackTrace()
            }
        })
        th.start()
        return true
    }

    internal inner class CommunicationThread(private var mClientSocket: Socket?) : Runnable {

        override fun run() {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    val input = BufferedReader(InputStreamReader(mClientSocket!!.getInputStream()))
                    val data = input.readLine()
                    Log.d(TAG, "Got data from socket: " + data!!)
                    if (data == null || !data.equals("mirror", ignoreCase = true)) {
                        mClientSocket!!.close()
                        return
                    }
                    mSocketOutputStream = mClientSocket!!.getOutputStream()
                    val osw = OutputStreamWriter(mSocketOutputStream!!)
                    osw.write(String.format(HTTP_MESSAGE_TEMPLATE, mSelectedWidth, mSelectedHeight))
                    osw.flush()
                    mSocketOutputStream!!.flush()
                    if (mSelectedFormat == MediaFormat.MIMETYPE_VIDEO_AVC) {
                        if (mSelectedWidth == 1280 && mSelectedHeight == 720) {
                            mSocketOutputStream!!.write(H264_PREDEFINED_HEADER_1280x720)
                        } else if (mSelectedWidth == 800 && mSelectedHeight == 480) {
                            mSocketOutputStream!!.write(H264_PREDEFINED_HEADER_800x480)
                        } else {
                            Log.e(TAG, "Unknown width: $mSelectedWidth, height: $mSelectedHeight")
                            mSocketOutputStream!!.close()
                            mClientSocket!!.close()
                            mClientSocket = null
                            mSocketOutputStream = null
                        }
                    } else if (mSelectedFormat == MediaFormat.MIMETYPE_VIDEO_VP8) {
                        mIvfWriter = IvfWriter(mSocketOutputStream!!, mSelectedWidth, mSelectedHeight)
                        mIvfWriter!!.writeHeader()
                    } else {
                        Log.e(TAG, "Unknown format: " + mSelectedFormat!!)
                        mSocketOutputStream!!.close()
                        mClientSocket!!.close()
                        mClientSocket = null
                        mSocketOutputStream = null
                    }
                    if (mSocketOutputStream != null) {
                        mHandler.post(mStartEncodingRunnable)
                    }
                    return
                } catch (e: UnknownHostException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                mClientSocket = null
                mSocketOutputStream = null
            }
        }
    }

    private fun createSocket(): Boolean {
        val th = Thread(Runnable {
            try {
                val serverAddr = InetAddress.getByName(mReceiverIp)
                Log.e(TAG, "Address  : $serverAddr")

                mSocket = Socket(serverAddr, Common.VIEWER_PORT)
                mSocketOutputStream = mSocket!!.getOutputStream()
                val osw = OutputStreamWriter(mSocketOutputStream!!)
                osw.write(String.format(HTTP_MESSAGE_TEMPLATE, mSelectedWidth, mSelectedHeight))
                osw.flush()
                mSocketOutputStream!!.flush()
                if (mSelectedFormat == MediaFormat.MIMETYPE_VIDEO_AVC) {
                    if (mSelectedWidth == 1280 && mSelectedHeight == 720) {
                        mSocketOutputStream!!.write(H264_PREDEFINED_HEADER_1280x720)
                    } else if (mSelectedWidth == 800 && mSelectedHeight == 480) {
                        mSocketOutputStream!!.write(H264_PREDEFINED_HEADER_800x480)
                    } else {
                        Log.e(TAG, "Unknown width: $mSelectedWidth, height: $mSelectedHeight")
                        mSocketOutputStream!!.close()
                        mSocket!!.close()
                        mSocket = null
                        mSocketOutputStream = null
                    }
                } else if (mSelectedFormat == MediaFormat.MIMETYPE_VIDEO_VP8) {
                    mIvfWriter = IvfWriter(mSocketOutputStream!!, mSelectedWidth, mSelectedHeight)
                    mIvfWriter!!.writeHeader()
                } else {
                    Log.e(TAG, "Unknown format: " + mSelectedFormat!!)
                    mSocketOutputStream!!.close()
                    mSocket!!.close()
                    mSocket = null
                    mSocketOutputStream = null
                }
                return@Runnable
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            mSocket = null
            mSocketOutputStream = null
        })
        th.start()
        try {
            th.join()
            if (mSocket != null && mSocketOutputStream != null) {
                return true
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        return false
    }

    private fun closeSocket(closeServerSocket: Boolean = false) {
        if (mSocket != null) {
            try {
                mSocket!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        if (closeServerSocket) {
            if (mServerSocket != null) {
                try {
                    mServerSocket!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            mServerSocket = null
        }
        mSocket = null
        mSocketOutputStream = null
    }

    companion object {

        private val HTTP_MESSAGE_TEMPLATE = "POST /api/v1/h264 HTTP/1.1\r\n" +
                "Connection: close\r\n" +
                "X-WIDTH: %1\$d\r\n" +
                "X-HEIGHT: %2\$d\r\n" +
                "\r\n"

        // 1280x720@25
        private val H264_PREDEFINED_HEADER_1280x720 = byteArrayOf(
            0x21.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x01.toByte(),
            0x67.toByte(),
            0x42.toByte(),
            0x80.toByte(),
            0x20.toByte(),
            0xda.toByte(),
            0x01.toByte(),
            0x40.toByte(),
            0x16.toByte(),
            0xe8.toByte(),
            0x06.toByte(),
            0xd0.toByte(),
            0xa1.toByte(),
            0x35.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x01.toByte(),
            0x68.toByte(),
            0xce.toByte(),
            0x06.toByte(),
            0xe2.toByte(),
            0x32.toByte(),
            0x24.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x7a.toByte(),
            0x83.toByte(),
            0x3d.toByte(),
            0xae.toByte(),
            0x37.toByte(),
            0x00.toByte(),
            0x00.toByte()
        )

        // 800x480@25
        private val H264_PREDEFINED_HEADER_800x480 = byteArrayOf(
            0x21.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x01.toByte(),
            0x67.toByte(),
            0x42.toByte(),
            0x80.toByte(),
            0x20.toByte(),
            0xda.toByte(),
            0x03.toByte(),
            0x20.toByte(),
            0xf6.toByte(),
            0x80.toByte(),
            0x6d.toByte(),
            0x0a.toByte(),
            0x13.toByte(),
            0x50.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x01.toByte(),
            0x68.toByte(),
            0xce.toByte(),
            0x06.toByte(),
            0xe2.toByte(),
            0x32.toByte(),
            0x24.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x7a.toByte(),
            0x83.toByte(),
            0x3d.toByte(),
            0xae.toByte(),
            0x37.toByte(),
            0x00.toByte(),
            0x00.toByte()
        )
    }
}
