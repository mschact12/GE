/**
 *  Virtual Legacy Thermostat
 *
 *  Copyright 2019 Andrew Sayre
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Virtual Legacy Thermostat", namespace: "andrewsayre", author: "Andrew Sayre") {
        capability "Actuator"
		capability "Temperature Measurement"
		capability "Relative Humidity Measurement"
		capability "Thermostat"
		capability "Refresh"
		capability "Sensor"
		capability "Health Check"
        
		command "switchMode"
		command "switchFanMode"
		command "lowerHeatingSetpoint"
		command "raiseHeatingSetpoint"
		command "lowerCoolSetpoint"
		command "raiseCoolSetpoint"
		command "poll"
	}


	simulator {

	}

	tiles {
		multiAttributeTile(name:"temperature", type:"generic", width:3, height:2, canChangeIcon: true) {
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
				attributeState("temperature", label:'${currentValue}°', icon: "st.alarm.temperature.normal",
					backgroundColors:[
							// Celsius
							[value: 0, color: "#153591"],
							[value: 7, color: "#1e9cbb"],
							[value: 15, color: "#90d2a7"],
							[value: 23, color: "#44b621"],
							[value: 28, color: "#f1d801"],
							[value: 35, color: "#d04e00"],
							[value: 37, color: "#bc2323"],
							// Fahrenheit
							[value: 40, color: "#153591"],
							[value: 44, color: "#1e9cbb"],
							[value: 59, color: "#90d2a7"],
							[value: 74, color: "#44b621"],
							[value: 84, color: "#f1d801"],
							[value: 95, color: "#d04e00"],
							[value: 96, color: "#bc2323"]
					]
				)
			}
		}
		standardTile("mode", "device.thermostatMode", width:2, height:2, inactiveLabel: false, decoration: "flat") {
			state "off", action:"switchMode", nextState:"...", icon: "st.thermostat.heating-cooling-off"
			state "heat", action:"switchMode", nextState:"...", icon: "st.thermostat.heat"
			state "cool", action:"switchMode", nextState:"...", icon: "st.thermostat.cool"
			state "auto", action:"switchMode", nextState:"...", icon: "st.thermostat.auto"
			state "emergency heat", action:"switchMode", nextState:"...", icon: "st.thermostat.emergency-heat"
			state "...", label: "Updating...",nextState:"...", backgroundColor:"#ffffff"
		}
		standardTile("fanMode", "device.thermostatFanMode", width:2, height:2, inactiveLabel: false, decoration: "flat") {
			state "auto", action:"switchFanMode", nextState:"...", icon: "st.thermostat.fan-auto"
			state "on", action:"switchFanMode", nextState:"...", icon: "st.thermostat.fan-on"
			state "circulate", action:"switchFanMode", nextState:"...", icon: "st.thermostat.fan-circulate"
			state "...", label: "Updating...", nextState:"...", backgroundColor:"#ffffff"
		}
		standardTile("lowerHeatingSetpoint", "device.heatingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "heatingSetpoint", action:"lowerHeatingSetpoint", icon:"st.thermostat.thermostat-left"
		}
		valueTile("heatingSetpoint", "device.heatingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "heatingSetpoint", label:'${currentValue}° heat', backgroundColor:"#ffffff"
		}
		standardTile("raiseHeatingSetpoint", "device.heatingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "heatingSetpoint", action:"raiseHeatingSetpoint", icon:"st.thermostat.thermostat-right"
		}
		standardTile("lowerCoolSetpoint", "device.coolingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "coolingSetpoint", action:"lowerCoolSetpoint", icon:"st.thermostat.thermostat-left"
		}
		valueTile("coolingSetpoint", "device.coolingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "coolingSetpoint", label:'${currentValue}° cool', backgroundColor:"#ffffff"
		}
		standardTile("raiseCoolSetpoint", "device.heatingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "heatingSetpoint", action:"raiseCoolSetpoint", icon:"st.thermostat.thermostat-right"
		}
		standardTile("thermostatOperatingState", "device.thermostatOperatingState", width: 2, height:1, decoration: "flat") {
			state "thermostatOperatingState", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		standardTile("refresh", "device.thermostatMode", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		main "temperature"
		details(["temperature", "lowerHeatingSetpoint", "heatingSetpoint", "raiseHeatingSetpoint", "lowerCoolSetpoint",
				"coolingSetpoint", "raiseCoolSetpoint", "mode", "fanMode", "thermostatOperatingState", "refresh"])
	}
}

def installed() {
	// Configure device
	runIn(3, "initialize", [overwrite: true])  // Allow configure command to be sent and acknowledged before proceeding
}

def updated() {
	initialize()
}

def initialize() {
	// Device-Watch simply pings if no device events received for 32min(checkInterval)
	sendEvent(name: "checkInterval", value: 2 * 15 * 60 + 2 * 60, displayed: false)
	unschedule()
	pollDevice()
}

def parse(String description)
{
}

// Command Implementations
def poll() {
	// Call refresh which will cap the polling to once every 2 minutes
	refresh()
}

def refresh() {
	// Only allow refresh every 2 minutes to prevent flooding the Zwave network
	def timeNow = now()
	if (!state.refreshTriggeredAt || (2 * 60 * 1000 < (timeNow - state.refreshTriggeredAt))) {
		state.refreshTriggeredAt = timeNow
		// use runIn with overwrite to prevent multiple DTH instances run before state.refreshTriggeredAt has been saved
		runIn(2, "pollDevice", [overwrite: true])
	}
}

def pollDevice() {
	Random random = new Random()
    
    // core states of the device
    state.supportedModes = ["off", "heat", "cool", "auto"]
    if (!state.thermostatMode) {
    	state.thermostatMode = "off"
    }
    state.supportedFanModes = ["auto", "circulate", "on"]
    if (!state.thermostatFanMode) {
    	state.thermostatFanMode = "auto"
    }
    if (!state.heatingSetpoint) {
    	state.heatingSetpoint = 64
    }
    if (!state.coolingSetpoint) {
    	state.coolingSetpoint = 72
    }


	// thermostatModeSupported
    sendEvent(name: "supportedThermostatModes", value: state.supportedModes, displayed: false)
    // thermostatFanModeSupported
    sendEvent(name: "supportedThermostatFanModes", value: state.supportedFanModes, displayed: false)    
    // thermostatMode
    sendEvent(name: "thermostatMode", value: state.thermostatMode, data:[supportedThermostatModes: state.supportedModes])
    // thermostatFanMode
    sendEvent(name: "thermostatFanMode", value: state.thermostatFanMode, data:[supportedThermostatFanModes: state.supportedFanModes])
    // thermostatSetpoint
    sendEvent(name: "heatingSetpoint", value: state.heatingSetpoint, unit: "F", displayed: false)
    sendEvent(name: "coolingSetpoint", value: state.coolingSetpoint, unit: "F", displayed: false)


    // thermostatOperatingState
    def possibleOperatingModes = ["fan only", "vent economizer"]
    if (state.thermostatFanMode == "auto") {
    	possibleOperatingModes += "idle"
    }
    switch (state.thermostatMode) {
    	case "heat":
        	possibleOperatingModes += ["heating", "pending heat"]
            sendEvent(name: "thermostatSetpoint", value: state.heatingSetpoint, unit: "F", displayed: false)
            break
        case "cool":
        	possibleOperatingModes += ["cooling", "pending cool"]
            sendEvent(name: "thermostatSetpoint", value: state.coolingSetpoint, unit: "F", displayed: false)
            break
        case "auto":
        	possibleOperatingModes += ["heating", "cooling", "pending heat", "pending cool"]
            sendEvent(name: "thermostatSetpoint", value: null, displayed: false)
            break
    }
    def operatingState = possibleOperatingModes[random.nextInt(possibleOperatingModes.size())]
    sendEvent(name: "thermostatOperatingState", value: operatingState)
    
    // sensorMultilevel.temperature
    def temperature = 0
    switch (operatingState) {
    	case "idle":
       		temperature = state.heatingSetpoint
            break
        case "fan only":
       		temperature = state.heatingSetpoint
            break
        case "vent economizer":
       		temperature = state.heatingSetpoint
            break
        case "heating":
        	temperature = state.heatingSetpoint - random.nextInt(4) + 1
            break
        case "pending heat":
        	temperature = state.heatingSetpoint - random.nextInt(4) + 1
            break
        case "cooling":
        	temperature = state.coolingSetpoint + random.nextInt(4) - 1
            break
        case "pending cool":
        	temperature = state.coolingSetpoint + random.nextInt(4) - 1
            break
    }
    sendEvent(name: "temperature", value: temperature, unit: "F")
    sendEvent(name: "humidity", value: random.nextInt(41), unit: "%")
}

def raiseHeatingSetpoint() {
	state.heatingSetpoint = state.heatingSetpoint + 1
	pollDevice()
}

def lowerHeatingSetpoint() {
	state.heatingSetpoint = state.heatingSetpoint - 1
	pollDevice()
}

def raiseCoolSetpoint() {
	state.coolingSetpoint = state.coolingSetpoint + 1
	pollDevice()
}

def lowerCoolSetpoint() {
	state.coolingSetpoint = state.coolingSetpoint - 1
	pollDevice()
}

def setHeatingSetpoint(degrees) {
	if (degrees) {
		state.heatingSetpoint = degrees.toDouble()
        pollDevice()
	}
}

def setCoolingSetpoint(degrees) {
	if (degrees) {
		state.coolingSetpoint = degrees.toDouble()
        pollDevice()
	}
}


/**
 * PING is used by Device-Watch in attempt to reach the Device
 * */
