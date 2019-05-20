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

import android.media.MediaFormat

/**
 * Created by yschi on 2015/5/28.
 */
object Common {
    val VIEWER_PORT = 53515

    val DISCOVER_PORT = 53515
    val DISCOVER_MESSAGE = "hello"

    val DEFAULT_SCREEN_WIDTH = 1280
    val DEFAULT_SCREEN_HEIGHT = 720
    val DEFAULT_SCREEN_DPI = 320
    val DEFAULT_VIDEO_BITRATE = 6144000
    val DEFAULT_VIDEO_FPS = 25
    val DEFAULT_VIDEO_MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC

    // Activity to service
    val MSG_REGISTER_CLIENT = 200
    val MSG_UNREGISTER_CLIENT = 201
    val MSG_STOP_CAST = 301

    val EXTRA_RESULT_CODE = "result_code"
    val EXTRA_RESULT_DATA = "result_data"
    val EXTRA_RECEIVER_IP = "receiver_ip"

    val EXTRA_SCREEN_WIDTH = "screen_width"
    val EXTRA_SCREEN_HEIGHT = "screen_height"
    val EXTRA_SCREEN_DPI = "screen_dpi"
    val EXTRA_VIDEO_FORMAT = "video_format"
    val EXTRA_VIDEO_BITRATE = "video_bitrate"

    val ACTION_STOP_CAST = "com.yschi.castscreen.ACTION_STOP_CAST"
}
