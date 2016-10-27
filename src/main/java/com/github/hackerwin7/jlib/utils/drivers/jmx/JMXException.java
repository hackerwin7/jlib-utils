package com.github.hackerwin7.jlib.utils.drivers.jmx;

import org.apache.commons.lang.exception.NestableRuntimeException;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/10/26
 * Time: 4:54 PM
 * Desc:
 * Tips:
 */
public class JMXException extends NestableRuntimeException {
    private static final long serialVersionUID = 712323423532453986L;

    public JMXException(String msg) {
        super(msg);
    }

    public JMXException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public JMXException(String errCode, String errDesc) {
        super(errCode + ":" + errDesc);
    }

    public JMXException(String errCode, String errDesc, Throwable cause) {
        super(errCode + ":" + errDesc, cause);
    }

    public JMXException(Throwable cause) {
        super(cause);
    }

    public Throwable fillInStackTrace() {
        return this;
    }
}
