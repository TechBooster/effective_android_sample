package jp.muo.jellybeanmediaapisample;

import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class MainActivity extends Activity {
    static final int QUEUE_TIMEOUT_US = 5000;
    static final String TAG = "JBMediaSample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button playVorbisPlainMpBtn = (Button) findViewById(R.id.play_vorbis_plain_mp_btn);
        playVorbisPlainMpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVorbisPlainWithMediaPlayer();
            }
        });
        Button playVorbisPlainMcBtn = (Button) findViewById(R.id.play_vorbis_plain_mc_btn);
        playVorbisPlainMcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        playVorbisPlainWithMediaCodec();
                    }
                }).start();
            }
        });
    }

    private void playVorbisPlainWithMediaPlayer() {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.sin_3sec_plain);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                mp.release();
            }
        });
        mp.start();
    }

    private void playVorbisPlainWithMediaCodec() {
        final AssetFileDescriptor descriptor = getResources().openRawResourceFd(R.raw.sin_3sec_plain);
        final MediaExtractor extractor = new MediaExtractor();
        try {
            extractor.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            MediaFormat format = extractor.getTrackFormat(0);
            final int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            final int channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            final String mimeType = format.getString(MediaFormat.KEY_MIME);
            final long duration = format.getLong(MediaFormat.KEY_DURATION);
            Log.d(TAG, String.format("[MediaFormat] MIME: %s, sampling-rate: %d, channels: %d, duration: %d us",
                    mimeType,
                    sampleRate,
                    channels,
                    duration));

            final MediaCodec codec = MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME));
            codec.configure(format, null, null, 0);
            codec.start();

            final ByteBuffer[] inputBuffers = codec.getInputBuffers();
            ByteBuffer[] outputBuffers = codec.getOutputBuffers();
            extractor.selectTrack(0);
            boolean inBufFinished = false;
            boolean outBufFinished = false;
            final AudioTrack audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    (1 < channels ? AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_OUT_MONO),
                    AudioFormat.ENCODING_PCM_16BIT,
                    sampleRate * 2,
                    AudioTrack.MODE_STREAM
                    );
            audioTrack.play();
            final MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            for (; ; ) {
                if (!inBufFinished) {
                    final int inputBufferIdx = codec.dequeueInputBuffer(QUEUE_TIMEOUT_US);
                    if (0 <= inputBufferIdx) {
                        ByteBuffer currentInputBuffer = inputBuffers[inputBufferIdx];
                        int sampleSize = extractor.readSampleData(currentInputBuffer, 0);
                        if (sampleSize != -1) {
                            long presentationTimeUs = 0;
                            if (0 <= sampleSize) {
                                presentationTimeUs = extractor.getSampleTime();
                            } else {
                                inBufFinished = true;
                            }
                            byte[] decBuffer = new byte[sampleSize];
                            currentInputBuffer.get(decBuffer);
                            currentInputBuffer.clear();
                            // フレームのデコード処理(ダミー)
                            for (int i = 0; i < decBuffer.length; ++i) {
                                decBuffer[i] = decBuffer[i];
                            }
                            currentInputBuffer.put(decBuffer);
                            // バッファを逐次queueへ入れていく方式でなければ、currentInputBufferのpositionが
                            // 0以外となることもあるかもしれない。その場合にはここのpositionを調整するか、
                            // queueInputBufferの第2引数を変更する必要があると考えられる。
                            currentInputBuffer.position(0);

                            codec.queueInputBuffer(inputBufferIdx, 0, sampleSize, presentationTimeUs, inBufFinished ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);
                            if (!inBufFinished) {
                                extractor.advance();
                            }
                        }
                    }
                    else {
                        inBufFinished = true;
                    }
                }

                final int outputBufferIdx = codec.dequeueOutputBuffer(bufferInfo, QUEUE_TIMEOUT_US);
                if (0 <= outputBufferIdx) {
                    final byte[] outBuf = new byte[bufferInfo.size];
                    if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        outBufFinished = true;
                    }
                    final ByteBuffer intermediateBuffer = outputBuffers[outputBufferIdx];
                    intermediateBuffer.get(outBuf);
                    intermediateBuffer.clear();
                    // 出力先バッファの改変処理(ダミー)
                    for (int i = 0; i < outBuf.length; ++i) {
                        outBuf[i] = outBuf[i];
                    }
                    audioTrack.write(outBuf, 0, outBuf.length);
                    codec.releaseOutputBuffer(outputBufferIdx, false);
                }
                else if (outputBufferIdx == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    outputBuffers = codec.getOutputBuffers();
                }
                else if (outputBufferIdx == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    format = codec.getOutputFormat();
                    audioTrack.setPlaybackRate(format.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                    // チャンネル数が変わったりデータフォーマットが変わったりしたら、audioTrack自体作り直しが必要
                }
                else if (outputBufferIdx == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // ストリームの出力中に頻繁にこれが呼ばれるようなら、タイムアウトが短すぎるのかもしれないので長めにしてみると良いかも。
                    // 入力ストリームの読み込み完了後にここへ来た場合は、出力も完了したとみなす。
                    if (inBufFinished) {
                        outBufFinished = true;
                    }
                }

                if (inBufFinished && outBufFinished) {
                    break;
                }
            }
            audioTrack.stop();
            audioTrack.release();
            codec.stop();
            codec.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
