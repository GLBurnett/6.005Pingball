#
board name =boardOne
#Testing no gravity, no friction1, no friction2
#Testing lots of different floats and ints
ball name=BallOne x=1.1 y=0 xVelocity = 0.0 yVelocity = .0
#comment
squareBumper name = SquareOne x=0 y=1
circleBumper name = Circle x=6 y = 7 
triangleBumper name=Triangle x=5 y=0 orientation = 180
absorber name = Absorber_One x=9 y= 10 width = 4 height = 2
leftFlipper name=FlipLeft x=8 y = 8 orientation = 90.0
rightFlipper name = FlipRight x = 18 y = 18 orientation = 270.
#comment
#comment
#testing all whitespace comments
#     
#
fire trigger = FlipLeft action = FlipRight
fire trigger = SquareOne action = Absorber_One
fire trigger = Circle action = Absorber_One