#define LOG_TAG "TsPlayer"
#include "TsPlayer.h"
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <utils/Log.h>
//#include "../IPTVPlayer/PubAndroid.h"	 
#define DPrint(x)

/*nexus api*/
#include "nexus_hdmi_output.h"
#include "nexus_platform.h"
#include "nexus_video_decoder.h"
#include "nexus_stc_channel.h"
#include "nexus_display.h"
#include "nexus_video_window.h"
#include "nexus_video_input.h"
#include "nexus_video_decoder_trick.h"
#include "nexus_video_decoder_types.h"
#include "nexus_audio_decoder.h"
#include "nexus_audio_input.h"
#include "nexus_audio_output.h"
#include "nexus_audio_decoder_trick.h"
#include "nexus_audio_decoder_types.h"
#include "nexus_core_utils.h"



#include "nexus_message.h"
#include "nexus_component_output.h"
#include "nexus_playpump.h"
#if NEXUS_DTV_PLATFORM
#include "nexus_platform_boardcfg.h"
#endif

#if 1
#include <binder/IPCThreadState.h>
#include <binder/ProcessState.h>
#include <binder/IServiceManager.h>
#endif

#include "nexus_interface.h"
#include "nexusservice.h"

#include <stdio.h>
#include <string.h>
#include <assert.h>
#include "bstd.h"
#include "bkni.h"
#include "bkni_multi.h"

/*nexus struct*/

NEXUS_PlaypumpSettings playpumpSettings;
NEXUS_PlaypumpHandle playpump;
BKNI_EventHandle event;
NEXUS_StcChannelHandle stcChannel;
NEXUS_StcChannelSettings stcSettings;
NEXUS_PidChannelHandle videoPidChannel;
NEXUS_DisplayHandle display;
NEXUS_VideoWindowHandle window;
NEXUS_VideoDecoderHandle videoDecoder;
NEXUS_VideoDecoderStartSettings videoProgram;
NEXUS_PlatformSettings platformSettings;
NEXUS_PlatformConfiguration platformConfig;
NEXUS_DisplaySettings displaySettings;
NEXUS_PlaypumpOpenSettings playpumpopensettings;
NEXUS_VideoDecoderOpenSettings videodecoderopensettings;
NEXUS_AudioDecoderStartSettings audioProgram;
NEXUS_AudioDecoderHandle audioDecoder, audioPassthrough;

NEXUS_VideoDecoderTrickState trickSettings;
NEXUS_AudioDecoderTrickState audioState;

NEXUS_VideoOutput  videooutput;
NEXUS_VideoWindowSettings  windowSettings;
NEXUS_GraphicsSettings  graphicsSettings;
NEXUS_VideoFormatInfo   info;

NEXUS_VideoDecoderStatus vstatus;
NEXUS_PlaypumpStatus pstatus;
NEXUS_AudioDecoderStatus astatus;
NEXUS_DisplaySettings displaysettings;

NEXUS_AudioOutputSettings OutputSettings;
NEXUS_AudioDecoderSettings DecoderSettings;


NEXUS_Error rc;

typedef struct{
	unsigned short	pid;//pid
	int				nVideoWidth;//视频宽度
	int				nVideoHeight;//视频高度
	int				nFrameRate;//帧率
	NEXUS_VideoCodec codec;//视频格式
	unsigned long	cFmt;//编码格式
}nexus_video_p;
typedef struct{
	unsigned short	pid;//pid
	int				nChannels;//声道数
	int				nSampleRate;//采样率
	NEXUS_AudioCodec codec;//音频格式
	int				nExtraSize;
	unsigned char*	pExtraData;	
}nexus_audio_p;
nexus_video_p           pVideoformat;
nexus_audio_p           pAudioformat;

/*namespace android {  */


#if (ANDROID_SUPPORTS_NEXUS_MP == 0)
class BpNexusClient: public android::BpInterface<INexusClient>
{
public:
    BpNexusClient(const android::sp<android::IBinder>& impl)
            : android::BpInterface<INexusClient>(impl)
    {
    }

    void NexusHandles(NEXUS_TRANSACT_ID eTransactId, int32_t *pHandle)
    {
        android::Parcel data, reply;
        data.writeInterfaceToken(INexusService::getInterfaceDescriptor());
        remote()->transact(eTransactId, data, &reply);
        *pHandle = reply.readInt32();
    }
};
android_IMPLEMENT_META_INTERFACE(NexusClient, NEXUS_INTERFACE_NAME)
#endif 

