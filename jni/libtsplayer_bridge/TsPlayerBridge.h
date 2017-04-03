#ifndef TSPLAYERBRIGHE_H
#define TSPLAYERBRIGHE_H

 /*
 private native boolean  _SetVideoWindow(int x,int y,int width,int height);
 private native boolean  _StartPlay();
 private native boolean  _Pause();
 private native boolean  _Resume();
 private native boolean  _Fast();
 private native boolean  _StopFast();
 private native boolean  _Seek();
 private native boolean  _SetVolume(int volume);
 private native int 	 _GetVolume();
 private native boolean  _SetRatio(int ratio);
 private native int 	 _GetAudioBalance();
 private native boolean  _SetAudioBalance(int nAudioBalance); 
 private native boolean  _IsSoftFit();
 private native void	 _SetEPGSize(int w, int h);
 */
 

/*
typedef bool (*TsPlayer_Bridge_SetVideoWindow_func)(int x,int y,int width,int height);
typedef bool (*TsPlayer_Bridge_StartPlay_func)();	
typedef bool (*TsPlayer_Bridge_Pause_func)();
typedef bool (*TsPlayer_Bridge_Resume_func)();
typedef bool (*TsPlayer_Bridge_Fast_func)();
typedef bool (*TsPlayer_Bridge_Seek_func)();
typedef bool (*TsPlayer_Bridge_SetVolume_func)(int volume);
typedef int  (*TsPlayer_Bridge_GetVolume_func)();
typedef bool (*TsPlayer_Bridge_SetRatio_func)(int ratio);
typedef int (*TsPlayer_Bridge_GetAudioBalance_func)();
typedef bool(*TsPlayer_Bridge_SetAudioBalance_func)(int nAudioBalance);
typedef bool(*TsPlayer_Bridge_IsSoftFit_func)();
typedef void(*TsPlayer_Bridge_SetEPGSize_func)(int w, int h);
*/
#include "TsPlayer.h"

//class ITsPlayer;

typedef ITsPlayer*(*TsPlayer_get_func)();


class TsPlayerBridge{
public:
	static TsPlayerBridge* getTsPlayerBridge();
	static void* feedData(void* arg);

	~TsPlayerBridge();

  	bool SetVideoWindow(int x,int y,int width,int height);
   	bool StartPlay();
   	bool Pause();
   	bool Resume();
   	bool Fast();
   	bool StopFast();
   	bool Seek();
   	bool SetVolume(int volume);
   	int  GetVolume();
   	bool SetRatio(int ratio);
   	int  GetAudioBalance();
   	bool SetAudioBalance(int nAudioBalance); 
   	bool IsSoftFit();
   	void SetEPGSize(int w, int h);	
	bool Stop();
	int WriteData(unsigned char* pBuffer, unsigned int nSize);

	
	//for test
	void tryInitTsPlayer();


private:
	TsPlayerBridge();
	
	class FeedDataProcess{
		typedef void*(*ProcessFunc)(void*);
	public:
		FeedDataProcess(ProcessFunc proc);
		void run();
		void stop();
	
	private:
		FeedDataProcess();
		pthread_t mThread; 
		ProcessFunc mProc;
		
	};

	
	void* 				mDlHandle;
	TsPlayer_get_func 	mTsPlayer_get_func;
	ITsPlayer* 			mTsPlayer;
	FeedDataProcess*    mFeedDataProcess;

	
	/*
	TsPlayer_Bridge_SetVideoWindow_func mTsPlayer_Bridge_SetVideoWindow_func;
	TsPlayer_Bridge_StartPlay_func mTsPlayer_Bridge_StartPlay_func;
	TsPlayer_Bridge_Pause_func mTsPlayer_Bridge_Pause_func;
	TsPlayer_Bridge_Resume_func mTsPlayer_Bridge_Resume_func;
	TsPlayer_Bridge_Fast_func mTsPlayer_Bridge_Fast_func;
	TsPlayer_Bridge_Seek_func mTsPlayer_Bridge_Seek_func;
	TsPlayer_Bridge_SetVolume_func mTsPlayer_Bridge_SetVolume_func;
	TsPlayer_Bridge_GetVolume_func mTsPlayer_Bridge_GetVolume_func;
	TsPlayer_Bridge_SetRatio_func mTsPlayer_Bridge_SetRatio_func;
	TsPlayer_Bridge_GetAudioBalance_func mTsPlayer_Bridge_GetAudioBalance_func;

	TsPlayer_Bridge_SetAudioBalance_func mTsPlayer_Bridge_SetAudioBalance_func;
	TsPlayer_Bridge_IsSoftFit_func mTsPlayer_Bridge_IsSoftFit_func;
	TsPlayer_Bridge_SetEPGSize_func mTsPlayer_Bridge_SetEPGSize_func;
	*/
	

};





#endif
