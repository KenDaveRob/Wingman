/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Wingman;

/**
 *
 * @author Kenneth
 */
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;



/**
 *
 * @author Kenneth
 */
public class Wingman extends JApplet implements Runnable {

    private static final Wingman demo = new Wingman();
    private Thread thread; 
   
    private int x = 0, move = 0;
    private BufferedImage bimg;
    private Random generator = new Random(1234567);
    
    private ArrayList<Player> players;
    private java.util.List<Enemy> enemies;
    private java.util.List<Powerup> powerups;
    private java.util.List<Bullet> bullets;
    private java.util.List<Explosion> explosions;
    private int frameCounter;
    private static SpawnSequence scenario;
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;
    private static int initalNumberOfLives = 3;
    private Object scenarioFile = Wingman.class.getResource("Resources/myScenario.txt"); 
    
    int speed = 1, score = 0;
    int damageFromRamming;
    
    Island I1, I2, I3;
    
    Player playerOne;
    Player playerTwo;
    Image lifeImage;
    Image gameOverImage;
    
    Soundtrack soundtrack;
    Thread soundthread;
    
    GameEvents gameEvents;

    boolean gameOver;
    String gameOverMessage = "You Lost!";
    ImageObserver observer;
    
    javax.swing.JMenu fileMenu;
    javax.swing.JMenu optionsMenu;
    javax.swing.JMenuBar menuBar;
    javax.swing.JMenuItem pauseMenuItem;
    javax.swing.JMenuItem killSoundtrackMenuItem;
    javax.swing.JMenuItem openScenarioMenuItem;
    JFrame mainFrame;

