package exoatmos;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;


/**
 *	@author 		Curtis Phan
 *	@date 			December 28, 2016
 *
 *	Purpose: The player will control a spaceship that can shoot bullets. There will be enemies the player can shoot down that will try
 *					 to fire bullets at the player ship. The player can move, slow their movement, fire bullets, change fire mode and use bombs 
 *					 to clear the screen. They can also pickup power-ups to help them. The program spawns enemies and bosses at set times 
 *					depending on which map/level is being played. The object of the game is for the player to get to the end of the level without 
 *					losing all of their lives and beat the boss at the end.
 *
 *Version History:
 *
 *	******Version 1.1******
 *- Added functionality for additional levels/maps
 *- Added a new enemy pattern
 *- Tweaked some of the graphics
 *
 *	******Version 1.0******
 *- Completed game (first level)
 *
 *	Known Bugs: - Minimizing the applet window being using the minimize button speeds up the program drastically and
 *							  causes other bizarre things to happen. 
 *							- Occasional flickering when there are too many bullets on screen. Only slightly noticeable.
 *							- Level progression is dependent on time, so if bombs/cheat are abused at certain stages of the game eg patternE, there will be a long delay 
 *							  between the end of a pattern and the start of the next one where nothing happens.
 */
public class ShooterApplet extends Applet implements Runnable, KeyListener, MouseListener, MouseMotionListener{

	int timer;							//a timer used to determine how long the game has been running
	final int fireDelay = 5;		//the delay between firing bullets from the player ship

	//Objects for update method
	private Image dbImage;
	private Graphics dbg;

	//A random number generator
	Random rng;

	//booleans that become true when certain inputs from the user have been received
	boolean slowHeld;												//'Shift' key held, slows player movement
	boolean enterHeld;												//'Enter' key held, fires bullets
	boolean bombUsed;												//'X' key pressed, uses a bomb to clear the screen
	boolean[] directionHeld = new boolean[4];		//'W', 'A', 'S', or 'D' held, determines direction of player movement
	boolean[] mousedOver = new boolean[4];		//Mouse moved over certain parts of the screen, highlights buttons when they are moused over

	//booleans that determine which screen the program is on so the appropriate components and text can be placed on-screen
	boolean mainMenu = true;				//the main menu
	boolean mainGame = false;				//runs through the actual game
	boolean paused = false;					//pauses game and displays pause screen 
	boolean showInstructions = false;	//shows instructions
	boolean showCredits = false;					//shows credits
	boolean victory = false;	//indicate that the player has won; the trigger is in the despawnObjects() method, when the boss despawns
	boolean defeat = false;		//indicate that the player has lost; the trigger is in the collisionDetection() method, when the player loses all of their lives

	boolean cheat = false;		//enables cheat, player becomes invincible

	int currentLevel; 				//the current level/stage of the game 
	final int LAST_LEVEL=1;	//the final level in the game

	Map[] level = new Map[2];	//Map object being used for game progression (spawning enemies and bosses)
	Map currentMap;					//the current Map object; based on current level

	PlayerShip player;				//the player ship object
	Boss boss;							//the object for the boss of the current level

	ArrayList<PlayerBullet> pBulletsOnScreen = new ArrayList<PlayerBullet> ();		//a list of all of the player bullets objects on screen
	ArrayList<EnemyBullet> eBulletsOnScreen = new ArrayList<EnemyBullet> ();		//a list of all of the enemy bullets objects on screen
	ArrayList<NormalEnemy> eShipsOnScreen = new ArrayList<NormalEnemy> ();	//a list of all of the enemy ship objects on screen
	ArrayList<PowerUp> powerUpsOnScreen = new ArrayList<PowerUp> ();				//a list of all of the power-ups on screen

	Font titleFont = new Font ("Castellar", Font.PLAIN, 65);
	//font for all the text in the sidebar of the main game
	Font sidebarFontBig  = new Font("Stencil", Font.PLAIN, 35);	
	Font sidebarFontSmall = new Font("Stencil", Font.PLAIN, 22);
	//font for all the text in the buttons
	Font buttonFontBig = new Font ("Lucida Console", Font.PLAIN, 30);
	Font buttonFontSmall = new Font ("Lucida Console", Font.PLAIN, 18);
	Font keyCommandFont = new Font ("Lucida Console", Font.ITALIC, 15);		//italicized text displaying key command
	//displays messages and instructions
	Font messageFont = new Font ("Arial", Font.PLAIN, 16);							
	//font for the text next to boss health bar
	Font bossFont = new Font ("Perpetua", Font.BOLD, 35);		

	//color of the sidebar and side bar text
	Color sidebarColor = new Color (160, 162, 162);	
	Color sidebarTextColor = new Color (149, 0, 4);	
	//color for the various buttons
	Color buttonBGC1a = new Color(80, 80, 80, 200);			//non-highlighted borders
	Color buttonBGC1b = new Color(120, 120, 120, 200);	//highlighted borders
	Color buttonBGC2 = new Color(200, 200, 200, 150);		//button body
	//color for the unfilled health bar
	Color healthBarColor = new Color (100,100,100,150);


