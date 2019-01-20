package com.volmit.catalyst.hosts.v12;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunkSnapshot;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlock;

import com.volmit.catalyst.api.ShadowChunk;

import net.minecraft.server.v1_12_R1.BiomeBase;
import net.minecraft.server.v1_12_R1.Block;
import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.ChunkSection;
import net.minecraft.server.v1_12_R1.ChunkSnapshot;
import net.minecraft.server.v1_12_R1.IBlockData;
import net.minecraft.server.v1_12_R1.PacketPlayOutMapChunk;
import net.minecraft.server.v1_12_R1.PacketPlayOutUnloadChunk;

public class ShadowChunk12 implements ShadowChunk
{
	private final Chunk actual;
	private final org.bukkit.Chunk chunk;
	private Chunk nmsCopy;
	private final boolean skylight;
	private final boolean[] modified;
	private boolean biomeModified;

	public ShadowChunk12(org.bukkit.Chunk chunk)
	{
		this.chunk = chunk;
		actual = ((CraftChunk) chunk).getHandle();
		nmsCopy = copy(actual);
		skylight = chunk.getWorld().getEnvironment().equals(Environment.NORMAL);
		modified = new boolean[16];
		dumpModifications();
	}

	@Override
	public void queueSection(int section)
	{
		if(nmsCopy.getSections()[section] != null)
		{
			modified[section] = true;
		}
	}

	@Override
	public void queueBiomes()
	{
		biomeModified = true;
	}

	@Override
	public org.bukkit.Chunk getSource()
	{
		return chunk;
	}

	@Override
	public void rebase()
	{
		nmsCopy = copy(actual);
		dumpModifications();
	}

	@Override
	public List<Object> flush()
	{
		List<Object> packets = new ArrayList<>();

		int chunkMask = getEntireMask();
		int modMask = getModifiedMask();

		if(biomeModified)
		{
			packets.add(new PacketPlayOutUnloadChunk(nmsCopy.locX, nmsCopy.locZ));
			modMask = chunkMask;
		}

		packets.add(new PacketPlayOutMapChunk(nmsCopy, modMask));
		dumpModifications();

		return packets;
	}

	@Override
	public int getEntireMask()
	{
		int bitMask = 0;

		for(int section = 0; section < nmsCopy.getSections().length; section++)
		{
			if(nmsCopy.getSections()[section] != null)
			{
				bitMask += 1 << section;
			}
		}

		return bitMask;
	}

	@Override
	public int getModifiedMask()
	{
		int bitMask = 0;

		for(int section = 0; section < modified.length; section++)
		{
			if(modified[section])
			{
				bitMask += 1 << section;
			}
		}

		return bitMask;
	}

	@Override
	public void setSkyLight(int x, int y, int z, int light)
	{
		if(!skylight)
		{
			return;
		}

		if(nmsCopy.getSections()[y >> 4] == null)
		{
			nmsCopy.getSections()[y >> 4] = new ChunkSection((y >> 4) << 4, skylight);
		}

		nmsCopy.getSections()[y >> 4].getSkyLightArray().a(x, y & 15, z, light);
		modified[y >> 4] = true;
	}

	@Override
	public void setBlockLight(int x, int y, int z, int light)
	{
		if(nmsCopy.getSections()[y >> 4] == null)
		{
			nmsCopy.getSections()[y >> 4] = new ChunkSection((y >> 4) << 4, skylight);
		}

		nmsCopy.getSections()[y >> 4].getEmittedLightArray().a(x, y & 15, z, light);
		modified[y >> 4] = true;
	}

	@Override
	public void setBiome(int x, int z, Biome bio)
	{
		setBiome(x, z, BiomeBase.REGISTRY_ID.a(CraftBlock.biomeToBiomeBase(bio)));
	}

	@Override
	public void setBiome(int x, int z, int id)
	{
		nmsCopy.getBiomeIndex()[(z & 15) << 4 | x & 15] = (byte) id;
		biomeModified = true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setBlock(int x, int y, int z, Material type, int data)
	{
		setBlock(x, y, z, type.getId(), data);
	}

	@Override
	public void setBlock(int x, int y, int z, Material type)
	{
		setBlock(x, y, z, type, 0);
	}

	@Override
	public void setBlock(Location l, Material type, int data)
	{
		setBlock(l.getBlockX() & 15, l.getBlockY(), l.getBlockZ() & 15, type, data);
	}

	@Override
	public void setBlock(Location l, Material type)
	{
		setBlock(l, type, 0);
	}

	@Override
	public void setBlock(int x, int y, int z, int type, int data)
	{
		@SuppressWarnings("deprecation")
		IBlockData d = Block.getById(type).fromLegacyData(data);
		int section = y >> 4;

		if(nmsCopy.getSections()[section] == null)
		{
			nmsCopy.getSections()[section] = new ChunkSection(section << 4, skylight);
		}

		nmsCopy.getSections()[section].setType(x, y & 15, z, d);
		modified[section] = true;
	}

	private Chunk copy(Chunk actual)
	{
		ChunkSnapshot snap = snapshot(actual);
		Chunk c = new Chunk(actual.world, snap, actual.locX, actual.locZ);
		copyTileEntities(actual, c);
		copyBiomes(actual, c);

		return c;
	}

	private void copyBiomes(Chunk actual, Chunk c)
	{
		for(int i = 0; i < actual.getBiomeIndex().length; i++)
		{
			c.getBiomeIndex()[i] = actual.getBiomeIndex()[i];
		}
	}

	private void copyTileEntities(Chunk actual, Chunk c)
	{
		c.tileEntities.putAll(actual.tileEntities);
	}

	@SuppressWarnings("deprecation")
	private ChunkSnapshot snapshot(Chunk actual)
	{
		ChunkSnapshot s = new ChunkSnapshot();
		CraftChunkSnapshot snap = (CraftChunkSnapshot) actual.bukkitChunk.getChunkSnapshot(false, false, false);

		for(int i = 0; i < 16; i++)
		{
			for(int k = 0; k < 16; k++)
			{
				for(int j = 0; j < 256; j++)
				{
					s.a(i, j, k, Block.getById(snap.getBlockTypeId(i, j, k)).fromLegacyData(snap.getBlockData(i, j, k)));
				}
			}
		}

		return s;
	}

	private void dumpModifications()
	{
		Arrays.fill(modified, false);
		biomeModified = false;
	}
}