android::sp<android::IServiceManager>   sm;
android::sp<android::IBinder>			binder;
android::sp<INexusClient>			   iNC;

#ifndef FBIOPUT_OSD_SRCCOLORKEY
#define  FBIOPUT_OSD_SRCCOLORKEY    0x46fb
#endif

#ifndef FBIOPUT_OSD_SRCKEY_ENABLE
#define  FBIOPUT_OSD_SRCKEY_ENABLE  0x46fa
#endif


#ifndef FBIOPUT_OSD_SET_GBL_ALPHA
#define  FBIOPUT_OSD_SET_GBL_ALPHA  0x4500
#endif

static void play_callback(void *context, int param)
{
    BSTD_UNUSED(param);
    BKNI_SetEvent((BKNI_EventHandle)context);
}

static void message_callback(void *context, int param)
{
    BSTD_UNUSED(param);
    
    LOGE("Enter message_callback");
 
}



static void setdefaultpsi(nexus_video_p pVideoformat, nexus_audio_p pAudioformat)
{
    LOGE("\n ...............set default psi ........................\n");
    /*parser video format*/
	pVideoformat.pid=0x1022;
	pVideoformat.codec=NEXUS_VideoCodec_eH264;
	pVideoformat.nVideoWidth=1280;
	pVideoformat.nVideoHeight=720;
	pVideoformat.nFrameRate=30;
    pVideoformat.cFmt=0;
	/*parser audio format*/
	pAudioformat.pid=0x1023;
	pAudioformat.codec=NEXUS_AudioCodec_eMpeg;
	pAudioformat.nChannels=2;
	pAudioformat.nSampleRate=48000;
	pAudioformat.nExtraSize=0;
    pAudioformat.pExtraData=0;
	
	return;
}

