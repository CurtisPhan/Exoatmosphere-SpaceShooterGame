package exoatmos;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

/**	The Map Class
 * 
 *		Creates a Map object that creates enemy ships and bosses, and spawn them at the appropriate times
 *
 */

public class Map{
	private static PlayerShip player;							//the player ship object
	private NormalEnemy[] listOfEnemies;				//the list of all of the enemies in a level
	private Boss[] listOfBosses;									//the list of all bosses in a level
	private ArrayList <NormalEnemy> newlySpawnedEnemies = new ArrayList <NormalEnemy>();	//a list of all of the enemies that have just been spawned
	private Boss newlySpawnedBoss;						//the boss that has just been spawned
	private int level;													//the level this map is associated with
	private int timer;													//the timer used to determine when to spawn enemies
	private int pattern;												//the current enemy pattern, used to determine when to spawn what enemies
	private int eIndex;													//the position an enemy will be in the list of enemies or the index of the first enemy in a pattern
	private Image bgImg;											//the scrolling image used for backdrop of the map
	private int bg1y;													//map scrolls by using two images stacked on top of each other - the y-coordinate of the first image
	private int bg2y;													//																											   - the y-coordinate of the second image
	public static final int LEFT_BOUND = 250;			//the x-coordinate of the left boundary of the map in the applet
	public static final int RIGHT_BOUND = 900;		//the x-coordinate of the right boundary of the map in the applet
	public static final int TOP_BOUND = 0;				//the y-coordinate of the top boundary of the map in the applet
	public static final int BOTTOM_BOUND = 850;	//the y-coordinate of the bottom boundary of the map in the applet

	//Constructor
	public Map(int level) {
		// TODO Auto-generated constructor stub
		this.level = level;
		bgImg = Database.getBackground();
		bg1y = 0;
		bg2y = -Map.BOTTOM_BOUND;
		pattern = 1;
		if (level == 1){
			listOfEnemies = new NormalEnemy[55];
			listOfBosses = new Boss[1];
			listOfBosses[0] = new Boss(Map.LEFT_BOUND + 210, Map.TOP_BOUND - 100, 0);

			int xPos;		//determines initial x-coordinate of enemy position
			int yPos;		//determines initial y-coordinate of enemy position

			//PATTERN A - an assortment of simple enemies
			createEnemy(200,-50, Math.toRadians(270), 0);
			createEnemy(500,-50, Math.toRadians(270), 0);
			createEnemy(300,-50, Math.toRadians(270), 0);
			createEnemy(100,-50, Math.toRadians(270), 0);
			createEnemy(600,-50, Math.toRadians(270), 0);		//enemy #5
			createEnemy(400,-50, Math.toRadians(270), 0);

			//PATTERN B - waves of enemies that come in from the sides
			//Enemies # 7-26
			for (int x = 0; x<5;x++){
				createEnemy(-50,0, Math.toRadians(320), 1);
				createEnemy(700,0, Math.toRadians(220), 1);
				createEnemy(-100,100, Math.toRadians(320), 1);
				createEnemy(750,100, Math.toRadians(220), 1);
			}

			//PATTERN C - waves of wave bullet firing enemies
			//Enemies # 27-40
			for (int x = 0; x < 14; x++){
				xPos =(x*130 +40) % 585;
				createEnemy(xPos,-50, Math.toRadians(270), 2);
			}

			//PATTERN D - enemies that come in from the sides and curve down
			//Enemies #41-48
			for (int x = 0; x < 4; x++){
				xPos = - 80 - x*75;
				yPos = 30 + x*70;
				createEnemy(xPos,yPos, Math.toRadians(360), 3);
				createEnemy(650 - xPos,yPos, Math.toRadians(180), 3);
			}

			//PATTERN E - enemies that move in a circle
			//Enemies #49-54
			for (int x = 0; x < 3; x++){
				createEnemy(95,855,Math.toRadians(90), 4);
				createEnemy(555, -105,Math.toRadians(270), 4);
			}
		}
	}

