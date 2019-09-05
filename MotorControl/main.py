import pigpio
from time import time, sleep
from math import ceil
import sys

from servo import Servo
from packet_handler import PacketHandler
from properties import properties
print(properties)

timestamp=False
verbose=False
for argument in sys.argv:
    if argument == '-v':
        verbose=True
    elif argument == '-t':
        timestamp=True

#Remember: launch pigpio daemon (sudo pigpiod)

#declarre packetHandler
packetHandler = PacketHandler(verbose)

#declare pigpio
pi = pigpio.pi()

#create servos
pan = Servo(pi, properties['pan_pin'], properties['starting_position'],
            properties['min_position'], properties['max_position'],
            properties['invert_x'])

sleep(1)

def moveServos(values):
    pan.moveTo(pi, values[0])
    sleep(properties['motor_delay'])

if timestamp:
    while True:
        values = packetHandler.fetchPacket()
        values_timestamp = time()
        print("values fetched at "+str(values_timestamp))
        moveServos(values)
        servos_timestamp = time()
        print("servos moved at "+str(servos_timestamp))
        print("TOTAL TIME = "+str(servos_timestamp-values_timestamp))
else:    
    while True:
        values = packetHandler.fetchPacket()
        moveServos(values)
