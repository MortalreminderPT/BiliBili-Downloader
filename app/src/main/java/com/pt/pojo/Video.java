package com.pt.pojo;

public class Video {
	private VideoView videoView;
	private VideoPlayerUrl videoPlayerUrl;

	public Video(VideoView videoView, VideoPlayerUrl videoPlayerUrl)
	{
		this.videoView = videoView;
		this.videoPlayerUrl = videoPlayerUrl;
	}

    public Video() {};

	public void setVideoView(VideoView videoView)
	{
		this.videoView = videoView;
	}

	public VideoView getVideoView()
	{
		return videoView;
	}

	public void setVideoPlayerUrl(VideoPlayerUrl videoPlayerUrl)
	{
		this.videoPlayerUrl = videoPlayerUrl;
	}

	public VideoPlayerUrl getVideoPlayerUrl()
	{
		return videoPlayerUrl;
	}}
