#!/bin/bash

#
# Copyright 2014 Julián Zaragoza 
#  
# This file is part of BN: Battery Notification
#  
# BN: Battery Notification is free software; you can redistribute it and/or modify it under the terms of the GNU 
# General Public License as published by the Free Software Foundation; either version 2 of the
# License, or (at your option) any later version. This program is distributed in the hope that it will be
# useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
# or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
# You should have received a copy of the GNU General Public License along with this program; if not, 
# write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
# 
color="blue"
x=190

for ((i=1; i<101; i++))
do

#COLORS

if [ $i -gt 89 ]; then
  color="blue";
elif [ $i -gt 15 ]; then
  color="black";
else
  color="red";
fi

#X COORDENATES
if [ $i -eq 100 ]; then
  x=50;
elif [ $i -gt 9 ]; then
  x=120;
else
  x=200;
fi

#IMAGE CREATION
convert -font helvetica -fill $color -pointsize 308 -draw "text $x,430 '$i'" ic_state_battery.png "percentage/ic_state_battery$i.png"


#CONVERSION
#ldpi
convert "percentage/ic_state_battery$i.png" -resize 25x25 "percentage/ldpi/ic_state_battery$i.png"
#mdpi
convert "percentage/ic_state_battery$i.png" -resize 25x25 "percentage/mdpi/ic_state_battery$i.png"
#hdpi
convert "percentage/ic_state_battery$i.png" -resize 36x36 "percentage/hdpi/ic_state_battery$i.png"
#xhdpi
convert "percentage/ic_state_battery$i.png" -resize 48x48 "percentage/xhdpi/ic_state_battery$i.png"
#xxhdpi
convert "percentage/ic_state_battery$i.png" -resize 72x72 "percentage/xxhdpi/ic_state_battery$i.png"

done
# 
