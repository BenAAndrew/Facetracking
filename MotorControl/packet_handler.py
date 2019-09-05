import socket
import netifaces as ni
from properties import properties

class PacketHandler:
    def __init__(self, verbose):
        #UDP setup
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.sock.bind((self.getIP(), properties['port'])) 
        self.verbose = verbose

    def getIP(self):
        nic = 'eth0'
        if properties['wireless']:
            nic = 'wlan0'
        return ni.ifaddresses(nic)[ni.AF_INET][0]['addr']

    def fetchPacket(self):
        if self.verbose:
            print("waiting on packet")
        data = self.sock.recvfrom(properties['buffer_size'])[0]
        values = list()
        for i in range(0, len(data), 4):
            values.append(int.from_bytes(data[i:i+4], byteorder='big', signed=True))
        if self.verbose:
            print("packet received :"+str(values))
        return values