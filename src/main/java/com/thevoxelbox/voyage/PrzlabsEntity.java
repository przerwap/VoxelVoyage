package com.thevoxelbox.voyage;

import org.bukkit.entity.Player;

public abstract interface PrzlabsEntity
{
  public abstract void rightClick(Player paramPlayer, int paramInt);
  
  public abstract void setCrystal(com.thevoxelbox.voyage.PrzlabsCrystal paramPrzlabsCrystal, int paramInt);
  
  public abstract int getEntID();
}


/* Location:              C:\intellij\VoxelVoyage\VoxelVoyage.jar!\com\thevoxelbox\voyage\PrzlabsEntity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */