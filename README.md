# KukaBT

1. Start driver : roslaunch youbot_oodl youbot_oodl.launch

sudo rfcomm bind /dev/rfcomm0 04:C2:3E:AC:B4:02 6
sudo chmod 777 /dev/rfcomm0
python youbot_keyboard_teleop.py
