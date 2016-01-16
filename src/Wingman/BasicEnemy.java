/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Wingman;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import javax.swing.JApplet;

/**
 *
 * @author Kenneth
 */



public class BasicEnemy extends Enemy
{
    protected int speed;
    protected int boom, boomTime, count;
    private Random gen;
    protected int fireRate, fireCounter, bulletSpeed;
    
    BasicEnemy(JApplet workingApplet, Image img, int speed, Random gen) 
    {
        this.workingApplet = workingApplet;
        this.img = img;
        this.x = Math.abs(gen.nextInt() % (600 - 30));
        this.y = -100;
        this.speed = speed;
        this.gen = gen;
        this.show = true;
        this.bulletSpeed = 2;
        this.scoreShootingDown = 2;
        sizeX = img.getWidth(null);
        sizeY = img.getHeight(null);
        boom = 0;
    }

    public BasicEnemy(JApplet workingApplet, Image img, int speed, int x, int y, int fireRate) {
        this.workingApplet = workingApplet;
        this.img = img;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.show = true;
        this.fireRate = fireRate;
        this.bulletSpeed = 2;
        this.scoreShootingDown = 2;
        boom = 0;
        sizeX = img.getWidth(null);
        sizeY = img.getHeight(null);
    }

    public BasicEnemy(int x, int y, int speed, int fireRate) 
    {
        this.x = x;
        this.y = y;
        this.speed = 1;
        this.fireRate = fireRate;
        this.bulletSpeed = 2;
        this.scoreShootingDown = 2;
        boom = 0;
        this.show = true;//was false, for some reason?
    }

    
    
    

    public int getFireRate() {
        return fireRate;
    }

    public void setFireRate(int fireRate) {
        this.fireRate = fireRate;
    }

    public int getBulletSpeed() {
        return bulletSpeed;
    }
    
    

    public void setBulletSpeed(int bulletSpeed) {
        this.bulletSpeed = bulletSpeed;
    }
    
    public void tick(int w, int h, GameEvents gameEvents) throws NullPointerException {
        java.util.List<Bullet> newBullets = super.tick(gameEvents);
        
        if(newBullets != null) ((Wingman)this.workingApplet).getBullets().add(newBullets.get(0));

        this.fireCounter++;
    }


    public void draw(Graphics g, ImageObserver obs) {
        if (show) {
            g.drawImage(img, x, y, obs);
        }
        
            if(count == boomTime){
                boom = 0;
                count = 0;
            } else count++;
            //this.obs = obs; 
    }
    
    public boolean collision(int x, int y, int w, int h) {
        if ((y + h > this.y) && (y < this.y + img.getHeight(null))) { // very simple test for showing an idea -- this only checks y forwarding direction
            if ((x + w > this.x) && (x < this.x + img.getWidth(null))) {
                return true;
            }
        }
        return false;
    }
    

    @Override
    public void update(Observable o, Object arg) {
        GameEvents ge = (GameEvents) arg;
        if (ge.type == 4) {
            if (this == (BasicEnemy) ge.event) 
            {
                if(boom == 0) 
                    {
                        this.show = false;
                        this.boomTime = 5*7*1  + 1; 
                        ((Wingman)this.workingApplet).getExplosions().add(new Explosion(this.workingApplet, this.x, this.y, 2, 5, 7, 1));
                        ((Wingman)this.workingApplet).playSound("Resources/snd_explosion2.wav");
                        ((Wingman)this.workingApplet).score += this.scoreShootingDown;
                    }
                 boom = 1;
            }
        }
    }

    @Override
    void move() {
        y += speed;
    }

    @Override
    java.util.List<Bullet> fire() 
    {
        if(this.fireCounter > this.fireRate)
        {
            this.fireCounter = 0;
            java.util.ArrayList<Bullet> newBullets = new java.util.ArrayList<Bullet>();
            newBullets.add(new Bullet(((Wingman)this.workingApplet).getPlayers(), ((Wingman)this.workingApplet).getEnemies(), ((Wingman)this.workingApplet).gameEvents
                    ,this.x, this.y, 0, this.bulletSpeed, 1));
                    
            return newBullets;
        }
        
        return null;
    }
}