CTsPlayer::CTsPlayer()
{
  #if 0
    return; 
#else
    LOGE("\n.......................enter ctsplayer init............................\n");
	NEXUS_ClientConfiguration   clientConfig;

    LOGE("\n..........................join platform ................................\n");
	rc = NEXUS_Platform_Join();
	if(rc!=NEXUS_SUCCESS)
	{
	   LOGE("\n......................NEXUS_Platform_Join Failed.............................\n");
	   sleep(1);
	}
	else
	{
		LOGE("\n.........................NEXUS_Platform_Join Success..............................\n");  
	} 	  

	NEXUS_Platform_GetConfiguration(&platformConfig);

    BKNI_CreateEvent(&event);	
    LOGE("\n.......................Requesting nexus handles from binder..........................\n");
	sm = android::defaultServiceManager();
    binder = sm->getService(android::String16(NEXUS_INTERFACE_NAME));
    if (binder == 0){
        LOGE("\n.........................Nexusservice is not ready, abort...........................");
        return;
    }
    iNC = android::interface_cast<INexusClient>(binder);
	
	NEXUS_Playpump_GetDefaultOpenSettings(&playpumpopensettings);
	LOGE("\n.........................open playpump.............................\n");	
	NEXUS_Platform_GetClientConfiguration(&clientConfig);
    playpumpopensettings.heap = clientConfig.heap[1]; /* playpump requires heap with eFull mapping */ 
    //yuan 
    
	playpumpopensettings.fifoSize *= 2;
	playpumpopensettings.numDescriptors *=2;  
	/*
	playpumpopensettings.numDescriptors = 200;  
	playpumpopensettings.fifoSize=37600*41;
	*/
    playpump = NEXUS_Playpump_Open(0, &playpumpopensettings);
	if(playpump==NULL)
	{
	 LOGE("\n.........................open playpump fail.............................\n");
	 return;
	}
    
	LOGE("\n....................playpump open setting numDescriptors=200............................\n");
	NEXUS_Playpump_GetSettings(playpump, &playpumpSettings);
    playpumpSettings.dataCallback.callback = play_callback;
    playpumpSettings.dataCallback.context = event;
    //yuan
    playpumpSettings.transportType =  NEXUS_TransportType_eTs;  
    playpumpSettings.originalTransportType = NEXUS_TransportType_eTs;
    
    NEXUS_Playpump_SetSettings(playpump, &playpumpSettings);    
    LOGE("\n.......................open stcchannel.............................\n");

    
    NEXUS_StcChannel_GetDefaultSettings(0, &stcSettings);
    stcSettings.timebase = NEXUS_Timebase_e0;
    stcSettings.mode = NEXUS_StcChannelMode_eAuto;
	stcSettings.modeSettings.Auto.behavior=NEXUS_StcChannelAutoModeBehavior_eFirstAvailable;

    //yuan
    stcSettings.modeSettings.Auto.transportType = NEXUS_TransportType_eTs;
  /*  stcSettings.modeSettings.Auto.behavior=NEXUS_StcChannelAutoModeBehavior_eVideoMaster; */
    stcChannel = NEXUS_StcChannel_Open(0, &stcSettings);
    if(stcChannel==NULL)
	{
	 LOGE("\n.........................open stcChannel failed.............................\n");
	 return;
	}	
	LOGE("\n.......................open video decoder......................\n");	
	NEXUS_VideoDecoder_GetDefaultOpenSettings(&videodecoderopensettings);
	videodecoderopensettings.avc51Enabled=true;
	#if 0
	videoDecoder = NEXUS_VideoDecoder_Open(0, &videodecoderopensettings); 
	#else
	videoDecoder = NEXUS_VideoDecoder_Open(0, NULL); 
	#endif
	if(videoDecoder==NULL)
	{
	 LOGE("\n.........................open video decoder handle failed.............................\n");
	 return;
	}	
	LOGE("\n.........................get display handle.....................\n");
	iNC->NexusHandles(GET_HANDLE_NEXUS_DISPLAY, reinterpret_cast<int32_t*>(&display));
	if(display==NULL)
	{
	 LOGE("\n.........................get display handle failed.............................\n");
	 return;
	}


	#if 1
	LOGE("\n...........................get window handle..........................\n");
	iNC->NexusHandles(GET_HANDLE_NEXUS_WINDOW, reinterpret_cast<int32_t*>(&window));
	if(window==NULL)
	{
	 LOGE("\n.........................get window handle failed.............................\n");
	 return;
	}
	NEXUS_VideoWindow_AddInput(window, NEXUS_VideoDecoder_GetConnector(videoDecoder));	
	#endif
	
	LOGE("\n...........................get audio decoder handle..........................\n");
	iNC->NexusHandles(GET_HANDLE_NEXUS_AUDIO_PCMDECODER, reinterpret_cast<int32_t*>(&audioDecoder));
	if(audioDecoder==NULL)
	{
	 LOGE("\n.........................get audio decoder handle failed.............................\n");
	 return;
	}
	
	#if 1
	LOGE("\n.............................set default psi..........................\n");
	setdefaultpsi(pVideoformat,pAudioformat);	
	#else
	LOGE("\n.............................parser the psi...............................\n");
	paserpsiinfo();
        #endif
	LOGE("\n.............................return from parser the psi...............................\n");
        #endif


}

CTsPlayer::~CTsPlayer()
{
  
#if 0
     LOGE("\n........................release ctsplayer................................\n");
     return;   
#else
    LOGE("\n........................release ctsplayer................................\n");
	LOGE("\n........................stop video decoder................................\n");
	if(videoDecoder)
	{	
	 NEXUS_VideoDecoder_Stop(videoDecoder);  
	}
	LOGE("\n........................stop audio decoder................................\n");
	if(audioDecoder)
	{
	 NEXUS_AudioDecoder_Stop(audioDecoder);  
	}
	LOGE("\n........................stop and close playpump................................\n");
	if(playpump)
	{ 
         NEXUS_Playpump_CloseAllPidChannels(playpump);   
	
	 NEXUS_Playpump_Close(playpump);
    }
	LOGE("\n........................close video decoder................................\n");
	if(videoDecoder)
	{
	
	 
	 NEXUS_VideoDecoder_Close(videoDecoder);
	}
	
#endif
}

