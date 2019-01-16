package com.volmit.catalyst.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

/**
 * Represents a chunk that can be filled with data or wrap an existing chunk,
 * then be sent via U.getService(NMSSVC.class).sendChunkMap()
 *
 * @author cyberpwn
 *
 */
public class AbstractChunk
{
	public static final int DEFAULT_BIOME = Biome.PLAINS.ordinal();
	private boolean debug;
	private ChunkSection[] sections;
	private byte[] biome;
	private int x;
	private int z;
	private boolean sky;
	private boolean full;

	/**
	 * Create a new abstract chunk
	 */
	public AbstractChunk()
	{
		debug = false;
		sections = new ChunkSection[16];
		clearBiomes();
		full = false;
	}

	/**
	 * Create a new chunk with the given chunk coords
	 *
	 * @param x
	 *            the chunk x
	 * @param z
	 *            the chunk z
	 */
	public AbstractChunk(int x, int z)
	{
		this();
		setX(x);
		setZ(z);
	}

	/**
	 * Create a new chunk and fully absorbing the specified chunk
	 *
	 * @param chunk
	 *            the chunk to absorb
	 */
	public AbstractChunk(Chunk chunk)
	{
		this();
		x = chunk.getX();
		z = chunk.getZ();
		absorb(chunk);
	}

	/**
	 * Clear all biome data
	 */
	public void clearBiomes()
	{
		biome = new byte[256];
	}

	/**
	 * Absorb only the given chunk section. Does not record biome data. Does not
	 * record X and Z coordinates from chunk.
	 *
	 * @param c
	 *            the chunk.
	 * @param s
	 *            the section coord. 0 - 15
	 */
	@SuppressWarnings("deprecation")
	public void absorbChunkSection(Chunk c, int s)
	{
		int startY = s * 16;
		int endY = startY + 15;

		for(int i = 0; i < 16; i++)
		{
			for(int j = 0; j < 16; j++)
			{
				for(int k = startY; k < endY + 1; k++)
				{
					Block b = c.getBlock(i, k, j);

					if(!b.isEmpty())
					{
						set(i, k, j, b.getTypeId(), b.getData());
						setBlockLight(i, k, j, (byte) (b.getLightFromBlocks()));
						setSkyLight(i, k, j, (byte) (b.getLightFromSky()));
					}
				}
			}
		}
	}

	/**
	 * Set the chunk X and Z to the given chunks X and Z
	 *
	 * @param c
	 *            the chunk
	 */
	public void setXZ(Chunk c)
	{
		setX(c.getX());
		setZ(c.getZ());
	}

	/**
	 * Absorb biome mapping for the given chunk. Does not record block data or
	 * lighting. Does not record sections. Does not record chunk X and Z
	 *
	 * @param c
	 *            the chunk to map biomes from
	 */
	public void absorbBiomes(Chunk c)
	{
		for(int i = 0; i < 16; i++)
		{
			for(int j = 0; j < 16; j++)
			{
				c.getWorld().getBiome(i + (c.getX() * 16), j + (c.getZ() * 16));
			}
		}
	}

	/**
	 * Remove the chunk section
	 *
	 * @param section
	 *            the section to remove
	 */
	public void removeSection(int section)
	{
		sections[section] = null;
	}

	/**
	 * Absorb the entire given chunk, absorbs biome data, and every chunk section.
	 * This will also CREATE new EMPTY chunk sections even if the given chunk does
	 * not have any chunk sections in that location.
	 *
	 * @param c
	 *            the chunk to absorb
	 */
	@SuppressWarnings("deprecation")
	public void absorb(Chunk c)
	{
		setX(c.getX());
		setZ(c.getZ());

		for(int i = 0; i < 16; i++)
		{
			for(int j = 0; j < 16; j++)
			{
				for(int k = 0; k < 256; k++)
				{
					Block b = c.getBlock(i, k, j);

					if(!b.isEmpty())
					{
						set(i, k, j, b.getTypeId(), b.getData());
						setBlockLight(i, k, j, (byte) (b.getLightFromBlocks()));
						setSkyLight(i, k, j, (byte) (b.getLightFromSky()));
					}
				}

				c.getWorld().getBiome(i + (x * 16), j + (z * 16));
			}
		}
	}

	/**
	 * Check if this chunk is set to have sky
	 *
	 * @return true if has sky
	 */
	public boolean hasSky()
	{
		return sky;
	}

	/**
	 * Set this chunk to have a sky (i.e. not the nether)
	 *
	 * @param sky
	 *            set sky
	 */
	public void setSky(boolean sky)
	{
		this.sky = sky;
	}

	/**
	 * Set the biome for this position
	 *
	 * @param x
	 *            the x coord (0 - 15)
	 * @param z
	 *            the z coord (0 - 15)
	 * @param b
	 *            the biome
	 */
	public void setBiome(int x, int z, Biome b)
	{
		biome[z * 16 | x] = (byte) b.ordinal();
	}

	/**
	 * Get the biome at this position
	 *
	 * @param x
	 *            the x
	 * @param z
	 *            the z
	 * @return the biome or null if it doesnt exist
	 */
	public Biome getBiome(int x, int z)
	{
		return Biome.values()[biome[z * 16 | x]];
	}

	/**
	 * Write this chunk to a byte array
	 *
	 * @return the bytes representing this chunk
	 * @throws IllegalStateException
	 *             shit happens
	 * @throws IOException
	 *             shit happens
	 */
	public byte[] write() throws IllegalStateException, IOException
	{
		ByteArrayOutputStream boas = new ByteArrayOutputStream();

		verify();

		for(int i = 0; i < sections.length; i++)
		{
			if(sections[i] != null)
			{
				int bts = sections[i].writeToBuf(boas, hasSky());
				log("Section: " + i + " -> WRITE " + "(" + bts + " bytes, " + sections[i].getBitsPerBlock() + " bits/block, " + sections[i].getCount() + " blocks" + ")");
			}

			else
			{
				log("Section: " + i + " -> SKIP");
			}
		}

		if(isFull())
		{
			boas.write(biome);
		}

		return boas.toByteArray();
	}

	private void verify()
	{
		for(int i = 0; i < 16; i++)
		{
			if(sections[i] != null && sections[i].isEmpty())
			{
				sections[i] = null;
			}
		}
	}

	private void log(String string)
	{
		if(debug)
		{
			System.out.println("[ASChunk: " + getX() + "," + getZ() + "]: " + string);
		}
	}

	/**
	 * Check if this chunk is ground up continuous.
	 *
	 * @return true if every chunk section from the ground up to the empty sections
	 *         is filled (i.e. there is no fragmentation starting from the ground)
	 */
	public boolean isContinuous()
	{
		int m = 0;
		int d = 0;

		for(int section = 0; section < sections.length; section++)
		{
			if(sections[section] != null)
			{
				m++;
			}
		}

		for(int section = 0; section < m; section++)
		{
			if(sections[section] != null)
			{
				d++;
			}
		}

		return d == m;
	}

	/**
	 * Set the skylight level
	 *
	 * @param x
	 *            the x coord
	 * @param y
	 *            the y coord
	 * @param z
	 *            the z coord
	 * @param level
	 *            the light level (0-15)
	 */
	public void setSkyLight(int x, int y, int z, byte level)
	{
		int sect = getSection(y);
		int sectY = getSectionY(y);
		ensureSection(sect);
		sections[sect].setSkyLight(x, sectY, z, level);
	}

	/**
	 * Set the blocklight level
	 *
	 * @param x
	 *            the x coord
	 * @param y
	 *            the y coord
	 * @param z
	 *            the z coord
	 * @param level
	 *            the light level (0-15)
	 */
	public void setBlockLight(int x, int y, int z, byte level)
	{
		int sect = getSection(y);
		int sectY = getSectionY(y);
		ensureSection(sect);
		sections[sect].setBlockLight(x, sectY, z, level);
	}

	/**
	 * Set the skylight level
	 *
	 * @param x
	 *            the x coord
	 * @param y
	 *            the y coord
	 * @param z
	 *            the z coord
	 * @param level
	 *            the light level (0-15)
	 */
	public void setSkyLight(int x, int y, int z, int level)
	{
		setSkyLight(x, y, z, (byte) level);
	}

	/**
	 * Set the blocklight level
	 *
	 * @param x
	 *            the x coord
	 * @param y
	 *            the y coord
	 * @param z
	 *            the z coord
	 * @param level
	 *            the light level (0-15)
	 */
	public void setBlockLight(int x, int y, int z, int level)
	{
		setBlockLight(x, y, z, (byte) level);
	}

	/**
	 * Optimize all chunk sections. This operation is expensive, though a
	 * millisecond benchmark is returned.
	 */
	public double optimize()
	{
		double t = 0;

		for(int i = 0; i < 16; i++)
		{
			t += optimizeSection(i);
		}

		return t;
	}

