package nl.digitalekabeltelevisie.data.mpeg.pes.video26x.sei;

import java.math.BigInteger;
import java.util.logging.Logger;

import javax.swing.tree.DefaultMutableTreeNode;

import nl.digitalekabeltelevisie.controller.KVP;
import nl.digitalekabeltelevisie.util.BitSource;

/*
** based on Rec. ITU-T H.265 v8 (08/2021) D.2.27 Time Code SEI message syntax.
*/

public class TimeCodeSei_message extends Sei_message {

	private static final Logger	logger	= Logger.getLogger(TimeCodeSei_message.class.getName());

	private final int numClockTs;
	private final int clockTimestampFlag;
	private final int unitsFieldBasedFlag;
	private final int countingType;
	private final int fullTimestampFlag;
	private final int disconinuityFlag;
	private final int cntDroppedFlag;
	private final int frames;
	private final int seconds;
	private final int minutes;
	private final int hours;
	private final int timeOffsetLength;
	private final Integer timeOffsetValue;

	public TimeCodeSei_message(BitSource bitSource) {
		super(bitSource);

		// bitSource has been read by super(), so now convert payload back into BitSource
		var bitSourcePayload = new BitSource(payload, 0);

		numClockTs = bitSourcePayload.u(2);
		clockTimestampFlag = bitSourcePayload.u(1);
		unitsFieldBasedFlag = bitSourcePayload.u(1);
		countingType = bitSourcePayload.u(5);
		fullTimestampFlag = bitSourcePayload.u(1);
		disconinuityFlag = bitSourcePayload.u(1);
		cntDroppedFlag = bitSourcePayload.u(1);
		frames = bitSourcePayload.u(9);
		seconds = bitSourcePayload.u(6);
		minutes = bitSourcePayload.u(6);
		hours = bitSourcePayload.u(5);
		timeOffsetLength = bitSourcePayload.u(5);
		if (timeOffsetLength > 0) {
			timeOffsetValue = bitSourcePayload.u(timeOffsetLength);
		} else {
			timeOffsetValue = null;
		}
	}

	@Override
	public DefaultMutableTreeNode getJTreeNode(final int modus) {
		final DefaultMutableTreeNode s = super.getJTreeNode(modus);
		s.add(new DefaultMutableTreeNode(new KVP("numClockTs", numClockTs, null)));
		s.add(new DefaultMutableTreeNode(new KVP("clockTimestampFlag", clockTimestampFlag, null)));
		s.add(new DefaultMutableTreeNode(new KVP("unitsFieldBasedFlag", unitsFieldBasedFlag, null)));
		s.add(new DefaultMutableTreeNode(new KVP("countingType", countingType, null)));
		s.add(new DefaultMutableTreeNode(new KVP("fullTimestampFlag", fullTimestampFlag, null)));
		s.add(new DefaultMutableTreeNode(new KVP("disconinuityFlag", disconinuityFlag, null)));
		s.add(new DefaultMutableTreeNode(new KVP("cntDroppedFlag", cntDroppedFlag, null)));
		s.add(new DefaultMutableTreeNode(new KVP("frames", frames, null)));
		s.add(new DefaultMutableTreeNode(new KVP("seconds", seconds, null)));
		s.add(new DefaultMutableTreeNode(new KVP("minutes", minutes, null)));
		s.add(new DefaultMutableTreeNode(new KVP("hours", hours, null)));
		s.add(new DefaultMutableTreeNode(new KVP("hhmmssff", String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, frames), null)));
		s.add(new DefaultMutableTreeNode(new KVP("timeOffsetLength", timeOffsetLength, null)));
		if (timeOffsetValue != null) {
			s.add(new DefaultMutableTreeNode(new KVP("timeOffsetValue", timeOffsetValue, null)));
		}

		return s;
	}
}
