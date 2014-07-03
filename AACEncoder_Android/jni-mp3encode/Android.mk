ROOT_PATH := $(call my-dir)

# make speex and lame
LOCAL_PATH := $(call my-dir)

LOCAL_LDLIBS    := -llog

LOCAL_SHARED_LIBRARIES := lib_hslab_aac
	
LOCAL_MODULE    :=_hs_aac
LOCAL_MODULE_FILENAME := lib_hs_aac


include $(BUILD_SHARED_LIBRARY)
include $(LOCAL_PATH)/libAAC/Android.mk

