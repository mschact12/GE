# Andrew Sayre's SmartThings
This repository is for customer SmartThings device handlers and other items.

## GE Dimmer Switch 14294 Device Handler
This device handler is for the GE Z-Wave Plus Smart Control Dimmer Switch (model 14294) and has been tested with firmware versions 5.26 and 5.29 (likely works with others).  This provides the base functionality from the Dimmer Switch DH but includes the following enhancements and features:
1. **Level Fade Transition/Duration:** Modifications to the base-handler to take advantage of the switch's ability to fade between light levels smoothly over a specified duration.  A new preference is available to set the default duration when none is specified (which is the case when using the app, scenes, routines, etc...).  This prevents the behavior of the light instantly jumping to the target level.  This does not change the on/off effects (step/duration parameters) -- it only applies when transitioning between two different levels (i.e. 50 --> 100).
   * `Default Fade Duration`: The number of seconds (0-128) for the device to fade from the current level to a new set level.  The default value is `1`.  Set to `0` to disable and the device will instantly jump to the light level when set, per the original behavior.
1. **Button Double-tap (up/down) support:**  Preferences to set the lighting  level to a specific value when double-tapped up or down.  For example use case: raise the lighting level to 100 when double-tapped up.  The following preferences are exposed:
   * `Enable Double-Tap Up Level`: When enabled, sets the lighting level to the value of `Double-Tap Up Level`, otherwise the device raises a button press event - `pressed_1`.  The default value is `false`.
   * `Double-Tap Up Level`: The lighting level to set when double-tapped up and `Enable Double-Tap Up Level`.  The default value is `100`.
   * `Enable Double-Tap Down Level`: When enabled, sets the lighting level to the value of `Double-Tap Down Level`, otherwise the device raises a button press event - `pressed_2`. The default value is `false`.
   * `Double-Tap Down Level`: The lighting level to set when double-tapped down and `Enable Double-Tap Down Level`.  The default value is `0`.