	/**Resets the map by resetting the timer, pattern, scrolling background, and all of the enemies and bosses*/
	public void reset(){
		//Reset all of the enemies in this level
		for (int x = 0; x < listOfEnemies.length; x++){
			if (listOfEnemies[x]==null)
				break;
			listOfEnemies[x].reset();
		}
		//Reset all of the bosses in this level
		for (int x = 0; x < listOfBosses.length; x++){
			if (listOfBosses[x]==null)
				break;
			listOfBosses[x].reset();
		}
		bg1y = 0;
		bg2y = -Map.BOTTOM_BOUND;
		timer = 0;
		pattern = 1;
		newlySpawnedEnemies.clear();
		newlySpawnedBoss= null;
	}

	/**	Spawn enemies/bosses at the appropriate times
	 * 	When those appropriate times are is determined by the current pattern
	 * 	Pattern method names don't necessarily correspond with pattern order
	 */
	public void run(){ 
		timer++;
		newlySpawnedEnemies.clear();
		newlySpawnedBoss = null;
		switch (pattern){
		case 1: patternA();break;
		case 2: patternC();break;
		case 3: patternD(); break;
		case 4: patternB(); break;
		case 5: patternE(); break;
		case 6: bossPattern(); break;
		}
	}

	/********PATTERNS*********/
	/**All patterns spawn one or more enemies or a boss when the timer reaches a certain number**/
	/**Patterns are different depending on which level the map corresponds to*/
	/**eIndex is set to be equal to the index of the first enemy in the pattern in the listOfEnemies all having indices greater than eIndex**/
	/**When the timer reaches one final number, it resets the timer and switches over to the next pattern*/

	private void patternA(){
		if (level == 1){
			eIndex = 0;
			switch(timer){
			case 20:
				spawnEnemy(eIndex + 1);break;
			case 24:
				spawnEnemy(eIndex + 2);break;
			case 36:
				spawnEnemy(eIndex + 3);break;
			case 42:
				spawnEnemy(eIndex + 4);break;
			case 51:
				spawnEnemy(eIndex + 5);break;	
			case 60:
				spawnEnemy(eIndex + 6);break;
			case 70:
				timer = 0;
				pattern++;
				break;
			}
		}
	}

	private void patternB(){
		if (level == 1){
			eIndex = 6;
			switch(timer){
			case 10:
				spawnEnemy(eIndex + 1);break;
			case 15:
				spawnEnemy(eIndex + 2);break;
			case 25:
				spawnEnemy(eIndex + 3);
				spawnEnemy(eIndex + 5);
				break;
			case 40:
				spawnEnemy(eIndex + 4);
				spawnEnemy(eIndex + 6);
				break;
			case 70:
				spawnEnemy(eIndex + 7);break;
			case 75:
				spawnEnemy(eIndex + 8);break;
			case 80:
				spawnEnemy(eIndex + 9);break;
			case 110:
				spawnEnemy(eIndex + 10);break;
			case 115:
				spawnEnemy(eIndex + 11);break;
			case 120:
				spawnEnemy(eIndex + 12);break;
			case 140:
				spawnEnemy(eIndex+13);break;
			case 145:
				spawnEnemy(eIndex+14);break;
			case 150:
				spawnEnemy(eIndex+15);break;
			case 155:
				spawnEnemy(eIndex+16);break;
			case 180:
				spawnEnemy(eIndex+17);
				spawnEnemy(eIndex+18);
				spawnEnemy(eIndex+19);
				spawnEnemy(eIndex+20);
				break;
			case 220:
				timer = 0;
				pattern++;
				break;
			}
		}
	}
	
