# xperia-camera-enhancements
<b>Xposed module which adds some hacks to the stock camera app of Sony Xperia devices</b>

<i><b>Note 1:</b> This module has alpha-status. For now, it doesn't allow any settings. I will add a settings page in an upcoming release to switch on or off the two hacks implemented till now in this module</i> 

<i><b>Note 2:</b> Tested on my Z1 Compact. Chances are good that it works well with other Xperia devices, as far as they have the same camera app. I would appreciate your feedback :-)</i>

<b>HACK #1: Let the user choose which gallery app gets opened by the camera</b>

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
- Timeshift burst

In other camera modes, the camera will possibly still always use the Album app.

If it does not work and you do not get asked which app to use, try to clear the defaults of the gallery app that gets opened by default!

<b>Hack #2</b> Keep Geo-Tags setting ON</b>

This hack prevents a very annoying behavior of the stock camera app. Whenever you switch the camera to another mode than superior auto or manual, it will set the Geo-Tags setting to OFF, if GPS has been disallowed in the system-wide android settings. It will stay OFF in all camera modes until you set it to ON manually again, even if GPS has been allowed again in the system settings. The result of this behavior is that most of the pictures I made with my phone lack the location tag, although I never disabled the geo tags intentionally in the camera settings.

This module prevents this behavior, the Geo-Tags setting will stay set to ON until you deliberately disable it.

