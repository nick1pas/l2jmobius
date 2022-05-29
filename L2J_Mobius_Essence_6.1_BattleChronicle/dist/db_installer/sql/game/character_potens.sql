DROP TABLE IF EXISTS `character_potens`;
CREATE TABLE IF NOT EXISTS `character_potens` (
  `charId` INT UNSIGNED NOT NULL DEFAULT 0,
  `enchant_level` INT NOT NULL DEFAULT 0,
  `enchant_exp` INT NOT NULL DEFAULT 0,
  `poten_id` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`charId`,`poten_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;