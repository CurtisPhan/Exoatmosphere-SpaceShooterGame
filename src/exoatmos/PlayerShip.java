package exoatmos;

import java.util.ArrayList;

/**	The PlayerShip Class
 * 
 *		Creates a PlayerShip object that the user can control
 *		Stores all of the information necessary for the player to have, detect grazes (when an object skims the ship) and perform all the actions the parent class can
 *
 */
public class PlayerShip extends Ship{
	private int lives;																					//the amount of lives the player has
	private int bombs;																				//the amount of bombs the player has 
	private int score;																					//the score accumulated by the player
	private int killCount;																			//the amount of kills the player has gotten
	private int grazeCount;																		//the amount of grazes the player has gotten
	private int invTimer;																				//the timer used to keep track of how long the player has beeen invincible
	private final static int SCORE_THRESHOLD_INCREMENT = 200000; 		//the amount the life/bomb score threshold increases by after its been reached
	private int lifeScoreThreshold = SCORE_THRESHOLD_INCREMENT/2;	//the score the player needs to have before it gains an extra life
	private int bombScoreThreshold = SCORE_THRESHOLD_INCREMENT;	//the score the player needs to have before it gains an extra bomb
	private boolean spreadFire;																//determines the player's fire mode (focus fire [false] or spread fire [true])
	private Hitbox grazeHitbox;																//the hitbox used to detect grazes
	private ArrayList<PlayerBullet> newlySpawnedBullets = new ArrayList<PlayerBullet> ();	//a list of all of the bullets the player has just spawned

	//Constructor
	public PlayerShip(double initXPos, double initYPos) {
		super(initXPos, initYPos);
		img = Database.getPlayerShip();
		width = 50;
		height = 50;
		speed = 4;
		xSpeed = 0;
		ySpeed = 0;
		hitboxes = new Hitbox[1];
		hitboxes[0] = new Hitbox (xPos + 20,  yPos + 20, 10, 10, this);
		grazeHitbox = new Hitbox (xPos,  yPos, width, height, this);
		lives = 3;
		bombs = 3;
		score = 0;
		killCount = 0;
		grazeCount = 0;
		invincible = true;
	}



	@Override
	public void reset() {
		super.reset();
		//resets the player specific fields
		grazeHitbox.reset();
		lives = 3;
		bombs = 3;
		score = 0;
		killCount = 0;
		grazeCount = 0;
		invTimer = 0;
		lifeScoreThreshold = SCORE_THRESHOLD_INCREMENT/2;
		bombScoreThreshold = SCORE_THRESHOLD_INCREMENT;
		spreadFire = false;
		invincible = true;
	}



	@Override
	public void move(){
		//keeps the player from moving off the screen
		if (xPos + xSpeed < Map.LEFT_BOUND || xPos + width  + xSpeed > Map.RIGHT_BOUND)
			xSpeed = 0;
		if (yPos + ySpeed < Map.TOP_BOUND  || yPos + height + ySpeed> Map.BOTTOM_BOUND)
			ySpeed = 0;

		super.move();
		//move the graze hitbox
		grazeHitbox.move();
	}

	/**Detects whether player has been grazed or not*/
	public boolean grazedBy(CollidableObject opposingObject){
		Hitbox opposingHitboxes[] = opposingObject.getHitboxes();

		//checks the graze hitbox against each of the opposing object's hitboxes
		for(int n = 0; n < opposingHitboxes.length; n++){
			if (grazeHitbox.intersects(opposingHitboxes[n]))
				return true;
		}
		return false;
	}

