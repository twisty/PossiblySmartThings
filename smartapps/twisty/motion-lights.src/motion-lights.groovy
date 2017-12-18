/**
 *  Copyright 2017 Tim Brayshaw
 */
definition(
    name: "Motion Lights",
    namespace: "twisty",
    author: "Tim Brayshaw",
    description: "Temporarily turn on lights when there is motion, and restore the lighting after motion stops.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX4Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)

preferences {
    section("When motion is detected...") {
        input "motion", "capability.motionSensor", title: "Motion sensor:"
    }
    section("These devices are switched on...") {
        input "switches", "capability.switch", title: "Choose switches or lights:", multiple: true
    }
    section("Turn off again after...") {
        input "minutesLater", "number", title: "Minutes:", defaultValue: 5, range: "1..*" 
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    captureSwitchStatus()
    state.isTurnedOn = false
    subscribe(switches, "switch", switchActivityHandler)
    subscribe(motion, "motion.active", motionActiveHandler)
    subscribe(motion, "motion.inactive", motionInactiveHandler)
}

def captureSwitchStatus() {
    log.debug "== captureSwitchStatus =="
    def switchTouched = [:]
    def switchStates = [:]
    def switchLevels = [:]
    for (device in switches) {
        def key = device.getId()
        if (device.hasCapability("Switch Level")) {
            switchLevels[key] = device.currentValue("level")
        }
        switchStates[key] = device.currentValue("switch")
        switchTouched[key] = false
    }
    state.initialSwitchStates = switchStates
    state.initialSwitchLevels = switchLevels
    state.switchTouched = switchTouched
}

def motionActiveHandler(evt) {
    log.debug "== motionActiveHandler =="
    if ((state.isTurnedOn == false) && isDark()) {
        state.isTurnedOn = true
        captureSwitchStatus()
        switchOn();
    } else {
        log.warn "Motion detected, but we have already triggered switches."
    }
}

def motionInactiveHandler(evt) {
    log.debug "== motionInactiveHandler =="
    if (state.isTurnedOn == true) {
        def delay = getDelay()
        log.info "Restoring switches in ${minutesLater} minutes (${delay} seconds)"
        runIn(delay, restoreSwitches)
    }
}

def switchActivityHandler(evt) {
    log.debug "== switchActivityHandler =="
    if (state.isTurnedOn == true) {
        def id = evt.device.getId()
        if (state.switchTouched[id] == false) {
            // @TODO: Also monitor changes to the switch level, and flagging not to restore if changed from 100.
            if (evt.device.currentSwitch == "off") {
                log.info "Switch was turned off since turning on by by us. Flagging that we shouldn't restore this switch."
                state.switchTouched[id] = true
            }
        }
    }
}

def getDelay() {
    minutesLater * 60
}

def isDark() {
	def darkTimes = getSunriseAndSunset(sunsetOffset: "-00:30", sunriseOffset: "+00:30")
    def now = new Date()
    def isLight = now.before(darkTimes.sunset) && now.after(darkTimes.sunrise)
    !isLight
}

def switchOn() {
    log.debug "== switchOn =="
    for (device in switches) {
        device.on()
        if (device.hasCapability("Switch Level")) {
        	def key = device.getId()
            def thisSwitchInitialLevel = state.initialSwitchLevels[key]
            def level = 100
            if (thisSwitchInitialLevel) {
            	level = Math.round(thisSwitchInitialLevel * 1.5)
                level = Math.min(level, 100)
            }
            log.info "Setting level to: ${level}"
            device.setLevel(level)
        }
    }
}

def restoreSwitches() {
    log.debug "== restoreSwitches =="
    if (state.isTurnedOn == true) {
        def motionIsActive = (motion.currentMotion == "active")
        if (motionIsActive) {
            log.warn "Motion sensor is currently active, but timeout for restoring switches has been called. Skipping restore."
        } else {
            for (device in switches) {
                restoreSwitch(device)
            }
            state.isTurnedOn = false
        }
    }
}

def restoreSwitch(device) {
    log.debug "== restoreSwitch =="
    def key = device.getId()
    if (state.switchTouched[key] == false) {
        def thisSwitchInitialState = state.initialSwitchStates[key]
        if (thisSwitchInitialState == "on") {
            log.info "Leaving switch on. ${device}"
            def thisSwitchInitialLevel = state.initialSwitchLevels[key]
            if (thisSwitchInitialLevel) {
                log.info "Restoring level to: ${thisSwitchInitialLevel}"
                device.setLevel(thisSwitchInitialLevel)
            }
        } else {
            log.info "Turning switch off. ${device}"
                device.off()
        }
    } else {
        log.warn "Switch has been interacted with, skipping restore."
    }
}