	public void init(){
		resize(900,850);

		//prepare images
		Database.prepareDatabase(this);


		//create player object and assign it to the map class
		player = new PlayerShip((Map.LEFT_BOUND + Map.RIGHT_BOUND)/2, Map.BOTTOM_BOUND -100);
		Map.setPlayer(player);
		//prepare maps
		level[0] = new Map(1);

		rng = new Random();
		//detect keyboard and mouse inputs
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void start(){
		// define a new thread
		Thread th = new Thread (this);
		// start this thread
		th.start ();
	}

	public void stop(){

	}

	/**Detects whenever one object on screen (ship/bullet/boss) collides with an object on the opposite side (player vs enemy or player vs boss)
	 *	Any decrements of a loop counter are used to account for the changes in an ArrayList's size and the indices of the objects in the ArrayLists
	 *	when removing an object from one, preventing inaccurate collision detection and IndexOutOfBoundsErrors
	 */
	private void collisionDetection (){
		/**	Run through each enemy type object to check if it collides or grazes with the player ship
		 *	Detect collision only when the player is not invulnerable to damage
		 *	Subtract from lives, make player invincible, reset location of player ship, and despawn enemy object (unless boss) if the player collides with the object
		 *	Add to graze count and indicate that enemy object has been grazed, if player ship grazes with the object and object has not already been grazed.
		 */
		if(player.isInvincible() == false){

			//enemy bullet
			for (int x = 0 ; x < eBulletsOnScreen.size() ; x++){
				if (player.collidesWith(eBulletsOnScreen.get(x))){
					player.processDeath();
					eBulletsOnScreen.remove(x);
					break;
				}
				else if (eBulletsOnScreen.get(x).getGrazed() == false && player.grazedBy(eBulletsOnScreen.get(x))){
					player.setGrazeCount(player.getGrazeCount() + 1);
					eBulletsOnScreen.get(x).setGrazed(true);
					player.setScore(player.getScore() + 200);
				}
			}

			//enemy ship
			for (int x = 0 ; x < eShipsOnScreen.size() ; x++){
				if (player.collidesWith(eShipsOnScreen.get(x))){
					player.processDeath();
					eShipsOnScreen.remove(x);
					break;
				}
				else if (eShipsOnScreen.get(x).getGrazed() == false && player.grazedBy(eShipsOnScreen.get(x))){
					player.setGrazeCount(player.getGrazeCount() + 1);
					eShipsOnScreen.get(x).setGrazed(true);
					player.setScore(player.getScore() + 200);
				}
			}

			//picks up power-up when player touches it
			for(int x= 0; x<powerUpsOnScreen.size();x++){
				if(player.grazedBy(powerUpsOnScreen.get(x))){
					player.pickUpPowerUp(powerUpsOnScreen.get(x));
					powerUpsOnScreen.remove(x);
				}
			}

			//boss
			//only check collision when boss is onscreen
			if (boss != null){
				if (player.collidesWith(boss))
					player.processDeath();
			}

			//TODO DEFEAT
			//Indicate that the player has lost when they have lost all of their lives
			if (player.getLives() <= 0)
				defeat = true;
		}


		//runs through each player bullet hitbox to check if it collides with an enemy ship/boss
		for (int x = 0 ; x < pBulletsOnScreen.size() ; x++){

			/**	Runs through each enemy ship hitbox to check it if it collides with the player bullet
			 *	Skips collision detection if enemy is spawning ie is offscreen
			 *	If enemy ship and player bullet collide, despawn player bullets and lower enemy ship's health
			 *	Add to kill count, increase score, adds any dropped Powerups and despawn enemy ship when enemy loses all of its health
			 */
			for (int y = 0 ; y < eShipsOnScreen.size() ; y++){
				if (eShipsOnScreen.get(y).isInSpawn())
					continue;
				if (eShipsOnScreen.get(y).collidesWith (pBulletsOnScreen.get(x))){
					eShipsOnScreen.get(y).setHealth(eShipsOnScreen.get(y).getHealth() - 1);
					player.setScore(player.getScore() + 10);
					pBulletsOnScreen.remove(x);
					x--;
					if (eShipsOnScreen.get(y).getHealth() <= 0){
						player.setKillCount(player.getKillCount() + 1);
						player.setScore(player.getScore() + eShipsOnScreen.get(y).getScoreValue());
						PowerUp tempP = eShipsOnScreen.get(y).dropPowerUp(rng.nextInt((int)(1/NormalEnemy.DROP_RATE)));
						if (tempP != null)
							powerUpsOnScreen.add(tempP);
						eShipsOnScreen.remove(y);
					}
					break;
				}
			}


			/**	Runs through each boss hitbox to check it if it collides with the player bullet
			 *	Skips collision detection if boss is offscreen, 
			 *	If boss and player bullet collide, despawn player bullets and lower boss' health unless boss is invincible
			 *	Add to kill count when boss loses all of its health
			 */
			if (boss != null && boss.isInSpawn() == false && boss.getHealth() > 0){
				if (boss.collidesWith (pBulletsOnScreen.get(x))){
					if (boss.isInvincible() == false){
						boss.setHealth(boss.getHealth() - 1);
						boss.healthCheck();
						player.setScore(player.getScore() + 10);
					}
					pBulletsOnScreen.remove(x);
					x--;

					//despawn boss and indicate that level is beat when boss loses all of its health
					if (boss.getHealth() <= 0){
						player.setKillCount(player.getKillCount() + 1);
						player.setScore(player.getScore() + boss.getScoreValue());
					}
				}
			}
		}
	}

	/**moves player ship based on input from the user*/
	private void playerMovement ()
	{
		double xSpeed, ySpeed;

		//horizontal movement
		if (directionHeld [0] == true)				//left
			xSpeed = -player.getSpeed();
		else if (directionHeld [1] == true)		//right
			xSpeed = player.getSpeed();
		else															//neither, stops horizontal movement
			xSpeed = 0;

		//vertical movement
		if (directionHeld [2] == true)				//up
			ySpeed = -player.getSpeed();
		else if (directionHeld [3] == true)		//down
			ySpeed = player.getSpeed();
		else															//neither, stops vertical movement
			ySpeed = 0;

		//halves player's speed if slow button is held down
		if (slowHeld == true){
			xSpeed/=2;
			ySpeed/=2;
		}

		//move the player object
		player.setXSpeed(xSpeed);
		player.setYSpeed(ySpeed);
		player.move();
	}

	/**clears the screen of bullets and damages enemies when bomb key is pressed as long as number of bombs is greater than 0*/
	private void useBomb(){
		if (player.getBombs() > 0){
			eBulletsOnScreen.clear();
			//decrease the bosses and enemies' health by 40
			for (int x =0; x<eShipsOnScreen.size(); x++){
				eShipsOnScreen.get(x).setHealth(eShipsOnScreen.get(x).getHealth() - 40);
				if (eShipsOnScreen.get(x).getHealth() <= 0){
					eShipsOnScreen.remove(x);
					x--;
				}
			}
			if (boss != null){
				boss.setHealth(boss.getHealth() - 40);
				boss.healthCheck();
				//despawn boss and indicate that level is beat when boss loses all of its health
				if (boss.getHealth() <= 0){
					player.setKillCount(player.getKillCount() + 1);
					player.setScore(player.getScore() + boss.getScoreValue());
				}
			}

			player.setBombs(player.getBombs()-1);
		}
		bombUsed = false;
	}

	/**takes in a CollidableObject and checks if it's onscreen or not based on the map boundaries*/
	private boolean checkOnScreen(CollidableObject obj){
		if (obj.getXPos() + obj.getWidth() < Map.LEFT_BOUND || obj.getXPos() > Map.RIGHT_BOUND || 
				obj.getYPos() + obj.getHeight()< Map.TOP_BOUND || obj.getYPos() > Map.BOTTOM_BOUND)
			return false;
		else
			return true;
	}

	/**	Despawns objects when they go off screen as long as they aren't in their spawning phase (ie are entering the screen offscreen)
	 *	Any decrements of a loop counter are used to account for the changes in an ArrayList's size and the indices of the objects in the ArrayLists
	 *	when removing an object from one, preventing inaccurate collision detection and IndexOutOfBoundsErrors
	 */
	private void despawnObjects (){
		//despawn player bullets
		for (int i = 0; i < pBulletsOnScreen.size(); i++){
			if (checkOnScreen (pBulletsOnScreen.get(i)) == false){
				pBulletsOnScreen.remove(i);
				i--;
			}
		}

		//despawn enemy bullets
		for (int i = 0; i < eBulletsOnScreen.size(); i++){
			if (checkOnScreen (eBulletsOnScreen.get(i)) == false){
				eBulletsOnScreen.remove(i);
				i--;
			}
		}

		//despawn enemy ships
		for (int i = 0; i < eShipsOnScreen.size(); i++){
			if (checkOnScreen (eShipsOnScreen.get(i)) == false && eShipsOnScreen.get(i).isInSpawn() == false){
				eShipsOnScreen.remove(i);
				i--;
			}
		}

		//despawn power ups
		for (int i = 0; i < powerUpsOnScreen.size(); i++){
			if (checkOnScreen (powerUpsOnScreen.get(i)) == false){
				powerUpsOnScreen.remove(i);
				i--;
			}
		}

		//TODO VICTORY
		//despawn boss when boss goes off screen while in its death phase and indicate that the player has won
		if (boss != null){
			if (checkOnScreen (boss) == false && boss.getPhase() == Boss.DEATH_PHASE){
				boss = null;
				victory = true;
			}
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Thread.currentThread ().setPriority(Thread.MIN_PRIORITY);
		while(true)
		{
			if(mainGame == true && paused == false && victory == false && defeat == false){
				timer++;

				//scroll the map background
				currentMap.moveBackground();

				//PLAYER ACTIONS
				playerMovement();

				//increment invincibility timer if player is invincible and cheat isn't enabled
				//used to allow player some time to recover from death
				if (player.isInvincible() && cheat == false)
					player.incrementInvTimer();

				//spawn bullets/used bombs when the appropriate keys are pressed
				if (enterHeld == true){
					//if the played has spawned a bullet, add the spawned bullet(s) to the screen
					if (player.spawnBullets() == true){
						for (int i = 0; i < player.getNewlySpawnedBullets().size(); i++)
							pBulletsOnScreen.add(player.getNewlySpawnedBullets().get(i));
					}
				}
				if (bombUsed == true)
					useBomb();

				//award player bonus lives/bombs for reaching certain scores
				player.scoreBonus();

				//ENEMY ACTIONS for every enemy ship on screen
				for (int x = 0; x < eShipsOnScreen.size(); x++){
					eShipsOnScreen.get(x).move();
					//if the enemy ship has spawned a bullet, add the spawned bullet(s) to the screen
					if (eShipsOnScreen.get(x).spawnBullets() == true){
						for (int i = 0; i < eShipsOnScreen.get(x).getNewlySpawnedBullets().size(); i++)
							eBulletsOnScreen.add(eShipsOnScreen.get(x).getNewlySpawnedBullets().get(i));
					}
					//indicate that enemy is out of spawn phase when enemy enters screen fully
					if (eShipsOnScreen.get(x).isInSpawn() && checkOnScreen (eShipsOnScreen.get(x)) == true)
						eShipsOnScreen.get(x).setInSpawn(false);
				}

				//BOSS ACTIONS if boss is onscreen
				if (boss != null){
					boss.move();
					//if the boss has spawned a bullet, add the spawned bullet(s) to the screen
					if (boss.spawnBullets() == true){
						for (int i = 0; i < boss.getNewlySpawnedBullets().size(); i++)
							eBulletsOnScreen.add(boss.getNewlySpawnedBullets().get(i));
					}
					//indicate that boss is out of spawn phase when boss enters screen fully
					if (boss.isInSpawn() && checkOnScreen (boss) == true)
						boss.setInSpawn(false);			
				}

				//MOVEMENT for every bullet and power-up on screen
				for (int x = 0; x < pBulletsOnScreen.size(); x++)			//player bullet
					pBulletsOnScreen.get(x).move();
				for (int x = 0; x < eBulletsOnScreen.size(); x++)			//enemy bullet
					eBulletsOnScreen.get(x).move();
				for (int x = 0; x < powerUpsOnScreen.size(); x++)			//power-up
					powerUpsOnScreen.get(x).move();

				//spawn enemies and bosses, and add them to the screen every 10th of a second
				if (timer%10 == 0){
					currentMap.run();
					for (int i = 0; i < currentMap.getNewlySpawnedEnemies().size(); i++)
						eShipsOnScreen.add(currentMap.getNewlySpawnedEnemies().get(i));
					if (boss == null){
						boss = currentMap.getNewlySpawnedBoss();
						//reset the timer when the boss is spawned since the timer needs to be 0 to do the gradually filling health bar effect
						if (boss != null)	
							timer = 0;
					}
					despawnObjects();
				}
				collisionDetection();
			}
			repaint ();

			try
			{
				// Stop thread for 10 milliseconds
				Thread.sleep (10);
			}
			catch (InterruptedException ex)
			{
				// do nothing
			}
			Thread.currentThread ().setPriority (Thread.MAX_PRIORITY);
		}
	}

	/** Update - Method, implements double buffering */
	public void update (Graphics g){

		// initialize buffer
		if (dbImage == null)
		{
			dbImage = createImage (this.getSize ().width, this.getSize ().height);
			dbg = dbImage.getGraphics ();
		}

		// clear screen in background
		dbg.setColor (getBackground ());
		dbg.fillRect (0, 0, this.getSize ().width, this.getSize ().height);

		// draw elements in background
		dbg.setColor (getForeground ());
		paint (dbg);

		// draw image on the screen
		g.drawImage (dbImage, 0, 0, this);

	}

	public void paint(Graphics g){
		//TODO paint


		/**MAIN MENU SCREEN*/
		if (mainMenu == true){
			g.drawImage(Database.getMenuBG(), 0, 0, 900, 850, null);

			int yPos;
			//draw option boxes and highlight them when moused over
			for (int x = 0; x < 4;x++){
				yPos = x*170+130;
				if (mousedOver[x] == false)
					g.setColor (buttonBGC1a);
				else if (mousedOver[x] == true)
					g.setColor (buttonBGC1b);
				g.fillRect (300, yPos, 300, 150);
				g.setColor (buttonBGC2);
				g.fillRect (310, yPos + 10, 280, 130);
			}
			g.setColor (Color.white);

			//draw title of game
			g.setFont (titleFont);
			g.drawString ("EXOATMOSPHERE", 130, 100);


			//draw button labels
			g.setColor(Color.black);
			g.setFont(buttonFontBig);
			g.drawString ("Start Game", 360, 200);
			g.drawString ("Instructions", 350, 380);
			g.drawString ("Cheat", 390, 550);
			g.drawString ("Credits", 390, 720);

			//display if cheat is on or off
			g.setFont (buttonFontSmall);
			if (cheat== true)
				g.drawString ("On", 490, 547);
			else
				g.drawString ("Off", 490, 547);

			//instructional text
			g.setFont (messageFont);
			g.drawString ("Become completely immune to damage", 313, 590);

			//italicized possible key commands for this screen
			g.setFont (keyCommandFont);
			g.drawString ("Press the 'space' key", 360, 220);
			g.drawString ("Press the 'Backspace' key", 340, 400);
			g.drawString ("Press the 'C' key", 380, 570);
			g.drawString ("Press the 'Esc' key", 360, 740);
		}


		/**MAIN GAME SCREEN*/
		else if (mainGame == true){
			currentMap.draw(g);

			//draw the various game objects
			for (int i = 0; i < pBulletsOnScreen.size(); i++){
				g.setClip(0, 0, 900, 850);
				pBulletsOnScreen.get(i).draw(g);
			}
			//draws the player every frame if player is not invincible
			//draws the player for 4 frames then doesn't draw the player for 4 frames if player is invincible
			if (timer%20<10 || !player.isInvincible()){
				g.setClip(0, 0, 900, 850);
				player.draw(g);
			}
			if (boss != null){
				g.setClip(0, 0, 900, 850);
				boss.draw(g);
			}
			for (int i = 0; i < eBulletsOnScreen.size(); i++){
				g.setClip(0, 0, 900, 850);
				eBulletsOnScreen.get(i).draw(g);
			}
			for (int i = 0; i < eShipsOnScreen.size(); i++){
				g.setClip(0, 0, 900, 850);
				eShipsOnScreen.get(i).draw(g);
			}
			for (int i = 0; i < powerUpsOnScreen.size(); i++){
				g.setClip(0, 0, 900, 850);
				powerUpsOnScreen.get(i).draw(g);
			}


			//draw sidebar
			g.setColor(sidebarColor);
			g.fillRect (0, 0, 250, 850);

			//draw boxes for the life and bomb icons
			g.setColor (Color.gray);
			g.fillRect (25, 25, 70, 70);
			g.fillRect (25, 125, 70, 70);
			g.setColor (Color.darkGray);
			g.fillRect (30, 30, 60, 60);
			g.fillRect (30, 130, 60, 60);

			//draw and scale the life and bomb icons
			g.drawImage (Database.getPlayerShip(), 40, 40, 40, 40, null);
			g.drawImage (Database.getBomb(), 40, 140, 40, 40, null);

			g.setFont(keyCommandFont);
			g.setColor(Color.black);

			g.drawString ("'P'   -   Pause Game", 30, 840);
			g.setFont(sidebarFontSmall);
			//multiplication symbol next to the life and bomb icons
			g.drawString ("x", 120, 70);
			g.drawString ("x", 120, 170);

			//displays player's stats
			g.drawString ("Graze Count:", 10, 650);
			g.drawString ("Enemies", 10, 720);
			g.drawString ("Destroyed:", 10, 740);
			g.drawString ("Score:", 10, 810);

			g.setColor(sidebarTextColor);
			g.drawString (Integer.toString(player.getGrazeCount()), 10, 680);
			g.drawString (Integer.toString(player.getKillCount()), 10, 770);
			g.drawString (Integer.toString(player.getScore()), 100, 810);

			//display number of lives and bombs remaining next to their icons
			g.setFont (sidebarFontBig);
			g.drawString (String.valueOf (player.getLives()), 150, 75);
			g.drawString (String.valueOf (player.getBombs()), 150, 175);

			//fills boss' health bar with cyan segments representing the boss' health
			if (boss !=null){
				//empty bar
				g.setColor (healthBarColor);
				g.fillRect (373, 13, 500, 34);
				//outline of  bar
				g.setColor (Color.red);
				g.drawRect (373, 13, 500, 34);

				g.setFont(bossFont);
				g.drawString("BOSS",270, 41);


				g.setColor (Color.cyan);
				//gradually fills bar up until boss is fully on screen, one segment for every 150 milliseconds
				if (boss.getPhase() == 0){
					for (int x = 0 ; x <  25 ; x++) {
						if (x > timer/15)
							break;
						g.fillRect (375 + x * 20, 15, 17, 30);
					}
				}
				//fills health bar up to boss' current health, one segment = 1/25 of boss' health
				else{
					for (int x = 0 ; x < Math.ceil((double) boss.getHealth() *25/ boss.getInitHealth()) ; x++)
						g.fillRect (375 + x * 20, 15, 17, 30);
				}
			}
		}


		/**PAUSE SCREEN*/
		if (paused == true){
			//background for pause menu
			g.setColor (Color.darkGray);
			g.fillRoundRect (330, 340, 490, 160, 15, 15);

			g.setColor (Color.lightGray);
			g.fillRect (340, 350, 470, 140);

			//draw option boxes
			int xPos;
			for (int x = 0; x < 3;x++){
				xPos = x*155+345;
				if (mousedOver[x] == false)
					g.setColor (buttonBGC1a);
				else if (mousedOver[x] == true)
					g.setColor (buttonBGC1b);
				g.fillRoundRect (xPos, 425, 150, 60, 15, 15);
				g.setColor (buttonBGC2);
				g.fillRect (xPos+5, 430, 140, 50);
			}


			g.setColor (Color.black);

			//large heading
			g.setFont (buttonFontBig);
			g.drawString ("Pause Menu", 485, 390);

			//button labels 
			g.setFont (buttonFontSmall);
			g.drawString ("Resume", 387, 450);
			g.drawString ("Instructions", 510, 450);
			g.drawString ("Main Menu", 681, 450);

			//italicized possible key commands for this screen
			g.setFont (keyCommandFont);
			g.drawString ("'p'", 407, 470);
			g.drawString ("'backspace'", 524, 470);
			g.drawString ("'esc'", 704, 470);
		}

		/**END SCREENS*/
		//draw victory screen
		if (victory == true){
			//draw textbox
			g.setColor (Color.darkGray);
			g.fillRoundRect (330, 340, 490, 160, 15, 15);

			g.setColor (Color.lightGray);
			g.fillRect (340, 350, 470, 140);

			if (mousedOver[0] == false)
				g.setColor (buttonBGC1a);
			else if (mousedOver[0] == true)
				g.setColor (buttonBGC1b);
			g.fillRoundRect (500, 425, 150, 60, 15, 15);
			g.setColor (buttonBGC2);
			g.fillRect (505, 430, 140, 50);

			g.setColor (Color.black);
			g.setFont (buttonFontBig);

			//display appropriate message based on the level completed
			if (currentLevel == LAST_LEVEL){
				g.drawString ("You Beat the Game!", 410, 380);
				g.setFont (buttonFontSmall);
				g.drawString ("Main Menu", 525, 450);
			}
			else {
				g.drawString ("You Beat the Level!", 410, 380);
				g.setFont (buttonFontSmall);
				g.drawString ("Next Level", 520, 450);
			}

			g.setFont (messageFont);
			g.drawString ("Your Score was: " + player.getScore(), 505, 410);

			//italicized possible key commands for this screen
			g.setFont (keyCommandFont);
			g.drawString ("'space'", 544, 470);
		}

		//draw defeat screen
		if (defeat == true){
			//draw textbox
			g.setColor (Color.darkGray);
			g.fillRoundRect (330, 340, 490, 160, 15, 15);

			g.setColor (Color.lightGray);
			g.fillRect (340, 350, 470, 140);

			int xPos;
			for (int x = 0; x < 2;x++){
				xPos = x*170+420;
				if (mousedOver[x] == false)
					g.setColor (buttonBGC1a);
				else if (mousedOver[x] == true)
					g.setColor (buttonBGC1b);
				g.fillRoundRect (xPos, 425, 150, 60, 15, 15);
				g.setColor (buttonBGC2);
				g.fillRect (xPos+5, 430, 140, 50);
			}
			g.setColor (Color.black);

			g.setFont (buttonFontBig);
			g.drawString ("Continue?", 495, 380);

			g.setFont (buttonFontSmall);
			g.drawString ("Yes", 480, 450);
			g.drawString ("No", 655, 450);

			g.setFont (messageFont);
			g.drawString ("Your Score was: " + player.getScore(), 505, 410);

			//italicized possible key commands from this screen
			g.setFont (keyCommandFont);
			g.drawString ("'space'", 464, 470);
			g.drawString ("'backspace'", 617, 470);
		}

		/**INSTRUCTIONS SCREEN*/
		if (showInstructions == true){
			//draw textbox
			g.setColor (Color.darkGray);
			g.fillRoundRect (145, 140, 610, 580, 15, 15);
			g.setColor (Color.lightGray);
			g.fillRect (155, 150, 590, 560);

			//print text
			g.setColor (Color.black);

			//large headings
			g.setFont (buttonFontBig);
			g.drawString ("Instructions", 345, 180);
			g.drawString ("Controls", 375, 320);
			g.drawString ("Tips", 410, 510);

			//small text
			g.setFont (messageFont);
			g.drawString ("You are on a mission to defend the Earth from the alien invaders. Make your", 210, 210);
			g.drawString ("way through the level, dodging enemy fire and shooting down as many ships as", 180, 230);
			g.drawString ("possible. Make good use of the power-ups enemies drop to help you on your", 180, 250);
			g.drawString ("mission. Defeat the boss at the end to beat the level. Beat every level to beat the", 180, 270);
			g.drawString ("game.", 180, 290);
			g.drawString ("'W'   -   Move Up", 220, 360);
			g.drawString ("'S'   -   Move Down", 220, 400);
			g.drawString ("'A'   -   Move Left", 220, 440);
			g.drawString ("'D'   -   Move Right", 220, 480);
			g.drawString ("'Enter'   -   Fire Bullets", 520, 360);
			g.drawString ("'Shift'   -   Slow Movement", 520, 400);
			g.drawString ("'Ctrl'   -   Change Fire Mode", 520, 440);
			g.drawString ("'X'   -   Use Bomb", 520, 480);
			g.drawString ("- Hold the 'Shift' key down to slow your ship down for more precise movement", 165, 535);
			g.drawString ("- Use the bomb with the 'X' key to clear screen of enemies and bullets if you're", 165, 565);
			g.drawString ("  in trouble.", 165, 585);
			g.drawString ("- You only lose a life when a bullet or enemy hits the red circle in the middle of your", 165, 615);
			g.drawString ("  ship. Getting hit anywhere else on your ship means you were \"grazed\" adding to", 165, 635);
			g.drawString ("  the graze count and your score.", 165, 655);

			//italicized possible key commands for this screen
			g.setFont (keyCommandFont);
			g.drawString ("Press the 'Backspace' key to return", 300, 680);
		}

		/**CREDITS SCREEN*/
		if (showCredits == true){
			//draw textbox
			g.setColor (Color.darkGray);
			g.fillRoundRect (145, 140, 610, 580, 15, 15);
			g.setColor (Color.lightGray);
			g.fillRect (155, 150, 590, 560);

			//print text
			g.setColor (Color.black);

			//large headings
			g.setFont (buttonFontBig);
			g.drawString ("Programming", 355, 180);
			g.drawString ("Art", 420, 320);

			//TODO
			//smaller text
			g.setFont (buttonFontSmall);
			g.drawString ("Made by Curtis Phan", 350, 230);
			g.drawString ("Bullets and Power-ups made by Curtis Phan", 235, 360);
			g.drawString ("Other graphics taken from various sources", 235, 390);

			//italicized links
			g.setFont (keyCommandFont);

			//italicized possible key commands from this screen
			g.drawString ("Press the 'Esc' key to return", 330, 680);
		}
	}


	/**Reset game and return to the main menu*/
	private void returnToMain(){
		mainMenu = true;
		mainGame = false;
		paused = false;
		showInstructions = false;
		showCredits = false;
		victory = false;
		defeat = false;
		//remove and reset all game objects and current map
		pBulletsOnScreen.clear();
		eBulletsOnScreen.clear();
		eShipsOnScreen.clear();
		boss = null;
		player.reset();
		currentMap.reset();
		currentMap = null;
		for (int x = 0; x<mousedOver.length;x++)
			mousedOver[x] = false;
	}

	//start the game from level 1
	private void startGame(){
		mainMenu = false;
		mainGame = true;
		showInstructions = false;
		showCredits = false;
		currentLevel = 1;
		currentMap = level[currentLevel - 1];
		for (int x = 0; x<mousedOver.length;x++)
			mousedOver[x] = false;
	}
	@Override
	/**Performs certain actions based on key commands*/
	public void keyPressed(KeyEvent evt) {
		// TODO Auto-generated method stub

		//take in different key commands based on what screen the program is on
		if (mainGame == true){											//MAIN GAME
			if (evt.getKeyCode() == KeyEvent.VK_A)					//move player left
				directionHeld [0] = true;

			if (evt.getKeyCode() == KeyEvent.VK_D)					//move player right	
				directionHeld [1] = true;

			if (evt.getKeyCode() == KeyEvent.VK_W)				//move player up
				directionHeld [2] = true;

			if (evt.getKeyCode() == KeyEvent.VK_S)					//move player down
				directionHeld [3] = true;


			if (evt.getKeyCode() == KeyEvent.VK_ENTER)		//fire bullets 
				enterHeld = true;

			if (evt.getKeyCode() == KeyEvent.VK_SHIFT)			//slow player movement
				slowHeld = true;

			if (evt.getKeyCode() == KeyEvent.VK_CONTROL)	//change fire mode
				player.setSpreadFire(!player.getSpreadFire());
		}
	}

	@Override
	/**Performs certain actions based on key commands*/
	public void keyReleased(KeyEvent evt) {
		// TODO Auto-generated method stub

		// registers certain keys only on release so  it only registers the key once
		// if they were in the keyPressed method, holding down a key would cause it to register
		// repeatedly while it's held down causing, for example, rapid pausing and unpausing of the game

		//take in different key commands based on what screen the program is on
		if (mainMenu == true){															//MAIN MENU
			if (evt.getKeyCode() == KeyEvent.VK_SPACE)						//start game
				startGame();
			if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE)			//show/exit show instructions
				showInstructions = !showInstructions;
			if (evt.getKeyCode() == KeyEvent.VK_C){							//toggle cheat
				cheat = !cheat;
				player.setInvincible(cheat);
			}
			if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)			//show/exit show instructions
				showCredits = !showCredits;
		}

		if (mainGame == true){															//MAIN GAME
			if (evt.getKeyCode() == KeyEvent.VK_A)								//Stop moving left
				directionHeld [0] = false;

			if (evt.getKeyCode() == KeyEvent.VK_D)								//Stop moving right
				directionHeld [1] = false;

			if (evt.getKeyCode() == KeyEvent.VK_W)							//Stop moving up
				directionHeld [2] = false;

			if (evt.getKeyCode() == KeyEvent.VK_S)								//Stop moving down
				directionHeld [3] = false;


			if (evt.getKeyCode() == KeyEvent.VK_ENTER){					//Stop firing bullets
				enterHeld = false;
				player.resetFireTimer();
			}

			if (evt.getKeyCode() == KeyEvent.VK_SHIFT)						//Stop slowing movement
				slowHeld = false;

			if (evt.getKeyCode() == KeyEvent.VK_X)								//Use bomb
				bombUsed = true;

			if (evt.getKeyCode() == KeyEvent.VK_P && victory == false && defeat == false){							//pause/unpause game
				paused = !paused;
				showInstructions = false;
			}


			if (paused == true ){																//PAUSE SCREEN
				if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)					//return to main menu
					returnToMain();
				if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE){		//show/exit show instructions
					for (int x = 0; x<mousedOver.length;x++)
						mousedOver[x] = false;
					showInstructions = !showInstructions;
				}
			}

