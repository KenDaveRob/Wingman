/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Wingman;

import java.awt.Image;
import javax.swing.JApplet;


public class AimingEnemy extends BasicEnemy {

    public AimingEnemy(JApplet workingApplet, Image img, int speed, int x, int y, int fireRate) {
        super(workingApplet, img, speed, x, y, fireRate);
        this.setScoreShootingDown(6);
    }

    
    public AimingEnemy( int x, int y, int speed, int fireRate) {
        super(x, y, speed, fireRate);
        this.setScoreShootingDown(6);
    }
    
    @Override
    public void tick(int w, int h, GameEvents gameEvents) throws NullPointerException {
        java.util.List<Bullet> newBullets = super.tick(gameEvents);
        
        if(newBullets != null) 
            ((Wingman)this.workingApplet).getBullets().addAll(newBullets);

        this.fireCounter++;
    }
    
    @Override
    java.util.List<Bullet> fire() 
    {
        if(this.fireCounter > this.fireRate)
        {
            double theta;
            double xStep;
            double yStep;
            int ySign;
            java.util.ArrayList<Bullet> newBullets = new java.util.ArrayList<Bullet>();

            this.fireCounter = 0;
            
            for(Player currentPlayer : ((Wingman)this.workingApplet).getPlayers())
            {                
                theta = Math.atan(((double)this.x-(double)currentPlayer.getX())/((double)this.y - (double)currentPlayer.getY()));
                xStep = Math.sin(theta)*this.bulletSpeed;
                yStep = Math.cos(theta)*this.bulletSpeed;
                ySign = 1;
            
                if(this.y > currentPlayer.getY()) ySign = -1;

                if(ySign == 1)
                    newBullets.add(new Bullet(((Wingman)this.workingApplet).getPlayers(), ((Wingman)this.workingApplet).getEnemies(), ((Wingman)this.workingApplet).gameEvents
                            ,this.x, this.y, (int)Math.round(xStep), (int)Math.round(yStep)+1, 1));
                
                else
                    newBullets.add(new Bullet(((Wingman)this.workingApplet).getPlayers(), ((Wingman)this.workingApplet).getEnemies(), ((Wingman)this.workingApplet).gameEvents
                            ,this.x, this.y, -(int)Math.round(xStep), -(int)Math.round(yStep), 1));
            }
            
            return newBullets;            
        }
        
        return null;
    }
    
}
