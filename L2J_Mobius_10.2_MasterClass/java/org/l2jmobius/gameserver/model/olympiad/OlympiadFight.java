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
package org.l2jmobius.gameserver.model.olympiad;

/**
 * @author dontknowdontcare
 */
public class OlympiadFight
{
	private final String _opponentName;
	private final int _opponentClassId;
	private final int _winner; // 0 = win, 1 = loss, 2 = draw
	private final int _opponentLevel;
	
	public OlympiadFight(String opponentName, int opponentLevel, int opponentClassId, int winner)
	{
		_opponentName = opponentName;
		_opponentClassId = opponentClassId;
		_winner = winner;
		_opponentLevel = opponentLevel;
	}
	
	public String getOpponentName()
	{
		return _opponentName;
	}
	
	public int getOpponentClassId()
	{
		return _opponentClassId;
	}
	
	public int getWinner()
	{
		return _winner;
	}
	
	public int getOpponentLevel()
	{
		return _opponentLevel;
	}
}