	@Override
	public boolean spawnBullets() {
		// TODO Auto-generated method stub
		//Clear previous list of spawned bullets and increment fire timer
		newlySpawnedBullets.clear();

		//Fire 3 bullets every 0.05 seconds
		if (fireTimer % 5 == 0){
			//spread bullets out if fire mode is spread fire, keep them in one direction if fire mode is focus fire
			if (spreadFire == true){																																	//spread fire
				newlySpawnedBullets.add(new PlayerBullet(xPos+5, yPos+25, Math.toRadians(100),10));
				newlySpawnedBullets.add(new PlayerBullet(xPos+20, yPos+10, Math.toRadians(90), 10));
				newlySpawnedBullets.add(new PlayerBullet(xPos+35, yPos+25, Math.toRadians(80), 10));
			}
			else{																																								//focus fire
				newlySpawnedBullets.add(new PlayerBullet(xPos+5, yPos+25, Math.toRadians(90), 10));
				newlySpawnedBullets.add(new PlayerBullet(xPos+20, yPos+10, Math.toRadians(90), 10));
				newlySpawnedBullets.add(new PlayerBullet(xPos+35, yPos+25, Math.toRadians(90), 10));
			}
		}
		fireTimer++;
		//return true if bullets have been spawned, false if not
		if (newlySpawnedBullets.isEmpty() == false)
			return true;
		return false;
	}

	/**Decrement lives by 1, make player invincible and reset position when player dies*/
	public void processDeath(){
		lives--;
		invincible = true;
		xPos = initXPos;
		yPos = initYPos;

		for (int x = 0; x < hitboxes.length; x++)
			hitboxes[x].reset();
		grazeHitbox.reset();
	}

	/**Increase invinciblility timer until 2 seconds have passed, then set player's invincibility to false and reset timer*/
	public void incrementInvTimer(){
		invTimer++;
		if (invTimer > 200){
			invincible = false;
			invTimer=0;
		}
	}

	/**Give the player extra lives and bombs when their score reaches certain thresholds*/
	public void scoreBonus(){
		if (score >= lifeScoreThreshold){												//lives
			lives++;
			lifeScoreThreshold+=SCORE_THRESHOLD_INCREMENT;
		}	
		if (score >= bombScoreThreshold){											//bombs
			bombs++;
			bombScoreThreshold+=SCORE_THRESHOLD_INCREMENT;
		}
	}

	public void pickUpPowerUp(PowerUp powerUp){
		if (powerUp.getType()==0)
			lives++;
		else if (powerUp.getType()==1)
			bombs++;
		else if (powerUp.getType()==2)
			score+=10000;
	}

	/**Return the amount of lives the player has*/
	public int getLives(){
		return lives;
	}

	/**Return the amount of bombs the player has*/
	public int getBombs(){
		return bombs;
	}

	/**Return the score the player has*/
	public int getScore(){
		return score;
	}

	/**Return the amount of kills the player has*/
	public int getKillCount(){
		return killCount;
	}

	/**Return the amount of grazes the player has*/
	public int getGrazeCount(){
		return grazeCount;
	}

	/**Return the list of bullets the player has just spawned*/
	public ArrayList<PlayerBullet> getNewlySpawnedBullets(){
		return newlySpawnedBullets;
	}

	/**Return the player's fire mode*/
	public boolean getSpreadFire() {
		return spreadFire;
	}

	/**Set the amount of lives the player has*/
	public void setLives(int lives){
		this.lives = lives;
	}

	/**Set the amount of bombs the player has*/
	public void setBombs(int bombs){
		this.bombs = bombs;
	}

	/**Set the score the player has*/
	public void setScore(int score){
		this.score = score;
	}

	/**Set the amount of kills the player has*/
	public void setKillCount(int killCount){
		this.killCount = killCount;
	}

	/**Set the amount of grazes the player has*/
	public void setGrazeCount(int grazeCount){
		this.grazeCount = grazeCount;
	}

	/**Set the player's fire mode*/
	public void setSpreadFire(boolean spreadFire) {
		this.spreadFire = spreadFire;
	}


	/**Setters for the fields from a parent class*/
	/**Set the x-component of the player's speed*/
	public void setXSpeed(double xSpeed){
		this.xSpeed = xSpeed;
	}

	/**Set the y-component of the player's speed*/
	public void setYSpeed(double ySpeed){
		this.ySpeed = ySpeed;
	}

	/**Set whether or not the player is invincible*/
	public void setInvincible(boolean invincible) {
		this.invincible = invincible;
	}

	/**Reset fire timer*/
	public void resetFireTimer() {
		fireTimer = 0;
	}
}
