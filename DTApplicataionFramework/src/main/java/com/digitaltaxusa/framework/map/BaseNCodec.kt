package com.digitaltaxusa.framework.map

import org.apache.commons.codec.BinaryDecoder
import org.apache.commons.codec.BinaryEncoder
import org.apache.commons.codec.DecoderException
import org.apache.commons.codec.EncoderException
import org.apache.commons.codec.binary.StringUtils
import java.util.*
import kotlin.math.min

/**
 * Defines common encoding/decoding methods for byte array encoders/decoders.
 *
 * @property decodedBlockSize Int Number of bytes in each full block of un-encoded data,
 * e.g. 4 for Base64 and 5 for Base32.
 * @property encodedBlockSize Int Number of bytes in each full block of encoded data,
 * e.g. 3 for Base64 and 8 for Base32.
 * @property pad Byte Represents bytes for encoding/decoding. Code of a Char is the value it
 * was constructed with, and the UTF-16 code unit corresponding to this Char.
 * @property lineLength Int If 0, use chunking with a length [lineLength].
 * @property chunkSeparatorLength Int The chunk separator length, if relevant.
 * @constructor
 */
abstract class BaseNCodec internal constructor(
    private val decodedBlockSize: Int,
    private val encodedBlockSize: Int,
    lineLength: Int, chunkSeparatorLength: Int
) : BinaryEncoder, BinaryDecoder {

    val pad = PAD_DEFAULT // instance variable just in case it needs to vary later

    /**
     * Chunk size for encoding. Not used when decoding.
     *
     * <p>A value of zero or less implies no chunking of the encoded data.
     * Rounded down to nearest multiple of encodedBlockSize.</p>
     */
    val lineLength: Int

    /**
     * Size of chunk separator. Not used unless [.lineLength] > 0.
     */
    private val chunkSeparatorLength: Int

    /**
     * Returns the amount of buffered data available for reading.
     *
     * @param context Interface to global information about an application environment.
     * @return The amount of buffered data available for reading.
     */
    private fun available(
        context: Context
    ): Int {
        // package protected for access from I/O streams
        return if (context.buffer != null) {
            context.pos - context.readPos
        } else {
            0
        }
    }

    /**
     * Increases our buffer by the [DEFAULT_BUFFER_RESIZE_FACTOR].
     *
     * @param context Interface to global information about an application environment.
     */
    private fun resizeBuffer(
        context: Context
    ): ByteArray? {
        if (context.buffer == null) {
            context.buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            context.pos = 0
            context.readPos = 0
        } else {
            val bufferSize = context.buffer?.size ?: 0
            val bytes = ByteArray(bufferSize * DEFAULT_BUFFER_RESIZE_FACTOR)
            System.arraycopy(context.buffer!!, 0, bytes, 0, bufferSize)
            context.buffer = bytes
        }
        return context.buffer
    }

    /**
     * Ensure that the buffer has room for size bytes.
     *
     * @param size The minimum spare space required.
     * @param context Interface to global information about an application environment.
     */
    fun ensureBufferSize(
        size: Int,
        context: Context
    ): ByteArray? {
        val bufferSize = context.buffer?.size ?: 0
        return if (context.buffer == null || bufferSize < context.pos + size) {
            resizeBuffer(context)
        } else {
            context.buffer
        }
    }

    /**
     * Extracts buffered data into the provided byte[] array, starting at position bPos,
     * up to a maximum of bAvail bytes. Returns how many bytes were actually extracted.
     *
     * Package protected for access from I/O streams.
     *
     * @param bytes The byte[] array to extract the buffered data into.
     * @param bPos Position in byte[] array to start extraction at.
     * @param bAvail Amount of bytes we're allowed to extract. We may extract fewer
     * (if fewer are available).
     * @param context Interface to global information about an application environment.
     * @return The number of bytes successfully extracted into the provided byte[] array.
     */
    fun readResults(
        bytes: ByteArray,
        bPos: Int,
        bAvail: Int,
        context: Context
    ): Int {
        if (context.buffer != null) {
            val length = min(available(context), bAvail)
            System.arraycopy(context.buffer!!, context.readPos, bytes, bPos, length)
            context.readPos += length
            if (context.readPos >= context.pos) {
                // hasData() will return false, and this method can return -1
                context.buffer = null
            }
            return length
        }
        return if (context.eof) {
            EOF
        } else {
            0
        }
    }

    /**
     * Encodes an Object using the Base-N algorithm. This method is provided in order to
     * satisfy the requirements of the Encoder interface, and will throw an EncoderException
     * if the supplied object is not of type byte[].
     *
     * @param obj Object to encode.
     * @return An object (of type byte[]) containing the Base-N encoded data which corresponds
     * to the byte[] supplied.
     * @throws EncoderException If the parameter supplied is not of type byte[].
     */
    @Throws(EncoderException::class)
    override fun encode(
        obj: Any
    ): ByteArray {
        if (obj !is ByteArray) {
            throw EncoderException("Parameter supplied to Base-N encode is not a byte[]")
        }
        return encode(obj)
    }

    /**
     * Decodes an Object using the Base-N algorithm. This method is provided in order to
     * satisfy the requirements of the Decoder interface, and will throw a DecoderException
     * if the supplied object is not of type byte[] or String.
     *
     * @param obj Object to decode.
     * @return An object (of type byte[]) containing the binary data which corresponds to
     * the byte[] or String supplied.
     * @throws DecoderException if the parameter supplied is not of type byte[].
     */
    @Throws(DecoderException::class)
    override fun decode(
        obj: Any
    ): ByteArray {
        return when (obj) {
            is ByteArray -> {
                decode(obj)
            }
            is String -> {
                decodeString(obj)
            }
            else -> {
                throw DecoderException("Parameter supplied to Base-N decode is not a byte[] or a String")
            }
        }
    }

    /**
     * Decodes a String containing characters in the Base-N alphabet.
     *
     * @param pArray A String containing Base-N character data.
     * @return A byte array containing binary data.
     */
    fun decodeString(
        pArray: String?
    ): ByteArray {
        return decode(StringUtils.getBytesUtf8(pArray))
    }

    /**
     * Decodes a byte[] containing characters in the Base-N alphabet.
     *
     * @param bytes A byte array containing Base-N character data.
     * @return A byte array containing binary data.
     */
    override fun decode(
        bytes: ByteArray
    ): ByteArray {
        if (bytes.isEmpty()) {
            return bytes
        }
        val context = Context()
        decode(bytes, 0, bytes.size, context)
        decode(bytes, 0, EOF, context) // notify decoder of EOF
        val result = ByteArray(context.pos)
        readResults(result, 0, result.size, context)
        return result
    }

    /**
     * Encodes a byte[] containing binary data, into a byte[] containing characters
     * in the alphabet.
     *
     * @param bytes A byte array containing binary data.
     * @return A byte array containing only the base alphabetic character data.
     */
    override fun encode(
        bytes: ByteArray
    ): ByteArray {
        if (bytes.isEmpty()) {
            return bytes
        }
        val context = Context()
        encode(bytes, 0, bytes.size, context)
        encode(bytes, 0, EOF, context) // notify encoder of EOF
        val buf = ByteArray(context.pos - context.readPos)
        readResults(buf, 0, buf.size, context)
        return buf
    }

    // package protected for access from I/O streams
    abstract fun encode(pArray: ByteArray?, i: Int, length: Int, context: Context)

    // package protected for access from I/O streams
    abstract fun decode(pArray: ByteArray?, i: Int, length: Int, context: Context)

    /**
     * Returns whether or not the `octet` is in the current alphabet.
     * Does not allow whitespace or pad.
     *
     * @param value The value to test.
     * @return True if the value is defined in the current alphabet, otherwise false.
     */
    protected abstract fun isInAlphabet(value: Byte): Boolean

    /**
     * Tests a given byte array to see if it contains any characters within the alphabet or PAD.
     *
     * Intended for use in checking line-ending arrays.
     *
     * @param bytes Byte array to test.
     * @return True if any byte is a valid character in the alphabet or PAD, otherwise false.
     */
    fun containsAlphabetOrPad(
        bytes: ByteArray?
    ): Boolean {
        if (bytes == null) {
            return false
        }
        for (element in bytes) {
            if (pad == element || isInAlphabet(element)) {
                return true
            }
        }
        return false
    }

    /**
     * Calculates the amount of space needed to encode the supplied array.
     *
     * @param bytes Byte[] array which will later be encoded.
     * @return Amount of space needed to encoded the supplied array.
     * Returns a long since a max-len array will require > Integer.MAX_VALUE.
     */
    fun getEncodedLength(
        bytes: ByteArray
    ): Long {
        // calculate non-chunked size - rounded up to allow for padding
        // cast to long is needed to avoid possibility of overflow
        var length = (bytes.size + decodedBlockSize - 1) /
                decodedBlockSize * encodedBlockSize.toLong()
        if (lineLength > 0) {
            // using chunking
            // round up to nearest multiple
            length += (length + lineLength - 1) / lineLength * chunkSeparatorLength
        }
        return length
    }

    /**
     * Holds thread context so classes can be thread-safe.
     *
     * This class is not itself thread-safe; each thread must allocate its own copy.
     */
    class Context {

        /**
         * Place holder for the bytes we're dealing with for our based logic.
         * Bitwise operations store and extract the encoding or decoding from this variable.
         */
        var ibitWorkArea = 0

        /**
         * Place holder for the bytes we're dealing with for our based logic.
         * Bitwise operations store and extract the encoding or decoding from this variable.
         */
        var lbitWorkArea: Long = 0

        /**
         * Buffer for streaming.
         */
        var buffer: ByteArray? = null

        /**
         * Position where next character should be written in the buffer.
         */
        var pos = 0

        /**
         * Position where next character should be read from the buffer.
         */
        var readPos = 0

        /**
         * Boolean flag to indicate the EOF has been reached. Once EOF has been reached,
         * this object becomes useless and must be thrown away.
         */
        var eof = false

        /**
         * Variable tracks how many characters have been written to the current line.
         * Only used when encoding. We use it to make sure each encoded line never goes beyond
         * lineLength (if lineLength > 0).
         */
        var currentLinePos = 0

        /**
         * Writes to the buffer only occur after every 3/5 reads when encoding, and every 4/8
         * reads when decoding.This variable helps track that.
         */
        var modulus = 0

        /**
         * Returns a String useful for debugging (especially within a debugger).
         *
         * @return String useful for debugging.
         */
        override fun toString(): String {
            return String.format(
                "%s[buffer=%s, currentLinePos=%s, eof=%s, ibitWorkArea=%s, lbitWorkArea=%s, " +
                        "modulus=%s, pos=%s, readPos=%s]",
                this.javaClass.simpleName,
                Arrays.toString(buffer),
                currentLinePos,
                eof,
                ibitWorkArea,
                lbitWorkArea,
                modulus,
                pos,
                readPos
            )
        }
    }

    companion object {

        /**
         * The {@value} character limit does not count the trailing CRLF, but counts all other
         * characters, including any equal signs.
         *
         * Ref-
         * [RFC 2045 section 6.8](http://www.ietf.org/rfc/rfc2045.txt)
         */
        internal const val MIME_CHUNK_SIZE = 76

        /**
         * Mask used to extract 8 bits, used in decoding bytes.
         */
        internal const val MASK_8BITS = 0xff

        /**
         * Get the default buffer size. Can be overridden.
         *
         * Defines the default buffer size - currently {@value}
         * - must be large enough for at least one encoded block+separator.
         *
         * @return [DEFAULT_BUFFER_SIZE].
         */
        internal const val DEFAULT_BUFFER_SIZE = 8192

        /**
         * Byte used to pad output.
         *
         * <p>Allow static access to default.</p>
         */
        internal const val PAD_DEFAULT = '='.code.toByte()
        internal const val EOF = -1
        internal const val DEFAULT_BUFFER_RESIZE_FACTOR = 2
    }

    init {
        val useChunking = lineLength > 0 && chunkSeparatorLength > 0
        this.lineLength = if (useChunking) {
            lineLength / encodedBlockSize * encodedBlockSize
        } else {
            0
        }
        this.chunkSeparatorLength = chunkSeparatorLength
    }
}