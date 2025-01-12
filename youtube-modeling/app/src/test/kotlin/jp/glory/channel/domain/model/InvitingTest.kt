package jp.glory.channel.domain.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneOffset

class InvitingTest {
    @Nested
    inner class TestIsNotExpired {
        @Test
        fun `Return true, when limit is after`() {
            val clock = Clock.fixed(
                OffsetDateTime.now().toInstant(),
                ZoneOffset.UTC
            )
            val limitAt = OffsetDateTime.now(clock).plusSeconds(1)
            val sut = Inviting(
                invitedUserId = InvitedUserId("test-inviting-id"),
                limitAt = limitAt
            )

            Assertions.assertTrue(sut.isNotExpired(clock))
        }

        @Test
        fun `Return true, when limit is equal`() {
            val clock = Clock.fixed(
                OffsetDateTime.now().toInstant(),
                ZoneOffset.UTC
            )
            val limitAt = OffsetDateTime.now(clock)
            val sut = Inviting(
                invitedUserId = InvitedUserId("test-inviting-id"),
                limitAt = limitAt
            )

            Assertions.assertTrue(sut.isNotExpired(clock))
        }

        @Test
        fun `Return false, when limit is before`() {
            val clock = Clock.fixed(
                OffsetDateTime.now().toInstant(),
                ZoneOffset.UTC
            )
            val limitAt = OffsetDateTime.now(clock).minusSeconds(1)
            val sut = Inviting(
                invitedUserId = InvitedUserId("test-inviting-id"),
                limitAt = limitAt
            )

            Assertions.assertFalse(sut.isNotExpired(clock))
        }
    }
}