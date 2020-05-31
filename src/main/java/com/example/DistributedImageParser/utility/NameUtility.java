package com.example.DistributedImageParser.utility;

import org.springframework.stereotype.Component;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class NameUtility {


    public static String getFileNameWithOutExtension(File name) {
        return name.getName().split(Pattern.quote("."))[0];
    }

    public static String videoFileToImageDateFolder(File name) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").format(NameUtility.videoFileToDate(getFileNameWithOutExtension(name)));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String videoFileToImageTimeFolder(File name) {
        try {
            return new SimpleDateFormat("HH").format(NameUtility.videoFileToDate(getFileNameWithOutExtension(name)));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getImageFolder(File source, File target) {
        return target.getAbsolutePath() + File.separator + NameUtility.videoFileToImageDateFolder(source) + File.separator + NameUtility.videoFileToImageTimeFolder(source);
    }

    public String finderCreation(String source) {
        String[] paths = new File(source).getName().split("_");
        Optional<String> date = Arrays.stream(paths[1].split("-")).reduce(String::concat);
        return String.format("%06d%02d%04d", Long.valueOf(date.orElse("000000")), Long.valueOf(paths[2].split("-")[0]), Long.valueOf(new File(source).getParentFile().getName()));
    }


    public static Date videoFileToDate(String videoFile) throws ParseException {
        List<String> list = Arrays.asList(videoFile.split("_"));
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm").parse(list.get(1) + "_" + list.get(2));
    }

    public static File targetPerVideo(File target, File sourceFile) {
        return new File(target.getAbsolutePath() + File.separator + sourceFile.getParentFile().getName());
    }

}
