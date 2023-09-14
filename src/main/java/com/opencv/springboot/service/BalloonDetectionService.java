package com.opencv.springboot.service;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by frkn on 16/01/2017.
 */

@Service
public class BalloonDetectionService {


    public static final Scalar GREEN_LOWER = new Scalar(29, 86, 6);
    public static final Scalar GREEN_UPPER = new Scalar(64, 355, 255);

    public void detectGreenBlob(MultipartFile file) throws IOException {
        final Mat imageFromFile = Imgcodecs.imdecode(new MatOfByte(file.getBytes()), Imgcodecs.IMREAD_UNCHANGED);

        final Mat blurredHSVimg = convertToBlurredHSV(imageFromFile);
        var onlyGreenImg = greenMask(blurredHSVimg);

        var bufferedImgResult = toBufferedImage(onlyGreenImg);
        ImageIO.write(bufferedImgResult, "jpg", new File("C:\\detected\\detected.jpg"));
    }

    private static BufferedImage toBufferedImage(Mat onlyGreenImg) throws IOException {
        MatOfByte matOfByteFromOnlyGreenImg = new MatOfByte();
        Imgcodecs.imencode(".jpg", onlyGreenImg, matOfByteFromOnlyGreenImg);

        InputStream in = new ByteArrayInputStream(matOfByteFromOnlyGreenImg.toArray());

        return ImageIO.read(in);
    }

    private static Mat greenMask(final Mat blurredHSVimg) {
        final Mat maskedImg = new Mat();
        Core.inRange(blurredHSVimg, GREEN_LOWER, GREEN_UPPER, maskedImg);

        final Mat erodedImg = new Mat();
        Imgproc.erode(maskedImg, erodedImg, new Mat(), new Point(), 2);

        final Mat dilatedImg = new Mat();
        Imgproc.dilate(erodedImg, dilatedImg, new Mat(), new Point(), 2);

        return dilatedImg;
    }

    private Mat convertToBlurredHSV(Mat imageFromFile) {
        Mat blurredImg = new Mat();
        Imgproc.GaussianBlur(imageFromFile, blurredImg, new Size(11, 11), 0);

        Mat hsvImg = new Mat();
        Imgproc.cvtColor(imageFromFile, blurredImg, Imgproc.COLOR_BGR2HSV);

        return hsvImg;
    }

    private byte[] mat2Image(Mat frame) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".jpg", frame, buffer);
        return buffer.toArray();
    }
}
