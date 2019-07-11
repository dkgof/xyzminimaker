package dk.fambagge.xyzminimaker.capture;

public class Timelapse {
    private static final String CAPTURE_COMMAND = " ffmpeg -f video4linux2 -s 640x480 -i /dev/video1 -vf drawtext=\"fontfile=/usr/share/fonts/truetype/dejavu/DejaVuSansMono.ttf:text='%{localtime\\:%T}':x=20:y=20:fontcolor=white\" -r 1/10 -f image2 test%04d.png";
}
