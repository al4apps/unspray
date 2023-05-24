package com.al4gms.unspray.data.modelsdb.content.photo

import com.al4gms.unspray.data.modelsdb.content.ContentDB
import com.al4gms.unspray.data.modelsdb.content.TagDB
import com.al4gms.unspray.data.modelsdb.user.UserDB
import com.al4gms.unspray.data.modelsui.Links
import com.al4gms.unspray.data.modelsui.content.Content
import com.al4gms.unspray.data.modelsui.content.Exif
import com.al4gms.unspray.data.modelsui.content.Location
import com.al4gms.unspray.data.modelsui.content.PhotoUrls
import com.al4gms.unspray.data.modelsui.content.PositionGeographic
import com.al4gms.unspray.data.modelsui.content.Tag
import com.al4gms.unspray.data.modelsui.user.ProfileImageUrls
import com.al4gms.unspray.data.modelsui.user.User

data class PhotoDbEntities(
    val photoDB: ContentDB.Photo,
    val userDB: UserDB,
    val photoUrlsDB: PhotoUrlsDB,
    val exifDB: ExifDB?,
    val locationDB: LocationDB?,
    val tagsDB: List<TagDB>,
) {
    fun toPhoto(): Content.Photo {
        return Content.Photo(
            id = photoDB.id,
            createdAt = photoDB.createdAt,
            width = photoDB.width,
            height = photoDB.height,
            blurHash = photoDB.blurHash,
            downloads = photoDB.downloads,
            likes = photoDB.likes,
            likedByUser = photoDB.likedByUser,
            user = getUser(),
            description = photoDB.description,
            urls = getUrls(),
            exif = getExif(),
            location = getLocation(),
            tags = getTags(),
            links = Links(photoDB.downloadLink),
        )
    }

    private fun getUser(): User {
        return User(
            id = userDB.id,
            username = userDB.username,
            name = userDB.name,
            bio = userDB.name,
            profileImage = ProfileImageUrls(
                small = userDB.profileImageSmall,
                medium = userDB.profileImageMedium,
                large = userDB.profileImageLarge,
            ),
            location = userDB.location,
            totalLikes = userDB.totalLikes,
            totalPhotos = userDB.totalPhotos,
            totalCollections = userDB.totalCollections,
            instagramUsername = userDB.instagramUsername,
            email = userDB.email,
            downloads = userDB.downloads,
        )
    }

    private fun getUrls(): PhotoUrls {
        return PhotoUrls(
            raw = photoUrlsDB.raw,
            full = photoUrlsDB.full,
            regular = photoUrlsDB.regular,
            small = photoUrlsDB.small,
            thumb = photoUrlsDB.thumb,
            smallS3 = photoUrlsDB.smallS3,
        )
    }

    private fun getExif(): Exif? {
        if (exifDB == null) return null
        return Exif(
            make = exifDB.make,
            model = exifDB.model,
            name = exifDB.name,
            exposureTime = exifDB.exposureTime,
            aperture = exifDB.aperture,
            focalLength = exifDB.focalLength,
            iso = exifDB.iso,
        )
    }

    private fun getLocation(): Location? {
        if (locationDB == null) return null
        return Location(
            city = locationDB.city,
            country = locationDB.country,
            position = getPosition(),
        )
    }

    private fun getPosition(): PositionGeographic? {
        if (locationDB == null) return null
        return PositionGeographic(
            latitude = locationDB.latitude,
            longitude = locationDB.longitude,
        )
    }

    private fun getTags(): List<Tag>? {
        return tagsDB.map {
            Tag(
                type = it.type,
                title = it.title,
            )
        }
    }
}
