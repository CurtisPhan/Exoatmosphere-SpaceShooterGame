package exoatmos;


public class Hitbox  {
	private CollidableObject object;	//the object this hitbox belongs to
	private double initLeft;				//the left boundary of the hitbox
	private double initRight;				//the top boundary of the hitbox
	private double initTop;					//the right boundary of the hitbox
	private double initBottom;			//the bottom boundary of the hitbox
	private double left;						//the left boundary of the hitbox
	private double right;					//the top boundary of the hitbox
	private double top;						//the right boundary of the hitbox
	private double bottom;				//the bottom boundary of the hitbox
	
	//Constructor
	public Hitbox(double initXPos, double initYPos, double width, double height, CollidableObject object){
		this.initLeft = initXPos;		
		this.initRight = initXPos + width;		
		this.initTop = initYPos;	
		this.initBottom = initYPos + height;	
		this.object = object;
		left = initLeft;		
		right = initRight;		
		top = initTop;	
		bottom = initBottom;		
	}

	/**Reset hitbox back to its original position*/
	public void reset(){
		left = initLeft;		
		right = initRight;		
		top = initTop;	
		bottom = initBottom;	
	}

	/**Moves the hitbox at the same rate that the CollidableObject it is associated with moves at*/
	public void move(){
		left += object.getXSpeed();
		right += object.getXSpeed();
		top += object.getYSpeed();
		bottom += object.getYSpeed();
	}

	/**Checks if this hitbox intersects another hitbox*/
	public boolean intersects(Hitbox opposingHitbox){
		//Returns true if one hitbox is within the boundaries of the other hitbox in both the X-axis and the Y-axis, return false if not
		if (left > opposingHitbox.getLeft() && left < opposingHitbox.getRight() || 
				right > opposingHitbox.getLeft() && right < opposingHitbox.getRight() ||
				left < opposingHitbox.getLeft() && right > opposingHitbox.getRight() )
		{
			if (top > opposingHitbox.getTop() && top < opposingHitbox.getBottom() || 
					bottom > opposingHitbox.getTop() && bottom < opposingHitbox.getBottom() ||
					top < opposingHitbox.getTop() && bottom > opposingHitbox.getBottom() )
				return true;
		}
		return false;
	}
	
	/**Return the left boundary of the hitbox*/
	public double getLeft() {
		return left;
	}

	/**Return the right boundary of the hitbox*/
	public double getRight() {
		return right;
	}

	/**Return the top boundary of the hitbox*/
	public double getTop() {
		return top;
	}

	/**Return the bottom boundary of the hitbox*/
	public double getBottom() {
		return bottom;
	}
}
