package com.github.hackerwin7.jlib.utils.executors;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/11/02
 * Time: 4:23 PM
 * Desc:
 * Tips:
 */
public class SystemProperty {

    private static final Logger LOG = Logger.getLogger(SystemProperty.class);

    public static void main(String[] args) throws MalformedURLException, FileNotFoundException {


        /* java -cp conf/*:lib/* com.github.hackerwin7.jlib.utils.executors.SystemProperty */
        /* java -cp conf:lib/* com.github.hackerwin7.jlib.utils.executors.SystemProperty */

//        -cp <class search path of directories and zip/jar files>
//        -classpath <class search path of directories and zip/jar files>
//        A : separated list of directories, JAR archives,
//                and ZIP archives to search for class files.


//        System.out.println(System.getProperty("java.class.path"));
//        System.out.println(SystemProperty.class.getResource("").getPath());
//        System.out.println(SystemProperty.class.getClassLoader().getResource("").getPath());
//
//        InputStream stream = null;
//
//        stream = SystemProperty.class.getResourceAsStream("log4j.properties");
//        System.out.println(stream);
//
//        stream = SystemProperty.class.getResourceAsStream("/log4j.properties");
//        System.out.println(stream);
//
//        stream = SystemProperty.class.getClassLoader().getResourceAsStream("log4j.properties");
//        System.out.println(stream);
//
//        stream = SystemProperty.class.getClassLoader().getResourceAsStream("/log4j.properties");
//        System.out.println(stream);

        /* log4j configure from log4j.properties inner classpath, not -D property */
//        PropertyConfigurator.configure(SystemProperty.class.getClassLoader().getResource("log4j.properties")); // as resource

//        PropertyConfigurator.configure(SystemProperty.class.getClassLoader().getResourceAsStream("log4j.properties")); // as stream

//        PropertyConfigurator.configure(System.getProperty("config.log4j"));
//
//        PropertyConfigurator.configure(new File(System.getProperty("config.log4j")).toURI().toURL());
//
//        PropertyConfigurator.configure(new FileInputStream(System.getProperty("config.log4j")));

        File cur = new File(System.getProperty("user.dir"));

        while (true) {

            File par = cur.getParentFile();
            if(par == null)
                break;
            LOG.info(par.getPath());

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }

            cur = par;
        }
    }
}