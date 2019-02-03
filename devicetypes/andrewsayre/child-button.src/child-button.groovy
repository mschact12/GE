/**
 *  Child Button
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
	definition (name: "Child Button", namespace: "andrewsayre", author: "Andrew Sayre") {
		capability "Button"
	}
	simulator {
	}

	tiles(scale: 2) {
		multiAttributeTile(name: "rich-control", type: "generic", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.button", key: "PRIMARY_CONTROL") {
				attributeState "default", label: ' ', action: "", icon: "st.unknown.zwave.remote-controller", backgroundColor: "#ffffff"
			}
		}
	}
}

def installed() {
	sendEvent(name: "numberOfButtons", value: 1)
}

def raiseButtonPushed() {
    sendEvent(name: "button", value: "pushed", data: [buttonNumber: 1], isStateChange: true, type: "physical")
}