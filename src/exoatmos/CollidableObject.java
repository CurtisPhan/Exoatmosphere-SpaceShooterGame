package exoatmos;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

/**	The CollidableObject Class
 * 
 *		Creates a CollidableObject object that can move as well as target an-d collide with other CollidableObjects
 *
 */

public abstract class CollidableObject{
	protected double initXPos;			//the initial x-coordinate of the top-left corner of the object
	protected double initYPos;			//the initial y-coordinate of the top-left corner of the object
	protected double xPos;				//the x-coordinate of the top-left corner of the object
	protected double yPos;				//the y-coordinate of the top-left corner of the object
	protected double width;				//the width of the object
	protected double height;			//the height of the object
	protected double speed;				//the speed at which the object moves (in pixels/cycle)
	protected double angle;				//the angle at which the object moves (in radians)
	protected double xSpeed;			//the speed at which the object will move in the x direction (in pixels/cycle)
	protected double ySpeed;			//the speed at which the object will move in the y direction (in pixels/cycle)
	protected Hitbox[] hitboxes;		//the areas used for hit detection
	protected int moveTimer;			//the timer used to determine when to change movement
	protected int type;						//determines width, height, hitboxes, movement behaviour, and bullet spawning behaviour and health
	protected Image img;					//the image associated with the object

	//Constructor 
	public CollidableObject(double initXPos, double initYPos){
		this.initXPos = initXPos;
		this.initYPos = initYPos;
		xPos = initXPos;
		yPos = initYPos;
	}

	//Constructor for objects with a type
	public CollidableObject(double initXPos, double initYPos,int type){
		this.initXPos = initXPos;
		this.initYPos = initYPos;
		xPos = initXPos;
		yPos = initYPos;
		this.type = type;
	}


	/**target the specified ship and returns the angle that this object should move or fire bullets towards to hit the target*/
	protected double target(Ship ship){
		double dx = (ship.getXPos() + ship.getWidth()/2) - (xPos + width/2);		//determines x component of the distance between this and the target
		double dy = (ship.getYPos() + ship.getHeight()/2) - (yPos + height/2);		//determines y component of the distance between this and the target

		double angle = -Math.atan2(dy, dx); 																//determines angle based on x and y component

		return angle;
	}

	/**target the specified ship and returns the angle that the bullets this object fired should move towards to hit the target
	 * xPos and yPos are the initial coordinates of the centre of the bullets to be spawned
	 */
	protected double target(Ship ship, double xPos, double yPos){
		double dx = (ship.getXPos() + ship.getWidth()/2) - (xPos);
		double dy = (ship.getYPos() + ship.getHeight()/2) - (yPos);

		double angle = -Math.atan2(dy, dx);

		return angle;
	}

	/**move object and its hitboxes, and increment moveTimer*/
	public void move(){
		xPos += xSpeed;
		yPos += ySpeed;

		for (int x = 0; x < hitboxes.length; x++)
			hitboxes[x].move();

		moveTimer++;
	}

	/**Detects collisions between this object and specified opposing CollidableObject*/
	public boolean collidesWith(CollidableObject opposingObject){
		Hitbox opposingHitboxes[] = opposingObject.getHitboxes();

		//checks each one of this object's hitboxes against each of the opposing object's hitboxes
		for (int m = 0; m < hitboxes.length; m++){
			for(int n = 0; n < opposingHitboxes.length; n++){
				if (hitboxes[m].intersects(opposingHitboxes[n]))
					return true;
			}
		}
		return false;
	}


	/**Draws the image associated with this object or a placeholder(for debug reasons)*/
	public void draw (Graphics g){
		if (img != null)
			g.drawImage(img, (int)Math.round(xPos), (int) Math.round(yPos), (int) width, (int) height, null);
		else{
			if (this instanceof PlayerShip)
				g.setColor(Color.yellow);
			else if (this instanceof PlayerBullet)
				g.setColor(Color.green);		
			else if (this instanceof EnemyShip)
				g.setColor(Color.red);
			else if (this instanceof EnemyBullet)
				g.setColor(Color.blue);
			else if (this instanceof Boss)
				g.setColor(Color.white);
			g.fillRect((int)Math.round(xPos), (int)Math.round(yPos), (int)width, (int)height);
		}
		//draws the circle used to indicate the actual hitbox of the player ship
		if (this instanceof PlayerShip){
			g.setColor(Color.yellow);
			g.fillOval((int)(hitboxes[0].getLeft()), (int)(hitboxes[0].getTop()), (int)(hitboxes[0].getRight() -hitboxes[0].getLeft()), (int)(hitboxes[0].getBottom() -hitboxes[0].getTop()));
		}
		/**
		//draws outline of hitboxes
		g.setColor(Color.magenta);
		if (this instanceof Bullet==false)
			for (int m = 0; m < hitboxes.length; m++)
				g.drawRect((int)hitboxes[m].getLeft(), (int)hitboxes[m].getTop(), (int)(hitboxes[m].getRight() - hitboxes[m].getLeft()), (int)(hitboxes[m].getBottom() - hitboxes[m].getTop()));
		 */

	}

	/**Returns the object's x-position (top-left corner)*/
	public double getXPos() {
		return xPos;
	}

	/**Returns the object's y-position (top-left corner)*/
	public double getYPos() {
		return yPos;
	}

	/**Returns the object's width*/
	public double getWidth() {
		return width;
	}

	/**Returns the object's height*/
	public double getHeight() {
		return height;
	}

	/**Return the object's speed*/
	public double getSpeed() {
		return speed;
	}

	/**Return the object's speed in the x direction*/
	public double getXSpeed() {
		return xSpeed;
	}

	/**Return the object's speed in the y direction*/
	public double getYSpeed() {
		return ySpeed;
	}

	/**Return all of the object's hitboxes*/
	public Hitbox[] getHitboxes(){
		return hitboxes;
	}
}
