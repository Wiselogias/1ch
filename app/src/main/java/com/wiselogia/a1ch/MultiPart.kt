package com.wiselogia.a1ch

import java.io.*
import java.net.URLConnection

/*
 * DO NOT FORGET TO PUT package ...; HERE!
 * DO _NOT_ USE com.example.*!
 *
 * package io.github.[MyName].android.hw4;
 */



/**
 * Helper for `multipart/form-data` requests
 */
class MultipartTool(private val connectionOS: OutputStream, private val boundary: String) :
    Closeable {
    private val connectionW: Writer
    @Throws(IOException::class)
    fun appendJsonField(fieldName: String?, data: String?) {
        appendField(fieldName, data, "application/json; charset=utf-8")
    }

    @Throws(IOException::class)
    fun appendField(
        fieldName: String?,
        data: String?,
        contentType: String?
    ) {
        connectionW.append("--").append(boundary).append(CRLF)
            .append("Content-Disposition: form-data; name=\"").append(fieldName).append('"')
            .append(CRLF)
            .append("Content-Type: ").append(contentType).append(CRLF)
            .append(CRLF)
            .append(data).append(CRLF)
            .flush()
    }

    @Throws(IOException::class)
    fun appendFile(
        fieldName: String?,
        data: ByteArray,
        contentType: String?,
        fileName: String?
    ) {
        appendFile(fieldName, data, 0, data.size, contentType, fileName)
    }

    @Throws(IOException::class)
    fun appendFile(
        fieldName: String?,
        data: ByteArray?,
        offset: Int,
        len: Int,
        contentType: String?,
        fileName: String?
    ) {
        appendFilePreamble(fieldName, contentType, (len - offset).toLong(), fileName)
        connectionOS.write(data, offset, len)
        appendFilePS()
    }

    @JvmOverloads
    @Throws(IOException::class)
    fun appendFile(
        fieldName: String?,
        file: File,
        contentType: String? = URLConnection.guessContentTypeFromName(file.name)
    ) {
        val fis = FileInputStream(file)
        try {
            appendFile(fieldName, fis, contentType, file.length(), file.absolutePath)
        } finally {
            fis.close()
        }
    }

    /**
     * Appends file provided via [InputStream]. It is caller responsibility
     * to close that stream.
     */
    @Throws(IOException::class)
    fun appendFile(
        fieldName: String?,
        data: InputStream,
        contentType: String?,
        contentLength: Long,
        fileName: String?
    ) {
        appendFilePreamble(fieldName, contentType, contentLength, fileName)
        val buffer = ByteArray(4096)
        var n: Int
        while (data.read(buffer).also { n = it } != -1) {
            connectionOS.write(buffer, 0, n)
        }
        appendFilePS()
    }

    @Throws(IOException::class)
    override fun close() {
        connectionW.append(CRLF)
            .append("--").append(boundary).append("--")
            .append(CRLF)
            .close()
    }

    @Throws(IOException::class)
    private fun appendFilePreamble(
        fieldName: String?,
        contentType: String?,
        contentLength: Long,
        fileName: String?
    ) {
        connectionW.append("--").append(boundary).append(CRLF)
            .append("Content-Disposition: form-data; name=\"").append(fieldName)
            .append("\"; filename=\"").append(fileName).append('"').append(CRLF)
            .append("Content-Type: ").append(contentType).append(CRLF)
            .append("Content-Length: ").append(java.lang.Long.toString(contentLength)).append(CRLF)
            .append("Content-Transfer-Encoding: binary").append(CRLF)
            .append(CRLF)
            .flush()
    }

    @Throws(IOException::class)
    private fun appendFilePS() {
        connectionOS.flush()
        connectionW.append(CRLF).flush()
    }

    companion object {
        private const val CRLF = "\r\n"

        /**
         * [System.currentTimeMillis]-based unique char sequence generator
         * @return some sequence
         */
        fun generateBoundary(): String {
            return "------" + System.currentTimeMillis() + "------"
        }
    }

    /**
     * Wrapper for [OutputStream] from [HttpURLConnection]
     * which allows easily send `multipart/form-data` requests.
     *
     *
     * **NOTE:** any other [HttpURLConnection] setup must be done
     * before opening connection (via e.g. getting output stream) for sending
     * multipart requests. This class *does not* perform such actions.
     *
     * @param connectionOS output stream from [HttpURLConnection.getOutputStream]
     * @param boundary some unique data, must be put in `Content-Type` header
     * like this:
     * <pre>conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);</pre>
     * @see .generateBoundary
     */
    init {
        connectionW = OutputStreamWriter(connectionOS)
    }
}