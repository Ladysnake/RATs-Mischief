package doctor4t.ratsmischief.common.init;

import doctor4t.ratsmischief.common.RatsMischief;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModBlocks {
	Map<Block, Identifier> BLOCKS = new LinkedHashMap<>();

//	Block MOD_BLOCK = createBlock("mod_block", new ModBlock(QuiltBlockSettings.of(Material.METAL, MapColor.DEEPSLATE_GRAY).strength(-1.0F, 3600000.0F).sounds(BlockSoundGroup.COPPER)), true);

	static void initialize() {
		BLOCKS.keySet().forEach(block -> Registry.register(Registry.BLOCK, BLOCKS.get(block), block));
	}

	private static <T extends Block> T createBlock(String name, T block, boolean createItem) {
		BLOCKS.put(block, new Identifier(RatsMischief.MOD_ID, name));
		if (createItem) {
			ModItems.ITEMS.put(new BlockItem(block, new QuiltItemSettings()), BLOCKS.get(block));
		}
		return block;
	}
}
