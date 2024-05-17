package org.deus.src.services;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Optional;

@Service
public class AudioConverterService {
    private static final Logger logger = LoggerFactory.getLogger(AudioConverterService.class);

    public Optional<byte[]> convertToAAC(byte[] audioBytes, int bitRate) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(audioBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream);
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputStream, 1);

        try {
            grabber.start();
            recorder.setFormat("adts");
            recorder.setAudioBitrate(bitRate);
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setAudioChannels(grabber.getAudioChannels());
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
            recorder.start();

            while (true) {
                Frame frame = grabber.grab();
                if (frame == null) {
                    break;
                }
                if (frame.samples != null) {
                    recorder.record(frame);
                }
            }

            recorder.stop();
            grabber.stop();

            grabber.release();
            recorder.release();

            return Optional.of(outputStream.toByteArray());
        } catch (FFmpegFrameRecorder.Exception | FrameGrabber.Exception e) {
            logger.error("Error during audio conversion", e);
            return Optional.empty();
        }
    }
}
