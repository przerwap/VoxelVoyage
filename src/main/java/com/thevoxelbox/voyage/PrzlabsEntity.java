package com.thevoxelbox.voyage;

import org.bukkit.entity.Player;

public abstract interface PrzlabsEntity
{
  public abstract void rightClick(Player paramPlayer, int paramInt);
  
  public abstract void setCrystal(com.thevoxelbox.voyage.PrzlabsCrystal paramPrzlabsCrystal, int paramInt);
  
  public abstract int getEntID();
}