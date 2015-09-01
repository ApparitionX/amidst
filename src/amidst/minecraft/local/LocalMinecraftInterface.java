package amidst.minecraft.local;

import java.util.Map;

import MoF.SaveLoader.Type;
import amidst.logging.Log;
import amidst.minecraft.IMinecraftInterface;
import amidst.version.VersionInfo;

public class LocalMinecraftInterface implements IMinecraftInterface {
	/**
	 * A GenLayer instance, at quarter scale to the final biome layer (i.e. both
	 * axis are divided by 4). Minecraft calculates biomes at
	 * quarter-resolution, then noisily interpolates the biome-map up to 1:1
	 * resolution when needed, this is the biome GenLayer before it is
	 * interpolated.
	 */
	private MinecraftObject approximationBiomeGenerator;

	/**
	 * A GenLayer instance, the biome layer. (1:1 scale) Minecraft calculates
	 * biomes at quarter-resolution, then noisily interpolates the biome-map up
	 * to 1:1 resolution when needed, this is the interpolated biome GenLayer.
	 */
	private MinecraftObject exactBiomeGenerator;

	private MinecraftClass intCacheClass;
	private MinecraftClass blockInitClass;
	private MinecraftClass genLayerClass;
	private MinecraftClass worldTypeClass;
	private VersionInfo version;

	public LocalMinecraftInterface(
			Map<String, MinecraftClass> minecraftClassMap, VersionInfo version) {
		this.intCacheClass = minecraftClassMap.get("IntCache");
		this.blockInitClass = minecraftClassMap.get("BlockInit");
		this.genLayerClass = minecraftClassMap.get("GenLayer");
		this.worldTypeClass = minecraftClassMap.get("WorldType");
		this.version = version;
	}

	@Override
	public int[] getBiomeData(int x, int y, int width, int height,
			boolean useQuarterResolutionMap) {
		intCacheClass.callStaticMethod("resetIntCache");
		return (int[]) getBiomeGenerator(useQuarterResolutionMap).callMethod(
				"getInts", x, y, width, height);
	}

	private MinecraftObject getBiomeGenerator(boolean useQuarterResolutionMap) {
		if (useQuarterResolutionMap) {
			return approximationBiomeGenerator;
		} else {
			return exactBiomeGenerator;
		}
	}

	@Override
	public void createWorld(long seed, String typeName, String generatorOptions) {
		Log.debug("Attempting to create world with seed: " + seed + ", type: "
				+ typeName + ", and the following generator options:");
		Log.debug(generatorOptions);
		initializeBlock();
		Object[] genLayers = getGenLayers(seed, typeName, generatorOptions);
		approximationBiomeGenerator = new MinecraftObject(genLayerClass,
				genLayers[0]);
		exactBiomeGenerator = new MinecraftObject(genLayerClass, genLayers[1]);
	}

	private void initializeBlock() {
		// Minecraft 1.8 and higher require block initialization to be called
		// before creating a biome generator.
		if (blockInitClass != null) {
			blockInitClass.callStaticMethod("initialize");
		}
	}

	private Object[] getGenLayers(long seed, String typeName,
			String generatorOptions) {
		if (worldTypeClass == null) {
			return initializeAllBiomeGenerators(seed);
		} else if (initializeAllBiomeGeneratorsWithParamsExists()) {
			return initializeAllBiomeGeneratorsWithParams(seed,
					generatorOptions, getWorldType(typeName));
		} else {
			return initializeAllBiomeGenerators(seed, getWorldType(typeName));
		}
	}

	private Object getWorldType(String typeName) {
		String type = Type.fromMixedCase(typeName).getValue();
		MinecraftObject object = (MinecraftObject) worldTypeClass
				.getStaticPropertyValue(type);
		return object.getObject();
	}

	private boolean initializeAllBiomeGeneratorsWithParamsExists() {
		return genLayerClass
				.getMethod("initializeAllBiomeGeneratorsWithParams").exists();
	}

	private Object[] initializeAllBiomeGenerators(long seed, Object worldType) {
		return (Object[]) genLayerClass.callStaticMethod(
				"initializeAllBiomeGenerators", seed, worldType);
	}

	private Object[] initializeAllBiomeGenerators(long seed) {
		return (Object[]) genLayerClass.callStaticMethod(
				"initializeAllBiomeGenerators", seed);
	}

	private Object[] initializeAllBiomeGeneratorsWithParams(long seed,
			String generatorOptions, Object worldType) {
		return (Object[]) genLayerClass.callStaticMethod(
				"initializeAllBiomeGeneratorsWithParams", seed, worldType,
				generatorOptions);
	}

	@Override
	public VersionInfo getVersion() {
		return version;
	}
}