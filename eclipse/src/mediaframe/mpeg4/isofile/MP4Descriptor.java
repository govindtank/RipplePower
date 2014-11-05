/**
 * MediaFrame is an Open Source streaming media platform in Java 
 * which provides a fast, easy to implement and extremely small applet 
 * that enables to view your audio/video content without having 
 * to rely on external player applications or bulky plug-ins.
 * 
 * Copyright (C) 2004/5 MediaFrame (http://www.mediaframe.org).
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */


/**
	This software module was originally developed by Apple Computer, Inc.
	in the course of development of MPEG-4. 
	This software module is an implementation of a part of one or 
	more MPEG-4 tools as specified by MPEG-4. 
	ISO/IEC gives users of MPEG-4 free license to this
	software module or modifications thereof for use in hardware 
	or software products claiming conformance to MPEG-4.
	Those intending to use this software module in hardware or software
	products are advised that its use may infringe existing patents.
	The original developer of this software module and his/her company,
	the subsequent editors and their companies, and ISO/IEC have no
	liability for use of this software module or modifications thereof
	in an implementation.
	Copyright is not released for non MPEG-4 conforming
	products. Apple Computer, Inc. retains full right to use the code for its own
	purpose, assign or donate the code to a third party and to
	inhibit third parties from using the code for non 
	MPEG-4 conforming products.
	This copyright notice must be included in all copies or
	derivative works. Copyright (c) 1999, 2000.
*/

package mediaframe.mpeg4.isofile;

import java.io.IOException;
import java.util.Vector;

import org.ripple.power.sound.DataStream;



/**
 * MP4Descriptor
 * 
 */
public class MP4Descriptor {

	public final static int MP4ES_DescriptorTag = 3;
	public final static int MP4DecoderConfigDescriptorTag = 4;
	public final static int MP4DecSpecificInfoDescriptorTag = 5;

	protected int type;
	protected int size;
	
	protected int readed;
	
	protected Vector children = new Vector();
	
	public MP4Descriptor(int type, int size) {
		super();
		this.readed = 0;
		this.type = type;
		this.size = size;
	}
	
	public static MP4Descriptor createDescriptor(DataStream bitstream) throws IOException {
		int tag = (int)bitstream.readBytes(1);
		int readed = 1;
		int size = 0;
		int b = 0;
		do {
			b = (int)bitstream.readBytes(1);
			size <<= 7;
			size |= b & 0x7f; 
			readed ++;
		} while((b & 0x80) == 0x80);
		MP4Descriptor descriptor = new MP4Descriptor(tag, size);
		switch(tag) {
			case MP4ES_DescriptorTag:
				descriptor.createES_Descriptor(bitstream);
				break;
			case MP4DecoderConfigDescriptorTag:
				descriptor.createDecoderConfigDescriptor(bitstream);
				break;
			case MP4DecSpecificInfoDescriptorTag:
				descriptor.createDecSpecificInfoDescriptor(bitstream);
				break;
			default:
				break;
		}
		bitstream.skipBytes(descriptor.size - descriptor.readed);
		descriptor.readed = readed + descriptor.size; 
		return descriptor;
	}
	
	/**
	 * Loads the MP4ES_Descriptor from the input bitstream.
	 * @param bitstream the input bitstream
	 */
	public void createES_Descriptor(DataStream bitstream) throws IOException {
		bitstream.readBytes(2);
		int flags = (int)bitstream.readBytes(1);
		boolean streamDependenceFlag = (flags & (1 << 7)) != 0;
		boolean urlFlag = (flags & (1 << 6)) != 0;
		boolean ocrFlag = (flags & (1 << 5)) != 0;
		readed += 3; 
		if(streamDependenceFlag) {
			bitstream.skipBytes(2);
			readed += 2;
		}
		if(urlFlag) {
			int str_size = (int)bitstream.readBytes(1);
			bitstream.readString(str_size);
			readed += str_size + 1;
		}
		if(ocrFlag) {
			bitstream.skipBytes(2);
			readed += 2;
		}
		while(readed < size) {
			MP4Descriptor descriptor = createDescriptor(bitstream);
			children.addElement(descriptor);
			readed += descriptor.getReaded();
		}
	}
	
	/**
	 * Loads the MP4DecoderConfigDescriptor from the input bitstream.
	 * @param bitstream the input bitstream
	 */
	public void createDecoderConfigDescriptor(DataStream bitstream) throws IOException {
		bitstream.readBytes(1);
		int value = (int)bitstream.readBytes(1);
		value = (int)bitstream.readBytes(2);
		int bufferSizeDB = value << 8; 
		value = (int)bitstream.readBytes(1);
		bufferSizeDB |= value & 0xff; 
		bitstream.readBytes(4);
		bitstream.readBytes(4);
		readed += 13;
		if(readed < size) {
			MP4Descriptor descriptor = createDescriptor(bitstream);
			children.addElement(descriptor);
			readed += descriptor.getReaded();
		}
	}

	
	protected int decSpecificDataSize;
	protected long decSpecificDataOffset;
	
	/**
	 * Loads the MP4DecSpecificInfoDescriptor from the input bitstream.
	 * @param bitstream the input bitstream
	 */
	public void createDecSpecificInfoDescriptor(DataStream bitstream) throws IOException {
			decSpecificDataOffset = bitstream.getOffset();
			decSpecificDataSize = size - readed;
	}
	
	public long getDecSpecificDataOffset() {
		return decSpecificDataOffset;
	}

	public int getDecSpecificDataSize() {
		return decSpecificDataSize;
	}

	/**
	 * Lookups for a child descriptor with the specified <code>type</code>, skips the <code>number</code> 
	 * children with the same type before finding a result. 
	 * @param type the type of the descriptor.
	 * @param number the number of child descriptors to skip
	 * @return the descriptor which was being searched. 
	 */	
	public MP4Descriptor lookup(int type, int number) {
		int position = 0; 
		for(int i = 0; i < children.size(); i++) {
			MP4Descriptor descriptor = (MP4Descriptor)children.elementAt(i);
			if(descriptor.getType() == type) {
				if(position >= number) {
					return descriptor;
				}
				position ++;
			}
		}
		return null;
	}

	/**
	 * Returns the type of this descriptor.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Gets the number of data bytes which were readed from the stream;
	 * @return the number of readed data bytes.
	 */
	public int getReaded() {
		return readed;
	}

}
