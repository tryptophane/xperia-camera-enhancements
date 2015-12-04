# xperia-camera-enhancements
<b>Xposed module with various mods for the stock camera app of Sony Xperia devices.</b>

<i><b>Note:</b> Tested on my Z1 Compact. Chances are good that it works well with other Xperia devices, as far as they have the same camera app. I would appreciate your feedback :-)</i>

<b>MOD #1: Let the user choose which gallery app gets opened by the camera</b>

In the latest Lollipop releases for the Xperia Z1 Compact and other Xperia Z devices, it is not possible anymore to choose which gallery app is used when you tap on the small picture thumbnail in the camera UI. It will always open the Sony Album app. But some people, like me, dislike the Sony Album app and prefer to use alternatives like Quickpic or others.

With this mod, the camera app lets you choose again which gallery app to use when you tap on the thumbnail. Even better, it lets you choose different apps for normal JPEG photos and some special formats, like timeshift pictures or sound photos. For those kind of pictures, it is still advisable to use the Album app which supports the features of those formats (like playing sound on a picture).

The mod has been implemented to work in the following camera modes (each of them has a separate implementation for launching the gallery and had to be hacked separatedly:

<ul>
<li>Superior Auto</li>
<li>Manual</li>
<li>Background Defocus</li>
<li>AR-Effect</li>
<li>Panorama</li>
<li>Creative Effect</li>
<li>Sound Photo</li>
<li>Timeshift burst</li>
</ul>

In other camera modes, the camera will possibly still always use the Album app.

If it does not work and you do not get asked which app to use, try to clear the defaults of the gallery app that gets opened by default!

<b>MOD #2: Keep Geo-Tags setting ON</b>

This mod prevents a very annoying behavior of the stock camera app. Whenever you switch the camera to another mode than superior auto or manual, it will set the Geo-Tags setting to OFF, if GPS has been disallowed in the system-wide android settings. It will stay OFF in all camera modes until you set it to ON manually again, even if GPS has been allowed again in the system settings. The result of this behavior is that most of the pictures I made with my phone lack the location tag, although I never disabled the geo tags intentionally in the camera settings.

This mod prevents this behavior, the Geo-Tags setting will stay set to ON until you deliberately disable it.

<b>MOD #3: Enable location setting in system on start</b>
This will enable the location setting in the android system on camera start/resume. It will disable it again on close/hide, but only if it has been enabled by this mod, not if location was already enabled before starting the camera.

<b>MOD #4: Force photo mode on start</b>

In manual mode, the camera will always start in photo mode, even if the last mode used was video.

<b>Credits:</b>

Many thanks to:

<ul>
<li><b>rovo89</b> for the magnific Xposed framework.</li>
<li><b>venkat kamesh</b> for a tutorial on how to hack the camera UI on a smali basis, which led me to make my first steps in reverse engineering of android APKs.</li>
</ul>

