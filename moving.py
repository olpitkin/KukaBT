#!/usr/bin/env python
# Initial code created by Graylin Trevor Jay (tjay@cs.brown.edu) and published under Creative Commons Attribution license.
# addition for signal interrupt by Koen Buys

import roslib
import rospy
from geometry_msgs.msg import Twist
import sys, select, termios, tty, signal
import thread
import serial
import pyurg
import threading
import serial.tools.list_ports

# Our virtual serial port device name
PORT = '/dev/rfcomm0'
# Baud rate for communication
BAUDRATE = 921600
# Timeout for operations
TIMEOUT = 1

# Create and open our serial port
port = serial.Serial(port=PORT, baudrate=BAUDRATE, timeout=TIMEOUT)
#port.open()

stop = True
right=False
left=False
back=False
slomo=False
obstacle=False
count=0
starting=False
speedlvl=0

fullStop = False
arduinoSerialData= None
# = serial.Serial('/dev/ttyACM0', 9600)  # Create Serial port object called arduinoSerialData

ports = list(serial.tools.list_ports.comports())
for p in ports:
    if "PID=2341" in p[2]:
	arduinoSerialData= serial.Serial(p[0], 9600)
	print p[0]

msg = """
Reading from the keyboard  and Publishing to Twist!
---------------------------
Moving around:
   u    i    o
   j    k    l
   m    ,    .

q/z : increase/decrease max speeds by 10%
w/x : increase/decrease only linear speed by 10%
e/c : increase/decrease only angular speed by 10%
anything else : stop

CTRL-C to quit
"""

# x,y,theta ratio
moveBindings = {
'i':(1, 0, 0),  # forwards
'o':(1, 0, -1),  # forwards + rotation right
'j':(0, 1, 0),  # left
'l':(0, -1, 0),  # right
'r':(1, 1, 1.),  # right
'u':(1, 0, 1),  # forwards + rotation left
',':(-1, 0, 0),  # backward
'.':(0, 0, -1),  # turn right on spot
'm':(0, 0, 1),  # turn left on spot
}

speedBindings = {
'q':(1.1, 1.1),
'z':(.9, .9),
'w':(1.1, 1),
'x':(.9, 1),
'e':(1, 1.1),
'c':(1, .9),
}

class TimeoutException(Exception): 
    pass

def filtervalues(mylist):
	for length in mylist:
		if (length <= 5):
			mylist.remove(length)
	return mylist

urg = pyurg.UrgDevice()
if not urg.connect():
	print 'Could not connect.' 

def laserscan():
	threading.Timer(0.5, laserscan).start()
	global urg
	data = urg.capture()
	fdata = filtervalues(data[0])
	if len(fdata) is not 0:	
			degreeNum = (len(fdata))/240.0
			s11 = int(degreeNum*80)
			s21 = int(degreeNum*106)
			s22 = int(degreeNum*134)
			s23 = int(degreeNum*160)
			s31 = int(degreeNum*240)
			averagedistanceLeft = sum(fdata[s11:s21])/(s21-s11)
			averagedistanceAhead = sum(fdata[s21:s22])/(s22-s21)
			averagedistanceRight = sum(fdata[s22:s23])/(s23-s22)	
			#print averagedistanceAhead, "averagedisAhead"
			#print averagedistanceLeft, "averagedistanceLeft"
			#print averagedistanceRight, "averagedistanceRight"   
			#print speed	
			if averagedistanceAhead < 500:
			    print "stop"
			    global obstacle
			    obstacle = True 
			if averagedistanceAhead > 500:
			    global obstacle
			    obstacle =  False

def androidMessage():
	threading.Timer(1, androidMessage).start()
	bytes = port.read(1024)
	message = bytes.decode("utf-8")
	print message, "ANDROID"  
	
def getKey():
    def timeout_handler(signum, frame):
        raise TimeoutException()
    old_handler = signal.signal(signal.SIGALRM, timeout_handler)
    signal.alarm(1)  # this is the watchdog timing
    tty.setraw(sys.stdin.fileno())
    select.select([sys.stdin], [], [], 0)
    try:
       key = sys.stdin.read(1)
       # print "Read key"
    except TimeoutException:
       # print "Timeout"
       return "-"
    finally:
       signal.signal(signal.SIGALRM, old_handler)
    signal.alarm(0)
    termios.tcsetattr(sys.stdin, termios.TCSADRAIN, settings)
    return key

