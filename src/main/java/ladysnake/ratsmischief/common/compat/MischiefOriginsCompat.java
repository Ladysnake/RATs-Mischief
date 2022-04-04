package ladysnake.ratsmischief.common.compat;

public class MischiefOriginsCompat {
//	public static final Identifier CONDITION_ID = new Identifier(Mischief.MODID, "enable_rat");
//	public static final ConditionFactory<Entity> CONDITION_FACTORY = new ConditionFactory<>(CONDITION_ID, new SerializableData().add("enabled", SerializableDataTypes.BOOLEAN), (instance, entity) -> {
//		if (FabricLoader.getInstance().isModLoaded("requiem")) {
//			StartingRemnantType startingRemnantType = RequiemSyncedGamerules.get(entity.world).getStartingRemnantType();
//			return startingRemnantType.getRemnantType() == StartingRemnantType.CHOOSE.getRemnantType();
//		}
//		return false;
//	});

	public static void init() {
//		Registry.register(ApoliRegistries.ENTITY_CONDITION, CONDITION_ID, CONDITION_FACTORY);
	}
}