	/**
	 * Optimizes the given chunk section. This operation is expensive, but can help
	 * with the data size. Does nothing if there is no section in this area.
	 *
	 * @param section
	 *            the chunk section
	 * @return the time spent optimizing
	 */
	public double optimizeSection(int section)
	{
		if(hasSection(section))
		{
			sections[section].optimize();
		}

		return 0;
	}

	/**
	 * Clone the given chunk section. This is a full copy.
	 *
	 * @param the
	 *            section coord
	 * @return the coped section, or null if it doesnt exist
	 */
	public ChunkSection cloneSection(int section)
	{
		if(hasSection(section))
		{
			return sections[section].clone();
		}

		return null;
	}

	/**
	 * Check if the given section coord exists
	 *
	 * @param section
	 *            the section coord
	 * @return true if there is a section at this coord.
	 */
	public boolean hasSection(int section)
	{
		return sections[section] != null;
	}

	/**
	 * Set the block data at this location
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 * @param id
	 *            the block id
	 * @param data
	 *            the block meta id
	 */
	public void set(int x, int y, int z, int id, byte data)
	{
		int sect = getSection(y);
		int sectY = getSectionY(y);
		ensureSection(sect);
		sections[sect].setType(x, sectY, z, id, data);
	}

	/**
	 * Set the block data at this location
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 * @param id
	 *            the block id
	 * @param data
	 *            the block meta id
	 */
	public void set(int x, int y, int z, int id, int data)
	{
		set(x, y, z, id, (byte) data);
	}

	/**
	 * Set the block data in this area
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 * @param m
	 *            the material (assumed data = 0)
	 */
	@SuppressWarnings("deprecation")
	public void set(int x, int y, int z, Material m)
	{
		set(x, y, z, m.getId(), 0);
	}

	/**
	 * Ensure the section exists by creating it if it doesnt
	 *
	 * @param sect
	 *            the section id
	 */
	public void ensureSection(int sect)
	{
		if(sections[sect] == null)
		{
			sections[sect] = new ChunkSection();
		}
	}

	/**
	 * Get the y coordinate of section relative coords from a chunk relative y
	 * value.<br/>
	 * getSectionY(15) -> 15<br/>
	 * getSectionY(17) -> 1<br/>
	 * getSectionY(34) -> 2<br/>
	 *
	 * @param y
	 *            the chunk relative y coord
	 * @return the chunk section relative y coord
	 */
	public int getSectionY(int y)
	{
		return y - (getSection(y) * 16);
	}

	/**
	 * Get the chunks bitmask representing which chunk sections are used and which
	 * sections do not exist.
	 *
	 * @return the bitmask
	 */
	public int getBitMask()
	{
		int bitMask = 0;

		for(int section = 0; section < sections.length; section++)
		{
			if(sections[section] != null)
			{
				bitMask += 1 << section;
			}
		}

		return bitMask;
	}

	/**
	 * Get the bitshifted chunk section coordinate.<br/>
	 * getSection(15) -> 0<br/>
	 * getSection(16) -> 1<br/>
	 * getSection(255) -> 15<br/>
	 * getSection(127) -> 7<br/>
	 *
	 * @param y
	 *            the chunk relative y
	 * @return the section coordinate
	 */
	public int getSection(int y)
	{
		return y >> 4;
	}

	/**
	 * Get all chunk sections (not a copy!)
	 *
	 * @return the chunk section array
	 */
	public ChunkSection[] getSections()
	{
		return sections;
	}

	/**
	 * Get the chunk x
	 *
	 * @return the chunk x
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Get the chunk z
	 *
	 * @return the chunk z
	 */
	public int getZ()
	{
		return z;
	}

	/**
	 * Set the chunk x
	 *
	 * @param x
	 *            the chunk x
	 */
	public void setX(int x)
	{
		this.x = x;
	}

	/**
	 * Set the chunk z
	 *
	 * @param z
	 *            the chunk z
	 */
	public void setZ(int z)
	{
		this.z = z;
	}

	/**
	 * Debug the output of this chunk when it is sent
	 *
	 * @param b
	 *            set to true for outputs
	 */
	public void setDebug(boolean b)
	{
		debug = true;
	}

	public void setFull(boolean full)
	{
		this.full = full;
	}

	public boolean isFull()
	{
		return full && isContinuous();
	}
}
