package exoatmos;

/** The PlayerBullet Class
 * 
 *		Creates a PlayerBullet object that can collide with and take health from enemy ships, and perform all the actions the parent class can
 *
 */
public class PlayerBullet extends Bullet {

	//Constructor
	public PlayerBullet(double initXPos, double initYPos, double initAngle, double initSpeed) {
		super(initXPos, initYPos, initAngle, initSpeed);
		img = Database.getPlayerBullet();
		width = 10;
		height = 10;
		hitboxes = new Hitbox[1];
		hitboxes[0] = new Hitbox (xPos,  yPos, width, height, this);
		speed = 10;
		xSpeed = speed * Math.cos(angle);
		ySpeed = -speed * Math.sin(angle);
	}
}
