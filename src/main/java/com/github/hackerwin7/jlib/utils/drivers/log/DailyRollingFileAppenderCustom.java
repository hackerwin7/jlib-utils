package com.github.hackerwin7.jlib.utils.drivers.log;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import java.io.*;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/07/26
 * Time: 5:16 PM
 * Desc: DailyRollingFileAppenderCustom with maxBackupIndex
 * Tips:
 */
public class DailyRollingFileAppenderCustom extends FileAppender {

    /* custom the DailyRollingFileAppenderCustom */
    protected int maxBackupIndex = 7;

    public int getMaxBackupIndex() {
        return maxBackupIndex;
    }

    public void setMaxBackupIndex(int maxBackupIndex) {
        this.maxBackupIndex = maxBackupIndex;
    }

    class ModifiedTimeSortableFile extends File implements Serializable, Comparable<File> {

        private static final long serialVersionUID = 1373373728209668895L;

        public ModifiedTimeSortableFile(String parent, String child) {
            super(parent, child);
        }

        public ModifiedTimeSortableFile(URI uri) {
            super(uri);
        }

        public ModifiedTimeSortableFile(File parent, String child) {
            super(parent, child);
        }

        public ModifiedTimeSortableFile(String string) {
            super(string);
        }

        @Override
        public int compareTo(File pathname) {
            long thisVal = this.lastModified();
            long anotherVal = pathname.lastModified();
            return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
        }
    }

