package exoatmos;

/**	The Ship Class
 * 
 *		Creates a Ship object that can fire bullets, reset to its original state, and perform all the actions the parent class can
 *
 */
public abstract class Ship extends CollidableObject {

	protected int fireTimer;				//the timer used to determine when to fire bullets
	protected boolean invincible;		//determines whether ship is invulnerable to damage or not
	//Constructor
	public Ship(double initXPos, double initYPos) {
		super(initXPos, initYPos);
	}

	//Constructor for ships with types
	public Ship(double initXPos, double initYPos, int type) {
		super(initXPos, initYPos,type);
	}

	/**Reset ship to its original state*/
	public void reset(){
		xPos = initXPos;
		yPos = initYPos;
		fireTimer = 0;
		moveTimer = 0;

		for (int x = 0; x < hitboxes.length; x++)
			hitboxes[x].reset();
	}

	/**Spawns bullets from the ship, returns true if bullets were spawned*/
	public abstract boolean spawnBullets();

	/**Returns whther ship is invincible or not*/
	public boolean isInvincible() {
		return invincible;
	}
}
