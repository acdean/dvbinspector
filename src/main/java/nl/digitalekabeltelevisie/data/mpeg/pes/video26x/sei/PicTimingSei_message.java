package nl.digitalekabeltelevisie.data.mpeg.pes.video26x.sei;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.tree.DefaultMutableTreeNode;

import nl.digitalekabeltelevisie.controller.KVP;
import nl.digitalekabeltelevisie.util.BitSource;

/*
** based on Rec. ITU-T H.264 (10/2016) D.1.3 pic_timing SEI message syntax
** NB there are assumptions made here and it doesn't read the entire sei_message
*/

public class PicTimingSei_message extends Sei_message {

	private static final Logger	logger	= Logger.getLogger(PicTimingSei_message.class.getName());

	// these are from elsewhere. how do i access them here?
	private Boolean cpbDpbDelaysPresentFlag = false;	// from sps 
	private int cpb_removal_delay_length_minus1 = 23;	// H.264 10/2016 E.1.2
	private int dpb_output_delay_length_minus1 = 23;	// H.264 10/2016 E.1.2
	private Boolean picStructPresentFlag = true;		// H.264 10/2016 E.1.1

	private Integer cpbRemovalDelay, dpbOutputDelay;
	private Integer picStruct;
	private final List<Timestamp> clockTimestamps = new ArrayList<>();

	public PicTimingSei_message(BitSource bitSource) {
		super(bitSource);

		// bitSource has been read by super(), so now convert payload back into BitSource
		var bitSourcePayload = new BitSource(payload, 0);

		// guess whether delays exist based on message size
		cpbDpbDelaysPresentFlag = (payload.length > 9);

		if (cpbDpbDelaysPresentFlag) {
			cpbRemovalDelay = bitSourcePayload.u(cpb_removal_delay_length_minus1 + 1);
			dpbOutputDelay = bitSourcePayload.u(dpb_output_delay_length_minus1 + 1);
		}
		if (picStructPresentFlag) {
			picStruct = bitSourcePayload.u(4);
			for (int i = 0 ; i < getNumberOfTimestamps(picStruct) ; i++) {
				Timestamp ts = new Timestamp();
				ts.clockTimestampFlag = bitSourcePayload.f(1);
				if (ts.clockTimestampFlag == 1) {
					ts.ctType = bitSourcePayload.u(2);
					ts.nuitFieldBasedFlag = bitSourcePayload.f(1);
					ts.countingType = bitSourcePayload.u(5);
					ts.fullTimestampFlag = bitSourcePayload.f(1);
					ts.discontinuityFlag = bitSourcePayload.f(1);
					ts.cntDroppedFlag = bitSourcePayload.f(1);
					ts.frames = bitSourcePayload.u(8);
					if (ts.getFullTimestampFlag() == 1) {
						ts.seconds = bitSourcePayload.u(6);
						ts.minutes = bitSourcePayload.u(6);
						ts.hours = bitSourcePayload.u(5);
					}
				}
				clockTimestamps.add(ts);
			}
		}
	}

	// H.264 (10/2016) Table D.1
	private int getNumberOfTimestamps(int picStruct) {
		switch(picStruct) {
			case 0:	// progressive
			case 1:	// top field
			case 2:	// bottom field
				return 1;
			case 3:	// top then bottom
			case 4:	// bottom then top
			case 7:	// frame doubling
				return 2;
			case 5:	// top, bottom, top
			case 6:	// bottom, top, bottom
			case 8:	// frame tripling
				return 3;
			default:	// reserved
				return 0;
		}
	}

	// H.264 (10/2016) Table D.1
	String describePicStruct(int picStruct) {
		switch (picStruct) {
			case 0:
				return "Progressive Frame";
			case 1:
				return "Top Field";
			case 2:
				return "Bottom Field";
			case 3:
				return "Top Field, Bottom Field";
			case 4:
				return "Bottom Field, Top Field";
			case 5:
				return "Top, Bottom, Top";
			case 6:
				return "Bottom, Top, Bottom";
			case 7:
				return "Frame Doubling";
			case 8:
				return "Framte Tripling";
			default:
				return "reserved";
		}
	}

