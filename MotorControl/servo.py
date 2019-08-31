class Servo:
    def __init__(self, pi, pin, startingPos, minPos, maxPos, inverted):
        pi.set_servo_pulsewidth(pin, startingPos)
        self.pin = pin
        self.position = startingPos
        self.maxPosition = maxPos
        self.minPosition = minPos
        self.inverted = inverted
        
    def applyInversion(change):
        if self.inverted:
            return -change
        return change
        
    def moveTo(self, pi, change):
        newPosition = self.position + applyInversion(change)
        if newPosition <= self.maxPosition and newPosition >= self.minPosition:
            self.position = newPosition
            pi.set_servo_pulsewidth(self.pin, self.position)