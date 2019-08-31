import socket
import pigpio
import time
from math import ceil

#Remember: launch pigpio daemon (sudo pigpiod)

#program config
verbose = True
timeStamp = False
invertX = True
invertY = True

#servo config
pan = 3
tilt = 2
panSensitivity = 0.5
tiltSensitivity = 0.5

#udp config
UDP_IP = "192.168.137.1"
UDP_PORT = 8081

#define constants & positions
panPos = 1500
tiltPos = 1500
maxPos = 2500
minPos = 500
#declare pigpio
pi = pigpio.pi()
#set positions
pi.set_servo_pulsewidth(pan, panPos)
pi.set_servo_pulsewidth(tilt, tiltPos)
#wait
time.sleep(1)

#UDP setup
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM) # UDP
sock.bind((UDP_IP, UDP_PORT))

def fetchPacket():
    if verbose:
        print("waiting on packet")
    data, addr = sock.recvfrom(1024) # buffer size is 1024 bytes
    values = list()
    for i in range(0, len(data), 4):
        values.append(int.from_bytes(data[i:i+4], byteorder='big', signed=True))
    if verbose:
        print("packet received :"+str(values))
    return values

def applyInversions(values):
    if invertX:
       values[0] = -values[0]
    if invertY:
        values[1] = -values[1]
    return values

def moveServos(values):
    global panPos, tiltPos
    values = applyInversions(values)
    
    newPanPos = panPos + values[0] * panSensitivity
    if newPanPos <= maxPos and newPanPos >= minPos:
        panPos = newPanPos
        pi.set_servo_pulsewidth(pan, panPos)

    newTiltPos = tiltPos + values[1] * tiltSensitivity
    if newTiltPos <= maxPos and newTiltPos >= minPos:
        tiltPos = newTiltPos
        pi.set_servo_pulsewidth(tilt, tiltPos)

    time.sleep(0.0001)

while True:
    values = fetchPacket()
    moveServos(values)
