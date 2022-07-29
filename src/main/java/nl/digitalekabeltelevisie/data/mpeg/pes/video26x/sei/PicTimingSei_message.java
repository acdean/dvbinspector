package nl.digitalekabeltelevisie.data.mpeg.pes.video26x.sei;

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
	private int clockTimestampFlag;
	private int ctType;
	private int nuitFieldBasedFlag;
	private int countingType;
	private int fullTimestampFlag;
	private int discontinuityFlag;
	private int cntDroppedFlag;
	private int frames, seconds, minutes, hours;

	public PicTimingSei_message(BitSource bitSource) {
		super(bitSource);

		// bitSource has been read by super(), so now convert payload back into BitSource
		var bitSourcePayload = new BitSource(payload, 0);

		System.out.println("Payload: " + payload.length);
		if (payload.length == 1) {
			// the test has a malformed pic_timing sei_message
			logger.warning("Short pic_timing. Assuming seconds.");
			frames = bitSourcePayload.u(8);
			return;
		}

		if (cpbDpbDelaysPresentFlag) {
			cpbRemovalDelay = bitSourcePayload.u(cpb_removal_delay_length_minus1 + 1);
			dpbOutputDelay = bitSourcePayload.u(dpb_output_delay_length_minus1 + 1);
		}
		if (picStructPresentFlag) {
			picStruct = bitSourcePayload.u(4);
			clockTimestampFlag = bitSourcePayload.f(1);
			ctType = bitSourcePayload.u(2);
			nuitFieldBasedFlag = bitSourcePayload.f(1);
			countingType = bitSourcePayload.u(5);
			fullTimestampFlag = bitSourcePayload.f(1);
			discontinuityFlag = bitSourcePayload.f(1);
			cntDroppedFlag = bitSourcePayload.f(1);
			frames = bitSourcePayload.u(8);
			System.out.println("frames: " + frames);
			seconds = bitSourcePayload.u(6);
			System.out.println("seconds: " + seconds);
			minutes = bitSourcePayload.u(6);
			System.out.println("minutes: " + minutes);
			hours = bitSourcePayload.u(5);
			System.out.println("hours: " + hours);
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
			s.add(new DefaultMutableTreeNode(new KVP("picStruct", picStruct, null)));
			s.add(new DefaultMutableTreeNode(new KVP("clockTimestampFlag", clockTimestampFlag, null)));
			s.add(new DefaultMutableTreeNode(new KVP("ctType", ctType, null)));
			s.add(new DefaultMutableTreeNode(new KVP("nuitFieldBasedFlag", nuitFieldBasedFlag, null)));
			s.add(new DefaultMutableTreeNode(new KVP("countingType", countingType, null)));
			s.add(new DefaultMutableTreeNode(new KVP("fullTimestampFlag", fullTimestampFlag, null)));
			s.add(new DefaultMutableTreeNode(new KVP("discontinuityFlag", discontinuityFlag, null)));
			s.add(new DefaultMutableTreeNode(new KVP("cntDroppedFlag", cntDroppedFlag, null)));
			s.add(new DefaultMutableTreeNode(new KVP("frames", frames, null)));
			s.add(new DefaultMutableTreeNode(new KVP("seconds", seconds, null)));
			s.add(new DefaultMutableTreeNode(new KVP("minutes", minutes, null)));
			s.add(new DefaultMutableTreeNode(new KVP("hours", hours, null)));
		}

		return s;
	}
}
