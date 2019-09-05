import socket
import pigpio
import netifaces as ni
from time import time, sleep
from math import ceil
import sys

from servo import Servo
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

#Get IP
def getIP():
    nic = 'eth0'
    if properties['wireless']:
        nic = 'wlan0'
    return ni.ifaddresses(nic)[ni.AF_INET][0]['addr']
    
#UDP setup
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM) # UDP
sock.bind((getIP(), properties['port'])) 

#declare pigpio
pi = pigpio.pi()

#create servos
pan = Servo(pi, properties['pan_pin'], properties['starting_position'],
            properties['min_position'], properties['max_position'],
            properties['invert_x'])

sleep(1)


def fetchPacket():
    if verbose:
        print("waiting on packet")
    data, addr = sock.recvfrom(properties['buffer_size'])
    values = list()
    for i in range(0, len(data), 4):
        values.append(int.from_bytes(data[i:i+4], byteorder='big', signed=True))
    if verbose:
        print("packet received :"+str(values))
    return values

def moveServos(values):
    pan.moveTo(pi, values[0])
    sleep(properties['motor_delay'])

if timestamp:
    while True:
        values = fetchPacket()
        values_timestamp = time()
        print("values fetched at "+str(values_timestamp))
        moveServos(values)
        servos_timestamp = time()
        print("servos moved at "+str(servos_timestamp))
        print("TOTAL TIME = "+str(servos_timestamp-values_timestamp))
else:    
    while True:
        values = fetchPacket()
        moveServos(values)
