package nl.digitalekabeltelevisie.data.mpeg.pes.video26x.sei;

import nl.digitalekabeltelevisie.data.mpeg.pes.video26x.sei.PicTimingSei_message.Timestamp;
import org.junit.Test;
import nl.digitalekabeltelevisie.util.BitSource;

import static org.junit.Assert.*;

public class PicTimingSei_messageTest {

	@Test
	public void testPicStruct3() {
		byte[] bytes = new byte[]{
			0x01, 0x01,	// payload type and size
			0x32};
		BitSource bits = new BitSource(bytes, 0);
		PicTimingSei_message message = new PicTimingSei_message(bits);

		assertEquals(3, (long)message.getPicStruct());
		Timestamp ts = message.getTimestamps().get(0);
		assertEquals(0, ts.getClockTimestampFlag());
		// and nothing more
	}

	@Test
	public void testPicStructWithTime() {
		byte[] bytes = new byte[]{
			0x01, 0x09, // payload type and size
			0x08, 0x04,
			0x14, (byte)0xA3, (byte)0xB6, (byte)0x80, 0x00, 0x00, 0x40};
		BitSource bits = new BitSource(bytes, 0);
		PicTimingSei_message message = new PicTimingSei_message(bits);

		assertNull(message.getCpbRemovalDelay());
		assertNull(message.getDpbOutputDelay());
		assertEquals(0, (long)message.getPicStruct());
		Timestamp ts = message.getTimestamps().get(0);
		assertEquals(1, ts.getClockTimestampFlag());
		assertEquals(1, ts.getFullTimestampFlag());
		assertEquals(20, ts.getFrames());
		assertEquals(40, ts.getSeconds());
		assertEquals(59, ts.getMinutes());
		assertEquals(13, ts.getHours());
	}

	@Test
	public void testPicStructWithDelayAndTime() {
		byte[] bytes = new byte[]{
			0x01, 0x0f,	// payload type and size
			0x12, 0x34, 0x56, 0x45, 0x67, (byte)0x89,	// delays (24 bits each)
			0x08, 0x04,
			0x14, (byte)0xA3, (byte)0xB6, (byte)0x80, 0x00, 0x00, 0x40};
		BitSource bits = new BitSource(bytes, 0);
		PicTimingSei_message message = new PicTimingSei_message(bits);

		assertEquals(0x123456L, (long)message.getCpbRemovalDelay());
		assertEquals(0x456789L, (long)message.getDpbOutputDelay());
		assertEquals(0, (long)message.getPicStruct());
		Timestamp ts = message.getTimestamps().get(0);
		assertEquals(1, ts.getClockTimestampFlag());
		assertEquals(1, ts.getFullTimestampFlag());
		assertEquals(20, ts.getFrames());
		assertEquals(40, ts.getSeconds());
		assertEquals(59, ts.getMinutes());
		assertEquals(13, ts.getHours());
	}
}
