package org.yogpstop.qp;

import java.util.HashMap;

import com.google.common.io.ByteArrayDataInput;

import buildcraft.api.power.IPowerProvider;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStationary;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquid;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;

public class TilePump extends APacketTile implements ITankContainer {
	private final HashMap<Integer, Integer> liquids = new HashMap<Integer, Integer>();
	private ForgeDirection connectTo = ForgeDirection.UNKNOWN;
	private boolean initialized = false;

	private byte prev = (byte) ForgeDirection.UNKNOWN.ordinal();

	public static double CE;
	public static double BP;

	protected byte efficiency;

	boolean connected() {
		int pX = this.xCoord;
		int pY = this.yCoord;
		int pZ = this.zCoord;
		switch (this.connectTo) {
		case UP:
			pY++;
			break;
		case DOWN:
			pY--;
			break;
		case SOUTH:
			pZ++;
			break;
		case NORTH:
			pZ--;
			break;
		case EAST:
			pX++;
			break;
		case WEST:
			pX--;
			break;
		default:
		}
		TileEntity te = this.worldObj.getBlockTileEntity(pX, pY, pZ);
		if (te instanceof TileBasic) return true;
		this.connectTo = ForgeDirection.UNKNOWN;
		sendNowPacket();
		return false;
	}

