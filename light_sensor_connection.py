import urequests
import time
from machine import Pin
import network

   # Set up the light pin on the Raspberry Pi Pico

led = Pin(15, Pin.OUT)
pir_sensor = Pin(14, Pin.IN)
   # Connect to Wi-Fi
wlan = network.WLAN(network.STA_IF)
wlan.active(True)
wlan.connect('iPhone', 'tintinnemonemo')

while not wlan.isconnected():
    pass

   # Function to check light status from server
def check_light_status():
    response = urequests.get("http://20.93.3.161:3001/get-light-status")
    data = response.json()
    response.close()
    if data["light_on"]:
        led.high()
    else:
        led.low()
def update_light_status_on_server(light_status):
    try:
        url = "http://20.93.3.161:3001/set-light-on" if light_status else "http://20.93.3.161:3001/set-light-off"
        response = urequests.post(url)
        response.close()
    except Exception as e:
        print("Error updating light status:", e)

light_status = False  # Initial light status (off)

while True:
    try:
        # Step 1: Check light status from the server
        server_light_status = check_light_status()
        if server_light_status is not None and server_light_status != light_status:
            light_status = server_light_status
            if light_status:
                led.high()
            else:
                led.low()
        
        # Step 2: Handle PIR sensor input
        if pir_sensor.value() == 1:  # Motion detected
            if not light_status:  # If the light is off, turn it on
                light_status = True
                led.high()
                update_light_status_on_server(light_status)
        else:  # No motion detected
            if light_status:  # If the light is on, turn it off
                light_status = True
                led.high()
                update_light_status_on_server(light_status)

        time.sleep(1)  # Poll every second
    except Exception as e:
        print("Unexpected error in main loop:", e)
   # Polling loop
