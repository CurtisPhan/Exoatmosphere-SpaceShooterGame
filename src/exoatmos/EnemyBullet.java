package exoatmos;

/** The EnemyBullet Class
 * 
 *		Creates a EnemyBullet object that can collide and graze with, and take lives from the player ship, and perform all the actions the parent class can
 *
 *		ENEMY TYPE SUMMARY
 *			Type 0: Unchanging movement pattern
 *			Type 1: Homing Bullet
 *			Type 2: Moves in a wave
 *			Type 3: Moves in a wave
 *			Type 4: Unchanging movement pattern
 *			Type 5: Starts moving slowly, stops, then moves rapidly towards the player
 */
public class EnemyBullet extends Bullet {

	private boolean grazed;		//determines whether or not the enemy has grazed/been grazed by the player already

	//Constructor
	public EnemyBullet(double initXPos, double initYPos, double initAngle, double initSpeed, int type) {
		super(initXPos, initYPos, initAngle, initSpeed, type);
		img = Database.getEnemyBullet();
		grazed = false;
		
		//determine width, height and behaviour of bullet depending on the type
		switch(this.type){
		case 0:
		case 2:
		case 3:
			width = 20;
			height = 20;
			break;
		case 1:
			width = 20;
			height = 20;
			homing = true;
			break;
		case 4:
			width = 10;
			height = 10;
			break;
		case 5:
			width = 20;
			height = 20;
			break;
		}
		hitboxes = new Hitbox[1];
		hitboxes[0] = new Hitbox (xPos,  yPos, width, height, this);
		xSpeed = speed * Math.cos(angle);
		ySpeed = -speed * Math.sin(angle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void move(){
		switch (type){
		case 1:		/**Homing Bullet**/
			//stop homing after bullet travels below a certain point
			if (yPos > 800 || yPos > Map.getPlayer().getYPos())
				homing = false;
			else 
				homing = true;

			if (homing == true)
				home(Map.getPlayer());
			break;
		case 2:		/**Wave Bullet 1**/		
			//move bullet in a sine wave going down (goes to right first)
			ySpeed = -Math.sin(angle)*speed;
			xSpeed = initXPos + 15*Math.sin(0.009*Math.PI*(yPos-initYPos)) - xPos;
			angle = -Math.atan2(ySpeed, xSpeed);
			break;
		case 3:		/**Wave Bullet 2**/
			//move bullet in a negative sine wave going down (goes to left first)
			ySpeed = -Math.sin(angle)*speed;
			xSpeed = initXPos - 15*Math.sin(0.009*Math.PI*(yPos-initYPos)) - xPos;
			angle = -Math.atan2(ySpeed, xSpeed);
			break;
		case 5:		/**Delayed Targeted Shot*/
			//stop movement after 0.2 seconds
			//start movement again 0.5 seconds after
			if (moveTimer == 20){
				speed = 0;
				xSpeed = speed * Math.cos(angle);
				ySpeed = -speed * Math.sin(angle);
			}
			if (moveTimer == 70){
				angle = target(Map.getPlayer());
				speed = 15;
				xSpeed = speed * Math.cos(angle);
				ySpeed = -speed * Math.sin(angle);
			}
				
			break;
		}
			

		super.move();
	}

	/**Return whether or not the enemy bullet has grazed the player already*/
	public boolean getGrazed(){
		return grazed;
	}

	/**Set whether or not the enemy bullet has grazed the player already*/
	public void setGrazed(boolean grazed){
		this.grazed = grazed;
	}

}
