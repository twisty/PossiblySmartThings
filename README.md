# (Possibly) SmartThings

## [Arrival lights](https://github.com/twisty/PossiblySmartThings/blob/master/smartapps/twisty/arrival-lights.src/)

This SmartApp temporarily switches things on (like light bulbs) when a presence sensor arrives.

If a light is already on, we turn the brightness up to 100%.

Then, after a configuarable number of minutes, the switches / lights are restored to the state they were in before they were turned on by this SmartApp.

## [Motion lights](https://github.com/twisty/PossiblySmartThings/blob/master/smartapps/twisty/motion-lights.src/)

This SmartApp temporarily switches things on (like light bulbs) when a motion sensor is triggered.

If a light is already on, we increase the brightness a little.

When the motion stops, we wait for a configurable number of minutes, then restore the switches / lights to the state they were in before they were turned on by this SmartApp.
