package exoatmos;

import java.util.Random;
/** The PowerUp Class
 * 
 *		Creates a PowerUp object that drops from enemies and give the player various bonuses
 *
 */
public class PowerUp extends CollidableObject {

	//Constructor
	public PowerUp(double initXPos, double initYPos) {
		super(initXPos, initYPos);
		Random rng = new Random();
		int typeDeterminant = rng.nextInt(10);
		if (typeDeterminant >= 0 & typeDeterminant<=3)
			type = 0;
		else if (typeDeterminant >= 4 & typeDeterminant<=7)
			type = 1;
		else
			type = 2;
		img = Database.getPowerUp(type);
		
		width = 30;
		height =30;
		speed = 1.3;
		angle = Math.toRadians(270);
		hitboxes = new Hitbox[1];
		hitboxes[0] = new Hitbox (xPos,  yPos, width, height, this);
		xSpeed = speed * Math.cos(angle);
		ySpeed = -speed * Math.sin(angle);
		// TODO Auto-generated constructor stub
	}
	
	/**Returns the powerup's type*/
	public int getType() {
		return type;
	}

}
