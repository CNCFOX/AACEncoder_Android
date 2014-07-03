LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := libstagefright
LOCAL_SRC_FILES := libstagefright.so
LOCAL_C_INCLUDES:=$(JNI_H_INCLUDE)
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libdl
LOCAL_SRC_FILES := libdl.so
LOCAL_C_INCLUDES:=$(JNI_H_INCLUDE)
include $(PREBUILT_SHARED_LIBRARY)