    private List<ModifiedTimeSortableFile> getAllFiles() {
        List<ModifiedTimeSortableFile> files = new ArrayList<>();
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String directoryName = dir.getPath();
                LogLog.debug("directory name: " + directoryName);
                File file = new File(fileName);
                String parentDirectory = file.getParent();
                if(parentDirectory != null) {
                    String localFile = fileName.substring(directoryName.length());
                    return name.startsWith(localFile);
                }
                return name.startsWith(fileName);
            }
        };
        File file = new File(fileName);
        String parentDirectory = file.getParent();
        if(file.exists()) {
            if(file.getParent() == null) {
                String absolutePath = file.getAbsolutePath();
                parentDirectory = absolutePath.substring(0, absolutePath.lastIndexOf(fileName));
            }
        }
        File dir = new File(parentDirectory);
        String[] names = dir.list(filter);

        for(String single : names) {
            files.add(new ModifiedTimeSortableFile(dir + System.getProperty("file.separator") + single));
        }
        return files;
    }

    /* end custom DailyRollingFileAppender */


    // The code assumes that the following constants are in a increasing
    // sequence.
    static final int TOP_OF_TROUBLE=-1;
    static final int TOP_OF_MINUTE = 0;
    static final int TOP_OF_HOUR   = 1;
    static final int HALF_DAY      = 2;
    static final int TOP_OF_DAY    = 3;
    static final int TOP_OF_WEEK   = 4;
    static final int TOP_OF_MONTH  = 5;


    /**
     The date pattern. By default, the pattern is set to
     "'.'yyyy-MM-dd" meaning daily rollover.
     */
    private String datePattern = "'.'yyyy-MM-dd";

    /**
     The log file will be renamed to the value of the
     scheduledFilename variable when the next interval is entered. For
     example, if the rollover period is one hour, the log file will be
     renamed to the value of "scheduledFilename" at the beginning of
     the next hour.

     The precise time when a rollover occurs depends on logging
     activity.
     */
    private String scheduledFilename;

    /**
     The next time we estimate a rollover should occur. */
    private long nextCheck = System.currentTimeMillis () - 1;

    Date now = new Date();

    SimpleDateFormat sdf;

    RollingCalendar rc = new RollingCalendar();

    int checkPeriod = TOP_OF_TROUBLE;

    // The gmtTimeZone is used only in computeCheckPeriod() method.
    static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");


    /**
     The default constructor does nothing. */
    public DailyRollingFileAppenderCustom() {
    }

    /**
     Instantiate a <code>DailyRollingFileAppenderCustom</code> and open the
     file designated by <code>filename</code>. The opened filename will
     become the ouput destination for this appender.

     */
    public DailyRollingFileAppenderCustom(Layout layout, String filename,
                                          String datePattern) throws IOException {
        super(layout, filename, true);
        this.datePattern = datePattern;
        activateOptions();
    }

    /**
     The <b>DatePattern</b> takes a string in the same format as
     expected by {@link SimpleDateFormat}. This options determines the
     rollover schedule.
     */
    public void setDatePattern(String pattern) {
        datePattern = pattern;
    }

    /** Returns the value of the <b>DatePattern</b> option. */
    public String getDatePattern() {
        return datePattern;
    }

    public void activateOptions() {
        super.activateOptions();
        if(datePattern != null && fileName != null) {
            now.setTime(System.currentTimeMillis());
            sdf = new SimpleDateFormat(datePattern);
            int type = computeCheckPeriod();
            printPeriodicity(type);
            rc.setType(type);
            File file = new File(fileName);
            scheduledFilename = fileName+sdf.format(new Date(file.lastModified()));

        } else {
            LogLog.error("Either File or DatePattern options are not set for appender ["
                    +name+"].");
        }
    }

    void printPeriodicity(int type) {
        switch(type) {
            case TOP_OF_MINUTE:
                LogLog.debug("Appender ["+name+"] to be rolled every minute.");
                break;
            case TOP_OF_HOUR:
                LogLog.debug("Appender ["+name
                        +"] to be rolled on top of every hour.");
                break;
            case HALF_DAY:
                LogLog.debug("Appender ["+name
                        +"] to be rolled at midday and midnight.");
                break;
            case TOP_OF_DAY:
                LogLog.debug("Appender ["+name
                        +"] to be rolled at midnight.");
                break;
            case TOP_OF_WEEK:
                LogLog.debug("Appender ["+name
                        +"] to be rolled at start of week.");
                break;
            case TOP_OF_MONTH:
                LogLog.debug("Appender ["+name
                        +"] to be rolled at start of every month.");
                break;
            default:
                LogLog.warn("Unknown periodicity for appender ["+name+"].");
        }
    }


    // This method computes the roll over period by looping over the
    // periods, starting with the shortest, and stopping when the r0 is
    // different from from r1, where r0 is the epoch formatted according
    // the datePattern (supplied by the user) and r1 is the
    // epoch+nextMillis(i) formatted according to datePattern. All date
    // formatting is done in GMT and not local format because the test
    // logic is based on comparisons relative to 1970-01-01 00:00:00
    // GMT (the epoch).

    int computeCheckPeriod() {
        RollingCalendar rollingCalendar = new RollingCalendar(gmtTimeZone, Locale.getDefault());
        // set sate to 1970-01-01 00:00:00 GMT
        Date epoch = new Date(0);
        if(datePattern != null) {
            for(int i = TOP_OF_MINUTE; i <= TOP_OF_MONTH; i++) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
                simpleDateFormat.setTimeZone(gmtTimeZone); // do all date formatting in GMT
                String r0 = simpleDateFormat.format(epoch);
                rollingCalendar.setType(i);
                Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
                String r1 =  simpleDateFormat.format(next);
                //System.out.println("Type = "+i+", r0 = "+r0+", r1 = "+r1);
                if(r0 != null && r1 != null && !r0.equals(r1)) {
                    return i;
                }
            }
        }
        return TOP_OF_TROUBLE; // Deliberately head for trouble...
    }

    /**
     Rollover the current file to a new file.
     */
    void rollOver() throws IOException {

        /* custom sort with max index rolling */
        List<ModifiedTimeSortableFile> files = getAllFiles();
        Collections.sort(files);
        if(files.size() >= maxBackupIndex) {
            int index = 0;
            int diff = files.size() - maxBackupIndex + 1;
            for(ModifiedTimeSortableFile file : files) {
                if(index >= diff)
                    break;
                file.delete();
                index++;
            }
        }
        /* custom sort with max index rolling */


    /* Compute filename, but only if datePattern is specified */
        if (datePattern == null) {
            errorHandler.error("Missing DatePattern option in rollOver().");
            return;
        }

        String datedFilename = fileName+sdf.format(now);
        // It is too early to roll over because we are still within the
        // bounds of the current interval. Rollover will occur once the
        // next interval is reached.
        if (scheduledFilename.equals(datedFilename)) {
            return;
        }

        // close current file, and rename it to datedFilename
        this.closeFile();

        File target  = new File(scheduledFilename);
        if (target.exists()) {
            target.delete();
        }

        File file = new File(fileName);
        boolean result = file.renameTo(target);
        if(result) {
            LogLog.debug(fileName +" -> "+ scheduledFilename);
        } else {
            LogLog.error("Failed to rename ["+fileName+"] to ["+scheduledFilename+"].");
        }

        try {
            // This will also close the file. This is OK since multiple
            // close operations are safe.
            this.setFile(fileName, true, this.bufferedIO, this.bufferSize);
        }
        catch(IOException e) {
            errorHandler.error("setFile("+fileName+", true) call failed.");
        }
        scheduledFilename = datedFilename;
    }

    /**
     * This method differentiates DailyRollingFileAppenderCustom from its
     * super class.
     *
     * <p>Before actually logging, this method will check whether it is
     * time to do a rollover. If it is, it will schedule the next
     * rollover time and then rollover.
     * */
    protected void subAppend(LoggingEvent event) {
        long n = System.currentTimeMillis();
        if (n >= nextCheck) {
            now.setTime(n);
            nextCheck = rc.getNextCheckMillis(now);
            try {
                rollOver();
            }
            catch(IOException ioe) {
                if (ioe instanceof InterruptedIOException) {
                    Thread.currentThread().interrupt();
                }
                LogLog.error("rollOver() failed.", ioe);
            }
        }
        super.subAppend(event);
    }
}