	@Override
	public DefaultMutableTreeNode getJTreeNode(final int modus) {
		final DefaultMutableTreeNode s = super.getJTreeNode(modus);
		if (cpbRemovalDelay != null) {
			s.add(new DefaultMutableTreeNode(new KVP("cpbRemovalDelay", cpbRemovalDelay, null)));
		}
		if (dpbOutputDelay != null) {
			s.add(new DefaultMutableTreeNode(new KVP("dpbOutputDelay", dpbOutputDelay, null)));
		}
		if (picStructPresentFlag) {
			s.add(new DefaultMutableTreeNode(new KVP("picStruct", picStruct, describePicStruct(picStruct))));
			for (Timestamp ts : clockTimestamps) {
				s.add(new DefaultMutableTreeNode(new KVP("clockTimestampFlag", ts.clockTimestampFlag, null)));
				if (ts.clockTimestampFlag == 1) {
					s.add(new DefaultMutableTreeNode(new KVP("ctType", ts.ctType, null)));
					s.add(new DefaultMutableTreeNode(new KVP("nuitFieldBasedFlag", ts.nuitFieldBasedFlag, null)));
					s.add(new DefaultMutableTreeNode(new KVP("countingType", ts.countingType, null)));
					s.add(new DefaultMutableTreeNode(new KVP("fullTimestampFlag", ts.fullTimestampFlag, null)));
					s.add(new DefaultMutableTreeNode(new KVP("discontinuityFlag", ts.discontinuityFlag, null)));
					s.add(new DefaultMutableTreeNode(new KVP("cntDroppedFlag", ts.cntDroppedFlag, null)));
					s.add(new DefaultMutableTreeNode(new KVP("frames", ts.frames, null)));
					s.add(new DefaultMutableTreeNode(new KVP("seconds", ts.seconds, null)));
					s.add(new DefaultMutableTreeNode(new KVP("minutes", ts.minutes, null)));
					s.add(new DefaultMutableTreeNode(new KVP("hours", ts.hours, null)));
				}
			}
		}

		return s;
	}

	public Boolean getCpbDpbDelaysPresentFlag() {
		return cpbDpbDelaysPresentFlag;
	}

	public int getCpb_removal_delay_length_minus1() {
		return cpb_removal_delay_length_minus1;
	}

	public int getDpb_output_delay_length_minus1() {
		return dpb_output_delay_length_minus1;
	}

	public Boolean getPicStructPresentFlag() {
		return picStructPresentFlag;
	}

	public Integer getCpbRemovalDelay() {
		return cpbRemovalDelay;
	}

	public Integer getDpbOutputDelay() {
		return dpbOutputDelay;
	}

	public Integer getPicStruct() {
		return picStruct;
	}

	public List<Timestamp> getTimestamps() {
		return clockTimestamps;
	}

	public class Timestamp {
		private int clockTimestampFlag;
		private int ctType;
		private int nuitFieldBasedFlag;
		private int countingType;
		private int fullTimestampFlag;
		private int discontinuityFlag;
		private int cntDroppedFlag;
		private int frames, seconds, minutes, hours;

		public int getClockTimestampFlag() {
			return clockTimestampFlag;
		}

		public int getCtType() {
			return ctType;
		}

		public int getNuitFieldBasedFlag() {
			return nuitFieldBasedFlag;
		}

		public int getCountingType() {
			return countingType;
		}

		public int getFullTimestampFlag() {
			return fullTimestampFlag;
		}

		public int getDiscontinuityFlag() {
			return discontinuityFlag;
		}

		public int getCntDroppedFlag() {
			return cntDroppedFlag;
		}

		public int getFrames() {
			return frames;
		}

		public int getSeconds() {
			return seconds;
		}

		public int getMinutes() {
			return minutes;
		}

		public int getHours() {
			return hours;
		}
	}
}
