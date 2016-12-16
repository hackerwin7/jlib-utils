package com.github.hackerwin7.jlib.utils.sort;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.comparator.NameFileComparator;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/12/16
 * Time: 10:25 AM
 * Desc:
 * Tips:
 */
public class SortTest {
    public static void main(String[] args) {
        List<File> fileList = Arrays.asList(new File("/home/fff/Servers").listFiles());
        System.out.println(fileList);
        Collections.sort(fileList, NameFileComparator.NAME_COMPARATOR);
        System.out.println(fileList);
    }
}
