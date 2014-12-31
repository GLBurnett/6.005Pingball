board name=Example

# define a ball
ball name=Ball x=1.8 y=4.5 xVelocity=-3.4 yVelocity=-2.3 

# define some bumpers
squareBumper name=Square x=0 y=2
circleBumper name=Circle x=4 y=3
triangleBumper name=Tri x=1 y=1 orientation=270

# define some flippers
  leftFlipper name=FlipL x=10 y=7 orientation=0 
rightFlipper name=FlipR x=12 y=7 orientation=0

# define an absorber to catch the ball
absorber name=Abs x=0 y=19 width=20 height=1 

# define events between gadgets
fire trigger=Square action=FlipL

# make the absorber self-triggering
fire trigger=Abs action=Abs 