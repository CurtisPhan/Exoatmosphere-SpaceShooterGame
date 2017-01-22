package exoatmos;

/**	The NormalEnemy Class
 * 
 *		Creates an NormalEnemy object that acts in various ways to impede the player's progress through the level/map, and perform all the actions the parent class can
 *
 *		ENEMY TYPE SUMMARY
 *			Type 0: Unchanging movement pattern; Only fires bullet downward
 *			Type 1: Unchanging movement pattern; Fires bullets directed towards the player
 *			Type 2: Unchanging movement pattern; Fires bullets in a wave pattern
 *			Type 3: Comes in from sides of the screen then curves downwards until it goes straight down; Fires bullets directed towards the player
 *			Type 4: Comes in from the top and bottom, then moves in a circle; Fires bullets directed towards the centre of the circle
 */
public class NormalEnemy extends EnemyShip{
	public static final double DROP_RATE = 0.05;	//the rate at which power-ups have a chance to spawn from an enemy

	//Constructor
	public NormalEnemy(double initXPos, double initYPos, double initAngle, int type) {
		super(initXPos, initYPos, initAngle, type);
		angle = initAngle;
		img = Database.getEnemy(type);

		//determine width and height of enemy depending on the type
		switch(this.type){
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
			width = 50;
			height = 50;
			break;
		}

		//determine health, speed and score value of enemy depending on the type
		switch(this.type){
		case 0:
		case 1:
			initHealth = 12;
			speed = 1.5;
			scoreValue = 500;
			break;
		case 2:
			initHealth = 20;
			speed = 1.5;
			scoreValue = 1000;
			break;
		case 3:
			initHealth = 12;
			speed = 2;
			scoreValue = 800;
			break;
		case 4: 
			initHealth = 80;
			speed = 2;
			scoreValue = 5000;
			break;
		}

		health = initHealth;
		hitboxes = new Hitbox[1];
		hitboxes[0] = new Hitbox (xPos,  yPos, width, height, this);
		xSpeed = speed * Math.cos(this.angle);
		ySpeed = -speed * Math.sin(this.angle);
		// TODO Auto-generated constructor stub
	}

	@Override
	//Fire bullets in different ways based on type
	public boolean spawnBullets() {
		// TODO Auto-generated method stub
		//Clear previous list of spawned bullets 
		newlySpawnedBullets.clear();

		switch(type){
		case 0:
			//Fires 2 bullets every 1.5 seconds, 0.2 seconds apart from each other
			if (fireTimer == 120 || fireTimer == 150)
				newlySpawnedBullets.add(new EnemyBullet(xPos+15, yPos+15, 3*Math.PI/2, 3, 0));
			//Reset fire timer when it crosses this threshold
			else if (fireTimer >= 150)
				fireTimer = 0;
			break;
		case 1:
			//Fires 3 bullets  at the same time, every 2 seconds
			if (fireTimer == 120){
				double targetAngle = target(Map.getPlayer());
				for (int x = -1; x <=1; x++)
					newlySpawnedBullets.add(new EnemyBullet(xPos+15, yPos+15, targetAngle+x*Math.toRadians(20), 3, 0));
			}
			//Reset fire timer when it crosses this threshold
			else if (fireTimer >= 200)
				fireTimer = 0;
			break;
		case 2:
			//Fire 2 bullets at the same time, every 0.1 seconds
			if (fireTimer % 10 == 0){
				newlySpawnedBullets.add(new EnemyBullet(xPos+15, yPos+25, 3*Math.PI/2, 4, 2));
				newlySpawnedBullets.add(new EnemyBullet(xPos+15, yPos+25, 3*Math.PI/2, 4, 3));
			}
			break;
		case 3:
			//Don't start firing until enemy starts turning
			if (moveTimer < 150)
				fireTimer = 0;
			//Fires 2 bullets every 2 seconds, 0.2 seconds apart from each other
			if (fireTimer == 20 || fireTimer == 40){
				double targetAngle = target(Map.getPlayer());
				newlySpawnedBullets.add(new EnemyBullet(xPos+15, yPos+15, targetAngle, 3, 0));
			}
			//Reset counter when it crosses this threshold
			else if (fireTimer >= 200)
				fireTimer = 0;
			break;
		case 4: 
			//When enemy is in circular motion, fire a bullet towards the centre of the circle every 0.05 seconds
			if (moveTimer>=240 & angle > initAngle - 7*Math.PI){
				if (fireTimer % 5 == 0){
					newlySpawnedBullets.add(new EnemyBullet(xPos+15, yPos+15, angle - Math.toRadians(90), 4, 0));
				}
			}
			break;
		}
		fireTimer++;
		//return true if bullets have been spawned, false if not
		if (newlySpawnedBullets.isEmpty() == false)
			return true;
		return false;
	}

	@Override
	public void move(){
		switch(type){
		case 3:
			//starts turning after 1.5 seconds from spawn and continues to turn until it is going straight down
			//turns clockwise if coming from the left, counterclockwise if coming from the right
			if (moveTimer >= 150 & angle != Math.toRadians(270)){
				if (angle < Math.toRadians(270))
					angle +=Math.toRadians(2);
				else if (angle > Math.toRadians(270))
					angle -=Math.toRadians(2);
				xSpeed = speed * Math.cos(angle);
				ySpeed = -speed * Math.sin(angle);
			}
			break;
		case 4:
			//starts moving in a circle after 2.4 seconds from spawn and continues to do so until it has completed 3.5 rotations
			if (moveTimer >= 240 & angle > initAngle - 7*Math.PI){
				angle -= Math.toRadians(0.5);
				xSpeed = speed * Math.cos(angle);
				ySpeed = -speed * Math.sin(angle);
			}
			break;
		}
		super.move();
	}

	/**Has a 1 in 20 chance of dropping a powerup*/
	public PowerUp dropPowerUp(int randNum){
		if (randNum==7)
			return new PowerUp(xPos+width/2 -15, yPos+height/2-15);
		else
			return null;
	}
}
