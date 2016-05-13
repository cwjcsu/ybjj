/*$Id: $
 --------------------------------------
  Skybility
 ---------------------------------------
  Copyright By Skybility ,All right Reserved
 * author                     date    comment
 * chenweijun@skybility.com  2015年8月28日  Created
 */
package com.cwjcsu.common.log;

import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.util.FileSize;

import java.io.File;

/**
 * 整个生命周期会在首次调用日志框架的时候进行一次size检查，然后其他行为与SizeBasedTriggeringPolicy一致
 * 
 * @author atlas
 *
 */
public class TriggerOnFirstLogSizeBasedTriggeringPolicy<E> extends
		SizeBasedTriggeringPolicy<E> {

	private FileSize maxFileSize;

	private volatile boolean triggered = false;

	public TriggerOnFirstLogSizeBasedTriggeringPolicy() {
		setMaxFileSize(Long.toString(DEFAULT_MAX_FILE_SIZE));
	}

	public TriggerOnFirstLogSizeBasedTriggeringPolicy(final String maxFileSize) {
		setMaxFileSize(maxFileSize);
	}

	public boolean isTriggeringEvent(final File activeFile, final E event) {
		if (triggered) {
			return super.isTriggeringEvent(activeFile, event);
		} else {
			triggered = true;
			return (activeFile.length() >= maxFileSize.getSize());
		}
	}

	public void setMaxFileSize(String maxFileSize) {
		super.setMaxFileSize(maxFileSize);
		this.maxFileSize = FileSize.valueOf(maxFileSize);
	}

}
