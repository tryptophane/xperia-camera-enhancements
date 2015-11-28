# xperia-cam-unbind-gallery
<b>Xposed module to let the user choose which gallery app gets opened by the camera</b>

<b><i>Tested on my Z1 Compact. Chances are good that it works with other Xperia Z devices, but I can't test it. Please try it out and tell me :-)<i></b>

In the latest Lollipop releases for the Xperia Z1 Compact and other Xperia Z devices, it is not possible anymore to choose which gallery app is used when you tap on the small picture thumbnail in the camera UI. It will always open the Sony Album app. But some people, like me, dislike the Sony Album app and prefer to use alternatives like Quickpic or others.

By installing this module, the camera app lets you choose again which gallery app to use when you tap on the thumbnail. Even better, it lets you choose different apps for normal JPEG photos and some special formats, like timeshift pictures or sound photos. For those kind of pictures, it is still advisable to use the Album app which supports the features of those formats (like playing sound on a picture).

The module has been implemented to work in the following camera modes (each of them has a separate implementation for launching the gallery and had to be hacked separatedly:

- Superior Auto
- Manual
- Background Defocus
- AR-Effect
- Panorama
- Creative Effect
- Sound Photo

In other camera modes, the camera will possibly still always use the Album app.

<b>If it does not work and you do not get asked which app to use, try to clear the defaults of the gallery app that gets opened by default!</b>



