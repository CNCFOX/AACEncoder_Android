#include "libAAC/libSYS/include/aacenc_lib.h"
#include "com_hsae_audiorecorder_aac.h"
#include "f_audiofmt.h"









#define FA_AACENC_MPEG_VER_DEF   1
#define FA_AACENC_OBJ_TYPE_DEF   2
#define CHNMAX          2
#define FRAME_SIZE_MAX  (CHNMAX * 1024) //2048 
#define AAC_FRAME_SIZE  1024



//void fa_aacenc_encode(uintptr_t handle, unsigned char *buf_in, int inlen, unsigned char *buf_out, int *outlen);




static f_fmt_t fmt;

char wavsamples_in[FRAME_SIZE_MAX];
unsigned char aac_buf[FRAME_SIZE_MAX];
uint64_t AACencoderFD;


static lame_global_flags *glf = NULL;

JNIEXPORT jint JNICALL Java_com_hsae_audiorecorder_AACEncoder_init(
		JNIEnv *env, jclass cls, jint inSamplerate, jint outChannel,
		jint outSamplerate, jint outBitrate, jint quality) 
{/*
	if (glf != NULL) {
		lame_close(glf);
		glf = NULL;
	}
	glf = lame_init();
	lame_set_in_samplerate(glf, inSamplerate);
	lame_set_num_channels(glf, outChannel);
	lame_set_out_samplerate(glf, outSamplerate);
	lame_set_brate(glf, outBitrate);
	lame_set_quality(glf, quality);
	lame_init_params(glf);
*/

	int lfe_enable =0;
	int band_width =20;
	int speed_level =2;
	AACencoderFD=0;
	AACencoderFD=fa_aacenc_init(outSamplerate,outBitrate,outChannel,FA_AACENC_MPEG_VER_DEF,FA_AACENC_OBJ_TYPE_DEF,lfe_enable,band_width,speed_level);
	return AACencoderFD;
}



JNIEXPORT jint JNICALL Java_com_hsae_audiorecorder_AACEncoder_encode(
		JNIEnv *env, jclass cls, jint chn_num,jshortArray pcm_buf,jsize pcm_size, jbyteArray mp3buf,jsize aac_len)

{
	jshort* j_buffer_l = (*env)->GetShortArrayElements(env, buffer_l, NULL);

	jshort* j_buffer_r = (*env)->GetShortArrayElements(env, buffer_r, NULL);
	const jsize mp3buf_size = (*env)->GetArrayLength(env, mp3buf);
	jbyte* j_mp3buf = (*env)->GetByteArrayElements(env, mp3buf, NULL);

	int result = lame_encode_buffer(glf, j_buffer_l, j_buffer_r,
			samples, j_mp3buf, mp3buf_size);
	(*env)->ReleaseShortArrayElements(env, buffer_l, j_buffer_l, 0);
	(*env)->ReleaseShortArrayElements(env, buffer_r, j_buffer_r, 0);
	(*env)->ReleaseByteArrayElements(env, mp3buf, j_mp3buf, 0);

	return result;
//	int result=0;
//	jshort* j_buffer = (*env)->GetShortArrayElements(env, pcm_buf, NULL);
//	const jsize aac_len = (*env)->GetArrayLength(env, aac_buf);
//
//jbyte* j_aac_buf = (*env)->GetByteArrayElements(env, aac_buf, NULL);
//fa_aacenc_encode(AACencoderFD, j_buffer, chn_num*2*pcm_size, j_aac_buf, &aac_len);
//(*env)->ReleaseByteArrayElements(env, aac_buf, j_aac_buf, 0);
//
//result=aac_len;
////memset(j_buffer, 0, chn_num*2*pcm_size);
return result;
}










JNIEXPORT jint JNICALL Java_com_hsae_audiorecorder_SimpleLame_flush(
		JNIEnv *env, jclass cls, jbyteArray mp3buf) {
	const jsize mp3buf_size = (*env)->GetArrayLength(env, mp3buf);
	jbyte* j_mp3buf = (*env)->GetByteArrayElements(env, mp3buf, NULL);

	int result = lame_encode_flush(glf, j_mp3buf, mp3buf_size);

	(*env)->ReleaseByteArrayElements(env, mp3buf, j_mp3buf, 0);

	return result;
}

JNIEXPORT void JNICALL Java_com_hsae_audiorecorder_SimpleLame_close(
		JNIEnv *env, jclass cls) {
	lame_close(glf);
	glf = NULL;
}


