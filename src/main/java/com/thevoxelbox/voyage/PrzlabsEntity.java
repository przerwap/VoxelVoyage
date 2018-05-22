package com.thevoxelbox.voyage;

import com.thevoxelbox.voyage.entities.Crystal;
import org.bukkit.entity.Player;

public abstract interface PrzlabsEntity
{
  public abstract void rightClick(Player paramPlayer, int paramInt);
  
  public abstract void setCrystal(Crystal paramPrzlabsCrystal, int paramInt);
  
  public abstract int getEntID();
}