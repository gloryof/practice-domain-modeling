package jp.glory.channel.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SubscriberTest {

    @Nested
    inner class TestIsSubscribed {
        @Test
        fun `Return true when chanel is subscribed`() {
            val targetId = ChanelId("subscribed-id-1")
            val subscribedChannelIds: List<ChanelId> = listOf(
                targetId
            )
            val sut = createSut(
                subscribedChannelIds = subscribedChannelIds
            )

            assertTrue(sut.isSubscribed(targetId))
        }

        @Test
        fun `Return false when chanel is subscribed`() {
            val targetId = ChanelId("subscribed-id-1")
            val subscribedChannelIds: List<ChanelId> = emptyList()
            val sut = createSut(
                subscribedChannelIds = subscribedChannelIds
            )

            assertFalse(sut.isSubscribed(targetId))
        }
    }

    private fun createSut(
        id: SubscriberId = SubscriberId("test-subscriber-id"),
        subscribedChannelIds: List<ChanelId> = listOf(
            ChanelId("subscribed-id-1"),
            ChanelId("subscribed-id-2"),
        )
    ): Subscriber =
        Subscriber(
            id = id,
            subscribedChannelIds = subscribedChannelIds
        )
}