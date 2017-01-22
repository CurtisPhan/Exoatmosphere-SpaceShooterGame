package exoatmos;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.io.BufferedInputStream;
import java.io.IOException;




public abstract class Database {


	private static Image backgroundImage;										//Image of the map background
	private static Image playerShipImage;											//Image of the player ship
	private static Image bombImage;													//Image of the bomb icon
	private static Image menuBGImage;												//Image of the main menu background
	private static Image playerBulletImage;										//Image of the player bullets
	private static Image enemyBulletImage;										//Image of the enemy bullets
	
	private static Image[] enemyImage = new Image[5];					//Images of the various enemy ships
	private static Image[] bossImage = new Image[1];						//Images of the various bosses
	private static Image[] powerUpImage = new Image[3];				//Images of the various power-ups

	//File paths for images
	private static final String BACKGROUND_PATH = "Assets/background.png";	
	private static final String PLAYER_PATH ="Assets/playerShip.png";
	private static final String BOMB_PATH ="Assets/bomb.png";
	private static final String MENU_PATH ="Assets/menu.png";
	private static final String PBULLET_PATH ="Assets/playerBullet.png";
	private static final String EBULLET_PATH ="Assets/enemyBullet.png";

	private static final String[] ENEMY_PATH = new String[enemyImage.length];
	private static final String[] BOSS_PATH = new String[bossImage.length];
	private static final String[] POWERUP_PATH = new String[powerUpImage.length];

	/**Load every image file into the program*/
	public static void prepareDatabase(Component comp) {
		// Now, it can actually take some time to load the image, and
		// it could fail (image not found, etc).  The following checks for
		// all that.
		MediaTracker tracker = new MediaTracker(comp);
		backgroundImage = loadImage(BACKGROUND_PATH, comp, tracker);
		playerShipImage = loadImage(PLAYER_PATH, comp, tracker);
		bombImage = loadImage(BOMB_PATH, comp, tracker);
		menuBGImage = loadImage(MENU_PATH, comp, tracker);
		playerBulletImage = loadImage(PBULLET_PATH, comp, tracker);
		enemyBulletImage = loadImage(EBULLET_PATH, comp, tracker);

		//Load every enemy image
		for (int x = 0; x< enemyImage.length; x++){
			ENEMY_PATH[x] ="Assets/enemyShip" + (x+1) + ".png";
			enemyImage[x] = loadImage(ENEMY_PATH[x], comp, tracker);
		}
		//load every boss image
		for (int x = 0; x< bossImage.length; x++){
			BOSS_PATH[x] ="Assets/boss" + (x+1) + ".png";
			bossImage[x] = loadImage(BOSS_PATH[x], comp, tracker);
		}
		//load every power-up image
		for (int x = 0; x< powerUpImage.length; x++){
			POWERUP_PATH[x] ="Assets/powerup" + (x+1) + ".png";
			powerUpImage[x] = loadImage(POWERUP_PATH[x], comp, tracker);
		}
		// Wait until all the images are loaded.  This can throw an
		// InterruptedException although it's not likely, so we ignore
		// it if it occurs.
		try
		{
			tracker.waitForAll ();
		}
		catch (InterruptedException e)
		{
		}
		// If there were any errors loading the image, then abort the
		// program with a message.
		if (tracker.isErrorAny ())
		{
			System.out.println("Couldn't load");
			return;
		}
	}
	
	/**Loads an image into the applet and adds it to the tracker*/
	private static Image loadImage(String imgPath, Component comp, MediaTracker tracker){
		Image img;
		BufferedInputStream imgStream = new BufferedInputStream(Database.class.getResourceAsStream("/"+imgPath));
		{
			try {
				img =  javax.imageio.ImageIO.read(imgStream);
			} catch (IOException e) {
				img = null;
			}
			comp.prepareImage (img, comp);
			tracker.addImage (img, 0);
			return img;
		}
	}
	
	
	/**Return the image of the map background*/
	public static Image getBackground(){
		return backgroundImage;
	}

	/**Return the image of the player ship*/
	public static Image getPlayerShip(){
		return playerShipImage;
	}

	/**Return the image of the bomb icon*/
	public static Image getBomb(){
		return bombImage;
	}

	/**Return the image of the main menu background*/
	public static Image getMenuBG(){
		return menuBGImage;
	}

	/**Return the image of an player bullet*/
	public static Image getPlayerBullet(){
		return playerBulletImage;
	}

	/**Return the image of an enemy bullet*/
	public static Image getEnemyBullet(){
		return enemyBulletImage;
	}

	/**Return the image of the specified enemy ship*/
	public static Image getEnemy(int type){
		return enemyImage[type];
	}

	/**Return the image of the specified boss*/
	public static Image getBoss(int type){
		return bossImage[type];
	}
	
	/**Return the image of the specified boss*/
	public static Image getPowerUp(int type){
		return powerUpImage[type];
	}

}