/**
 *  RollingCalendar is a helper class to DailyRollingFileAppenderCustom.
 *  Given a periodicity type and the current time, it computes the
 *  start of the next interval.
 * */
class RollingCalendar extends GregorianCalendar {
    private static final long serialVersionUID = -3560331770601814177L;

    int type = DailyRollingFileAppenderCustom.TOP_OF_TROUBLE;

    RollingCalendar() {
        super();
    }

    RollingCalendar(TimeZone tz, Locale locale) {
        super(tz, locale);
    }

    void setType(int type) {
        this.type = type;
    }

    public long getNextCheckMillis(Date now) {
        return getNextCheckDate(now).getTime();
    }

    public Date getNextCheckDate(Date now) {
        this.setTime(now);

        switch(type) {
            case DailyRollingFileAppenderCustom.TOP_OF_MINUTE:
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.MINUTE, 1);
                break;
            case DailyRollingFileAppenderCustom.TOP_OF_HOUR:
                this.set(Calendar.MINUTE, 0);
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.HOUR_OF_DAY, 1);
                break;
            case DailyRollingFileAppenderCustom.HALF_DAY:
                this.set(Calendar.MINUTE, 0);
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                int hour = get(Calendar.HOUR_OF_DAY);
                if(hour < 12) {
                    this.set(Calendar.HOUR_OF_DAY, 12);
                } else {
                    this.set(Calendar.HOUR_OF_DAY, 0);
                    this.add(Calendar.DAY_OF_MONTH, 1);
                }
                break;
            case DailyRollingFileAppenderCustom.TOP_OF_DAY:
                this.set(Calendar.HOUR_OF_DAY, 0);
                this.set(Calendar.MINUTE, 0);
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.DATE, 1);
                break;
            case DailyRollingFileAppenderCustom.TOP_OF_WEEK:
                this.set(Calendar.DAY_OF_WEEK, getFirstDayOfWeek());
                this.set(Calendar.HOUR_OF_DAY, 0);
                this.set(Calendar.MINUTE, 0);
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case DailyRollingFileAppenderCustom.TOP_OF_MONTH:
                this.set(Calendar.DATE, 1);
                this.set(Calendar.HOUR_OF_DAY, 0);
                this.set(Calendar.MINUTE, 0);
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.MONTH, 1);
                break;
            default:
                throw new IllegalStateException("Unknown periodicity type.");
        }
        return getTime();
    }
}
