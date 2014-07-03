LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE :=lib_turkeyzhu_AAC
LOCAL_SRC_FILES := lib_turkeyzhu_AAC.so
LOCAL_C_INCLUDES:=$(JNI_H_INCLUDE)
include $(PREBUILT_SHARED_LIBRARY)