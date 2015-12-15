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

<b>MOD #2: Force photo mode on start</b>
In manual mode, the camera will always start in photo mode, even if the last mode used was video.

<b>MOD #3: Force auto mode on start</b>
The camera will always start in "Auto" mode, when startes from menu, shortcut or history. This mod does not apply when the camera is started from the camera button or lockscreen. In this case, the default "Superior Auto" mode will be started.

<b>MOD #4: Keep Geo-Tags setting ON</b>

This mod prevents a very annoying behavior of the stock camera app. Whenever you switch the camera to another mode than superior auto or manual, it will set the Geo-Tags setting to OFF, if GPS has been disallowed in the system-wide android settings. It will stay OFF in all camera modes until you set it to ON manually again, even if GPS has been allowed again in the system settings. The result of this behavior is that most of the pictures I made with my phone lack the location tag, although I never disabled the geo tags intentionally in the camera settings.

This mod prevents this behavior, the Geo-Tags setting will stay set to ON until you deliberately disable it.

<b>MOD #5: Enable location setting in system on start</b>
This will enable location in the android system settings on camera start/resume, to make sure that geo tags can always get recorded (see also MOD #7 to restore the previous location settings on camera exit.

This mod applies only to the camera modes "superior auto" and "manual". When starting other modes, the location setting will stay untouched. In combination with MOD #7, this means: if GPS gets activated by starting one of this 2 modes and MOD #7 is activated, switching to another mode will deactivate GPS again. To record geo-tags in the other modes, you will still have to activate GPS manually in the system settings if MOD #7 is activated.This restriction is due to technical limitations due to the fact that each camera mode is an independent app, and these apps cannot communicate without any time lag. But this would be necessary to make this mod work reliably across different modes.

<b>MOD #6: Choose location mode setting</b>
This mod enables to choose the mode for acquiring location to be set in system. It can be either GPS, networks or both.
This Xposed module will never disable a mode that was activated prior to camera start! E.g., if before camera start location was enabled in system and set to GPS, and you choose to enable location based on networks in this module, it will set the location mode to GPS and networks on camera start.

<b>MOD #7: Restore location setting in system on exit</b>
When exiting the camera, this mod will restore the location settings in system to their state before camera start. It applies only to the modes "superior auto" and "manual".

<b>MOD #8: Mod geo-tag icon</b>
Show a blue geo-tag icon when location is fixed by networks and a white one for GPS-fix.

<b>Credits:</b>

Many thanks to:

<ul>
<li><b>rovo89</b> for the magnific Xposed framework.</li>
<li><b>venkat kamesh</b> for a tutorial on how to hack the camera UI on a smali basis, which led me to make my first steps in reverse engineering of android APKs.</li>
</ul>

