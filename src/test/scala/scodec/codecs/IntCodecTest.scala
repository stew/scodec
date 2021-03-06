package scodec
package codecs

import scalaz.\/
import scalaz.syntax.std.option._
import org.scalacheck.Gen
import scodec.bits.BitVector

class IntCodecTest extends CodecSuite {
  def check(low: Int, high: Int)(f: (Int) => Unit) {
    forAll(Gen.choose(low, high)) { n =>
      whenever(n >= low) { f(n) }
    }
  }

  "the int32 codec" should { "roundtrip" in { forAll { (n: Int) => roundtrip(int32, n) } } }
  "the int32L codec" should { "roundtrip" in { forAll { (n: Int) => roundtrip(int32L, n) } } }
  "the uint24L codec" should { "roundtrip" in { check(0, (1 << 24) - 1) { (n: Int) => roundtrip(uint24L, n) } } }
  "the int16 codec" should { "roundtrip" in { check(-32768, 32767) { (n: Int) => roundtrip(int16, n) } } }
  "the uint16 codec" should { "roundtrip" in { check(0, 65535) { (n: Int) => roundtrip(uint16, n) } } }
  "the uint16L codec" should { "roundtrip" in { check(0, 65535) { (n: Int) => roundtrip(uint16L, n) } } }
  "the uint8 codec" should { "roundtrip" in { check(0, 255) { (n: Int) => roundtrip(uint8, n) } } }
  "the uint8L codec" should { "roundtrip" in { check(0, 255) { (n: Int) => roundtrip(uint8L, n) } } }
  "the uint4 codec" should { "roundtrip" in { check(0, 1 << 3) { (n: Int) => roundtrip(uint4, n) } } }
  "the uint4L codec" should { "roundtrip" in { check(0, (1 << 4) - 1) { (n: Int) => roundtrip(uint4L, n) } } }
  "the uint(n) codec" should { "roundtrip" in { uint(13).encode(1) shouldBe \/.right(BitVector.low(13).set(12)) } }
  "the uintL(n) codec" should { "roundtrip" in { uintL(13).encode(1) shouldBe \/.right(BitVector.low(13).set(7)) } }

  "the int codecs" should {
    "support endianess correctly" in {
      forAll { (n: Int) =>
        val bigEndian = int32.encode(n).toOption.err("big").toByteVector
        val littleEndian = int32L.encode(n).toOption.err("little").toByteVector
        littleEndian shouldBe bigEndian.reverse
      }
      check(0, 15) { (n: Int) =>
        val bigEndian = uint4.encode(n).valueOr(sys.error).toByteVector
        val littleEndian = uint4L.encode(n).valueOr(sys.error).toByteVector
        littleEndian shouldBe bigEndian.reverse
      }
      check(0, (1 << 24) - 1) { (n: Int) =>
        val bigEndian = uint24.encode(n).valueOr(sys.error).toByteVector
        val littleEndian = uint24L.encode(n).valueOr(sys.error).toByteVector
        littleEndian shouldBe bigEndian.reverse
      }
      check(0, 8191) { (n: Int) =>
        whenever(n >= 0 && n <= 8191) {
          val bigEndian = uint(13).encode(n).valueOr(sys.error)
          val littleEndian = uintL(13).encode(n).valueOr(sys.error).toByteVector
          val flipped = BitVector(littleEndian.last).take(5) ++ littleEndian.init.reverse.toBitVector
          flipped shouldBe bigEndian
        }
      }
    }

    "return an error when value to encode is out of legal range" in {
      int16.encode(65536) shouldBe \/.left("65536 is greater than maximum value 32767 for 16-bit signed integer")
      int16.encode(-32769) shouldBe \/.left("-32769 is less than minimum value -32768 for 16-bit signed integer")
      uint16.encode(-1) shouldBe \/.left("-1 is less than minimum value 0 for 16-bit unsigned integer")
    }

    "return an error when decoding with too few bits" in {
      int16.decode(BitVector.low(8)) shouldBe \/.left("cannot acquire 16 bits from a vector that contains 8 bits")
    }
  }
}
