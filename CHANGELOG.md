1.3.0
=====
 - Changed `dropLeft`/`~>` and `dropRight`/`<~` to require a `Codec[Unit]` on the dropped side.
   Previously, the requirement was a `Codec[X]` and an implicitly available `Monoid[X]`.
   To convert a `Codec[X]` to a `Codec[Unit]`, use the `unit` combinator.
 - Changed size based codecs to use `Long` instead of `Int` sizes.

1.2.1
=====
 - Added `fail` combinator
 - Added `mappedEnum` combinator
 - Added `vector` and `list` combinators and deprecated `repeated`
 - Added `vectorOfN` and `listOfN` combinators for count encoded values
 - Added `unit` and `unitM` combinators to convert a `Codec[A]` to a `Codec[Unit]`
 - Added `scodec.codecs.literals._` for implicitly converting literal values to constant codecs.
 - Added `constantLenient` codec that's equivalent to `constant` but does not validate that the decoded bits equal the constant value
 - Improved support for `as` method:
   - Better compiler error message when an implicit `CodecAsAux` is not found
   - Support for reverse bindings (e.g., `Codec[Point3D].as[Int :: Int :: Int :: HNil]`)
   - Support for singleton bindings without having to first lift `Codec[A]` to `Codec[A :: HNil]` (e.g., `case class Foo(x: Int); uint8.as[Foo]`)
 - Added `optional` combinator
 - Added `withDefault` and `withDefaultValue` combinators
 - Added `recover` and `lookahead` combinators
 - Added `dropUnits` combinator for `HList` codecs, greatly simplifying creation of codecs that have unit values due to use of `constant` or `ignore`.
   For example, `(uint8 :: ignore(4) :: uint4 :: ignore(3) :: uint5).dropUnits` results in a `Codec[Int :: Int :: Int :: HNil]`
 - Added support for xmapping polymorphic functions via `polyxmap` and `polyxmap` combinators
  - `polyxmap` takes 2 polymorphic functions, forward and reverse 
  - `polyxmap1` takes 1 polymorphic function that's used in both directions
  - both combinators work on `HList` codecs and value codecs

1.2.0
=====
 - Upgraded to Scalaz 7.1.0
 - Published ScalaDoc links in POM so that other projects can link to ScalaDoc

1.1.0
=====
 - Upgraded to Shapeless 2

1.0.0
=====
 - Added `endiannessDependent` combinator
 - Used `scodec.bits.ByteOrdering` instead of a boolean for indicating big endian vs little endian
 - Significant performance improvements

1.0.0-RC2
=========
 - Upgrade to scodec-bits 1.0.0-RC2

1.0.0-RC1
========
 - Added `bits` and `bytes` codecs that behave like `bits(size)`/`bytes(size)` but with no size constraint
 - Replaced discriminator support with new implementation that is much more general (thanks to Paul C)
 - Removed `Codec.{ encode, decode }` overloads that aliased `Codec#{ encode, decode }`
 - Introduced `encodeValid`, `decodeValue`, and `decodeValidValue` methods on both the `Codec` class and object
 - Added new combinators (`lazily`, `complete`, `compact`)
 - API docs

1.0.0-M3
========
 - Removed unnecessary dependencies from pom

1.0.0-M2
========
 - Changed group id from com.github.scodec to org.typelevel
 - Changed artifact id from scodec to scodec-core
 - Deprecated `scodec.{ BitVector, ByteVector }` in favor of `scodec.bits.{ BitVector, ByteVector }`
   - Deprecated forwarders will be removed in M3
 - Reduced public API
   - made many types package private
   - removed methods from `Codec` companion that existed directly on `Codec`

1.0.0-M1
========
 - JAR restructuring
   - scodec-bits: no dependency JAR containing BitVector, ByteVector, and supporting types
   - scodec: dependds on scodec-bits and adds encoding/decoding capabilities
   - See scodec-bits for list of improvements to BitVector and ByteVector
 - Package restructuring
   - scodec.bits package contains BitVector, ByteVector, and supporting types
   - scodec package contains main abstractions of encoding/decoding
   - scodec.codecs package contains reusable codecs
 - Encoder, Decoder, and GenCodec abstractions, which allow simpler transforms if a full Codec is not required