    public void init() {
        setFocusable(true);
        setBackground(Color.white);
        Image myPlane, island1, island2, island3, enemy;
        
        frameCounter = 0;
        damageFromRamming = 10;
        
        lifeImage = getSprite("Resources/life.png");   
        gameOverImage = getSprite("Resources/gameOver.png");
        island1 = getSprite("Resources/island1.png");
        island2 = getSprite("Resources/island2.png");
        island3 = getSprite("Resources/island3.png");
        myPlane = getSprite("Resources/myplane_1.png");
        Bullet.largeImg = getSprite("Resources/bullet.png");
        Bullet.smallImg = getSprite("Resources/enemybullet1.png");
        Bullet.largeLeftImg = getSprite("Resources/bulletLeft.png");
        Bullet.largeRightImg = getSprite("Resources/bulletRight.png");
        Bullet.bulletStrengthArray = new ArrayList<Integer>();
        Bullet.bulletStrengthArray.add(0, 5);
        Bullet.bulletStrengthArray.add(1, 10);
 
        gameOver = false;
        observer = this;
        
        players = new ArrayList<Player>();
        I1 = new Island(island1, 100, 100, speed, generator);
        I2 = new Island(island2, 200, 400, speed, generator);
        I3 = new Island(island3, 300, 200, speed, generator);
        enemies = new LinkedList<Enemy>();
        powerups = new LinkedList<Powerup>();
        bullets = new LinkedList<Bullet>();
        explosions = new LinkedList<Explosion>();
        scenario = new SpawnSequence(this);
        try {
            scenario.loadSequence(this.scenarioFile);
        } catch (Exception ex) {
            System.out.println("Exception found inside" + this.getClass().getName() + ".init()...");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        
        
        KeyControl key = new KeyControl();
        addKeyListener(key);
        
        gameEvents = new GameEvents();
        
        playerOne = new Player(myPlane, 1, 300, 260, 10, initalNumberOfLives, new MovementKeys(KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT), '\n', this);
        playerTwo = new Player(myPlane, 2, 200, 260, 10, initalNumberOfLives, new MovementKeys(KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D), ' ', this);
        playerOne.setFireRefractoryPeriod(12);
        playerTwo.setFireRefractoryPeriod(12);
        players.add(playerOne);
        players.add(playerTwo);
        
        gameEvents.addObserver(playerOne);
        gameEvents.addObserver(playerTwo);
        
        soundtrack = new Soundtrack();
        soundthread = new Thread(soundtrack);
        soundthread.start();
        
        
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        pauseMenuItem = new javax.swing.JMenuItem();
        killSoundtrackMenuItem = new javax.swing.JMenuItem();
        openScenarioMenuItem = new javax.swing.JMenuItem();
        optionsMenu = new javax.swing.JMenu();

        fileMenu.setText("File");
        pauseMenuItem.setText("Pause");
        killSoundtrackMenuItem.setText("Please god stop the sound!");
        openScenarioMenuItem.setText("Open Scenario");
        
        pauseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseActionPerformed(evt);
            }
        });
        killSoundtrackMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                killSoundActionPerformed(evt);
            }
        });
        openScenarioMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openActionPerformed(evt);
            }
        });
                
        fileMenu.add(pauseMenuItem);
        fileMenu.add(killSoundtrackMenuItem);

        optionsMenu.add(openScenarioMenuItem);
        menuBar.add(fileMenu);

        optionsMenu.setText("Options");
        menuBar.add(optionsMenu);

        mainFrame = new JFrame("Scrolling Shooter");
        
        mainFrame.setJMenuBar(menuBar);
        mainFrame.addWindowListener(new WindowAdapter() {});
        mainFrame.getContentPane().add("Center", demo);
        mainFrame.pack();
        mainFrame.setSize(new Dimension(WIDTH, HEIGHT));
        mainFrame.setVisible(true);
        mainFrame.setResizable(false);
        
    }
   

    public java.util.List<Enemy> getEnemies() {
        return enemies;
    }

    public java.util.List<Bullet> getBullets() {
        return bullets;
    }

    public java.util.List<Explosion> getExplosions() {
        return explosions;
    }

    public GameEvents getGameEvents() {
        return gameEvents;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
    
    public class Island {

        Image img;
        int x, y, speed;
        Random gen;

        Island(Image img, int x, int y, int speed, Random gen) {
            this.img = img;
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.gen = gen;
        }

        public void update(int w, int h) {
            y += speed;
            if (y >= h) {
                y = -100;
                x = Math.abs(gen.nextInt() % (w - 30));
            }
        }

        public void draw(Graphics g, ImageObserver obs) {
            g.drawImage(img, x, y, obs);
        }
    }
    

    public class KeyControl extends KeyAdapter {

        public void keyPressed(KeyEvent e) {
            gameEvents.setValue(e);
        }

        //Using Released may make the plane more responsive
        public void keyReleased(KeyEvent e) {
            gameEvents.setValueReleased(e);
        }
    }
    
    public Image getSprite(String name) {
        URL url = Wingman.class.getResource(name);
        Image img = getToolkit().getImage(url);
        try {
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(img, 0);
            tracker.waitForID(0);
        } catch (Exception e) {
        }
        return img;
    }

    // generates a new color with the specified hue
    public void drawBackGroundWithTileImage(int w, int h, Graphics2D g2) {
        Image sea;
        sea = getSprite("Resources/water.png");
        int TileWidth = sea.getWidth(this);
        int TileHeight = sea.getHeight(this);

        int NumberX = (int) (w / TileWidth);
        int NumberY = (int) (h / TileHeight);

        Image Buffer = createImage(NumberX * TileWidth, NumberY * TileHeight);
        //Graphics BufferG = Buffer.getGraphics();


        for (int i = -1; i <= NumberY; i++) {
            for (int j = 0; j <= NumberX; j++) {
                g2.drawImage(sea, j * TileWidth, i * TileHeight + (move % TileHeight), TileWidth, TileHeight, this);
            }
        }
        move += speed;
    }

    public void drawDemo(int w, int h, Graphics2D g2) {

        if (!gameOver) {
            drawBackGroundWithTileImage(w, h, g2);
            I1.update(w, h);
            I1.draw(g2, this);

            I2.update(w, h);
            I2.draw(g2, this);

            I3.update(w, h);
            I3.draw(g2, this);

            if(!playerOne.isDestroyed)
            {
                playerOne.tick();
                playerOne.draw(g2, this);
            }
            
            if(!playerTwo.isDestroyed)
            {
                playerTwo.tick();
                playerTwo.draw(g2, this);
            }
            
            updateAndDrawBullets(w, h, g2);
            clearOldBullets();
            
            generateEnemies();
            updateAndDrawEnemies(w, h, g2);
            clearOldEnemies();
            
            updateAndDrawPowerups(w, h, g2);
            
            processExplosions(g2);
            
            drawHealthAndLives(g2);
            
            this.frameCounter++;
            if(playerOne.isDestroyed && playerTwo.isDestroyed) this.gameOver = true;
        }
        else  
        {
            drawBackGroundWithTileImage(w, h, g2);

            g2.drawImage(this.gameOverImage, (this.getWidth()/2)-(this.gameOverImage.getWidth(null)/2), (this.getHeight()/2)-(this.gameOverImage.getHeight(null)/2), this);
            g2.setColor(Color.BLACK);
            g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, 50));
            g2.drawString("Score: "+this.score+" "+this.gameOverMessage, (this.getWidth()/2)-225, (this.getHeight()/2)-50);
        }
  
    }
    
    private void drawHealthAndLives(Graphics2D g2)
    {
        int playerOneHealth = this.playerOne.getHealth();
        int playerTwoHealth = this.playerTwo.getHealth();
        int playerOneLives = this.playerOne.getLives();
        int playerTwoLives = this.playerTwo.getLives();
        //int index;
        g2.setColor(Color.green);
        g2.fillRect(Wingman.WIDTH-playerOneHealth, 0, playerOneHealth, 10);
        g2.fillRect(0, 0, playerTwoHealth, 10);
        
        g2.drawString("Player Two", 0, 15+lifeImage.getHeight(null));
        g2.drawString("Joint Score: "+this.score, 0, 30+lifeImage.getHeight(null));
        g2.drawString("Player One", Wingman.WIDTH-68, 15+lifeImage.getHeight(null));
        
        for(int i = 1; i < playerOneLives+1; i++)
            g2.drawImage(lifeImage, Wingman.WIDTH-i*lifeImage.getWidth(null), 10, this);
        
        
        
        for(int i = 0; i < playerTwoLives; i++)
            g2.drawImage(lifeImage, i*lifeImage.getWidth(null), 10, this);
    }
    public void addExplosion(Explosion newExplosion)
    {
        this.explosions.add(newExplosion);
    }
    
    public void addBullet(int x, int y, int xStep, int yStep, int strength)
    {
        this.bullets.add(new Bullet(players, enemies, gameEvents, x, y, xStep, yStep, strength));
    }
    
    public void addBullet(int x, int y, int xStep, int yStep, int strength, int angle)
    {
        this.bullets.add(new Bullet(players, enemies, gameEvents, x, y, xStep, yStep, strength, angle));
    }
    
    private void processExplosions(Graphics2D g2)
    {
        Explosion explosionIndex;
        Iterator<Explosion> enemiesIterator = this.explosions.iterator();
        while(enemiesIterator.hasNext())
        {
            explosionIndex = enemiesIterator.next();
            if(!explosionIndex.show)
                enemiesIterator.remove();
            
            else
            {
                explosionIndex.incrementAnimation();
                explosionIndex.draw(g2, this);
            }
        }
    }
    
    private void updateAndDrawEnemies(int w, int h, Graphics2D g2)
    {
        for(Enemy enemyIndex : this.enemies)
        {
            enemyIndex.tick(w, h, this.gameEvents);
            enemyIndex.draw(g2, this);
        }
    }
    
    private void updateAndDrawPowerups(int w, int h, Graphics2D g2)
    {
        for(Powerup powerupIndex : this.powerups)
        {
            powerupIndex.tick(w, h, this.gameEvents);
            powerupIndex.draw(g2, this);
        }
    }
    
    private void updateAndDrawBullets(int w, int h, Graphics2D g2)
    {
        for(Bullet bulletIndex : this.bullets)
        {
            bulletIndex.tick(w, h);
            bulletIndex.draw(g2, this);
        }
    }
    
    
    
    private void generateEnemies()
    {
        SpawnEvent currentSpawnEvent = this.scenario.getSpawnEvent(this.frameCounter);
        
        if(currentSpawnEvent != null)
        {
            for(Spawnable newSpawn : currentSpawnEvent.getSpawnList())
            {
                //This is a very poor way to load the images,
                //however I had a great deal of trouble trying to load the images into there class instances inside the SpawnSequence.loadSequence() method
                if(Enemy.class.isInstance(newSpawn))
                {
                    Enemy newEnemy = (Enemy)newSpawn;
                    if(newEnemy.getClass() == BasicEnemy.class)
                        newEnemy.setImageAndApplet(this, getSprite("Resources/enemy1_1.png"));
                    else if(newEnemy.getClass() == AimingEnemy.class)
                        newEnemy.setImageAndApplet(this, getSprite("Resources/enemy2_1.png"));
                    else if(newEnemy.getClass() == BossEnemy.class)
                    {
                        newEnemy.setImageAndApplet(this, getSprite("Resources/myboss_1.png"));
                        ((BossEnemy)newEnemy).setCenter();
                    }
                    gameEvents.addObserver(newEnemy);
                    this.enemies.add(newEnemy);
                }
                else if(Powerup.class.isInstance(newSpawn))
                {
                    this.powerups.add((Powerup)newSpawn);
                }
            }
            
        }
    }

    private void clearOldEnemies()
    {
        Iterator<Enemy> enemiesIterator = this.enemies.iterator();
        while(enemiesIterator.hasNext())
        {
            BasicEnemy currentEnemy = (BasicEnemy)enemiesIterator.next();
            if(currentEnemy.getY() > Wingman.HEIGHT)
                enemiesIterator.remove();
            
            else if(!currentEnemy.isShown())
                enemiesIterator.remove();
        }
    }
    
    private void clearOldBullets()
    {
        Iterator<Bullet> bulletIterator = this.bullets.iterator();
        while(bulletIterator.hasNext())
        {
            Bullet currentBullet = bulletIterator.next();
            if(currentBullet.getY() > Wingman.HEIGHT || currentBullet.getY() < 0 ||
                    currentBullet.getX() > Wingman.WIDTH || currentBullet.getX() < 0)
                bulletIterator.remove();
            
            else if(!currentBullet.isShown())
                bulletIterator.remove();
        }
    }

    public Graphics2D createGraphics2D(int w, int h) {
        Graphics2D g2 = null;
        if (bimg == null || bimg.getWidth() != w || bimg.getHeight() != h) {
            bimg = (BufferedImage) createImage(w, h);
        }
        g2 = bimg.createGraphics();
        g2.setBackground(getBackground());
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.clearRect(0, 0, w, h);
        return g2;
    }

    public void paint(Graphics g) {
        Dimension d = getSize();
        Graphics2D g2 = createGraphics2D(d.width, d.height);
        drawDemo(d.width, d.height, g2);
        g2.dispose();
        g.drawImage(bimg, 0, 0, this);
    }

    public void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    public void run() {
    	
        Thread me = Thread.currentThread();
        while (thread == me) {
            repaint();
          
          try {
                thread.sleep(25);
            } catch (InterruptedException e) {
                break;
            }
            
        }
    	    	
    }
    
    
    
    /* Usefull java sound resource websites
     * http://www.anyexample.com/programming/java/java_play_wav_sound_file.xml
     * http://stackoverflow.com/questions/8425481/play-wav-file-from-jar-as-resource-using-java
     * see http://www.cs.cmu.edu/~illah/CLASSDOCS/javasound.pdf
     */
    void playSound(String filename)
    {
        try {
            AudioPlayer.player.start(new AudioStream(getClass().getResourceAsStream(filename)));
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }

    public static void main(String argv[]) {
        
        demo.init();     
        demo.start();
    }
    
    private void pauseActionPerformed(java.awt.event.ActionEvent evt) {                                     
        if(demo.pauseMenuItem.getText().equals("Pause"))
        {
            demo.thread = null;
            demo.soundtrack.setContinueLooping(false);
            demo.pauseMenuItem.setText("Restart");
        }
        
        else
        {
            soundtrack = new Soundtrack();
            soundthread = new Thread(soundtrack);
            soundthread.start();
            demo.start();
            demo.pauseMenuItem.setText("Pause");
        }
    }      
    
    private void killSoundActionPerformed(java.awt.event.ActionEvent evt) {   
        if(demo.killSoundtrackMenuItem.getText().equals("Please god stop the sound!"))
        {
            demo.soundtrack.setContinueLooping(false);
            demo.killSoundtrackMenuItem.setText("Please turn on that wonderfull noise, It's positively orgasmic.");
        }
        
        else
        {
            soundtrack = new Soundtrack();
            soundthread = new Thread(soundtrack);
            soundthread.start();
            demo.killSoundtrackMenuItem.setText("Please god stop the sound!");
        }     
    }   
    
    private void openActionPerformed(java.awt.event.ActionEvent evt) {
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            scenarioFile = fc.getSelectedFile();
            demo.thread = null;
            demo.soundtrack.setContinueLooping(false);

            demo.init();
            demo.start();

        }
    }

    /**
     * This method is called from within the init() method to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
