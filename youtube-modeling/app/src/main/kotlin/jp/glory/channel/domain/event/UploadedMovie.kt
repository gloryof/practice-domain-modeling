package jp.glory.channel.domain.event

import jp.glory.channel.domain.model.ChanelId
import jp.glory.channel.domain.model.MovieId
import jp.glory.channel.domain.model.MovieTitle
import jp.glory.channel.domain.model.ReleaseAt
import java.io.InputStream

data class UploadedMovie(
    val movieId: MovieId,
    val chanelId: ChanelId,
    val title: MovieTitle,
    val releaseAt: ReleaseAt,
    val binary: InputStream,
)