//取得播放模式,保留，暂不用
int  CTsPlayer::GetPlayMode()
{
	return 1;
}
int CTsPlayer::SetVideoWindow(int x,int y,int width,int height)
{
   
#if 0
LOGE("\n........................set video window...........................\n");
return 0;
#else
     
    LOGE("\n........................set video window...........................\n");
    LOGE("\n x: %d y:%d,width :%d,heigh:%d\n",x,y,width,height);
    
	
    NEXUS_VideoWindow_GetSettings(window, &windowSettings); 
	LOGE("\n windowSettings.position.width %d, windowSettings.position.height %d\n", windowSettings.position.width, windowSettings.position.height);
    windowSettings.position.x = x;
    windowSettings.position.y = y;
#if 1
    windowSettings.position.width = width;
    windowSettings.position.height = height;	
#else
    if(width<info.width-x)
{
     windowSettings.position.width = info.width-x;
}
else
{
    windowSettings.position.width=info.width-x;
}
if(height<info.height-y)
{
   windowSettings.position.height = info.height-y-38;
}
else
{
   windowSettings.position.height =info.height-y;
}
#endif
    rc=NEXUS_VideoWindow_SetSettings(window, &windowSettings);
    if(rc!=NEXUS_SUCCESS)
    {
       LOGE("\n.................set video window failed......................\n");
    }
    LOGE("\n windowSettings.position.width %d, windowSettings.position.height %d\n", windowSettings.position.width, windowSettings.position.height);

	
    return 0;
	
#endif
}


int CTsPlayer::SetColorKey(int enable,int key565)
{
	
#if 0
LOGE("\n........................set color key...........................\n");
return 0;
#else
	//if (m_nOsdBpp != 16)
	//	return 0;
      LOGE("\n........................set color key...........................\n");
    LOGE("\n enable :%d,key565 :%d\n",enable,key565);
	NEXUS_Display_GetGraphicsSettings(display, &graphicsSettings);
    graphicsSettings.chromakeyEnabled= enable;
    graphicsSettings.lowerChromakey= key565;
    graphicsSettings.upperChromakey= key565;
    graphicsSettings.alpha = 0x80;
    rc = NEXUS_Display_SetGraphicsSettings(display, &graphicsSettings);
	int ret = 0;
    return ret;
#endif
}

int CTsPlayer::VideoShow(void)
{
	
#if 0
LOGE("\n........................video show...........................\n");
return 0;
#else
LOGE("\n........................video show...........................\n");

    NEXUS_VideoWindow_GetSettings(window, &windowSettings);  
    windowSettings.visible= true;
    NEXUS_VideoWindow_SetSettings(window, &windowSettings); 
	return 0;
#endif
}
int CTsPlayer::VideoHide(void)
{
	return -1;
}


void CTsPlayer::InitVideo(PVIDEO_PARA_T pVideoPara)
{

	
#if 0
    LOGE("\n........................init video...........................\n");
    return; 
#else
    
    LOGE("\n........................init video...........................\n");
    
    LOGE("\n pVideoPara->vFmt:%d\n",pVideoPara->vFmt);
	pVideoformat.pid=pVideoPara->pid;
	
	pVideoformat.codec=NEXUS_VideoCodec_eH264;
	pVideoformat.nVideoWidth=pVideoPara->nVideoWidth;
	pVideoformat.nVideoHeight=pVideoPara->nVideoHeight;
	pVideoformat.nFrameRate=pVideoPara->nFrameRate;
	pVideoformat.cFmt=pVideoPara->cFmt;

    LOGE("\n...pVideoPara->nVideoWidth %d pVideoPara->nVideoHeight %d\n",pVideoPara->nVideoWidth,pVideoPara->nVideoHeight);
	
	videoPidChannel = NEXUS_Playpump_OpenPidChannel(playpump, pVideoformat.pid, NULL);
	if(videoPidChannel==NULL)
	{
	 LOGE("\n.........................open video Pid Channel failed.............................\n");
	 return;
	}
    NEXUS_VideoDecoder_GetDefaultStartSettings(&videoProgram);
	videoProgram.codec = pVideoformat.codec;
	videoProgram.pidChannel = videoPidChannel;
	videoProgram.stcChannel = stcChannel;  
	#if 1
	videoProgram.errorHandling=NEXUS_VideoDecoderErrorHandling_ePrognostic;  
	videoProgram.prerollRate = 80;
	LOGE("\n.....set preroll rate 80 errorHandling NEXUS_VideoDecoderErrorHandling_ePrognostic..............\n");
	#endif
	
	return ;
#endif
}

