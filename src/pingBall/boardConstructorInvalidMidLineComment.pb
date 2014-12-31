board name=boardConstructorMidLineComment gravity = 25.0

# define some balls
ball name=BallA x=0.25 y=3.25 xVelocity=0.1 yVelocity=0.1 #MidLineComment 
ball name=BallB x=5.25 y=3.25 xVelocity=0.1 yVelocity=0.1 
ball name=BallC x=10.25 y=3.25 xVelocity=0.1 yVelocity=0.1 
ball name=BallD x=15.25 y=3.25 xVelocity=0.1 yVelocity=0.1 
ball name=BallE x=19.25 y=3.25 xVelocity=0.1 yVelocity=0.1 

# define some left flippers
leftFlipper name=lFlipper1 x=0 y=6 orientation=90 
leftFlipper name=lFlipper2 x=4 y=1 orientation=90 
leftFlipper name=lFlipper3 x=7 y=4 orientation=90
leftFlipper name=lFlipper4 x=5 y=2 orientation=90

# define some right flippers 
rightFlipper name=rFlipper1 x=2 y=15 orientation=0
rightFlipper name=rFlipper2 x=7 y=9 orientation=0
rightFlipper name=rFlipper3 x=10 y=12 orientation=0
rightFlipper name=rFlipper4 x=12 y=13 orientation=0
#random comment
#random whitespace will follow






















# define some circle bumpers
circleBumper name=CircleA x=5 y=18
circleBumper name=CircleB x=7 y=13

# define some triangle bumpers
triangleBumper name=Triangle1 x=19 y=0 orientation=90
triangleBumper name=Triangle2 x=10 y=18 orientation=90

#define some square bumpers
squareBumper name=Square1 x=4 y=2
squareBumper name=Square2 x=2 y=2

# define some absorbers
absorber name=Abs2 x=0 y=19 width=20 height=1 
















# define events between gizmos
fire trigger=CircleA action=rFlipper1
fire trigger=Abs2 action=lFlipper2