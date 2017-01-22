package exoatmos;

import java.util.ArrayList;

/**	The EnemyShip Class
 * 
 *		Creates an EnemyShip object that serves as an obstacle to the player
 *		Stores all of the information necessary for the enemy to have,  and perform all the actions the parent class can
 *
 */
public abstract class EnemyShip extends Ship {
	protected double initAngle;				//the initial angle the enemy was moving at
	protected int initHealth;						//the initial health of the enemy; should also be final, but because of how initHealth is determined, it can't be final
	protected int health;							//the current health of the enemy
	protected int scoreValue;					//the score given to the player when they kill this enemy
	protected boolean grazed;					//determines whether or not the enemy has grazed/been grazed by the player already
	protected boolean inSpawn;				//determines whether or not the enemy is in spawn (is in the process of entering the screen)
	protected ArrayList<EnemyBullet> newlySpawnedBullets = new ArrayList<EnemyBullet> ();	//a list of all of the bullets the enemy has just spawned

	//Constructor
	public EnemyShip(double initXPos, double initYPos, double initAngle, int type) {
		super(initXPos, initYPos, type);
		this.initAngle = initAngle;
		inSpawn = true;
		grazed = false;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void reset() {
		super.reset();
		//resets the enemy specific fields
		angle = initAngle;
		health = initHealth;
		inSpawn = true;
		xSpeed = speed * Math.cos(angle);
		ySpeed = -speed * Math.sin(angle);
	}

	/**Return the initial health of the enemy*/
	public void setInitHealth(int initHealth){
		this.initHealth = initHealth;
	}

	/**Return the initial health of the enemy*/
	public int getInitHealth(){
		return initHealth;
	}

	/**Return the current health of the enemy*/
	public int getHealth(){
		return health;
	}

	/**Return the score value of the enemy*/
	public int getScoreValue() {
		return scoreValue;
	}

	/**Return whether or not the enemy has grazed the player already*/
	public boolean getGrazed(){
		return grazed;
	}

	/**Return whether or not the enemy is in spawn*/
	public boolean isInSpawn() {
		return inSpawn;
	}

	/**Return the list of bullets the player has just spawned*/
	public ArrayList<EnemyBullet> getNewlySpawnedBullets(){
		return newlySpawnedBullets;
	}

	/**Set the current health of the enemy*/
	public void setHealth(int health){
		this.health = health;
	}

	/**Set whether or not the enemy has grazed the player already*/
	public void setGrazed(boolean grazed){
		this.grazed = grazed;
	}

	/**Set whether or not the enemy is in spawn*/
	public void setInSpawn(boolean inSpawn) {
		this.inSpawn = inSpawn;
	}
}
