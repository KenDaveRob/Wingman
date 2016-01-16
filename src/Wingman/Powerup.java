/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Wingman;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import javax.swing.JApplet;

/**
 *
 * @author Kenneth
 */
public class Powerup implements Spawnable
{
    protected Image img;
    protected int x, y, sizeX, sizeY;
    protected int powerupDuration;
    protected int speed;
    protected JApplet workingApplet;
    protected boolean show;

    public Powerup(Image img, int x, int y, int speed, int powerupDuration, JApplet workingApplet) {
        this.img = img;
        this.sizeX = img.getWidth(null);
        this.sizeY = img.getHeight(null);
        this.show = true;
        this.powerupDuration = powerupDuration;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.workingApplet = workingApplet;
    }


    public void draw(Graphics g, ImageObserver obs) {
        if (show) {
            g.drawImage(img, x, y, obs);
        }
    }

    public int getPowerupDuration() {
        return powerupDuration;
    }
    

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isShown() {
        return show;
    }

    @Override
    public void tick(int w, int h, GameEvents gameEvents) throws NullPointerException {
        int i = 1;
        y += this.speed;


        for (Player currentPlayer : ((Wingman) this.workingApplet).getPlayers()) {
            if (currentPlayer.collision(x, y, img.getWidth(null), img.getHeight(null))) {
                show = false;
                gameEvents.setValue("Powerup " + i, this);
                break;
            }
            i++;
        }
    }
 
    
    
    
    
}