void CTsPlayer::InitAudio(PAUDIO_PARA_T pAudioPara)
{
	
#if 0
    LOGE("\n..........................init audio..............................\n");
    return;  
#else
    LOGE("\n..........................init audio..............................\n");
	LOGE("\n pAudioPara->aFmt:%d\n",pAudioPara->aFmt);
	pAudioformat.pid=pAudioPara->pid;
	
	
	if(pAudioPara->aFmt==FORMAT_MPEG )
		{
		  pAudioformat.codec=NEXUS_AudioCodec_eMpeg;
		}
	else if(pAudioPara->aFmt==FORMAT_AAC)
		{
		  pAudioformat.codec=NEXUS_AudioCodec_eAac;
		}
	else if(pAudioPara->aFmt==FORMAT_AC3)
		{
		  pAudioformat.codec=NEXUS_AudioCodec_eAc3;
		}
	
	
	pAudioformat.nChannels=pAudioPara->nChannels;
	pAudioformat.nSampleRate=pAudioPara->nSampleRate;
	pAudioformat.nExtraSize=pAudioPara->nExtraSize;
	memcpy(pAudioformat.pExtraData,pAudioPara->pExtraData,pAudioPara->nExtraSize);
	

	NEXUS_AudioDecoder_GetDefaultStartSettings(&audioProgram);
	audioProgram.codec = pAudioformat.codec;
	audioProgram.pidChannel = NEXUS_Playpump_OpenPidChannel(playpump, pAudioformat.pid, NULL);
	audioProgram.stcChannel = stcChannel;
	if(audioProgram.pidChannel==NULL)
	{
	 LOGE("\n.........................open audio pid channel failed.............................\n");
	 return;
	}


	return ;
#endif
}

bool CTsPlayer::StartPlay()
{
	bool resume;
#if 0
    LOGE("\n.........................start play.......................\n");
    return 0;  
#else
    LOGE("\n.........................start play.......................\n");	


    LOGE("\n..........................start playpump...........................\n");
	rc=NEXUS_Playpump_Start(playpump);
	if(rc!=NEXUS_SUCCESS)
	{
	 LOGE("\n.........................start playpump failed.............................\n");
	 return -1;
	}

	LOGE("\n.......................start video decoder.....................\n");
        if(videoProgram.pidChannel==NULL)
        {
          LOGE("\n.........................videoProgram.pidChannel not open.............................\n");
          
         }
    rc=NEXUS_VideoDecoder_Start(videoDecoder, &videoProgram);
	if(rc!=NEXUS_SUCCESS)
	{
	 LOGE("\n.........................start video decoder failed.............................\n");
	 LOGE("\n rc :%d\n",rc);
         return -1;
	}
	LOGE("\n........................start audio decoder.......................\n");
    rc=NEXUS_AudioDecoder_Start(audioDecoder, &audioProgram);
	if(rc!=NEXUS_SUCCESS)
	{
	 LOGE("\n.........................start audio decoder failed.............................\n");
	 return -1;
	}

	#if 1
    LOGE("\n....................resume stc speed.......................\n");	
	rc=NEXUS_StcChannel_SetRate(stcChannel, NEXUS_NORMAL_DECODE_RATE ,NEXUS_NORMAL_PLAY_SPEED-1); 
		if(rc!=NEXUS_SUCCESS)
		{
		 LOGE("\n.........................set stc channel rate failed.............................\n");
		 return -1;
		}	

     #else
     resume=Resume();
     if(!resume)
     	{
     	 LOGE("\n...............resume settings from trickmode................\n");
     	}

     #endif


	
	return 0;
#endif
}


static void wait(){

    
    while (1)
    {
        NEXUS_Playpump_GetStatus(playpump, &pstatus);
        if (pstatus.fifoDepth == 0)
            break;

        BKNI_Sleep(100);
    }


}