	private void patternC(){
		if (level == 1){
			eIndex = 26;
			switch(timer){
			case 10:
				spawnEnemy(eIndex + 1);
				spawnEnemy(eIndex + 2);
				spawnEnemy(eIndex + 3);
				spawnEnemy(eIndex + 4);
				spawnEnemy(eIndex + 5);
				break;
			case 35:
				spawnEnemy(eIndex + 6);
				spawnEnemy(eIndex + 7);
				spawnEnemy(eIndex + 8);
				spawnEnemy(eIndex + 9);
				break;
			case 60:
				spawnEnemy(eIndex + 10);
				spawnEnemy(eIndex + 11);
				spawnEnemy(eIndex + 12);
				spawnEnemy(eIndex + 13);
				spawnEnemy(eIndex + 14);
				break;
			case 100:
				timer= 0;
				pattern++;
				break;
			}
		}
	}

	private void patternD(){
		if (level == 1){
			eIndex = 40;
			switch(timer){
			case 10:
				spawnEnemy(eIndex + 1);
				spawnEnemy(eIndex + 2);
				break;
			case 20:
				spawnEnemy(eIndex + 3);
				spawnEnemy(eIndex + 4);
				break;
			case 30:
				spawnEnemy(eIndex + 5);
				spawnEnemy(eIndex + 6);
				break;
			case 40:
				spawnEnemy(eIndex + 7);
				spawnEnemy(eIndex + 8);
				break;
			case 100:
				timer= 0;
				pattern++;
				break;
			}
		}
	}
	
	private void patternE(){
		if (level == 1){
			eIndex = 48;
			switch(timer){
			case 10:
				spawnEnemy(eIndex + 1);
				spawnEnemy(eIndex + 2);
				break;
			case 22:
				spawnEnemy(eIndex + 3);
				spawnEnemy(eIndex + 4);
				break;
			case 34:
				spawnEnemy(eIndex + 5);
				spawnEnemy(eIndex + 6);
				break;
			case 330:
				timer= 0;
				pattern++;
				break;
			}
		}
	}

	private void bossPattern(){
		if (level == 1){
			switch (timer){
			case 10: spawnBoss(1); break;
			}
		}
	}

	/**Create an enemy object and store it in the list of enemies*/
	private void createEnemy(double xPos, double yPos, double angle, int type){
		listOfEnemies[eIndex] = new NormalEnemy(Map.LEFT_BOUND + xPos, Map.TOP_BOUND + yPos, angle, type);
		eIndex++;
	}

	/**Spawn an enemy by putting in the newlySpawnedEnemies list so it can be added to the screen*/
	private void spawnEnemy(int index){
		newlySpawnedEnemies.add(listOfEnemies[index-1]);
	}

	/**Spawn an enemy by putting in the newlySpawnedBoss reference so it can be added to the screen*/
	private void spawnBoss(int index){
		newlySpawnedBoss = listOfBosses[index-1];
	}

	/**Moves the map background to simulates a scrolling effect*/
	public void moveBackground(){
		bg1y+=1;
		bg2y+=1;

		//Move images back to the top when when they go below the bottom of the screen
		if (bg1y >= Map.BOTTOM_BOUND)
			bg1y = -Map.BOTTOM_BOUND+1;

		if (bg2y >= Map.BOTTOM_BOUND)
			bg2y = -Map.BOTTOM_BOUND+1;
	}

	/**Draws the map background*/
	public void draw (Graphics g){
		g.setClip(0, 0, 900, 850);
		g.drawImage(bgImg, Map.LEFT_BOUND,bg1y, 650, 850, null);
		g.setClip(0, 0, 900, 850);
		g.drawImage(bgImg, Map.LEFT_BOUND,bg2y,  650, 850,null);
	}

	/**Return the list of enemies the map has just spawned*/
	public ArrayList<NormalEnemy> getNewlySpawnedEnemies() {
		return newlySpawnedEnemies;
	}

	/**Return the boss the map has just spawned*/
	public Boss getNewlySpawnedBoss() {
		return newlySpawnedBoss;
	}

	/**Return the player ship object*/
	public static PlayerShip getPlayer(){
		return player;
	}

	/**Set the player ship object*/
	public static void setPlayer(PlayerShip p){
		player = p;
	}
}
