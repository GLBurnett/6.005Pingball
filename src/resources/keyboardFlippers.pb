board name=keyboardFlippers

ball name=ballAt4_4 x=4 y=4 xVelocity=0.0 yVelocity=0.0
ball name=ballAt10_4 x=11 y=4 xVelocity=0.0 yVelocity=0.0

rightFlipper name=FlipRight x=4 y=10 orientation=0
leftFlipper name=FlipLeft x=10 y=10 orientation=0

keyup key=r action=FlipRight
keydown key=l action=FlipLeft