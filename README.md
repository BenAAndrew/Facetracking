# Facetracking - Raspberry Pi IoT System

# Introduction
This project is designed to cover the process of creating a simple processing system for IoT solutions, and how to create a real time face tracking system. 

The end goal of this was to take an Inmoov head (http://inmoov.fr/head-3/) and make it watch people in real time by tracking their face and moving servos to fix on their face (a bit creepy but why not).

To make this possible I used a <b>Raspberry pi</b> with a camera mounted inside the head which will then stream this to an <b>external pc</b> (a concept ideal for IoT problems). Let me explain this further...

# The process
To be able to face track in real time optimisation is key. We need to be able to do all of the following within milliseconds to ensure the system can track the face reliably;
<ol>
  <li><b>Take a photo:</b> Pretty obvious</li>
  <li><b>Decode the image*:</b> Take the photo and convert it into the format needed for classification</li>  
  <li><b>Classify*:</b> Identify the main face in the decoded image</li>
  <li><b>Send position to servos:</b> Act upon the location of this face to move the camera to center on them</li>
</ol>

Now although this would all be possible on a IoT device such as a Raspberry Pi, getting this fast enough to be practical for our problem is quite difficult. This is where the concept of <b>cloud computing</b> can come in handy.

We know we can't fit a pc inside the head but why not offload a lot of the work to an external machine. This way we can keep the code on the Raspberry Pi simple, and use a more powerful machine for the processing. If you take another look at the list the steps marked with asterisks are the one's we'll offload to a java program on a PC, and we'll make the PC and Pi communicate over a network.

# Raspberry Pi setup
So as discussed the Pi will have two jobs; <b>Handle an image stream from the camera</b> and <b>Send adjustments to the servos</b>.

For the first step there's no need to reinvent the wheel. Video and image streaming libraries are pretty common and I settled on <b>mjpeg-streamer</b>, a reliable stream choice for Raspberry pi's. I followed this guide to get it setup;

<b>https://github.com/cncjs/cncjs/wiki/Setup-Guide:-Raspberry-Pi-%7C-MJPEG-Streamer-Install-&-Setup-&-FFMpeg-Recording</b>

After our PC uses this stream to find a face it will send us a packet with 2 integers. This is the x and y difference of the face that it found and now we need to relay this to the motors so they can adjust accordingly. This code is written in <b>Python</b> so firstly install python if you don't have it already and install the requirements by running

```
pip install -r requirements.txt
```

when in the <b>MotorControl</b> folder. This installs two libraries;
<ul>
  <li><b>netifaces: </b>Used to get the devices IP</li>
  <li><b>pigpio: </b>Used to interact with the servos though the raspberry pi's GPIO pins</li>
</ul>
