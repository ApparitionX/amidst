package amidst.mojangapi.file.nbt.playerfile;

import java.io.IOException;
import java.util.List;

import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.Tag;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.nbt.NBTTagKeys;
import amidst.mojangapi.world.player.PlayerCoordinates;

@Immutable
public enum PlayerLocationLoader {
	;

	public static PlayerCoordinates readFromPlayerFile(CompoundTag file)
			throws IOException {
		try {
			return readPlayerCoordinates(file);
		} catch (Exception e) {
			throw new IOException("cannot read player coordinates", e);
		}
	}

	public static PlayerCoordinates readFromLevelDat(CompoundTag file)
			throws IOException {
		try {
			return readPlayerCoordinates(getSinglePlayerPlayerTag(getTagRootTag(file)));
		} catch (Exception e) {
			throw new IOException("cannot read player coordinates", e);
		}
	}

	private static CompoundTag getTagRootTag(CompoundTag rootTag) {
		return (CompoundTag) rootTag.getValue().get(NBTTagKeys.TAG_KEY_DATA);
	}

	private static CompoundTag getSinglePlayerPlayerTag(CompoundTag rootDataTag) {
		return (CompoundTag) rootDataTag.getValue().get(
				NBTTagKeys.TAG_KEY_PLAYER);

	}

	private static PlayerCoordinates readPlayerCoordinates(CompoundTag tag) {
		ListTag posTag = (ListTag) getTagPos(tag);
		List<Tag> posList = posTag.getValue();
		// @formatter:off
		return new PlayerCoordinates(
				(long) (double) (Double) posList.get(0).getValue(),
				(long) (double) (Double) posList.get(1).getValue(),
				(long) (double) (Double) posList.get(2).getValue());
		// @formatter:on
	}

	private static Tag getTagPos(CompoundTag tag) {
		return tag.getValue().get(NBTTagKeys.TAG_KEY_POS);
	}
}