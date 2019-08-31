import socket
import pigpio
import netifaces as ni
import time
from math import ceil

from servo import Servo
from properties import properties
print(properties)

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

time.sleep(1)


def fetchPacket():
    if properties['verbose']:
        print("waiting on packet")
    data, addr = sock.recvfrom(properties['buffer_size'])
    values = list()
    for i in range(0, len(data), 4):
        values.append(int.from_bytes(data[i:i+4], byteorder='big', signed=True))
    if properties['verbose']:
        print("packet received :"+str(values))
    return values

def moveServos(values):
    global pan
    pan.moveTo(values[0])
    time.sleep(properties['motor_delay'])

while True:
    values = fetchPacket()
    moveServos(values)
