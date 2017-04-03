/**
 * 
 */
package com.cisco.slingshot.net.rtp;



/**
 * @author zhiqli
 *
 */
public final class RtpVideoCodec {
    public  int type;

    /**
     * The encoding parameters to be used in the corresponding SDP attribute.
     */
    public  String rtpmap;

    /**
     * The format parameters to be used in the corresponding SDP attribute.
     */
    public  String fmtp;
 
	public static final RtpVideoCodec V_H264 = new RtpVideoCodec(
	/*profile-level-id = byte0  byte1  byte2
	 * byte0 :  profile_idc 
	 * 						0x42(66) indicated baseline profile
	 * 						0x4d(77) indicated main profile
	 * byte1 :  profile-iop\
	 * byte2 :  level_idc
	 * 	
	 * */
			/*framesize:98 640-480*/
			/*profile-level-id=4DA041;sprop-parameter-sets=Z0IACpZTBYmI,aMljiA==*/
	/*profile-level-id=428016;max-br=5000;max-mbps=245000;max-fs=9000;max-smbps=245000;packetization-mode=1;max-fps=6000*/
	//		98, "H264/90000" , null	
			97, "H264/90000",
			"profile-level-id=420016;max-br=5000;max-mbps=245000;max-fs=9000;max-smbps=245000;packetization-mode=1;max-fps=6000"

	//		"profile-level-id=428016;max-br=5000;max-mbps=245000;max-fs=9000;packetization-mode=1;max-fps=6000"
		);

	/*pc linphone format*/
	public static final RtpVideoCodec V_VP8 = new RtpVideoCodec(
			99, "VP8/90000" , null	
		);

	public static final RtpVideoCodec V_MP4ES = new RtpVideoCodec(
			100, "MP4-ES/90000" , "profile-level-id=3"	
		);

	public static final RtpVideoCodec V_H263_1998 = new RtpVideoCodec(
			101, "H263-1998/90000" , null	
		);
	
	
	public static final RtpVideoCodec V_H263_1996 = new RtpVideoCodec(
			34, "H263/90000" , null	
		);
	
	public static final RtpVideoCodec VIDEOCODEC [] = {
		V_H264 
	};
		
    public RtpVideoCodec(int type, String rtpmap, String fmtp) {
        this.type = type;
        this.rtpmap = rtpmap;
        this.fmtp = fmtp;
    }

    public static RtpVideoCodec getCodec(int type, String rtpmap, String fmtp) {
        if (type < 0 || type > 127) {
            return null;
        }

        RtpVideoCodec hint = null;
        if (rtpmap != null) {
            String clue = rtpmap.trim().toUpperCase();
            for (RtpVideoCodec codec : VIDEOCODEC) {
                if (clue.startsWith(codec.rtpmap)) {
                    String channels = clue.substring(codec.rtpmap.length());
                    if (channels.length() == 0 || channels.equals("/1")) {
                        hint = codec;
                        hint.type = type;
                    }
                    break;
                }
            }
        } else if (type < 96) {
            for (RtpVideoCodec codec : VIDEOCODEC) {
                if (type == codec.type) {
                    hint = codec;
                    hint.type = type;
                    rtpmap = codec.rtpmap;
                    break;
                }
            }
        }

        if (hint == null) {
            return null;
        }
        return new RtpVideoCodec(hint.type, hint.rtpmap, hint.fmtp);
    }
    
}