def ping() {
	log.debug "ping() called"
}

def switchMode() {
	def currentMode = device.currentValue("thermostatMode")
	def supportedModes = state.supportedModes
    def next = { supportedModes[supportedModes.indexOf(it) + 1] ?: supportedModes[0] }
    def nextMode = next(currentMode)
   	state.thermostatMode = nextMode
    pollDevice()
}

def switchToMode(nextMode) {
	def supportedModes = state.supportedModes
    if (supportedModes.contains(nextMode)) {
    	state.thermostatMode = nextMode
        pollDevice()
    } else {
        log.debug("ThermostatMode $nextMode is not supported by ${device.displayName}")
    }
}

def switchFanMode() {
	def currentMode = device.currentValue("thermostatFanMode")
	def supportedFanModes = state.supportedFanModes
    def next = { supportedFanModes[supportedFanModes.indexOf(it) + 1] ?: supportedFanModes[0] }
    def nextMode = next(currentMode)
    state.thermostatFanMode = nextMode
    pollDevice()
}

def switchToFanMode(nextMode) {
	def supportedFanModes = state.supportedFanModes
    if (supportedFanModes.contains(nextMode)) {
        state.thermostatFanMode = nextMode
    	pollDevice()
    } else {
        log.debug("FanMode $nextMode is not supported by ${device.displayName}")
    }
}

def setThermostatMode(String value) {
	switchToMode(value)
}

def setThermostatFanMode(String value) {
	switchToFanMode(value)
}


def off() {
	switchToMode("off")
}

def heat() {
	switchToMode("heat")
}

def emergencyHeat() {
	switchToMode("emergency heat")
}

def cool() {
	switchToMode("cool")
}

def auto() {
	switchToMode("auto")
}

def fanOn() {
	switchToFanMode("on")
}

def fanAuto() {
	switchToFanMode("auto")
}

def fanCirculate() {
	switchToFanMode("circulate")
}