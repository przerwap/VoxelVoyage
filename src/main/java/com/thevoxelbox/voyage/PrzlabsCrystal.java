/*     */ package com.thevoxelbox.voyage;
/*     */ 
/*     */ import net.minecraft.server.DamageSource;
/*     */ import net.minecraft.server.EntityEnderCrystal;
/*     */ import net.minecraft.server.EntityPlayer;
/*     */ import net.minecraft.server.MathHelper;
/*     */ import net.minecraft.server.NBTTagCompound;
/*     */ import net.minecraft.server.NetServerHandler;
/*     */ import net.minecraft.server.Packet34EntityTeleport;
/*     */ import net.minecraft.server.World;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PrzlabsCrystal
/*     */   extends EntityEnderCrystal
/*     */ {
/*     */   private Player focus;
/*     */   private BeziPoint bezi;
/*     */   private int index;
/*     */   private boolean controlled;
/*     */   private PrzlabsEntity owner;
/*  31 */   private boolean decorative = false;
/*  32 */   private int message = 0;
/*  33 */   private String[] messages = { "Hello, I'm decorative!", "Pretty cool huh?", "Bet you can't float like this!", "I'm cooler than you." };
/*     */   
/*     */   public PrzlabsCrystal(World world) {
/*  36 */     super(world);
/*     */   }
/*     */   
/*     */   public PrzlabsCrystal(World world, boolean decor) {
/*  40 */     super(world);
/*  41 */     this.decorative = decor;
/*     */   }
/*     */   
/*     */   public PrzlabsCrystal(World world, BeziPoint point, int count, PrzlabsEntity mother) {
/*  45 */     super(world);
/*  46 */     this.bezi = point;
/*  47 */     this.index = count;
/*  48 */     this.owner = mother;
/*  49 */     setPosition(this.bezi.x, this.bezi.y, this.bezi.z);
/*     */   }
/*     */   
/*     */   public void w_()
/*     */   {
/*  54 */     if (this.decorative) {
/*  55 */       if (++this.message == this.messages.length) {
/*  56 */         this.message = 0;
/*     */       }
/*  58 */       return;
/*     */     }
/*  60 */     if (this.bezi == null) {
/*  61 */       die();
/*  62 */       return;
/*     */     }
/*  64 */     if (this.focus == null) return;
/*     */     int i;
/*     */     int j;
/*  67 */     int k; if (this.controlled) {
/*  68 */       double distance = 1.75D;
/*  69 */       Location player_loc = this.focus.getLocation();
/*  70 */       double rot_x = (player_loc.getYaw() + 90.0F) % 360.0F;
/*  71 */       double rot_y = player_loc.getPitch() * -1.0F;
/*  72 */       double rot_ycos = Math.cos(Math.toRadians(rot_y));
/*  73 */       double rot_ysin = Math.sin(Math.toRadians(rot_y));
/*  74 */       double rot_xcos = Math.cos(Math.toRadians(rot_x));
/*  75 */       double rot_xsin = Math.sin(Math.toRadians(rot_x));
/*     */       
/*  77 */       double h_length = distance * rot_ycos;
/*  78 */       double y_offset = distance * rot_ysin;
/*  79 */       double x_offset = h_length * rot_xcos;
/*  80 */       double z_offset = h_length * rot_xsin;
/*     */       
/*  82 */       double target_x = x_offset + player_loc.getX();
/*  83 */       double target_y = y_offset + player_loc.getY();
/*  84 */       double target_z = z_offset + player_loc.getZ();
/*  85 */       this.bezi.x = target_x;
/*  86 */       this.bezi.y = target_y;
/*  87 */       this.bezi.z = target_z;
/*  88 */       setPosition(target_x, target_y, target_z);
/*  89 */       i = MathHelper.floor(target_x * 32.0D);
/*  90 */       j = MathHelper.floor(target_y * 32.0D);
/*  91 */       k = MathHelper.floor(target_z * 32.0D);
/*  92 */       for (Object o : this.world.players) {
/*  93 */         ((EntityPlayer)o).netServerHandler.sendPacket(new Packet34EntityTeleport(this.id, i, j, k, (byte)0, (byte)0));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean damageEntity(DamageSource ds, int i)
/*     */   {
/* 106 */     return false;
/*     */   }
/*     */   
/*     */   public void rightClick(Player user) {
/* 110 */     if (this.decorative) {
/* 111 */       user.sendMessage(ChatColor.AQUA + "[" + ChatColor.LIGHT_PURPLE + "CRYSTAL" + ChatColor.AQUA + "] " + ChatColor.GREEN + this.messages[this.message]);
/*     */     }
/* 113 */     if ((this.focus == null) || (this.focus.getUniqueId() != user.getUniqueId())) {
/* 114 */       this.focus = user;
/*     */     }
/* 116 */     this.controlled = (!this.controlled);
/*     */   }
/*     */   
/*     */   public void a(NBTTagCompound in)
/*     */   {
/* 121 */     byte byt = in.getByte("Decor");
/* 122 */     if (byt == 1) {
/* 123 */       this.decorative = true;
/* 124 */     } else if (byt == 2) {
/* 125 */       this.decorative = false;
/*     */     } else {
/* 127 */       this.decorative = false;
/*     */     }
/*     */   }
/*     */   
/*     */   public void b(NBTTagCompound out)
/*     */   {
/* 133 */     out.setByte("Decor", (byte)(this.decorative ? 1 : 2));
/*     */   }
/*     */   
/*     */   public int getAirTicks()
/*     */   {
/* 138 */     return this.decorative ? 12345 : 12346;
/*     */   }
/*     */   
/*     */   public void b(net.minecraft.server.Entity entity, int index)
/*     */   {
/* 143 */     if ((entity instanceof EntityPlayer)) {
/* 144 */       EntityPlayer play = (EntityPlayer)entity;
/* 145 */       org.bukkit.entity.Entity bplay = play.getBukkitEntity();
/* 146 */       if ((bplay instanceof Player)) {
/* 147 */         rightClick((Player)bplay);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\intellij\VoxelVoyage\VoxelVoyage.jar!\com\thevoxelbox\voyage\PrzlabsCrystal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */