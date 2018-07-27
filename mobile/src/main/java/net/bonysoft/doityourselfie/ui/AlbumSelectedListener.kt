package net.bonysoft.doityourselfie.ui

import net.bonysoft.doityourselfie.photos.model.CompleteAlbum

interface AlbumSelectedListener {

    fun onAlbumSelected(completeAlbum: CompleteAlbum)
}