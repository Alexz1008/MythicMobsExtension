package com.gmail.berndivader.mythicmobsext.conditions;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.SkillString;
import io.lumine.xikage.mythicmobs.skills.SkillTrigger;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityComparisonCondition;

@ExternalAnnotation(name="parsedstance,pstance",author="BerndiVader")
public class ParsedStanceCondition
extends
AbstractCustomCondition
implements
IEntityComparisonCondition {
	private String stance;
	private boolean compareToSelf;

	public ParsedStanceCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		this.stance=mlc.getString(new String[]{"stance","s"});
		this.compareToSelf=mlc.getBoolean(new String[]{"compareself","cs"},false);
		if (this.stance!=null&&(this.stance.startsWith("\"")&&this.stance.endsWith("\""))) {
			this.stance=this.stance.substring(1,this.stance.length()-1);
		}
		this.stance=SkillString.parseMessageSpecialChars(this.stance);
	}

	@Override
	public boolean check(AbstractEntity caster, AbstractEntity ae) {
		ActiveMob am=Utils.mobmanager.getMythicMobInstance(caster);
		SkillMetadata data=new SkillMetadata(SkillTrigger.API,am,ae);
		String stance=Utils.parseMobVariables(this.stance,data,caster,ae,null);
		ActiveMob target=this.compareToSelf?am:Utils.mobmanager.getMythicMobInstance(ae);
		return target.getStance().contains(stance);
	}

}
