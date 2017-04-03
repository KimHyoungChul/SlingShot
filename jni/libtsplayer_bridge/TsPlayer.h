#ifndef _TSPLAYER_H_
#define _TSPLAYER_H_
#include <stdio.h>
#include <stdlib.h>
extern "C" {
#include "vformat.h"
#include "aformat.h"
}
#include <surfaceflinger/Surface.h>
#include <surfaceflinger/ISurface.h>
using namespace android;

#define TRICKMODE_NONE       0x00
#define TRICKMODE_I          0x01
#define TRICKMODE_FFFB       0x02

typedef struct{
	unsigned short	pid;//pid
	int				nVideoWidth;//��Ƶ���
	int				nVideoHeight;//��Ƶ�߶�
	int				nFrameRate;//֡��
	vformat_t		vFmt;//��Ƶ��ʽ
	unsigned long	cFmt;//�����ʽ
}VIDEO_PARA_T, *PVIDEO_PARA_T;
typedef struct{
	unsigned short	pid;//pid
	int				nChannels;//������
	int				nSampleRate;//������
	aformat_t		aFmt;//��Ƶ��ʽ
	int				nExtraSize;
	unsigned char*	pExtraData;	
}AUDIO_PARA_T, *PAUDIO_PARA_T;

class CTsPlayer;

class ITsPlayer{
public:
	ITsPlayer(){}
	virtual ~ITsPlayer(){}
public:
	//ȡ�ò���ģʽ
	virtual int  GetPlayMode()=0;
	//��ʾ����
	virtual int  SetVideoWindow(int x,int y,int width,int height)=0;
	//16λɫ����Ҫ����colorkey��͸����Ƶ��
	virtual int  SetColorKey(int enable,int key565)=0;
	//x��ʾ��Ƶ
	virtual int  VideoShow(void)=0;
	//������Ƶ
	virtual int  VideoHide(void)=0;
	//��ʼ����Ƶ����
	virtual void InitVideo(PVIDEO_PARA_T pVideoPara)=0;
	//��ʼ����Ƶ����
	virtual void InitAudio(PAUDIO_PARA_T pAudioPara)=0;
	//��ʼ����
	virtual bool StartPlay()=0;
	//��ts��д��
	virtual int WriteData(unsigned char* pBuffer, unsigned int nSize)=0;
	//��ͣ
	virtual bool Pause()=0;
	//��������
	virtual bool Resume()=0;
	//�������
	virtual bool Fast()=0;
	//ֹͣ�������
	virtual bool StopFast()=0;
	//ֹͣ
	virtual bool Stop()=0;
    //��λ
    virtual bool Seek()=0;
    //�趨����
	//�趨����
	virtual bool SetVolume(int volume)=0;
	//��ȡ����
	virtual int GetVolume()=0;
	//�趨��Ƶ��ʾ����
	virtual bool SetRatio(int nRatio)=0;
	//��ȡ��ǰ����
	virtual int GetAudioBalance()=0;
	//��������
	virtual bool SetAudioBalance(int nAudioBalance)=0;
	//��ȡ��Ƶ�ֱ���
	virtual void GetVideoPixels(int& width, int& height)=0;
	virtual bool IsSoftFit()=0;
	virtual void SetEPGSize(int w, int h)=0;
};



class CTsPlayer : public ITsPlayer{
public:
	CTsPlayer();
	virtual ~CTsPlayer();
public:
	//ȡ�ò���ģʽ
	virtual int  GetPlayMode();
	//��ʾ����
	virtual int  SetVideoWindow(int x,int y,int width,int height);
	//16λɫ����Ҫ����colorkey��͸����Ƶ��
	virtual int  SetColorKey(int enable,int key565);
	//x��ʾ��Ƶ
	virtual int  VideoShow(void);
	//������Ƶ
	virtual int  VideoHide(void);
	//��ʼ����Ƶ����
	virtual void InitVideo(PVIDEO_PARA_T pVideoPara);
	//��ʼ����Ƶ����
	virtual void InitAudio(PAUDIO_PARA_T pAudioPara);
	//��ʼ����
	virtual bool StartPlay();
	//��ts��д��
	virtual int WriteData(unsigned char* pBuffer, unsigned int nSize);
	//��ͣ
	virtual bool Pause();
	//��������
	virtual bool Resume();
	//�������
	virtual bool Fast();
	//ֹͣ�������
	virtual bool StopFast();
	//ֹͣ
	virtual bool Stop();
    //��λ
    virtual bool Seek();
    //�趨����
	//�趨����
	virtual bool SetVolume(int volume);
	//��ȡ����
	virtual int GetVolume();
	//�趨��Ƶ��ʾ����
	virtual bool SetRatio(int nRatio);
	//��ȡ��ǰ����
	virtual int GetAudioBalance();
	//��������
	virtual bool SetAudioBalance(int nAudioBalance);
	//��ȡ��Ƶ�ֱ���
	virtual void GetVideoPixels(int& width, int& height);
	virtual bool IsSoftFit();
	virtual void SetEPGSize(int w, int h);
};

ITsPlayer* GetTsPlayer();
void SetSurface(Surface* pSurface);

#endif