int CTsPlayer::WriteData(unsigned char* pBuffer, unsigned int nSize)
{
	
#if 0
     LOGE("\n......................write data to buffer.........................\n");
     return 0;  
#else
    LOGE("\n......................write data to buffer.........................\n");   
 /*  size_t  ws;   */
 
	if(pBuffer==NULL||!nSize)
	{
	 LOGE("\n no data can write to buffer\n");
	 return -1;
	}

    
	while (1) 
	{
		void *buffer;
        size_t buffer_size;
		int n;
		NEXUS_Error rc;
        
		if (NEXUS_Playpump_GetBuffer(playpump, &buffer, &buffer_size))
            break;
		if (buffer_size == 0) {
            LOGE("\n ==============buffer_size == 0 ====================\n");
            //NEXUS_Playpump_Flush(playpump);
            //BKNI_WaitForEvent(event, BKNI_INFINITE);
            
            continue;
        }
		
		/* The first call to get_buffer will return the entire playback buffer.
        If we use it, we're going to have to wait until the descriptor is complete,
        and then we're going to underflow. So use a max size. */
         #define MAX_READ (188*1024)   
		/*#define MAX_READ (37600) */
        if (buffer_size > MAX_READ)
            buffer_size = MAX_READ;
			
	    LOGE("\n nSize:%d buffer_size:%d\n",nSize,buffer_size);

        
		if (nSize >buffer_size) 
		{

            LOGE("\n ===============nSize >buffer_size ================ \n");
            #if 0
            NEXUS_Playpump_Flush(playpump);
			continue;
			#else
		    memcpy(buffer,pBuffer,buffer_size);		     
		    rc=NEXUS_Playpump_WriteComplete(playpump, 0, buffer_size);  
		    if(rc!=NEXUS_SUCCESS)
	        {
	           LOGE("\n.........................write to playpump buffer failed.............................\n");
	           return -1;
	        }
			else
			{
			   LOGE("\n.........................write to playpump buffer succeed.............................\n");
			   break;
			}
			#endif
		}
		else 
		{
		    memcpy(buffer,pBuffer,nSize);		
		    rc=NEXUS_Playpump_WriteComplete(playpump, 0, nSize);  
		    if(rc!=NEXUS_SUCCESS)
	        {
	           LOGE("\n.........................write to playpump buffer failed.............................\n");
	           return -1;
	        }
			else
			{
			   LOGE("\n.........................write to playpump buffer succeed.............................\n");
		       //break;
			}
		}

        wait();
        break;
      }
		


       

    
	NEXUS_VideoDecoder_GetStatus(videoDecoder, &vstatus);
	NEXUS_Playpump_GetStatus(playpump, &pstatus);
	NEXUS_AudioDecoder_GetStatus(audioDecoder,&astatus);
    
	LOGE("\n state %s decode =%d%%\n",vstatus.started? "started!":"Not started" ,vstatus.fifoSize?(vstatus.fifoDepth*100)/vstatus.fifoSize:0);
	LOGE("\n state %s playpump=%d%%\n",pstatus.started? "started!":"Not started",pstatus.fifoSize?(pstatus.fifoDepth*100)/pstatus.fifoSize:0);
	LOGE("\n state %s audio=%d%%\n",astatus.started? "started!":"Not started",astatus.fifoSize?(astatus.fifoDepth*100)/astatus.fifoSize:0);

	return 1;
#endif
}




bool CTsPlayer::Pause()
{
	
#if 0
    LOGE("\n......................pause.......................\n");
    return 0; 
#else
    LOGE("\n......................pause.......................\n");
	NEXUS_VideoDecoder_GetTrickState(videoDecoder, &trickSettings);
	trickSettings.rate = 0;
	trickSettings.topFieldOnly = true;
	rc=NEXUS_VideoDecoder_SetTrickState(videoDecoder, &trickSettings); 
    if(rc!=NEXUS_SUCCESS)
	{
	 LOGE("\n.........................set video decoder trick state failed.............................\n");
	 return -1;
	}	
	
	NEXUS_AudioDecoder_GetTrickState(audioDecoder, &audioState);
	audioState.rate = 0;
	audioState.muted = true;
	rc=NEXUS_AudioDecoder_SetTrickState(audioDecoder, &audioState);	
    if(rc!=NEXUS_SUCCESS)
	{
	 LOGE("\n.........................set audio decoder trick state failed.............................\n");
	 return -1;
	}		
	rc=NEXUS_StcChannel_SetRate(stcChannel, 0,NEXUS_NORMAL_PLAY_SPEED-1);	
	if(rc!=NEXUS_SUCCESS)
	{
	 LOGE("\n.........................set stc channel rate failed.............................\n");
	 return -1;
	}	

	return 0;
#endif
}

