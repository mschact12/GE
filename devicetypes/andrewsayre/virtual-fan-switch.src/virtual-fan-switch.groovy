/**
 *  Virtual Fan Switch
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
	definition (name: "Virtual Fan Switch", namespace: "andrewsayre", author: "Andrew Sayre") {
		capability "Switch Level"
		capability "Switch"
		capability "Fan Speed"
		capability "Actuator"
		capability "Refresh"
		capability "Sensor"
        
        command "low"
		command "medium"
		command "high"
		command "raiseFanSpeed"
		command "lowerFanSpeed"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {
		multiAttributeTile(name: "fanSpeed", type: "generic", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.fanSpeed", key: "PRIMARY_CONTROL") {
				attributeState "0", label: "off", action: "switch.on", icon: "st.thermostat.fan-off", backgroundColor: "#ffffff"
				attributeState "1", label: "low", action: "switch.off", icon: "st.thermostat.fan-on", backgroundColor: "#00a0dc"
				attributeState "2", label: "medium", action: "switch.off", icon: "st.thermostat.fan-on", backgroundColor: "#00a0dc"
				attributeState "3", label: "high", action: "switch.off", icon: "st.thermostat.fan-on", backgroundColor: "#00a0dc"
			}
			tileAttribute("device.fanSpeed", key: "VALUE_CONTROL") {
				attributeState "VALUE_UP", action: "raiseFanSpeed"
				attributeState "VALUE_DOWN", action: "lowerFanSpeed"
			}
		}
		main "fanSpeed"
		details(["fanSpeed"])
	}
}

def parse(String description) {
}

def on() {
	setLevel(100)
}

def off() {
	setLevel(0)
}

def setLevel(value) {
	log.debug "setLevel >> value: $value"
	def level = value as Integer
    sendEvent(name: "switch", value: level > 0 ? "on" : "off", isStateChange: true)
    sendEvent(name: "level", value: level == 99 ? 100 : level, isStateChange: true)
    sendEvent(name: "fanSpeed", value: Math.round(level/33), isStateChange: true)
}

def setFanSpeed(speed) {
	setLevel(speed * 33)
}

def raiseFanSpeed() {
	setFanSpeed(Math.min((device.currentValue("fanSpeed") as Integer) + 1, 3))
}

def lowerFanSpeed() {
	setFanSpeed(Math.max((device.currentValue("fanSpeed") as Integer) - 1, 0))
}

def low() {
	setFanSpeed(1)
}

def medium() {
	setFanSpeed(2)
}

def high() {
	setFanSpeed(3)
}