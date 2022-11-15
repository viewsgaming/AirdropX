package nu.mine.raidisland.enums;

import lombok.Getter;
import org.mineacademy.fo.remain.CompMaterial;


public enum SpawningEvent {

	LIGHTNING_STRIKE("Lightning Strike" , CompMaterial.LIGHTNING_ROD , "",
			"Strike lightning on",
			"the airdrop.",
			"You can choose whether",
			"it can be damaged on not."),
	SPAWN_ZOMBIE("Spawn Zombie" , CompMaterial.ZOMBIE_SPAWN_EGG , "",
			"Spawn custom zombies",
			"nearby the airdrop."),
	RADIATION_AREA("Radiation Area" , CompMaterial.SLIME_BALL , "",
			"Generate radiation aren",
			"Players whom were living",
			"in range will be damaged"),
	AIRSTRIKE("Airstrike" , CompMaterial.TNT , "",
			"Call for the airstrike",
			"to drop nearby the airdrop."),

	FALLING_ANIMATION("Falling animation" , CompMaterial.ELYTRA , "",
			"Make an airdrop",
			"drop from sky");

	@Getter
	private final String name;
	@Getter
	private final CompMaterial material;

	@Getter
	private final String[] description;

	SpawningEvent(String name , CompMaterial material , String... description) {
		this.name = name;
		this.material = material;
		this.description = description;
	}
}
