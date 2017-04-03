REFSW_PATH :=vendor/broadcom/bcm${BCHP_CHIP}/brcm_nexus
LOCAL_PATH:= $(call my-dir)

# Nexus multi-process, client-server related CFLAGS
MP_CFLAGS = -DANDROID_CLIENT_SECURITY_MODE=$(ANDROID_CLIENT_SECURITY_MODE)

ifeq ($(ANDROID_SUPPORTS_NEXUS_MP),y)
MP_CFLAGS += -DANDROID_SUPPORTS_NEXUS_MP=1 
else
MP_CFLAGS += -DANDROID_SUPPORTS_NEXUS_MP=0 
endif

ifeq ($(ANDROID_SUPPORTS_SERVER_SIDE_DISPLAY),y)
MP_CFLAGS += -DANDROID_SUPPORTS_SERVER_SIDE_DISPLAY=1
else
MP_CFLAGS += -DANDROID_SUPPORTS_SERVER_SIDE_DISPLAY=0
endif

ifeq ($(ANDROID_SUPPORTS_NSC),y)
MP_CFLAGS += -DANDROID_SUPPORTS_NSC=1
else
MP_CFLAGS += -DANDROID_SUPPORTS_NSC=0
endif

ifeq ($(ANDROID_SUPPORTS_API_OVER_BINDER),y)
MP_CFLAGS += -DANDROID_SUPPORTS_API_OVER_BINDER=1
else
MP_CFLAGS += -DANDROID_SUPPORTS_API_OVER_BINDER=0
endif 

include $(REFSW_PATH)/bin/include/platform_app.inc

include $(CLEAR_VARS)

LOCAL_PRELINK_MODULE := false
LOCAL_MODULE_PATH := $(TARGET_OUT_SHARED_LIBRARIES)/hw
LOCAL_MODULE_TAGS := eng

LOCAL_SHARED_LIBRARIES := liblog libcutils libbinder libutils libnexusservice libb_os libb_playback_ip libnexus libnativehelper libdl
ifeq ($(ANDROID_SUPPORTS_API_OVER_BINDER),y)
LOCAL_SHARED_LIBRARIES += libnexusipcclient
endif

LOCAL_C_INCLUDES += $(REFSW_PATH)/bin/include \
					$(REFSW_PATH)/../libnexusservice
ifeq ($(ANDROID_SUPPORTS_API_OVER_BINDER),y)
LOCAL_C_INCLUDES += $(REFSW_PATH)/../libnexusipc					
endif


LOCAL_MODULE    := libtsplayer_bridge
#LOCAL_CFLAGS    := -Werror -Wl,-rpath,./ 
#LOCAL_CFLAGS += -I../amcodec/include
#LOCAL_CFLAGS += -Wno-multichar
#LOCAL_CXXFLAGS := -DHAVE_SYS_UIO_H
#LOCAL_PRELINK_MODULE := false

LOCAL_SRC_FILES := \
	TsPlayer.cpp \
	jni_main.cpp \
	TsPlayerBridge.cpp \
	
#LOCAL_LDLIBS    := -lc -llog 
LOCAL_CFLAGS:= $(NEXUS_CFLAGS) -DANDROID  -DFEATURE_GPU_ACCEL_H264_BRCM -DFEATURE_AUDIO_HWCODEC

#include $(BUILD_STATIC_LIBRARY)
include $(BUILD_SHARED_LIBRARY)
#include $(BUILD_EXECUTABLE)


