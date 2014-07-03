LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
    com_hsae_audiorecorder_aac.c \
    cmnMemory.c

LOCAL_MODULE_TAGS := debug

LOCAL_MODULE := AACEnc

LOCAL_ARM_MODE := arm

LOCAL_LDLIBS := -llog

LOCAL_SHARED_LIBRARIES := \
    libstagefright \
    libdl

#LOCAL_C_INCLUDES := \
 #   $(LOCAL_PATH)/ \
 #   $(LOCAL_PATH)/../../common \
 #   $(LOCAL_PATH)/../../common/include \

#include $(BUILD_EXECUTABLE)
include $(BUILD_SHARED_LIBRARY) 

include $(LOCAL_PATH)/share/Android.mk
