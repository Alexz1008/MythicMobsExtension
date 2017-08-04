package com.gmail.berndivader.mmcustomskills26;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import io.lumine.xikage.mythicmobs.skills.INoTargetSkill;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.SkillString;

public class ParsedStance extends SkillMechanic
implements
ITargetedEntitySkill,
INoTargetSkill {
	
	protected String stance;
	protected MobManager mobmanager = Main.getPlugin().getMobManager();

	public ParsedStance(String skill, MythicLineConfig mlc) {
		super(skill, mlc);
		String s = mlc.getString(new String[]{"stance","s"});
		if (s.startsWith("\"") && s.endsWith("\"")) s = s.substring(1, s.length()-1);
		s = SkillString.parseMessageSpecialChars(s);
	}

	@Override
	public boolean cast(SkillMetadata data) {
		castAtEntity(data, data.getCaster().getEntity());
		return true;
	}

	@Override
	public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (target!=null 
				&& mobmanager.isActiveMob(target)) {
			ActiveMob am = mobmanager.getMythicMobInstance(target);
			am.setStance(SkillString.parseMobVariables(this.stance, data.getCaster(), target, data.getTrigger()));
		}
		return true;
	}
	
}
