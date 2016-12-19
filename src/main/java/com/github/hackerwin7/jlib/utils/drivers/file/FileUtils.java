package com.github.hackerwin7.jlib.utils.drivers.file;

import com.github.hackerwin7.jlib.utils.executors.TrainBdpConfig;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.OS;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.codehaus.plexus.logging.AbstractLogger;
import org.ho.yaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/28
 * Time: 9:51 AM
 * Desc: file utils set
 */
public class FileUtils {

    /* logger */
    private static final Logger LOG = Logger.getLogger(FileUtils.class);

    /* constants */
    public static final int MBYTES = 1024 * 1024;

    /**
     * load resource or classpath file name and convert into list
     * @param fileName resources of file name
     * @return list string
     * @throws Exception
     */
    public static List<String> file2List(String fileName) throws Exception {
        InputStream is = TrainBdpConfig.class.getClassLoader().getResourceAsStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        List<String> rows = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            if(!StringUtils.isBlank(line)) {
                rows.add(line);
            }
        }
        br.close();
        is.close();
        return rows;
    }

    /**
     * load the specified class loader
     * @param fileName
     * @param loader
     * @return list string
     * @throws Exception
     */
    public static List<String> file2List(String fileName, ClassLoader loader) throws Exception {
        InputStream is = loader.getResourceAsStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        List<String> rows = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            if(!StringUtils.isBlank(line)) {
                rows.add(line);
            }
        }
        br.close();
        is.close();
        return rows;
    }

    /**
     * load the specified class loader
     * @param fileName
     * @param stream
     * @return list string
     * @throws Exception
     */
    public static List<String> file2List(String fileName, InputStream stream) throws Exception {
        InputStream is = stream;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        List<String> rows = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            if(!StringUtils.isBlank(line)) {
                rows.add(line);
            }
        }
        br.close();
        is.close();
        return rows;
    }

    /**
     * read yaml file and return the value
     * @param fileName
     * @param key
     * @return value string
     * @throws Exception
     */
    public static String readYaml(String fileName, String key) throws Exception {
        HashMap map = Yaml.loadType(FileUtils.class.getClassLoader().getResource(fileName).openStream(), HashMap.class);
        return map.get(key).toString();
    }

    /**
     * delete recursively if file is directory
     * @param file
     * @throws IOException
     */
    public static void delete(File file) throws IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(file);
    }

    /**
     * un tar the tar gz archive package
     * @param src
     * @param destDir
     */
    public static void unTarGzPlexus(File src, File destDir) {
        unTarGzPlexus(src, destDir, null);
    }

    /**
     * un tar the tar.gz archive
     * @param src
     * @param destDir
     * @param dest
     */
    private static void unTarGzPlexus(File src, File destDir, File dest) {
        if(src == null)
            return;
        TarGZipUnArchiver unArchiver = new TarGZipUnArchiver();
        unArchiver.enableLogging(new AbstractLogger(org.codehaus.plexus.logging.Logger.LEVEL_INFO, "archiver") {
            @Override
            public void debug(String message, Throwable throwable) {
                if(isDebugEnabled()) {
                    if(throwable != null)
                        LOG.debug(message, throwable);
                    else
                        LOG.debug(message);
                }
            }

            @Override
            public void info(String message, Throwable throwable) {
                if(isInfoEnabled()) {
                    if(throwable != null)
                        LOG.info(message, throwable);
                    else
                        LOG.info(message);
                }
            }

            @Override
            public void warn(String message, Throwable throwable) {
                if(isWarnEnabled()) {
                    if(throwable != null)
                        LOG.warn(message, throwable);
                    else
                        LOG.warn(message);
                }
            }

            @Override
            public void error(String message, Throwable throwable) {
                if(isErrorEnabled()) {
                    if(throwable != null)
                        LOG.error(message, throwable);
                    else
                        LOG.error(message);
                }
            }

            @Override
            public void fatalError(String message, Throwable throwable) {
                if(isFatalErrorEnabled()) {
                    if(throwable != null)
                        LOG.fatal(message, throwable);
                    else
                        LOG.fatal(message);
                }
            }

            @Override
            public org.codehaus.plexus.logging.Logger getChildLogger(String name) {
                return null;
            }
        });
        unArchiver.setSourceFile(src);
        if(destDir != null)
            unArchiver.setDestDirectory(destDir);
        if (dest != null)
            unArchiver.setDestFile(dest);
        unArchiver.extract();
    }

    /**
     * un tar gz the archive file using apache-common-compress
     * inspired by: event-store-maven-plugin EventStoreDownloadMojo.java
     * @param archive
     * @param destDir
     */
    public static void unTarGz(File archive, File destDir) {
        try {
            /* pre-check */
            if(!destDir.exists())
                if(!destDir.mkdirs())
                    throw new IOException("mkdirs failed to make the dir for dest = " + destDir.getAbsolutePath());
            TarArchiveInputStream tarIn = new TarArchiveInputStream(
                    new GzipCompressorInputStream(
                            new BufferedInputStream(
                                    new FileInputStream(archive)
                            )
                    )
            );
            TarArchiveEntry entry;
            while ((entry = (TarArchiveEntry) tarIn.getNextEntry()) != null) {
                LOG.info("Extracting: " + entry.getName());
                File file = new File(destDir, entry.getName());
                if(entry.isDirectory()) {
                    if (!file.exists())
                        if (!file.mkdirs())
                            throw new IOException("mkdirs failed, when make the sub dir = " + file.getAbsolutePath());
                } else {
                    if(!file.getParentFile().exists())
                        file.getParentFile().mkdirs(); // create parent dirs
                    int count = 0;
                    byte[] read = new byte[MBYTES];
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file), MBYTES);
                    while ((count = tarIn.read(read, 0, MBYTES)) != -1) {
                        bos.write(read, 0, count);
                    }
                    bos.close();
                    entry.getMode();
                }
                applyFileMode(file, new FileMode(entry.getMode()));
            }
            tarIn.close();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * apply file mode, inspired by RapidEnv Unpacker.java
     * @param file
     * @param fileMode
     * @throws IOException
     */
    private static void applyFileMode(final File file, final FileMode fileMode) throws IOException {
        if (OS.isFamilyUnix() || OS.isFamilyMac()) {
            final String smode = fileMode.toChmodStringFull();
            final CommandLine cmdLine = new CommandLine("chmod");
            cmdLine.addArgument(smode);
            cmdLine.addArgument(file.getAbsolutePath());
            final Executor executor = new DefaultExecutor();
            try {
                final int result = executor.execute(cmdLine);
                if (result != 0) {
                    throw new IOException("Error # " + result
                            + " while trying to set mode \"" + smode
                            + "\" for file: " + file.getAbsolutePath());
                }
            } catch (final IOException ex) {
                throw new IOException (
                        "Error while trying to set mode \"" + smode
                                + "\" for file: " + file.getAbsolutePath(), ex);
            }
        } else {
            file.setReadable(fileMode.isUr() || fileMode.isGr()
                    || fileMode.isOr());
            file.setWritable(fileMode.isUw() || fileMode.isGw()
                    || fileMode.isOw());
            file.setExecutable(fileMode.isUx() || fileMode.isGx()
                    || fileMode.isOx());
        }
    }

    /**
     * unarchive the archive file into dest dir and change the base name into the specific {@code name}
     * @param archive
     * @param destDir
     * @param name
     */
    public static void unTarGz(File archive, File destDir, String name) {
        if(!archive.exists())
            return;
        if(!destDir.exists())
            destDir.mkdirs();
        File tmp = new File(destDir, archive.getName() + "_tmp_" + System.currentTimeMillis());
        if(tmp.exists())
            tmp.delete();
        else
            tmp.mkdirs();
        unTarGz(archive, tmp);
        File tmpDest = tmp.listFiles()[0];
        tmpDest.renameTo(new File(destDir, name)); // mv out of tmp dir
        if(tmp.exists())
            tmp.delete();
    }
}