	boolean working() {
		return this.currentHeight >= this.cy;
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		return null;
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttc) {
		super.readFromNBT(nbttc);
		this.efficiency = nbttc.getByte("efficiency");
		this.connectTo = ForgeDirection.values()[nbttc.getByte("connectTo")];
		this.prev = (byte) (this.connectTo.ordinal() | (working() ? 0x80 : 0));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttc) {
		super.writeToNBT(nbttc);
		nbttc.setByte("efficiency", this.efficiency);
		nbttc.setByte("connectTo", (byte) this.connectTo.ordinal());
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!this.worldObj.isRemote && !this.initialized) {
			int pX, pY, pZ;
			TileEntity te;

			pX = this.xCoord;
			pY = this.yCoord;
			pZ = this.zCoord;
			switch (this.connectTo) {
			case UP:
				pY++;
				break;
			case DOWN:
				pY--;
				break;
			case SOUTH:
				pZ++;
				break;
			case NORTH:
				pZ--;
				break;
			case EAST:
				pX++;
				break;
			case WEST:
				pX--;
				break;
			default:
			}
			te = this.worldObj.getBlockTileEntity(pX, pY, pZ);
			if (te instanceof TileBasic && ((TileBasic) te).connect(this.connectTo.getOpposite())) {
				sendNowPacket();
				this.initialized = true;
			} else if (this.worldObj.isAirBlock(pX, pY, pZ) || this.connectTo == ForgeDirection.UNKNOWN) {
				this.connectTo = ForgeDirection.UNKNOWN;
				sendNowPacket();
				this.initialized = true;
			}
		}
	}

	void setEnchantment(ItemStack is) {
		if (this.efficiency > 0) is.addEnchantment(Enchantment.enchantmentsList[32], this.efficiency);
	}

	void init(NBTTagList nbttl) {
		if (nbttl != null) for (int i = 0; i < nbttl.tagCount(); i++) {
			short id = ((NBTTagCompound) nbttl.tagAt(i)).getShort("id");
			short lvl = ((NBTTagCompound) nbttl.tagAt(i)).getShort("lvl");
			if (id == 32) this.efficiency = (byte) lvl;
		}
		reinit();
	}

	void reinit() {
		int pX, pY, pZ;
		TileEntity te;
		for (ForgeDirection fd : ForgeDirection.VALID_DIRECTIONS) {
			pX = this.xCoord;
			pY = this.yCoord;
			pZ = this.zCoord;
			switch (fd) {
			case UP:
				pY++;
				break;
			case DOWN:
				pY--;
				break;
			case SOUTH:
				pZ++;
				break;
			case NORTH:
				pZ--;
				break;
			case EAST:
				pX++;
				break;
			case WEST:
				pX--;
				break;
			default:
			}
			te = this.worldObj.getBlockTileEntity(pX, pY, pZ);
			if (te instanceof TileBasic && ((TileBasic) te).connect(fd.getOpposite())) {
				this.connectTo = fd;
				sendNowPacket();
				return;
			}
		}
		this.connectTo = ForgeDirection.UNKNOWN;
		sendNowPacket();
		return;
	}

	private void sendNowPacket() {
		byte c = (byte) (this.connectTo.ordinal() | (working() ? 0x80 : 0));
		if (c != this.prev) {
			this.prev = c;
			PacketHandler.sendNowPacket(this, c);
		}
	}

	@Override
	void recievePacketOnServer(byte pattern, ByteArrayDataInput data, EntityPlayer ep) {}

	@Override
	void recievePacketOnClient(byte pattern, ByteArrayDataInput data) {
		switch (pattern) {
		case PacketHandler.packetNow:
			byte flag = data.readByte();
			if ((flag & 0x80) != 0) this.cy = this.currentHeight = -1;
			else this.currentHeight = Integer.MIN_VALUE;
			this.connectTo = ForgeDirection.getOrientation(flag & 0x7F);
			this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static final int Y_SIZE = 256;
	private static final int CHUNK_SCALE = 16;
	private static final int RANGE = 4;

	private boolean[][][] blocks;
	private ExtendedBlockStorage[][][] ebses;
	private int xOffset, yOffset, zOffset, currentHeight = Integer.MIN_VALUE;
	private int cx, cy = -1, cz;

	private Block b_c;
	private ExtendedBlockStorage ebs_c;
	private int block_side;

	private void setTargetRepeating(int x, int y, int z) {
		this.ebs_c = this.ebses[x >> 4][z >> 4][y >> 4];
		if (this.ebs_c == null) { return; }
		this.b_c = Block.blocksList[this.ebs_c.getExtBlockID(x & 0xF, y & 0xF, z & 0xF)];
		if (!this.blocks[y - this.yOffset][x][z] && (this.b_c instanceof ILiquid || (this.b_c != null ? this.b_c.blockMaterial.isLiquid() : false))) {
			this.blocks[y - this.yOffset][x][z] = true;
			if (0 < x) setTargetRepeating(x - 1, y, z);
			if (x < this.block_side - 1) setTargetRepeating(x + 1, y, z);
			if (0 < z) setTargetRepeating(x, y, z - 1);
			if (z < this.block_side - 1) setTargetRepeating(x, y, z + 1);
			if (y < Y_SIZE) setTargetRepeating(x, y + 1, z);
		}
	}

	private void searchLiquid(int x, int y, int z, int rg) {
		int chunk_side = (1 + rg * 2);
		this.block_side = chunk_side * CHUNK_SCALE;
		this.cx = x;
		this.cy = y;
		this.cz = z;
		this.xOffset = ((x >> 4) - rg) << 4;
		this.yOffset = (y >> 4) << 4;
		this.zOffset = ((z >> 4) - rg) << 4;
		this.currentHeight = Y_SIZE - 1;
		this.blocks = new boolean[Y_SIZE - this.yOffset][this.block_side][this.block_side];
		this.ebses = new ExtendedBlockStorage[chunk_side][chunk_side][];
		int kx, kz;
		for (kx = 0; kx < chunk_side; kx++) {
			for (kz = 0; kz < chunk_side; kz++) {
				this.ebses[kx][kz] = this.worldObj.getChunkFromChunkCoords(kx + (this.xOffset >> 4), kz + (this.zOffset >> 4)).getBlockStorageArray();
			}
		}
		setTargetRepeating(x - this.xOffset, y, z - this.zOffset);
	}

	boolean removeLiquids(IPowerProvider pp, int x, int y, int z) {
		if (!this.worldObj.getBlockMaterial(x, y, z).isLiquid()) return true;
		sendNowPacket();
		if (this.cx != x || this.cy != y || this.cz != z || this.currentHeight < this.cy) searchLiquid(x, y, z, RANGE);
		int block_count = 0;
		Block bb;
		int bx, bz, meta, bid;
		HashMap<Integer, Integer> cacheLiquids = new HashMap<Integer, Integer>();
		for (; block_count == 0; this.currentHeight--) {
			if (this.currentHeight < this.cy) return false;
			for (bx = 0; bx < this.block_side; bx++) {
				for (bz = 0; bz < this.block_side; bz++) {
					if (this.blocks[this.currentHeight - this.yOffset][bx][bz]) {
						bid = this.ebses[bx >> 4][bz >> 4][this.currentHeight >> 4].getExtBlockID(bx & 0xF, this.currentHeight & 0xF, bz & 0xF);
						bb = Block.blocksList[bid];
						meta = this.ebses[bx >> 4][bz >> 4][this.currentHeight >> 4].getExtBlockMetadata(bx & 0xF, this.currentHeight & 0xF, bz & 0xF);
						if ((bb instanceof ILiquid && ((ILiquid) bb).stillLiquidId() == bid && ((ILiquid) bb).stillLiquidMeta() == meta)
								|| bb instanceof BlockStationary) {
							block_count++;
							if (!cacheLiquids.containsKey(bid)) cacheLiquids.put(bid, 0);
							cacheLiquids.put(bid, cacheLiquids.get(bid) + LiquidContainerRegistry.BUCKET_VOLUME);
						}
					}
				}
			}
		}
		this.currentHeight++;
		float p = (float) (block_count * BP / Math.pow(CE, this.efficiency));
		if (pp.useEnergy(p, p, true) == p) {
			for (Integer key : cacheLiquids.keySet()) {
				if (!this.liquids.containsKey(key)) this.liquids.put(key, 0);
				this.liquids.put(key, this.liquids.get(key) + cacheLiquids.get(key));
			}
			for (bx = 0; bx < this.block_side; bx++) {
				for (bz = 0; bz < this.block_side; bz++) {
					if (this.blocks[this.currentHeight - this.yOffset][bx][bz]) {
						bid = this.ebses[bx >> 4][bz >> 4][this.currentHeight >> 4].getExtBlockID(bx & 0xF, this.currentHeight & 0xF, bz & 0xF);
						bb = Block.blocksList[bid];
						if ((bb != null ? bb.blockMaterial.isLiquid() : false) || bb instanceof ILiquid) {
							this.worldObj.setBlockToAir(bx + this.xOffset, this.currentHeight, bz + this.zOffset);
						}
					}
				}
			}
			this.currentHeight--;
		}
		sendNowPacket();
		return this.currentHeight < this.cy;
	}
}