			else if (victory == true){														//VICTORY SCREEN
				if (evt.getKeyCode() == KeyEvent.VK_SPACE){
					for (int x = 0; x<mousedOver.length;x++)
						mousedOver[x] = false;
					if (currentLevel == LAST_LEVEL)										//return to main menu
						returnToMain();
					else{																					//go to next level
						victory = false;
						currentLevel++;
						currentMap = level[currentLevel - 1];
					}
				}
			}
			else if (defeat == true){															//DEFEAT SCREEN
				if (evt.getKeyCode() == KeyEvent.VK_SPACE){						//continue with the level
					for (int x = 0; x<mousedOver.length;x++)
						mousedOver[x] = false;
					defeat = false;
					player.reset();
				}
				else if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE){	//return to main menu
					returnToMain();
				}
			}

		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	/**Performs certain actions based on mouse clicks in certain areas*/
	public void mousePressed(MouseEvent me) {
		// TODO Auto-generated method stub
		//detect key presses in different areas based on what screen the program is on
		if (showInstructions == true)		//click anywhere any time the instructions screen is up to exit instructions
			showInstructions = false;

		//MAIN MENU
		else if (mainMenu == true){
			if (me.getX() >= 300 && me.getX() <= 600 && me.getY() >= 130 && me.getY() <= 280) 			//start game when button is pressed
				startGame();
			else if (me.getX() >= 300 && me.getX() <= 600 && me.getY() >= 300 && me.getY() <= 450)		//show instructions when button is pressed
				showInstructions = true;
			else if (me.getX() >= 300 && me.getX() <= 600 && me.getY() >= 470 && me.getY() <= 620){		//toggle cheat when button is pressed
				cheat = !cheat;
				player.setInvincible(cheat);
			}
			else if (me.getX() >= 300 && me.getX() <= 600 && me.getY() >= 640 && me.getY() <= 790)		//show credits when button is pressed
				showCredits=!showCredits;

		}

		//PAUSE SCREEN
		else if (paused == true){
			if (me.getX() >= 345 && me.getX() <= 495 && me.getY() >= 425 && me.getY() <= 485){			//resume game when button is pressed
				paused = false;
				showInstructions = false;
			}
			else if (me.getX() >= 500 && me.getX() <= 650 && me.getY() >= 425 && me.getY() <= 485)		//show instructions when button is pressed
				showInstructions = true;
			else if (me.getX() >= 655 && me.getX() <= 805 && me.getY() >= 425 && me.getY() <= 485)		//return to main menu when button is pressed
				returnToMain();
		}	

		//VICTORY SCREEN
		else if (victory == true){														
			if (me.getX() >= 500 && me.getX() <= 650 && me.getY() >= 425 && me.getY() <= 485){
				for (int x = 0; x<mousedOver.length;x++)
					mousedOver[x] = false;
				if (currentLevel == LAST_LEVEL)										//return to main menu
					returnToMain();
				else{																					//go to next level
					victory = false;
					currentLevel++;
					currentMap = level[currentLevel - 1];
				}
			}
		}

		//DEFEAT SCREEN
		else if (defeat == true){															
			if (me.getX() >= 420 && me.getX() <= 570 && me.getY() >= 425 && me.getY() <= 485){						//continue with the level
				for (int x = 0; x<mousedOver.length;x++)
					mousedOver[x] = false;
				defeat = false;
				player.reset();
			}
			else if (me.getX() >= 590 && me.getX() <= 740 && me.getY() >= 425 && me.getY() <= 485){				//return to main menu
				returnToMain();
			}
		}

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	/**highlight buttons when mouse is over them*/
	public void mouseMoved(MouseEvent me) {
		// TODO Auto-generated method stub
		//highlight buttons on MAIN MENU
		if (mainMenu == true){
			int yPos;
			//determine position of button and whether it's highlighted or not
			for (int x = 0; x<4;x++){
				yPos = x*170 +130;
				if (me.getX() >= 300 && me.getX() <= 600 && me.getY() >= yPos && me.getY() <= yPos+150) 
					mousedOver[x] = true;
				else 
					mousedOver[x] = false;
			}
		}

		//highlight buttons on PAUSE SCREEN
		else if (paused == true){
			int xPos;
			//determine position of button and whether it's highlighted or not
			for (int x = 0; x < 3;x++){
				xPos = x*155+345;
				if (me.getX() >= xPos && me.getX() <= xPos + 150 && me.getY() >= 425 && me.getY() <= 485) 
					mousedOver[x] = true;
				else 
					mousedOver[x] = false;
			}
		}

		//highlight buttons on VICTORY SCREEN
		else if(victory == true){
			//determine position of button and whether it's highlighted or not
			if (me.getX() >= 500 && me.getX() <= 650 && me.getY() >= 425 && me.getY() <= 485)
				mousedOver[0] = true;
			else
				mousedOver[0] = false;
		}

		//highlight buttons on DEFEAT SCREEN
		else if (defeat == true){
			int xPos;
			//determine position of button and whether it's highlighted or not
			for (int x = 0; x < 2; x++){
				xPos = x*170+420;
				if (me.getX() >= xPos && me.getX() <= xPos + 150 && me.getY() >= 425 && me.getY() <= 485) 
					mousedOver[x] = true;
				else 
					mousedOver[x] = false;
			}
		}
	}
}
