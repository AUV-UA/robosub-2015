#!/bin/bash

sudo modprobe v4l2loopback devices=4

bmdcapture -m 11 -C 0 -F nut -f pipe:1 | ffmpeg -re -i - -vf scale=1280x720 -r 15 -f v4l2 /dev/video0 &
bmdcapture -m 8 -C 1 -F nut -f pipe:1 | ffmpeg -re -i - -vf scale=1920x1080 -r 15 -f v4l2 /dev/video1 &
