package com.pt.bilibilidownloader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pt.pojo.MyImageView;
import com.pt.pojo.Video;
import com.pt.pojo.VideoPlayerUrl;
import com.pt.pojo.VideoView;
import com.pt.tool.BiliDownloader;
import com.pt.tool.BiliParser;
import com.pt.tool.PicSaver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    MainController mainController = new MainController();
    ExecutorService singlePool = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final TextView boardView = findViewById(R.id.board);
        final TextView videotitle = findViewById(R.id.videotitle);
        final EditText bvid = findViewById(R.id.bvid);
        final MyImageView videopic = findViewById(R.id.videopic);
        final Button findButton = findViewById(R.id.find);
        final Button downloadButton = findViewById(R.id.download);
        final Button savepicButton = findViewById(R.id.savepic);

        final CheckBox soundOnly = findViewById(R.id.soundonly);
        final Video video = new Video();
        final LinearLayout body = findViewById(R.id.body);

        body.setVisibility(View.GONE);

        Handler handler = new Handler(new Handler.Callback() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean handleMessage(Message message) {
                switch (message.arg1) {
                    case R.id.board:
                        boardView.setText((String) message.obj);
                        break;
                    case R.id.videotitle:
                        videotitle.setText((String) message.obj);
                        break;
                    case R.id.videopic:
                        videopic.setImageURL((String) message.obj);
                        break;
                    case R.id.body:
                        body.setVisibility(message.arg2);
                        break;
                }
                return false;
            }
        });

        findButton.setOnClickListener(
                view -> singlePool.submit(
                        () -> {
                            VideoView videoview = mainController.findVideo(bvid.getText().toString());
                            VideoPlayerUrl videoPlayerUrl = mainController.parseVideo(videoview);
                            Video videoParsed = new Video(videoview, videoPlayerUrl);
                            video.setVideoView(videoParsed.getVideoView());
                            video.setVideoPlayerUrl(videoParsed.getVideoPlayerUrl());

                            Message message = new Message();
                            message.arg1 = R.id.videotitle;
                            message.obj = video.getVideoView().getTitle();
                            handler.sendMessage(message);

                            message = new Message();
                            message.arg1 = R.id.videopic;
                            message.obj = video.getVideoView().getPic();
                            handler.sendMessage(message);

                            message = new Message();
                            message.arg1 = R.id.body;
                            message.arg2 = videoview == null ? View.GONE : View.VISIBLE;
                            handler.sendMessage(message);

                            //videopic.setImageURL(video.getVideoView().getPic());
                            //body.setVisibility(videoview==null ? View.GONE : View.VISIBLE);
                        }
                )
        );
        downloadButton.setOnClickListener(
                view -> singlePool.submit(
                        () -> {
                            Message message = new Message();
                            message.arg1 = R.id.board;
                            message.obj = "正在下载";
                            handler.sendMessage(message);

                            String res = mainController.downloadVideo(video, soundOnly.isChecked());
                            message = new Message();
                            message.arg1 = R.id.board;
                            message.obj = res;
                            handler.sendMessage(message);
                            return res;
                        }
                )
        );
        savepicButton.setOnClickListener(
                view -> singlePool.submit(
                        (Callable<String>) () -> {
                            Message message = new Message();
                            message.arg1 = R.id.board;
                            message.obj = "正在下载";
                            handler.sendMessage(message);

                            message = new Message();
                            message.arg1 = R.id.board;
                            message.obj = (mainController.savePic(video.getVideoView()));
                            handler.sendMessage(message);
                            return null;
                        }
                )
        );
    }
}

class MainController {
    BiliParser biliParser = new BiliParser();
    BiliDownloader biliDownloader = new BiliDownloader();

    public VideoView findVideo(final String bvid) {
        return biliParser.inspect(bvid);
    }

    public VideoPlayerUrl parseVideo(VideoView videoView) {
        return biliParser.parse(videoView);
    }

    public String savePic(VideoView videoview) {
        return PicSaver.save(videoview.getBvid(), videoview.getPic());
    }

    public String downloadVideo(Video video, Boolean soundOnly) {
        if (soundOnly.equals(false)) {
            return biliDownloader.download(video);
        } else {
            List<Video> videoList = new ArrayList<>();
            List<VideoPlayerUrl> videoPlayerUrlList = biliParser.parsePart(video.getVideoView(), true);
            for (VideoPlayerUrl videoPlayerUrl : videoPlayerUrlList) {
                videoList.add(new Video(video.getVideoView(), videoPlayerUrl));
            }
            return biliDownloader.download(videoList);
        }
    }
}


