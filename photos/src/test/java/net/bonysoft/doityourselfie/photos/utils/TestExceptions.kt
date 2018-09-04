package net.bonysoft.doityourselfie.photos.utils

fun String?.clean(): String? =
        this?.let {
            substringBefore("?")
        }