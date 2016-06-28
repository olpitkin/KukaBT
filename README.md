# KukaBT

* roslaunch youbot_oodl youbot_oodl.launch

* Bind rfcomm port :
 
* sudo rfcomm bind /dev/rfcomm0 04:C2:3E:AC:B4:02 6
* sudo rfcomm bind/dev/rfcomm0 5C:3C:27:4B:89:B5 4 

* sudo chmod 777 /dev/rfcomm0
* python youbot_keyboard_teleop.py
