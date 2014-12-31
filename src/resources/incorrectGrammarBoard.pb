#This board should fail; there is incorrect grammar
board name=board_withChars89InName gravity = 25. friction1= 0 friction2 =.110
#comment
#ball name = ball_B x=1 y=4 xVelocity = 1.1 yVelocity = -4.7
ball name=ball_A x=1 y = 0.0 xVelocity = -2.6 yVelocity = -1
#should fail bc can't have a non-integer position
squareBumper name=Square x = 0 y=1.1
