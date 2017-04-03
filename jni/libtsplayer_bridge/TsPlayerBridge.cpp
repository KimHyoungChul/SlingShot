#define LOG_TAG "SLINGSHOT_JNI"
 
#include "config.h"
#include <dlfcn.h>
#include <sys/types.h>
#include <unistd.h>
#include <utils/Log.h>
#include <pthread.h>
#include <fcntl.h>


#include "TsPlayerBridge.h"

const int VIDEO_WIDTH = 640;
const int VIDEO_HEIGHT = 480;
const int BUFFER_LENGTH = 188 * 1000;
void* TsPlayerBridge::feedData(void* arg){
	
    SLINGSHOT_LOGD("Start feed data...");

	unsigned char buffer[BUFFER_LENGTH + 1];
    memset(buffer, 0,BUFFER_LENGTH + 1);
	
    FILE *fp = NULL;
    FILE *fp_out = NULL;
    fp = fopen("/test3.ts","rb");
    if(fp == NULL){
       SLINGSHOT_LOGE("open file failed");
    }

    int loop = 0;
    while(1){ 
        SLINGSHOT_LOGD("Start loop %d", loop);
        
        size_t count =  fread(buffer, BUFFER_LENGTH, 1,fp);
        SLINGSHOT_LOGD("count: %d", count);
        SLINGSHOT_LOGD("Content buffer[0]: %x", buffer[0]);
        SLINGSHOT_LOGD("Content buffer[1]: %x", buffer[1]);
        SLINGSHOT_LOGD("Content buffer[2]: %x", buffer[2]);
        SLINGSHOT_LOGD("Content buffer[3]: %x", buffer[3]);
        SLINGSHOT_LOGD("Content buffer[4]: %x", buffer[4]);
       
       // fwrite(buffer,BUFFER_LENGTH,1,fp_out);
        TsPlayerBridge::getTsPlayerBridge()->WriteData(buffer,BUFFER_LENGTH);
        usleep(5000);

		 /*
	        if(loop >= 100){
	            break;
	        }
	        loop++;
	        */
        
    }
     LOGI("stop feed data...");
     fclose(fp);
     pthread_exit(0);
     return NULL;

}


TsPlayerBridge* TsPlayerBridge::getTsPlayerBridge(){

	static TsPlayerBridge* tsPlayer = NULL;

	if(tsPlayer == NULL){
		tsPlayer = new TsPlayerBridge();
	}
	return tsPlayer;

}


TsPlayerBridge::TsPlayerBridge()
	:mDlHandle(NULL),
	 mTsPlayer(NULL),
	 mTsPlayer_get_func(NULL),
	 mFeedDataProcess(NULL)


{
	//load libTsPlayer.so
 	SLINGSHOT_LOGD("open  libTsPlayer.so"); 
    mDlHandle = dlopen("hw/libTsPlayer.so", RTLD_NOW);
    if(NULL == mDlHandle){
        SLINGSHOT_LOGD("dlopen libTsPlayer.so failed because %s\n", dlerror());
        return;
    }
    else
        SLINGSHOT_LOGE("dlopen libTsPlayer.so handle is %p\n",mDlHandle);    

    
    mTsPlayer_get_func    =    reinterpret_cast<TsPlayer_get_func>(dlsym(mDlHandle, "GetTsPlayer"));
	if(mTsPlayer_get_func != NULL){
		SLINGSHOT_LOGD("mTsPlayer_get_func success!! Get mTsPlayer");
		mTsPlayer = (*mTsPlayer_get_func)();
		if(mTsPlayer == NULL){
			SLINGSHOT_LOGE("Get TsPlayer failded!!");
		}else{
			SLINGSHOT_LOGD("Get TsPlayer success!!");
			tryInitTsPlayer();			

		}
	}else{
		SLINGSHOT_LOGD("mTsPlayer_get_func failed!!");

	}

}

