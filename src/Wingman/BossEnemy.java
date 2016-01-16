/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Wingman;

import java.awt.Image;
import java.util.Observable;
import java.util.Random;
import javax.swing.JApplet;

/**
 *
 * @author Kenneth
 */
public class BossEnemy extends AimingEnemy
{
    private int motionCounter;
    private int motionCycleRate;
    private int motionType;
    private int health;
    private Random generator;
    private Player targetPlayer;
    private int xCenter;
    private int yCenter;
    
    public BossEnemy(JApplet workingApplet, Image img, int speed, int x, int y, int fireRate, int health) {
        super(workingApplet, img, speed, x, y, fireRate);
        this.health = health;
        this.motionCounter = 0;
        this.motionCycleRate = 15;
        motionType = 0;
        this.generator = new Random(1234567);
        this.scoreShootingDown = 25;
    }

    
    public BossEnemy( int x, int y, int speed, int fireRate, int health) 
    {
        super(x, y, speed, fireRate);
        this.health = health;
        this.motionCounter = 0;
        this.motionCycleRate = 15;
        motionType = 0;
        this.generator = new Random(1234567);
        this.scoreShootingDown = 25;
    }
    

    public void setCenter()
    {
        int xCenter = this.workingApplet.getWidth()/2;
        int yCenter = this.workingApplet.getHeight()/2;
    }
    

    protected void damageFromCollision() 
    {
        health -= ((Wingman)this.workingApplet).damageFromRamming/4;
        if(health <= 0)
        {
            this.show = false;
            ((Wingman)this.workingApplet).score += this.scoreShootingDown;
            ((Wingman)this.workingApplet).gameOver = true;
            ((Wingman)this.workingApplet).gameOverMessage = "You Won!";
        }
    }
    
    @Override
    public void update(Observable o, Object arg) {
        GameEvents ge = (GameEvents) arg;
        if (ge.type == 4) {
            if (this == (BasicEnemy) ge.event) {
                if (!Bullet.class.isInstance(ge.cause) || ge.cause == null) {
                    System.out.println("Error: inside player.update(), gameEvent.cause does not exist or is not a bullet");
                } else {
                    if (boom == 0) {
                        health -= Bullet.bulletStrengthArray.get(1);
                        if (health <= 0) {
                            this.show = false;
                            ((Wingman) this.workingApplet).score += this.scoreShootingDown;
                            ((Wingman)this.workingApplet).gameOver = true;
                            ((Wingman)this.workingApplet).gameOverMessage = "You Won!";
                        }
                        this.boomTime = 5 * 7 * 1 + 1;
                        ((Wingman) this.workingApplet).getExplosions().add(new Explosion(this.workingApplet, this.x, this.y, 2, 5, 7, 1));
                        ((Wingman) this.workingApplet).playSound("Resources/snd_explosion2.wav");
                        ((Wingman) this.workingApplet).score += this.scoreShootingDown;


                    }
                    boom = 1;
                }
            }
        }
    }

    @Override
    void move() 
    {
        
        //Randomly cycles between targets/centering every motionCycleRate frames
        if(this.motionCounter % motionCycleRate == 0) 
        {
            motionType &= 1;
            int playerIndex = Math.abs(generator.nextInt() % 2);
            targetPlayer = ((Wingman)this.workingApplet).getPlayers().get(playerIndex);
        }
        
        //Move toward targeted player
        if(motionType == 0)
        {
            double theta = Math.atan(((double) this.x - (double) targetPlayer.getX()) / ((double) this.y - (double) targetPlayer.getY()));
            double xStep = Math.sin(theta) * this.speed;
            double yStep = Math.cos(theta) * this.speed;
            int ySign = 1;

            if(this.y > targetPlayer.getY()) ySign = -1;

            if(ySign == 1)
            {
                x += (int)Math.round(xStep);
                y += (int)Math.round(yStep)+1;
            }
            else
            {
                x += -(int)Math.round(xStep);
                y += -(int)Math.round(yStep);
            }
        }
        
        //quickly move toward the center of the screen
        else if(motionType == 1)
        {
            if(x < this.xCenter) x += 2*speed;
            
            if(x > this.xCenter) x -= 2*speed;
            
            if(y < this.yCenter) y += 2*speed;
            
            if(y > this.yCenter) y -= 2*speed;
        }
        
        this.motionCounter++;
    }
    
    
    
}
