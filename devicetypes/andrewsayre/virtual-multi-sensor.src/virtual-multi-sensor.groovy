/**
 *  Virtual Multi-Sensor
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
	definition (name: "Virtual Multi-Sensor", namespace: "andrewsayre", author: "Andrew Sayre") {
		capability "Three Axis"
		capability "Battery"
		capability "Configuration"
		capability "Sensor"
		capability "Contact Sensor"
		capability "Acceleration Sensor"
		capability "Refresh"
		capability "Temperature Measurement"
		capability "Health Check"
       
        attribute "status", "string"
	}

	simulator {
	}
	preferences {
		section {
			image(name: 'educationalcontent', multiple: true, images: [
					"http://cdn.device-gse.smartthings.com/Multi/Multi1.jpg",
					"http://cdn.device-gse.smartthings.com/Multi/Multi2.jpg",
					"http://cdn.device-gse.smartthings.com/Multi/Multi3.jpg",
					"http://cdn.device-gse.smartthings.com/Multi/Multi4.jpg"
			])
		}
		section {
			input title: "Temperature Offset", description: "This feature allows you to correct any temperature variations by selecting an offset. Ex: If your sensor consistently reports a temp that's 5 degrees too warm, you'd enter '-5'. If 3 degrees too cold, enter '+3'.", displayDuringSetup: false, type: "paragraph", element: "paragraph"
			input "tempOffset", "number", title: "Degrees", description: "Adjust temperature by this many degrees", range: "*..*", displayDuringSetup: false
		}
		section {
			input("garageSensor", "enum", title: "Do you want to use this sensor on a garage door?", description: "Tap to set", options: ["Yes", "No"], defaultValue: "No", required: false, displayDuringSetup: false)
		}
	}

	tiles(scale: 2) {
		multiAttributeTile(name: "status", type: "generic", width: 6, height: 4) {
			tileAttribute("device.status", key: "PRIMARY_CONTROL") {
				attributeState "open", label: 'Open', icon: "st.contact.contact.open", backgroundColor: "#e86d13"
				attributeState "closed", label: 'Closed', icon: "st.contact.contact.closed", backgroundColor: "#00a0dc"
				attributeState "garage-open", label: 'Open', icon: "st.doors.garage.garage-open", backgroundColor: "#e86d13"
				attributeState "garage-closed", label: 'Closed', icon: "st.doors.garage.garage-closed", backgroundColor: "#00a0dc"
			}
		}
		standardTile("contact", "device.contact", width: 2, height: 2) {
			state("open", label: 'Open', icon: "st.contact.contact.open", backgroundColor: "#e86d13")
			state("closed", label: 'Closed', icon: "st.contact.contact.closed", backgroundColor: "#00a0dc")
		}
		standardTile("acceleration", "device.acceleration", width: 2, height: 2) {
			state("active", label: 'Active', icon: "st.motion.acceleration.active", backgroundColor: "#00a0dc")
			state("inactive", label: 'Inactive', icon: "st.motion.acceleration.inactive", backgroundColor: "#cccccc")
		}
		valueTile("temperature", "device.temperature", width: 2, height: 2) {
			state("temperature", label: '${currentValue}°',
					backgroundColors: [
							[value: 31, color: "#153591"],
							[value: 44, color: "#1e9cbb"],
							[value: 59, color: "#90d2a7"],
							[value: 74, color: "#44b621"],
							[value: 84, color: "#f1d801"],
							[value: 95, color: "#d04e00"],
							[value: 96, color: "#bc2323"]
					]
			)
		}
		valueTile("battery", "device.battery", decoration: "flat", inactiveLabel: false, width: 2, height: 2) {
			state "battery", label: '${currentValue}% battery', unit: ""
		}
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", action: "refresh.refresh", icon: "st.secondary.refresh"
		}


		main(["status", "acceleration", "temperature"])
		details(["status", "acceleration", "temperature", "battery", "refresh"])
	}
}

def installed() {
	updated()
}

def updated(){ 
	sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 1 * 60, displayed: false)

	Random random = new Random()
	
	sendEvent(name: "contact", value: random.nextInt(2) > 0 ? "open" : "closed", isStateChange: true)
    sendEvent(name: "battery", value: random.nextInt(101), unit: '%', isStateChange: true)
    sendEvent(name: "threeAxis", value: ['x': 100, 'y': 100, 'z': 100], isStateChange: true)
	sendEvent(name: "acceleration", value: random.nextInt(2) > 0 ? "active" : "inactive", isStateChange: true)
    sendEvent(name: "temperature", value: random.nextInt(101), unit: 'F', isStateChange: true)
    
    unschedule()
    runEvery1Minute(updated)
}

def refresh() {
}