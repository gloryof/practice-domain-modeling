package jp.glory.channel.domain.model

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import jp.glory.base.domain.DomainErrorCode
import jp.glory.channel.domain.event.AcceptedInvite
import jp.glory.channel.domain.event.InvitedChannel
import jp.glory.channel.domain.event.UploadedMovie
import java.io.InputStream
import java.time.Clock
import java.time.OffsetDateTime
import java.util.UUID

class ManageChannel(
    val id: ChannelId,
    private val owners: MutableList<ChannelOwnerId>,
    private val inviting: MutableMap<InvitedUserId, Inviting>
) {
    fun upload(
        uploadUserId: ChannelOwnerId,
        movieTitle: MovieTitle,
        releaseAt: ReleaseAt,
        binary: InputStream,
        idGenerator: MovieIdGenerator
    ): Result<UploadedMovie, DomainErrorCode> =
        if (!owners.contains(uploadUserId)) {
            Err(DomainErrorCode.NotHaveUploadMovieAuthority)
        } else {
            Ok(
                UploadedMovie(
                    movieId = idGenerator.generate(),
                    channelId = id,
                    title = movieTitle,
                    releaseAt = releaseAt,
                    binary = binary
                )
            )
        }

    fun invite(
        inviterOwnerId: ChannelOwnerId,
        invitedUserId: InvitedUserId,
        limitAt: OffsetDateTime
    ): Result<InvitedChannel, DomainErrorCode> =
        if (!owners.contains(inviterOwnerId)) {
            Err(DomainErrorCode.NotHaveInviteAuthority)
        } else {
            Ok(
                InvitedChannel(
                    channelId = id,
                    invitedUserId = invitedUserId,
                    limitAt = limitAt
                )
            )
        }

    fun acceptInvite(
        invitedUserId: InvitedUserId,
        clock: Clock
    ): Result<AcceptedInvite, DomainErrorCode> {
        val inviting = inviting[invitedUserId]
            ?: return Err(DomainErrorCode.NotInvited)

        return if (inviting.isNotExpired(clock)) {
            Ok(
                AcceptedInvite(
                    channelId = id,
                    invitedUserId = inviting.invitedUserId
                )
            )
        } else {
            Err(DomainErrorCode.NotInvited)
        }
    }

    fun handleInvitedChannel(
        event: InvitedChannel
    ) {
        inviting[event.invitedUserId] = Inviting(
            invitedUserId = event.invitedUserId,
            limitAt = event.limitAt
        )
    }

    fun handleAcceptedInvite(
        event: AcceptedInvite
    ): Result<Unit, DomainErrorCode> {
        if (!inviting.contains(event.invitedUserId)) {
            return Err(DomainErrorCode.NotInvited)
        }

        inviting.remove(event.invitedUserId)
        owners.add(ChannelOwnerId(event.invitedUserId.value))

        return Ok(Unit)
    }

    fun getInviting(userId: InvitedUserId): Inviting? =
        inviting[userId]

    fun getOwners(): List<ChannelOwnerId> =
        owners.toList()

}
class ChannelIdIdGenerator {
    fun generate(): ChannelId =
        ChannelId(UUID.randomUUID().toString())
}

class Inviting(
    val invitedUserId: InvitedUserId,
    val limitAt: OffsetDateTime
) {
    fun isNotExpired(clock: Clock): Boolean {
        val now = OffsetDateTime.now(clock)

        return limitAt.equals(now) || limitAt.isAfter(now)
    }
}

class MovieIdGenerator {
    fun generate(): MovieId =
        MovieId(UUID.randomUUID().toString())
}

@JvmInline
value class ChannelOwnerId(val value: String)

@JvmInline
value class InvitedUserId(val value: String)

class MovieId(val value: String)

@JvmInline
value class MovieTitle(val value: String) {
    init {
        require(value.length < 100)
    }
}

@JvmInline
value class ReleaseAt(val value: OffsetDateTime)