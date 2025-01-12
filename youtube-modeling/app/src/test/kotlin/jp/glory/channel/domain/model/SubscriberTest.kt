package jp.glory.channel.domain.model

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SubscriberTest {

    @Nested
    inner class TestIsSubscribed {
        @Test
        fun `Return true when channel is subscribed`() {
            val targetId = ChannelId("subscribed-id-1")
            val subscribedChannelIds: List<ChannelId> = listOf(
                targetId
            )
            val sut = createSut(
                subscribedChannelIds = subscribedChannelIds
            )

            assertTrue(sut.isSubscribed(targetId))
        }

        @Test
        fun `Return false when channel is subscribed`() {
            val targetId = ChannelId("subscribed-id-1")
            val subscribedChannelIds: List<ChannelId> = emptyList()
            val sut = createSut(
                subscribedChannelIds = subscribedChannelIds
            )

            assertFalse(sut.isSubscribed(targetId))
        }
    }

    private fun createSut(
        id: SubscriberId = SubscriberId("test-subscriber-id"),
        subscribedChannelIds: List<ChannelId> = listOf(
            ChannelId("subscribed-id-1"),
            ChannelId("subscribed-id-2"),
        )
    ): Subscriber =
        Subscriber(
            id = id,
            subscribedChannelIds = subscribedChannelIds
        )
}