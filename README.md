# (Possibly) SmartThings

## [Arrival lights](https://github.com/twisty/PossiblySmartThings/blob/master/smartapps/twisty/arrival-lights.src/)

This SmartApp temporarily switches things on (like light bulbs) when any specified presence sensor arrives.

If a bulb is already on, we set it's brightness to 100%.

Then, after a configuarable number of minutes, the switches / lights are restored to the state they were in before they were turned on by this SmartApp.

## [Motion lights](https://github.com/twisty/PossiblySmartThings/blob/master/smartapps/twisty/motion-lights.src/)

This SmartApp temporarily switches things on (like light bulbs) when motion is sensed.

If a bulb is already on, we set it's brightness to 100%.

When the motion stops, we wait for a configuarable number of minutes, then restore the switches / lights to the state they were in before they were turned on by this SmartApp.
