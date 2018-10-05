# Do-It-Yourselfie

This project is an experiment based on Android Things and Google Photos API that will let you create a *photo boot* (like those usually found at weddings) that will automatically upload the taken pictures to Google Photos albums.

## Our setup
Our system was based on a Raspberry PI equipped with a Rainbow HAT, an external 21,5" monitor (without touchscreen), a big red *Normally Closed (NC)* button to snap the pictures and an external camera module.

Since Google Photos requires an authentication, we provided a companion Android app that will let you log into an account and will share the token with Android Things using Nearby API.

### Disclaimer
We did not equip our board with a 4G module, so we set up a mobile hotspot and that was enough to upload the pictures to the album.

_____

## Working schema
### Project schematics

![schematics][schema-img]

### Authentication

![authentication][auth-img]

### Photo taking and upload

![photo][photo-img]

____

## Technologies and libraries

The project is written in Kotlin and it's based on Android Things. For the networking layer, we used Retrofit and OkHttp.


[//]: Resources
[schema-img]: resources/schematics.png
[auth-img]: resources/authentication.png
[photo-img]: resources/photo.png