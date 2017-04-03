#define LOG_TAG "SLINGSHOT_JNI"

#include "config.h"
#include <jni.h>
#include <JNIHelp.h>

#include "TsPlayerBridge.h"

#define EXPORT __attribute__((visibility("default")))

const char* JAVA_TSPLAYER_CLASSPATH = "com/cisco/slingshot/test/TsPlayer";
/*
private native boolean  _SetVideoWindow(int x,int y,int width,int height);
private native boolean  _StartPlay();
private native boolean  _Pause();
private native boolean  _Resume();
private native boolean  _Fast();
private native boolean  _StopFast();
private native boolean  _Seek();
private native boolean  _SetVolume(int volume);
private native int      _GetVolume();
private native boolean  _SetRatio(int ratio);
private native int      _GetAudioBalance();
private native boolean  _SetAudioBalance(int nAudioBalance); 
private native boolean  _IsSoftFit();
private native void     _SetEPGSize(int w, int h);
*/

TsPlayerBridge* gTsPlayerBridge;


static jboolean nativeSetVideoWindow(JNIEnv *env, jobject obj, jint x, jint y, jint w, jint h){
    SLINGSHOT_LOGD("==== nativeSetVideoWindow ====");
	if(gTsPlayerBridge!=NULL){
		return gTsPlayerBridge->SetVideoWindow( x, y, w, h);
	}
    return false;
}

static jboolean  nativeStartPlay(JNIEnv *env, jobject obj){
    SLINGSHOT_LOGD("==== nativeStartPlay ====");
	
	if(gTsPlayerBridge!=NULL){
		return gTsPlayerBridge->StartPlay();
	}
	
    return false;
}
static jboolean  nativePause(JNIEnv *env, jobject obj){
    SLINGSHOT_LOGD("==== nativePause ====");
	if(gTsPlayerBridge!=NULL){
		return gTsPlayerBridge->Pause();
	}	
    return false;
}
static jboolean  nativeResume(JNIEnv *env, jobject obj){
    SLINGSHOT_LOGD("==== nativeResume ====");
	if(gTsPlayerBridge!=NULL){
		return gTsPlayerBridge->Resume();
	}	
    return false;
}
static jboolean  nativeFast(JNIEnv *env, jobject obj){
    SLINGSHOT_LOGD("==== nativeFast ====");
	if(gTsPlayerBridge!=NULL){
		return gTsPlayerBridge->Fast();
	}

	return false;

}
static jboolean  nativeStopFast(JNIEnv *env, jobject obj){
    SLINGSHOT_LOGD("==== nativeStopFast ====");
	if(gTsPlayerBridge!=NULL){
		return gTsPlayerBridge->StopFast();
	}

	return false;

}

static jboolean  nativeStop(JNIEnv *env, jobject obj){
    SLINGSHOT_LOGD("==== nativeStop ====");
	if(gTsPlayerBridge!=NULL){
		return gTsPlayerBridge->Stop();
	}

	return false;

}

static jboolean  nativeSeek(JNIEnv *env, jobject obj){
    SLINGSHOT_LOGD("==== nativeSeek ====");
	if(gTsPlayerBridge!=NULL){
		return gTsPlayerBridge->Seek();
	}

	return false;

}
static jboolean  nativeSetVolume(JNIEnv *env, jobject obj, jint volume){
    SLINGSHOT_LOGD("==== nativeSetVolume ====");
	if(gTsPlayerBridge!=NULL){
		return gTsPlayerBridge->SetVolume(volume);
	}

	return false;

}
static jint      nativeGetVolume(JNIEnv *env, jobject obj){
    SLINGSHOT_LOGD("==== nativeGetVolume ====");
	if(gTsPlayerBridge!=NULL){
		return gTsPlayerBridge->GetVolume();
	}

	return 0;

}
static jboolean  nativeSetRatio(JNIEnv *env, jobject obj,jint ratio){
    SLINGSHOT_LOGD("==== nativeSetRatio ====");
	if(gTsPlayerBridge!=NULL){
		return gTsPlayerBridge->SetRatio(ratio);
	}

	return false;

}
static jint      nativeGetAudioBalance(JNIEnv *env, jobject obj){
    SLINGSHOT_LOGD("==== nativeGetAudioBalance ====");
	if(gTsPlayerBridge!=NULL){
		return gTsPlayerBridge->GetAudioBalance();
	}

	return 2;

}
static jboolean  nativeSetAudioBalance(JNIEnv *env, jobject obj,jint nAudioBalance){
    SLINGSHOT_LOGD("==== nativeSetAudioBalance ====");
	if(gTsPlayerBridge!=NULL){
		return gTsPlayerBridge->SetAudioBalance(nAudioBalance);
	}

	return false;

}
static jboolean  nativeIsSoftFit(JNIEnv *env, jobject obj){
    SLINGSHOT_LOGD("==== nativesSoftFit ====");
	if(gTsPlayerBridge!=NULL){
		return gTsPlayerBridge->IsSoftFit();
	}

	return false;

}
static void      nativeSetEPGSize(JNIEnv *env, jobject obj,jint w, jint h){
    SLINGSHOT_LOGD("==== nativeSetEPGSize ====");
	if(gTsPlayerBridge!=NULL){
		 gTsPlayerBridge->SetEPGSize( w, h);
	}

}

 


//  the native methods list to  register of TsPlayer
static JNINativeMethod  gJavaMethods[]  =
{
    { "_SetVideoWindow", "(IIII)Z", (void *) nativeSetVideoWindow},
    { "_StartPlay", "()Z", (void *) nativeStartPlay},
    { "_Pause", "()Z", (void *) nativePause},
    { "_Resume", "()Z", (void *) nativeResume},
    { "_Fast", "()Z", (void *) nativeFast},
    { "_StopFast", "()Z", (void *) nativeStopFast},
    { "_Seek", "()Z", (void *) nativeSeek},
    { "_SetVolume", "(I)Z", (void *) nativeSetVolume},
    { "_GetVolume", "()I" ,(void *) nativeGetVolume},
    { "_SetRatio", "(I)Z", (void *) nativeSetRatio},
    { "_GetAudioBalance", "()I", (void *) nativeGetAudioBalance},
    { "_SetAudioBalance", "(I)Z", (void *) nativeSetAudioBalance},
    { "_IsSoftFit", "()Z", (void *) nativeIsSoftFit},
    { "_SetEPGSize", "(II)V", (void *) nativeSetEPGSize},
	{ "_Stop", "()Z", (void *) nativeStop}
    
};
 

EXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
    SLINGSHOT_LOGE("CUIYUAN_DEBUG, JNI_OnLoad");

    JNIEnv *env = NULL;

    if (vm->GetEnv((void * *) &env, JNI_VERSION_1_4) != JNI_OK)
    {
        return -1;
    }

    jniRegisterNativeMethods(env, JAVA_TSPLAYER_CLASSPATH, gJavaMethods, NELEM(gJavaMethods));

	gTsPlayerBridge = TsPlayerBridge::getTsPlayerBridge(); 
	if(gTsPlayerBridge == NULL){
		SLINGSHOT_LOGE("TsPlayerBridge::getTsPlayerBridge() failed!!");
	}


    return JNI_VERSION_1_4;
}



