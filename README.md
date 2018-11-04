# Andrew Sayre's SmartThings
This repository is for customer SmartThings device handlers and other items.

## GE Dimmer Switch 14294 DH
This device handler is for the GE Z-Wave Plus Smart Control Dimmer Switch (model 14294) and has been tested with firmware versions 5.26 and 5.29 (likely works with others.  This provides the base functionality from the Dimmer Switch DH but includes the following enhancements:
1. Double-tap button: Adds the button capability to the device and passes value "pressed_1" when double-taped up and "pressed_2" when double-taped down.
2. Level fade duration - Specify a default duration (in seconds) to use when fading between levels/brightness.  Set default to 0 to instantly change levels (same behavior as the original Dimmer Switch).
