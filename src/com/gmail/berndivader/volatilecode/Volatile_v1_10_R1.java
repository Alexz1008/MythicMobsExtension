package com.gmail.berndivader.volatilecode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.gmail.berndivader.NMS.NMSUtil;
import com.gmail.berndivader.mmcustomskills26.CustomSkillStuff;
import com.gmail.berndivader.mmcustomskills26.Main;

import io.lumine.xikage.mythicmobs.MythicMobs;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftLivingEntity;

import net.minecraft.server.v1_10_R1.EntityCreature;
import net.minecraft.server.v1_10_R1.EntityInsentient;
import net.minecraft.server.v1_10_R1.EntityLiving;
import net.minecraft.server.v1_10_R1.PathfinderGoal;
import net.minecraft.server.v1_10_R1.PathfinderGoalFleeSun;
import net.minecraft.server.v1_10_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_10_R1.PathfinderGoalSelector;

public class Volatile_v1_10_R1 
implements VolatileHandler {
	
	public Volatile_v1_10_R1() {
	}
	
	@Override
	public void aiPathfinderGoal(LivingEntity entity, String uGoal, LivingEntity target) {
		World w = entity.getWorld();
        EntityInsentient e = (EntityInsentient)((CraftLivingEntity)entity).getHandle();
        EntityLiving tE = null;
        if (target!=null) {
        	tE = (EntityLiving)((CraftLivingEntity)entity).getHandle();
        }
        Field goalsField;
        int i=0;
        String goal=uGoal;
        String data=null;
        String data1=null;
        String[] parse = uGoal.split(" ");
        if (parse[0].matches("[0-9]*")) {
        	i = Integer.parseInt(parse[0]);
        	if (parse.length>1) {
        		goal = parse[1];
        		if (parse.length>2) {
        			data = parse[2];
        		}
        		if (parse.length>3) {
        			data1 = parse[3];
        		}
        	}
        }
		try {
			goalsField = EntityInsentient.class.getDeclaredField("goalSelector");
	        goalsField.setAccessible(true);
	        PathfinderGoalSelector goals = (PathfinderGoalSelector)goalsField.get((Object)e);
	        switch (goal) {
	        case "rangedmelee": {
	            if (e instanceof EntityCreature) {
	            	float range = 2.0f;
	            	if (CustomSkillStuff.isNumeric(data)) {
	            		range = Float.parseFloat(data);
	            	}
	            	goals.a(i, (PathfinderGoal)new PathfinderGoalMeleeRangeAttack((EntityCreature)e, 1.0, true, range));
	            }
	        	break;
	        }
	        case "runfromsun": {
	        	if (e instanceof EntityCreature) {
	        		double speed = 1.0d;
	            	if (CustomSkillStuff.isNumeric(data)) {
	            		speed = Double.parseDouble(data);
	            	}
	            	goals.a(i, (PathfinderGoal)new PathfinderGoalFleeSun((EntityCreature)e, speed));
	        	}
	        	break;
	        }
	        case "followentity": {
	        	UUID uuid=null;
	        	if (e instanceof EntityCreature) {
	        		double speed = 1.0d;
	        		if (CustomSkillStuff.isNumeric(data)) {
	        			speed = Double.parseDouble(data);
	        		}
	        		if (data1!=null && (uuid = CustomSkillStuff.isUUID(data1))!=null) {
	        			Main.getPlugin().getNMSUtils();
						Entity ee = NMSUtil.getEntity(w, uuid);
	        			if (ee instanceof LivingEntity) {
	        		        tE = (EntityLiving)((CraftLivingEntity)(LivingEntity)ee).getHandle();
	        			}
	        		}
	        		if (tE!=null && tE.isAlive()) {
		            	goals.a(i, (PathfinderGoal)new PathfinderGoalFollowEntity(e,tE, speed));
	        		}
	        	}
	        	break;
	        }
	        default: {
	        	List<String>gList=new ArrayList<String>();
	        	gList.add(uGoal);
	            MythicMobs.inst().getVolatileCodeHandler().aiGoalSelectorHandler(entity, gList);
	        }}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
    
	public class PathfinderGoalMeleeRangeAttack extends PathfinderGoalMeleeAttack {
		protected float range;

		public PathfinderGoalMeleeRangeAttack(EntityCreature entityCreature, double d, boolean b, float range) {
			super(entityCreature, d, b);
			this.range=range;
		}

		@Override
		protected double a(EntityLiving entity) {
		    return (double)(this.b.width * this.range * this.b.width * this.range + entity.width);
		}
	}
	
    public class PathfinderGoalFollowEntity extends PathfinderGoal {
        private double speed;
        private EntityInsentient entity;
        private EntityLiving entity1;

        public PathfinderGoalFollowEntity(EntityInsentient e, EntityLiving e1, double s) {
            this.entity = e;
            this.entity1 = e1;
            this.speed = s;
        }
        
        public boolean a() {
            try {
            	if (this.entity1==null 
            			|| !this.entity1.isAlive() 
            			|| !this.entity1.getWorld().equals(this.entity.getWorld())) {
            		return false;
            	}
            	World world = this.entity1.getWorld().getWorld();
            	Location dLoc = new Location(world,this.entity1.locX,this.entity1.locY,this.entity1.locZ);
            	Location sLoc = new Location(world,this.entity.locX,this.entity.locY,this.entity.locZ);
            	if (sLoc.distanceSquared(dLoc)>1024.0) {
            		this.entity.teleportTo(dLoc, false);
            		return true;
            	}
                if (sLoc.distanceSquared(dLoc)>this.speed) {
                    this.entity.getNavigation().a(dLoc.getX(), dLoc.getY(), dLoc.getZ(), this.speed);
                    return true;
                }
                return false;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }
}