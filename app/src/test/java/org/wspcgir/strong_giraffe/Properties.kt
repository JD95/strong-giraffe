package org.wspcgir.strong_giraffe

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll
import org.wspcgir.strong_giraffe.model.WeekRange
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

class Properties: DescribeSpec({
    describe("Week Ranges") {
        it("should correctly give start and end dates") {
            forAll(Arb.int(0..6)) { n ->
                val now = OffsetDateTime.of(2024,9,22 + n,15,43,3,4, ZoneOffset.of("-07:00"))
                val expectedStart = OffsetDateTime.of(2024,9,22,0,0,0,0, ZoneOffset.of("-07:00"))
                val expectedEnd = OffsetDateTime.of(2024,9,29,0,0,0,0, ZoneOffset.of("-07:00"))
                val range = WeekRange.forInstant(
                    now.toInstant(),
                    TimeZone.getTimeZone(now.toZonedDateTime().zone)
                )
                assertSoftly {
                    expectedStart.dayOfMonth shouldBe range.start.dayOfMonth
                    expectedEnd.dayOfMonth shouldBe range.end.dayOfMonth
                }
                true
            }
        }
    }
})