bool CTsPlayer::Resume()
{
	
#if 0
    LOGE("\n..........................resume........................\n");
    return 0;
#else
    LOGE("\n..........................resume........................\n");
	NEXUS_VideoDecoder_GetTrickState(videoDecoder, &trickSettings);
	trickSettings.rate = NEXUS_NORMAL_DECODE_RATE;
	trickSettings.topFieldOnly = false;
	rc=NEXUS_VideoDecoder_SetTrickState(videoDecoder, &trickSettings); 	
    if(rc!=NEXUS_SUCCESS)
	{
	 LOGE("\n.........................set video decoder trick state failed.............................\n");
	 return -1;
	}		
				
	NEXUS_AudioDecoder_GetTrickState(audioDecoder, &audioState);
	audioState.rate = NEXUS_NORMAL_DECODE_RATE;
	audioState.muted = false;	
	rc=NEXUS_AudioDecoder_SetTrickState(audioDecoder, &audioState);   
	if(rc!=NEXUS_SUCCESS)
	{
	 LOGE("\n.........................set audio decoder trick state failed.............................\n");
	 return -1;
	}		
	rc=NEXUS_StcChannel_SetRate(stcChannel, NEXUS_NORMAL_DECODE_RATE ,NEXUS_NORMAL_PLAY_SPEED-1); 
	if(rc!=NEXUS_SUCCESS)
	{
	 LOGE("\n.........................set stc channel rate failed.............................\n");
	 return -1;
	}	
	return 0;
#endif
}

