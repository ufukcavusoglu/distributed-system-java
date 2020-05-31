package com.example.DistributedImageParser.service;

import com.example.DistributedImageParser.utility.NameUtility;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class Ffmpeg {

    public Ffmpeg() {
    }

    static synchronized Boolean imageParser(File source, File target) throws Exception {
        String imageFolder = NameUtility.getImageFolder(source, target);
        try {
            if (new File(imageFolder).mkdirs()) {
                return Runtime.getRuntime().exec("ffmpeg -loglevel quiet -i " + source.getAbsolutePath() + " -s 384x216 -vf fps=1 " + imageFolder + File.separator + "%0d.jpg").waitFor() == 0;
            } else return false;
        } catch (Exception e) {
            throw new Exception("File parsing is Failed");
        }
    }

}


