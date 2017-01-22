package exoatmos;

/** The Bullet Class
 * 
 *		Creates a Bullet object that can collide with and take health/lives away from ships and can home in on ships,
 *		and perform all the actions the parent class can
 *
 */
public class Bullet extends CollidableObject {
	protected boolean homing;		//Determines whether the bullet is homing in on a ship or not

	//Constructor
	public Bullet(double initXPos, double initYPos, double initAngle, double initSpeed) {
		super(initXPos, initYPos);
		angle = initAngle;
		speed = initSpeed;
		// TODO Auto-generated constructor stub
	}

	//Constructor for bullets with types
	public Bullet(double initXPos, double initYPos, double initAngle, double initSpeed, int type) {
		super(initXPos, initYPos, type);
		angle = initAngle;
		speed = initSpeed;
		// TODO Auto-generated constructor stub
	}

	/**Home in on a ship by adjusting the bullet's angle of motion to move towards the target*/
	protected void home(Ship ship){
		angle = target(ship);

		//Loops until angle is between 0 and 2Pi for ease of calculation
		while (angle < 0)
			angle += 2*Math.PI;
		while (angle > 2*Math.PI)
			angle -= 2*Math.PI;

		//changes angle of enemy bullet to only be directed downwards
		if (this instanceof EnemyBullet){
			if (angle < 5*Math.PI/4 && angle >= Math.PI/2)
				angle = 5*Math.PI/4;
			else if (angle >= 0 && angle < Math.PI/2 || angle > 7*Math.PI/4 && angle <= 2*Math.PI)
				angle = 7*Math.PI/4;
		}

		xSpeed = speed * Math.cos(angle);
		ySpeed = -speed * Math.sin(angle);

	}

}
