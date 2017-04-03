package com.cisco.slingshot.test;

import android.util.Log;


public class TsPlayer{

	private final  static String LOG_TAG = "SS_TsPlayer";
 
	static{
		Log.w(LOG_TAG, "System.loadLibrary");
		System.loadLibrary("tsplayer_bridge");
	}


	private String mProgramUrl ;

	public static  TsPlayer newInstance(){
		return new TsPlayer();
	}

	private TsPlayer(){}


	

	/*
	virtual int  GetPlayMode();    \
	virtual int  SetVideoWindow(int x,int y,int width,int height);	  \
	virtual int  SetColorKey(int enable,int key565);	\
	virtual int  VideoShow(void);	 \
	virtual int  VideoHide(void);	 \
	virtual void InitVideo(PVIDEO_PARA_T pVideoPara);	 \
	virtual void InitAudio(PAUDIO_PARA_T pAudioPara);	 \
	virtual bool StartPlay();	 \
	virtual int WriteData(unsigned char* pBuffer, unsigned int nSize);	  \
	virtual bool Pause();	 \
	virtual bool Resume();	  \
	virtual bool Fast();	\
	virtual bool StopFast();	\
	virtual bool Stop();	\
	virtual bool Seek();	\
	virtual bool SetVolume(int volume);    \
	virtual int GetVolume();	\
	virtual bool SetRatio(int nRatio);	  \
	virtual int GetAudioBalance();	  \
	virtual bool SetAudioBalance(int nAudioBalance);	\
	virtual void GetVideoPixels(int& width, int& height);	 \
	virtual bool IsSoftFit();	 \
	virtual void SetEPGSize(int w, int h);
	*/


	public void setProgramUrl(String url){
		mProgramUrl = url;
	}

	public boolean 	SetVideoWindow(int x,int y,int width,int height){
		return _SetVideoWindow(x,y,width,height);

	}
 
	public boolean 	StartPlay(){
		return _StartPlay();

	} 
	public boolean 	Pause(){return _Pause();}
	public boolean 	Resume(){return _Resume();}
	public boolean 	Fast(){return _Fast();}
	public boolean 	StopFast(){return _StopFast();}
	public boolean 	Seek(){return _Seek();}
	public boolean 	SetVolume(int volume){return _SetVolume(volume);}
	public int 		GetVolume(){return _GetVolume();}
	public boolean  Stop(){return _Stop();}
	public boolean  Release(){return _Release();}
	public boolean  Init(){return _Init();}
	public boolean	SetRatio(int ratio){return _SetRatio(ratio);}
	public int 		GetAudioBalance(){return _GetAudioBalance();}
	public boolean 	SetAudioBalance(int nAudioBalance){return _SetAudioBalance(nAudioBalance);}
	public boolean 	IsSoftFit(){return _IsSoftFit();}
	public void 	SetEPGSize(int w, int h){_SetEPGSize(w,h);}	
	
	public int     TestShell(){return _TestShell();}
	
	//jni native funtion
	private native boolean 	_SetVideoWindow(int x,int y,int width,int height);
	private native boolean 	_StartPlay();
	private native boolean 	_Pause();
	private native boolean 	_Resume();
	private native boolean 	_Fast();
	private native boolean 	_StopFast();
	private native boolean 	_Seek();
	private native boolean 	_SetVolume(int volume);
	private native int 		_GetVolume();
	private native boolean  _Stop();
	private native boolean  _Release();
	private native boolean  _Init();
	private native boolean 	_SetRatio(int ratio);
	private native int 		_GetAudioBalance();
	private native boolean 	_SetAudioBalance(int nAudioBalance);
	private native boolean 	_IsSoftFit();
	private native void 	_SetEPGSize(int w, int h);
	private native int      _TestShell();
	



}