bool CTsPlayer::Fast()
{
	
#if 0
    LOGE("\n.......................fast..........................\n");
    return 0;
#else
    LOGE("\n.......................fast..........................\n");
        LOGE("\n........flush playpump buffer and video decoder buffer...............\n");
   /*     NEXUS_Playpump_Flush(playpump);
        NEXUS_VideoDecoder_Flush(videoDecoder);  */

	NEXUS_VideoDecoder_GetTrickState(videoDecoder, &trickSettings);
	trickSettings.hostTrickModesEnabled = true; 				 
	trickSettings.decodeMode = NEXUS_VideoDecoderDecodeMode_eI;
	trickSettings.tsmEnabled = NEXUS_TsmMode_eDisabled;
	trickSettings.topFieldOnly = true;
	trickSettings.reverseFields = false;
	rc=NEXUS_VideoDecoder_SetTrickState(videoDecoder, &trickSettings);
	if(rc!=NEXUS_SUCCESS)
	{
	 LOGE("\n.........................set video decoder trick state failed.............................\n");
	 return -1;
	}		
	NEXUS_AudioDecoder_GetTrickState(audioDecoder,&audioState);
	audioState.forceStopped=true;
	rc=NEXUS_AudioDecoder_SetTrickState(audioDecoder,&audioState);  
    if(rc!=NEXUS_SUCCESS)
	{
	 LOGE("\n.........................set audio decoder trick state failed.............................\n");
	 return -1;
	}		
	return 0;
#endif
}
bool CTsPlayer::StopFast() 
{
	
#if 0
    LOGE("\n.............................stop fast................................\n");
    return 0;
#else
    LOGE("\n.............................stop fast................................\n");
	NEXUS_VideoDecoder_GetTrickState(videoDecoder, &trickSettings);
	trickSettings.hostTrickModesEnabled = false;				 
	trickSettings.decodeMode = NEXUS_VideoDecoderDecodeMode_eAll;
	trickSettings.tsmEnabled = NEXUS_TsmMode_eEnabled;
	trickSettings.topFieldOnly = false;
	trickSettings.reverseFields = false;
	rc=NEXUS_VideoDecoder_SetTrickState(videoDecoder, &trickSettings);
	if(rc!=NEXUS_SUCCESS)
	{
	 LOGE("\n.........................set video decoder trick state failed.............................\n");
	 return -1;
	}		
				
	NEXUS_AudioDecoder_GetTrickState(audioDecoder,&audioState);
	audioState.forceStopped=false;
	audioState.tsmEnabled=NEXUS_TsmMode_eEnabled;
	audioState.rate=NEXUS_NORMAL_DECODE_RATE;
	rc=NEXUS_AudioDecoder_SetTrickState(audioDecoder,&audioState);   
    if(rc!=NEXUS_SUCCESS)
	{
	 LOGE("\n.........................set audio decoder trick state failed.............................\n");
	 return -1;
	}		
	return 0;
#endif
}
bool CTsPlayer::Stop()
{
	
#if 0
    LOGE("\n...........................stop...........................\n");
    return 0;
#else
    LOGE("\n.........stop videodecoder audiodecoder and playpump...........................\n");
    LOGE("\n...........................stop videodecoder...........................\n");
	NEXUS_VideoDecoder_Stop(videoDecoder);
    LOGE("\n...........................stop audiodecoder...........................\n");
	NEXUS_AudioDecoder_Stop(audioDecoder);
	NEXUS_Playpump_Flush(playpump);
#if 1
    LOGE("\n...........................stop playpump...........................\n");
	NEXUS_Playpump_Stop(playpump);
#endif  
	return 0;
#endif
}
bool CTsPlayer::Seek()
{	
	Stop();
	return StartPlay();
}
int CTsPlayer::GetVolume()
{
    NEXUS_AudioDecoder_GetSettings(audioDecoder,&DecoderSettings);
	return 0;
}
bool CTsPlayer::SetRatio(int nRatio)
{
	return false;
}
bool CTsPlayer::SetVolume(int volume)
{
    NEXUS_AudioDecoder_GetSettings(audioDecoder,&DecoderSettings);
    DecoderSettings.volumeMatrix[NEXUS_AudioChannel_eLeft][NEXUS_AudioChannel_eLeft] = volume;
    DecoderSettings.volumeMatrix[NEXUS_AudioChannel_eRight][NEXUS_AudioChannel_eRight] = volume;
    DecoderSettings.volumeMatrix[NEXUS_AudioChannel_eLeft][NEXUS_AudioChannel_eRight] = 0;
    DecoderSettings.volumeMatrix[NEXUS_AudioChannel_eRight][NEXUS_AudioChannel_eLeft] = 0;
	NEXUS_AudioDecoder_SetSettings(audioDecoder,&DecoderSettings);
	return true;
}
//获取当前声道,1:左声道，2:右声道，3:双声道
int CTsPlayer::GetAudioBalance()
{
    NEXUS_AudioOutput_GetSettings(NEXUS_HdmiOutput_GetAudioConnector(platformConfig.outputs.hdmi[0]),&OutputSettings);
	return 2;
}
//设置声道
//nAudioBlance:,1:左声道，2:右声道，3:双声道
bool CTsPlayer::SetAudioBalance(int nAudioBalance)
{
    NEXUS_AudioOutput_GetSettings(NEXUS_HdmiOutput_GetAudioConnector(platformConfig.outputs.hdmi[0]),&OutputSettings);
    OutputSettings.channelMode = NEXUS_AudioChannelMode_eStereo;
    NEXUS_AudioOutput_SetSettings(NEXUS_HdmiOutput_GetAudioConnector(platformConfig.outputs.hdmi[0]),&OutputSettings);  	
	return true;
}
	//获取视频分辩率
void CTsPlayer::GetVideoPixels(int& width, int& height)
{
#if 0
LOGE("\n.......get video pixels.........\n");
#else
    LOGE("\n.......get video pixels.........\n");
    NEXUS_Display_GetSettings(display, &displaySettings);
    NEXUS_VideoFormat_GetInfo(displaySettings.format, &info);	
	LOGE("\n info.width %d, info.height %d\n", info.width, info.height);
	width=info.width;
	/* height=info.height-36; */
        height=info.height;
	LOGE("\n width %d, height %d\n", width,height);
	#if 0
	width = 1920;
	height = 1080;
	#endif
#endif
}
bool CTsPlayer::IsSoftFit()
{
	return true;
}
void CTsPlayer::SetEPGSize(int w, int h)
{
	
}


ITsPlayer* GetTsPlayer()
{
    LOGE("\n.....................ctsplayer.............................\n");
	return new CTsPlayer();
	LOGE("\n.....................return from ctsplayer.............................\n");
}

void SetSurface(Surface* pSurface)
{
	
#if 1
LOGE("\n.........................get surface handle.............................\n");
return;
#else
  if(pSurface==NULL)
	{
	 LOGE("\n.........................get window handle failed.............................\n");
	 return;
	}
	NEXUS_VideoWindow_AddInput(window, NEXUS_VideoDecoder_GetConnector(videoDecoder));	
#endif
}
