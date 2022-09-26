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
package quests;

import java.util.logging.Level;
import java.util.logging.Logger;

import quests.Q00070_SagaOfThePhoenixKnight.Q00070_SagaOfThePhoenixKnight;
import quests.Q00071_SagaOfEvasTemplar.Q00071_SagaOfEvasTemplar;
import quests.Q00072_SagaOfTheSwordMuse.Q00072_SagaOfTheSwordMuse;
import quests.Q00073_SagaOfTheDuelist.Q00073_SagaOfTheDuelist;
import quests.Q00074_SagaOfTheDreadnought.Q00074_SagaOfTheDreadnought;
import quests.Q00075_SagaOfTheTitan.Q00075_SagaOfTheTitan;
import quests.Q00076_SagaOfTheGrandKhavatari.Q00076_SagaOfTheGrandKhavatari;
import quests.Q00077_SagaOfTheDominator.Q00077_SagaOfTheDominator;
import quests.Q00078_SagaOfTheDoomcryer.Q00078_SagaOfTheDoomcryer;
import quests.Q00079_SagaOfTheAdventurer.Q00079_SagaOfTheAdventurer;
import quests.Q00080_SagaOfTheWindRider.Q00080_SagaOfTheWindRider;
import quests.Q00081_SagaOfTheGhostHunter.Q00081_SagaOfTheGhostHunter;
import quests.Q00082_SagaOfTheSagittarius.Q00082_SagaOfTheSagittarius;
import quests.Q00083_SagaOfTheMoonlightSentinel.Q00083_SagaOfTheMoonlightSentinel;
import quests.Q00084_SagaOfTheGhostSentinel.Q00084_SagaOfTheGhostSentinel;
import quests.Q00085_SagaOfTheCardinal.Q00085_SagaOfTheCardinal;
import quests.Q00086_SagaOfTheHierophant.Q00086_SagaOfTheHierophant;
import quests.Q00087_SagaOfEvasSaint.Q00087_SagaOfEvasSaint;
import quests.Q00088_SagaOfTheArchmage.Q00088_SagaOfTheArchmage;
import quests.Q00089_SagaOfTheMysticMuse.Q00089_SagaOfTheMysticMuse;
import quests.Q00090_SagaOfTheStormScreamer.Q00090_SagaOfTheStormScreamer;
import quests.Q00091_SagaOfTheArcanaLord.Q00091_SagaOfTheArcanaLord;
import quests.Q00092_SagaOfTheElementalMaster.Q00092_SagaOfTheElementalMaster;
import quests.Q00093_SagaOfTheSpectralMaster.Q00093_SagaOfTheSpectralMaster;
import quests.Q00094_SagaOfTheSoultaker.Q00094_SagaOfTheSoultaker;
import quests.Q00095_SagaOfTheHellKnight.Q00095_SagaOfTheHellKnight;
import quests.Q00096_SagaOfTheSpectralDancer.Q00096_SagaOfTheSpectralDancer;
import quests.Q00097_SagaOfTheShillienTemplar.Q00097_SagaOfTheShillienTemplar;
import quests.Q00098_SagaOfTheShillienSaint.Q00098_SagaOfTheShillienSaint;
import quests.Q00099_SagaOfTheFortuneSeeker.Q00099_SagaOfTheFortuneSeeker;
import quests.Q00100_SagaOfTheMaestro.Q00100_SagaOfTheMaestro;
import quests.Q00127_FishingSpecialistsRequest.Q00127_FishingSpecialistsRequest;
import quests.Q00300_HuntingLetoLizardman.Q00300_HuntingLetoLizardman;
import quests.Q00326_VanquishRemnants.Q00326_VanquishRemnants;
import quests.Q00327_RecoverTheFarmland.Q00327_RecoverTheFarmland;
import quests.Q00328_SenseForBusiness.Q00328_SenseForBusiness;
import quests.Q00329_CuriosityOfADwarf.Q00329_CuriosityOfADwarf;
import quests.Q00331_ArrowOfVengeance.Q00331_ArrowOfVengeance;
import quests.Q00333_HuntOfTheBlackLion.Q00333_HuntOfTheBlackLion;
import quests.Q00344_1000YearsTheEndOfLamentation.Q00344_1000YearsTheEndOfLamentation;
import quests.Q00348_AnArrogantSearch.Q00348_AnArrogantSearch;
import quests.Q00354_ConquestOfAlligatorIsland.Q00354_ConquestOfAlligatorIsland;
import quests.Q00355_FamilyHonor.Q00355_FamilyHonor;
import quests.Q00356_DigUpTheSeaOfSpores.Q00356_DigUpTheSeaOfSpores;
import quests.Q00358_IllegitimateChildOfTheGoddess.Q00358_IllegitimateChildOfTheGoddess;
import quests.Q00360_PlunderTheirSupplies.Q00360_PlunderTheirSupplies;
import quests.Q00369_CollectorOfJewels.Q00369_CollectorOfJewels;
import quests.Q00370_AnElderSowsSeeds.Q00370_AnElderSowsSeeds;
import quests.Q00500_BrothersBoundInChains.Q00500_BrothersBoundInChains;
import quests.Q00662_AGameOfCards.Q00662_AGameOfCards;
import quests.Q00933_ExploringTheWestWingOfTheDungeonOfAbyss.Q00933_ExploringTheWestWingOfTheDungeonOfAbyss;
import quests.Q00935_ExploringTheEastWingOfTheDungeonOfAbyss.Q00935_ExploringTheEastWingOfTheDungeonOfAbyss;
import quests.Q10673_SagaOfLegend.Q10673_SagaOfLegend;
import quests.Q10866_PunitiveOperationOnTheDevilIsle.Q10866_PunitiveOperationOnTheDevilIsle;
import quests.Q10981_UnbearableWolvesHowling.Q10981_UnbearableWolvesHowling;
import quests.Q10982_SpiderHunt.Q10982_SpiderHunt;
import quests.Q10983_TroubledForest.Q10983_TroubledForest;
import quests.Q10984_CollectSpiderweb.Q10984_CollectSpiderweb;
import quests.Q10985_CleaningUpTheGround.Q10985_CleaningUpTheGround;
import quests.Q10986_SwampMonster.Q10986_SwampMonster;
import quests.Q10987_PlunderedGraves.Q10987_PlunderedGraves;
import quests.Q10988_Conspiracy.Q10988_Conspiracy;
import quests.Q10989_DangerousPredators.Q10989_DangerousPredators;
import quests.Q10990_PoisonExtraction.Q10990_PoisonExtraction;
import quests.Q10993_FutureDwarves.Q10993_FutureDwarves;
import quests.Q10994_FutureOrcs.Q10994_FutureOrcs;
import quests.not_done.Q00630_PirateTreasureHunt;
import quests.not_done.Q00664_QuarrelsTime;
import quests.not_done.Q00910_RequestFromTheRedLibraGuildLv1;
import quests.not_done.Q00911_RequestFromTheRedLibraGuildLv2;
import quests.not_done.Q00912_RequestFromTheRedLibraGuildLv3;
import quests.not_done.Q00913_RequestFromTheRedLibraGuildLv4;
import quests.not_done.Q00914_RequestFromTheRedLibraGuildLv5;
import quests.not_done.Q10861_MonsterArenaTheBirthOfAWarrior;
import quests.not_done.Q10862_MonsterArenaChallenge10Battles;
import quests.not_done.Q10863_MonsterArenaNewChallenge15Battles;
import quests.not_done.Q10864_MonsterArenaBraveWarrior25Battles;
import quests.not_done.Q10865_MonsterArenaLastCall40Battles;
import quests.not_done.Q10867_GoneMissing;
import quests.not_done.Q10868_TheDarkSideOfPower;
import quests.not_done.Q10870_UnfinishedDevice;
import quests.not_done.Q10871_DeathToThePirateKing;

