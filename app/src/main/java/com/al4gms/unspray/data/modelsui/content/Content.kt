package com.al4gms.unspray.data.modelsui.content

import com.al4gms.unspray.data.modelsdb.content.CollectionDbEntities
import com.al4gms.unspray.data.modelsdb.content.ContentDB
import com.al4gms.unspray.data.modelsdb.content.TagDB
import com.al4gms.unspray.data.modelsdb.content.photo.ExifDB
import com.al4gms.unspray.data.modelsdb.content.photo.LocationDB
import com.al4gms.unspray.data.modelsdb.content.photo.PhotoDbEntities
import com.al4gms.unspray.data.modelsdb.content.photo.PhotoUrlsDB
import com.al4gms.unspray.data.modelsdb.user.UserDB
import com.al4gms.unspray.data.modelsui.Links
import com.al4gms.unspray.data.modelsui.user.User
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

sealed class Content {
    @JsonClass(generateAdapter = true)
    data class Collection(
        val id: String,
        val title: String,
        val description: String?,
        @Json(name = "total_photos")
        val totalPhotos: Int,
        val private: Boolean,
        @Json(name = "cover_photo")
        val coverPhoto: Photo,
        val user: User,
        val tags: List<Tag>?,
    ) : Content() {
        fun toCollectionDbEntities(): CollectionDbEntities {
            return CollectionDbEntities(
                collection = ContentDB.Collection(
                    id = id,
                    title,
                    description,
                    totalPhotos,
                    private,
                    user.id,
                ),
                user = UserDB(
                    id = this.user.id,
                    username = this.user.username,
                    name = this.user.name,
                    bio = this.user.bio,
                    location = this.user.location,
                    totalLikes = this.user.totalLikes,
                    totalPhotos = this.user.totalPhotos,
                    totalCollections = this.user.totalCollections,
                    instagramUsername = this.user.instagramUsername,
                    email = this.user.email,
                    downloads = this.user.downloads,
                    profileImageSmall = user.profileImage.small,
                    profileImageMedium = user.profileImage.small,
                    profileImageLarge = user.profileImage.small,
                ),
                ContentDB.Photo(
                    coverPhoto.id,
                    coverPhoto.createdAt,
                    coverPhoto.width,
                    coverPhoto.height,
                    coverPhoto.blurHash ?: "",
                    coverPhoto.downloads,
                    coverPhoto.likes,
                    coverPhoto.likedByUser,
                    coverPhoto.user.id,
                    coverPhoto.description,
                    coverPhoto.links.downloadLocation,
                    PhotoUrlsDB(
                        raw = coverPhoto.urls.raw,
                        full = coverPhoto.urls.raw,
                        regular = coverPhoto.urls.raw,
                        small = coverPhoto.urls.raw,
                        smallS3 = coverPhoto.urls.raw,
                        thumb = coverPhoto.urls.raw,
                    ),
                    null,
                    null,
                ),
                tags = tags?.map { tag ->
                    TagDB(
                        photoId = id,
                        type = tag.type,
                        title = tag.title,
                    )
                },
            )
        }
    }

    @JsonClass(generateAdapter = true)
    data class Photo(
        val id: String,
        @Json(name = "created_at")
        val createdAt: String,
        val width: Int,
        val height: Int,
        @Json(name = "blur_hash")
        val blurHash: String? = "",
        val downloads: Int?,
        val likes: Int,
        @Json(name = "liked_by_user")
        val likedByUser: Boolean,
        val user: User,
        val description: String?,
        val urls: PhotoUrls,
        val exif: Exif?,
        val location: Location?,
        val tags: List<Tag>?,
        val links: Links,
    ) : Content() {
        fun toDbEntities(): PhotoDbEntities {
            val photoUrlsDB = PhotoUrlsDB(
                raw = this.urls.raw,
                full = this.urls.full,
                regular = this.urls.regular,
                small = this.urls.small,
                thumb = this.urls.thumb,
                smallS3 = this.urls.smallS3,
            )

            val exifDB = ExifDB(
                make = this.exif?.make,
                model = this.exif?.make,
                name = this.exif?.make,
                exposureTime = this.exif?.make,
                aperture = this.exif?.aperture,
                focalLength = this.exif?.make,
                iso = this.exif?.iso,
            )

            val locationDB = LocationDB(
                city = this.location?.city,
                country = this.location?.city,
                latitude = this.location?.position?.latitude,
                longitude = this.location?.position?.longitude,
            )

            val photoDB = ContentDB.Photo(
                id = this.id,
                createdAt = this.createdAt,
                width = this.width,
                height = this.height,
                blurHash = this.blurHash ?: "",
                downloads = this.downloads,
                likes = this.likes,
                likedByUser = this.likedByUser,
                userId = this.user.id,
                description = this.description,
                downloadLink = this.links.downloadLocation,
                photoUrls = photoUrlsDB,
                exif = exifDB,
                locationDB,
            )
            val userDB = user.toUserDb()
            val tagsDB = this.tags?.map { tag ->
                TagDB(
                    photoId = this.id,
                    type = tag.type,
                    title = tag.title,
                )
            }
            return PhotoDbEntities(
                photoDB,
                userDB,
                photoUrlsDB,
                exifDB,
                locationDB,
                tagsDB ?: emptyList(),
            )
        }
    }
}
