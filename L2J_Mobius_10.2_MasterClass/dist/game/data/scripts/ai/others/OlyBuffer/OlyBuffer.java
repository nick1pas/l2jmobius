/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.others.OlyBuffer;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.skill.CommonSkill;
import org.l2jmobius.gameserver.model.skill.SkillCaster;

import ai.AbstractNpcAI;

/**
 * Olympiad Buffer AI.
 * @author St3eT
 */
public class OlyBuffer extends AbstractNpcAI
{
	// NPC
	private static final int OLYMPIAD_BUFFER = 36402;
	// Skills
	private static final CommonSkill[] BUFFS =
	{
		CommonSkill.OLYMPIAD_HARMONY,
		CommonSkill.OLYMPIAD_MELODY,
	};
	
	private OlyBuffer()
	{
		addStartNpc(OLYMPIAD_BUFFER);
		addFirstTalkId(OLYMPIAD_BUFFER);
		addTalkId(OLYMPIAD_BUFFER);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		String htmltext = null;
		if (npc.isScriptValue(0))
		{
			htmltext = "olympiad_master001.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "buff":
			{
				applyBuffs(npc, player);
				break;
			}
		}
		npc.setScriptValue(1);
		getTimers().addTimer("DELETE_ME", 5000, evnt -> npc.deleteMe());
		return "olympiad_master003.htm";
	}
	
	private void applyBuffs(Npc npc, Player player)
	{
		for (CommonSkill holder : BUFFS)
		{
			SkillCaster.triggerCast(npc, player, holder.getSkill());
		}
	}
	
	public static void main(String[] args)
	{
		new OlyBuffer();
	}
}