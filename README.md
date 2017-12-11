# (Possibly) SmartThings

## [Outside light](https://github.com/twisty/PossiblySmartThings/blob/master/smartapps/twisty/outside-light.src/)

This SmartApp temporarily switches things on (like light bulbs) when any specified presence sensor arrives.

If a bulb was already on, it's brightness is set to 100%.

Then, after a configuarable number of minutes, the switches / lights are restored to the state they were in before this SmartApp turned them on.
