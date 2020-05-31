package com.example.DistributedImageParser.service;

import com.example.DistributedImageParser.utility.NameUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

@Service
public class ParseService {

    @Value("${spring.application.imagefolder_path}")
    private String targetPath;
    private int channelCount = Runtime.getRuntime().availableProcessors();
    private File sourceEr;

    public synchronized Boolean channelEmitter(String source) {
        System.out.println(source);
        return Objects.nonNull(source) && !source.equals(String.valueOf(0)) ? channelEmitter(channelCount, source, new File(targetPath)) : Boolean.FALSE;
    }

    private Boolean channelEmitter(int channelCounts, String source, File target) {
        sourceEr = new File(source);
        return ((Function<File, File>) (sourcePerVideo -> parsingControl(sourcePerVideo, target, source))).andThen(this::rename).apply(new File(source));
    }

    private Duration duration(File file) {
        return Duration.ofSeconds(Long.parseLong(file.getName().substring(0, file.getName().lastIndexOf("."))));
    }

    private File parsingControl(File sourcePerVideo, File target, String source) {
        try {
            return Ffmpeg.imageParser(sourcePerVideo, NameUtility.targetPerVideo(target, sourcePerVideo)) ?
                    (new File(NameUtility.getImageFolder(new File(source), NameUtility.targetPerVideo(target, new File(source))))) : null;
        } catch (Exception e) {
            try {
                throw new Exception("Parsing Video File to images From " + source + " is failed");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    private Boolean renameTo(File file, File e) {
        return e.renameTo(new File(file + File.separator + LocalTime.MIDNIGHT.plus(duration(e)).format(DateTimeFormatter.ofPattern("mm-ss")) + ".jpg"));
    }

    private Boolean rename(File file) {
        if (file != null) {
            Arrays.asList(Objects.requireNonNull(file.listFiles())).parallelStream().forEach(e -> {
                if (!renameTo(file, e)) {
                    try {
                        throw new Exception("Renaming as seconds while proccessing file : " + e.getAbsolutePath());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } else try {
            throw new Exception("File at doest not exist " + sourceEr + " It might be terminated or corrupted");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}

