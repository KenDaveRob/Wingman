/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Wingman;

import Wingman.Wingman.*;
import java.util.LinkedList;

/**
 *
 * @author Kenneth
 */
public class SpawnEvent implements Comparable
{
    private LinkedList<Spawnable> spawnList;
    private int time;

    public SpawnEvent(LinkedList<Spawnable> spawnList, int time) {
        this.spawnList = spawnList;
        this.time = time;
    }

    public SpawnEvent() {
    }
    
    
    
    public LinkedList<Spawnable> getSpawnList() {
        return spawnList;
    }

    public int getTime() {
        return time;
    }

    public void setSpawnList(LinkedList<Spawnable> enemys) {
        this.spawnList = enemys;
    }

    public void setTime(int time) {
        this.time = time;
    }
    
    
    
    public int compareTo(SpawnEvent otherEvent)
    {
        return time - otherEvent.getTime();
    }

    @Override
    public int compareTo(Object o) 
    {
        if(o.getClass() != this.getClass())
            throw new ClassCastException("Attempt to compare a SpawnEvent to a \""+o.getClass().toString()+"\" is invalid."); 
        
        return compareTo((SpawnEvent)o);
    }
    
}