TsPlayerBridge::~TsPlayerBridge(){
	if(mDlHandle!=NULL){
		dlclose(mDlHandle);
		mDlHandle = NULL;
	}
	if(mTsPlayer){
		delete mTsPlayer;
		mTsPlayer = NULL;
	}

	if(mFeedDataProcess){
		mFeedDataProcess->stop();
		delete mFeedDataProcess;
		mFeedDataProcess = NULL;
	}
	
}
void TsPlayerBridge::tryInitTsPlayer(){
	SLINGSHOT_LOGD("Try to init Ts Player by use of default param");

	
	if(!mTsPlayer)
		return;

	/*init video*/
        PVIDEO_PARA_T pVideo_param = (PVIDEO_PARA_T)malloc(sizeof(VIDEO_PARA_T));
        memset(pVideo_param, 0 ,sizeof(VIDEO_PARA_T));
        pVideo_param->pid = 0x161;
        pVideo_param->cFmt = 0;
        pVideo_param->nFrameRate = 30;
        pVideo_param->nVideoHeight = VIDEO_HEIGHT;
        pVideo_param->nVideoWidth = VIDEO_WIDTH;
        pVideo_param->vFmt = VFORMAT_H264;
        
        mTsPlayer->InitVideo(pVideo_param);

        /*init audio*/
        PAUDIO_PARA_T pAudio_param = (PAUDIO_PARA_T)malloc(sizeof(AUDIO_PARA_T));
        memset(pAudio_param, 0 ,sizeof(AUDIO_PARA_T));
        pAudio_param->pid = 0x162;
        pAudio_param->nChannels = 2;
        pAudio_param->aFmt = FORMAT_AC3;
        pAudio_param->nSampleRate = 48000;
        pAudio_param->nExtraSize = 0;        
        pAudio_param->pExtraData = 0; 
    
        mTsPlayer->InitAudio(pAudio_param);	

		free(pVideo_param);
        free(pAudio_param);
	
}


int TsPlayerBridge::WriteData(unsigned char* pBuffer, unsigned int nSize){
	if(mTsPlayer != NULL){
		return mTsPlayer->WriteData( pBuffer, nSize);
	}
	return 0;
}

bool TsPlayerBridge::SetVideoWindow(int x,int y,int width,int height){
	if(mTsPlayer != NULL){
		return mTsPlayer->SetVideoWindow( x, y, width, height);
	}
	return false;
}
bool TsPlayerBridge::StartPlay(){

	
	if(mTsPlayer != NULL){
        mTsPlayer->SetColorKey(1,0);
        mTsPlayer->VideoShow();

		mTsPlayer->StartPlay();
		//start feed data
		mFeedDataProcess = new FeedDataProcess(feedData);
		mFeedDataProcess->run();
		 
	}
	return false;	
}
bool TsPlayerBridge::Pause(){
	if(mTsPlayer != NULL){
		return mTsPlayer->Pause();
	}
	return false;

}
bool TsPlayerBridge::Resume(){
	if(mTsPlayer != NULL){
		return mTsPlayer->Resume();
	}
	return false;

}
bool TsPlayerBridge::Fast(){
	if(mTsPlayer != NULL){

		return mTsPlayer->Fast();
	}
	return false;

}
bool TsPlayerBridge::StopFast(){
	if(mTsPlayer != NULL){
		return mTsPlayer->StopFast();
	}
	return false;

}

bool TsPlayerBridge::Stop(){
	if(mTsPlayer != NULL){
				//test
		if(mFeedDataProcess){
			mFeedDataProcess->stop();
		}
		return mTsPlayer->Stop();
	}
	return false;


}

bool TsPlayerBridge::Seek(){
	if(mTsPlayer != NULL){
		return mTsPlayer->Seek();
	}
	return false;

}
bool TsPlayerBridge::SetVolume(int volume){
	if(mTsPlayer != NULL){
		return mTsPlayer->SetVolume(volume);
	}
	return false;

}
int  TsPlayerBridge::GetVolume(){
	if(mTsPlayer != NULL){
		return mTsPlayer->GetVolume();
	}
	return 0;

}
bool TsPlayerBridge::SetRatio(int ratio){
	if(mTsPlayer != NULL){
		return mTsPlayer->SetRatio(ratio);
	}
	return false;

}
int  TsPlayerBridge::GetAudioBalance(){
	if(mTsPlayer != NULL){
		return mTsPlayer->GetAudioBalance();
	}
	return 0;

}
bool TsPlayerBridge::SetAudioBalance(int nAudioBalance){
	if(mTsPlayer != NULL){
		return mTsPlayer->SetAudioBalance(nAudioBalance);
	}
	return false;

}
bool TsPlayerBridge::IsSoftFit(){
	if(mTsPlayer != NULL){
		return mTsPlayer->IsSoftFit();
	}
	return false;

}
void TsPlayerBridge::SetEPGSize(int w, int h){
	if(mTsPlayer != NULL){
		mTsPlayer->SetEPGSize( w,h);
	}
}


TsPlayerBridge::FeedDataProcess::FeedDataProcess(ProcessFunc proc)
	:mProc(proc)
{
}

void TsPlayerBridge::FeedDataProcess::run(){
	SLINGSHOT_LOGD("Start to feed data ...");
	//init pthread;
    pthread_attr_t attr;
    pthread_attr_init(&attr);
    pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_JOINABLE);
    pthread_create(&mThread, &attr, mProc, 0);
    pthread_attr_destroy(&attr);	
}


void TsPlayerBridge::FeedDataProcess::stop(){
	
}


