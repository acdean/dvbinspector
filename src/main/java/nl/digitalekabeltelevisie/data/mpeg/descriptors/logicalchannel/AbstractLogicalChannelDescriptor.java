/**
 *
 *  http://www.digitalekabeltelevisie.nl/dvb_inspector
 *
 *  This code is Copyright 2009-2022 by Eric Berendsen (e_berendsen@digitalekabeltelevisie.nl)
 *
 *  This file is part of DVB Inspector.
 *
 *  DVB Inspector is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DVB Inspector is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with DVB Inspector.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  The author requests that he be notified of any application, applet, or
 *  other binary that makes use of this code, but that's more out of curiosity
 *  than anything and is not required.
 *
 */

package nl.digitalekabeltelevisie.data.mpeg.descriptors.logicalchannel;

import static nl.digitalekabeltelevisie.util.Utils.addListJTree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import nl.digitalekabeltelevisie.controller.KVP;
import nl.digitalekabeltelevisie.data.mpeg.descriptors.Descriptor;
import nl.digitalekabeltelevisie.data.mpeg.descriptors.DescriptorContext;
import nl.digitalekabeltelevisie.data.mpeg.psi.TableSection;
import nl.digitalekabeltelevisie.gui.TableSource;
import nl.digitalekabeltelevisie.util.tablemodel.FlexTableModel;
import nl.digitalekabeltelevisie.util.tablemodel.TableHeader;
import nl.digitalekabeltelevisie.util.tablemodel.TableHeaderBuilder;

/**
 * @author Eric
 *
 */
public abstract class AbstractLogicalChannelDescriptor extends Descriptor implements TableSource{
	
	
	public abstract class AbstractLogicalChannel implements LogicalChannelInterface{
		
		public AbstractLogicalChannel(int service_id, int visible_service_flag, int reserved,
				int logical_channel_number) {
			super();
			this.service_id = service_id;
			this.visible_service_flag = visible_service_flag;
			this.reserved = reserved;
			this.logical_channel_number = logical_channel_number;
		}

		protected final int service_id;
		protected final int visible_service_flag;
		protected final int reserved;
		protected final int logical_channel_number;
		
		
		@Override
		public int getService_id() {
			return service_id;
		}

		@Override
		public int getVisible_service_flag() {
			return visible_service_flag;
		}

		@Override
		public int getReserved() {
			return reserved;
		}

		@Override
		public int getLogical_channel_number() {
			return logical_channel_number;
		}
		
		public String getServiceName() {
			return findServiceName(service_id);
		}

		@Override
		public DefaultMutableTreeNode getJTreeNode(final int modus) {
		
			final DefaultMutableTreeNode s = new DefaultMutableTreeNode(new KVP(createNodeLabel(service_id, logical_channel_number)));
			s.add(new DefaultMutableTreeNode(new KVP("service_id", service_id, null)));
			s.add(new DefaultMutableTreeNode(new KVP("visible_service_flag", visible_service_flag, null)));
			s.add(new DefaultMutableTreeNode(new KVP("reserved", reserved, null)));
			s.add(new DefaultMutableTreeNode(new KVP("logical_channel_number", logical_channel_number, null)));
			return s;
		}


	}

	public final DescriptorContext descriptorContext;
	protected final List<AbstractLogicalChannel> channelList = new ArrayList<>();


	public AbstractLogicalChannelDescriptor(byte[] b, int offset, TableSection parent, DescriptorContext descriptorContext) {
		super(b, offset, parent);
		this.descriptorContext = descriptorContext;

	}

	public String findServiceName(int service_id) {
		return getPSI().getSdt().getServiceName(descriptorContext.original_network_id, descriptorContext.transport_stream_id, service_id);
	}

	public String createNodeLabel(int service_id, int logical_channel_number) {
		if (descriptorContext.hasOnidTsid()) {
			String serviceName = findServiceName(service_id);
			if (serviceName != null) {
				return "logical_channel (" + serviceName + "): " + logical_channel_number;
			}
		}
		return "logical_channel  " + logical_channel_number;
	}
	
	@Override
	public DefaultMutableTreeNode getJTreeNode(final int modus){

		final DefaultMutableTreeNode t = super.getJTreeNode(modus, this);
		addListJTree(t,channelList,modus,"logical_channels",this);
		return t;
	}



	@Override
	public String toString() {
		final StringBuilder buf = new StringBuilder(super.toString());
		for (final LogicalChannelInterface s : channelList) {
			buf.append(s.toString());
		}


		return buf.toString();
	}

	public int getNoServices(){
		return channelList.size();
	}


	public List<AbstractLogicalChannel> getChannelList() {
		return channelList;
	}

	private static TableHeader<AbstractLogicalChannel,AbstractLogicalChannel>  buildTableHeader() {

		return new TableHeaderBuilder<AbstractLogicalChannel,AbstractLogicalChannel>().
				addRequiredRowColumn("service_id", AbstractLogicalChannel::getService_id, Integer.class).
				addRequiredRowColumn("logical_channel_number", AbstractLogicalChannel::getLogical_channel_number, Integer.class).
				addOptionalRowColumn("service name", AbstractLogicalChannel::getServiceName, String.class).
				build();
	}


	public TableModel getTableModel() {
		FlexTableModel<AbstractLogicalChannel,AbstractLogicalChannel> tableModel =  new FlexTableModel<>(buildTableHeader());

		for(AbstractLogicalChannel ch:channelList) {
			tableModel.addData(ch, List.of(ch));
		}

		tableModel.process();
		return tableModel;
	}


}