speed = 0.5
turn = 0.5

def vels(speed, turn):
    return "currently:\tspeed %s\tturn %s " % (speed, turn)

def setSpeed(speedlvl):
    global speed
    global turn
    if speedlvl==0:
	speed=0
	turn=0
    elif speedlvl == 1:
	speed=0.2
	turn=0.2
    elif speedlvl == 2:
	speed=0.5
	turn=0.5
    elif speedlvl == 3:
	speed=0.8
	turn=0.8
    elif speedlvl == 4:
	speed=1
	speed=1

if __name__ == "__main__":
    settings = termios.tcgetattr(sys.stdin)
    pub = rospy.Publisher('cmd_vel', Twist)
    rospy.init_node('teleop_twist_keyboard')
    filename = raw_input("enter protocol file name: ")
    protocol = open("protocols/" + filename + ".txt", "w")
    protocol.write(filename)
    x = 0
    y = 0
    th = 0
    status = 0
    try:
        print msg
        print vels(speed, turn)
	laserscan()
	androidMessage()
        while(1):
	    if count > 0:
		count-=1
	    key = ""	
	    if (arduinoSerialData.inWaiting() > 0):
        	myData = arduinoSerialData.readline()
		protocol.write(myData)
		if "0: " in myData or "X: " in myData:
		    continue
		print myData

        	if "GO" in myData:
		    stop = False
		    starting=True
  		    count=1000
		    speedlvl = 1
		elif "STOP!!" in myData:
		    stop = True
		    starting=False
		    count=0
		    speedlvl=0
		elif "slow" in myData:
		    starting=False
		    if(speedlvl>0 and count==0):
			speedlvl=speedlvl-1
			setSpeed(speedlvl)
			count=1000
		elif "speed" in myData:
		    starting=False
		    if(speedlvl<4 and count==0):
		    	speedlvl=speedlvl+1
			setSpeed(speedlvl)
			count=1000
		elif "right" in myData:
		    if(right==False):			
			right=True
		    else:
			right=False
		elif "left" in myData:
		    if(left==False):			
			left=True
		    else:
			left=False
		elif "back" in myData:
		    if(back==False):			
			back=True
			speedlvl=1
			setSpeed(1)
		    else:
			back=False
			speed=0
		elif "slomo" in myData:
		    if(slomo==False):			
			slomo=True
			setSpeed(1)
		    else:
			slomo=False
			setSpeed(0)
		elif "quit" in myData:
		    protocol.close()
		    sys.exit
	    if(stop==False and obstacle==False):
		if starting == True and count == 0:
		    speedlvl+=1
		    setSpeed(speedlvl)
		    print "faster"
		    if(speedlvl)==3:
			starting=False
		    else:
			count=1000
		if right == True:
		    key="o"
		elif left == True:
		    key="u"
		else:
		    key="i"
	    elif(right==True):
		if slomo == True:
		    key="o"
		else:
		    key = "."
	    elif(left == True):
		if(slomo == True):
		    key="u"
		else:
		    key = "m"
	    elif(back == True):
		    key=","
	    elif(slomo == True):
		    key="i"
	    elif obstacle == True:
		start=False
		count=0
		
	    if len(key) > 1:
		print key
            if key in moveBindings.keys():	
		x = moveBindings[key][0]
		y = moveBindings[key][1]
		th = moveBindings[key][2]
            elif key in speedBindings.keys():
                speed = speed * speedBindings[key][0]
                turn = turn * speedBindings[key][1]
                print vels(speed, turn)
                if (status == 14):
                    print msg
                status = (status + 1) % 15
            else:
                x = 0
                y = 0
                th = 0
                if (key == '\x03'):
                    sys.exit(1)
            twist = Twist()
            twist.linear.x = x * speed 
            twist.linear.y = y * speed 
            twist.linear.z = 0
            twist.angular.x = 0 
            twist.angular.y = 0
            twist.angular.z = th * turn
            pub.publish(twist)
    except:
        print e
    finally:
        twist = Twist()
        twist.linear.x = 0; twist.linear.y = 0; twist.linear.z = 0
        twist.angular.x = 0; twist.angular.y = 0; twist.angular.z = 0
        pub.publish(twist)
        termios.tcsetattr(sys.stdin, termios.TCSADRAIN, settings)
