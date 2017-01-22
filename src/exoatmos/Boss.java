package exoatmos;

/**	The Boss Class
 * 
 *		Creates a Boss object that acts as the final obstacle before the end of a level/map, and perform all the actions the parent class can
 *
 *		BOSS PATTERN SUMMARY
 *			BOSS TYPE = 1, Ship with 2 guns
 *				Phase 0: Spawning, moving into position
 *				Phase 1: Continuous horizontal movement; Fires bullets in spreads from each gun
 *				Phase 2: Unmoving; Fires sets of bullets from each gun directed towards the player
 *				Phase DEATH_PHASE: Move towards the top of the screen to despawn itself after it has lost all of its health
 */
public class Boss extends EnemyShip{
	private int phase;														//The phase of the boss, determines movement and bullet spawning, dependent on health
	public static final int DEATH_PHASE = 100;			//The final phase that is initiated when the boss loses all of its health, 100 is an arbitrary value

	//Constructor
	public Boss(double initXPos, double initYPos, int type) {
		super(initXPos, initYPos, Math.toRadians(270), type);
		img = Database.getBoss(type);
		//Determine the health, width, height, hitboxes and score value of the boss depending on type
		switch(this.type){
		case 0:
			initHealth = 2000;
			width = 230;
			height = 115;
			hitboxes = new Hitbox[5];
			hitboxes[0] = new Hitbox (xPos+10,  yPos+30, width-20, 35, this);
			hitboxes[1] = new Hitbox (xPos+10,  yPos+65, 25, 45, this);
			hitboxes[2] = new Hitbox (xPos+width-35,  yPos+65, 25, 45, this);
			hitboxes[3] = new Hitbox (xPos+55,  yPos+10, 120, 20, this);
			hitboxes[4] = new Hitbox (xPos+75,  yPos+65, 80, 20, this);
			scoreValue = 60000;
			break;
		}
		health = initHealth;
		changePhase(0);
		// TODO Auto-generated constructor stub
	}

	/**Reset move and fire timers, change pattern, and set initial speed and angle*/
	public void changePhase(int phase){
		this.phase = phase;
		moveTimer = 0;
		fireTimer = 0;
		switch (type){
		case 0:
			switch (phase){
			case 0:
				invincible = true;
				speed = 0.5;
				angle = Math.toRadians(270);
				break;
			case 1:
				speed = 1;
				angle = Math.toRadians(0);
				break;
			case 2:
				if (xPos<initXPos){
					speed = 1;
					angle = Math.toRadians(0);
				}
				else if (xPos>initXPos){
					speed = 1;
					angle = Math.toRadians(180);
				}
				else {
					speed = 0;
					angle = Math.toRadians(270);
				}
				break;
			case DEATH_PHASE:
				speed = 1;
				if (xPos<initXPos)
					angle = Math.toRadians(0);
				else if (xPos>initXPos)
					angle = Math.toRadians(180);
				else 
					angle = Math.toRadians(90);
				break;
			}
			break;
		}
		xSpeed = speed * Math.cos(angle);
		ySpeed = -speed * Math.sin(angle);
	}

	/**Reset Phase to 0*/
	public void reset() {
		super.reset();
		changePhase(0);
	}

	/**Changes phase when boss' health falls below certain points*/
	public void healthCheck(){
		//changes phase into death phase when health falls below 0
		if (health == 1250)
			changePhase(2);
		if (health <= 0)
			changePhase(DEATH_PHASE);
	}
	@Override
	//Fire bullets in different ways based on type and phase
	public boolean spawnBullets() {
		// TODO Auto-generated method stub
		newlySpawnedBullets.clear();

		switch(type){
		case 0:
			switch(phase){
			case 1:		/**Spread Shots**/
				//Fires 3 times every 2 seconds, 0.4 seconds apart from each other
				if (fireTimer % 40 == 0  && fireTimer % 200 < 120){
					//Spawn 5 bullets from each gun in a spread
					for (int x = -3; x <= 3; x++){
						newlySpawnedBullets.add(new EnemyBullet(xPos+13, yPos+height -20, Math.toRadians(270) + x*Math.toRadians(12.5), 2, 0));				//left gun
						newlySpawnedBullets.add(new EnemyBullet(xPos+width-35, yPos+height - 20, Math.toRadians(270) + x*Math.toRadians(12.5), 2, 0));	//right gun
					}
				}
				break;
			case 2:		
				/**Targeted Shots**/
				if (fireTimer >= 200 && fireTimer < 1200){
					//Fires twice every 0.8 seconds, 0.2 seconds apart from each other
					if (fireTimer % 20 == 0 && fireTimer % 80 < 60){
						double targetAngle1 = target(Map.getPlayer(), xPos + 23, yPos +height - 15);				//left gun angle
						double targetAngle2 = target(Map.getPlayer(), xPos +width - 25, yPos +height - 15);	//right gun angle
						//Spawn 9 bullets from each gun in rectangle formation
						for (int x = -1; x <=1; x++){
							for (int y = -1; y<=1; y++){
								newlySpawnedBullets.add(new EnemyBullet(xPos+18+x*8, yPos+height - 5 + y*12.5, targetAngle1, 4, 4));			//left gun
								newlySpawnedBullets.add(new EnemyBullet(xPos+width-30+x*8, yPos+height -5 +y*12.5, targetAngle2,4, 4));	//right gun
							}
						}
					}
				}
				/**Delayed Quick Targeted Shots**/
				else if (fireTimer>=1200 && fireTimer<2200){
					//Fires every 1 second
					if (fireTimer % 60 == 0 ){
						//Spawn 5 bullets from each gun in a spread
						for (int x = -2; x <=2; x++){
							newlySpawnedBullets.add(new EnemyBullet(xPos+13, yPos+height -20, Math.toRadians(270 +x*40), 1, 5));			//left gun
							newlySpawnedBullets.add(new EnemyBullet(xPos + width - 35, yPos+height -20, Math.toRadians(270 +x*40), 1, 5));			//right gun
						}
					}
				}
				//reset fire timer
				else if (fireTimer >= 2200)
					fireTimer = 0;
				break;
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
	public void move() {
		// TODO Auto-generated method stub
		switch(type){
		case 0:
			switch(phase){
			case 0: 	/**Initial Phase*/
				//Move from top of screen into initial position
				if (yPos>100){
					invincible = false;
					changePhase(1);
				}
				break;
			case 1:	/**Continuous Horizontal Movement*/
				//Start moving the other direction once it hits the side walls
				if (xPos<Map.LEFT_BOUND+10 || xPos+width>Map.RIGHT_BOUND -10){
					angle+= Math.PI;
					xSpeed = speed * Math.cos(angle);
					ySpeed = -speed * Math.sin(angle);
				}
				break;
			case 2:	/**Move to Centre of Screen*/
				if (xPos == initXPos){
					speed = 0;
					angle =Math.toRadians(270);
					xSpeed = speed * Math.cos(angle);
					ySpeed = -speed * Math.sin(angle);
				}
			case DEATH_PHASE:
				if (xPos == initXPos){
					angle =Math.toRadians(90);
					xSpeed = speed * Math.cos(angle);
					ySpeed = -speed * Math.sin(angle);
				}
				break;
			}
			break;
		}
		super.move();
	}

	/**Return the current phase of the boss*/
	public int getPhase(){
		return phase;
	}

}
