LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libimageBlur_jni
LOCAL_SRC_FILES := com_wb_launcher3_jni_ImageBlur.cpp
LOCAL_LDFLAGS	:= -lm -llog -ljnigraphics
LOCAL_SDK_VERSION := 9
LOCAL_ARM_MODE := arm

LOCAL_MODULE_TAGS := optional
include $(BUILD_SHARED_LIBRARY)