/**
 * @author NosBit
 */
public class QuestMasterHandler
{
	private static final Logger LOGGER = Logger.getLogger(QuestMasterHandler.class.getName());
	
	private static final Class<?>[] QUESTS =
	{
		Q00070_SagaOfThePhoenixKnight.class,
		Q00071_SagaOfEvasTemplar.class,
		Q00072_SagaOfTheSwordMuse.class,
		Q00073_SagaOfTheDuelist.class,
		Q00074_SagaOfTheDreadnought.class,
		Q00075_SagaOfTheTitan.class,
		Q00076_SagaOfTheGrandKhavatari.class,
		Q00077_SagaOfTheDominator.class,
		Q00078_SagaOfTheDoomcryer.class,
		Q00079_SagaOfTheAdventurer.class,
		Q00080_SagaOfTheWindRider.class,
		Q00081_SagaOfTheGhostHunter.class,
		Q00082_SagaOfTheSagittarius.class,
		Q00083_SagaOfTheMoonlightSentinel.class,
		Q00084_SagaOfTheGhostSentinel.class,
		Q00085_SagaOfTheCardinal.class,
		Q00086_SagaOfTheHierophant.class,
		Q00087_SagaOfEvasSaint.class,
		Q00088_SagaOfTheArchmage.class,
		Q00089_SagaOfTheMysticMuse.class,
		Q00090_SagaOfTheStormScreamer.class,
		Q00091_SagaOfTheArcanaLord.class,
		Q00092_SagaOfTheElementalMaster.class,
		Q00093_SagaOfTheSpectralMaster.class,
		Q00094_SagaOfTheSoultaker.class,
		Q00095_SagaOfTheHellKnight.class,
		Q00096_SagaOfTheSpectralDancer.class,
		Q00097_SagaOfTheShillienTemplar.class,
		Q00098_SagaOfTheShillienSaint.class,
		Q00099_SagaOfTheFortuneSeeker.class,
		Q00100_SagaOfTheMaestro.class,
		Q00127_FishingSpecialistsRequest.class,
		Q00300_HuntingLetoLizardman.class,
		Q00326_VanquishRemnants.class,
		Q00327_RecoverTheFarmland.class,
		Q00328_SenseForBusiness.class,
		Q00329_CuriosityOfADwarf.class,
		Q00331_ArrowOfVengeance.class,
		Q00333_HuntOfTheBlackLion.class,
		Q00344_1000YearsTheEndOfLamentation.class,
		Q00348_AnArrogantSearch.class,
		Q00354_ConquestOfAlligatorIsland.class,
		Q00355_FamilyHonor.class,
		Q00356_DigUpTheSeaOfSpores.class,
		Q00358_IllegitimateChildOfTheGoddess.class,
		Q00360_PlunderTheirSupplies.class,
		Q00369_CollectorOfJewels.class,
		Q00370_AnElderSowsSeeds.class,
		Q00500_BrothersBoundInChains.class,
		Q00630_PirateTreasureHunt.class, // TODO: Not done.
		Q00662_AGameOfCards.class,
		Q00664_QuarrelsTime.class, // TODO: Not done.
		Q00910_RequestFromTheRedLibraGuildLv1.class, // TODO: Not done.
		Q00911_RequestFromTheRedLibraGuildLv2.class, // TODO: Not done.
		Q00912_RequestFromTheRedLibraGuildLv3.class, // TODO: Not done.
		Q00913_RequestFromTheRedLibraGuildLv4.class, // TODO: Not done.
		Q00914_RequestFromTheRedLibraGuildLv5.class, // TODO: Not done.
		Q00933_ExploringTheWestWingOfTheDungeonOfAbyss.class,
		Q00935_ExploringTheEastWingOfTheDungeonOfAbyss.class,
		Q10673_SagaOfLegend.class,
		Q10861_MonsterArenaTheBirthOfAWarrior.class, // TODO: Not done.
		Q10862_MonsterArenaChallenge10Battles.class, // TODO: Not done.
		Q10863_MonsterArenaNewChallenge15Battles.class, // TODO: Not done.
		Q10864_MonsterArenaBraveWarrior25Battles.class, // TODO: Not done.
		Q10865_MonsterArenaLastCall40Battles.class, // TODO: Not done.
		Q10866_PunitiveOperationOnTheDevilIsle.class,
		Q10867_GoneMissing.class, // TODO: Not done.
		Q10868_TheDarkSideOfPower.class, // TODO: Not done.
		Q10870_UnfinishedDevice.class, // TODO: Not done.
		Q10871_DeathToThePirateKing.class, // TODO: Not done.
		Q10981_UnbearableWolvesHowling.class,
		Q10982_SpiderHunt.class,
		Q10983_TroubledForest.class,
		Q10984_CollectSpiderweb.class,
		Q10985_CleaningUpTheGround.class,
		Q10986_SwampMonster.class,
		Q10987_PlunderedGraves.class,
		Q10988_Conspiracy.class,
		Q10989_DangerousPredators.class,
		Q10990_PoisonExtraction.class,
		Q10993_FutureDwarves.class,
		Q10994_FutureOrcs.class,
	};
	
	public static void main(String[] args)
	{
		for (Class<?> quest : QUESTS)
		{
			try
			{
				quest.getDeclaredConstructor().newInstance();
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, QuestMasterHandler.class.getSimpleName() + ": Failed loading " + quest.getSimpleName() + ":", e);
			}
		}
	}
}
