/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Wingman;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JApplet;

/**
 *
 * @author Kenneth
 */
public class SpawnSequence 
{
    private java.util.SortedSet<SpawnEvent> timeLine;
    private BufferedReader scenerioSource;
    private JApplet workingApplet; 

    public SpawnSequence(JApplet workingApplet) {
        this.timeLine = new java.util.TreeSet<SpawnEvent>();
        this.workingApplet = workingApplet;
    }
    
    public SpawnEvent getSpawnEvent(int timeIndex)
    {
        for(SpawnEvent spawnIndex : timeLine)
        {
            if(spawnIndex.getTime() == timeIndex)
            {
                timeLine.remove(spawnIndex);
                return spawnIndex;
            }
            else if(spawnIndex.getTime() > timeIndex)
                break;
        }
        
        return null;        
    }
    
    
    public void loadSequence(Object scenerioFile) throws Exception
    {
        String currentLine;
        List<String> parts;
        SpawnEvent nextEvent;
        LinkedList<Spawnable> spawnList;
        
        try
        {
            if(File.class.isInstance(scenerioFile))
                scenerioSource = new BufferedReader(new FileReader((File)scenerioFile));
            
            else if(scenerioFile.getClass() == URL.class)
                scenerioSource = new BufferedReader(new InputStreamReader(((URL)scenerioFile).openStream()));
            
            else
                throw new Exception("Error: Scenario File is neither a URL or a local File.");
            
            //scenerioSource = new BufferedReader(new FileReader((File)scenerioFile));

            while((currentLine = scenerioSource.readLine()) != null)
            {
                //Blank lines get ignored
                if(!currentLine.equals(""))
                {
                    nextEvent = new SpawnEvent();
                    spawnList = new LinkedList<Spawnable>();
                    //colon breaks frameTime and spawnList ex (time:item1,point;item2,point
                    parts = Arrays.asList(currentLine.split(":"));



                    nextEvent.setTime(Integer.parseInt(parts.get(0)));

                    parts = Arrays.asList(parts.get(1).split(";"));

                    for(String enemyInformation : parts)
                    {
                        String spawnType = enemyInformation.split(",")[0];

                        if(spawnType.equals("AimingEnemy"))
                            spawnList.add(new AimingEnemy(Integer.parseInt(enemyInformation.split(",")[1]),-32, 1, 50));
                        else if(spawnType.equals("BasicEnemy"))
                            spawnList.add(new BasicEnemy( Integer.parseInt(enemyInformation.split(",")[1]),-32, 1, 50));
                        //public BossEnemy( int x, int y, int speed, int fireRate, int health)
                        else if(spawnType.equals("BossEnemy"))
                            spawnList.add(new BossEnemy( Integer.parseInt(enemyInformation.split(",")[1]),-32, 1, 40, 250));
                        //public Powerup(Image img, int x, int y, JApplet workingApplet)
                        else if(spawnType.equals("Powerup"))
                        {

                            Powerup newPowerup = new Powerup(((Wingman)this.workingApplet).getSprite("Resources/powerup.png"),
                                    Integer.parseInt(enemyInformation.split(",")[1]), -32, 3, 10, this.workingApplet);
                            spawnList.add(newPowerup);
                        }
                        else
                        {
                            throw new Exception("ERROR: inside SpawnSequence.loadSequence(File scenerioFile), Enemy type \""+spawnType+"\" is unknown.");
                        }
                    }
                    nextEvent.setSpawnList(spawnList);                
                    this.timeLine.add(nextEvent);
                }
            }
        }
        
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
        finally
        {
            try
            {
                scenerioSource.close();
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
